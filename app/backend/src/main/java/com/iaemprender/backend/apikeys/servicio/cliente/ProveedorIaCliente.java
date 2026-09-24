package com.iaemprender.backend.apikeys.servicio.cliente;

public interface ProveedorIaCliente {
  String getCodigo();

  boolean verificarClave(String claveEnTextoPlano);
}
