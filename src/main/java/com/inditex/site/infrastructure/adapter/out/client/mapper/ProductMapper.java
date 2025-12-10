package com.inditex.site.infrastructure.adapter.out.client.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import com.inditex.site.domain.model.Product;
import com.inditex.site.infrastructure.adapter.out.client.dto.ProductExternalDTO;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(source = "availability", target = "available")
    Product toDomain(ProductExternalDTO dto);
}
