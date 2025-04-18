package com.example.bordados.DTOs;

import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectionDTO {
    private Long id;
    
    @NotBlank(message = "El nombre de la colección es obligatorio")
    private String nameCollection;
    
    private String imageCollection;
    
    @NotNull(message = "La categoría es obligatoria")
    private Long categoryId;
    
    private String categoryName;

    @Transient
    private MultipartFile imageFile;
}