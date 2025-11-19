import requests
import json
import argparse


def read_java_file(file_path: str) -> str:
    """
    Reads the content of a Java file.

    :param file_path: Path to the Java file.
    :return: Content of the file as a string.
    """
    with open(file_path, 'r', encoding='utf-8') as file:
        return file.read()


def send_to_strawberry_api(api_url: str, java_code: str) -> str:
    """
    Sends the Java code to the Strawberry GraphQL API as a 'generate_test_class' prompt.

    :param api_url: URL of the running Strawberry API (GraphQL endpoint).
    :param java_code: The Java class code to be sent.
    :return: The LLM-generated test class or an error message.
    """

    # GraphQL mutation/query string
    query = """
    query GenerateTestClass($prompt: Prompt!) {
        prompt(prompt: $prompt) {
            llmResponse
        }
    }
    """

    # GraphQL variables
    variables = {
        "prompt": {
            "promptText": java_code,
            "promptType": "pool",
            "additionalParam": None
        }
    }

    # Making the POST request to Strawberry GraphQL API
    try:
        response = requests.post(
            api_url,
            json={"query": query, "variables": variables},
            headers={"Content-Type": "application/json"},
            timeout=5000  # Timeout can be adjusted
        )
        response.raise_for_status()  # Raises an error for bad status codes
    except requests.RequestException as e:
        print(f"❌ Failed to reach API: {e}")
        return None

    # Extracting LLM response from GraphQL JSON response
    response_data = response.json()
    if "errors" in response_data:
        print("❌ Errors from API:", response_data["errors"])
        return None

    return response_data["data"]["prompt"]["llmResponse"]


def main():
    parser = argparse.ArgumentParser(description="Send Java class file to Strawberry API for test class generation.")
    parser.add_argument("file_path", type=str, help="Path to the Java file.")
    parser.add_argument("--api-url", type=str, default="http://localhost:8000/graphql",
                        help="URL of the Strawberry API endpoint.")
    args = parser.parse_args()

    # Step 1: Read Java file
    java_code = read_java_file(args.file_path)
    print(f"\n📄 Successfully read Java file: {args.file_path}\n")

    # Step 2: Send to Strawberry API
    test_class = send_to_strawberry_api(args.api_url, java_code)

    # Step 3: Output response
    if test_class:
        print("\n✅ Generated Test Class:\n")
        print(test_class)
    else:
        print("\n❌ Failed to generate test class.")


if __name__ == "__main__":
    main()