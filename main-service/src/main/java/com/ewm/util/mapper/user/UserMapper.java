package com.ewm.util.mapper.user;

import com.ewm.model.User;
import com.ewm.util.mapper.DateMapper;
import dtostorage.main.user.NewUserRequest;
import dtostorage.main.user.UserDto;
import dtostorage.main.user.UserShortDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {DateMapper.class})
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto toUserDto(User user);

    @Mapping(target = "id", ignore = true)
    User toUser(NewUserRequest newUserRequest);

    UserShortDto toUserShortDto(User user);
}
