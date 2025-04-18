package com.example.bordados.service;

import java.util.List;
import com.example.bordados.DTOs.ProductDTO;
import com.example.bordados.model.Product;

public interface ProductService {

    Product createProduct(ProductDTO productDTO);

    Product updateProduct(Long id, ProductDTO productDetails);

    Product getProductById(Long id);
    
    ProductDTO convertToDTO(Product product);  

    List<Product> getAllProduct();

    List<Product> getProducsByCategory(Long categoryId);

    List<Product> getProductsByCollection(Long collectionId);

    List<Product> getProductBySubCategory(Long subCategoryId);

    List<Product> searchProductsByName(String name);

    List<Product> getDiscountedProducts();

    void deleteProduct(Long id);

    List<Product> getCustomizableProducts();

    List<Product> getTopSellingProducts();

    List<Product> getRandomProducts();

    double calculateDiscountedPrice(Product product, boolean firstPurchase, boolean isAffiliate);
}
