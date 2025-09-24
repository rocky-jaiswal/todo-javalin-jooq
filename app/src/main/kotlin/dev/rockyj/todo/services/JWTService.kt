package dev.rockyj.todo.services

import dev.rockyj.todo.config.Secrets
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.crypto.RSASSAVerifier
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo
import java.io.FileReader
import java.security.Security
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

/**
 * JWT Service for signing and verifying tokens using RSA keys with passphrase protection
 * Keys are static and loaded from files
 */
class JWTService {
    private val signer: RSASSASigner
    private val verifier: RSASSAVerifier

    companion object {
        private val KEY_PASSWORD = Secrets.KEY_PASSWORD
        private val PRIVATE_KEY_FILE = Secrets.PRIVATE_KEY_FILE
        private val PUBLIC_KEY_FILE = Secrets.PUBLIC_KEY_FILE

        init {
            // Add BouncyCastle provider for PEM parsing
            Security.addProvider(BouncyCastleProvider())
        }
    }

    init {
        val privateKeyResource = Thread.currentThread().contextClassLoader.getResource(PRIVATE_KEY_FILE)
            ?: throw IllegalArgumentException("File not found!")

        val pubKeyResource = Thread.currentThread().contextClassLoader.getResource(PUBLIC_KEY_FILE)
            ?: throw IllegalArgumentException("File not found!")

        // Load keys from files
        val privateKey = loadPrivateKey(privateKeyResource.path, KEY_PASSWORD)
        val publicKey = loadPublicKey(pubKeyResource.path)

        // Initialize signer and verifier
        this.signer = RSASSASigner(privateKey)
        this.verifier = RSASSAVerifier(publicKey)
    }

    /**
     * Sign a JWT with custom claims
     */
    // @Throws(Exception::class)
    fun signJWT(
        subject: String?, audience: String?, expirationMinutes: Long, customClaims: MutableMap<String?, Any?>?
    ): String {
        val now = Instant.now()
        val expiration = now.plus(expirationMinutes, ChronoUnit.MINUTES)


        // Create JWT claims
        val claimsBuilder = JWTClaimsSet.Builder()
            .subject(subject)
            .issuer("dev.rockyj")
            .audience(audience)
            .expirationTime(Date.from(expiration))
            .notBeforeTime(Date.from(now))
            .issueTime(Date.from(now))
            .jwtID(UUID.randomUUID().toString())


        // Add custom claims
        customClaims?.forEach(claimsBuilder::claim)
        val claimsSet: JWTClaimsSet? = claimsBuilder.build()

        // Create JWT header
        val header = JWSHeader(JWSAlgorithm.RS512)

        // Create and sign JWT
        val signedJWT = SignedJWT(header, claimsSet)
        signedJWT.sign(signer)

        return signedJWT.serialize()
    }

    /**
     * Verify and parse a JWT
     */
    // @Throws(Exception::class)
    fun verifyJWT(token: String?): JWTClaimsSet {
        val signedJWT: SignedJWT = SignedJWT.parse(token)

        // Verify signature
        if (!signedJWT.verify(verifier)) {
            throw SecurityException("Invalid JWT signature")
        }

        val claimsSet: JWTClaimsSet = signedJWT.jwtClaimsSet

        // Verify expiration
        val expirationTime: Date? = claimsSet.expirationTime
        if (expirationTime != null && expirationTime.before(Date())) {
            throw SecurityException("JWT has expired")
        }

        // Verify not before
        val notBefore: Date? = claimsSet.notBeforeTime
        if (notBefore != null && notBefore.after(Date())) {
            throw SecurityException("JWT not yet valid")
        }

        return claimsSet
    }

    /**
     * Load RSA private key from PEM file with passphrase
     */
    // @Throws(Exception::class)
    private fun loadPrivateKey(filePath: String, passphrase: String): RSAPrivateKey {
        FileReader(filePath).use { fileReader ->
            PEMParser(fileReader).use { pemParser ->
                val pemObject = pemParser.readObject() as PKCS8EncryptedPrivateKeyInfo

                val decryptorProvider = JceOpenSSLPKCS8DecryptorProviderBuilder()
                    .setProvider("BC")
                    .build(passphrase.toCharArray())

                val privateKeyInfo = pemObject.decryptPrivateKeyInfo(decryptorProvider)
                val converter = JcaPEMKeyConverter().setProvider("BC")
                val privateKey = converter.getPrivateKey(privateKeyInfo)

                if (privateKey !is RSAPrivateKey) {
                    throw IllegalArgumentException("Key is not an RSA private key")
                }

                return privateKey
            }
        }
    }

    /**
     * Load RSA public key from PEM file
     */
    // @Throws(Exception::class)
    private fun loadPublicKey(filePath: String): RSAPublicKey {
        FileReader(filePath).use { fileReader ->
            PEMParser(fileReader).use { pemParser ->
                val pemObject: Any? = pemParser.readObject()
                val converter: JcaPEMKeyConverter = JcaPEMKeyConverter().setProvider("BC")

                if (pemObject is SubjectPublicKeyInfo) {
                    return converter.getPublicKey(pemObject) as RSAPublicKey
                } else {
                    throw IllegalArgumentException("Unsupported PEM object type for public key")
                }
            }
        }
    }
}