package com.example.bordados.controller.user;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.example.bordados.DTOs.CartDTO;
import com.example.bordados.DTOs.CategorySubCategoryDTO;
import com.example.bordados.DTOs.NoticesDTO;
import com.example.bordados.model.User;
import com.example.bordados.model.Enums.Color;
import com.example.bordados.model.Enums.FitType;
import com.example.bordados.model.Enums.Size;
import com.example.bordados.service.CartService;
import com.example.bordados.service.CategoryService;
import com.example.bordados.service.IUserService;
import com.example.bordados.service.NoticesService;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
@AllArgsConstructor
@RequestMapping("/bordados/carrito")
public class CartController {

    private final CartService cartService;
    private final IUserService userService;
    private final CategoryService categoryService;
    private final NoticesService noticesService;

    @ModelAttribute("categoriesWithSub")
    public List<CategorySubCategoryDTO> getCategoriesWithSubCategories() {
        return categoryService.getAllCategoriesWithSubCategories();
    }

    @ModelAttribute("currentNotices")
    public NoticesDTO getCurrentNotices() {
        return noticesService.getCurrentNotices();
    }

    @GetMapping("")
    public String ShowViewCart(Model model, Principal principal) {
        User user = userService.getCurrentUser();

        List<CartDTO> cartItems = cartService.getCartByUserId(user.getId());
        model.addAttribute("cartItems", cartItems);
        return "user/cart";
    }

    @PostMapping("/agregar")
    public String addToCart(@RequestParam Long productId,
            @RequestParam int quantity,
            @RequestParam("sizes") List<String> sizeStrings,
            @RequestParam("colors") List<String> colorStrings,
            @RequestParam("fits") List<String> fitStrings) {

        User user = userService.getCurrentUser();

        try {
            // Convertir strings a enums con manejo de errores detallado
            List<Size> sizes = new ArrayList<>();
            for (String sizeStr : sizeStrings) {
                try {
                    sizes.add(Size.valueOf(sizeStr.trim().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Talla inválida: " + sizeStr + ". Valores permitidos: " + Arrays.toString(Size.values()));
                }
            }

            List<Color> colors = new ArrayList<>();
            for (String colorStr : colorStrings) {
                try {
                    colors.add(Color.valueOf(colorStr.trim().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Color inválido: " + colorStr + ". Valores permitidos: " + Arrays.toString(Color.values()));
                }
            }

            List<FitType> fits = new ArrayList<>();
            for (String fitStr : fitStrings) {
                try {
                    fits.add(FitType.valueOf(fitStr.trim().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Tipo de ajuste inválido: " + fitStr + ". Valores permitidos: "
                                    + Arrays.toString(FitType.values()));
                }
            }

            cartService.addProductToCart(user.getId(), productId, quantity, sizes, colors, fits);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error en los datos: " + e.getMessage());
        }

        return "redirect:/bordados/carrito";
    }

    @PostMapping("/eliminar")
    public String removeFromCart(@RequestParam("cartId") Long cartId, Principal principal) {

        cartService.removeFromCart(cartId);

        return "redirect:/bordados/carrito";
    }

}
