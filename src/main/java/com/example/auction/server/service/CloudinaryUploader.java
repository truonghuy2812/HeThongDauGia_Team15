package com.example.auction.server.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;

public class CloudinaryUploader {
    private static final Logger log = LoggerFactory.getLogger(CloudinaryUploader.class);

    private static final Cloudinary CLOUDINARY = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "drpv2tdr9"));

    public static String uploadImage(File file) {
        try {
            // upload anh len Cloudianry
            Map uploadResult = CLOUDINARY.uploader().unsignedUpload(
                    file,
                    "upload-image",
                    ObjectUtils.emptyMap()
            );
            // Trả về link bảo mật (https)
            return (String) uploadResult.get("url");
        } catch (Exception e) {
            log.error("LOI UPLOAD ANH" + e.getMessage());
            log.error("Exception", e);
            return null;
        }
    }
}