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
@DiscriminatorValue(value = "ANNOUNCEMENT")
@NoArgsConstructor
public class Announcement extends Post {
    public Announcement(Long id, User owner, Collection<Comment> comments, Collection<Like> likes, String message,
            long timestamp, boolean alreadyComplaint, String postType) {
        super(id, owner, likes, comments, message, timestamp, alreadyComplaint, postType);
    }
}
