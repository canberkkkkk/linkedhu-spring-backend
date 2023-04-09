package linkedhu.controller;

import java.security.Principal;
import java.util.Date;
import java.util.List;

import linkedhu.constants.RoleConstants;
import linkedhu.model.Complaint;
import linkedhu.model.Post;
import linkedhu.model.User;
import linkedhu.request.CreateComplaintRequest;
import linkedhu.response.SimpleResponse;
import linkedhu.service.ComplaintService;
import linkedhu.service.PostService;
import linkedhu.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:3000")
public class ComplaintController {
    private final UserService userService;
    private final PostService postService;
    private final ComplaintService complaintService;

    // Get methods for complaint
    @GetMapping("/complaints")
    @PreAuthorize("hasAuthority('" + RoleConstants.ROLE_ADMIN + "')")
    public ResponseEntity<Object> getComplaints() {
        List<Complaint> complaints = complaintService.getComplaints();
        return ResponseEntity.ok(new SimpleResponse("success", "Successfully retrieved", complaints));
    }

    // Post methods for complaint
    @PostMapping("/complaint/{postId}")
    public ResponseEntity<Object> createComplaint(@RequestBody CreateComplaintRequest request,
            @PathVariable Long postId, Principal principal) {
        if (request.getReason() == null)
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "Please specify reason", null));

        User user = userService.getUser(principal.getName());
        Post post = postService.getPost(postId);

        if (post == null)
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "Post is not found", null));

        if (post.getAlreadyComplaint())
            return ResponseEntity.badRequest()
                    .body(new SimpleResponse("error", "This post is already complaint to admin", null));

        Complaint complaint = complaintService
                .saveComplaint(new Complaint(null, user, post, request.getReason(), (new Date()).getTime()));
        return ResponseEntity.ok(new SimpleResponse("success", "Successfully created", complaint));
    }
}
