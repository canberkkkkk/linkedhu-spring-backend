package linkedhu.model;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@DiscriminatorValue(value = "VIDEO")
@NoArgsConstructor
public class Video extends Post {
    private String videoName;

    @Column(length = 20000000)
    private byte[] video;

    public Video(Long id, User owner, Collection<Comment> comments, Collection<Like> likes, String message,
            long timestamp, String postType, String videoName, byte[] video, boolean alreadyComplaint) {
        super(id, owner, likes, comments, message, timestamp, alreadyComplaint, postType);
        this.videoName = videoName;
        this.video = video;
    }
}
