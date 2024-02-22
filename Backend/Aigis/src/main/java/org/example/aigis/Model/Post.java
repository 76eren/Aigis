package org.example.aigis.Model;


import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String content;

    @ManyToOne
    @JoinColumn(name = "post_user")
    private User user;

    private int likes = 0;

    private long date;

    @ManyToOne
    @JoinColumn(name = "post_image")
    private Image image;
}
