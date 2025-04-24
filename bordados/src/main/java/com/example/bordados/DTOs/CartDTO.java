package com.example.bordados.DTOs;

import java.util.List;

import com.example.bordados.model.Enums.Color;
import com.example.bordados.model.Enums.FitType;
import com.example.bordados.model.Enums.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {
    private Long id;
    private Long productId;
    private String productName;
    private int quantity;
    private double price;
    private String image; 
    private List<Size> sizes;
    private List<Color> colors;
    private List<FitType> fitTypes;
}

