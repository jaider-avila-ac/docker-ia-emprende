package com.iaemprender.backend.inteligencia.orquestador;

import com.iaemprender.backend.iniciativas.modelo.Iniciativa;
import com.iaemprender.backend.inteligencia.servicio.cliente.ContextoNegocioGateway;
import com.iaemprender.backend.inteligencia.servicio.cliente.DatosAsistente;
import com.iaemprender.backend.plan.modelo.PlanSemanal;
import java.util.List;

public record ContextoIA(
    ContextoNegocioGateway negocio, DatosAsistente datos, List<Iniciativa> iniciativas, PlanSemanal plan) {}
