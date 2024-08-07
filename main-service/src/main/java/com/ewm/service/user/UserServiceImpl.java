package com.ewm.service.user;

import com.ewm.exception.NotExistsExeption;
import com.ewm.model.User;
import com.ewm.repository.UserRepository;
import com.ewm.util.mapper.user.UserMapper;
import dtostorage.main.user.NewUserRequest;
import dtostorage.main.user.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper = UserMapper.INSTANCE;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        log.debug("Запрос на получение пользователей - {}, с размерностью от {} до {}", ids, from, size);
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        log.info("Запрос на получение пользователей");

        if (ids == null || ids.isEmpty()) {
            log.info("Получение всех пользователей");

            return userRepository.findAll(pageable).toList().stream()
                .map(userMapper::toUserDto).collect(Collectors.toList());
        }

        return userRepository.findAllByIds(ids, pageable).toList().stream()
            .map(userMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(NewUserRequest newUserRequest) {
        log.debug("Запрос на создание пользователя - {}", newUserRequest);
        log.info("Запрос на создание пользователя");
        User user = userMapper.toUser(newUserRequest);

        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) throws NotExistsExeption {
        log.debug("Запрос на удаление пользоваителя с id - {}", id);
        log.info("Запрос на удаление пользователя");

        if (!userRepository.existsById(id)) {
            throw new NotExistsExeption(id.toString());
        }

        userRepository.deleteById(id);
    }
}
