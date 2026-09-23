package com.iaemprender.backend.evaluacion.modelo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class DificultadConverter implements AttributeConverter<Dificultad, String> {

  @Override
  public String convertToDatabaseColumn(Dificultad attribute) {
    return attribute == null ? null : attribute.getValorDb();
  }

  @Override
  public Dificultad convertToEntityAttribute(String dbData) {
    return dbData == null ? null : Dificultad.desdeValorDb(dbData);
  }
}
