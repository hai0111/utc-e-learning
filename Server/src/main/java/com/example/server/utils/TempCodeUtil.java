package com.example.server.utils;

import com.example.server.exception.CustomServiceException;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Component
public class TempCodeUtil {

    private static final Map<String, TemporaryCode> temporaryCodes = new HashMap<>();
    private static final long EXPIRATION_TIME = 600000; // 10 minutes

    private static class TemporaryCode {
        @Getter
        private String code;
        private long expirationTime;

        public TemporaryCode(String code) {
            this.code = code;
            this.expirationTime = System.currentTimeMillis() + EXPIRATION_TIME;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }

        public void resetExpirationTime() {
            this.expirationTime = System.currentTimeMillis() + EXPIRATION_TIME;
        }
    }

    private String generateTemporaryCode(String id) {
        Random random = new Random();
        int min = 100000;
        int max = 999999;
        int randomNumber = random.nextInt(max - min + 1) + min;
        String code = String.format("%06d", randomNumber);
        temporaryCodes.put(id, new TemporaryCode(code));
        return code;
    }

    public Boolean verifyTemporaryCode(String id, String code) {

        // Get archive code
        TemporaryCode storedCode = temporaryCodes.get(id);

        // Check for the existence of the ID and check if the code matches
        if (storedCode != null && storedCode.getCode().equals(code)) {

            // Check if the code has expired
            if (storedCode.isExpired()) {
                temporaryCodes.remove(id); // Delete expired code
                throw new CustomServiceException("Verification code has expired", HttpStatus.GONE);
            } else {
                temporaryCodes.remove(id); // Delete code after successful authentication
                return true;
            }
        }

        // Code does not match or ID does not exist
        throw new CustomServiceException("Verification code is invalid or does not match", HttpStatus.BAD_REQUEST);
    }

    public String createTemporaryCode(String id) {
        if (temporaryCodes.containsKey(id)) {
            TemporaryCode storedCode = temporaryCodes.get(id);
            storedCode.resetExpirationTime(); // Reset expiration time
            String newCode = generateTemporaryCode(id); // Generate new code
            System.out.println("Reset thành công. Mã mới: " + newCode);
            return newCode;
        } else {
            return generateTemporaryCode(id);
        }
    }

    public void removeCode(String id) {
        temporaryCodes.remove(id);
    }
}
