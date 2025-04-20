package com.example.bordados.controller.user;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.bordados.DTOs.CategorySubCategoryDTO;
import com.example.bordados.DTOs.NoticesDTO;
import com.example.bordados.service.CategoryService;
import com.example.bordados.service.NoticesService;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;


@Controller
@AllArgsConstructor
@RequestMapping("/bordados/contacto")
public class ContactController {
    
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
    public String showContact(Model model) {
        model.addAttribute("mensajeEnviado", false);
        return "user/contact";
    }
    
}
