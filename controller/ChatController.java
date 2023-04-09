package linkedhu.controller;

import java.security.Principal;
import java.util.Date;
import java.util.List;

import linkedhu.model.Chat;
import linkedhu.model.User;
import linkedhu.request.CreateMessageRequest;
import linkedhu.request.UpdateMessageRequest;
import linkedhu.response.SimpleResponse;
import linkedhu.service.ChatService;
import linkedhu.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:3000")
public class ChatController {
    private final ChatService chatService;
    private final UserService userService;

    // Get methods for chat
    @GetMapping("/chat/latest")
    public ResponseEntity<Object> getLatestChats(Principal principal) {
        List<Chat> latestChats = chatService.getLatestMessages(principal.getName());
        return ResponseEntity.ok(new SimpleResponse("success", "Successfully retrieved", latestChats));
    }

    @GetMapping("/chat/between/{id}")
    public ResponseEntity<Object> getLatestChats(@PathVariable Long id, Principal principal) {
        User user = userService.getUserById(id);

        if (user == null)
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "Other user is not found", null));

        List<Chat> chatBetween = chatService.getChatBetween(principal.getName(), user.getUsername());
        return ResponseEntity.ok(new SimpleResponse("success", "Successfully retrieved", chatBetween));
    }

    // Post methods for chat
    @PostMapping("/message/create")
    public ResponseEntity<Object> createMessage(@RequestBody CreateMessageRequest request, Principal principal) {
        if (request.getMessage() == null || request.getReceiver() == null)
            return ResponseEntity.badRequest()
                    .body(new SimpleResponse("error", "Receiver or message is not found", null));

        User sender = userService.getUser(principal.getName());
        User receiver = userService.getUser(request.getReceiver());

        if (receiver == null)
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "Receiver is not found", null));

        if (receiver.getUsername().equals(sender.getUsername()))
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "You cannot message yourself", null));

        Chat chat = chatService
                .saveMessage(new Chat(null, sender, receiver, request.getMessage(), (new Date()).getTime()));
        return ResponseEntity.ok(new SimpleResponse("success", "Successfully created", chat));
    }

    // Update methods for chat
    @PutMapping("/message/update/{id}")
    public ResponseEntity<Object> updateMessage(@RequestBody UpdateMessageRequest request, @PathVariable Long id,
            Principal principal) {
        if (request.getMessage() == null)
            return ResponseEntity.badRequest()
                    .body(new SimpleResponse("error", "Message is not found in request body", null));

        Chat message = chatService.getMessage(id);

        if (message == null)
            return ResponseEntity.badRequest()
                    .body(new SimpleResponse("error", "Message is not found", null));

        if (!message.getSender().getUsername().equals(userService.getUser(principal.getName()).getUsername()))
            return ResponseEntity.badRequest()
                    .body(new SimpleResponse("error", "You are not authorized", null));

        boolean success = chatService.updateMessage(id, request.getMessage());

        if (!success)
            return ResponseEntity.badRequest()
                    .body(new SimpleResponse("error", "Could not update the message", null));

        return ResponseEntity.ok(new SimpleResponse("success", "Successfully updated", chatService.getMessage(id)));
    }

    // Delete methods for chat
    @DeleteMapping("/message/delete/{id}")
    public ResponseEntity<Object> deleteMessage(@PathVariable Long id, Principal principal) {
        Chat message = chatService.getMessage(id);

        if (message == null)
            return ResponseEntity.badRequest()
                    .body(new SimpleResponse("error", "Message is not found", null));

        if (!message.getSender().getUsername().equals(userService.getUser(principal.getName()).getUsername()))
            return ResponseEntity.badRequest()
                    .body(new SimpleResponse("error", "You are not authorized", null));

        boolean success = chatService.deleteMessage(id);

        if (!success)
            return ResponseEntity.badRequest()
                    .body(new SimpleResponse("error", "Could not delete the message", null));

        return ResponseEntity.ok(new SimpleResponse("success", "Successfully deleted", null));
    }
}
