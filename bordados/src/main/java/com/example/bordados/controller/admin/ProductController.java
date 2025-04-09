package com.example.bordados.controller.admin;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.bordados.DTOs.CategorySubCategoryDTO;
import com.example.bordados.DTOs.ProductDTO;
import com.example.bordados.model.Product;
import com.example.bordados.service.ServiceImpl.CategoryServiceImpl;
import com.example.bordados.service.ServiceImpl.ProductServiceImpl;
import com.example.bordados.service.ServiceImpl.SubCategoryServiceImpl;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/admin/productos")
@Tag(name = "ProductController", description = "Controlador para gestionar los productos")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);
    private final ProductServiceImpl productService;
    private final CategoryServiceImpl categoryService;
    private final SubCategoryServiceImpl subCategoryService;

    public ProductController(ProductServiceImpl productService, CategoryServiceImpl categoryService,
            SubCategoryServiceImpl subCategoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.subCategoryService = subCategoryService;
    }

    @ModelAttribute("categoriesWithSub")
    public List<CategorySubCategoryDTO> getCategoriesWithSubCategories() {
        return categoryService.getAllCategoriesWithSubCategories();
    }

    @GetMapping
    public String showProducts(Model model) {
        try {
            List<Product> products = productService.getAllProduct();
            model.addAttribute("products", products);
            log.info("Mostrando todos los productos");
        } catch (Exception e) {
            log.error("Error al mostrar los productos", e);
            model.addAttribute("error", "No se pudo cargar la lista de productos.");
        }
        return "admin/product/showProduct";
    }

    @GetMapping("/crear")
    public String showCreateProductForm(Model model) {
        model.addAttribute("productDTO", new ProductDTO());
        model.addAttribute("categories", categoryService.getAllCategories()); // Cargar categorías
        model.addAttribute("subCategories", subCategoryService.getAllSubCategories()); // Cargar subcategorías
        return "admin/product/createProduct";
    }

    @PostMapping("/crear")
    public String createProduct(@ModelAttribute("productDTO") @Valid ProductDTO productDTO,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (productDTO.getImages().size() > 6) {
            result.rejectValue("images", "max.images", "Máximo 6 imágenes permitidas");
        }

        try {
            productService.createProduct(productDTO);
            redirectAttributes.addFlashAttribute("success", "Producto creado exitosamente");
            log.info("Producto creado con éxito: {}", productDTO.getName());
        } catch (Exception e) {
            log.error("Error al crear producto", e);
            redirectAttributes.addFlashAttribute("error", "Error al crear el producto");
        }

        return "redirect:/admin/productos";
    }

    @GetMapping("/editar/{id}")
    public String showEditProductForm(@PathVariable Long id, Model model) {
        try {
            Product product = productService.getProductById(id);
            ProductDTO productDTO = productService.convertToDTO(product);
            productDTO.setExistingImages(product.getImages());
            model.addAttribute("productDTO", productDTO);
            model.addAttribute("id", id);
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("subCategories", subCategoryService.getAllSubCategories());
            log.info("Mostrando formulario de edición para el producto con ID: {}", id);
        } catch (Exception e) {
            log.error("Error al cargar el producto para editar", e);
            model.addAttribute("error", "No se pudo cargar el producto");
            return "admin/product/showProduct";
        }

        return "admin/product/editProduct";
    }

    @PostMapping("/editar/{id}")
    public String updateProduct(@PathVariable Long id,
            @ModelAttribute("productDTO") @Valid ProductDTO productDTO,
            BindingResult result,
            @RequestParam(value = "imagesToDelete", required = false) List<String> imagesToDelete,
            @RequestParam(value = "newImages", required = false) List<MultipartFile> newImages,
            RedirectAttributes redirectAttributes,
            Model model) {

        try {
            productDTO.setImagesToDelete(imagesToDelete);
            productDTO.setNewImages(newImages);

            Product currentProduct = productService.getProductById(id);

            int currentImages = currentProduct.getImages().size();
            int imagesToDeleteCount = imagesToDelete != null ? imagesToDelete.size() : 0;
            int remainingImages = currentImages - imagesToDeleteCount;

            int newImagesCount = newImages != null ? newImages.size() : 0;
            int totalImages = remainingImages + newImagesCount;

            if (totalImages > 6) {
                result.rejectValue("newImages", "max.total.images",
                        "Máximo 6 imágenes permitidas. Total actual: " + totalImages);
                model.addAttribute("categories", categoryService.getAllCategories());
                model.addAttribute("subCategories", subCategoryService.getAllSubCategories());
                return "admin/product/editProduct";
            }

            if (totalImages < 1) {
                result.rejectValue("newImages", "min.images",
                        "Debe tener al menos 1 imagen");
                model.addAttribute("categories", categoryService.getAllCategories());
                model.addAttribute("subCategories", subCategoryService.getAllSubCategories());
                return "admin/product/editProduct";
            }

            productService.updateProduct(id, productDTO);
            redirectAttributes.addFlashAttribute("success", "Producto actualizado exitosamente");
            log.info("Producto con ID {} actualizado con éxito", id);
        } catch (Exception e) {
            log.error("Error al actualizar producto", e);
            redirectAttributes.addFlashAttribute("error",
                    "Error al actualizar el producto: " + e.getMessage());
        }

        return "redirect:/admin/productos";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("success", "Producto eliminado exitosamente");
            log.info("Producto con ID {} eliminado con éxito", id);
        } catch (Exception e) {
            log.error("Error al eliminar producto", e);
            String errorMessage = e.getMessage().contains("eliminar") ? e.getMessage() : "Error al eliminar el producto";
            redirectAttributes.addFlashAttribute("error", errorMessage);
            redirectAttributes.addFlashAttribute("errorDetail", "ID del producto: " + id);
        }

        return "redirect:/admin/productos";
    }

}
