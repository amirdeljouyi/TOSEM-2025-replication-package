import os
import strawberry
from RequestState import RequestState
from llm.LLMService import LLMService
from schema import Prompt, Response
from config.AppConfig import AppConfig
from fastapi import FastAPI
from strawberry.fastapi import GraphQLRouter

@strawberry.type
class Query:
    @strawberry.field
    def prompt(self, prompt: Prompt) -> Response:
        """
        Main entrypoint for handling LLM prompt requests via GraphQL.

        :param prompt: User input wrapped in Prompt type.
        :return: Processed LLM output wrapped in Response type.
        """
        request_state = RequestState(prompt.prompt_type)  # Track request state
        llm_service = LLMService(prompt, request_state)  # Instantiate service to handle process
        return llm_service.process_prompt()  # Execute full LLM pipeline

AppConfig.init_once()
schema = strawberry.Schema(query=Query)
graphql_app = GraphQLRouter(schema)
app = FastAPI()  # ✅ This is the ASGI app that uvicorn expects
app.include_router(graphql_app, prefix="/graphql")  # Now available at /graphql


# ----------- CLI Handling for Different Properties ----------- #
if __name__ == "__main__":
    import uvicorn
    import argparse

    parser = argparse.ArgumentParser(description="Run Strawberry GraphQL LLM API with dynamic model selection.")
    parser.add_argument("--model", type=str, help="LLM model to use (default: gpt-4o-mini)", default="gpt-4o-mini")
    parser.add_argument("--write_responses", type=bool, help="Write the responses into a file", default=False)
    parser.add_argument("--write_failed_responses", type=bool, help="Write the failed responses into a file", default=False)
    parser.add_argument("--log_to_file", type=bool, help="Write the logs", default=False)
    parser.add_argument("--log_level", type=str, help="Log Level", default="INFO")
    parser.add_argument("--host", type=str, help="Host for API server (default: 0.0.0.0)", default="0.0.0.0")
    parser.add_argument("--port", type=int, help="Port for API server (default: 8000)", default=8000)
    parser.add_argument("--attempt", type=int, help="Attempt (for naming)")
    parser.add_argument("--signature", type=str, help="signature (for naming)")
    args = parser.parse_args()

    os.environ["MODEL_NAME"] = args.model
    os.environ["WRITE_RESPONSES"] = str(args.write_responses)
    os.environ["WRITE_FAILED_RESPONSES"] = str(args.write_failed_responses)
    os.environ["LOG_TO_FILE"] = str(args.log_to_file)
    os.environ["LOG_LEVEL"] = str(args.log_level)
    os.environ["ATTEMPT"] = str(args.attempt)
    os.environ["SIGNATURE"] = str(args.signature)

    logger = AppConfig.get_instance().setup_logger("Main")
    logger.info(f"\n🚀 Starting server with model: {args.model}\n")

    uvicorn.run("main:app", host=args.host, port=args.port, reload=True)