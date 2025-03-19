package com.example.twitter.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Document(collection = "Posts")
public class Post {

    @Id
    private String id;
    private String userId;
    private LocalDate creationDate;

    @Size(max = 140, message = "Content must be at most 140 characters")
    private String content;

    private String parentPostId;

    private String threadRootId;

    private List<String> replies = new ArrayList<>();


    public Post() {
    }

    public Post(String userId, String content) {
        this.userId = userId;
        this.creationDate = LocalDate.now();
        this.content = content;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getParentPostId() {
        return parentPostId;
    }

    public void setParentPostId(String parentPostId) {
        this.parentPostId = parentPostId;
    }

    public String getThreadRootId() {
        return threadRootId;
    }

    public void setThreadRootId(String threadRootId) {
        this.threadRootId = threadRootId;
    }

    public List<String> getReplies() {
        return replies;
    }

    public void setReplies(List<String> replies) {
        this.replies = replies;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", creationDate=" + creationDate +
                ", content='" + content + '\'' +
                ", parentPostId='" + parentPostId + '\'' +
                ", threadRootId='" + threadRootId + '\'' +
                ", replies=" + replies +
                '}';
    }
}
