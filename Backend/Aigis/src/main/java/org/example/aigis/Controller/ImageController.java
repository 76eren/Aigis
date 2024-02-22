package org.example.aigis.Controller;


import lombok.RequiredArgsConstructor;
import org.example.aigis.Dao.ImageDao;
import org.example.aigis.Model.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/image")
@RequiredArgsConstructor
public class ImageController {
    private final ImageDao imageDao;

    @GetMapping(value = "/{id}")
    public ApiResponse<byte[]> getImageById(@PathVariable("id") UUID id) {
        try {
            byte[] imageBytes = imageDao.getImageById(id);
            if (imageBytes == null) {
                return new ApiResponse<>(null, null);
            }
            return new ApiResponse<>(imageBytes, null);
        }
        catch (Exception e) {
            return new ApiResponse<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
