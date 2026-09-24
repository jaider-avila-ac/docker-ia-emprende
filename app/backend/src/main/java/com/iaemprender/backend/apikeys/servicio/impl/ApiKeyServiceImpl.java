package com.iaemprender.backend.apikeys.servicio.impl;

import com.iaemprender.backend.apikeys.dto.ApiKeyEstadoResponse;
import com.iaemprender.backend.apikeys.modelo.EstadoApiKey;
import com.iaemprender.backend.apikeys.modelo.ProveedorIA;
import com.iaemprender.backend.apikeys.modelo.UsuarioApiKey;
import com.iaemprender.backend.apikeys.repositorio.ProveedorIARepository;
import com.iaemprender.backend.apikeys.repositorio.UsuarioApiKeyRepository;
import com.iaemprender.backend.apikeys.servicio.ApiKeyService;
import com.iaemprender.backend.apikeys.servicio.cliente.ProveedorIaCliente;
import com.iaemprender.backend.apikeys.servicio.cliente.VerificacionNoDisponibleException;
import com.iaemprender.backend.common.excepcion.RecursoNoEncontradoException;
import com.iaemprender.backend.common.excepcion.SolicitudInvalidaException;
import com.iaemprender.backend.common.seguridad.CifradoService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApiKeyServiceImpl implements ApiKeyService {
  private final UsuarioApiKeyRepository usuarioApiKeyRepository;
  private final ProveedorIARepository proveedorIARepository;
  private final CifradoService cifradoService;
  private final Map<String, ProveedorIaCliente> clientesPorCodigo;

  public ApiKeyServiceImpl(
      UsuarioApiKeyRepository usuarioApiKeyRepository,
      ProveedorIARepository proveedorIARepository,
      CifradoService cifradoService,
      List<ProveedorIaCliente> clientes) {
    this.usuarioApiKeyRepository = usuarioApiKeyRepository;
    this.proveedorIARepository = proveedorIARepository;
    this.cifradoService = cifradoService;
    this.clientesPorCodigo = clientes.stream().collect(Collectors.toMap(ProveedorIaCliente::getCodigo, Function.identity()));
  }

  @Override
  @Transactional
  public ApiKeyEstadoResponse guardar(Long usuarioId, String proveedorCodigo, String claveEnTextoPlano) {
    ProveedorIA proveedor = proveedorIARepository.findByCodigo(proveedorCodigo)
        .orElseThrow(() -> new SolicitudInvalidaException("Proveedor de IA no soportado: " + proveedorCodigo));

    boolean valida = verificarConProveedorOAsumirValida(proveedorCodigo, claveEnTextoPlano);
    if (!valida) {
      throw new SolicitudInvalidaException(
          "La clave no parece válida para " + proveedor.getNombre() + ". Revisa que la copiaste completa.");
    }

    CifradoService.Cifrado cifrado = cifradoService.cifrar(claveEnTextoPlano);

    UsuarioApiKey entidad = usuarioApiKeyRepository
        .findByUsuarioIdAndProveedor_Codigo(usuarioId, proveedorCodigo)
        .orElseGet(UsuarioApiKey::new);
    boolean esNueva = entidad.getId() == null;

    entidad.setUsuarioId(usuarioId);
    entidad.setProveedor(proveedor);
    entidad.setClaveCifrada(cifrado.datos());
    entidad.setIv(cifrado.iv());
    entidad.setClaveUltimos4(ultimosCaracteres(claveEnTextoPlano, 4));
    entidad.setEstado(EstadoApiKey.ACTIVA);
    entidad.setUltimaVerificacion(LocalDateTime.now());
    if (esNueva) {
      entidad.setTokensUsadosPeriodo(0);
      entidad.setPeriodoInicio(LocalDate.now());
    }

    return ApiKeyEstadoResponse.desde(usuarioApiKeyRepository.save(entidad));
  }

  @Override
  @Transactional(readOnly = true)

  public List<ApiKeyEstadoResponse> listarEstados(Long usuarioId) {
    return usuarioApiKeyRepository.findByUsuarioId(usuarioId).stream().map(ApiKeyEstadoResponse::desde).toList();
  }

  @Override
  @Transactional
  public void eliminar(Long usuarioId, String proveedorCodigo) {
    usuarioApiKeyRepository.findByUsuarioIdAndProveedor_Codigo(usuarioId, proveedorCodigo)
        .ifPresent(usuarioApiKeyRepository::delete);
  }

  @Override
  public String obtenerClaveDescifrada(Long usuarioId, String proveedorCodigo) {
    UsuarioApiKey entidad = usuarioApiKeyRepository
        .findByUsuarioIdAndProveedor_Codigo(usuarioId, proveedorCodigo)
        .orElseThrow(() -> new RecursoNoEncontradoException("No tienes una clave guardada para " + proveedorCodigo + "."));
    return cifradoService.descifrar(entidad.getClaveCifrada(), entidad.getIv());
  }

  private boolean verificarConProveedorOAsumirValida(String proveedorCodigo, String clave) {
    ProveedorIaCliente cliente = clientesPorCodigo.get(proveedorCodigo);
    if (cliente == null) {
      return true;
    }
    try {
      return cliente.verificarClave(clave);
    } catch (VerificacionNoDisponibleException ex) {
      return true;
    }
  }

  private String ultimosCaracteres(String texto, int n) {
    return texto.length() <= n ? texto : texto.substring(texto.length() - n);
  }
}
