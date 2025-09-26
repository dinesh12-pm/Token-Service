package com.example.demo.repository;

import com.example.demo.entity.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCredentialsRepository extends JpaRepository<UserCredentials,Long> {
    Optional<UserCredentials> findByUserName(String username);
}
