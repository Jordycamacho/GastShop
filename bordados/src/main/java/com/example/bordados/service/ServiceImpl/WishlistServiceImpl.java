package com.example.bordados.service.ServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.bordados.DTOs.WishlistProductDTO;
import com.example.bordados.model.Product;
import com.example.bordados.model.User;
import com.example.bordados.model.Wishlist;
import com.example.bordados.repository.ProductRepository;
import com.example.bordados.repository.UserRepository;
import com.example.bordados.repository.WishlistRepository;
import com.example.bordados.service.WishlistService;

import jakarta.transaction.Transactional;

@Service
public class WishlistServiceImpl implements WishlistService {
    @Autowired
    private WishlistRepository wishlistRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserServiceImpl userService;

    @Override
    public void addToWishlist(Long productId) {
        User user = userService.getCurrentUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Wishlist wishlist = Wishlist.builder()
                .user(user)
                .product(product)
                .build();
        wishlistRepository.save(wishlist);
    }

    @Override
    @Transactional
    public void removeFromWishlist(Long productId) {
        User user = userService.getCurrentUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        wishlistRepository.findByUserAndProduct(user, product)
                .ifPresentOrElse(
                        wishlistRepository::delete,
                        () -> {
                            throw new RuntimeException("Producto no encontrado en tu lista de deseos");
                        });
    }

    @Override
    public List<WishlistProductDTO> getWishlistByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        List<Wishlist> wishlists = wishlistRepository.findByUser(user);
        
        return wishlists.stream()
                .map(wishlist -> convertToWishlistDTO(wishlist.getProduct()))
                .collect(Collectors.toList());
    }

    private WishlistProductDTO convertToWishlistDTO(Product product) {
        return WishlistProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .imagePaths(product.getImages())
                .price(product.getPrice())
                .discount(product.getDiscount())

                .build();
    }
}
