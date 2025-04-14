package com.example.hebergement.repositories;

import com.example.hebergement.entities.Hebergement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import feign.Param;
import java.util.List;

@Repository
public interface HebergementRepository extends JpaRepository<Hebergement, Long> {
    List<Hebergement> findByVille(String ville);
}
