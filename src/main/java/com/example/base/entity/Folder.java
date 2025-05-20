package com.example.base.entity;

import lombok.Data;
import javax.persistence.*;

@Entity
@Data
public class Folder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
} 