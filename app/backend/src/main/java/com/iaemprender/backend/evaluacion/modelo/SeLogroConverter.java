package com.iaemprender.backend.evaluacion.modelo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class SeLogroConverter implements AttributeConverter<SeLogro, String> {
  @Override
  public String convertToDatabaseColumn(SeLogro attribute) {
    return attribute == null ? null : attribute.getValorDb();
  }

  @Override
  public SeLogro convertToEntityAttribute(String dbData) {
    return dbData == null ? null : SeLogro.desdeValorDb(dbData);
  }
}
