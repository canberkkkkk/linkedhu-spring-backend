package linkedhu.repository;

import java.util.List;

import linkedhu.model.Announcement;
import linkedhu.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByOwner(User owner);
}