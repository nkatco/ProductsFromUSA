package com.example.productsfromusa.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"user", "anonsPosts", "lastPosts", "category"})
@ToString(exclude = {"user", "anonsPosts", "lastPosts", "category"})
@Entity
@Table(name = "posts")
public class Post {
    @Id
    @UuidGenerator
    private String id;
    private int number;
    private String name;
    private String text;
    private int price;
    private int oldPrice;
    private String ref;
    private String image;
    @ManyToOne
    @JoinColumn(name="user_id", nullable=true)
    private User user;
    @OneToMany(mappedBy="post", fetch = FetchType.EAGER)
    public Set<AnonsPost> anonsPosts;
    @OneToMany(mappedBy="post", fetch = FetchType.EAGER)
    public Set<LastPost> lastPosts;
    @ManyToOne
    @JoinColumn(name="category_id", nullable=true)
    private Category category;
    @CreationTimestamp
    private LocalDateTime creationDate;
}
