package com.example.techadvisor.mapper;

import com.example.techadvisor.dto.ProductDTO;
import com.example.techadvisor.entity.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDTO toDto(Product product);

    Product toEntity(ProductDTO productDTO);
}