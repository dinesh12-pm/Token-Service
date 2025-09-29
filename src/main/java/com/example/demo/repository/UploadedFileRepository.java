package com.example.demo.repository;

import com.example.demo.entity.UploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadedFileRepository  extends JpaRepository<UploadedFile, Long> {
}
