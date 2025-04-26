package com.example.bordados.model;

import java.util.ArrayList;
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

@Entity
@Data
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private int quantity;

    @ElementCollection
    @CollectionTable(name = "order_detail_sizes", joinColumns = @JoinColumn(name = "order_detail_id"))
    @Column(name = "size")
    @Enumerated(EnumType.STRING)
    private List<Size> sizes;

    @ElementCollection
    @CollectionTable(name = "order_detail_colors", joinColumns = @JoinColumn(name = "order_detail_id"))
    @Column(name = "color")
    @Enumerated(EnumType.STRING)
    private List<Color> colors;

    @ElementCollection
    @CollectionTable(name = "order_detail_fit_types", joinColumns = @JoinColumn(name = "order_detail_id"))
    @Column(name = "fit_type")
    @Enumerated(EnumType.STRING)
    private List<FitType> fitTypes;

    public void setSizes(List<Size> sizes) {
        this.sizes = sizes != null ? new ArrayList<>(sizes) : new ArrayList<>();
    }

    public void setColors(List<Color> colors) {
        this.colors = colors != null ? new ArrayList<>(colors) : new ArrayList<>();
    }

    public void setFitTypes(List<FitType> fitTypes) {
        this.fitTypes = fitTypes != null ? new ArrayList<>(fitTypes) : new ArrayList<>();
    }
}
