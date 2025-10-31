package com.logistic.digitale_logistic.service.Admin;

import com.logistic.digitale_logistic.dto.RegisterRequest;
import com.logistic.digitale_logistic.dto.UserDTO;
import com.logistic.digitale_logistic.entity.User;
import com.logistic.digitale_logistic.mapper.UserMapper;
import com.logistic.digitale_logistic.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserDTO createUser(RegisterRequest request) {
        User user = userMapper.fromRegisterRequest(request);
        return userMapper.toDto(userRepository.save(user));
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public UserDTO updateUser(Long id, UserDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        user.setActive(dto.getActive());

        return userMapper.toDto(userRepository.save(user));
    }

    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(false);
        userRepository.save(user);
    }
}
