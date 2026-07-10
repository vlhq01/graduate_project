package com.example.techadvisor.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

    private static final String SPLIT_CHAR = ",";

    @Override
    public String convertToDatabaseColumn(List<String> stringList) {
        if (stringList == null || stringList.isEmpty()) {
            return null; // Lưu NULL xuống DB nếu list rỗng
        }
        return String.join(SPLIT_CHAR, stringList); // Biến thành "id1,id2"
    }

    @Override
    public List<String> convertToEntityAttribute(String string) {
        if (string == null || string.isEmpty()) {
            return Collections.emptyList(); // Trả về list rỗng nếu DB là NULL
        }
        return Arrays.asList(string.split(SPLIT_CHAR)); // Cắt "id1,id2" thành List
    }
}
