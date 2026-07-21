package com.bobocode.Services.Products;

import com.bobocode.Entities.Products.Bucket;
import com.bobocode.Entities.Products.Product;
import com.bobocode.Entities.Users.User;

import java.util.List;
import java.util.Scanner;

public class BucketService {

    public void addProductToBucket(Bucket bucket, Product product) {
        bucket.getProductsInBucket().add(product);
    }

    public void removeProductFromBucket(Bucket bucket, Product product) {
        bucket.getProductsInBucket().remove(product);
    }

    public List<Product> getProductsFromBucket(Bucket bucket) {
        return bucket.getProductsInBucket();
    }

}
