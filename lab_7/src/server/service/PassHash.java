package lab_7.server.service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * класс хэшев
 */
public abstract class PassHash {
    private static final Logger logger = Logger.getLogger(PassHash.class.getName());

    /**
     * хэширует строку в SHA-384
     *
     * @param input строка ввода
     * @return строка хеша
     */
    public static String hash(String input) {
        try {
            byte[] digest;
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-384");
            messageDigest.update(input.getBytes());
            digest = messageDigest.digest();
            BigInteger bigInteger = new BigInteger(1, digest);
            StringBuilder hash = new StringBuilder(bigInteger.toString(16));
            while (hash.length() < 32)
                hash.insert(0, "0");
            return hash.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.log(Level.SEVERE, "Сбой при логировании пароля ", e);
            return null;
        }
    }
}
