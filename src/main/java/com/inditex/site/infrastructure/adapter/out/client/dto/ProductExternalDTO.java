package com.inditex.site.infrastructure.adapter.out.client.dto;

import lombok.Data;

@Data
public class ProductExternalDTO {

    private String id;
    private String name;
    private String description;
    private Double price;
    private Boolean availability;
}
