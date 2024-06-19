package com.example.productsfromusa.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "short_links")
public class ShortLink {
    @Id
    @UuidGenerator
    private String id;
    @ManyToOne
    @JoinColumn(name="anons_post_id", nullable=true)
    private AnonsPost anonsPost;
    private String link;
    @CreationTimestamp
    private LocalDateTime creationDate;
}
