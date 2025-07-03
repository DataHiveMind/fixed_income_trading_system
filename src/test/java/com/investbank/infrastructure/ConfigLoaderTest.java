package com.investbank.infrastructure;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;

public class ConfigLoaderTest {
    @Test
    void testValidYamlLoad() {
        Map<String, Object> config = ConfigLoader.load("config/fix.yml");
        assertNotNull(config);
    }
}
