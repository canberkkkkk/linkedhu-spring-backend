package linkedhu.repository;

import java.util.List;

import linkedhu.model.Chat;
import linkedhu.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findBySenderAndReceiver(User sender, User receiver);

    List<Chat> findBySender(User sender);

    List<Chat> findByReceiver(User receiver);
}