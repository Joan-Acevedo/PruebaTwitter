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

    /**
     * Constructor for JWTService.
     * 
     * Initializes the JWTService by loading the private and public keys used for
     * JWT
     * operations from PEM-encoded strings provided through environment variables.
     * 
     * @param privateJwtPem The PEM-encoded private key string injected from the
     *                      environment variable PRIVATE_JWT
     * @param publicJwtPem  The PEM-encoded public key string injected from the
     *                      environment variable PUBLIC_JWT
     */
    public JWTService(@Value("${PRIVATE_JWT}") String privateJwtPem,
            @Value("${PUBLIC_JWT}") String publicJwtPem) {
        this.privateKey = loadPrivateKey(privateJwtPem);
        this.publicKey = loadPublicKey(publicJwtPem);
    }

    // 🔹 Firma un string usando RS256
    /**
     * Signs a JWT (JSON Web Token) with the specified user information.
     * This method creates a token containing user identifiers and signs it
     * with a private key using the RS256 algorithm.
     *
     * @param userID   the unique identifier of the user
     * @param username the username of the user
     * @return a signed JWT string that can be used for authentication
     */
    public String sign(String userID, String username) {
        return Jwts.builder()
                .claim("userID", userID)
                .claim("username", username)
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    /**
     * Verifies the signature and validity of a JWT token.
     *
     * @param token The JWT token to verify
     * @return true if the token has a valid signature and structure, false
     *         otherwise
     * 
     *         This method attempts to parse the provided JWT token using the
     *         configured
     *         public key. If the parsing completes without exceptions, the token is
     *         considered valid. Any exceptions during token verification are
     *         caught,
     *         logged to the console, and result in returning false.
     */
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

    /**
     * Loads a private key from a PEM-encoded string.
     * 
     * This method converts a Base64 encoded private key in PKCS#8 format into a
     * PrivateKey object
     * that can be used for cryptographic operations.
     * 
     * @param pem The Base64 encoded private key string (PEM format without headers
     *            and footers)
     * @return The corresponding PrivateKey object
     * @throws RuntimeException If the key cannot be loaded or parsed correctly
     */
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

    /**
     * Loads a public key from its Base64 encoded PEM format string.
     * 
     * This method takes a Base64 encoded string representation of a public key,
     * decodes it, and converts it into a {@link PublicKey} object using RSA
     * algorithm.
     * 
     * @param pem A Base64 encoded string representing a public key in PEM format
     *            (without the BEGIN/END headers and newlines)
     * @return The converted {@link PublicKey} object
     * @throws RuntimeException If there's an error during the key loading process,
     *                          such as invalid encoding or unsupported key format
     */
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