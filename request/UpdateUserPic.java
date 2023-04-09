package linkedhu.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class UpdateUserPic {
    MultipartFile picture;
}
