package com.iaemprender.backend.apikeys.servicio;

import com.iaemprender.backend.apikeys.dto.ApiKeyEstadoResponse;
import java.util.List;

public interface ApiKeyService {

  ApiKeyEstadoResponse guardar(Long usuarioId, String proveedorCodigo, String claveEnTextoPlano);

  List<ApiKeyEstadoResponse> listarEstados(Long usuarioId);

  void eliminar(Long usuarioId, String proveedorCodigo);

  String obtenerClaveDescifrada(Long usuarioId, String proveedorCodigo);
}
