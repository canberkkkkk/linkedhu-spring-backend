package linkedhu.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.transaction.Transactional;

import linkedhu.model.Announcement;
import linkedhu.model.Complaint;
import linkedhu.model.Event;
import linkedhu.model.Meeting;
import linkedhu.model.Post;
import linkedhu.model.Video;
import linkedhu.repository.CommentRepository;
import linkedhu.repository.ComplaintRepository;
import linkedhu.repository.LikeRepository;
import linkedhu.repository.PostRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final AnnouncementService announcementService;
    private final EventService eventService;
    private final MeetingService meetingService;
    private final VideoService videoService;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final ComplaintRepository complaintRepository;
    private final PostRepository postRepository;
    private final int PAGE_SIZE = 1000;
    private final String SORT_FIELD = "timestamp";

    // Get post by id
    public Post getPost(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    // Get post type
    public String getPostType(Long id) {
        Post post = getPost(id);

        if (post == null)
            return null;

        return post.getPostType();
    }

    // Get posts by username
    public List<Object> getPostsByUsername(String username) {
        List<Object> posts = new ArrayList<>();
        List<Announcement> announcements = announcementService.getAnnouncementsByUsername(username);
        List<Event> events = eventService.getEventsByUsername(username);
        List<Meeting> meetings = meetingService.getMeetingsByUsername(username);
        List<Video> videos = videoService.getVideosByUsername(username);

        if (announcements != null)
            announcements.stream().forEach(post -> {
                posts.add(post);
            });

        if (events != null)
            events.stream().forEach(post -> {
                posts.add(post);
            });

        if (meetings != null)
            meetings.stream().forEach(post -> {
                posts.add(post);
            });

        if (videos != null)
            videos.stream().forEach(post -> {
                posts.add(post);
            });

        Collections.sort(posts, (o1, o2) -> -1 * (int) (((Post) o1).getTimestamp() - ((Post) o2).getTimestamp()));
        return posts;
    }

    // Get latest posts
    public List<Object> getLatestPosts(int page) {
        List<Object> latestPosts = new ArrayList<>();
        Page<Post> paginated = postRepository
                .findAll(PageRequest.of(page, PAGE_SIZE).withSort(Sort.by(SORT_FIELD).descending()));

        paginated.stream().forEach(post -> {
            Long id = post.getId();
            if (post.getPostType().equals("ANNOUNCEMENT"))
                latestPosts.add(announcementService.getAnnouncement(id));
            else if (post.getPostType().equals("MEETING"))
                latestPosts.add(meetingService.getMeeting(id));
            else if (post.getPostType().equals("EVENT"))
                latestPosts.add(eventService.getEvent(id));
            else if (post.getPostType().equals("VIDEO"))
                latestPosts.add(videoService.getVideo(id));
        });

        return latestPosts;
    }

    // Delete post
    public boolean deletePost(Long id) {
        Post post = getPost(id);

        if (post == null)
            return false;

        Complaint complaint = complaintRepository.findByPost(post);

        if (complaint != null)
            complaintRepository.delete(complaint);

        likeRepository.deleteAll(post.getLikes());
        commentRepository.deleteAll(post.getComments());
        postRepository.delete(post);
        return true;
    }
}
