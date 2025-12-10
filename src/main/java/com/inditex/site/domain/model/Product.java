package com.inditex.site.domain.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Product {

    String id;
    String name;
    String description;
    Double price;
    Boolean available;
}
