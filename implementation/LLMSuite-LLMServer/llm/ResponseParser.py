import os

from utility.code_validator import *


class ResponseParser:
    def __init__(self, prompt_type: str, additional_param: Optional[str] = None):
        self.prompt_type = prompt_type
        self.additional_param = additional_param
        self.logger = AppConfig.get_instance().setup_logger("Parser")

    def process(self, response: str) -> Optional[str] | Optional[dict]:
        """
        Central process to parse, validate, and handle the response based on prompt_type and state.
        """
        self.logger.info(f"Processing response for prompt_type: {self.prompt_type}")
        extracted_answer = self._extract_raw_content(response, self.prompt_type)

        if not extracted_answer:
            self.logger.info("Failed to extract any content from LLM response.")
            return None

        # Handling for each specific prompt type
        if self.prompt_type == "testname":
            return self.parse_testname(extracted_answer, self.additional_param)
        elif self.prompt_type == "testdata":
            return self.parse_test_data(extracted_answer)
        elif self.prompt_type == "pool":
            return self.parse_pool(extracted_answer)
        elif self.prompt_type == "generate":
            return self.parse_generate_test_method(extracted_answer)
        elif self.prompt_type == "codamosa":
            return self.parse_generate_test_method_mosa(extracted_answer)
        elif self.prompt_type == "generate_test_class" or self.prompt_type == "correct_test_class" or self.prompt_type == "generate_test_class_wosr":
            return self.parse_generate_test_class(extracted_answer)
        else:
            return self.parse_refined_test(extracted_answer)

    def _extract_raw_content(self, response: str, prompt_type: str) -> Optional[str]:
        if prompt_type == "generate_test_class" or prompt_type == "self_refinement":
            return response

        regex_map = {
            "testname": r'\[TESTNAME](.*?)\[/TESTNAME]',
            "testdata": r'\[TESTDATA](.*?)\[/TESTDATA]',
            "pool": r'\[SENTENCES](.*?)\[/SENTENCES]',
            "generate": r'\[TEST](.*?)\[/TEST]',
        }
        regex = regex_map.get(prompt_type, r'\[TEST](.*?)\[/TEST]')

        match = re.search(regex, response, re.DOTALL)
        if match:
            self.logger.info("[Parser] Extracted content based on tags.")
            return match.group(1).strip()

        self.logger.warning("[Parser] Failed to extract tagged content. Trying fallback extraction.")
        return self._extract_fallback(response)

    def _extract_fallback(self, response: str) -> Optional[str]:
        """ Fallback in case normal tags are missing """
        fallback_regex = r'```(.*?)```'
        match = re.search(fallback_regex, response, re.DOTALL)
        return match.group(1).strip() if match else None

    def parse_generate_test_method(self, code: str) -> Optional[str]:
        body = get_code_body(code)
        if not basic_validation(body):
            return None
        if not validate_java_code(body):
            return None
        return body

    def parse_generate_test_method_mosa(self, code: str) -> str | None:
        self.logger.info(f"Code to parse is: {code}")

        valid_tests, invalid_tests = validate_java_test_class(code)

        # Handle invalid tests: log them to a file or print warnings
        self.logger.debug("Invalid tests: {}".format(invalid_tests))
        if invalid_tests and AppConfig.get_instance().write_failed_responses:
            self._log_invalid_tests(invalid_tests)

        if valid_tests:
            first_value = next(iter(valid_tests.values()))
            body = get_code_body(first_value)
            if not basic_validation(body):
                return None
            if not validate_java_code(body):
                return None
            return body

        return None

    def parse_generate_test_class(self, code: str) -> Optional[dict]:
        self.logger.info(f"Code to parse is: {code}")

        valid_tests, invalid_tests = validate_java_test_class(code)

        # Handle invalid tests: log them to a file or print warnings
        self.logger.debug("Invalid tests: {}".format(invalid_tests))
        if invalid_tests and AppConfig.get_instance().write_failed_responses:
            self._log_invalid_tests(invalid_tests)

        return valid_tests

    def parse_test_data(self, code: str) -> Optional[str]:
        extracted = get_code_body(code)
        return extracted if "@Test" not in code else None

    def parse_pool(self, sentences: str) -> str:
        self.logger.info(f"Sentences to parse is: {sentences}")

        # Regex that supports both numbered and unnumbered quoted sentences
        pattern = r'(?:^\d+\.\s*)?"(.*?)"'

        # re.MULTILINE makes ^ and $ work on each line
        results = re.findall(pattern, sentences, re.MULTILINE)

        return "\n".join(results)

    def parse_testname(self, code: str, additional_param: Optional[str]) -> Optional[str]:
        name = validate_test_name(code, additional_param)
        self.logger.info(f"Processed testname: {name}")
        return name

    def parse_refined_test(self, code: str) -> Optional[dict]:
        valid_tests, invalid_tests = validate_java_test_class(code)

        # Handle invalid tests: log them to a file or print warnings
        if invalid_tests and AppConfig.get_instance().write_failed_responses:
            self._log_invalid_tests(invalid_tests)

        return valid_tests

    def _log_invalid_tests(self, invalid_tests: dict[str, str], filename: str = "invalid_tests.log"):
        """
        Logs invalid test methods into a file for review.
        """

        directory = "invalid_responses"
        file_path = os.path.join(directory, filename)
        with open(file_path, "a", encoding="utf-8") as f:
            f.write("\n===== Invalid Test Methods Detected =====\n")
            for method_name, method_code in invalid_tests.items():
                f.write(f"\n// Invalid Test: {method_name}\n")
                f.write(f"{method_code}\n\n")
        self.logger.info(f"[Handler] ⚠️ Logged {len(invalid_tests)} invalid test methods to {filename}.")
