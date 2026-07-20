package com.bobocode.Entities.Products;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class Bucket {
    private List<Product> productsInBucket = new ArrayList<>();

}
