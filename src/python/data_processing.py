import pandas as pd
import numpy as np
from utils import load_config, log


def process_raw_data(input_path, output_path):
    df = pd.read_csv(input_path)
    df["timestamp"] = pd.to_datetime(df["timestamp"])
    df = df.drop_duplicates()
    df = df[df["price"] > 0]
    df = df[df["size"] > 0]
    df.to_parquet(output_path)
    log(f"Processed {len(df)} rows to {output_path}")


if __name__ == "__main__":
    cfg = load_config("config/database.yml")
    process_raw_data(cfg["raw_ticks"], cfg["processed_ticks"])
