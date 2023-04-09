package linkedhu.service;

import java.io.IOException;
import java.util.List;

import javax.transaction.Transactional;

import linkedhu.model.User;
import linkedhu.model.Video;
import linkedhu.repository.VideoRepository;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class VideoService {
    private final UserService userService;
    private final VideoRepository videoRepository;

    // Create new video
    public Video saveVideo(Video video) {
        return videoRepository.save(video);
    }

    // Get video by id
    public Video getVideo(Long id) {
        return videoRepository.findById(id).orElse(null);
    }

    // Get video by username
    public List<Video> getVideosByUsername(String username) {
        User user = userService.getUser(username);

        if (user == null)
            return null;

        return videoRepository.findByOwner(user);
    }

    // Delete video
    public boolean deleteVideo(Long id) {
        Video video = getVideo(id);

        if (video == null)
            return false;

        // to-do delete all comments and likes
        videoRepository.delete(video);
        return true;
    }

    // Update video
    public boolean updateVideo(Long id, String newMessage, MultipartFile video) throws IOException {
        Video existing = getVideo(id);

        if (existing == null)
            return false;

        existing.setMessage(newMessage);
        existing.setVideoName(video.getOriginalFilename());
        existing.setVideo(video.getBytes());
        videoRepository.save(existing);
        return true;
    }
}
