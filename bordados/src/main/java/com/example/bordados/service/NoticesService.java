package com.example.bordados.service;

import java.util.List;

import com.example.bordados.DTOs.NoticesDTO;
import com.example.bordados.model.Notices;

public interface NoticesService {
    NoticesDTO getCurrentNotices();
    NoticesDTO updateNotices(NoticesDTO noticesDTO);
    NoticesDTO createNotices(NoticesDTO noticesDTO);
    void deleteNotices(Long id);
    List<Notices> getAllNotices();
}
