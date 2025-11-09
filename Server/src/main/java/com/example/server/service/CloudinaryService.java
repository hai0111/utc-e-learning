package com.example.server.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface CloudinaryService {
    String uploadFile(MultipartFile file, String folderName);
    boolean deleteFile(String publicId);
    Map<String, Object> getFileInfo(String url);
}
