from antlr4 import *
from ANTLR.Java20Lexer import Java20Lexer
from ANTLR.Java20Parser import Java20Parser
from antlr4.error.ErrorStrategy import BailErrorStrategy

def validate_java_code(code: str) -> bool:
    """
    A function to utilize the ANTLR parser to validate a piece of java code
    :param code: the code to be validated
    :return: whether the code is valid
    """
    augmented_code = """public void main(){\n""" + code + """\n}"""

    try:
        input_stream = InputStream(augmented_code)
        lexer = Java20Lexer(input_stream)
        token_stream = CommonTokenStream(lexer)
        parser = Java20Parser(token_stream)

        parser._errHandler = BailErrorStrategy()

        parser.methodDeclaration()

        return True

    except:
        return False
