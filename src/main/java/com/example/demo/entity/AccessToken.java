package com.example.demo.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "access_token")
@Builder
public class AccessToken {

    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String token;

    @Column(nullable = false,name = "user_name")
    private String userName;

    @Column(nullable = false,name = "created_at")
    private Instant createdAt = Instant.now();

    @Column(name = "expires_at")
    private Instant expiresAt;

    private boolean revoked = false;

}
