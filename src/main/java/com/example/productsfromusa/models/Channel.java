package com.example.productsfromusa.models;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.Objects;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode
@ToString
@Table(name = "channels")
public class Channel {
    @Id
    @UuidGenerator
    private String id;
    private String name;
    private long telegramId;
    private long chatId;
    @OneToMany(mappedBy="channel", fetch = FetchType.LAZY)
    private Set<Statistic> statistics;
    private boolean isActive;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "channelSettings_id", referencedColumnName = "id")
    private ChannelSettings channelSettings;
    @ManyToOne
    @JoinColumn(name="user_id", nullable=true)
    private User user;
}
