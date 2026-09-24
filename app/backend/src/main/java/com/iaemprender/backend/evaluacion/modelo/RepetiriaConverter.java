package com.iaemprender.backend.evaluacion.modelo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RepetiriaConverter implements AttributeConverter<Repetiria, String> {
  @Override
  public String convertToDatabaseColumn(Repetiria attribute) {
    return attribute == null ? null : attribute.getValorDb();
  }

  @Override
  public Repetiria convertToEntityAttribute(String dbData) {
    return dbData == null ? null : Repetiria.desdeValorDb(dbData);
  }
}
