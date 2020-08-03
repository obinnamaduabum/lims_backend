/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hertfordshire.access.config.utils;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import java.io.IOException;
import java.io.Reader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class PemUtils {

    private static byte[] parsePEMFile(Reader pemFile) throws IOException {
        PemReader reader = new PemReader(pemFile);
        PemObject pemObject = reader.readPemObject();
        return pemObject.getContent();
    }

    public static PublicKey getPublicKey(Reader pemFile, String algorithm) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        KeyFactory kf = KeyFactory.getInstance(algorithm);
        EncodedKeySpec keySpec = new X509EncodedKeySpec(parsePEMFile(pemFile));
        return kf.generatePublic(keySpec);
    }

    public static PrivateKey getPrivateKey(Reader pemFile, String algorithm) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        KeyFactory kf = KeyFactory.getInstance(algorithm);
        EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(parsePEMFile(pemFile));
        return kf.generatePrivate(keySpec);
    }
}
