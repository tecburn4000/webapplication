package com.example.webapplication.service.impl;

import com.example.webapplication.dto.mapper.UserMapper;
import com.example.webapplication.dto.UserRegistrationDto;
import com.example.webapplication.entities.Authority;
import com.example.webapplication.entities.User;
import com.example.webapplication.service.exception.UserAlreadyExistException;
import com.example.webapplication.repositories.security.AuthorityRepository;
import com.example.webapplication.repositories.security.UserRepository;
import com.example.webapplication.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthorityRepository authorityRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private User user;

    @Mock
    private Authority authority;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRegistrationDto userRegistrationDto;

    private UserService userService;


    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, authorityRepository, passwordEncoder, userMapper);
    }


    @Test
    void registerNewUser_EmailExists() {

        when(userRegistrationDto.getEmail()).thenReturn("email");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        assertThrows(
                UserAlreadyExistException.class,
                () -> userService.registerNewUser(userRegistrationDto));
    }

    @Test
    void registerNewUser_UsernameExists() {

        when(userRegistrationDto.getUsername()).thenReturn("username");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        assertThrows(
                UserAlreadyExistException.class,
                () -> userService.registerNewUser(userRegistrationDto));
    }

    @Test
    void registerNewUser_AuthorityIsNotUser() {

        when(userRegistrationDto.getEmail()).thenReturn("email");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRegistrationDto.getUsername()).thenReturn("username");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        // check authority
        when(authorityRepository.findByRole(anyString())).thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> userService.registerNewUser(userRegistrationDto));
    }

    @Test
    void registerNewUserTest() {

        // email ok
        when(userRegistrationDto.getEmail()).thenReturn("email");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        // username ok
        when(userRegistrationDto.getUsername()).thenReturn("username");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        // authority ok
        when(authorityRepository.findByRole(anyString())).thenReturn(Optional.of(authority));
        // save returns a mock user
        when(userRepository.save(any(User.class))).thenReturn(user);
        // user mapper returns mock user
        when(userMapper.toUserEntity(any(UserRegistrationDto.class))).thenReturn(user);
        // Test
        userService.registerNewUser(userRegistrationDto);

        // verification
        verify(userRepository, Mockito.times(1)).save(any(User.class));
        verify(userRepository, Mockito.times(1)).findByEmail(anyString());
        verify(userRepository, Mockito.times(1)).findByUsername(anyString());
    }

}