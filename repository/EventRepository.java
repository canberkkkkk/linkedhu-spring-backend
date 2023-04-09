package linkedhu.repository;

import java.util.List;

import linkedhu.model.Event;
import linkedhu.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByOwner(User owner);
}