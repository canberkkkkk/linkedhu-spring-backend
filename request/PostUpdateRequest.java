package linkedhu.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class PostUpdateRequest {
    String message;
    String link;
    String place;
    long time;
    MultipartFile video;
}