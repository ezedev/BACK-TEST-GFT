package com.inditex.site.infrastructure.adapter.in.rest.mapper;

import com.inditex.site.domain.model.Product;
import com.inditex.site.infrastructure.adapter.in.rest.dto.ProductDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductDtoMapper {

    @Mapping(source = "available", target = "availability")
    ProductDTO toDto(Product product);
}
