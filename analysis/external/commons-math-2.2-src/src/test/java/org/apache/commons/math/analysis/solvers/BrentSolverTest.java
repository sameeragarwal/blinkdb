/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math.analysis.solvers;

import junit.framework.TestCase;

import org.apache.commons.math.MathException;
import org.apache.commons.math.analysis.MonitoredFunction;
import org.apache.commons.math.analysis.QuinticFunction;
import org.apache.commons.math.analysis.SinFunction;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.util.FastMath;

/**
 * Testcase for UnivariateRealSolver.
 * Because Brent-Dekker is guaranteed to converge in less than the default
 * maximum iteration count due to bisection fallback, it is quite hard to
 * debug. I include measured iteration counts plus one in order to detect
 * regressions. On average Brent-Dekker should use 4..5 iterations for the
 * default absolute accuracy of 10E-8 for sinus and the quintic function around
 * zero, and 5..10 iterations for the other zeros.
 *
 * @version $Revision:670469 $ $Date:2008-06-23 10:01:38 +0200 (lun., 23 juin 2008) $
 */
public final class BrentSolverTest extends TestCase {

    public BrentSolverTest(String name) {
        super(name);
    }

    @Deprecated
    public void testDeprecated() throws MathException {
        // The sinus function is behaved well around the root at #pi. The second
        // order derivative is zero, which means linar approximating methods will
        // still converge quadratically.
        UnivariateRealFunction f = new SinFunction();
        double result;
        UnivariateRealSolver solver = new BrentSolver(f);
        // Somewhat benign interval. The function is monotone.
        result = solver.solve(3, 4);
        //System.out.println(
        //    "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, FastMath.PI, solver.getAbsoluteAccuracy());
        // 4 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 5);
        // Larger and somewhat less benign interval. The function is grows first.
        result = solver.solve(1, 4);
        //System.out.println(
        //    "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, FastMath.PI, solver.getAbsoluteAccuracy());
        // 5 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 6);
        solver = new SecantSolver(f);
        result = solver.solve(3, 4);
        //System.out.println(
        //    "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, FastMath.PI, solver.getAbsoluteAccuracy());
        // 4 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 5);
        result = solver.solve(1, 4);
        //System.out.println(
        //    "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, FastMath.PI, solver.getAbsoluteAccuracy());
        // 5 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 6);
        assertEquals(result, solver.getResult(), 0);
    }

    public void testSinZero() throws MathException {
        // The sinus function is behaved well around the root at #pi. The second
        // order derivative is zero, which means linar approximating methods will
        // still converge quadratically.
        UnivariateRealFunction f = new SinFunction();
        double result;
        UnivariateRealSolver solver = new BrentSolver();
        // Somewhat benign interval. The function is monotone.
        result = solver.solve(f, 3, 4);
        //System.out.println(
        //    "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, FastMath.PI, solver.getAbsoluteAccuracy());
        // 4 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 5);
        // Larger and somewhat less benign interval. The function is grows first.
        result = solver.solve(f, 1, 4);
        //System.out.println(
        //    "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, FastMath.PI, solver.getAbsoluteAccuracy());
        // 5 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 6);
        solver = new SecantSolver();
        result = solver.solve(f, 3, 4);
        //System.out.println(
        //    "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, FastMath.PI, solver.getAbsoluteAccuracy());
        // 4 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 5);
        result = solver.solve(f, 1, 4);
        //System.out.println(
        //    "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, FastMath.PI, solver.getAbsoluteAccuracy());
        // 5 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 6);
        assertEquals(result, solver.getResult(), 0);
    }

