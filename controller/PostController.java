package linkedhu.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import linkedhu.model.Announcement;
import linkedhu.model.Comment;
import linkedhu.model.Event;
import linkedhu.model.Like;
import linkedhu.model.Meeting;
import linkedhu.model.Post;
import linkedhu.model.User;
import linkedhu.model.Video;
import linkedhu.request.CommentRequest;
import linkedhu.request.PostCreateRequest;
import linkedhu.request.PostUpdateRequest;
import linkedhu.response.SimpleResponse;
import linkedhu.service.AnnouncementService;
import linkedhu.service.CommentService;
import linkedhu.service.EventService;
import linkedhu.service.LikeService;
import linkedhu.service.MeetingService;
import linkedhu.service.PostService;
import linkedhu.service.UserService;
import linkedhu.service.VideoService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:3000")
public class PostController {
    private final UserService userService;
    private final PostService postService;
    private final AnnouncementService announcementService;
    private final EventService eventService;
    private final MeetingService meetingService;
    private final VideoService videoService;
    private final LikeService likeService;
    private final CommentService commentService;

    // Get methods for posts
    @GetMapping("/post/{id}")
    public ResponseEntity<Object> getPost(@PathVariable Long id) {
        Post post = postService.getPost(id);

        if (post == null)
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "Post is not found", null));

        return ResponseEntity.ok(new SimpleResponse("success", "Successfully retrieved", getPostHelper(post)));
    }

    @GetMapping("/post/user/{id}")
    public ResponseEntity<Object> getUserPosts(@PathVariable Long id) {
        List<Object> userPosts = postService.getPostsByUsername(userService.getUserById(id).getUsername());
        return ResponseEntity.ok(new SimpleResponse("success", "Successfully retrieved", userPosts));
    }

    @GetMapping("/post/latest/{page}")
    public ResponseEntity<Object> getLatestPosts(@PathVariable int page) {
        List<Object> latestPosts = postService.getLatestPosts(page);
        return ResponseEntity.ok(new SimpleResponse("success", "Successfully retrieved", latestPosts));
    }

    // Post methods for posts
    @PostMapping("/post")
    public ResponseEntity<Object> createPost(@RequestBody PostCreateRequest request, Principal principal)
            throws IOException {
        User user = userService.getUser(principal.getName());

        if (user == null)
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "User is not found", null));

        Object post = createPostHelper(request, principal);

        if (post == null)
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "Could not create post", null));

        return ResponseEntity.ok(new SimpleResponse("success", "Post successfully created", post));
    }

    @PostMapping("/post/video")
    public ResponseEntity<Object> createVideo(@RequestParam MultipartFile file, @RequestParam String message,
            Principal principal)
            throws IOException {
        User user = userService.getUser(principal.getName());

        if (user == null)
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "User is not found", null));

        if (file == null || file.isEmpty() || file.getSize() == 0)
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "Please upload a proper video", null));

        Object post = videoService
                .saveVideo(new Video(null, user, new ArrayList<>(), new ArrayList<>(),
                        message, (new Date()).getTime(), "VIDEO",
                        file.getOriginalFilename(), file.getBytes(), false));

        if (post == null)
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "Could not create post", null));

        return ResponseEntity.ok(new SimpleResponse("success", "Post successfully created", post));
    }

    @PostMapping("/post/{id}/comment")
    public ResponseEntity<Object> createComment(@RequestBody CommentRequest request, @PathVariable Long id,
            Principal principal) {
        User user = userService.getUser(principal.getName());
        Post post = postService.getPost(id);

        if (user == null || post == null || request.getComment() == null)
            return ResponseEntity.badRequest()
                    .body(new SimpleResponse("error", "User or post or comment is not found", null));

        Comment comment = commentService.saveComment(new Comment(null, user, post, request.getComment()));
        return ResponseEntity.ok(new SimpleResponse("success", "Comment is created", comment));
    }

    @PostMapping("/post/{id}/like")
    public ResponseEntity<Object> createLike(@PathVariable Long id, Principal principal) {
        User user = userService.getUser(principal.getName());
        Post post = postService.getPost(id);
        boolean alreadyLiked = false;

        for (Like like : post.getLikes())
            if (like.getOwner().getUsername().equals(principal.getName())) {
                alreadyLiked = true;
                break;
            }

        if (user == null || post == null)
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "User or post is not found", null));

        if (alreadyLiked)
            return ResponseEntity.badRequest()
                    .body(new SimpleResponse("error", "You have already liked this post", null));

        Like like = likeService.saveLike(new Like(null, user, post));
        return ResponseEntity.ok(new SimpleResponse("success", "Like is created", like));
    }

    // Update methods for posts
    @PutMapping("/post/video/{id}")
    public ResponseEntity<Object> updateVideo(@PathVariable Long id, @RequestParam MultipartFile file,
            @RequestParam String message,
            Principal principal)
            throws IOException {
        Post post = postService.getPost(id);

        if (post == null)
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "Post is not found", null));

        User user = post.getOwner();
        if (!user.getUsername().equals(principal.getName()))
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "You are not authorized", null));

        boolean success = false;

        if (message != null && file != null
                && file.getSize() != 0)
            success = videoService.updateVideo(id, message, file);

        if (!success)
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "Could not update the post", null));

        return ResponseEntity.ok(new SimpleResponse("success", "Post is updated", getPostHelper(post)));
    }

    @PutMapping("/post/{id}")
    public ResponseEntity<Object> updatePost(@PathVariable Long id, @RequestBody PostUpdateRequest request,
            Principal principal)
            throws IOException {
        Post post = postService.getPost(id);

        if (post == null)
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "Post is not found", null));

        User user = post.getOwner();
        if (!user.getUsername().equals(principal.getName()))
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "You are not authorized", null));

        boolean success = false;
        String postType = post.getPostType();

        if (postType.equals("ANNOUNCEMENT") && request.getMessage() != null)
            success = announcementService.updateAnnouncement(id, request.getMessage());
        else if (postType.equals("MEETING") && request.getMessage() != null && request.getLink() != null
                && request.getTime() != 0)
            success = meetingService.updateMeeting(id, request.getMessage(), request.getLink(), request.getTime());
        else if (postType.equals("EVENT") && request.getMessage() != null && request.getPlace() != null
                && request.getTime() != 0)
            success = eventService.updateEvent(id, request.getMessage(), request.getPlace(), request.getTime());
        else if (postType.equals("VIDEO") && request.getMessage() != null && request.getVideo() != null
                && request.getVideo().getSize() != 0)
            success = videoService.updateVideo(id, request.getMessage(), request.getVideo());

        if (!success)
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "Could not update the post", null));

        return ResponseEntity.ok(new SimpleResponse("success", "Post is updated", getPostHelper(post)));
    }

    @PutMapping("/post/comment/{id}")
    public ResponseEntity<Object> updateComment(@RequestBody CommentRequest request, @PathVariable Long id,
            Principal principal) {
        Comment comment = commentService.getComment(id);

        if (comment == null || request.getComment() == null)
            return ResponseEntity.badRequest()
                    .body(new SimpleResponse("error", "Comment or comment text is not found", null));

        User user = comment.getOwner();
        if (!user.getUsername().equals(principal.getName()))
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "You are not authorized", null));

        boolean success = commentService.updateComment(id, request.getComment());

        if (!success)
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "Could not update the comment", null));

        return ResponseEntity.ok(new SimpleResponse("success", "Comment is updated", commentService.getComment(id)));
    }

    // Delete methods for posts
    @DeleteMapping("/post/{id}")
    public ResponseEntity<Object> deletePost(@PathVariable Long id, Principal principal) {
        Post post = postService.getPost(id);

        if (post == null)
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "Post is not found", null));

        User user = post.getOwner();
        if (!user.getUsername().equals(principal.getName()))
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "You are not authorized", null));

        boolean success = postService.deletePost(id);

        if (!success)
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "Could not delete the post", null));

        return ResponseEntity.ok(new SimpleResponse("success", "Post is deleted", null));
    }

    @DeleteMapping("/post/comment/{id}")
    public ResponseEntity<Object> deleteComment(@PathVariable Long id, Principal principal) {
        Comment comment = commentService.getComment(id);

        if (comment == null)
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "Comment is not found", null));

        User user = comment.getOwner();
        if (!user.getUsername().equals(principal.getName()))
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "You are not authorized", null));

        boolean success = commentService.deleteComment(id);

        if (!success)
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "Could not delete the comment", null));

        return ResponseEntity.ok(new SimpleResponse("success", "Comment is deleted", null));
    }

    @DeleteMapping("/post/like/{id}")
    public ResponseEntity<Object> deleteLike(@PathVariable Long id, Principal principal) {
        Like like = likeService.getLike(id);

        if (like == null)
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "Like is not found", null));

        User user = like.getOwner();
        if (!user.getUsername().equals(principal.getName()))
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "You are not authorized", null));

        boolean success = likeService.deleteLike(id);

        if (!success)
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "Could not delete the like", null));

        return ResponseEntity.ok(new SimpleResponse("success", "Like is deleted", null));
    }

    // Helpers
    public Object createPostHelper(PostCreateRequest request, Principal principal) throws IOException {
        String postType = request.getPostType();
        Object retPost = null;
        User owner = userService.getUser(principal.getName());

        if (postType.equals("ANNOUNCEMENT") && request.getMessage() != null)
            retPost = announcementService
                    .saveAnnouncement(new Announcement(null, owner, new ArrayList<>(), new ArrayList<>(),
                            request.getMessage(), (new Date()).getTime(), false, "ANNOUNCEMENT"));
        else if (postType.equals("EVENT") && request.getMessage() != null && request.getTime() != 0
                && request.getPlace() != null)
            retPost = eventService
                    .saveEvent(new Event(null, owner, new ArrayList<>(), new ArrayList<>(),
                            request.getMessage(), (new Date()).getTime(), "EVENT", request.getTime(),
                            request.getPlace(), false));
        else if (postType.equals("MEETING") && request.getMessage() != null && request.getTime() != 0
                && request.getLink() != null)
            retPost = meetingService
                    .saveMeeting(new Meeting(null, owner, new ArrayList<>(), new ArrayList<>(),
                            request.getMessage(), (new Date()).getTime(), "MEETING", request.getTime(),
                            request.getLink(), false));
        else if (postType.equals("VIDEO") && request.getMessage() != null && request.getVideo() != null)
            retPost = videoService
                    .saveVideo(new Video(null, owner, new ArrayList<>(), new ArrayList<>(),
                            request.getMessage(), (new Date()).getTime(), "VIDEO",
                            request.getVideo().getOriginalFilename(), request.getVideo().getBytes(), false));

        return retPost;
    }

    public Object getPostHelper(Post post) {
        long id = post.getId();
        String postType = post.getPostType();
        Object retPost = null;

        if (postType.equals("ANNOUNCEMENT"))
            retPost = announcementService.getAnnouncement(id);
        else if (postType.equals("EVENT"))
            retPost = eventService.getEvent(id);
        else if (postType.equals("MEETING"))
            retPost = meetingService.getMeeting(id);
        else if (postType.equals("VIDEO"))
            retPost = videoService.getVideo(id);

        return retPost;
    }
}
