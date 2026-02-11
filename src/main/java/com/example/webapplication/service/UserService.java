package com.example.webapplication.service;

import com.example.webapplication.dto.UserRegistrationDto;
import com.example.webapplication.dto.UserUpdateDto;
import com.example.webapplication.entities.User;
import com.example.webapplication.service.exception.UserAlreadyExistException;

import java.util.List;

public interface UserService {

    UserRegistrationDto registerNewUser(UserRegistrationDto userRegistrationDto) throws UserAlreadyExistException;
    UserRegistrationDto save(UserRegistrationDto userRegistrationDto) throws UserAlreadyExistException;
    UserUpdateDto updateExistingUser(UserUpdateDto userUpdateDto) throws UserAlreadyExistException;


    // only for internal use
    User findByUserName(String userName);
    List<User> findAllUsers();
    User findById(Long userId);
    void deleteById(Long userId);
    User save(User user) throws UserAlreadyExistException;
}