package com.karan.craftingtable.mappers;

import com.karan.craftingtable.entities.UserEntity;
import com.karan.craftingtable.models.requests.SignUpRequestDTO;
import com.karan.craftingtable.models.responses.UserProfileResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity toUserEntity(SignUpRequestDTO signUpRequestDTO);

    UserProfileResponseDTO toUserProfileResponseDTO(UserEntity userEntity);

}
