package com.itheima.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class UserToken {
    public static Long getUserIdFromToken(String jwt) throws Exception {
        // Remove "Bearer " prefix from access token
        String tokenWithoutPrefix = jwt.substring("Bearer ".length());
        // Decode the token payload (JWT) to get its contents
        String[] parts = tokenWithoutPrefix.split("\\.");
        String encodedPayload = parts[1];
        byte[] decodedPayloadBytes = Base64.getUrlDecoder().decode(encodedPayload);
        String decodedPayload = new String(decodedPayloadBytes, StandardCharsets.UTF_8);

        // Parse the payload as JSON to extract the user_id field
        ObjectMapper mapper = new ObjectMapper();
        JsonNode payloadJson = mapper.readTree(decodedPayload);
        return Long.valueOf(payloadJson.get("user_id").asText());
    }
}

