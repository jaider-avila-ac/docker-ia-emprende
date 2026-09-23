package com.iaemprender.backend.negocio.modelo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RubroConverter implements AttributeConverter<Rubro, String> {

  @Override
  public String convertToDatabaseColumn(Rubro attribute) {
    return attribute == null ? null : attribute.getValorDb();
  }

  @Override
  public Rubro convertToEntityAttribute(String dbData) {
    return dbData == null ? null : Rubro.desdeValorDb(dbData);
  }
}
