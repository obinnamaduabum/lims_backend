package com.hertfordshire.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static com.google.common.io.ByteStreams.toByteArray;

public class CryptographyUtil {


    private static final Logger logger = LoggerFactory.getLogger(CryptographyUtil.class.getSimpleName());

    static private Base64.Encoder encoder = Base64.getEncoder();

    private static final String ALGORITHM = "RSA";

    private static byte[] encrypt(byte[] publicKeyByteArray, byte[] inputData)
            throws Exception {

        X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyByteArray);
        KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
        PublicKey key = kf.generatePublic(spec);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encryptedBytes = cipher.doFinal(inputData);

        return encryptedBytes;
    }

    private static byte[] decrypt(byte[] privateKeyByteArray, byte[] inputData)
            throws Exception {

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyByteArray);
        KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
        PrivateKey key = kf.generatePrivate(spec);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] decryptedBytes = cipher.doFinal(inputData);

        return decryptedBytes;
    }

    public static KeyPair generateKeyPair()
            throws NoSuchAlgorithmException, NoSuchProviderException {

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);

        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");

        // 512 is keysize
        keyGen.initialize(512, random);

        //KeyPair generateKeyPair = keyGen.generateKeyPair();

        Base64.Encoder encoder = Base64.getEncoder();
       // logger.info("public: " + encoder.encodeToString(generateKeyPair.getPublic().getEncoded()));

       // logger.info("private: " + encoder.encodeToString(generateKeyPair.getPrivate().getEncoded()));

        String[] args = {"512", "1024"};

        if (args.length == 0) {
            System.err.println("usage: java algo keySize [outFile]");
            System.exit(1);
        }

        int index = 0;
        String algo = args[index]; index++;


        KeyPair kp = keyGen.generateKeyPair();


        String outFile = null;
        if ( index < args.length ) outFile = args[index]; index++;

        Writer out = null;
        try {
            if ( outFile != null ) out = new FileWriter(outFile + ".key");
            else out = new OutputStreamWriter(System.out);

            //System.err.println("Private key format: " + kp.getPrivate().getFormat());
            out.write("-----BEGIN RSA PRIVATE KEY-----\n");
            writeBase64(out, kp.getPrivate());
            out.write("-----END RSA PRIVATE KEY-----\n");

            if ( outFile != null ) {
                out.close();
                out = new FileWriter(outFile + ".pub");
            }

            // System.err.println("Public key format: " + kp.getPublic().getFormat());
            out.write("-----BEGIN RSA PUBLIC KEY-----\n");
            writeBase64(out, kp.getPublic());
            out.write("-----END RSA PUBLIC KEY-----\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if ( out != null ) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        return kp;
    }

    public static String encryptThisData(String data) throws Exception {

        byte[] publicKey = getPemPublicKey("fcm-cookie-encryption-keys/public.pem");

        byte[] encryptedData = encrypt(publicKey, data.getBytes());

        logger.info(new String(encryptedData));

        return new String(encryptedData);
    }


    public static String decryptThisData(String encryptedData) {


        try {
            byte[] privateKey = getPemPrivateKey("fcm-cookie-encryption-keys/private.pem");

            //logger.info("" + DatatypeConverter.parseHexBinary(encryptedData));

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKey);
            KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
            PrivateKey key = kf.generatePrivate(spec);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] bytes = cipher.doFinal(DatatypeConverter.parseHexBinary(encryptedData));
            // byte[] bytes = DatatypeConverter.parseHexBinary(encryptedData);
            byte[] decryptedData = decrypt(privateKey, bytes);
            logger.info(new String(decryptedData));
            return new String(decryptedData);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private static void writeBase64(Writer out,Key key) throws IOException {
        byte[] buf = key.getEncoded();
        out.write(encoder.encodeToString(buf));
        out.write("\n");
    }


    private static byte[] getPemPrivateKey(String filename) throws Exception {
        String temp = getPemKeyValue(filename);
        assert temp != null;
        String privKeyPEM = temp.replace("-----BEGIN RSA PRIVATE KEY-----\n", "");
        privKeyPEM = privKeyPEM.replace("-----END RSA PRIVATE KEY-----", "");
        com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64 b64 = new com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64();
        byte[] decoded = b64.decode(privKeyPEM);
        return decoded;
    }

    private static byte[] getPemPublicKey(String filename) throws Exception {

        String temp = getPemKeyValue(filename);
        assert temp != null;
        String publicKeyPEM = temp.replace("-----BEGIN RSA PUBLIC KEY-----\n", "");
        publicKeyPEM = publicKeyPEM.replace("-----END RSA PUBLIC KEY-----", "");
        com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64 b64 = new com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64();
        byte[] decoded = b64.decode(publicKeyPEM);
        return decoded;
    }

    private static String getPemKeyValue(String filename) {
        InputStream inputStream = ResourceUtil.getResourceAsStream(filename);
        byte[] keyBytes = null;
        try {
            keyBytes = toByteArray(inputStream);
            return new String(keyBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
