package com.iaemprender.backend.ofertas.dto;

import com.iaemprender.backend.ofertas.modelo.Oferta;
import java.math.BigDecimal;

public record OfertaResponse(
    Long id, String tipo, String nombre, String categoria, BigDecimal precio, String destacar) {

  public static OfertaResponse desde(Oferta o) {
    return new OfertaResponse(
        o.getId(), o.getTipo().getValorDb(), o.getNombre(), o.getCategoria(), o.getPrecio(), o.getDestacar());
  }
}
