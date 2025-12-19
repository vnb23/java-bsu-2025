package org.example.guitar_shop.repository;

import org.example.guitar_shop.model.Guitar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuitarRepository extends JpaRepository<Guitar, Long> {
}