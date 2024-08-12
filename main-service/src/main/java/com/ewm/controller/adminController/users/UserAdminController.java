package com.ewm.controller.adminController.users;

import com.ewm.service.user.UserService;
import dtostorage.main.user.NewUserRequest;
import dtostorage.main.user.UserDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Validated
public class UserAdminController {
    private final UserService userService;

    @GetMapping
    List<UserDto> getUsers(@RequestParam(value = "ids", required = false) List<Long> ids,
                           @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                           @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {

        return userService.getUsers(ids, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    UserDto createUser(@RequestBody @Valid NewUserRequest newUserRequest) {
        return userService.createUser(newUserRequest);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteUser(@PathVariable(name = "userId") Long userId) {
        userService.deleteUser(userId);
    }
}
