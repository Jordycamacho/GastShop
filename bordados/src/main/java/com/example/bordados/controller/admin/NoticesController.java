package com.example.bordados.controller.admin;

import java.util.Collections;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.bordados.DTOs.NoticesDTO;
import com.example.bordados.service.NoticesService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/notices")
@RequiredArgsConstructor
public class NoticesController {

    private final NoticesService noticesService;

    @GetMapping
    public String showNoticesManagement(Model model) {
        try {
            NoticesDTO currentNotices = noticesService.getCurrentNotices();
            model.addAttribute("notices", currentNotices);
            model.addAttribute("allNotices", noticesService.getAllNotices());
        } catch (Exception e) {
            model.addAttribute("notices", new NoticesDTO());
            model.addAttribute("allNotices", Collections.emptyList());
        }
        return "admin/notices/management";
    }

    @PostMapping
    public String updateNotices(@Valid @ModelAttribute("notices") NoticesDTO noticesDTO,
                              BindingResult result,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        if (result.hasErrors()) {
            model.addAttribute("allNotices", noticesService.getAllNotices());
            return "admin/notices/management";
        }

        try {
            noticesService.updateNotices(noticesDTO);
            redirectAttributes.addFlashAttribute("success", "Textos actualizados exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar los textos: " + e.getMessage());
        }
        return "redirect:/admin/notices";
    }

    @PostMapping("/create")
    public String createNotices(@Valid @ModelAttribute("notices") NoticesDTO noticesDTO,
                              BindingResult result,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        if (result.hasErrors()) {
            model.addAttribute("allNotices", noticesService.getAllNotices());
            return "admin/notices/management";
        }

        try {
            noticesService.createNotices(noticesDTO);
            redirectAttributes.addFlashAttribute("success", "Nuevos textos creados exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear los textos: " + e.getMessage());
        }
        return "redirect:/admin/notices";
    }

    @GetMapping("/delete/{id}")
    public String deleteNotices(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            noticesService.deleteNotices(id);
            redirectAttributes.addFlashAttribute("success", "Textos eliminados exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar los textos: " + e.getMessage());
        }
        return "redirect:/admin/notices";
    }
}