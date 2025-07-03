import yaml
import logging


def load_config(path):
    with open(path, "r") as f:
        return yaml.safe_load(f)


def log(msg):
    logging.basicConfig(level=logging.INFO)
    logging.info(msg)
