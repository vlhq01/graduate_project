package com.example.techadvisor.mapper;

import com.example.techadvisor.dto.ProductDTO;
import com.example.techadvisor.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
// componentModel="spring" giúp bạn có thể @Autowired cái Mapper này
@Mapper(componentModel = "spring")
public interface ProductMapper {

    // Thư viện sẽ tự động viết thân hàm dựa trên sự giống nhau về tên biến!
    ProductDTO toDto(Product product);

    Product toEntity(ProductDTO productDTO);
}