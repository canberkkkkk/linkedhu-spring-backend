package linkedhu.repository;

import java.util.List;

import linkedhu.model.Comment;
import linkedhu.model.Post;
import linkedhu.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);

    List<Comment> findByOwner(User owner);
}