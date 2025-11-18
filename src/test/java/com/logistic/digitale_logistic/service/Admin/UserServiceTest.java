package com.logistic.digitale_logistic.service.Admin;

import com.logistic.digitale_logistic.dto.RegisterRequest;
import com.logistic.digitale_logistic.dto.UserDTO;
import com.logistic.digitale_logistic.entity.User;
import com.logistic.digitale_logistic.enums.Role;
import com.logistic.digitale_logistic.mapper.UserMapper;
import com.logistic.digitale_logistic.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    // ------------------------------
    // ✔ TEST: CREATE USER
    // ------------------------------
    @Test
    void testCreateUser() {

        RegisterRequest req = new RegisterRequest();
        req.setName("Yassine");
        req.setEmail("yassine@example.com");
        req.setPassword("1234");
        req.setRole(Role.ADMIN);

        User userEntity = new User();
        userEntity.setId(1L);
        userEntity.setName("Yassine");
        userEntity.setEmail("yassine@example.com");

        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("Yassine");
        userDTO.setEmail("yassine@example.com");

        when(userMapper.fromRegisterRequest(req)).thenReturn(userEntity);
        when(userRepository.save(any(User.class))).thenReturn(userEntity);
        when(userMapper.toDto(userEntity)).thenReturn(userDTO);

        UserDTO result = userService.createUser(req);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Yassine", result.getName());

        verify(userRepository, times(1)).save(any(User.class));
    }

    // ------------------------------
    // ✔ TEST: GET ALL USERS
    // ------------------------------
    @Test
    void testGetAllUsers() {

        User u1 = new User();
        u1.setId(1L);
        User u2 = new User();
        u2.setId(2L);

        UserDTO d1 = new UserDTO();
        d1.setId(1L);
        UserDTO d2 = new UserDTO();
        d2.setId(2L);

        when(userRepository.findAll()).thenReturn(Arrays.asList(u1, u2));
        when(userMapper.toDto(u1)).thenReturn(d1);
        when(userMapper.toDto(u2)).thenReturn(d2);

        List<UserDTO> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    // ------------------------------
    // ✔ TEST: UPDATE USER
    // ------------------------------
    @Test
    void testUpdateUser() {

        User existing = new User();
        existing.setId(10L);
        existing.setName("Old");
        existing.setEmail("old@example.com");
        existing.setRole(Role.CLIENT);
        existing.setActive(true);

        UserDTO dto = new UserDTO();
        dto.setName("New");
        dto.setEmail("new@example.com");
        dto.setRole(Role.ADMIN);
        dto.setActive(false);

        when(userRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            // Verify the saved user has the updated values
            assertEquals("New", savedUser.getName());
            assertEquals("new@example.com", savedUser.getEmail());
            assertEquals(Role.ADMIN, savedUser.getRole());
            assertFalse(savedUser.getActive());
            return savedUser;
        });
        when(userMapper.toDto(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            UserDTO outputDTO = new UserDTO();
            outputDTO.setId(user.getId());
            outputDTO.setName(user.getName());
            outputDTO.setEmail(user.getEmail());
            outputDTO.setRole(user.getRole());
            outputDTO.setActive(user.getActive());
            return outputDTO;
        });

        UserDTO result = userService.updateUser(10L, dto);

        assertEquals("New", result.getName());
        assertEquals("new@example.com", result.getEmail());
        assertEquals(Role.ADMIN, result.getRole());
        assertFalse(result.getActive());
    }

    // -------------f-----------------
    // ✔ TEST: DEACTIVATE USER
    // ------------------------------
    @Test
    void testDeactivateUser() {

        User user = new User();
        user.setId(5L);
        user.setActive(true);

        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0)); // return modified user

        userService.deactivateUser(5L);

        assertFalse(user.getActive());
        verify(userRepository, times(1)).save(user);
    }
}
