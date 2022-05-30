package com.marella.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public class FileUtils {
    public static void upload(MultipartFile file, String path, String filename) throws IOException {
        String realPath = path + "/" + filename;
        File dest = new File(realPath);
        if(!dest.getParentFile().exists()){
            dest.getParentFile().mkdir();
        }
        file.transferTo(dest);
    }
}