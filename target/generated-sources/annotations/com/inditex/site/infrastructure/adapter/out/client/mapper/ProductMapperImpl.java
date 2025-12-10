package com.inditex.site.infrastructure.adapter.out.client.mapper;

import com.inditex.site.domain.model.Product;
import com.inditex.site.infrastructure.adapter.out.client.dto.ProductExternalDTO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-10T08:24:05+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Ubuntu)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public Product toDomain(ProductExternalDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Product.ProductBuilder product = Product.builder();

        product.available( dto.getAvailability() );
        product.id( dto.getId() );
        product.name( dto.getName() );
        product.description( dto.getDescription() );
        product.price( dto.getPrice() );

        return product.build();
    }
}
