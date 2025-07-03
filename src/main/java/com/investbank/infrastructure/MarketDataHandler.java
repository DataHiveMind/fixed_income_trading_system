package com.investbank.infrastructure;

import com.investbank.infrastructure.DisruptorManager;
import com.investbank.infrastructure.FixConnector;
import com.investbank.infrastructure.LoggingManager;
import com.investbank.infrastructure.ConfigLoader;
import org.slf4j.Logger;

public class MarketDataHandler {
    private final FixConnector fixConnector;
    private final DisruptorManager disruptorManager;
    private final Logger logger;
    // Add custom FAST parser as needed

    public MarketDataHandler(FixConnector fixConnector, DisruptorManager disruptorManager) {
        this.fixConnector = fixConnector;
        this.disruptorManager = disruptorManager;
        this.logger = LoggingManager.getLogger(MarketDataHandler.class);
    }

    public void start() {
        // Start FIX session
        fixConnector.connect();
        // Start proprietary feed if needed
        // ...
    }

    // Called by FixConnector or FAST parser on new tick
    public void onMarketDataTick(Object tick) {
        disruptorManager.publish(tick);
        logger.info("Published tick: {}", tick);
    }
}
