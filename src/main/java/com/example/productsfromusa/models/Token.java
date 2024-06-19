package com.example.productsfromusa.models;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "tokens")
public class Token implements Serializable {
    @Id
    @UuidGenerator
    private String id;
    private String name;
    private String dateOfPurchase;
    private String dateOfExpiration;
    private long price;
    @OneToOne
    @JoinColumn(name = "channel_id", referencedColumnName = "id", nullable = true)
    private Channel channel;
    private int anons;
    private boolean isActive;
    @ManyToOne
    @JoinColumn(name="user_id", nullable=true)
    private User user;
    @OneToMany(mappedBy="token", fetch = FetchType.EAGER)
    private Set<Anons> anonsList;

    @Override
    public int hashCode() {
        return Objects.hash(id, name, dateOfPurchase, dateOfExpiration, price, isActive);
    }
}
