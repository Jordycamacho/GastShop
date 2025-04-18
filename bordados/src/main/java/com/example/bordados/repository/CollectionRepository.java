package com.example.bordados.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.bordados.model.Collection;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Long> {
    
    // Usando @Query explícito
    @Query("SELECT c FROM Collection c WHERE c.category.idCategory = :categoryId")
    List<Collection> findByCategoryId(@Param("categoryId") Long categoryId);
    
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Collection c WHERE c.nameCollection = :nameCollection AND c.category.idCategory = :categoryId")
    boolean existsByNameCollectionAndCategoryId(@Param("nameCollection") String nameCollection, @Param("categoryId") Long categoryId);
    
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Collection c WHERE c.nameCollection = :nameCollection AND c.category.idCategory = :categoryId AND c.id <> :id")
    boolean existsByNameCollectionAndCategoryIdAndIdNot(@Param("nameCollection") String nameCollection, @Param("categoryId") Long categoryId, @Param("id") Long id);
}