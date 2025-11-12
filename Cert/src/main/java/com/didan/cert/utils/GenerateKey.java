package com.didan.cert.utils;

import java.io.OutputStream;
import java.io.Writer;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import lombok.experimental.UtilityClass;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import java.io.StringReader;

@UtilityClass
public class GenerateKey {

  static {
    Security.addProvider(new BouncyCastleProvider());
  }

  public static final int DEFAULT_KEY_SIZE = 2048;
  public static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
  public static final String ALGORITHM = "RSA";
  public static final int DEFAULT_VALIDITY_DAYS = 365 * 25;

  public void generate() throws Exception {
    String alias = "viettel";
    char[] password = "17052002".toCharArray();
    Path out = Paths.get("out");
    Files.createDirectories(out);

    // Create RSA Key Pair
    KeyPair kp = generateRSA(DEFAULT_KEY_SIZE);

    // Create X.509 Certificate
    X509Certificate cert = selfSigned(kp, DEFAULT_VALIDITY_DAYS);

    // Write private key và public key - CẢ 2 ĐỊNH DẠNG
    Path privateKeyPem = out.resolve(alias + "-privateKey.pem");
    Path publicKeyPem = out.resolve(alias + "-publicKey.pem");
    Path privateKeyBase64 = out.resolve(alias + "-privateKey.key");
    Path publicKeyBase64 = out.resolve(alias + "-publicKey.key");

    // Format chuẩn PEM (có header/footer)
    writePrivateKeyPKCS8Pem(kp.getPrivate(), privateKeyPem);
    writePublicKeyPem(kp.getPublic(), publicKeyPem);

    // Format Base64 thuần (không có header/footer)
    writePrivateKeyBase64(kp.getPrivate(), privateKeyBase64);
    writePublicKeyBase64(kp.getPublic(), publicKeyBase64);

    // Write certificate
    Path certPem = out.resolve(alias + "-certificate.crt");
    Path certBase64 = out.resolve(alias + "-certificate.key");
    writeCertificatePem(cert, certPem);
    writeCertificateBase64(cert, certBase64);

    // Pack into a PKCS#12 KeyStore
    Path p12 = out.resolve(alias + "-keypair.p12");
    writePKCS12(p12, alias, password, kp.getPrivate(), cert);

    // Test đọc lại cả 2 format
    System.out.println("=== Testing PEM format (with headers) ===");
    String privateKeyPemContent = Files.readString(privateKeyPem);
    PrivateKey loadedKeyPem = getPrivateKey(privateKeyPemContent);
    System.out.println("✓ Private key PEM loaded successfully!");

    System.out.println("\n=== Testing Base64 format (without headers) ===");
    String privateKeyBase64Content = Files.readString(privateKeyBase64);
    PrivateKey loadedKeyBase64 = getPrivateKeyFromBase64(privateKeyBase64Content);
    System.out.println("✓ Private key Base64 loaded successfully!");
  }

  public KeyPair generateRSA(int bits) throws Exception {
    KeyPairGenerator g = KeyPairGenerator.getInstance(ALGORITHM);
    g.initialize(bits);
    return g.generateKeyPair();
  }

