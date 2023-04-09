package linkedhu.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import linkedhu.model.Chat;
import linkedhu.model.User;
import linkedhu.repository.ChatRepository;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final UserService userService;

    // Get chat between
    public List<Chat> getChatBetween(String sender, String receiver) {
        User senderUser = userService.getUser(sender);
        User receiverUser = userService.getUser(receiver);

        List<Chat> sentMessages = chatRepository.findBySenderAndReceiver(senderUser, receiverUser);
        List<Chat> receivedMessages = chatRepository.findBySenderAndReceiver(receiverUser, senderUser);

        return getChatBetweenHelper(sentMessages, receivedMessages);
    }

    // Get latest chats - do later
    public List<Chat> getLatestMessages(String username) {
        User user = userService.getUser(username);

        List<Chat> sentMessages = chatRepository.findBySender(user);
        List<Chat> receivedMessages = chatRepository.findByReceiver(user);
        List<Chat> finalMessages = getChatBetweenHelper(sentMessages, receivedMessages);

        // Inefficient... but no time
        Map<Long, Chat> latestMessages = new HashMap<>();
        finalMessages.stream().forEach(message -> {
            Long receiverId = message.getReceiver().getId();
            Long senderId = message.getSender().getId();
            Long otherId = receiverId == user.getId() ? senderId : receiverId;

            if (!latestMessages.containsKey(otherId))
                latestMessages.put(otherId, message);

            if (latestMessages.containsKey(otherId)
                    && latestMessages.get(otherId).getTimestamp() < message.getTimestamp())
                latestMessages.put(otherId, message);
        });

        List<Chat> retMessages = new ArrayList<>();
        latestMessages.values().stream().forEach(message -> {
            retMessages.add(message);
        });

        Collections.sort(retMessages,
                (o1, o2) -> -1 * (int) (((Chat) o1).getTimestamp() - ((Chat) o2).getTimestamp()));

        return retMessages;
    }

    // Get chat message
    public Chat getMessage(Long id) {
        return chatRepository.findById(id).orElse(null);
    }

    // Send chat message
    public Chat saveMessage(Chat chat) {
        return chatRepository.save(chat);
    }

    // Update chat message
    public boolean updateMessage(Long id, String newMessage) {
        Chat existing = getMessage(id);

        if (existing == null)
            return false;

        existing.setMessage(newMessage);
        chatRepository.save(existing);
        return true;
    }

    // Delete chat message
    public boolean deleteMessage(Long id) {
        Chat chat = getMessage(id);

        if (chat == null)
            return false;

        chatRepository.delete(chat);
        return true;
    }

    // Helpers
    public List<Chat> getChatBetweenHelper(List<Chat> sent, List<Chat> received) {
        List<Chat> finalMessages = new ArrayList<>();

        if (sent != null)
            sent.stream().forEach(message -> {
                finalMessages.add(message);
            });

        if (received != null)
            received.stream().forEach(message -> {
                finalMessages.add(message);
            });

        Collections.sort(finalMessages,
                (o1, o2) -> -1 * (int) (((Chat) o1).getTimestamp() - ((Chat) o2).getTimestamp()));

        return finalMessages;
    }

}
