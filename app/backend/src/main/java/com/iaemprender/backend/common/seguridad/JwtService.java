package com.iaemprender.backend.common.seguridad;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
  private final SecretKey llave;
  private final long minutosExpiracion;

  public JwtService(
      @Value("${app.seguridad.jwt-secret}") String secreto,
      @Value("${app.seguridad.jwt-expiracion-minutos}") long minutosExpiracion) {
    this.llave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
    this.minutosExpiracion = minutosExpiracion;
  }

  public String generarToken(Long usuarioId, String correo) {
    Instant ahora = Instant.now();
    return Jwts.builder()
        .subject(usuarioId.toString())
        .claim("correo", correo)
        .issuedAt(Date.from(ahora))
        .expiration(Date.from(ahora.plusSeconds(minutosExpiracion * 60)))
        .signWith(llave)
        .compact();
  }

  public Long extraerUsuarioId(String token) {
    return Long.valueOf(extraerClaim(token, Claims::getSubject));
  }

  public boolean esValido(String token) {
    try {
      return !extraerClaim(token, Claims::getExpiration).before(new Date());
    } catch (Exception ex) {
      return false;
    }
  }

  private <T> T extraerClaim(String token, Function<Claims, T> resolver) {
    Claims claims = Jwts.parser().verifyWith(llave).build()
        .parseSignedClaims(token).getPayload();
    return resolver.apply(claims);
  }
}
