package com.example.productsfromusa.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usds")
public class USD {
    @Id
    @UuidGenerator
    private String id;
    private double course;
}
