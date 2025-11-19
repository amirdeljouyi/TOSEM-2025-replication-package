import logging
import os
import sys
from pathlib import Path
from time import strftime, gmtime


class AppConfig:
    _instance = None

    def __init__(self):
        if AppConfig._instance is not None:
            raise Exception("AppConfig is already initialized!")
        self.model_name = os.getenv("MODEL_NAME")
        self.write_responses = (os.getenv('WRITE_RESPONSES', 'False') == 'True')
        self.write_failed_responses = (os.getenv('WRITE_FAILED_RESPONSES', 'False') == 'True')
        self.log_to_file = (os.getenv('LOG_TO_FILE', 'False') == 'True')
        self.log_level = os.getenv('LOG_LEVEL', 'INFO').upper()
        self.load_api_keys()
        self.log_filename = strftime("log-%Y-%m-%d_%H-%M-%S.log", gmtime())
        self.attempt = os.getenv("ATTEMPT")
        self.signature = os.getenv("SIGNATURE")

        print("attempt", self.attempt)

        AppConfig._instance = self

    def load_api_keys(self):
        dotenv_path = Path('config.env')
        if dotenv_path.exists():
            from dotenv import load_dotenv
            load_dotenv(dotenv_path)
        self.openai_key = os.getenv("OPENAI_API_KEY")
        self.hf_key = os.getenv("HF_KEY")
        self.hf_url = os.getenv("HF_URL")


    @staticmethod
    def get_instance():
        if AppConfig._instance is None:
            raise Exception("AppConfig is not initialized. Initialize it first in main.py")
        return AppConfig._instance

    @staticmethod
    def init_once():
        """Initialize AppConfig only if it's not already initialized."""
        if AppConfig._instance is None:
            AppConfig()

    def setup_logger(self, name: str = "LLMApp") -> logging.Logger:
        level = self.log_level
        logger = logging.getLogger(name)
        logger.setLevel(level)

        # Prevent duplicate handlers
        if not logger.handlers:
            # Log format
            formatter = logging.Formatter(
                "[%(asctime)s] [%(levelname)s] [%(name)s] %(message)s",
                datefmt="%Y-%m-%d %H:%M:%S"
            )

            if self.log_to_file:
                file_handler = logging.FileHandler(self.log_filename)
                file_handler.setFormatter(formatter)
                file_handler.setLevel(level)
                logger.addHandler(file_handler)
            else:
                # Only console output
                console_handler = logging.StreamHandler(sys.stdout)
                console_handler.setFormatter(formatter)
                console_handler.setLevel(level)
                logger.addHandler(console_handler)

        return logger