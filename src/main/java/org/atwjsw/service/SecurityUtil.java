package org.atwjsw.service;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.apache.shiro.codec.Hex;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha512Hash;
import org.apache.shiro.util.ByteSource;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class SecurityUtil {

    @Inject
    private QueryService queryService;

    private SecretKey secretKey;

    @PostConstruct
    private void init() {
        secretKey = MacProvider.generateKey(SignatureAlgorithm.HS512);
    }

    // generate a secret key to sign the jwt token
    public Key generateKey() {
        return secretKey;
//        return new SecretKeySpec(keyString.getBytes(), 0, keyString.length(), "DES");
    }

    public boolean authenticateUser(String email, String password) {
        return queryService.authenticateUser(email, password);
    }

    public Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public boolean passwordMatch(String dbStoredHashedPassword, String saltText, String plainTextPassword) {
        ByteSource salt = ByteSource.Util.bytes((Hex.decode(saltText)));
        String hashedPassword = hashAndSaltPassword(plainTextPassword, salt);
        return hashedPassword.equals(dbStoredHashedPassword);
    }

    public Map<String, String> hashPassword(String plainTextPassword) {
        ByteSource salt = getSalt();
        Map<String, String> credMap = new HashMap<>();
        credMap.put("hashedPassword", hashAndSaltPassword(plainTextPassword, salt));
        credMap.put("salt", salt.toHex());
        return credMap;
    }

    private String hashAndSaltPassword(String plainTextPassword, ByteSource salt) {
        return new Sha512Hash(plainTextPassword, salt, 2000000).toHex();
    }

    private ByteSource getSalt() {
        return new SecureRandomNumberGenerator().nextBytes();
    }
}
