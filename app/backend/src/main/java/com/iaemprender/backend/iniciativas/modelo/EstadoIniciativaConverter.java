package com.iaemprender.backend.iniciativas.modelo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EstadoIniciativaConverter implements AttributeConverter<EstadoIniciativa, String> {
  @Override
  public String convertToDatabaseColumn(EstadoIniciativa attribute) {
    return attribute == null ? null : attribute.getValorDb();
  }

  @Override
  public EstadoIniciativa convertToEntityAttribute(String dbData) {
    return dbData == null ? null : EstadoIniciativa.desdeValorDb(dbData);
  }
}
