package com.iaemprender.backend.common.seguridad;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CifradoService {

  private static final int TAMANO_IV_BYTES = 12;
  private static final int TAMANO_TAG_BITS = 128;
  private static final String TRANSFORMACION = "AES/GCM/NoPadding";

  private final SecretKeySpec llave;
  private final SecureRandom aleatorio = new SecureRandom();

  public CifradoService(@Value("${app.cifrado.clave-secreta}") String secreto) throws NoSuchAlgorithmException {

    byte[] llaveDerivada = MessageDigest.getInstance("SHA-256").digest(secreto.getBytes(StandardCharsets.UTF_8));
    this.llave = new SecretKeySpec(llaveDerivada, "AES");
  }

  public record Cifrado(byte[] datos, byte[] iv) {}

  public Cifrado cifrar(String textoPlano) {
    try {
      byte[] iv = new byte[TAMANO_IV_BYTES];
      aleatorio.nextBytes(iv);
      Cipher cipher = Cipher.getInstance(TRANSFORMACION);
      cipher.init(Cipher.ENCRYPT_MODE, llave, new GCMParameterSpec(TAMANO_TAG_BITS, iv));
      byte[] datos = cipher.doFinal(textoPlano.getBytes(StandardCharsets.UTF_8));
      return new Cifrado(datos, iv);
    } catch (Exception ex) {
      throw new IllegalStateException("No se pudo cifrar el valor.", ex);
    }
  }

  public String descifrar(byte[] datos, byte[] iv) {
    try {
      Cipher cipher = Cipher.getInstance(TRANSFORMACION);
      cipher.init(Cipher.DECRYPT_MODE, llave, new GCMParameterSpec(TAMANO_TAG_BITS, iv));
      return new String(cipher.doFinal(datos), StandardCharsets.UTF_8);
    } catch (Exception ex) {
      throw new IllegalStateException("No se pudo descifrar el valor.", ex);
    }
  }
}
