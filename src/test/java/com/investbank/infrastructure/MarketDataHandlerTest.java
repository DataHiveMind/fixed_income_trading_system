package com.investbank.infrastructure;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MarketDataHandlerTest {
    @Test
    void testTickPublication() {
        class DummyDisruptor extends DisruptorManager {
            public Object last;

            public DummyDisruptor() {
                super(16, (e, s, b) -> {
                });
            }

            @Override
            public void publish(Object obj) {
                last = obj;
            }
        }
        DummyDisruptor disruptor = new DummyDisruptor();
        MarketDataHandler handler = new MarketDataHandler(null, disruptor);
        handler.onMarketDataTick("tick");
        assertEquals("tick", disruptor.last);
    }
}
