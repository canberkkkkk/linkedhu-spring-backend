package linkedhu.service;

import java.util.List;

import javax.transaction.Transactional;

import linkedhu.model.Event;
import linkedhu.model.User;
import linkedhu.repository.EventRepository;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {
    private final UserService userService;
    private final EventRepository eventRepository;

    // Create new event
    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }

    // Get event by id
    public Event getEvent(Long id) {
        return eventRepository.findById(id).orElse(null);
    }

    // Get event by username
    public List<Event> getEventsByUsername(String username) {
        User user = userService.getUser(username);

        if (user == null)
            return null;

        return eventRepository.findByOwner(user);
    }

    // Delete event
    public boolean deleteEvent(Long id) {
        Event event = getEvent(id);

        if (event == null)
            return false;

        // to-do delete all comments and likes
        eventRepository.delete(event);
        return true;
    }

    // Update event time and message
    public boolean updateEvent(Long id, String newMessage, String newPlace, long newTime) {
        Event existing = getEvent(id);

        if (existing == null)
            return false;

        existing.setMessage(newMessage);
        existing.setPlace(newPlace);
        existing.setTime(newTime);
        eventRepository.save(existing);
        return true;
    }
}
