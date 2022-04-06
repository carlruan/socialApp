package com.kaifengruan.socialapp.POJO;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name="posts")
@EntityListeners(AuditingEntityListener.class)
public class Post{

    @Id
    @Column(name = "post_id")
    private String postId = UUID.randomUUID().toString();

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @NotBlank
    private String content;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date post_created;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date post_updated;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public Date getPost_created() {
        return post_created;
    }

    public Date getPost_updated() {
        return post_updated;
    }

}
