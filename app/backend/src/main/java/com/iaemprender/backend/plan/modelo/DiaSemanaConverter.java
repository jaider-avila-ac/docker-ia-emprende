package com.iaemprender.backend.plan.modelo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class DiaSemanaConverter implements AttributeConverter<DiaSemana, String> {
  @Override
  public String convertToDatabaseColumn(DiaSemana attribute) {
    return attribute == null ? null : attribute.getValorDb();
  }

  @Override
  public DiaSemana convertToEntityAttribute(String dbData) {
    return dbData == null ? null : DiaSemana.desdeValorDb(dbData);
  }
}
