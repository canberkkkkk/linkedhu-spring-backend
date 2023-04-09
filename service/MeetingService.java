package linkedhu.service;

import java.util.List;

import javax.transaction.Transactional;

import linkedhu.model.Meeting;
import linkedhu.model.User;
import linkedhu.repository.MeetingRepository;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class MeetingService {
    private final UserService userService;
    private final MeetingRepository meetingRepository;

    // Create new meeting
    public Meeting saveMeeting(Meeting meeting) {
        return meetingRepository.save(meeting);
    }

    // Get meeting by id
    public Meeting getMeeting(Long id) {
        return meetingRepository.findById(id).orElse(null);
    }

    // Get meeting by username
    public List<Meeting> getMeetingsByUsername(String username) {
        User user = userService.getUser(username);

        if (user == null)
            return null;

        return meetingRepository.findByOwner(user);
    }

    // Delete meeting
    public boolean deleteMeeting(Long id) {
        Meeting meeting = getMeeting(id);

        if (meeting == null)
            return false;

        // to-do delete all comments and likes
        meetingRepository.delete(meeting);
        return true;
    }

    // Update meeting time and link
    public boolean updateMeeting(Long id, String newMessage, String newLink, long newTime) {
        Meeting existing = getMeeting(id);

        if (existing == null)
            return false;

        existing.setMessage(newMessage);
        existing.setLink(newLink);
        existing.setTime(newTime);
        meetingRepository.save(existing);
        return true;
    }
}
