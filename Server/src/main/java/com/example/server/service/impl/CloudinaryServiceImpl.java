package com.example.server.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.server.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file, String folderName) {
        try {
            Map<String, Object> uploadParams = ObjectUtils.asMap(
                    "folder", "lessons/" + folderName,
                    "resource_type", "auto" // Auto detect filetype (video, image, document)
            );

            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
            return uploadResult.get("secure_url").toString(); // Return HTTPS URL
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to Cloudinary: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteFile(String publicId) {
        try {
            Map<?, ?> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            return "ok".equals(result.get("result"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file from Cloudinary: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Object> getFileInfo(String url) {
        try {
            // Extract public_id from URL
            String publicId = extractPublicIdFromUrl(url);
            return cloudinary.api().resource(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get file info from Cloudinary: " + e.getMessage(), e);
        }
    }

    private String extractPublicIdFromUrl(String url) {
        // URL format: https://res.cloudinary.com/cloudname/type/version/folder/public_id
        String[] parts = url.split("/");
        String publicIdWithExtension = parts[parts.length - 1];
        // Remove file extension
        return publicIdWithExtension.substring(0, publicIdWithExtension.lastIndexOf('.'));
    }
}
