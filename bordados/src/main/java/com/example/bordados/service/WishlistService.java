package com.example.bordados.service;

import java.util.List;

import com.example.bordados.DTOs.WishlistProductDTO;

public interface WishlistService {
    void addToWishlist(Long productId);
    void removeFromWishlist(Long productId);
    List<WishlistProductDTO> getWishlistByUser(Long userId);
}