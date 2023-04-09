package repository;

import model.Complaint;
import model.Post;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    Complaint findByPost(Post post);
}