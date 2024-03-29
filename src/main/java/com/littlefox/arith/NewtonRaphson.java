package com.littlefox.arith;

import java.util.function.DoubleUnaryOperator;

/**
 * Simple implementation of the Newton-Raphson method for finding roots or
 * inverses of a function.
 * <p>
 * The function and its derivative must be supplied as instances of
 * DoubleUnaryOperator and the answers are computed as doubles.
 * <p>
 * For examples of usage, see the source of the test class or the Xirr class.
 * <p>
 * The <code>iterations</code> parameter is used as an upper bound on the number
 * of iterations to run the method for.
 * <p>
 * The <code>tolerance</code> parameter is used to determine when the method
 * has been successful.  If the value of the function at the candidate input
 * is within the <code>tolerance</code> of the desired target value, the
 * method terminates.
 */
public class NewtonRaphson {
    /** Default tolerance. */
    public static final double TOLERANCE = 0.000_000_1;

    public static boolean IsSout = false;


    /**
     * Convenience method for getting an instance of a {@link Builder}.
     * @return new Builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * function,f(x)
     */
    private final DoubleUnaryOperator func;

    /**
     * derivative,f'(x)
     */
    private final DoubleUnaryOperator derivative;

    /**
     * second derivative,f''(x)
     */
    private final DoubleUnaryOperator second_derivative;

    private final double tolerance;
    private final long iterations;

    /**
     * Construct an instance of the NewtonRaphson method for masochists who
     * do not want to use {@link #builder()}.
     * @param func the function
     * @param derivative the derivative of the function
     * @param tolerance the tolerance
     * @param iterations maximum number of iterations
     */
    public NewtonRaphson(
        DoubleUnaryOperator func,
        DoubleUnaryOperator derivative,
        DoubleUnaryOperator second_derivative,
        double tolerance,
        long iterations) {
        this.func = func;
        this.derivative = derivative;
        this.second_derivative = second_derivative;
        this.tolerance = tolerance;
        this.iterations = iterations;
    }

    /**
     * Equivalent to <code>inverse(0, guess)</code>.
     * <p>
     * Find a root of the function starting at the given guess.  Equivalent to
     * invoking <code>inverse(0, guess)</code>.  Finds the input value <i>x</i>
     * such that |<i>f</i>(<i>x</i>)| &lt; <i>tolerance</i>.
     * @param guess the value to start at
     * @return an input to the function which yields zero within the given
     *         tolerance
     * @see #inverse(double, double) 
     */
    public double findRoot(final double guess) {
        return inverse(0, guess);
    }

    /**
     * 给出收益值，计算收益率
     * @param guess
     * @param target
     * @return
     */
    public double findRoot(final double guess, double target) {
        return inverse(target, guess);
    }

    /**
     * Find the input value to the function which yields the given
     * <code>target</code>, starting at the <code>guess</code>.  More precisely,
     * finds an input value <i>x</i>
     * such that |<i>f</i>(<i>x</i>) - <code>target</code>| &lt; <i>tolerance</i>
     * @param target the target value of the function
     * @param guess value to start the algorithm with
     * @return the inverse of the function at <code>target</code> within the
     * given tolerance
     * @throws ZeroValuedDerivativeException if the derivative is 0 while
     *                                       executing the Newton-Raphson method
     * @throws NonconvergenceException if the method fails to converge in the
     *                                 given number of iterations
     */
    public double inverse(final double target, final double guess) {
        /**
         * x        candidate
         * f(x)     value
         * f'(x)    slope
         * f''(x)   curvature
         */
        double candidate = guess;
        for (long i = 0; i < iterations; i++) {
            double value = func.applyAsDouble(candidate) - target;
            if (Math.abs(value) < tolerance) {
                if(IsSout) System.out.println("=>[y:f(y)]"+candidate+":"+value);
                return candidate;
            } else {
                double slope = derivative.applyAsDouble(candidate);
                if (slope == 0.0) {
                    throw new ZeroValuedDerivativeException(
                        guess, i, candidate, value);
                }
                if(IsSout) {
                    double curvature = second_derivative.applyAsDouble(candidate);
                    System.out.println("[y:f(y):f'(y):f''(y)]\t"+candidate+"\t"+value+"\t"+slope+"\t"+curvature);
                    //验证收敛，需满足 f'(x)*f'(x)-|f(x)*f''(x)/2|>0
                    double convergence = slope * slope - Math.abs(value * curvature) * 0.5;
                    System.out.println("收敛判定："+convergence);
                    System.out.println("收敛判定："+curvature*value);
                }
                candidate -= value / slope;
            }
        }
        throw new NonconvergenceException(guess, iterations);
    }

    /**
     * Builder for {@link NewtonRaphson} instances.
     */
    public static class Builder {

        private DoubleUnaryOperator func;
        private DoubleUnaryOperator derivative;
        private DoubleUnaryOperator second_derivative;

        private double tolerance = TOLERANCE;
//        private long iterations = 10_000_000_00;
        private long iterations = 1_000;

        public Builder() {
        }

        public Builder withFunction(DoubleUnaryOperator func) {
            this.func = func;
            return this;
        }

        public Builder withDerivative(DoubleUnaryOperator derivative) {
            this.derivative = derivative;
            return this;
        }

        public Builder withSecondDerivative(DoubleUnaryOperator second_derivative) {
            this.second_derivative = second_derivative;
            return this;
        }

        public Builder withTolerance(double tolerance) {
            this.tolerance = tolerance;
            return this;
        }

        public Builder withIterations(long iterations) {
            this.iterations = iterations;
            return this;
        }

        public NewtonRaphson build() {
            return new NewtonRaphson(func, derivative, second_derivative, tolerance, iterations);
        }

        /**
         * Convenience method which builds the NewtonRaphson instance and
         * invokes {@link NewtonRaphson#findRoot(double)}.
         * @param guess see {@link NewtonRaphson#findRoot(double)}
         * @return see {@link NewtonRaphson#findRoot(double)}
         */
        public double findRoot(double guess) {
            return build().findRoot(guess);
        }

        /**
         * 给出收益，计算收益率
         * @param guess
         * @param target
         * @return
         */
        public double findRoot(double guess, double target) {
            return build().findRoot(guess,target);
        }
    }
}
