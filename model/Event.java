package linkedhu.model;

import java.util.Collection;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@DiscriminatorValue(value = "EVENT")
@NoArgsConstructor
public class Event extends Post {
    private long time;
    private String place;

    public Event(Long id, User owner, Collection<Comment> comments, Collection<Like> likes, String message,
            long timestamp, String postType, long time, String place, boolean alreadyComplaint) {
        super(id, owner, likes, comments, message, timestamp, alreadyComplaint, postType);
        this.time = time;
        this.place = place;
    }
}
