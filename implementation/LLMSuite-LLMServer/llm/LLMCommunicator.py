import time
import json
import traceback
import requests
from openai import OpenAI, OpenAIError, RateLimitError, APIConnectionError, Timeout
from RequestState import RequestState
from config.AppConfig import AppConfig


class LLMCommunicator:
    def __init__(self):
        self.openai_key = None
        self.hf_key = None
        self.config = AppConfig.get_instance()
        self.model = self.config.model_name
        self.openai_key = self.config.openai_key
        self.hf_key = self.config.hf_key
        self.hf_url = self.config.hf_url
        self.client = OpenAI(api_key=self.openai_key)
        self.logger = AppConfig.get_instance().setup_logger("LLMCommunicator")

        # Store chat history
        self.conversation_history = [
            {
                "role": "system",
                "content": (
                    "You are an expert Java developer specializing in writing high-quality unit tests for NLP libraries. "
                    "Follow best practices, ensuring comprehensive test coverage. "
                    "Strictly adhere to JUnit 4 conventions and the provided guidelines."
                ),
            }
        ]

    def call_llm(self, prompt: str, state: RequestState) -> str:
        state.increment_llm_calls()
        self.logger.info("Calling model {}".format(self.model))

        if self.model.startswith("gpt-") or self.model.startswith("chatgpt-"):
            return self._call_openai(prompt)
        elif self.model.endswith("-hf"):
            return self._call_huggingface_api(prompt)
        else:
            return self._call_ollama(prompt)

    def _call_openai(self, prompt: str, max_retries=3, retry_delay=20) -> str:
        # Append user input to history
        self.conversation_history.append({"role": "user", "content": prompt})
        attempt = 0

        while attempt < max_retries:
            try:
                response = self.client.chat.completions.create(
                    model=self.model,
                    messages=self.conversation_history,
                    # temperature=0.6,
                    # top_p=0.9,
                    max_tokens=12192,
                    timeout=120
                )

                # Append assistant's response to history
                assistant_reply = response.choices[0].message.content.strip()
                self.conversation_history.append({"role": "assistant", "content": assistant_reply})

                return assistant_reply

            except (Timeout, APIConnectionError) as e:
                self.logger.warning(f"[Timeout/APIConnection] Attempt {attempt + 1}: {e}")
            except RateLimitError as e:
                self.logger.warning(f"[RateLimitError] Attempt {attempt + 1}: {e}")
            except OpenAIError as e:
                self.logger.warning(f"[OpenAIError] Attempt {attempt + 1}: {e}")
                break  # don't retry for general OpenAI API errors

            attempt += 1
            time.sleep(retry_delay * attempt)

        self.logger.error("Error: LLM did not respond in time. Please try again later.")
        return "Error: LLM did not respond in time. Please try again later."

    def _call_ollama(self, prompt: str) -> str:
        API_URL = "http://localhost:11434/api/generate"
        try:
            response = requests.post(API_URL, json={"model": self.model, "prompt": prompt, "stream": False}, timeout=60)
            if response.status_code == 200:
                return response.json()["response"].strip()
        except requests.exceptions.Timeout:
            self.logger.error("Timeout from Ollama")
        return "Error during LLM processing."

    def _call_huggingface_api(self, prompt: str) -> str:
        headers = {
            "Accept": "application/json",
            "Authorization": f"Bearer {self.hf_url}",
            "Content-Type": "application/json"
        }
        data = {
            "inputs": prompt,
            "parameters": {
                "max_new_tokens": 400,
                "return_full_text": False,
                "rope_frequency_base": 1000000
            }
        }

        try:
            response = requests.post(self.hf_key, headers=headers, json=data, timeout=60)
            response.raise_for_status()
            json_response = response.json()
            if isinstance(json_response, list) and "generated_text" in json_response[0]:
                return json_response[0]["generated_text"].strip()
            else:
                self.logger.warning(f"Unexpected Hugging Face response format: {json_response}")
                return "Error: Unexpected response format from Hugging Face API."
        except Exception as e:
            self.logger.error(f"Hugging Face API error: {e}")
            self.logger.debug(traceback.format_exc())
            return "Error during Hugging Face API request."

    def reset(self):
        self.conversation_history = [
            {
                "role": "system",
                "content": (
                    "You are an expert Java developer specializing in writing high-quality unit tests for NLP libraries. "
                    "Follow best practices, ensuring comprehensive test coverage. "
                    "Strictly adhere to JUnit 4 conventions and the provided guidelines."
                ),
            }
        ]