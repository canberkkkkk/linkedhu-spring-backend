package linkedhu.service;

import java.util.List;

import javax.transaction.Transactional;

import linkedhu.model.Comment;
import linkedhu.model.Post;
import linkedhu.model.User;
import linkedhu.repository.CommentRepository;
import linkedhu.repository.PostRepository;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final PostService postService;
    private final PostRepository postRepository;

    // Create new comment
    public Comment saveComment(Comment commentObj) {
        Comment comment = commentRepository.save(commentObj);
        Post post = comment.getPost();
        comment.getPost().getComments().add(comment);
        postRepository.save(post);
        return comment;
    }

    // Get comment by id
    public Comment getComment(Long id) {
        return commentRepository.findById(id).orElse(null);
    }

    // Get comment by username
    public List<Comment> getCommentsByUsername(String username) {
        User user = userService.getUser(username);

        if (user == null)
            return null;

        return commentRepository.findByOwner(user);
    }

    // Get comment by post
    public List<Comment> getCommentsByPost(Long postId) {
        Post post = postService.getPost(postId);

        if (post == null)
            return null;

        return commentRepository.findByPost(post);
    }

    // Delete comment
    public boolean deleteComment(Long id) {
        Comment comment = getComment(id);

        if (comment == null)
            return false;

        Post post = comment.getPost();
        post.getComments().remove(comment);
        postRepository.save(post);
        commentRepository.delete(comment);
        return true;
    }

    // Update comment
    public boolean updateComment(Long id, String newComment) {
        Comment comment = getComment(id);

        if (comment == null)
            return false;

        comment.setComment(newComment);
        commentRepository.save(comment);
        return true;
    }
}
