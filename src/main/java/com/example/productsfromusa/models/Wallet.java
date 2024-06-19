package com.example.productsfromusa.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "wallets")
public class Wallet {

    @Id
    @UuidGenerator
    private String id;
    private long money;
    private String history;

    @Override
    public int hashCode() {
        return Objects.hash(id, money);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Wallet wallet = (Wallet) obj;
        return Objects.equals(id, wallet.id) &&
                Objects.equals(money, wallet.money);
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "id='" + id + '\'' +
                ", money=" + money +
                '}';
    }
}
