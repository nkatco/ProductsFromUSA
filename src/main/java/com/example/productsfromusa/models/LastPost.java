package com.example.productsfromusa.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "last_anons_posts")
public class LastPost implements Serializable {
    @Id
    @UuidGenerator
    private String id;
    @ManyToOne
    @JoinColumn(name="post_id", nullable=true)
    private Post post;

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
