package com.example.productsfromusa.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "watermark_images")
public class WatermarkImage {

    @Id
    @UuidGenerator
    private String id;
    private String path;
    private float alpha;
    private String mode;
    private int size;
}
