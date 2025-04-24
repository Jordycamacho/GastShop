package com.example.bordados.service.ServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.bordados.DTOs.CartDTO;
import com.example.bordados.model.Cart;
import com.example.bordados.model.Product;
import com.example.bordados.model.User;
import com.example.bordados.model.Enums.Color;
import com.example.bordados.model.Enums.FitType;
import com.example.bordados.model.Enums.Size;
import com.example.bordados.repository.CartRepository;
import com.example.bordados.service.CartService;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductServiceImpl productService;

    @Autowired
    private UserServiceImpl userService;

    @Override
    public List<CartDTO> getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId).stream()
            .map(cart -> {
                Product product = cart.getProduct();
                return CartDTO.builder()
                    .id(cart.getId())
                    .productId(product.getId())
                    .productName(product.getName())
                    .quantity(cart.getQuantity())
                    .sizes(cart.getSizes())
                    .colors(cart.getColors())
                    .price(product.getPrice())
                    .fitTypes(cart.getFitTypes())
                    .image(!product.getImages().isEmpty() 
                          ? product.getImages().get(0) 
                          : "default.jpg")
                    .build();
            })
            .collect(Collectors.toList());
    }

    @Override
    public void addProductToCart(Long userId, Long productId, int quantity, List<Size> sizes, List<Color> colors, List<FitType> fitTypes) {
        User user = userService.getCurrentUser();
        Product product = productService.getProductById(productId);

        if (product.getQuantity() < quantity) {
            throw new IllegalArgumentException("Cantidad no disponible");
        }

        if (sizes.size() != quantity || colors.size() != quantity || fitTypes.size() != quantity) {
            throw new IllegalArgumentException("Número de selecciones no coincide con la cantidad");
        }

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setProduct(product);
        cart.setQuantity(quantity);
        cart.setSizes(sizes);
        cart.setColors(colors);
        cart.setFitTypes(fitTypes);

        cartRepository.save(cart);
    }

    @Override
    public void removeFromCart(Long cartId) {
        cartRepository.deleteById(cartId);
    }
}

