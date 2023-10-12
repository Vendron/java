/*
arithmatic expressions assignment
taking arithmetic expressions and simplifying them.
TODO:
Use StringBuilder rather toString() for prettyPrint() method- this can be done by using the append() method to add to the string
Program has to look through the string and understand it, then simplify it. By taking apart the string and finding what is a double, variable, or operator. e.g. 2x + 3y(2x + 3y) = 2x^2 + 3y^2 + 6xy + 9xy
Simplify expressions such as x + x to 2x and x * x to x^2
Account for expressions such as x + x + x to 3x and x * x * x to x^3
This should be able to account for more complex expressions such as sin(x) + cos(x) or x^2 + 2x + 1 
To account for this it should use multiple subclasses to simplify the expressions.
*/

public class Arithmetic {

    // Base class for all arithmetic expressions
    abstract class ArithmeticExpression {
        public abstract StringBuilder prettyPrint();
        public abstract ArithmeticExpression simplify();
    }

    // Factory interface for creating arithmetic expressions
    static interface ExpressionFactory {
        ArithmeticExpression createVariable(String name);
        ArithmeticExpression createConstant(double value);

        ArithmeticExpression createAddition(ArithmeticExpression left, ArithmeticExpression right);
        ArithmeticExpression createMultiplication(ArithmeticExpression left, ArithmeticExpression right);
        ArithmeticExpression createDivision(ArithmeticExpression numerator, ArithmeticExpression denominator);
        ArithmeticExpression createExponentiation(ArithmeticExpression base, ArithmeticExpression exponent);

    }

    // MEF implements ExpressionFactory to create arithmetic expressions that can be simplified
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

    // Variable - e.g. x or y
    class Variable extends ArithmeticExpression {
        private String name;

        public Variable(String name) {
            this.name = name;
        }

        @Override
        public StringBuilder prettyPrint() {
            return new StringBuilder(name);
        }

        public ArithmeticExpression simplify() {
            return this;
        }
    }

    // Constant value - e.g. 5 or 7
    class Constant extends ArithmeticExpression {
        private double value;

        public Constant(double value) {
            this.value = value;
        }

        @Override
        public StringBuilder prettyPrint() {
            return new StringBuilder(Double.toString(value)); //String.valueOf -> Double.toString the difference is that Double.toString will print "NaN" and "Infinity" for Double.NaN and Double.POSITIVE_INFINITY respectively, while String.valueOf will print "null" and "Infinity".
        }

        public ArithmeticExpression simplify() {
            return this;
        }

        public double getValue() {
            return value;
        }
    }

    class CoefficientVariable extends ArithmeticExpression {
        private double coefficient;
        private Variable variable;

        public CoefficientVariable(double coefficient, Variable variable) {
            this.coefficient = coefficient;
            this.variable = variable;
        }

        @Override
        public StringBuilder prettyPrint() {
            return new StringBuilder().append(coefficient).append(variable.prettyPrint());
        }

        public ArithmeticExpression simplify() {
            return this;
        }
    }

    // Addition of two expressions - e.g. x + y or 5 + 7
    class Addition extends ArithmeticExpression {
        private ArithmeticExpression left, right;

