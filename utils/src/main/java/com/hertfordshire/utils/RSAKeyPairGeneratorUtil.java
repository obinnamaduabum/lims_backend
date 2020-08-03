package com.hertfordshire.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.util.Base64;

public class RSAKeyPairGeneratorUtil {

    private PrivateKey privateKey;
    private PublicKey publicKey;

    public RSAKeyPairGeneratorUtil() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);
        KeyPair pair = keyGen.generateKeyPair();
        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();
    }

    public void writeToFile(String path, byte[] key) throws IOException {
        File f = new File(path);
        f.getParentFile().mkdirs();

        FileOutputStream fos = new FileOutputStream(f);
        fos.write(key);
        fos.flush();
        fos.close();
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public static void doWriteToFile() throws NoSuchAlgorithmException, IOException {
        RSAKeyPairGeneratorUtil keyPairGeneratorUtil = new RSAKeyPairGeneratorUtil();
        keyPairGeneratorUtil.writeToFile("fcm-cookie-encryption-keys/publicKey", keyPairGeneratorUtil.getPublicKey().getEncoded());
        keyPairGeneratorUtil.writeToFile("fcm-cookie-encryption-keys/privateKey", keyPairGeneratorUtil.getPrivateKey().getEncoded());
        System.out.println(Base64.getEncoder().encodeToString(keyPairGeneratorUtil.getPublicKey().getEncoded()));
        System.out.println(Base64.getEncoder().encodeToString(keyPairGeneratorUtil.getPrivateKey().getEncoded()));
    }
}
