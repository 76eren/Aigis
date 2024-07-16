package org.example.aigis.Service;

import lombok.RequiredArgsConstructor;
import org.example.aigis.Dao.ImageDao;
import org.example.aigis.Dao.UserDAO;
import org.example.aigis.Model.ApiResponse;
import org.example.aigis.Model.Image;
import org.example.aigis.Model.SupportedExtensions;
import org.example.aigis.Model.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDAO userDAO;
    private final ImageDao imageDao;

    public Optional<User> assignProfilePicture(MultipartFile multipartFile, Optional<User> user) throws IOException {
        String imageExtention = multipartFile.getOriginalFilename().split("\\.")[1];

        for (SupportedExtensions i : SupportedExtensions.values()) {
            if (i.getExtension().substring(1).equalsIgnoreCase(imageExtention)) {
                Image image = imageDao.saveImage(multipartFile, i);
                user.get().setProfilePicture(image);
                return user;
            }
        }

        return Optional.empty();
    }
}
