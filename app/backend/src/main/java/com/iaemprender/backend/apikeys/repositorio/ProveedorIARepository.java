package com.iaemprender.backend.apikeys.repositorio;

import com.iaemprender.backend.apikeys.modelo.ProveedorIA;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProveedorIARepository extends JpaRepository<ProveedorIA, Integer> {
  Optional<ProveedorIA> findByCodigo(String codigo);
}
