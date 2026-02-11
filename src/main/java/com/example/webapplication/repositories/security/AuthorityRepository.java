package com.example.webapplication.repositories.security;

import com.example.webapplication.entities.Authority;
import com.example.webapplication.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority,Long> {
    Optional<Authority> findByRole(String role);

}
