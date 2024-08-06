package com.ewm.service.user;

import dtostorage.main.user.NewUserRequest;
import dtostorage.main.user.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(Long[] ids, Integer from, Integer size);

    UserDto createUser(NewUserRequest newUserRequest);

    void deleteUser(Long id);
}
