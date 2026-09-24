package com.iaemprender.backend.plan.modelo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TiempoDisponibleConverter implements AttributeConverter<TiempoDisponible, String> {
  @Override
  public String convertToDatabaseColumn(TiempoDisponible attribute) {
    return attribute == null ? null : attribute.getValorDb();
  }

  @Override
  public TiempoDisponible convertToEntityAttribute(String dbData) {
    return dbData == null ? null : TiempoDisponible.desdeValorDb(dbData);
  }
}
