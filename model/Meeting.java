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
@DiscriminatorValue(value = "MEETING")
@NoArgsConstructor
public class Meeting extends Post {
    private long time;
    private String link;

    public Meeting(Long id, User owner, Collection<Comment> comments, Collection<Like> likes, String message,
            long timestamp, String postType, long time, String link, boolean alreadyComplaint) {
        super(id, owner, likes, comments, message, timestamp, alreadyComplaint, postType);
        this.time = time;
        this.link = link;
    }
}
