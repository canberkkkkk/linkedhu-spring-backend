package linkedhu.service;

import java.util.List;

import javax.transaction.Transactional;

import linkedhu.model.Announcement;
import linkedhu.model.User;
import linkedhu.repository.AnnouncementRepository;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AnnouncementService {
    private final UserService userService;
    private final AnnouncementRepository announcementRepository;

    // Create new announcement
    public Announcement saveAnnouncement(Announcement announcement) {
        return announcementRepository.save(announcement);
    }

    // Get announcement by id
    public Announcement getAnnouncement(Long id) {
        return announcementRepository.findById(id).orElse(null);
    }

    // Get announcement by username
    public List<Announcement> getAnnouncementsByUsername(String username) {
        User user = userService.getUser(username);

        System.out.println(username);
        if (user == null)
            return null;

        return announcementRepository.findByOwner(user);
    }

    // Delete announcement
    public boolean deleteAnnouncement(Long id) {
        Announcement announcement = getAnnouncement(id);

        if (announcement == null)
            return false;

        // to-do delete all comments and likes
        announcementRepository.delete(announcement);
        return true;
    }

    // Update announcement message
    public boolean updateAnnouncement(Long id, String newMessage) {
        Announcement existing = getAnnouncement(id);

        if (existing == null)
            return false;

        existing.setMessage(newMessage);
        announcementRepository.save(existing);
        return true;
    }
}
