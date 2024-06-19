package com.example.productsfromusa.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "trash_anonss_posts")
public class TrashAnonsPost implements Serializable {
    @Id
    @UuidGenerator
    private String id;
    @ManyToOne
    @JoinColumn(name="anons_id", nullable=true)
    private Anons anons;
    @ManyToOne
    @JoinColumn(name="post_id", nullable=true)
    private Post post;

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
