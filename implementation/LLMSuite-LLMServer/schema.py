import strawberry
from typing import Optional

@strawberry.input
class Prompt:
    prompt_text: str
    prompt_type: Optional[str] = None
    additional_param: Optional[str] = None


@strawberry.type
class Response:
    llm_response: str