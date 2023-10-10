/*
arithmatic expressions assignment
taking arithmetic expressions and simplifying them.
*/

public class Arithmetic {
    /*
     * Base class for all arithmetic expressions
     */
    abstract class ArithmeticExpression {

        public abstract String prettyPrint();

        public abstract ArithmeticExpression simplify();
    }

    // Factory for creating arithmetic expressions
    interface ExpressionFactory {
        ArithmeticExpression createVariable(String name);

        ArithmeticExpression createConstant(double value);

        ArithmeticExpression createAddition(ArithmeticExpression left, ArithmeticExpression right);

        ArithmeticExpression createMultiplication(ArithmeticExpression left, ArithmeticExpression right);

        ArithmeticExpression createDivision(ArithmeticExpression numerator, ArithmeticExpression denominator);

        ArithmeticExpression createExponentiation(ArithmeticExpression base, ArithmeticExpression exponent);

    }

    // Factory for creating arithmetic expressions
    class MinimalExpressionFactory implements ExpressionFactory {
        public ArithmeticExpression createVariable(String name) {
            return new Variable(name);
        }

        public ArithmeticExpression createConstant(double value) {
            return new Constant(value);
        }

        public ArithmeticExpression createAddition(ArithmeticExpression left, ArithmeticExpression right) {
            return new Addition(left, right);
        }

        public ArithmeticExpression createMultiplication(ArithmeticExpression left, ArithmeticExpression right) {
            return new Multiplication(left, right);
        }

        public ArithmeticExpression createDivision(ArithmeticExpression numerator, ArithmeticExpression denominator) {
            return new Division(numerator, denominator);
        }

        public ArithmeticExpression createExponentiation(ArithmeticExpression base, ArithmeticExpression exponent) {
            return new Exponentiation(base, exponent);
        }
    }

    // Variable expression - e.g. x, y, z
    class Variable extends ArithmeticExpression {
        private String name;

        public Variable(String name) {
            this.name = name;
        }

        public String prettyPrint() {
            return name;
        }

        public ArithmeticExpression simplify() {
            return this;
        }
    }

    // Constant expression - e.g. 1, 2, 3
    class Constant extends ArithmeticExpression {
        private double value;

        public Constant(double value) {
            this.value = value;
        }

        public String prettyPrint() {
            return Double.toString(value);
        }

        public ArithmeticExpression simplify() {
            return this;
        }

        public double getValue() {
            return value;
        }
    }

    // Addition of two expressions, uses the ExpressionFactory to create new
    // expressions
    class Addition extends ArithmeticExpression {
        private ArithmeticExpression left, right;

        public Addition(ArithmeticExpression left, ArithmeticExpression right) {
            this.left = left;
            this.right = right;
        }

        public String prettyPrint() {
            return left.prettyPrint() + " + " + right.prettyPrint();
        }

        public ArithmeticExpression simplify() {
            ArithmeticExpression simplifiedLeft = left.simplify();
            ArithmeticExpression simplifiedRight = right.simplify();

            if (simplifiedLeft instanceof Constant && ((Constant) simplifiedLeft).getValue() == 0.0) {
                return simplifiedRight;
            }
            if (simplifiedRight instanceof Constant && ((Constant) simplifiedRight).getValue() == 0.0) {
                return simplifiedLeft;
            }
            return new Addition(simplifiedLeft, simplifiedRight); // Return addition if no simplification is possible
        }

    }

    class Multiplication extends ArithmeticExpression {
        private ArithmeticExpression left, right;

        public Multiplication(ArithmeticExpression left, ArithmeticExpression right) {
            this.left = left;
            this.right = right;
        }

        public String prettyPrint() {
            return left.prettyPrint() + " * " + right.prettyPrint();
        }

        public ArithmeticExpression simplify() {
            ArithmeticExpression simplifiedLeft = left.simplify();
            ArithmeticExpression simplifiedRight = right.simplify();

            if (simplifiedLeft instanceof Constant) {
                double leftValue = ((Constant) simplifiedLeft).getValue();
                if (leftValue == 0.0)
                    return new Constant(0.0);
                if (leftValue == 1.0)
                    return simplifiedRight;
            }

            if (simplifiedRight instanceof Constant) {
                double rightValue = ((Constant) simplifiedRight).getValue();
                if (rightValue == 0.0)
                    return new Constant(0.0);
                if (rightValue == 1.0)
                    return simplifiedLeft;
            }

            return new Multiplication(simplifiedLeft, simplifiedRight); // Return multiplication if no simplification is
                                                                        // possible
        }

    }

    // Extension Example 1
    // Division of two expressions - e.g. x / y
    class Division extends ArithmeticExpression {
        private ArithmeticExpression numerator, denominator;

        public Division(ArithmeticExpression numerator, ArithmeticExpression denominator) {
            this.numerator = numerator;
            this.denominator = denominator;
        }

        public String prettyPrint() {
            return numerator.prettyPrint() + " / " + denominator.prettyPrint();
        }

        public ArithmeticExpression simplify() {
            // Implement simplification logic
            ArithmeticExpression simplifiedNumerator = numerator.simplify();
            ArithmeticExpression simplifiedDenominator = denominator.simplify();

            // If the numerator is 0, return 0.
            if (simplifiedNumerator instanceof Constant && ((Constant) simplifiedNumerator).getValue() == 0.0) {
                return new Constant(0.0);
            }

            // If the denominator is 1, return the numerator.
            if (simplifiedDenominator instanceof Constant && ((Constant) simplifiedDenominator).getValue() == 1.0) {
                return simplifiedNumerator;
            }

            // If the numerator and denominator are the same, return 1.
            if (simplifiedNumerator.prettyPrint().equals(simplifiedDenominator.prettyPrint())) {
                return new Constant(1.0);
            }

            return new Division(simplifiedNumerator, simplifiedDenominator); // Return division if no simplification is
                                                                             // possible
        }
    }

    // Extension Example 2
    // This is more complex than division
    // Exponetatiation of two expressions - e.g. x ^ y

    class Exponentiation extends ArithmeticExpression {
        private ArithmeticExpression base, exponent;

        public Exponentiation(ArithmeticExpression base, ArithmeticExpression exponent) {
            this.base = base;
            this.exponent = exponent;
        }

        public String prettyPrint() {
            return base.prettyPrint() + "^" + exponent.prettyPrint();
        }

        public ArithmeticExpression simplify() {
            ArithmeticExpression simplifiedBase = base.simplify();
            ArithmeticExpression simplifiedExponent = exponent.simplify();

            // If the base is 1 or the exponent is 0, return 1.
            if (simplifiedBase instanceof Constant && ((Constant) simplifiedBase).getValue() == 1.0) {
                return new Constant(1.0);
            }
            if (simplifiedExponent instanceof Constant && ((Constant) simplifiedExponent).getValue() == 0.0) {
                return new Constant(1.0);
            }

            // If the base is 0, return 0.
            if (simplifiedBase instanceof Constant && ((Constant) simplifiedBase).getValue() == 0.0) {
                return new Constant(0.0);
            }

            // If the exponent is 1, return the base.
            if (simplifiedExponent instanceof Constant && ((Constant) simplifiedExponent).getValue() == 1.0) {
                return simplifiedBase;
            }

            return new Exponentiation(simplifiedBase, simplifiedExponent); // Return exponentiation if no simplification
                                                                           // is possible
        }
    }

    public static void main(String[] args) {
        System.out.println("Hello World!");
    }
}