package linkedhu.repository;

import java.util.List;

import linkedhu.model.Meeting;
import linkedhu.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    List<Meeting> findByOwner(User owner);
}