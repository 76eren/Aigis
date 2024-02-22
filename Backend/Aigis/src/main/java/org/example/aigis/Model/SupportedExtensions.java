package org.example.aigis.Model;

import lombok.Getter;

@Getter
public enum SupportedExtensions {
    JPG(".jpg"),
    PNG(".png"),
    JPEG(".jpeg"),
    GIF(".gif");

    private final String extension;
    SupportedExtensions(String extension) {
        this.extension = extension;
    }
}