package com.investbank.autohedge;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class AutoHedgeManagerTest {
    @Test
    void testHedgeOrderDispatch() {
        List<HedgeOrderManager.HedgeOrder> sentOrders = new ArrayList<>();
        HedgeOrderManager manager = new HedgeOrderManager(null) {
            @Override
            public void sendOrder(HedgeOrder order) {
                sentOrders.add(order);
            }
        };
        HedgeStrategy.HedgeParams params = new HedgeStrategy.HedgeParams(0.1, 0.1, 1.0, 1.0);
        HedgeStrategy strategy = new HedgeStrategy(params, HedgeStrategy.HedgeType.DELTA_NEUTRAL);
        HedgeEvaluator evaluator = new HedgeEvaluator();
        AutoHedgeManager auto = new AutoHedgeManager(manager, strategy, evaluator);
        auto.onFill(new AutoHedgeManager.FillEvent("SYM", 100, 99.0, 0.2, 0.0));
        assertFalse(sentOrders.isEmpty());
    }
}