  public X509Certificate selfSigned(KeyPair keyPair, int days) throws Exception {
    X500Name subject = buildSubjectDN();
    BigInteger serial = new BigInteger(64, new SecureRandom());
    Date notBefore = new Date();
    Date notAfter = Date.from(Instant.now().plus(days, ChronoUnit.DAYS));

    X509v3CertificateBuilder b = new JcaX509v3CertificateBuilder(
        subject, serial, notBefore, notAfter, subject, keyPair.getPublic()
    );

    b.addExtension(Extension.basicConstraints, true, new BasicConstraints(false));
    b.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));

    ContentSigner signer = new JcaContentSignerBuilder(SIGNATURE_ALGORITHM).build(keyPair.getPrivate());
    X509CertificateHolder h = b.build(signer);

    return new JcaX509CertificateConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME).getCertificate(h);
  }

  public X500Name buildSubjectDN() {
    X500NameBuilder b = new X500NameBuilder(BCStyle.INSTANCE);
    b.addRDN(BCStyle.CN, "VDS");
    b.addRDN(BCStyle.O, "Viettel Digital Services");
    b.addRDN(BCStyle.L, "Hanoi");
    b.addRDN(BCStyle.C, "VN");
    return b.build();
  }

  // ==================== PEM FORMAT (WITH HEADERS) ====================

  public void writePrivateKeyPKCS8Pem(PrivateKey privateKey, Path path) throws Exception {
    try (Writer w = Files.newBufferedWriter(path);
        JcaPEMWriter pem = new JcaPEMWriter(w)) {
      pem.writeObject(privateKey);
    }
  }

  public void writePublicKeyPem(PublicKey publicKey, Path path) throws Exception {
    try (Writer w = Files.newBufferedWriter(path);
        JcaPEMWriter pem = new JcaPEMWriter(w)) {
      pem.writeObject(publicKey);
    }
  }

  public void writeCertificatePem(X509Certificate certificate, Path path) throws Exception {
    try (Writer w = Files.newBufferedWriter(path);
        JcaPEMWriter pem = new JcaPEMWriter(w)) {
      pem.writeObject(certificate);
    }
  }

  // ==================== BASE64 FORMAT (WITHOUT HEADERS) ====================

  /**
   * Ghi private key dạng Base64 thuần (không có header/footer)
   */
  public void writePrivateKeyBase64(PrivateKey privateKey, Path path) throws Exception {
    byte[] encoded = privateKey.getEncoded(); // PKCS#8 format
    String base64 = Base64.getEncoder().encodeToString(encoded);
    Files.writeString(path, base64);
  }

  /**
   * Ghi public key dạng Base64 thuần (không có header/footer)
   */
  public void writePublicKeyBase64(PublicKey publicKey, Path path) throws Exception {
    byte[] encoded = publicKey.getEncoded(); // X.509 SubjectPublicKeyInfo format
    String base64 = Base64.getEncoder().encodeToString(encoded);
    Files.writeString(path, base64);
  }

  /**
   * Ghi certificate dạng Base64 thuần (không có header/footer)
   */
  public void writeCertificateBase64(X509Certificate certificate, Path path) throws Exception {
    byte[] encoded = certificate.getEncoded();
    String base64 = Base64.getEncoder().encodeToString(encoded);
    Files.writeString(path, base64);
  }

  /**
   * Ghi private key dạng Base64 có xuống dòng (giống PEM nhưng không có header)
   * Mỗi dòng 64 ký tự (chuẩn PEM)
   */
  public void writePrivateKeyBase64WithLineBreaks(PrivateKey privateKey, Path path) throws Exception {
    byte[] encoded = privateKey.getEncoded();
    String base64 = Base64.getEncoder().encodeToString(encoded);
    String formatted = formatBase64WithLineBreaks(base64, 64);
    Files.writeString(path, formatted);
  }

  /**
   * Format Base64 string với line breaks
   */
  private String formatBase64WithLineBreaks(String base64, int lineLength) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < base64.length(); i += lineLength) {
      int end = Math.min(i + lineLength, base64.length());
      sb.append(base64, i, end).append("\n");
    }
    return sb.toString();
  }

  // ==================== READING METHODS ====================

  public void writePKCS12(Path path, String alias, char[] password, PrivateKey privateKey, X509Certificate certificate) throws Exception {
    KeyStore ks = KeyStore.getInstance("PKCS12");
    ks.load(null, null);
    ks.setKeyEntry(alias, privateKey, password, new X509Certificate[]{certificate});
    try (OutputStream os = Files.newOutputStream(path)) {
      ks.store(os, password);
    }
  }

  /**
   * Đọc private key từ PEM string (có header/footer)
   */
  public PrivateKey getPrivateKey(String pemContent) throws Exception {
    try (PEMParser pemParser = new PEMParser(new StringReader(pemContent))) {
      Object object = pemParser.readObject();

      JcaPEMKeyConverter converter = new JcaPEMKeyConverter()
          .setProvider(BouncyCastleProvider.PROVIDER_NAME);

      if (object instanceof PrivateKeyInfo) {
        return converter.getPrivateKey((PrivateKeyInfo) object);
      } else if (object instanceof org.bouncycastle.openssl.PEMKeyPair) {
        org.bouncycastle.openssl.PEMKeyPair keyPair = (org.bouncycastle.openssl.PEMKeyPair) object;
        return converter.getPrivateKey(keyPair.getPrivateKeyInfo());
      }

      throw new IllegalArgumentException("Unsupported key format");
    }
  }

  /**
   * Đọc private key từ file PEM
   */
  public PrivateKey getPrivateKeyFromFile(Path path) throws Exception {
    String content = Files.readString(path);

    // Tự động detect format
    if (content.contains("-----BEGIN")) {
      // PEM format with headers
      return getPrivateKey(content);
    } else {
      // Base64 format without headers
      return getPrivateKeyFromBase64(content.replaceAll("\\s+", ""));
    }
  }

  /**
   * Đọc private key từ Base64 string (không có header/footer)
   */
  public PrivateKey getPrivateKeyFromBase64(String base64String) throws NoSuchAlgorithmException, InvalidKeySpecException {
    // Remove any whitespace/newlines
    String cleaned = base64String.replaceAll("\\s+", "");
    byte[] encodedKey = Base64.getDecoder().decode(cleaned);
    KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encodedKey);
    return keyFactory.generatePrivate(keySpec);
  }

  /**
   * Đọc public key từ Base64 string
   */
  public PublicKey getPublicKeyFromBase64(String base64String) throws Exception {
    String cleaned = base64String.replaceAll("\\s+", "");
    byte[] encodedKey = Base64.getDecoder().decode(cleaned);
    KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
    java.security.spec.X509EncodedKeySpec keySpec = new java.security.spec.X509EncodedKeySpec(encodedKey);
    return keyFactory.generatePublic(keySpec);
  }

  /**
   * Đọc public key từ PEM string
   */
  public PublicKey getPublicKey(String pemContent) throws Exception {
    try (PEMParser pemParser = new PEMParser(new StringReader(pemContent))) {
      Object object = pemParser.readObject();

      JcaPEMKeyConverter converter = new JcaPEMKeyConverter()
          .setProvider(BouncyCastleProvider.PROVIDER_NAME);

      if (object instanceof org.bouncycastle.asn1.x509.SubjectPublicKeyInfo) {
        return converter.getPublicKey((org.bouncycastle.asn1.x509.SubjectPublicKeyInfo) object);
      } else if (object instanceof org.bouncycastle.openssl.PEMKeyPair) {
        org.bouncycastle.openssl.PEMKeyPair keyPair = (org.bouncycastle.openssl.PEMKeyPair) object;
        return converter.getPublicKey(keyPair.getPublicKeyInfo());
      }

      throw new IllegalArgumentException("Unsupported key format");
    }
  }

  /**
   * Đọc certificate từ PEM string
   */
  public X509Certificate getCertificate(String pemContent) throws Exception {
    try (PEMParser pemParser = new PEMParser(new StringReader(pemContent))) {
      Object object = pemParser.readObject();

      if (object instanceof X509CertificateHolder) {
        return new JcaX509CertificateConverter()
            .setProvider(BouncyCastleProvider.PROVIDER_NAME)
            .getCertificate((X509CertificateHolder) object);
      }

      throw new IllegalArgumentException("Not a valid certificate");
    }
  }

  /**
   * Đọc certificate từ Base64 string
   */
  public X509Certificate getCertificateFromBase64(String base64String) throws Exception {
    String cleaned = base64String.replaceAll("\\s+", "");
    byte[] decoded = Base64.getDecoder().decode(cleaned);
    java.security.cert.CertificateFactory cf = java.security.cert.CertificateFactory.getInstance("X.509");
    return (X509Certificate) cf.generateCertificate(new java.io.ByteArrayInputStream(decoded));
  }
}