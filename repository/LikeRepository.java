package linkedhu.repository;

import java.util.List;

import linkedhu.model.Like;
import linkedhu.model.Post;
import linkedhu.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
    List<Like> findByPost(Post post);

    List<Like> findByOwner(User owner);
}