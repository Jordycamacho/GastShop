package com.example.bordados.service.ServiceImpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.bordados.DTOs.CollectionDTO;
import com.example.bordados.model.Collection;
import com.example.bordados.repository.CategoryRepository;
import com.example.bordados.repository.CollectionRepository;
import com.example.bordados.service.CollectionService;
import com.example.bordados.service.ImageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CollectionServiceImpl implements CollectionService {

    private final CollectionRepository collectionRepository;
    private final CategoryRepository categoryRepository;
    private final ImageService imageService;

    @Override
    public List<CollectionDTO> getAllCollections() {
        try {
            return collectionRepository.findAll().stream()
                    .map(collection -> new CollectionDTO(
                            collection.getId(),
                            collection.getNameCollection(),
                            collection.getImageCollection(),
                            collection.getCategory().getIdCategory(),
                            collection.getCategory().getNameCategory(), null))
                    .toList();
        } catch (Exception e) {
            log.error("Error al obtener las colecciones", e);
            throw new RuntimeException("Error al obtener las colecciones", e);
        }
    }

    @Override
    public CollectionDTO getCollectionById(Long id) {
        log.info("Obteniendo colección por ID: {}", id);
        return collectionRepository.findById(id)
                .map(collection -> new CollectionDTO(
                        collection.getId(),
                        collection.getNameCollection(),
                        collection.getImageCollection(),
                        collection.getCategory().getIdCategory(),
                        collection.getCategory().getNameCategory(), null))
                .orElseThrow(() -> new RuntimeException("Colección no encontrada"));
    }

    @Override
    public CollectionDTO createCollection(CollectionDTO collectionDTO) {
        log.info("Creando una nueva colección: {}", collectionDTO.getNameCollection());
        try {
            com.example.bordados.model.Category category = categoryRepository.findById(collectionDTO.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

            Collection collection = new Collection();
            collection.setNameCollection(collectionDTO.getNameCollection());
            collection.setCategory(category);

            // Manejar la imagen si se proporciona
            if (collectionDTO.getImageFile() != null && !collectionDTO.getImageFile().isEmpty()) {
                String imageName = imageService.saveImageNormal(collectionDTO.getImageFile());
                collection.setImageCollection(imageName);
            } else {
                collection.setImageCollection(imageService.getDefaultImage());
            }

            Collection saved = collectionRepository.save(collection);
            return convertToDTO(saved);
        } catch (Exception e) {
            log.error("Error al crear la colección", e);
            throw new RuntimeException("Error al crear la colección: " + e.getMessage(), e);
        }
    }

    @Override
    public CollectionDTO updateCollection(Long id, CollectionDTO collectionDTO) {
        try {
            Collection collection = collectionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Colección no encontrada"));

            if (collectionRepository.existsByNameCollectionAndCategoryIdAndIdNot(
                    collectionDTO.getNameCollection(),
                    collectionDTO.getCategoryId(),
                    id)) {
                throw new IllegalArgumentException("Ya existe una colección con ese nombre en esta categoría");
            }

            com.example.bordados.model.Category category = categoryRepository.findById(collectionDTO.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

            collection.setNameCollection(collectionDTO.getNameCollection());
            collection.setCategory(category);

            // Manejar la imagen si se proporciona una nueva
            if (collectionDTO.getImageFile() != null && !collectionDTO.getImageFile().isEmpty()) {
                // Eliminar imagen anterior si existe y no es la default
                if (collection.getImageCollection() != null && 
                    !collection.getImageCollection().equals(imageService.getDefaultImage())) {
                    imageService.deleteImageNormal(collection.getImageCollection());
                }
                // Guardar nueva imagen
                String imageName = imageService.saveImageNormal(collectionDTO.getImageFile());
                collection.setImageCollection(imageName);
            }
            
            Collection updated = collectionRepository.save(collection);
            return convertToDTO(updated);
        } catch (Exception e) {
            log.error("Error al actualizar la colección", e);
            throw new RuntimeException("Error al actualizar la colección: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteCollection(Long id) {
        log.warn("Eliminando colección con ID: {}", id);
        try {
            Collection collection = collectionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Colección no encontrada"));

            // Verificar si hay productos asociados
            if (!collection.getProducts().isEmpty()) {
                throw new IllegalStateException("No se puede eliminar la colección porque tiene productos asociados");
            }

            // Eliminar la imagen asociada si no es la default
            if (collection.getImageCollection() != null &&
                    !collection.getImageCollection().equals(imageService.getDefaultImage())) {
                imageService.deleteImageNormal(collection.getImageCollection());
            }

            collectionRepository.delete(collection);
        } catch (Exception e) {
            log.error("Error al eliminar la colección", e);
            throw new RuntimeException("Error al eliminar la colección: " + e.getMessage(), e);
        }
    }

    @Override
    public List<CollectionDTO> getCollectionsByCategory(Long categoryId) {
        log.info("Obteniendo colecciones por categoría ID: {}", categoryId);
        try {
            return collectionRepository.findByCategoryId(categoryId).stream()
                    .map(this::convertToDTO)
                    .toList();
        } catch (Exception e) {
            log.error("Error al obtener colecciones por categoría", e);
            throw new RuntimeException("Error al obtener colecciones por categoría", e);
        }
    }

    private CollectionDTO convertToDTO(Collection collection) {
        return new CollectionDTO(
                collection.getId(),
                collection.getNameCollection(),
                collection.getImageCollection(),
                collection.getCategory().getIdCategory(),
                collection.getCategory().getNameCategory(), null);
    }

    @Override
    public void deleteCollectionImage(Long id) {
        log.info("Eliminando imagen de colección con ID: {}", id);
        try {
            Collection collection = collectionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Colección no encontrada"));

            if (collection.getImageCollection() != null &&
                    !collection.getImageCollection().equals(imageService.getDefaultImage())) {
                imageService.deleteImageNormal(collection.getImageCollection());
                collection.setImageCollection(imageService.getDefaultImage());
                collectionRepository.save(collection);
            }
        } catch (Exception e) {
            log.error("Error al eliminar la imagen de la colección", e);
            throw new RuntimeException("Error al eliminar la imagen de la colección", e);
        }
    }
}