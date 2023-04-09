package linkedhu.service;

import java.util.List;

import javax.transaction.Transactional;

import linkedhu.model.Like;
import linkedhu.model.Post;
import linkedhu.model.User;
import linkedhu.repository.LikeRepository;
import linkedhu.repository.PostRepository;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserService userService;
    private final PostService postService;
    private final PostRepository postRepository;

    // Create new like
    public Like saveLike(Like likeObj) {
        Like like = likeRepository.save(likeObj);
        Post post = like.getPost();
        like.getPost().getLikes().add(like);
        postRepository.save(post);
        return like;
    }

    // Get like by id
    public Like getLike(Long id) {
        return likeRepository.findById(id).orElse(null);
    }

    // Get like by username
    public List<Like> getLikesByUsername(String username) {
        User user = userService.getUser(username);

        if (user == null)
            return null;

        return likeRepository.findByOwner(user);
    }

    // Get like by post
    public List<Like> getLikesByPost(Long postId) {
        Post post = postService.getPost(postId);

        if (post == null)
            return null;

        return likeRepository.findByPost(post);
    }

    // Delete like
    public boolean deleteLike(Long id) {
        Like like = getLike(id);

        if (like == null)
            return false;

        Post post = like.getPost();
        post.getLikes().remove(like);
        postRepository.save(post);
        likeRepository.delete(like);
        return true;
    }
}
