package com.example.webapplication.service;

import com.example.webapplication.entities.Authority;

public interface AuthorityService {
    Authority save(Authority authority);
    long count();
}

