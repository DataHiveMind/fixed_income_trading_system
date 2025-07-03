package com.investbank.infrastructure;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FixConnectorTest {
    @Test
    void testSessionLifecycle() {
        // This is a stub; in real tests, use QuickFIX/J simulator
        FixConnector connector = new FixConnector(null);
        assertNotNull(connector);
    }
}
