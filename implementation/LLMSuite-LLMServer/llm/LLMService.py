import os
from time import strftime, gmtime

from config.AppConfig import AppConfig
from llm.LLMCommunicator import LLMCommunicator
from llm.PromptBuilder import PromptBuilder
from llm.ResponseParser import ResponseParser
from schema import Prompt, Response
import RequestState
from utility.code_validator import merge_test_cases, extract_class_name_from_prompt
from pprint import pformat

class LLMService:

    def __init__(self, prompt: Prompt, state: RequestState):
        self.communicator = LLMCommunicator()
        self.prompt_builder = PromptBuilder()
        self.parser = ResponseParser(prompt.prompt_type, prompt.additional_param)
        self.state = state
        self.prompt = prompt
        self.logger = AppConfig.get_instance().setup_logger("LLMService")


    def process_prompt(self) -> Response:
        self.state.increment_iteration()

        if self.state.get_iteration() > 5:
            return Response(llm_response=f"// Unable to generate response after retries.\n{self.prompt.prompt_text}")

        constructed_prompt = self.prompt_builder.build(self.prompt, self.state)
        self.logger.info("Raw Prompt: \n%s", pformat(constructed_prompt))

        raw_response = self.communicator.call_llm(constructed_prompt, self.state)
        self.logger.info("Raw response: \n%s", pformat(raw_response))

        if self.state.prompt_type == "generate_test_class":
            raw_response = self._correct_test_class_generation()

        parsed_response = self.parser.process(raw_response)
        self.logger.info("Parsed response: \n%s", pformat(parsed_response))

        if self.state.prompt_type == "generate_test_class":
            parsed_response = self._handle_test_class_generation(parsed_response)
        elif self.state.prompt_type == "generate_test_class_wosr":
            parsed_response = self._dict_tests_to_string(parsed_response)

        if not parsed_response:
            return self.process_prompt()  # Recursive retry

        self.state.end_request()

        if AppConfig.get_instance().write_responses:
            self.write_tests_to_file(parsed_response)

        self.communicator.reset()

        self.logger.info("State: \n%s", pformat(self.state))

        return Response(llm_response=parsed_response)

    def _correct_test_class_generation(self) -> str:
        correct_prompt = Prompt(
            prompt_text=self.prompt.prompt_text,
            prompt_type="correct_test_class",
            additional_param=self.prompt.additional_param
        )

        corrected_prompt = self.prompt_builder.build(correct_prompt, self.state)
        self.logger.info("Corrected Raw Prompt: \n%s", pformat(corrected_prompt))

        corrected_response = self.communicator.call_llm(corrected_prompt, self.state)
        self.logger.info("Corrected Raw response: \n%s", pformat(corrected_response))

        return corrected_response

    def _handle_test_class_generation(self, validated_tests) -> str:

        while self.state.can_be_improved():
            refined_tests = self._refine_test_cases()

            self.logger.info("Refined Tests: \n%s", pformat(refined_tests))

            validated_tests = merge_test_cases(validated_tests, refined_tests)
            self.state.increment_self_improvement()

        return self._dict_tests_to_string(validated_tests)

    def _dict_tests_to_string(self, validated_tests) -> str:
        test_name = f"public class {extract_class_name_from_prompt(self.prompt.prompt_text, self.prompt.prompt_type)} {{ \n\n"
        body = "\n".join(validated_tests.values())

        return f"{test_name} {body} \n}}"

    def _refine_test_cases(self) -> dict:
        refinement_prompt = Prompt(
            prompt_text=self.prompt.prompt_text,
            prompt_type="self_refinement",
            additional_param=self.prompt.additional_param
        )
        refined_prompt = self.prompt_builder.build(refinement_prompt, self.state)
        self.logger.info("Raw Refined Prompt: \n%s", pformat(refined_prompt))

        refined_response = self.communicator.call_llm(refined_prompt, self.state)

        self.logger.info("Raw Refined response: \n%s", pformat(refined_response))

        return self.parser.process(refined_response) or {}

    def write_tests_to_file(self, file_response: str, filename: str = "FinalGeneratedTests"):
        """
        Write the final test methods into a file as a Java class.
        """
        directory = "response_results"
        if AppConfig.get_instance().signature is not None:
            directory = os.path.join(directory, AppConfig.get_instance().signature)

        filename = extract_class_name_from_prompt(self.prompt.prompt_text, self.prompt.prompt_type) or filename

        if self.prompt.prompt_type == "pool":
            filename = filename + ".txt"
        else:
            filename = filename  + ".java"

        file_path = os.path.join(directory, filename)

        with open(file_path, "w", encoding="utf-8") as f:
            f.write(file_response)

        self.logger.info(f"\n✅ Final test cases written to file: {filename}")