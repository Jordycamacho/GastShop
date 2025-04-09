package com.example.bordados.DTOs;

import java.util.List;

import com.example.bordados.model.Enums.Color;
import com.example.bordados.model.Enums.Size;

import lombok.Data;

@Data
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String category;
    private List<String> images;
    private int quantity;
    private double price;
    private Size size;
    private Color color;
}