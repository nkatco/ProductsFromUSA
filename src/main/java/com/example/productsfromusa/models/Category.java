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
@EqualsAndHashCode(exclude = {"posts", "anonses", "statistics"})
@ToString(exclude = {"posts", "anonses", "statistics"})
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @UuidGenerator
    private String id;
    private int number;
    private String name;
    @OneToMany(mappedBy="category", fetch = FetchType.LAZY)
    public Set<Post> posts;
    @OneToMany(mappedBy="category", fetch = FetchType.LAZY)
    public Set<Anons> anonses;
    @OneToMany(mappedBy="category", fetch = FetchType.LAZY)
    private Set<Statistic> statistics;
}
