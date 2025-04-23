package com.example.bordados.service.ServiceImpl;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import com.example.bordados.DTOs.ProductDTO;
import com.example.bordados.model.Product;
import com.example.bordados.repository.CategoryRepository;
import com.example.bordados.repository.CollectionRepository;
import com.example.bordados.repository.ProductRepository;
import com.example.bordados.repository.SubCategoryRepository;
import com.example.bordados.service.ProductService;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final ImageServiceImpl imageService;
    private final CollectionRepository collectionRepository;

   
    @Override
    public Product createProduct(ProductDTO productDTO) {
        try {

            if (productDTO.getImages() == null || productDTO.getImages().isEmpty()) {
                throw new IllegalArgumentException("Debe proporcionar al menos una imagen");
            }
            if (productDTO.getImages().size() > 6) {
                throw new IllegalArgumentException("No se pueden subir más de 6 imágenes");
            }

            List<String> savedImages = imageService.saveImages(productDTO.getImages());
            Product product = convertToEntity(productDTO);
            product.setImages(savedImages);
            product.setSalesCount(0);
            Product savedProduct = productRepository.save(product);
            log.info("Producto creado: {}", savedProduct.getId());
            return savedProduct;
        } catch (Exception e) {
            log.error("Error al crear producto: {}", e.getMessage());
            throw new RuntimeException("Error al crear producto", e);
        }
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));

        if (productDTO.getImagesToDelete() != null && !productDTO.getImagesToDelete().isEmpty()) {
            if (productDTO.getImagesToDelete().size() >= existingProduct.getImages().size()) {
                throw new IllegalArgumentException("No puedes eliminar todas las imágenes");
            }

            imageService.deleteImages(productDTO.getImagesToDelete());
            existingProduct.getImages().removeAll(productDTO.getImagesToDelete());
        }

        if (productDTO.getNewImages() != null && !productDTO.getNewImages().isEmpty()) {
            List<String> savedImages = imageService.saveImages(productDTO.getNewImages());
            existingProduct.getImages().addAll(savedImages);

            if (existingProduct.getImages().size() > 6) {
                imageService.deleteImages(savedImages);
                throw new IllegalArgumentException("No se pueden tener más de 6 imágenes");
            }
        }

        if (existingProduct.getImages().isEmpty()) {
            throw new IllegalArgumentException("El producto debe tener al menos 1 imagen");
        }

        updateProductFields(existingProduct, productDTO);

        Product updatedProduct = productRepository.save(existingProduct);
        log.info("Producto actualizado: {}", updatedProduct.getId());
        return updatedProduct;
    }

    @Override
    public List<Product> getProductsByCollection(Long collectionId) {
        try {
            return productRepository.findByCollectionId(collectionId);
        } catch (Exception e) {
            log.error("Error al obtener productos por colección ID: {}", collectionId, e);
            throw new RuntimeException("Error al obtener productos de la colección", e);
        }
    }

    private void updateProductFields(Product product, ProductDTO dto) {
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());
        product.setSizes(new HashSet<>(dto.getSizes()));
        product.setColors(new HashSet<>(dto.getColors()));
        product.setDiscount(dto.getDiscount());
        product.setCategory(categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada")));
        product.setSubCategory(dto.getSubCategoryId() != null
                ? subCategoryRepository.findById(dto.getSubCategoryId()).orElse(null)
                : null);
    }

    @Override
    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getProducsByCategory(Long categoryId) {
        return productRepository.findByCategoryIdCategory(categoryId);
    }

    @Override
    public List<Product> getProductBySubCategory(Long subCategoryId) {
        return productRepository.findBySubCategoryIdSubcategory(subCategoryId);
    }

    @Override
    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public List<Product> getDiscountedProducts() {
        return productRepository.findByDiscountGreaterThan(0.0);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
            
            if (product.getImages() != null && !product.getImages().isEmpty()) {
                imageService.deleteImages(product.getImages());
                log.info("Eliminadas {} imágenes físicas del producto ID: {}", product.getImages().size(), id);
            }
            
            productRepository.delete(product);
            log.info("Producto eliminado completamente: {}", id);
        } catch (Exception e) {
            log.error("Error al eliminar producto ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error al eliminar producto: " + e.getMessage(), e);
        }
    }

    @Override
    public double calculateDiscountedPrice(Product product, boolean firstPurchase, boolean isAffiliate) {
        double price = product.getPrice();
        double discount = product.getDiscount();

        if (firstPurchase) {
            discount += 5.0;
        }
        if (isAffiliate) {
            discount += 5.0;
        }

        double finalPrice = price - ((discount / 100) * price);
        log.info("Precio calculado para producto {}: {} con {}% de descuento", product.getId(), finalPrice, discount);
        return finalPrice;
    }

    private Product convertToEntity(ProductDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setQuantity(dto.getQuantity());
        product.setPrice(dto.getPrice());
        product.setDiscount(dto.getDiscount());
        product.setFitType(dto.getFitType());
        product.setSizes(dto.getSizes() != null ? new HashSet<>(dto.getSizes()) : new HashSet<>());
        product.setColors(dto.getColors() != null ? new HashSet<>(dto.getColors()) : new HashSet<>());
        product.setCategory(categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada")));
        if (dto.getSubCategoryId() != null) {
            product.setSubCategory(subCategoryRepository.findById(dto.getSubCategoryId())
                    .orElseThrow(() -> new RuntimeException("Subcategoría no encontrada")));
        }
        if (dto.getCollectionId() != null) {
            product.setCollection(collectionRepository.findById(dto.getCollectionId())
                    .orElseThrow(() -> new RuntimeException("Colección no encontrada")));
        }
        product.setSalesCount(0);
        return product;
    }

    @Override
    public Product getProductById(Long id) {
        try {
            return productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        } catch (Exception e) {
            log.error("Error al obtener producto por ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public ProductDTO convertToDTO(Product product) {
        return ProductDTO.builder()
                .name(product.getName())
                .description(product.getDescription())
                .quantity(product.getQuantity())
                .price(product.getPrice())
                .discount(product.getDiscount())
                .sizes(new HashSet<>(product.getSizes()))
                .colors(new HashSet<>(product.getColors()))
                .fitType(product.getFitType())
                .categoryId(product.getCategory().getIdCategory())
                .subCategoryId(product.getSubCategory() != null ? product.getSubCategory().getIdSubcategory() : null)
                .existingImages(product.getImages())
                .build();
    }

    @Override
    public List<Product> getCustomizableProducts() {
        return productRepository.findAll().stream()
                .filter(product -> product.getCategory().getNameCategory().equalsIgnoreCase("Personalizar"))
                .collect(Collectors.toList());
    }

    public List<Product> getTopSellingProducts() {
        return productRepository.findAll().stream()
                .filter(product -> !"Personalizar".equals(product.getCategory().getNameCategory()))
                .filter(product -> !product.getImages().isEmpty())
                .sorted(Comparator.comparingInt(Product::getSalesCount).reversed())
                .limit(8)
                .collect(Collectors.toList());
    }

    public List<Product> getRandomProducts() {
        List<Product> allProducts = productRepository.findAll().stream()
                .filter(product -> !"Personalizar".equals(product.getCategory().getNameCategory()))
                .filter(product -> !product.getImages().isEmpty())
                .collect(Collectors.toList());
        Collections.shuffle(allProducts);
        return allProducts.stream().limit(8).collect(Collectors.toList());
    }

}
