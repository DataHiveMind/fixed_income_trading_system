import pandas as pd
import numpy as np
from utils import log


def compute_features(input_path, output_path):
    df = pd.read_parquet(input_path)
    df["vwap"] = (df["price"] * df["size"]).rolling(50).sum() / df["size"].rolling(
        50
    ).sum()
    df["ma_20"] = df["price"].rolling(20).mean()
    df["ma_100"] = df["price"].rolling(100).mean()
    df["curve_shift"] = df["price"] - df["price"].shift(5)
    df["spread"] = df["ask"] - df["bid"]
    df.to_parquet(output_path)
    log(f"Features computed and saved to {output_path}")


if __name__ == "__main__":
    compute_features(
        "../../data/processed/ticks_parsed.parquet",
        "../../data/processed/features.parquet",
    )
