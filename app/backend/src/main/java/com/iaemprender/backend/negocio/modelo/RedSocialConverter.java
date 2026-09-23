package com.iaemprender.backend.negocio.modelo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RedSocialConverter implements AttributeConverter<RedSocial, String> {

  @Override
  public String convertToDatabaseColumn(RedSocial attribute) {
    return attribute == null ? null : attribute.getValorDb();
  }

  @Override
  public RedSocial convertToEntityAttribute(String dbData) {
    return dbData == null ? null : RedSocial.desdeValorDb(dbData);
  }
}
