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

import org.apache.commons.math.MathException;
import org.apache.commons.math.analysis.QuinticFunction;
import org.apache.commons.math.analysis.SinFunction;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.util.FastMath;

import junit.framework.TestCase;

/**
 * @version $Revision: 990655 $ $Date: 2010-08-29 23:49:40 +0200 (dim. 29 août 2010) $
 */
public final class BisectionSolverTest extends TestCase {

    @Deprecated
    public void testDeprecated() throws MathException {
        UnivariateRealFunction f = new SinFunction();
        double result;

        UnivariateRealSolver solver = new BisectionSolver(f);
        result = solver.solve(3, 4);
        assertEquals(result, FastMath.PI, solver.getAbsoluteAccuracy());

        result = solver.solve(1, 4);
        assertEquals(result, FastMath.PI, solver.getAbsoluteAccuracy());
    }

    public void testSinZero() throws MathException {
        UnivariateRealFunction f = new SinFunction();
        double result;

        UnivariateRealSolver solver = new BisectionSolver();
        result = solver.solve(f, 3, 4);
        assertEquals(result, FastMath.PI, solver.getAbsoluteAccuracy());

        result = solver.solve(f, 1, 4);
        assertEquals(result, FastMath.PI, solver.getAbsoluteAccuracy());
    }

   public void testQuinticZero() throws MathException {
        UnivariateRealFunction f = new QuinticFunction();
        double result;

        UnivariateRealSolver solver = new BisectionSolver();
        result = solver.solve(f, -0.2, 0.2);
        assertEquals(result, 0, solver.getAbsoluteAccuracy());

        result = solver.solve(f, -0.1, 0.3);
        assertEquals(result, 0, solver.getAbsoluteAccuracy());

        result = solver.solve(f, -0.3, 0.45);
        assertEquals(result, 0, solver.getAbsoluteAccuracy());

        result = solver.solve(f, 0.3, 0.7);
        assertEquals(result, 0.5, solver.getAbsoluteAccuracy());

        result = solver.solve(f, 0.2, 0.6);
        assertEquals(result, 0.5, solver.getAbsoluteAccuracy());

        result = solver.solve(f, 0.05, 0.95);
        assertEquals(result, 0.5, solver.getAbsoluteAccuracy());

        result = solver.solve(f, 0.85, 1.25);
        assertEquals(result, 1.0, solver.getAbsoluteAccuracy());

        result = solver.solve(f, 0.8, 1.2);
        assertEquals(result, 1.0, solver.getAbsoluteAccuracy());

        result = solver.solve(f, 0.85, 1.75);
        assertEquals(result, 1.0, solver.getAbsoluteAccuracy());

        result = solver.solve(f, 0.55, 1.45);
        assertEquals(result, 1.0, solver.getAbsoluteAccuracy());

        result = solver.solve(f, 0.85, 5);
        assertEquals(result, 1.0, solver.getAbsoluteAccuracy());

        assertEquals(result, solver.getResult(), 0);
        assertTrue(solver.getIterationCount() > 0);
    }

    public void testMath369() throws Exception {
        UnivariateRealFunction f = new SinFunction();
        UnivariateRealSolver solver = new BisectionSolver();
        assertEquals(FastMath.PI, solver.solve(f, 3.0, 3.2, 3.1), solver.getAbsoluteAccuracy());
    }

    /**
     *
     */
    public void testSetFunctionValueAccuracy(){
        double expected = 1.0e-2;
        UnivariateRealSolver solver = new BisectionSolver();
        solver.setFunctionValueAccuracy(expected);
        assertEquals(expected, solver.getFunctionValueAccuracy(), 1.0e-2);
    }

    /**
     *
     */
    public void testResetFunctionValueAccuracy(){
        double newValue = 1.0e-2;
        UnivariateRealSolver solver = new BisectionSolver();
        double oldValue = solver.getFunctionValueAccuracy();
        solver.setFunctionValueAccuracy(newValue);
        solver.resetFunctionValueAccuracy();
        assertEquals(oldValue, solver.getFunctionValueAccuracy(), 1.0e-2);
    }

    /**
     *
     */
    public void testSetAbsoluteAccuracy(){
        double expected = 1.0e-2;
        UnivariateRealSolver solver = new BisectionSolver();
        solver.setAbsoluteAccuracy(expected);
        assertEquals(expected, solver.getAbsoluteAccuracy(), 1.0e-2);
    }

    /**
     *
     */
    public void testResetAbsoluteAccuracy(){
        double newValue = 1.0e-2;
        UnivariateRealSolver solver = new BisectionSolver();
        double oldValue = solver.getAbsoluteAccuracy();
        solver.setAbsoluteAccuracy(newValue);
        solver.resetAbsoluteAccuracy();
        assertEquals(oldValue, solver.getAbsoluteAccuracy(), 1.0e-2);
    }

    /**
     *
     */
    public void testSetMaximalIterationCount(){
        int expected = 100;
        UnivariateRealSolver solver = new BisectionSolver();
        solver.setMaximalIterationCount(expected);
        assertEquals(expected, solver.getMaximalIterationCount());
    }

    /**
     *
     */
    public void testResetMaximalIterationCount(){
        int newValue = 10000;
        UnivariateRealSolver solver = new BisectionSolver();
        int oldValue = solver.getMaximalIterationCount();
        solver.setMaximalIterationCount(newValue);
        solver.resetMaximalIterationCount();
        assertEquals(oldValue, solver.getMaximalIterationCount());
    }

    /**
     *
     */
    public void testSetRelativeAccuracy(){
        double expected = 1.0e-2;
        UnivariateRealSolver solver = new BisectionSolver();
        solver.setRelativeAccuracy(expected);
        assertEquals(expected, solver.getRelativeAccuracy(), 1.0e-2);
    }

    /**
     *
     */
    public void testResetRelativeAccuracy(){
        double newValue = 1.0e-2;
        UnivariateRealSolver solver = new BisectionSolver();
        double oldValue = solver.getRelativeAccuracy();
        solver.setRelativeAccuracy(newValue);
        solver.resetRelativeAccuracy();
        assertEquals(oldValue, solver.getRelativeAccuracy(), 1.0e-2);
    }


}
