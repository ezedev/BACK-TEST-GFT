package com.inditex.site.infrastructure.adapter.in.rest.mapper;

import com.inditex.site.domain.model.Product;
import com.inditex.site.infrastructure.adapter.in.rest.dto.ProductDTO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-10T08:24:05+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Ubuntu)"
)
@Component
public class ProductDtoMapperImpl implements ProductDtoMapper {

    @Override
    public ProductDTO toDto(Product product) {
        if ( product == null ) {
            return null;
        }

        ProductDTO productDTO = new ProductDTO();

        productDTO.setAvailability( product.getAvailable() );
        productDTO.setId( product.getId() );
        productDTO.setName( product.getName() );
        if ( product.getPrice() != null ) {
            productDTO.setPrice( product.getPrice().floatValue() );
        }
        productDTO.setDescription( product.getDescription() );

        return productDTO;
    }
}
