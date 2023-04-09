package linkedhu.repository;

import java.util.List;

import linkedhu.model.User;
import linkedhu.model.Video;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Long> {
    List<Video> findByOwner(User owner);
}