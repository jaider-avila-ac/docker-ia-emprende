package com.iaemprender.backend.inteligencia.modelo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TipoFodaConverter implements AttributeConverter<TipoFoda, String> {
  @Override
  public String convertToDatabaseColumn(TipoFoda attribute) {
    return attribute == null ? null : attribute.getValorDb();
  }

  @Override
  public TipoFoda convertToEntityAttribute(String dbData) {
    return dbData == null ? null : TipoFoda.desdeValorDb(dbData);
  }
}
