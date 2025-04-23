package com.example.bordados.DTOs;

import java.util.List;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.example.bordados.model.Enums.Color;
import com.example.bordados.model.Enums.FitType;
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
    
    @NotBlank
    private String name;
    
    @NotBlank
    private String description;

    private List<MultipartFile> images;
    
    @NotNull
    private Integer quantity;
    
    @NotNull
    private Double price;
    
    @Builder.Default
    private Double discount = 0.0;
    
    @NotNull
    private Set<Size> sizes;
    
    @NotNull
    private Set<Color> colors;
    
    @NotNull
    private Long categoryId;

    @Builder.Default
    @NotNull
    private FitType fitType = FitType.STANDARD;
    
    private Long subCategoryId;

    private Long  collectionId;
    
    // Solo para edición
    private List<String> existingImages;
    private List<MultipartFile> newImages;
    private List<String> imagesToDelete;
}