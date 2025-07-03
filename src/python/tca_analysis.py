import pandas as pd
import numpy as np
from utils import log


def compute_tca(trades_path, output_path):
    trades = pd.read_parquet(trades_path)
    trades["slippage"] = trades["price"] - trades["benchmark_price"]
    summary = trades.groupby("symbol")["slippage"].describe()
    summary.to_csv(output_path)
    log(f"TCA summary saved to {output_path}")


if __name__ == "__main__":
    compute_tca(
        "../../data/processed/trades_enriched.parquet",
        "../../analytics/tca_summary.csv",
    )
