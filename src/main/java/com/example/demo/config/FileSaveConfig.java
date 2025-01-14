/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.config;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**

 * @author pc
 */
@Configuration
public class FileSaveConfig {
    @Bean
    public FileSaveConfig fileSave() {
        return new FileSaveConfig();
    }
    
    public String saveImage(MultipartFile imageFile) throws IOException {
    // Define the path where the image will be saved
    String folder = "src/main/resources/static/images/";
    Path path = Paths.get(folder + imageFile.getOriginalFilename());

    // Save the image to the path
    Files.createDirectories(path.getParent()); // Ensure directory exists
    Files.write(path, imageFile.getBytes());

    // Return the URL path of the image
    return "/images/" + imageFile.getOriginalFilename();
}
}
