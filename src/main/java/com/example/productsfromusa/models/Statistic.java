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
@Table(name = "statistics")
public class Statistic {
    @Id
    @UuidGenerator
    private String id;

    @ManyToOne
    @JoinColumn(name="channel_id", nullable=true)
    private Channel channel;
    @ManyToOne
    @JoinColumn(name="category_id", nullable=true)
    private Category category;
    private String ip;
    private String linkId;

    @CreationTimestamp
    private LocalDateTime creationDate;
}
