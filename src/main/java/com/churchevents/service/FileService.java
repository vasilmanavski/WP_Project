package com.churchevents.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    void uploadFile(MultipartFile file) throws IOException;
}
