package linkedhu.model;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.DiscriminatorType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "post_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @PrimaryKeyJoinColumn
    @JoinColumn(name = "owner")
    private User owner;

    @OneToMany
    private Collection<Like> likes;

    @OneToMany
    private Collection<Comment> comments;

    private String message;
    private long timestamp;
    private Boolean alreadyComplaint;

    @Column(name = "post_type", insertable = false, updatable = false)
    private String postType;
}
