    package com.bobocode.Services.Products;

    import com.bobocode.Entities.Products.Product;
    import lombok.RequiredArgsConstructor;

    import java.util.Comparator;
    import java.util.List;

    @RequiredArgsConstructor
    public class SortProductsService {

        public List<Product> sortProductsByPriceAsc(List<Product> productList) {
            return productList.stream()
                    .sorted(Comparator.comparing(Product::getPrice))
                    .toList();
        }

        public List<Product> sortProductsByPriceDesc(List<Product> productList) {
            return productList.stream()
                    .sorted(Comparator.comparing(Product::getPrice).reversed())
                    .toList();
        }

        public List<Product> sortProductsByNameAsc(List<Product> productList) {
            return productList.stream()
                    .sorted(Comparator.comparing(Product::getName))
                    .toList();
        }

        public List<Product> sortProductsByNameDesc(List<Product> productList) {
            return productList.stream()
                    .sorted(Comparator.comparing(Product::getName).reversed())
                    .toList();
        }
    }
