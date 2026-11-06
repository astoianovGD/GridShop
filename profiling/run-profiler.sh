#!/usr/bin/env bash
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
ASPROF="/tmp/async-profiler-3.0-macos/bin/asprof"

# Download async-profiler if not already in /tmp
if [ ! -x "$ASPROF" ]; then
  echo "Downloading async-profiler 3.0 for macOS..."
  curl -kLs https://github.com/async-profiler/async-profiler/releases/download/v3.0/async-profiler-3.0-macos.zip -o /tmp/async-profiler.zip
  unzip -qo /tmp/async-profiler.zip -d /tmp/
fi

LOADGEN_CP="$PROJECT_ROOT/product-service/target/classes:$HOME/.m2/repository/org/slf4j/slf4j-api/2.0.16/slf4j-api-2.0.16.jar"

echo "1. Starting product-service on port 18082..."
java -Xms256m -Xmx256m -XX:+UseG1GC \
  -jar "$PROJECT_ROOT/product-service/target/product-service-1.0-SNAPSHOT.jar" \
  --server.port=18082 \
  --eureka.client.enabled=false \
  --spring.kafka.listener.auto-startup=false \
  > /tmp/product-service-prof.log 2>&1 &
APP_PID=$!

cleanup() {
  kill $APP_PID 2>/dev/null || true
}
trap cleanup EXIT

echo "2. Waiting for product-service (PID=$APP_PID) to become healthy..."
for i in {1..30}; do
  if curl -sf http://localhost:18082/actuator/health >/dev/null 2>&1; then
    echo "   product-service is UP (PID=$APP_PID)!"
    break
  fi
  sleep 1
done

# Pre-warm LRU cache with 20,000 items
curl -sf -X POST "http://localhost:18082/api/v1/products/cache/warmup?count=20000" >/dev/null || true

echo "3. Running CPU Profiling (-e cpu) under 300 RPS load from LoadGenerator..."
java -cp "$LOADGEN_CP" \
  com.bobocode.concurrency.loadgen.LoadGenerator \
  "http://localhost:18082/api/v1/products" 300 10 > /tmp/loadgen-cpu.log 2>&1 &
LOAD_PID=$!
sleep 1
"$ASPROF" -e cpu -d 8 -f "$SCRIPT_DIR/cpu-profile.html" "$APP_PID"
wait $LOAD_PID || true

echo "4. Running Memory Allocation Profiling (-e alloc) under 300 RPS load from LoadGenerator..."
java -cp "$LOADGEN_CP" \
  com.bobocode.concurrency.loadgen.LoadGenerator \
  "http://localhost:18082/api/v1/products" 300 10 > /tmp/loadgen-alloc.log 2>&1 &
LOAD_PID=$!
sleep 1
"$ASPROF" -e alloc -d 8 -f "$SCRIPT_DIR/alloc-profile.html" "$APP_PID"
wait $LOAD_PID || true

echo "Done! Flame graphs saved to:"
echo "  - $SCRIPT_DIR/cpu-profile.html"
echo "  - $SCRIPT_DIR/alloc-profile.html"
