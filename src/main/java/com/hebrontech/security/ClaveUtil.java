package com.hebrontech.security;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class ClaveUtil {

    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final String PREFIJO = "pbkdf2-sha256";
    private static final int ITERACIONES = 600000;
    private static final int SALT_BYTES = 16;
    private static final int HASH_BITS = 256;
    private static final SecureRandom RANDOM = new SecureRandom();

    private ClaveUtil() {
    }

    public static String hash(String password) {
        if (password == null) {
            throw new IllegalArgumentException("La clave no puede ser nula");
        }

        byte[] salt = new byte[SALT_BYTES];
        RANDOM.nextBytes(salt);
        byte[] hash = derivar(password, salt);

        try {
            return PREFIJO + "$" + ITERACIONES + "$"
                    + Base64.getEncoder().encodeToString(salt) + "$"
                    + Base64.getEncoder().encodeToString(hash);
        } finally {
            Arrays.fill(salt, (byte) 0);
            Arrays.fill(hash, (byte) 0);
        }
    }

    public static boolean verificar(String password, String almacenado) {
        if (password == null || !esHash(almacenado)) {
            return false;
        }

        String[] partes = almacenado.split("\\$", -1);
        if (partes.length != 4 || !PREFIJO.equals(partes[0])) {
            return false;
        }

        byte[] salt = null;
        byte[] hashEsperado = null;
        byte[] hashCalculado = null;

        try {
            int iteraciones = Integer.parseInt(partes[1]);
            if (iteraciones != ITERACIONES) {
                return false;
            }

            salt = Base64.getDecoder().decode(partes[2]);
            hashEsperado = Base64.getDecoder().decode(partes[3]);
            if (salt.length != SALT_BYTES
                    || hashEsperado.length != HASH_BITS / 8) {
                return false;
            }

            hashCalculado = derivar(password, salt);
            return MessageDigest.isEqual(hashCalculado, hashEsperado);
        } catch (IllegalArgumentException e) {
            return false;
        } finally {
            if (salt != null) {
                Arrays.fill(salt, (byte) 0);
            }
            if (hashEsperado != null) {
                Arrays.fill(hashEsperado, (byte) 0);
            }
            if (hashCalculado != null) {
                Arrays.fill(hashCalculado, (byte) 0);
            }
        }
    }

    public static boolean esHash(String almacenado) {
        return almacenado != null
                && almacenado.startsWith(PREFIJO + "$");
    }

    private static byte[] derivar(String password, byte[] salt) {
        char[] caracteres = password.toCharArray();
        PBEKeySpec spec = null;

        try {
            spec = new PBEKeySpec(
                    caracteres, salt, ITERACIONES, HASH_BITS);
            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance(ALGORITMO);
            return factory.generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(
                    "No fue posible procesar la clave", e);
        } finally {
            if (spec != null) {
                spec.clearPassword();
            }
            Arrays.fill(caracteres, '\0');
        }
    }
}
