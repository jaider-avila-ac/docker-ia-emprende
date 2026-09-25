package com.iaemprender.backend.negocio.dto;

import com.iaemprender.backend.negocio.modelo.Negocio;
import com.iaemprender.backend.negocio.modelo.Pilar;
import com.iaemprender.backend.negocio.modelo.RedSocial;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

public record NegocioResponse(
    Long id,
    String nombre,
    String rubro,
    String ubicacion,
    boolean vendeEnLinea,
    String coberturaEnvio,
    String descripcion,
    String publicoObjetivo,
    String diferenciador,
    String tono,
    BigDecimal costosFijosMensuales,
    boolean activo,
    List<String> redesActivas,
    List<String> pilares) {
  public static NegocioResponse desde(Negocio n) {
    return new NegocioResponse(
        n.getId(),
        n.getNombre(),
        n.getRubro().getValorDb(),
        n.getUbicacion(),
        n.isVendeEnLinea(),
        n.getCoberturaEnvio(),
        n.getDescripcion(),
        n.getPublicoObjetivo(),
        n.getDiferenciador(),
        n.getTono(),
        n.getCostosFijosMensuales(),
        n.isActivo(),
        n.getRedesActivas().stream()
            .map(RedSocial::getValorDb)
            .sorted(Comparator.naturalOrder())
            .toList(),
        n.getPilares().stream()
            .map(Pilar::getValorDb)
            .sorted(Comparator.naturalOrder())
            .toList());
  }
}
