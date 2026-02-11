package com.example.webapplication.controller;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

public abstract class BaseControllerIntegrationTest {

  @Autowired
  WebApplicationContext webApplicationContext;

  protected MockMvc mockMvc;

  @BeforeEach
  public void setup() {
    mockMvc = MockMvcBuilders
        .webAppContextSetup(webApplicationContext)
        .apply(springSecurity())
        .build();
  }

//  static Stream<Arguments> getStreamAllUsers() {
//    return Stream.of(
//            Arguments.of("user", "password"),
//            Arguments.of("scott", "tiger"),
//            Arguments.of("spring", "guru"));
//  }
//
//  static Stream<Arguments> getStreamCustomerAdmin() {
//    return Stream.of(
//            Arguments.of("scott", "tiger"),
//            Arguments.of("spring", "guru"));
//  }
//
//  static Stream<Arguments> getStreamNoAdmin() {
//    return Stream.of(
//            Arguments.of("user", "password"),
//            Arguments.of("scott", "tiger"));
//  }
}