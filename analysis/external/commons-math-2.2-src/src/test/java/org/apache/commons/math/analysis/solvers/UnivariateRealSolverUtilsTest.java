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
import org.apache.commons.math.analysis.SinFunction;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.util.FastMath;

/**
 * @version $Revision: 1003901 $ $Date: 2010-10-03 00:11:04 +0200 (dim. 03 oct. 2010) $
 */
public class UnivariateRealSolverUtilsTest extends TestCase {

    protected UnivariateRealFunction sin = new SinFunction();

    public void testSolveNull() throws MathException {
        try {
            UnivariateRealSolverUtils.solve(null, 0.0, 4.0);
            fail();
        } catch(IllegalArgumentException ex){
            // success
        }
    }

    public void testSolveBadEndpoints() throws MathException {
        try { // bad endpoints
            UnivariateRealSolverUtils.solve(sin, -0.1, 4.0, 4.0);
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    public void testSolveBadAccuracy() throws MathException {
        try { // bad accuracy
            UnivariateRealSolverUtils.solve(sin, 0.0, 4.0, 0.0);
//             fail("Expecting IllegalArgumentException"); // TODO needs rework since convergence behaviour was changed
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    public void testSolveSin() throws MathException {
        double x = UnivariateRealSolverUtils.solve(sin, 1.0, 4.0);
        assertEquals(FastMath.PI, x, 1.0e-4);
    }

    public void testSolveAccuracyNull()  throws MathException {
        try {
            double accuracy = 1.0e-6;
            UnivariateRealSolverUtils.solve(null, 0.0, 4.0, accuracy);
            fail();
        } catch(IllegalArgumentException ex){
            // success
        }
    }

    public void testSolveAccuracySin() throws MathException {
        double accuracy = 1.0e-6;
        double x = UnivariateRealSolverUtils.solve(sin, 1.0,
                4.0, accuracy);
        assertEquals(FastMath.PI, x, accuracy);
    }

    public void testSolveNoRoot() throws MathException {
        try {
            UnivariateRealSolverUtils.solve(sin, 1.0, 1.5);
            fail("Expecting IllegalArgumentException ");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    public void testBracketSin() throws MathException {
        double[] result = UnivariateRealSolverUtils.bracket(sin,
                0.0, -2.0, 2.0);
        assertTrue(sin.value(result[0]) < 0);
        assertTrue(sin.value(result[1]) > 0);
    }

    public void testBracketEndpointRoot() throws MathException {
        double[] result = UnivariateRealSolverUtils.bracket(sin, 1.5, 0, 2.0);
        assertEquals(0.0, sin.value(result[0]), 1.0e-15);
        assertTrue(sin.value(result[1]) > 0);
    }

    public void testNullFunction() throws MathException {
        try { // null function
            UnivariateRealSolverUtils.bracket(null, 1.5, 0, 2.0);
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }
    
    public void testBadInitial() throws MathException {
        try { // initial not between endpoints
            UnivariateRealSolverUtils.bracket(sin, 2.5, 0, 2.0);
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }
    
    public void testBadEndpoints() throws MathException {
        try { // endpoints not valid
            UnivariateRealSolverUtils.bracket(sin, 1.5, 2.0, 1.0);
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }
    
    public void testBadMaximumIterations() throws MathException {
        try { // bad maximum iterations
            UnivariateRealSolverUtils.bracket(sin, 1.5, 0, 2.0, 0);
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

}
