package com.example.bordados.service.ServiceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.bordados.DTOs.NoticesDTO;
import com.example.bordados.model.Notices;
import com.example.bordados.repository.NoticesRepository;
import com.example.bordados.service.NoticesService;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticesServiceImpl implements NoticesService {
    
    private final NoticesRepository noticesRepository;

    @Override
    @Transactional(readOnly = true)
    public NoticesDTO getCurrentNotices() {
        return noticesRepository.findTopByOrderByIdDesc()
                .map(this::convertToDTO)
                .orElseGet(() -> {
                    NoticesDTO defaultNotices = new NoticesDTO();
                    defaultNotices.setOffers("texto prueba ofertas");
                    defaultNotices.setBannerMain("texto prueba ofertas");
                    defaultNotices.setBannerSecondary("texto prueba ofertas");
                    return defaultNotices;
                });
    }

    @Override
    @Transactional
    public NoticesDTO updateNotices(NoticesDTO noticesDTO) {
        Optional<Notices> existingNotices = noticesRepository.findTopByOrderByIdDesc();
        
        if (existingNotices.isPresent()) {
            Notices notices = existingNotices.get();
            notices.setOffers(noticesDTO.getOffers());
            notices.setBannerMain(noticesDTO.getBannerMain());
            notices.setBannerSecondary(noticesDTO.getBannerSecondary());
            return convertToDTO(noticesRepository.save(notices));
        } else {
            return createNotices(noticesDTO);
        }
    }

    @Override
    @Transactional
    public NoticesDTO createNotices(NoticesDTO noticesDTO) {
        Notices notices = new Notices();
        notices.setOffers(noticesDTO.getOffers());
        notices.setBannerMain(noticesDTO.getBannerMain());
        notices.setBannerSecondary(noticesDTO.getBannerSecondary());
        return convertToDTO(noticesRepository.save(notices));
    }

    @Override
    @Transactional
    public void deleteNotices(Long id) {
        Notices notices = noticesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Textos no encontrados con ID: " + id));
        noticesRepository.delete(notices);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notices> getAllNotices() {
        return noticesRepository.findAll();
    }

    private NoticesDTO convertToDTO(Notices notices) {
        NoticesDTO dto = new NoticesDTO();
        dto.setId(notices.getId());
        dto.setOffers(notices.getOffers());
        dto.setBannerMain(notices.getBannerMain());
        dto.setBannerSecondary(notices.getBannerSecondary());
        return dto;
    }

    private Notices convertToEntity(NoticesDTO noticesDTO) {
        Notices notices = new Notices();
        notices.setOffers(noticesDTO.getOffers());
        notices.setBannerMain(noticesDTO.getBannerMain());
        notices.setBannerSecondary(noticesDTO.getBannerSecondary());
        return notices;
    }
}