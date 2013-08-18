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
package org.apache.commons.math.optimization.univariate;

import org.apache.commons.math.MathException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.optimization.GoalType;

import org.junit.Assert;
import org.junit.Test;

public class BracketFinderTest {

    @Test
    public void testCubicMin() throws MathException {
        final BracketFinder bFind = new BracketFinder();
        final UnivariateRealFunction func = new UnivariateRealFunction() {
                public double value(double x) {
                    if (x < -2) {
                        return value(-2);
                    }
                    else  {
                        return (x - 1) * (x + 2) * (x + 3);
                    }
                }
            };

        bFind.search(func, GoalType.MINIMIZE, -2 , -1);
        final double tol = 1e-15;
        // Comparing with results computed in Python.
        Assert.assertEquals(-2, bFind.getLo(), tol);
        Assert.assertEquals(-1, bFind.getMid(), tol);
        Assert.assertEquals(0.61803399999999997, bFind.getHi(), tol);
    }

    @Test
    public void testCubicMax() throws MathException {
        final BracketFinder bFind = new BracketFinder();
        final UnivariateRealFunction func = new UnivariateRealFunction() {
                public double value(double x) {
                    if (x < -2) {
                        return value(-2);
                    }
                    else  {
                        return -(x - 1) * (x + 2) * (x + 3);
                    }
                }
            };

        bFind.search(func, GoalType.MAXIMIZE, -2 , -1);
        final double tol = 1e-15;
        Assert.assertEquals(-2, bFind.getLo(), tol);
        Assert.assertEquals(-1, bFind.getMid(), tol);
        Assert.assertEquals(0.61803399999999997, bFind.getHi(), tol);
    }
}
