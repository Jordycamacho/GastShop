package com.example.bordados.DTOs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.example.bordados.model.Enums.Color;
import com.example.bordados.model.Enums.Size;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long id;
    
    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "La descripción es obligatoria")
    private String description;

    @Builder.Default
    @NotNull(message = "Debe proporcionar al menos una imagen")
    private List<MultipartFile> images = new ArrayList<>();
    
    @NotNull(message = "La cantidad es obligatoria")
    private int quantity;

    @NotNull(message = "El precio es obligatorio")
    private double price;

    @Builder.Default
    @NotNull
    private double discount = 0.0;

    @Builder.Default
    @NotNull(message = "La talla es obligatoria")
    private Set<Size> sizes = new HashSet<>();

    @Builder.Default
    @NotNull(message = "El color es obligatorio")
    private Set<Color> colors = new HashSet<>();

    @NotNull(message = "La categoría es obligatoria")
    private Long categoryId;

    @Builder.Default
    private Long subCategoryId = null;

    private List<String> existingImages;
    private List<MultipartFile> newImages; 
    private List<String> imagesToDelete;
}
