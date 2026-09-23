package com.iaemprender.backend.inteligencia.servicio.cliente;

import java.util.List;

public record FodaContenido(
    List<String> fortalezas, List<String> oportunidades, List<String> debilidades, List<String> amenazas) {}
