package com.example.bordados.controller.admin;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.bordados.DTOs.CategorySubCategoryDTO;
import com.example.bordados.DTOs.CollectionDTO;
import com.example.bordados.repository.CategoryRepository;
import com.example.bordados.service.CategoryService;
import com.example.bordados.service.CollectionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/colecciones")
public class CollectionController {

    private final CollectionService collectionService;
    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;

    @ModelAttribute("categoriesWithSub")
    public List<CategorySubCategoryDTO> getCategoriesWithSubCategories() {
        return categoryService.getAllCategoriesWithSubCategories();
    }

    @GetMapping("")
    public String showAllCollections(Model model) {
        model.addAttribute("collections", collectionService.getAllCollections());
        return "admin/collections/showCollections";
    }

    @GetMapping("/crear")
    public String showCreateForm(Model model) {
        model.addAttribute("collection", new CollectionDTO());
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/collections/createCollection";
    }

    @PostMapping("/crear")
    public String createCollection(@ModelAttribute @Valid CollectionDTO collectionDTO, 
                                 BindingResult result, 
                                 Model model,
                                 @RequestParam("imageFile") MultipartFile imageFile) {
        
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            return "admin/collections/createCollection";
        }

        try {
            collectionDTO.setImageFile(imageFile);
            collectionService.createCollection(collectionDTO);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("categories", categoryRepository.findAll());
            return "admin/collections/createCollection";
        }

        return "redirect:/admin/colecciones";
    }

    @GetMapping("/editar/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            CollectionDTO collectionDTO = collectionService.getCollectionById(id);
            model.addAttribute("collection", collectionDTO);
            model.addAttribute("categories", categoryRepository.findAll());
            return "admin/collections/createCollection";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/colecciones";
        }
    }

    @PostMapping("/editar/{id}")
    public String updateCollection(@PathVariable Long id,
                                 @ModelAttribute @Valid CollectionDTO collectionDTO,
                                 BindingResult result,
                                 Model model,
                                 @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            return "admin/collections/createCollection";
        }

        try {
            collectionDTO.setImageFile(imageFile);
            collectionService.updateCollection(id, collectionDTO);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("categories", categoryRepository.findAll());
            return "admin/collections/createCollection";
        }

        return "redirect:/admin/colecciones";
    }

    @GetMapping("/eliminar/{id}")
    public String deleteCollection(@PathVariable Long id, Model model) {
        try {
            collectionService.deleteCollection(id);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/colecciones";
    }

    @GetMapping("/eliminar-imagen/{id}")
    public String deleteCollectionImage(@PathVariable Long id) {
        collectionService.deleteCollectionImage(id);
        return "redirect:/admin/colecciones/editar/" + id;
    }
}