   public void testQuinticZero() throws MathException {
        // The quintic function has zeros at 0, +-0.5 and +-1.
        // Around the root of 0 the function is well behaved, with a second derivative
        // of zero a 0.
        // The other roots are less well to find, in particular the root at 1, because
        // the function grows fast for x>1.
        // The function has extrema (first derivative is zero) at 0.27195613 and 0.82221643,
        // intervals containing these values are harder for the solvers.
        UnivariateRealFunction f = new QuinticFunction();
        double result;
        // Brent-Dekker solver.
        UnivariateRealSolver solver = new BrentSolver();
        // Symmetric bracket around 0. Test whether solvers can handle hitting
        // the root in the first iteration.
        result = solver.solve(f, -0.2, 0.2);
        //System.out.println(
        //    "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, 0, solver.getAbsoluteAccuracy());
        assertTrue(solver.getIterationCount() <= 2);
        // 1 iterations on i586 JDK 1.4.1.
        // Asymmetric bracket around 0, just for fun. Contains extremum.
        result = solver.solve(f, -0.1, 0.3);
        //System.out.println(
        //    "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, 0, solver.getAbsoluteAccuracy());
        // 5 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 6);
        // Large bracket around 0. Contains two extrema.
        result = solver.solve(f, -0.3, 0.45);
        //System.out.println(
        //    "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, 0, solver.getAbsoluteAccuracy());
        // 6 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 7);
        // Benign bracket around 0.5, function is monotonous.
        result = solver.solve(f, 0.3, 0.7);
        //System.out.println(
        //    "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, 0.5, solver.getAbsoluteAccuracy());
        // 6 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 7);
        // Less benign bracket around 0.5, contains one extremum.
        result = solver.solve(f, 0.2, 0.6);
        //System.out.println(
        //    "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, 0.5, solver.getAbsoluteAccuracy());
        // 6 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 7);
        // Large, less benign bracket around 0.5, contains both extrema.
        result = solver.solve(f, 0.05, 0.95);
        //System.out.println(
        //    "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, 0.5, solver.getAbsoluteAccuracy());
        // 8 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 9);
        // Relatively benign bracket around 1, function is monotonous. Fast growth for x>1
        // is still a problem.
        result = solver.solve(f, 0.85, 1.25);
        //System.out.println(
        //    "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
        // 8 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 9);
        // Less benign bracket around 1 with extremum.
        result = solver.solve(f, 0.8, 1.2);
        //System.out.println(
        //    "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
        // 8 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 9);
        // Large bracket around 1. Monotonous.
        result = solver.solve(f, 0.85, 1.75);
        //System.out.println(
        //    "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
        // 10 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 11);
        // Large bracket around 1. Interval contains extremum.
        result = solver.solve(f, 0.55, 1.45);
        //System.out.println(
        //    "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
        // 7 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 8);
        // Very large bracket around 1 for testing fast growth behaviour.
        result = solver.solve(f, 0.85, 5);
        //System.out.println(
       //     "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
        // 12 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 13);
        // Secant solver.
        solver = new SecantSolver();
        result = solver.solve(f, -0.2, 0.2);
        //System.out.println(
        //    "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, 0, solver.getAbsoluteAccuracy());
        // 1 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 2);
        result = solver.solve(f, -0.1, 0.3);
        //System.out.println(
        //    "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, 0, solver.getAbsoluteAccuracy());
        // 5 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 6);
        result = solver.solve(f, -0.3, 0.45);
        //System.out.println(
        //    "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, 0, solver.getAbsoluteAccuracy());
        // 6 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 7);
        result = solver.solve(f, 0.3, 0.7);
        //System.out.println(
        //    "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, 0.5, solver.getAbsoluteAccuracy());
        // 7 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 8);
        result = solver.solve(f, 0.2, 0.6);
        //System.out.println(
        //    "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, 0.5, solver.getAbsoluteAccuracy());
        // 6 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 7);
        result = solver.solve(f, 0.05, 0.95);
        //System.out.println(
        //    "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, 0.5, solver.getAbsoluteAccuracy());
        // 8 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 9);
        result = solver.solve(f, 0.85, 1.25);
        //System.out.println(
        //    "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
        // 10 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 11);
        result = solver.solve(f, 0.8, 1.2);
        //System.out.println(
        //    "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
        // 8 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 9);
        result = solver.solve(f, 0.85, 1.75);
        //System.out.println(
        //    "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
        // 14 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 15);
        // The followig is especially slow because the solver first has to reduce
        // the bracket to exclude the extremum. After that, convergence is rapide.
        result = solver.solve(f, 0.55, 1.45);
        //System.out.println(
        //    "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
        // 7 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 8);
        result = solver.solve(f, 0.85, 5);
        //System.out.println(
        //    "Root: " + result + " Iterations: " + solver.getIterationCount());
        assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
        // 14 iterations on i586 JDK 1.4.1.
        assertTrue(solver.getIterationCount() <= 15);
        // Static solve method
        result = UnivariateRealSolverUtils.solve(f, -0.2, 0.2);
        assertEquals(result, 0, solver.getAbsoluteAccuracy());
        result = UnivariateRealSolverUtils.solve(f, -0.1, 0.3);
        assertEquals(result, 0, 1E-8);
        result = UnivariateRealSolverUtils.solve(f, -0.3, 0.45);
        assertEquals(result, 0, 1E-6);
        result = UnivariateRealSolverUtils.solve(f, 0.3, 0.7);
        assertEquals(result, 0.5, 1E-6);
        result = UnivariateRealSolverUtils.solve(f, 0.2, 0.6);
        assertEquals(result, 0.5, 1E-6);
        result = UnivariateRealSolverUtils.solve(f, 0.05, 0.95);
        assertEquals(result, 0.5, 1E-6);
        result = UnivariateRealSolverUtils.solve(f, 0.85, 1.25);
        assertEquals(result, 1.0, 1E-6);
        result = UnivariateRealSolverUtils.solve(f, 0.8, 1.2);
        assertEquals(result, 1.0, 1E-6);
        result = UnivariateRealSolverUtils.solve(f, 0.85, 1.75);
        assertEquals(result, 1.0, 1E-6);
        result = UnivariateRealSolverUtils.solve(f, 0.55, 1.45);
        assertEquals(result, 1.0, 1E-6);
        result = UnivariateRealSolverUtils.solve(f, 0.85, 5);
        assertEquals(result, 1.0, 1E-6);
    }

