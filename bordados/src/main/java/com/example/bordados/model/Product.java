package com.example.bordados.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.example.bordados.model.Enums.Color;
import com.example.bordados.model.Enums.FitType;
import com.example.bordados.model.Enums.Size;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "La descripción es obligatoria")
    private String description;

    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    private List<String> images = new ArrayList<>();

    @NotNull(message = "La cantidad es obligatoria")
    private int quantity;

    @Column(name = "min_quantity", nullable = false, columnDefinition = "int default 1")
    private int minQuantity = 1;
    
    @NotNull(message = "El precio es obligatorio")
    private double price;

    @NotNull
    private double discount = 0.0;

    @ElementCollection(targetClass = Size.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "product_sizes", joinColumns = @JoinColumn(name = "product_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "size")
    private Set<Size> sizes; // S, M, L, XL

    @ElementCollection(targetClass = Color.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "product_colors", joinColumns = @JoinColumn(name = "product_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "color")
    private Set<Color> colors; // WHITE, BLACK

    @ManyToOne
    @JoinColumn(name = "idSubcategory")
    private SubCategory subCategory;

    @ManyToOne
    @JoinColumn(name = "collection_id")
    private Collection collection;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "fit_type")
    private FitType fitType = FitType.STANDARD;
    
    @ManyToOne
    @JoinColumn(name = "idCategory")
    private Category category;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int salesCount;

    public String getMainImage() {
        return !this.images.isEmpty() ? this.images.get(0) : "default.jpg";
    }
}