        public Addition(ArithmeticExpression left, ArithmeticExpression right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public StringBuilder prettyPrint() {
            StringBuilder sb = new StringBuilder();
            sb.append(left.prettyPrint()).append(" + ").append(right.prettyPrint());
            return sb;
        }

        public ArithmeticExpression simplify() {
            ArithmeticExpression simplifiedLeft = left.simplify();
            ArithmeticExpression simplifiedRight = right.simplify();

            if (simplifiedLeft instanceof Multiplication && simplifiedRight instanceof Multiplication) {
                Multiplication leftMul = (Multiplication) simplifiedLeft;
                Multiplication rightMul = (Multiplication) simplifiedRight;

                if (leftMul.left instanceof Constant && rightMul.left instanceof Constant &&
                        leftMul.right.prettyPrint().equals(rightMul.right.prettyPrint())) {
                    double newCoefficient = ((Constant) leftMul.left).getValue()
                            + ((Constant) rightMul.left).getValue();
                    return new Multiplication(new Constant(newCoefficient), leftMul.right).simplify();
                }
            }

            // If the left and right are both constants, add them together
            if (simplifiedLeft instanceof Constant && simplifiedRight instanceof Constant) {
                return new Constant(((Constant) simplifiedLeft).getValue() + ((Constant) simplifiedRight).getValue());
            }

            // If left and right are both variables, add them together e.g. x + x = 2x or 7y
            // + 9y = 16y
            if (simplifiedLeft instanceof Variable && simplifiedRight instanceof Variable) {
                // check if there are any constants in the expression, if so add them together

                // check if the variables are the same
                if (simplifiedLeft.prettyPrint().equals(simplifiedRight.prettyPrint())) {
                    return new Multiplication(new Constant(2.0), simplifiedLeft);
                }
            }

            if (simplifiedLeft.prettyPrint().equals(simplifiedRight.prettyPrint())) {
                return new Multiplication(new Constant(2.0), simplifiedLeft);
            }

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

        @Override
        public StringBuilder prettyPrint() {
            StringBuilder sb = new StringBuilder();
            sb.append(left.prettyPrint()).append(" * ").append(right.prettyPrint());
            return sb;
        }

        public ArithmeticExpression simplify() {
            ArithmeticExpression simplifiedLeft = left.simplify();
            ArithmeticExpression simplifiedRight = right.simplify();

            // If both operands are Variables and are the same, e.g. x*x
            if (simplifiedLeft instanceof Variable && simplifiedRight instanceof Variable) {
                if (simplifiedLeft.prettyPrint().toString().equals(simplifiedRight.prettyPrint().toString())) {
                    return new Exponentiation(simplifiedLeft, new Constant(2.0)).simplify();
                }
            }

            // If both operands are Constants, e.g. 5*2
            if (simplifiedLeft instanceof Constant && simplifiedRight instanceof Constant) {
                double resultValue = ((Constant) simplifiedLeft).getValue() * ((Constant) simplifiedRight).getValue();
                return new Constant(resultValue);
            }

            // If one operand is a Constant and the other is a Variable, e.g. x*5.0 or 5.0*x
            if (simplifiedLeft instanceof Variable && simplifiedRight instanceof Constant) {
                double coefficient = ((Constant) simplifiedRight).getValue();
                if (coefficient == 1.0) {
                    return simplifiedLeft; // Return just the variable if coefficient is 1
                }
                return new CoefficientVariable(coefficient, (Variable) simplifiedLeft);
            }

            if (simplifiedRight instanceof Variable && simplifiedLeft instanceof Constant) {
                double coefficient = ((Constant) simplifiedLeft).getValue();
                if (coefficient == 1.0) {
                    return simplifiedRight; // Return just the variable if coefficient is 1
                }
                return new CoefficientVariable(coefficient, (Variable) simplifiedRight);
            }

            // Return multiplication if no further simplification is possible
            return new Multiplication(simplifiedLeft, simplifiedRight);
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

        @Override
        public StringBuilder prettyPrint() {
            StringBuilder sb = new StringBuilder();
            sb.append(numerator.prettyPrint()).append(" / ").append(denominator.prettyPrint());
            return sb;
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

        @Override
        public StringBuilder prettyPrint() {
            StringBuilder sb = new StringBuilder();
            sb.append(base.prettyPrint()).append(" ^ ").append(exponent.prettyPrint());
            return sb;
        }

        public ArithmeticExpression simplify() {
            ArithmeticExpression simplifiedBase = base.simplify();
            ArithmeticExpression simplifiedExponent = exponent.simplify();

            // If the base is 1, return 1.
            if (simplifiedBase instanceof Constant && ((Constant) simplifiedBase).getValue() == 1.0) {
                return new Constant(1.0);
            }
            if (simplifiedExponent instanceof Constant && ((Constant) simplifiedExponent).getValue() == 0.0) {
                return new Constant(1.0);
            }

            // If the exponent is 0, return 1.
            if (simplifiedBase instanceof Constant && ((Constant) simplifiedBase).getValue() == 0.0) {
                if (simplifiedExponent instanceof Constant && ((Constant) simplifiedExponent).getValue() > 0) {
                    return new Constant(0.0);
                } else if (simplifiedExponent instanceof Constant && ((Constant) simplifiedExponent).getValue() < 0) {
                    throw new ArithmeticException("0 raised to a negative power is undefined.");
                }
            }

            // When the exponent is 1, return the base.
            if (simplifiedExponent instanceof Constant && ((Constant) simplifiedExponent).getValue() == 1.0) {
                return simplifiedBase;
            }

            // Return exponentiation if no simplification is possible
            return new Exponentiation(simplifiedBase, simplifiedExponent);
        }

    }

    public static void main(String[] args) {
        Arithmetic arithmeticInstance = new Arithmetic();
        MinimalExpressionFactory factory = arithmeticInstance.new MinimalExpressionFactory();

        double[] numbers = { 0, 1, 2, 3, 4, 5 };
        String[] variables = { "x", "y", "z" };

        // Testing variable with number
        for (String var : variables) {
            ArithmeticExpression variable = factory.createVariable(var);
            for (double num : numbers) {
                ArithmeticExpression constant = factory.createConstant(num);
                testExpressions(factory, variable, constant);
            }
        }

        // Testing variable with variable
        for (String var1 : variables) {
            ArithmeticExpression variable1 = factory.createVariable(var1);
            for (String var2 : variables) {
                ArithmeticExpression variable2 = factory.createVariable(var2);
                testExpressions(factory, variable1, variable2);
            }
        }

        // Testing number with number
        for (double num1 : numbers) {
            ArithmeticExpression constant1 = factory.createConstant(num1);
            for (double num2 : numbers) {
                ArithmeticExpression constant2 = factory.createConstant(num2);
                testExpressions(factory, constant1, constant2);
            }
        }

        ArithmeticExpression x = factory.createVariable("x");
        ArithmeticExpression y = factory.createVariable("y");
        testExpressions(factory, factory.createAddition(factory.createMultiplication(factory.createConstant(7), x), factory.createMultiplication(factory.createConstant(9), y)));
        testExpressions(factory, factory.createAddition(factory.createMultiplication(factory.createConstant(4), x), x));
        testExpressions(factory, factory.createMultiplication(x, x));
        testExpressions(factory, factory.createMultiplication(factory.createConstant(8), factory.createConstant(3)));
        testExpressions(factory, factory.createMultiplication(factory.createMultiplication(factory.createConstant(7), x), factory.createMultiplication(factory.createConstant(9), x)));
    }

    public static void testExpressions(MinimalExpressionFactory factory, ArithmeticExpression expr) {
        System.out.println(expr.prettyPrint() + " => Simplified: " + expr.simplify().prettyPrint());
        System.out.println("////");
    }

    public static void testExpressions(MinimalExpressionFactory factory, ArithmeticExpression expr1, ArithmeticExpression expr2) {
        // Test Addition
        ArithmeticExpression addition = factory.createAddition(expr1, expr2);
        System.out.println(
                expr1.prettyPrint() + " + " + expr2.prettyPrint() + ": " + addition.prettyPrint() + " => Simplified: "
                        + addition.simplify().prettyPrint());

        // Test Multiplication
        ArithmeticExpression multiplication = factory.createMultiplication(expr1, expr2);
        System.out.println(expr1.prettyPrint() + " * " + expr2.prettyPrint() + ": " + multiplication.prettyPrint()
                + " => Simplified: "
                + multiplication.simplify().prettyPrint());

        // Test Division (avoid division by zero)
        if (!(expr2 instanceof Constant && ((Constant) expr2).getValue() == 0.0)) {
            ArithmeticExpression division = factory.createDivision(expr1, expr2);
            System.out.println(expr1.prettyPrint() + " / " + expr2.prettyPrint() + ": " + division.prettyPrint()
                    + " => Simplified: "
                    + division.simplify().prettyPrint());
        }

        // Test Exponentiation
        ArithmeticExpression exponentiation = factory.createExponentiation(expr1, expr2);
        System.out.println(expr1.prettyPrint() + " ^ " + expr2.prettyPrint() + ": " + exponentiation.prettyPrint()
                + " => Simplified: "
                + exponentiation.simplify().prettyPrint());

        System.out.println("////");
    }

}