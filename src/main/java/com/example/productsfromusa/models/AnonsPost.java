package com.example.productsfromusa.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "anonss_posts")
public class AnonsPost implements Serializable {
    @Id
    @UuidGenerator
    private String id;
    @ManyToOne
    @JoinColumn(name="anons_id", nullable=true)
    private Anons anons;
    @ManyToOne
    @JoinColumn(name="post_id", nullable=true)
    private Post post;
    @OneToMany(mappedBy="anonsPost", fetch = FetchType.EAGER)
    public Set<ShortLink> shortLinks;
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
