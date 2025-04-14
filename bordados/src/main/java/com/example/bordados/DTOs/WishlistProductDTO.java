package com.example.bordados.DTOs;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishlistProductDTO {
    private Long id;
    private String name;
    private String description;
    private List<String> imagePaths;
    private double price;
    private double discount;
}
