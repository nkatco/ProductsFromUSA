package com.example.productsfromusa.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "prechannels")
public class PreChannel {
    @Id
    @UuidGenerator
    private String id;
    private String name;
    private long telegramId;
    private long chatId;
}
