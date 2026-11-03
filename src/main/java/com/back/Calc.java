package com.back;

public class Calc {
    // 연산자 덩어리는 앞뒤 공백까지 3글자: " + ", " - ", " * "
    private static final int OPERATOR_LENGTH = 3;

    public static int run(String expression) {
        String expr = expression.trim();

        // 우선순위가 낮은 연산자(+, -)에서 먼저 자른다.
        // 가장 나중에 계산할 연산이 가장 바깥에서 잘려야, 곱셈이 안쪽(먼저)에서 계산된다.
        int plusMinusStart = findLastOperatorOutsideParens(expr, " + ", " - ");
        if (plusMinusStart != -1) {
            return calculateAt(expr, plusMinusStart);
        }

        int multiplyStart = findLastOperatorOutsideParens(expr, " * ");
        if (multiplyStart != -1) {
            return calculateAt(expr, multiplyStart);
        }

        // "-10", "-(10 + 5)" 처럼 공백 없이 붙은 '-' 는 빼기가 아니라 음수 부호
        if (expr.startsWith("-")) {
            int afterSign = 1;
            return -run(expr.substring(afterSign));
        }

        // 괄호 밖 연산자가 없는데 '(' 로 시작하면 식 전체가 괄호 하나로 감싸인 것
        if (expr.startsWith("(")) {
            return run(removeOuterParens(expr));
        }

        return Integer.parseInt(expr);
    }

    // 괄호 밖(깊이 0)에서 가장 오른쪽 연산자 덩어리의 시작 위치(앞 공백 칸). 없으면 -1.
    // 오른쪽부터 찾는 이유: 같은 우선순위는 왼쪽부터 계산하므로 마지막 연산자가 가장 나중에 계산된다.
    private static int findLastOperatorOutsideParens(String expr, String... operators) {
        int depth = 0;

        for (int i = expr.length() - 1; i >= 0; i--) {
            char c = expr.charAt(i);

            if (c == ')') {
                depth++;
            } else if (c == '(') {
                depth--;
            } else if (depth == 0 && isOperatorAt(expr, i, operators)) {
                return i;
            }
        }

        return -1;
    }

    private static boolean isOperatorAt(String expr, int index, String... operators) {
        for (String operator : operators) {
            if (expr.startsWith(operator, index)) {
                return true;
            }
        }

        return false;
    }

    private static int calculateAt(String expr, int operatorStart) {
        int symbolIndex = operatorStart + 1; // 앞 공백 다음 칸이 기호
        int rightStart = operatorStart + OPERATOR_LENGTH;

        int left = run(expr.substring(0, operatorStart));
        int right = run(expr.substring(rightStart));

        return switch (expr.charAt(symbolIndex)) {
            case '+' -> left + right;
            case '-' -> left - right;
            case '*' -> left * right;
            default -> throw new IllegalArgumentException("지원하지 않는 연산자: " + expr.charAt(symbolIndex));
        };
    }

    private static String removeOuterParens(String expr) {
        int innerStart = 1;
        int innerEnd = expr.length() - 1;

        return expr.substring(innerStart, innerEnd);
    }
}
