package com.iaemprender.backend.negocio.servicio.impl;

import com.iaemprender.backend.common.excepcion.ConflictoException;
import com.iaemprender.backend.common.excepcion.RecursoNoEncontradoException;
import com.iaemprender.backend.common.excepcion.SolicitudInvalidaException;
import com.iaemprender.backend.negocio.dto.NegocioRequest;
import com.iaemprender.backend.negocio.modelo.Negocio;
import com.iaemprender.backend.negocio.modelo.Pilar;
import com.iaemprender.backend.negocio.modelo.RedSocial;
import com.iaemprender.backend.negocio.modelo.Rubro;
import com.iaemprender.backend.negocio.repositorio.NegocioRepository;
import com.iaemprender.backend.negocio.servicio.NegocioService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NegocioServiceImpl implements NegocioService {
  private static final int MAXIMO_NEGOCIOS_POR_USUARIO = 3;

  private final NegocioRepository negocioRepository;

  public NegocioServiceImpl(NegocioRepository negocioRepository) {
    this.negocioRepository = negocioRepository;
  }

  @Override
  @Transactional
  public Negocio crear(Long usuarioId, NegocioRequest datos) {
    long total = negocioRepository.countByUsuarioId(usuarioId);
    if (total >= MAXIMO_NEGOCIOS_POR_USUARIO) {
      throw new ConflictoException("Ya tienes " + MAXIMO_NEGOCIOS_POR_USUARIO + " negocios, el máximo permitido.");
    }

    Negocio negocio = new Negocio();
    negocio.setUsuarioId(usuarioId);
    aplicarDatos(negocio, datos);

    negocio.setActivo(total == 0);
    return negocioRepository.save(negocio);
  }

  @Override
  public List<Negocio> listarPropios(Long usuarioId) {
    return negocioRepository.findByUsuarioIdOrderByIdAsc(usuarioId);
  }

  @Override
  @Transactional
  public Negocio actualizar(Long usuarioId, Long negocioId, NegocioRequest datos) {
    Negocio negocio = obtenerPropioOFallar(usuarioId, negocioId);
    aplicarDatos(negocio, datos);
    return negocioRepository.save(negocio);
  }

  @Override
  @Transactional
  public Negocio activar(Long usuarioId, Long negocioId) {
    Negocio negocio = obtenerPropioOFallar(usuarioId, negocioId);
    negocioRepository.desactivarTodosDe(usuarioId);
    negocio.setActivo(true);
    return negocioRepository.save(negocio);
  }

  @Override
  @Transactional
  public void eliminar(Long usuarioId, Long negocioId) {
    negocioRepository.delete(obtenerPropioOFallar(usuarioId, negocioId));
  }

  @Override
  public Negocio obtenerActivo(Long usuarioId) {
    return negocioRepository.findByUsuarioIdAndActivoTrue(usuarioId)
        .orElseThrow(() -> new RecursoNoEncontradoException("Aún no tienes un negocio activo. Crea uno primero."));
  }

  private Negocio obtenerPropioOFallar(Long usuarioId, Long negocioId) {
    return negocioRepository.findByIdAndUsuarioId(negocioId, usuarioId)
        .orElseThrow(() -> new RecursoNoEncontradoException("Negocio no encontrado."));
  }

  private void aplicarDatos(Negocio negocio, NegocioRequest datos) {
    negocio.setNombre(datos.nombre());
    try {
      negocio.setRubro(Rubro.desdeValorDb(datos.rubro()));
    } catch (IllegalArgumentException ex) {
      throw new SolicitudInvalidaException("Rubro inválido: " + datos.rubro());
    }
    negocio.setUbicacion(datos.ubicacion());
    negocio.setVendeEnLinea(datos.vendeEnLinea());
    negocio.setCoberturaEnvio(datos.coberturaEnvio());
    negocio.setDescripcion(datos.descripcion());
    negocio.setPublicoObjetivo(datos.publicoObjetivo());
    negocio.setDiferenciador(datos.diferenciador());
    negocio.setTagline(datos.tagline());
    negocio.setTono(datos.tono());
    negocio.setColores(datos.colores());
    negocio.setReferenciasEstilo(datos.referenciasEstilo());
    negocio.setCostosFijosMensuales(datos.costosFijosMensuales());
    negocio.setRedesActivas(parsearValores(datos.redesActivas(), RedSocial::desdeValorDb));
    negocio.setPilares(parsearValores(datos.pilares(), Pilar::desdeValorDb));
  }

  private <T> Set<T> parsearValores(List<String> valores, Function<String, T> desdeValorDb) {
    if (valores == null) {
      return new HashSet<>();
    }
    Set<T> resultado = new HashSet<>();
    for (String valor : valores) {
      try {
        resultado.add(desdeValorDb.apply(valor));
      } catch (IllegalArgumentException ex) {
      }
    }
    return resultado;
  }
}
