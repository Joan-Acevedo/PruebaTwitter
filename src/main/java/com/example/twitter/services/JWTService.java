package com.example.twitter.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class JWTService {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public JWTService(@Value("${PRIVATE_JWT}") String privateJwtPem,
            @Value("${PUBLIC_JWT}") String publicJwtPem) {
        this.privateKey = loadPrivateKey(privateJwtPem);
        this.publicKey = loadPublicKey(publicJwtPem);
    }

    // 🔹 Firma un string usando RS256
    public String sign(String data) {
        return Jwts.builder()
                .claim("userID", data)
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    // 🔹 Verifica la firma de un token
    public boolean verify(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token);

            // Si no lanza excepción, la firma es válida
            return true;
        } catch (Exception e) {
            System.out.println("❌ Error al verificar la firma del token: " + e.getMessage());
            return false;
        }
    }

    private PrivateKey loadPrivateKey(String pem) {
        try {
            // Decodificar y convertir a PrivateKey
            byte[] encoded = Base64.getDecoder().decode(pem);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("Error al cargar la clave privada", e);
        }
    }

    private PublicKey loadPublicKey(String pem) {
        try {
            // Decodificar y convertir a PublicKey
            byte[] encoded = Base64.getDecoder().decode(pem);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("Error al cargar la clave pública", e);
        }
    }
}