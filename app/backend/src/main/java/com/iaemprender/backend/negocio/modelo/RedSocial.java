package com.iaemprender.backend.negocio.modelo;

public enum RedSocial {
  INSTAGRAM("Instagram"),
  FACEBOOK("Facebook"),
  TIKTOK("TikTok"),
  WHATSAPP_BUSINESS("WhatsApp Business");

  private final String valorDb;

  RedSocial(String valorDb) {
    this.valorDb = valorDb;
  }

  public String getValorDb() {
    return valorDb;
  }

  public static RedSocial desdeValorDb(String valor) {
    for (RedSocial r : values()) {
      if (r.valorDb.equals(valor)) {
        return r;
      }
    }
    throw new IllegalArgumentException("Red social no reconocida: " + valor);
  }
}
