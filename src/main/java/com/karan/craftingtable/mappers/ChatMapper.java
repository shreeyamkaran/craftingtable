package com.karan.craftingtable.mappers;

import com.karan.craftingtable.entities.ChatMessageEntity;
import com.karan.craftingtable.models.responses.ChatResponseDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChatMapper {

    List<ChatResponseDTO> fromListOfChatMessage(List<ChatMessageEntity> chatMessageList);

}
