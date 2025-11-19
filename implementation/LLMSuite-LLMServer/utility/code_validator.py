import re
from time import strftime, gmtime

from ANTLR.JavaValidator import validate_java_code
from typing import Optional

from config.AppConfig import AppConfig

def validate_test_name(name: str, additional_param: str = None) -> Optional[str]:
    invalid = ["@", "[", "]", "{", "}", ";", ":", "=", ",", "."]
    if any(char in name for char in invalid):
        return None
    name = name.strip()
    if not (5 <= len(name) <= 50):
        return None
    if name == additional_param:
        return None
    return name


def validate_java_test_class(code: str) -> tuple[dict[str, str], dict[str, str]]:
    """
    Extracts and validates all @Test methods in the given Java code.
    Returns dictionaries of valid and invalid test methods.

    :param code: Java code string containing test methods.
    :return: Tuple of two dictionaries:
             {method_name: method_code} for valid methods,
             {method_name: method_code} for invalid ones.
    """

    logger = AppConfig.get_instance().setup_logger("Validator")
    logger.info("Validating java code...")

    valid_test_methods = {}
    invalid_test_methods = {}

    code = code.replace("@TEST", "@Test")
    code = remove_java_comments(code)
    logger.info("Comments have been removed...")

    test_indices = [m.start() for m in re.finditer(r'@Test\b', code)]
    logger.debug(f"Found {len(test_indices)} @Test annotations.")

    for idx in test_indices:
        method_match = re.search(r'public\s+void\s+(\w+)\s*\(', code[idx:])
        if not method_match:
            logger.debug(f"Could not find method signature after @Test at index {idx}")
            continue

        method_name = method_match.group(1)
        method_sig_start = idx + method_match.start()
        brace_open = code.find('{', method_sig_start)
        if brace_open == -1:
            logger.debug(f"Could not find opening brace for method {method_name}")
            continue

        # Find the matching closing brace using brace counting
        brace_count = 1
        i = brace_open + 1
        while i < len(code) and brace_count > 0:
            if code[i] == '{':
                brace_count += 1
            elif code[i] == '}':
                brace_count -= 1
            i += 1

        if brace_count != 0:
            logger.debug(f"Unbalanced braces in method {method_name}")
            invalid_test_methods[method_name] = code[idx:i].strip()
            continue

        method_code = code[idx:i].strip()
        body = get_code_body(method_code)

        logger.debug(f"\n[Validator] Validating @Test method:\n{method_name}\n")

        # Assume valid for now
        logger.debug(f"[Validator] ✅ Method '{method_name}' is valid.")
        valid_test_methods[method_name] = method_code

        # Uncomment below if integrating Java code validator:
        # if validate_java_code(body):
        #     valid_test_methods[method_name] = method_code
        # else:
        #     invalid_test_methods[method_name] = method_code
        #     logger.debug(f"[Validator] ❌ Method '{method_name}' is invalid.")

    if not valid_test_methods:
        logger.debug("[Validator] ⚠️ No valid @Test methods found.")

    return valid_test_methods, invalid_test_methods


def remove_java_comments(code: str) -> str:
    # Pattern to match string literals, single-line comments, and multi-line comments
    pattern = re.compile(
        r'''
        ("(?:\\.|[^"\\])*") |        # group 1: string literals
        (//[^\n]*)       |           # group 2: single-line comments
        (/\*[\s\S]*?\*/)             # group 3: multi-line comments
        ''',
        re.VERBOSE
    )

    def replacer(match):
        if match.group(1):  # string literal
            return match.group(1)
        else:
            return ''  # remove comments

    return pattern.sub(replacer, code)

