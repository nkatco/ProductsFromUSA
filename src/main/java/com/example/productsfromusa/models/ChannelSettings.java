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
@Table(name = "channelSettings")
public class ChannelSettings {
    @Id
    @UuidGenerator
    private String id;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "watermark_id", referencedColumnName = "id")
    private WatermarkImage watermarkImage;
    private String priceNote;
    private String postText;
    private boolean showPrice;
    private boolean showOldPrice;
    private double addCourse;
    @Column(columnDefinition = "VARCHAR(3) DEFAULT 'USD'")
    private String course;
    private int reduction;
}
