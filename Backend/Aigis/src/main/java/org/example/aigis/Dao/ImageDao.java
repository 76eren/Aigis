package org.example.aigis.Dao;


import lombok.RequiredArgsConstructor;
import org.example.aigis.Model.Image;
import org.example.aigis.Model.SupportedExtensions;
import org.example.aigis.Repository.ImageRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ImageDao {
    private final ImageRepository imageRepository;

    public byte[] getImageById(UUID id) throws IOException {
        Image image = imageRepository.findById(id).orElse(null);
        if (image == null) {
            return null;
        }
        Path imagePath = Paths.get("images/" + id, image.getImageFileName());
        return Files.readAllBytes(imagePath);
    }

    public Image saveImage(MultipartFile multipartFile, SupportedExtensions extention) throws IOException {
        Image image = new Image();
        image.setId(UUID.randomUUID());
        image.setImageExtention(extention);
        image.setImageFileName("Image" + "." + extention);

        String directoryPath = "images/" + image.getId();
        Path directory = Paths.get(directoryPath);
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }

        Path filePath = directory.resolve(image.getImageFileName());
        Files.copy(multipartFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return imageRepository.save(image);

    }
}

