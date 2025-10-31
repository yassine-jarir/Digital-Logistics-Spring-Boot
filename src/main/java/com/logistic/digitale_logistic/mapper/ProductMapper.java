package com.logistic.digitale_logistic.mapper;

import com.logistic.digitale_logistic.dto.ProductDTO;
import com.logistic.digitale_logistic.entity.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDTO toProductDTO(Product product);
    Product toProductEntity(ProductDTO dto);

}
