package com.example.bordados.controller.user;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.bordados.DTOs.CategorySubCategoryDTO;
import com.example.bordados.DTOs.CollectionDTO;
import com.example.bordados.DTOs.NoticesDTO;
import com.example.bordados.DTOs.UserDTO;
import com.example.bordados.model.Product;
import com.example.bordados.service.CategoryService;
import com.example.bordados.service.CollectionService;
import com.example.bordados.service.IUserService;
import com.example.bordados.service.NoticesService;
import com.example.bordados.service.ProductService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@AllArgsConstructor
@RequestMapping("/bordados")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final IUserService userService;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final CollectionService collectionService;
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
    public String getProducts(Model model) {
        List<Product> customizableProducts = productService.getCustomizableProducts();
        List<Product> bestsellrs = productService.getTopSellingProducts();
        List<Product> randomProducts = productService.getRandomProducts();
        List<CollectionDTO> collections = collectionService.getAllCollections();

        model.addAttribute("customizableProducts", customizableProducts);
        model.addAttribute("bestsellers", bestsellrs);
        model.addAttribute("randomProducts", randomProducts);
        model.addAttribute("collections", collections);
        
        return "user/index";
    }

    @GetMapping("/categoria/{id}")
    public String getProductsByCategoryOrSubCategory(@PathVariable Long id, Model model) {
        List<Product> products = productService.getProductBySubCategory(id);
        model.addAttribute("products", products);
        return "user/products";
    }

    @GetMapping("/colecciones/{id}")
    public String getProductsByCollection(@PathVariable Long id, Model model) {
        List<Product> products = productService.getProductsByCollection(id);
        model.addAttribute("products", products);
        return "user/products";
    }

    
    @GetMapping("/login")
    public String getLogin(@RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {

        if (error != null) {
            model.addAttribute("error", "Correo electrónico o contraseña incorrectos.");
        }
        if (logout != null) {
            model.addAttribute("message", "Has cerrado sesión correctamente.");
        }
        return "user/SingIn";
    }

    @GetMapping("/registro")
    public String getRegistro(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "user/SingUp";
    }

    @GetMapping("/login-success")
    public String postLogin(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            Collection<? extends GrantedAuthority > authorities = authentication.getAuthorities();
            for (GrantedAuthority grantedAuthority : authorities) {
                if (grantedAuthority.getAuthority().equals("ROLE_ADMIN")) {
                    return "redirect:/admin/productos";
                }else if (grantedAuthority.getAuthority().equals("ROLE_USER")) {
                    return "redirect:/bordados";
                }
            }
        }
        return "redirect:/bordados/login";
    }

    @PostMapping("/registro")
    public String postRegistro(@Valid @ModelAttribute("userDTO") UserDTO userDTO, BindingResult result, Model model) {

        if (result.hasErrors()) {
            logger.warn("Error en el formulario de registro: {}", result.getAllErrors());
            return "user/SingUp";
        }

        try {
            // Registrar al usuario
            userService.registerUser(userDTO);
            logger.info("Usuario registrado exitosamente: {}", userDTO.getEmail());

            return "redirect:/bordados/login";
        } catch (IllegalArgumentException e) {
            logger.error("Error durante el registro: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "user/SingUp";
        } catch (Exception e) {
            logger.error("Error inesperado durante el registro: {}", e.getMessage());
            model.addAttribute("error", "Ocurrió un error inesperado. Inténtalo de nuevo.");
            return "user/SingUp";
        }
    }

    @GetMapping("/afiliacion")
    public String getviewAffiliation() {
        return "user/affiliate";
    }

    @GetMapping("/politicaprivacidad")
    public String getviewPrivacyPolicy() {
        return "user/privacyPolicy";
    }

    @GetMapping("/politicaenviosrembolsos")
    public String getMethodName() {
        return "user/refundPolicy";
    }
    
    @GetMapping("/terminosservicio")
    public String getviewTermsOfService() {
        return "user/termsService";
    }
}
