package org.soloviova.liudmyla.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PlayerMapper {

    private static PlayerMapper playerMapper;
    private final ObjectMapper objectMapper;

    private PlayerMapper() {
        objectMapper = new ObjectMapper();
    }

    public static synchronized PlayerMapper getInstance() {
        if (playerMapper == null) {
            playerMapper = new PlayerMapper();
        }
        return playerMapper;
    }

    public synchronized String mapPlayerObjectToJsonStringSuppressException(final Object playerObject) {
        try {
            return objectMapper.writeValueAsString(playerObject);
        } catch (JsonProcessingException e) {
            log.error("An exception occurred while mapping object {}:\n{}", playerObject.toString(), e.getMessage());
            return "{}";
        }
    }
}
