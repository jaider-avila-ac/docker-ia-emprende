package com.iaemprender.backend.ofertas.modelo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TipoOfertaConverter implements AttributeConverter<TipoOferta, String> {

  @Override
  public String convertToDatabaseColumn(TipoOferta attribute) {
    return attribute == null ? null : attribute.getValorDb();
  }

  @Override
  public TipoOferta convertToEntityAttribute(String dbData) {
    return dbData == null ? null : TipoOferta.desdeValorDb(dbData);
  }
}
