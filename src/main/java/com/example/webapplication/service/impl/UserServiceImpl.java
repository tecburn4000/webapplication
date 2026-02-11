package com.example.webapplication.service.impl;

import com.example.webapplication.dto.UserRegistrationDto;
import com.example.webapplication.dto.UserUpdateDto;
import com.example.webapplication.dto.mapper.UserMapper;
import com.example.webapplication.entities.Authority;
import com.example.webapplication.entities.User;
import com.example.webapplication.service.exception.UserAlreadyExistException;
import com.example.webapplication.repositories.security.AuthorityRepository;
import com.example.webapplication.repositories.security.UserRepository;
import com.example.webapplication.service.UserService;
import com.example.webapplication.service.exception.UserNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AllArgsConstructor
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserRegistrationDto registerNewUser(UserRegistrationDto userRegistrationDto) throws UserAlreadyExistException {
        if (emailExists(userRegistrationDto.getEmail())) {
            throw new UserAlreadyExistException("There is already an account with that email address: "
                    + userRegistrationDto.getEmail());
        }
        if (userNameExists(userRegistrationDto.getUsername())) {
            throw new UserAlreadyExistException("There is already an account with that username address: "
                    + userRegistrationDto.getUsername());
        }

        return save(userRegistrationDto);
    }

    @Override
    public UserRegistrationDto save(UserRegistrationDto userRegistrationDto) throws UserAlreadyExistException {

        // TODO: handle Role - change role
        Authority userAuthority =
                authorityRepository.findByRole("ROLE_NONE").orElseThrow(
                        () -> new RuntimeException("There is no authority with that role"));

        User saved = userRepository.save(userMapper.toUserEntity(userRegistrationDto));
        return userMapper.toUserRegistrationDTO(saved);
    }

    @Override
    public UserUpdateDto updateExistingUser(UserUpdateDto userUpdateDto) throws UserAlreadyExistException {

        User userByUsername = userRepository.findByUsername(userUpdateDto.getUsername()).orElseThrow(
                () -> new UserNotFoundException("There is no user with that username"));

        // update user data of existing user
        userMapper.updateUserFromUserUpdateDto(userUpdateDto, userByUsername);

        // TODO: handle Role - change role
        // roles will not be changed by the user itself!

        User saved = userRepository.save(userByUsername);
        return userMapper.toUserUpdateDTO(saved);
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteById(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public User save(User user) throws UserAlreadyExistException {
        return userRepository.save(user);
    }

    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User with given ID does not exist!"));
    }


    // TODO: kann ich die Methoden zusammenfassen?
    @Override
    public User findByUserName(String userName) {

        return userRepository.findByUsername(userName).orElseThrow(
                () -> new UserNotFoundException("User with given Name does not exist!"));
    }

    private boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    private boolean userNameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}