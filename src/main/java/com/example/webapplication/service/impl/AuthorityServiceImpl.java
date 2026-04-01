package com.example.webapplication.service.impl;

import com.example.webapplication.entities.Authority;
import com.example.webapplication.repositories.security.AuthorityRepository;
import com.example.webapplication.service.AuthorityService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AuthorityServiceImpl implements AuthorityService {

    private AuthorityRepository authorityRepository;

    @Override
    public Authority save(Authority authority) {
        return authorityRepository.save(authority);
    }

    @Override
    public long count() {
        return authorityRepository.count();
    }
}

