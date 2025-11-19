from datetime import datetime, timedelta

class RequestState:
    """Represents the state of a request, including its type, start and end times, and iteration count."""

    def __init__(self, prompt_type: str) -> None:
        """
        Initializes a new instance of RequestState.

        :param prompt_type: A string indicating the type of prompt for the request.
        """
        self.prompt_type: str = prompt_type
        self.request_start_time: datetime = datetime.now()
        self.iteration_count: int = 0
        self.llm_calls: int = 0
        self.request_end_time: datetime | None = None
        self.self_improvement: int = 0

    def increment_iteration(self) -> None:
        """Increments the iteration count by 1."""
        self.iteration_count += 1

    def get_iteration(self) -> int:
        """:return: the current iteration of the request"""
        return self.iteration_count

    def increment_llm_calls(self) -> None:
        """Increments the llm calls count by 1."""
        self.llm_calls += 1

    def increment_self_improvement(self) -> None:
        """Increments the llm calls count by 1."""
        self.self_improvement += 1

    def end_request(self) -> None:
        """Records the end time of the request."""
        self.request_end_time = datetime.now()

    def is_first_run(self) -> bool:
        """
        Checks if the current iteration is the first run.

        :return: True if it's the first iteration, False otherwise.
        """
        return self.iteration_count == 1


    def get_prompt_type(self) -> str:
        """:return: A method to return the type of the prompt"""
        return self.prompt_type

    def get_duration(self) -> timedelta:
        """
        Calculates the duration of the request.

        :return: A timedelta object representing the duration of the request. If the request has ended,
                 the duration is calculated from the start time to the end time. If the request is ongoing,
                 the duration is calculated from the start time to the current time.
        """
        end_time = self.request_end_time if self.request_end_time else datetime.now()
        return end_time - self.request_start_time

    def can_be_improved(self) -> bool:
        return self.self_improvement < 6

    def __repr__(self) -> str:
        """
        Provides a string representation of the RequestState object, useful for debugging and logging.

        :return: A string representation of the RequestState object.
        """
        return (f"RequestState(\n\tprompt_type={self.prompt_type}, "
                f"\n\trequest_start_time={self.request_start_time}, "
                f"\n\titeration_count={self.iteration_count}, "
                f"\n\tllm_cals={self.llm_calls}, "
                f"\n\tself_improvement={self.self_improvement}, "
                f"\n\trequest_end_time={self.request_end_time}"
                f"\n\trequest_duration={self.get_duration().__repr__()}\n)")
