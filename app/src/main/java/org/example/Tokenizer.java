package org.example;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {
    private String formula;
    private int idx;
    boolean startedWithEquals = false,;

    void init(String formula) {
        this.formula = formula;
        idx = 0;
    }

    List<Token> tokenize(String formula) {
        List<Token> tokens = new ArrayList<Token>();

        while (hasNextToken()) {
            Token token = getNextToken();
            tokens.add(token);
        }

        return tokens;
    }

    Token getNextToken() {
        Token token;
        String value;
        TokenType type = null;

        final String string = this.formula.substring(this.idx);

        if(string.charAt(idx) == '=' && this.idx == 0) {
            startedWithEquals = true;
            value = "=";
            idx++;
            token = new Token(value, TokenType.EQUALS);
            return token;
        } 

        if(startedWithEquals) {
            for(int i = 0; i < string.length(); i++) {
                if(string.charAt(i) == '(') {
                    value = string.substring(0, i + 1);
                    idx += i + 1;
                    token = new Token(value, TokenType.FUNCTION_NAME);
                    return token;
                }
            }
            startedWithEquals = false;
        }

        if(string.charAt(idx) == '(') {
            
        }

        // CELL
        // [A-Za-z][0-9]+
        formula.split()

        // FUNCTION
        // [A-Za-z]+

        throw new RuntimeException("Unexpected Token!");
    }

    boolean hasNextToken() {
        return idx < formula.length() - 1;
    }

}
