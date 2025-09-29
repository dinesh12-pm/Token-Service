package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "uploaded_file")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UploadedFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "s3_url")
    private String s3Url;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "uploaded_at")
    private Instant uploadedAt = Instant.now();

}
