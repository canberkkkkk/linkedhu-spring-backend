package linkedhu.repository;

import java.util.List;

import linkedhu.model.Post;
import linkedhu.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByOwner(User owner);
}