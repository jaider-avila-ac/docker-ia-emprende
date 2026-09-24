package com.iaemprender.backend.plan.controlador;

import com.iaemprender.backend.common.seguridad.UsuarioPrincipal;
import com.iaemprender.backend.plan.dto.AjustePlanRequest;
import com.iaemprender.backend.plan.dto.PlanAccionRequest;
import com.iaemprender.backend.plan.dto.PlanAccionResponse;
import com.iaemprender.backend.plan.dto.PlanSemanalResponse;
import com.iaemprender.backend.plan.modelo.PlanSemanal;
import com.iaemprender.backend.plan.servicio.PlanService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/plan")
public class PlanController {
  private final PlanService planService;

  public PlanController(PlanService planService) {
    this.planService = planService;
  }

  @GetMapping("/actual")
  public PlanSemanalResponse actual(@AuthenticationPrincipal UsuarioPrincipal principal) {
    return respuestaDesde(planService.obtenerOCrearSemanaActual(principal.getId()));
  }

  @PutMapping("/ajustes")
  public PlanSemanalResponse ajustar(
      @AuthenticationPrincipal UsuarioPrincipal principal, @Valid @RequestBody AjustePlanRequest datos) {
    return respuestaDesde(planService.actualizarAjustes(principal.getId(), datos.tiempoDisponible()));
  }

  @GetMapping("/semana/{numero}")
  public PlanSemanalResponse semana(
      @AuthenticationPrincipal UsuarioPrincipal principal,
      @PathVariable int numero,
      @RequestParam(required = false) Integer anio) {
    return respuestaDesde(planService.obtenerSemana(principal.getId(), anioOActual(anio), numero));
  }

  @PostMapping("/semana/{numero}/acciones")
  public ResponseEntity<PlanAccionResponse> agregarAccion(
      @AuthenticationPrincipal UsuarioPrincipal principal,
      @PathVariable int numero,
      @RequestParam(required = false) Integer anio,
      @Valid @RequestBody PlanAccionRequest datos) {
    var accion = planService.agregarAccion(principal.getId(), anioOActual(anio), numero, datos);
    return ResponseEntity.status(HttpStatus.CREATED).body(PlanAccionResponse.desde(accion));
  }

  @PutMapping("/acciones/{id}")
  public PlanAccionResponse actualizarAccion(
      @AuthenticationPrincipal UsuarioPrincipal principal,
      @PathVariable Long id,
      @Valid @RequestBody PlanAccionRequest datos) {
    return PlanAccionResponse.desde(planService.actualizarAccion(principal.getId(), id, datos));
  }

  @DeleteMapping("/acciones/{id}")
  public ResponseEntity<Void> eliminarAccion(@AuthenticationPrincipal UsuarioPrincipal principal, @PathVariable Long id) {
    planService.eliminarAccion(principal.getId(), id);
    return ResponseEntity.noContent().build();
  }

  private int anioOActual(Integer anio) {
    return anio != null ? anio : LocalDate.now().get(WeekFields.ISO.weekBasedYear());
  }

  private PlanSemanalResponse respuestaDesde(PlanSemanal plan) {
    var acciones = planService.listarAcciones(plan.getId()).stream().map(PlanAccionResponse::desde).toList();
    return PlanSemanalResponse.desde(plan, acciones);
  }
}
