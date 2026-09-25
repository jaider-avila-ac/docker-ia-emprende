package com.iaemprender.backend.inteligencia.dto;

import java.util.List;

public record AnalisisResponse(String resumen, List<String> queFunciono, List<String> queCambiar, String siguientePaso) {}
