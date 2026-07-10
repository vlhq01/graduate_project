package com.example.techadvisor.mapper;

import com.example.techadvisor.dto.ChatHistoryItemDTO;
import com.example.techadvisor.dto.ChatMessageDTO;
import com.example.techadvisor.entity.ChatMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatMessageMapper {

    @Mapping(target = "createdAt", expression = "java(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)")
    @Mapping(target = "suggestedProducts", ignore = true)
    ChatMessageDTO toDto(ChatMessage entity);

    @Mapping(target = "role", expression = "java(\"AI\".equals(entity.getSenderType()) ? \"assistant\" : \"user\")")
    @Mapping(target = "content", source = "content")
    ChatHistoryItemDTO toHistoryItem(ChatMessage entity);
}
