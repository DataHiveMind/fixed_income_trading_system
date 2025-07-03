package com.investbank.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import java.util.Map;

public class ConfigLoader {
    public static Map<String, Object> load(String path) {
        try {
            ObjectMapper mapper = path.endsWith(".yml") || path.endsWith(".yaml")
                    ? new ObjectMapper(new YAMLFactory())
                    : new ObjectMapper();
            return mapper.readValue(new File(path), Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config: " + path, e);
        }
    }
}
