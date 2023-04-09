package linkedhu.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class PostCreateRequest {
    String message;
    String link;
    String place;
    String postType;
    long time;
    MultipartFile video;
}
