package com.iaemprender.backend.negocio.modelo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PilarConverter implements AttributeConverter<Pilar, String> {

  @Override
  public String convertToDatabaseColumn(Pilar attribute) {
    return attribute == null ? null : attribute.getValorDb();
  }

  @Override
  public Pilar convertToEntityAttribute(String dbData) {
    return dbData == null ? null : Pilar.desdeValorDb(dbData);
  }
}
