package com.churchevents.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    void uploadFile(MultipartFile file);
    void saveFile(MultipartFile file);
}
