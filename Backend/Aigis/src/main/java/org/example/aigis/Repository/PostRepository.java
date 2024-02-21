package org.example.aigis.Repository;

import org.example.aigis.Model.Post;
import org.example.aigis.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PostRepository  extends JpaRepository<Post, UUID> {
}