    public void testRootEndpoints() throws Exception {
        UnivariateRealFunction f = new SinFunction();
        UnivariateRealSolver solver = new BrentSolver();

        // endpoint is root
        double result = solver.solve(f, FastMath.PI, 4);
        assertEquals(FastMath.PI, result, solver.getAbsoluteAccuracy());

        result = solver.solve(f, 3, FastMath.PI);
        assertEquals(FastMath.PI, result, solver.getAbsoluteAccuracy());

        result = solver.solve(f, FastMath.PI, 4, 3.5);
        assertEquals(FastMath.PI, result, solver.getAbsoluteAccuracy());

        result = solver.solve(f, 3, FastMath.PI, 3.07);
        assertEquals(FastMath.PI, result, solver.getAbsoluteAccuracy());

    }

    public void testBadEndpoints() throws Exception {
        UnivariateRealFunction f = new SinFunction();
        UnivariateRealSolver solver = new BrentSolver();
        try {  // bad interval
            solver.solve(f, 1, -1);
            fail("Expecting IllegalArgumentException - bad interval");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {  // no bracket
            solver.solve(f, 1, 1.5);
            fail("Expecting IllegalArgumentException - non-bracketing");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {  // no bracket
            solver.solve(f, 1, 1.5, 1.2);
            fail("Expecting IllegalArgumentException - non-bracketing");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    public void testInitialGuess() throws MathException {

        MonitoredFunction f = new MonitoredFunction(new QuinticFunction());
        UnivariateRealSolver solver = new BrentSolver();
        double result;

        // no guess
        result = solver.solve(f, 0.6, 7.0);
        assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
        int referenceCallsCount = f.getCallsCount();
        assertTrue(referenceCallsCount >= 13);

        // invalid guess (it *is* a root, but outside of the range)
        try {
          result = solver.solve(f, 0.6, 7.0, 0.0);
          fail("an IllegalArgumentException was expected");
        } catch (IllegalArgumentException iae) {
            // expected behaviour
        }

        // bad guess
        f.setCallsCount(0);
        result = solver.solve(f, 0.6, 7.0, 0.61);
        assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
        assertTrue(f.getCallsCount() > referenceCallsCount);

        // good guess
        f.setCallsCount(0);
        result = solver.solve(f, 0.6, 7.0, 0.999999);
        assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
        assertTrue(f.getCallsCount() < referenceCallsCount);

        // perfect guess
        f.setCallsCount(0);
        result = solver.solve(f, 0.6, 7.0, 1.0);
        assertEquals(result, 1.0, solver.getAbsoluteAccuracy());
        assertEquals(0, solver.getIterationCount());
        assertEquals(1, f.getCallsCount());

    }

}
