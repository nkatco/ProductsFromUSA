package com.example.productsfromusa.models;


import com.example.productsfromusa.states.States;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "anonses")
public class Anons implements Serializable {
    @Id
    @UuidGenerator
    private String id;
    @ManyToOne
    @JoinColumn(name="token_id", nullable=true)
    private Token token;
    @ManyToOne
    @JoinColumn(name="user_id", nullable=true)
    private User user;
    private String date;
    @OneToMany(mappedBy="anons", fetch = FetchType.EAGER)
    private Set<AnonsPost> anonsPosts;
    private int posts;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "last_post_id", referencedColumnName = "id")
    private LastPost lastPost;
    @ManyToOne
    @JoinColumn(name="category_id", nullable=true)
    private Category category;

    @Override
    public int hashCode() {
        return Objects.hash(id, date, posts);
    }
}
