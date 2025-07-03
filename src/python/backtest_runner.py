import pandas as pd
import numpy as np
from utils import log


class BacktestEngine:
    def __init__(self, trades, signals):
        self.trades = trades
        self.signals = signals
        self.pnl = []

    def run(self):
        for idx, row in self.trades.iterrows():
            signal = self.signals.get(row["timestamp"], 0)
            fill = signal * row["size"]
            pnl = fill * (row["price"] - row["price"].shift(1, fill_value=row["price"]))
            self.pnl.append(pnl)
        return np.cumsum(self.pnl)


if __name__ == "__main__":
    trades = pd.read_parquet("../../data/processed/trades_enriched.parquet")
    signals = {row["timestamp"]: row["signal"] for _, row in trades.iterrows()}
    engine = BacktestEngine(trades, signals)
    cum_pnl = engine.run()
    log(f"Final P&L: {cum_pnl[-1]}")
