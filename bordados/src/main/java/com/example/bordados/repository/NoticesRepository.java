package com.example.bordados.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.bordados.model.Notices;

@Repository
public interface NoticesRepository extends JpaRepository<Notices, Long>{
    Optional<Notices> findTopByOrderByIdDesc();
}
