package com.example.demo.controller;


import com.example.demo.Response.FileUploadResponse;
import com.example.demo.entity.UserCredentials;
import com.example.demo.repository.UserCredentialsRepository;
import com.example.demo.request.AuthRequest;
import com.example.demo.service.AccessTokenService;
import com.example.demo.service.PdfService;
import com.example.demo.service.S3Service;
import com.example.demo.util.JWTUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.User;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;


@RestController
public class AuthController {

    private final JWTUtil jwtUtil;
    private final UserCredentialsRepository userCredentialsRepository;
    private final AuthenticationManager authenticationManager;
    private final AccessTokenService accessTokenService;
    private final PdfService pdfService;
    private final S3Service s3Service;

    public AuthController(JWTUtil jwtUtil, UserCredentialsRepository userCredentialsRepository, AuthenticationManager authenticationManager, AccessTokenService accessTokenService, PdfService pdfService, S3Service s3Service) {
        this.jwtUtil = jwtUtil;
        this.userCredentialsRepository = userCredentialsRepository;
        this.authenticationManager = authenticationManager;
        this.accessTokenService = accessTokenService;
        this.pdfService = pdfService;
        this.s3Service = s3Service;
    }


    @GetMapping("/")
    public ResponseEntity<String> getHomepage(){

        return ResponseEntity.ok("Welcome to home page");
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody AuthRequest request){
        UserCredentials credentials = new UserCredentials();
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(request.getPassword());
        credentials.setUserName(request.getUserName());
        credentials.setPassword(encodedPassword);
        credentials.setRole("ROLE_USER");
        userCredentialsRepository.save(credentials);
        return ResponseEntity.ok("User Created successfully!!");
    }

    /**
     * to get jwt token by passing the valid credentials
     * @param authRequest
     * @return String(JWT Token)
     */
    @PostMapping("/authenticate")
    public String generateToken(@RequestBody AuthRequest authRequest){
        System.out.println("Generating token!!!!");
   try{
     Authentication authentication= authenticationManager.authenticate(
               new UsernamePasswordAuthenticationToken(authRequest.getUserName(),authRequest.getPassword()));
     return jwtUtil.generateToken(authRequest.getUserName());
   }
   catch (Exception ex){
       throw ex;
   }
    }

    @PostMapping("/getToken")
    public String getOpaqueToken(@RequestBody AuthRequest authRequest) {
        System.out.println("Inside getOpaqueToken method.......");

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUserName(), authRequest.getPassword()));
            return accessTokenService.generateAccessToken(authRequest.getUserName());
        } catch (Exception e) {
            throw e;
        }
    }

    @GetMapping("/employees/pdf")
    public void generatePdf(HttpServletResponse response) throws IOException {
        // 1. Set response headers
        response.setContentType("application/pdf");

        // 2. Force download with a proper file name
        response.setHeader("Content-Disposition", "attachment; filename=employees.pdf");

        // 2. Delegate PDF writing to service
        pdfService.generateEmployeePdf(response.getOutputStream());

    }

    public ResponseEntity<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile file,
                                             @RequestParam("keyName") String keyName){

        try{
            FileUploadResponse response = s3Service.uploadToS3(keyName,file);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            throw new RuntimeException("Error while uploading file to the bucket!!");
        }
    }
}
