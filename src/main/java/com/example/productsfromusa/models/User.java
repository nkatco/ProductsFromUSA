package com.example.productsfromusa.models;

import com.example.productsfromusa.states.States;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @UuidGenerator
    private String id;
    private Long telegramId;
    private String name;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "wallet_id", referencedColumnName = "id")
    private Wallet wallet;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "phone_id", referencedColumnName = "id")
    private Phone phone;
    @CreationTimestamp
    private LocalDateTime firstLoginDate;
    private Boolean isActive;
    private String state;
    @OneToMany(mappedBy="user", fetch = FetchType.LAZY)
    private Set<Anons> anonsList;
    @OneToMany(mappedBy="user", fetch = FetchType.EAGER)
    private Set<Channel> channels;
    @OneToMany(mappedBy="user", fetch = FetchType.EAGER)
    private Set<Token> tokens;
    private long chatId;
    @OneToMany(mappedBy="user", fetch = FetchType.LAZY)
    private Set<Post> postsList;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            this.isActive = true;
        }
        if (id == null) {
            this.state = States.BASIC_STATE;
        }
        if(wallet == null) {
            this.wallet = new Wallet();
            this.wallet.setHistory("");
            this.wallet.setMoney(0);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, telegramId, name, firstLoginDate, isActive, state);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return Objects.equals(id, user.id) &&
                Objects.equals(telegramId, user.telegramId) &&
                Objects.equals(name, user.name) &&
                Objects.equals(phone, user.phone) &&
                Objects.equals(firstLoginDate, user.firstLoginDate) &&
                Objects.equals(isActive, user.isActive) &&
                Objects.equals(state, user.state);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", telegramId=" + telegramId +
                ", name='" + name + '\'' +
                ", firstLoginDate=" + firstLoginDate +
                ", isActive=" + isActive +
                ", state='" + state + '\'' +
                '}';
    }
}
