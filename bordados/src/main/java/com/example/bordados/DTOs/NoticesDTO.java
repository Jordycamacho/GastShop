package com.example.bordados.DTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NoticesDTO {
    private Long id;
    
    @NotBlank(message = "Las ofertas no pueden estar vacías")
    private String offers = "texto prueba ofertas";
    
    @NotBlank(message = "El banner principal no puede estar vacío")    
    private String bannerMain = "texto prueba ofertas";
    
    @NotBlank(message = "El banner secundario no puede estar vacío")
    private String bannerSecondary = "texto prueba ofertas";
}