def get_code_body(code: str) -> str:
    """
    A method to extract purely the method body
    :param code: The code to extract only the code from
    :param mode: The type of the extracting
    :return:
    """
    # Splitting the input string into lines
    lines = code.split("\n")

    # Defining keywords and comment indicators
    keywords = {"import", "@Test", "@Timeout", "public", "void", "Class", "{", "}"}
    comment_indicators = {"*", "/*", "*/"}

    # Helper function to check if a line should be skipped
    def should_skip(line: str) -> bool:
        stripped_line = line.strip()
        if not stripped_line:
            return True  # Skip empty lines
        if any(stripped_line.startswith(indicator) for indicator in comment_indicators):
            return True  # Don't keep javadoc prior to method

        if any(keyword in stripped_line for keyword in keywords):
            if ("[]" in stripped_line) or ("try" in stripped_line):
                return False
            else:
                return True
            # take array declaration with the format type[]
            # name = new type[] {} into account
        return False

    # Removing lines from the top
    while lines and (len(lines[0].strip()) == 0 or should_skip(lines[0])):
        lines.pop(0)

    if len(lines) > 1 and "public" in lines[1]:
        lines.pop(1)

    # Removing lines from the bottom
    while lines and (len(lines[-1].strip()) == 0 or should_skip(lines[-1])):
        lines.pop()

    # Joining the remaining lines
    return "\n".join(line.strip() for line in lines)

def basic_validation(body: str) -> bool:
    if "try" in body and "catch" not in body:
        return False
    if "catch" in body and "try" not in body:
        return False
    if any(kw in body for kw in ["@Test", "Given(", "When(", "Then("]):
        return False
    return True


def merge_test_cases(dict1, dict2):
    # Detect duplicates (for information purposes)
    logger = AppConfig.get_instance().setup_logger("Validator")

    duplicate_keys = set(dict1.keys()) & set(dict2.keys())
    if duplicate_keys:
        logger.debug(f"Duplicates detected. These test cases will be kept from the first dict: {sorted(duplicate_keys)}")

    # Add only non-duplicate keys from dict2 to dict1
    for key, value in dict2.items():
        if key not in dict1:
            dict1[key] = value

    return dict1

def extract_class_name_from_prompt(prompt_text: str, prompt_type: str) -> str:
    """
    Extract potential class name from the prompt and append 'Test' to it.
    Fallback to 'FinalGeneratedTests' if no valid class name is found.
    """

    logger = AppConfig.get_instance().setup_logger("Validator")

    match = re.search(r'\bpublic\s+(?:abstract\s+)?class\s+(\w+)', prompt_text)
    if match:
        class_name = match.group(1)
    else:
        # Fallback if class keyword is not explicitly mentioned
        # Try to catch CamelCase identifiers heuristically
        match = re.search(r'\b([A-Z][a-zA-Z0-9_]*)\b', prompt_text)
        class_name = match.group(1) if match else "FinalGeneratedTests"

    logger.debug(f"Class name: {class_name}")

    # class_name = class_name + kebab_to_camel_case(AppConfig.get_instance().model_name) + strftime("%m%d%H%M", gmtime())
    if prompt_type == "pool":
        if AppConfig.get_instance().signature is not None:
            class_name = class_name + "_" + AppConfig.get_instance().signature + "_StaticPool"
        else:
            class_name = class_name + "_StaticPool"
    else:
        if AppConfig.get_instance().attempt is None:
            class_name = class_name + kebab_to_camel_case(AppConfig.get_instance().model_name) + strftime("%m%d%H%M", gmtime())
        elif AppConfig.get_instance().signature is not None:
            class_name = class_name + "_" +  AppConfig.get_instance().signature + "_" + AppConfig.get_instance().attempt + "_GPTLLMTest"
        else:
            class_name = class_name + "_" + AppConfig.get_instance().attempt + "_GPTLLMTest"

    return class_name

def kebab_to_camel_case(kebab_str: str) -> str:
    """
    Converts a kebab-case string to CamelCase (PascalCase) convention.

    Args:
        kebab_str (str): The kebab-case string (e.g., 'my-class-name').

    Returns:
        str: The converted CamelCase string (e.g., 'MyClassName').
    """
    return ''.join(word.capitalize() for word in kebab_str.split('-'))
