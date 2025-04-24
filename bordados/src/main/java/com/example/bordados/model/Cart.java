package com.example.bordados.model;

import java.util.List;

import com.example.bordados.model.Enums.Color;
import com.example.bordados.model.Enums.FitType;
import com.example.bordados.model.Enums.Size;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private int quantity;

    @ElementCollection
    @CollectionTable(name = "cart_sizes", joinColumns = @JoinColumn(name = "cart_id"))
    @Column(name = "size")
    @Enumerated(EnumType.STRING)
    private List<Size> sizes;

    @ElementCollection
    @CollectionTable(name = "cart_colors", joinColumns = @JoinColumn(name = "cart_id"))
    @Column(name = "color")
    @Enumerated(EnumType.STRING)
    private List<Color> colors;

    @ElementCollection
    @CollectionTable(name = "cart_fit_types", joinColumns = @JoinColumn(name = "cart_id"))
    @Column(name = "fit_type")
    @Enumerated(EnumType.STRING)
    private List<FitType> fitTypes;
}
