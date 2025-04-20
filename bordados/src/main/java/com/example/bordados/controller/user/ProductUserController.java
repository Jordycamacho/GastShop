package com.example.bordados.controller.user;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.bordados.DTOs.CategorySubCategoryDTO;
import com.example.bordados.DTOs.CustomizedProductDTO;
import com.example.bordados.DTOs.NoticesDTO;
import com.example.bordados.model.PricingConfiguration;
import com.example.bordados.model.Product;
import com.example.bordados.service.CategoryService;
import com.example.bordados.service.CustomizedProductDetailsService;
import com.example.bordados.service.NoticesService;
import com.example.bordados.service.ProductService;
import com.example.bordados.service.ServiceImpl.PricingServiceImpl;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/bordados/producto/")
public class ProductUserController {

    private static final Logger log = LoggerFactory.getLogger(ProductUserController.class);

    @Value("${stripe.key.public}")
    private String stripePublicKey;

    private final ProductService productService;
    private final CategoryService categoryService;
    private final CustomizedProductDetailsService customizationService;
    private final PricingServiceImpl pricingService;
    private final NoticesService noticesService;

    public ProductUserController(ProductService productService,
                               CategoryService categoryService,
                               CustomizedProductDetailsService customizationService,
                               PricingServiceImpl pricingService,
                               NoticesService noticesService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.customizationService = customizationService;
        this.pricingService = pricingService;
        this.noticesService = noticesService;
    }

    @ModelAttribute("categoriesWithSub")
    public List<CategorySubCategoryDTO> getCategoriesWithSubCategories() {
        return categoryService.getAllCategoriesWithSubCategories();
    }

    @ModelAttribute("currentNotices")
    public NoticesDTO getCurrentNotices() {
        return noticesService.getCurrentNotices();
    }

    @GetMapping("/vista/{id}")
    public String viewProduct(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "user/product";
    }

    @GetMapping("/personalizar/{id}")
    public String viewProductCustom(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        PricingConfiguration pricing = pricingService.getPricingConfiguration();
        
        model.addAttribute("stripePublicKey", stripePublicKey);
        model.addAttribute("product", product);
        model.addAttribute("pricing", pricing);
        return "user/ProductCustom";
    }

    @PostMapping("/save")
    public String saveCustomization(
            @ModelAttribute @Valid CustomizedProductDTO dto) {

        log.info("Recibiendo solicitud para personalizar producto con ID: {}", dto.getProductId());

        CustomizedProductDTO savedProduct = customizationService.saveCustomization(dto);

        log.info("Producto personalizado guardado con éxito. ID: {}", savedProduct.getProductId());

        return "redirect:/bordados/orden";
    }

    @GetMapping("/preview/{id}")
    public String previewEmbroidery(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);

        model.addAttribute("product", product);
        return "user/previewEmbroidery";
    }
}
