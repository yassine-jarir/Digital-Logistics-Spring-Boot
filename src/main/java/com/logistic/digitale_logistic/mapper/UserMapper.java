package com.logistic.digitale_logistic.mapper;

import com.logistic.digitale_logistic.dto.RegisterRequest;
import com.logistic.digitale_logistic.dto.UserDTO;
import com.logistic.digitale_logistic.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDto(User user);
    User toEntity(UserDTO dto);
    User fromRegisterRequest(RegisterRequest request);

}
