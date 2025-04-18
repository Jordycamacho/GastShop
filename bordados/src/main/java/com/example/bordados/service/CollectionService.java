package com.example.bordados.service;

import java.util.List;

import com.example.bordados.DTOs.CollectionDTO;

public interface CollectionService {
    
    List<CollectionDTO> getAllCollections();

    CollectionDTO getCollectionById(Long id);

    CollectionDTO createCollection(CollectionDTO collectionDTO);

    CollectionDTO updateCollection(Long id, CollectionDTO collectionDTO);

    void deleteCollection(Long id);

    List<CollectionDTO> getCollectionsByCategory(Long categoryId);

    void deleteCollectionImage(Long id);
}