package com.iaemprender.backend.apikeys.modelo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EstadoApiKeyConverter implements AttributeConverter<EstadoApiKey, String> {

  @Override
  public String convertToDatabaseColumn(EstadoApiKey attribute) {
    return attribute == null ? null : attribute.getValorDb();
  }

  @Override
  public EstadoApiKey convertToEntityAttribute(String dbData) {
    return dbData == null ? null : EstadoApiKey.desdeValorDb(dbData);
  }
}
