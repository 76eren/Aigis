package org.example.aigis.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.aigis.DTO.POST.PostCreateDTO;
import org.example.aigis.DTO.POST.PostGetDTO;
import org.example.aigis.Dao.ImageDao;
import org.example.aigis.Mapper.PostMapper;
import org.example.aigis.Model.Image;
import org.example.aigis.Model.Post;
import org.example.aigis.Model.SupportedExtensions;
import org.example.aigis.Model.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final ImageDao imageDao;
    private final PostMapper postMapper;

    public PostData createPost(MultipartFile multipartFile, String postCreateDTOstr, String usernameUnique) throws IOException {
        PostCreateDTO postCreateDTO = new ObjectMapper().readValue(postCreateDTOstr, PostCreateDTO.class);

        Image image = null;
        if (multipartFile != null) {
            String extention = multipartFile.getOriginalFilename().split("\\.")[1];
            for (SupportedExtensions i : SupportedExtensions.values()) {
                if (i.getExtension().substring(1).equalsIgnoreCase(extention)) {
                    image = imageDao.saveImage(multipartFile, i);
                    return new PostData(image, postCreateDTO);
                }
            }
        }
        return null; // Dangerous
    }

    public List<PostGetDTO> getPostsFromUser(Optional<User> user) {

        List<Post> posts = user.get().getPosts();
        List<PostGetDTO> postsGet = new ArrayList<>();
        for (Post post : posts) {
            postsGet.add(postMapper.fromEntityToPostGetDto(post));
        }

        // Sort the post by date going up
        // TODO: Sort this inside of the database instead of runtime
        postsGet.sort(Comparator.comparing(PostGetDTO::getDate));
        return postsGet;
    }




    public class PostData {
        private Image image;
        PostCreateDTO postCreateDTO;

        public PostData(Image image, PostCreateDTO postCreateDTO) {
            this.image = image;
            this.postCreateDTO = postCreateDTO;
        }

        public Image getImage() {
            return image;
        }

        public void setImage(Image image) {
            this.image = image;
        }

        public PostCreateDTO getPostCreateDTO() {
            return postCreateDTO;
        }

        public void setPostCreateDTO(PostCreateDTO postCreateDTO) {
            this.postCreateDTO = postCreateDTO;
        }
    }
}


