package linkedhu.service;

import java.util.List;

import javax.transaction.Transactional;

import linkedhu.model.Complaint;
import linkedhu.model.Post;
import linkedhu.repository.ComplaintRepository;
import linkedhu.repository.PostRepository;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ComplaintService {
    private final PostRepository postRepository;
    private final ComplaintRepository complaintRepository;
    private final String SORT_FIELD = "timestamp";

    // Save complaint
    public Complaint saveComplaint(Complaint complaint) {
        Post post = complaint.getPost();
        post.setAlreadyComplaint(true);
        postRepository.save(post);
        return complaintRepository.save(complaint);
    }

    // Get complaints
    public List<Complaint> getComplaints() {
        return complaintRepository.findAll(Sort.by(Sort.Direction.DESC, SORT_FIELD));
    }
}
