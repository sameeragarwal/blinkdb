//Licensed to the Apache Software Foundation (ASF) under one
//or more contributor license agreements.  See the NOTICE file
//distributed with this work for additional information
//regarding copyright ownership.  The ASF licenses this file
//to you under the Apache License, Version 2.0 (the
//"License"); you may not use this file except in compliance
//with the License.  You may obtain a copy of the License at

//http://www.apache.org/licenses/LICENSE-2.0

//Unless required by applicable law or agreed to in writing,
//software distributed under the License is distributed on an
//"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
//KIND, either express or implied.  See the License for the
//specific language governing permissions and limitations
//under the License.

package org.apache.commons.math.stat.descriptive.moment;

import junit.framework.TestCase;

import org.apache.commons.math.DimensionMismatchException;
import org.apache.commons.math.TestUtils;

public class VectorialMeanTest
extends TestCase {

    public VectorialMeanTest(String name) {
        super(name);
        points = null;
    }

    public void testMismatch() {
        try {
            new VectorialMean(8).increment(new double[5]);
            fail("an exception should have been thrown");
        } catch (DimensionMismatchException dme) {
            assertEquals(5, dme.getDimension1());
            assertEquals(8, dme.getDimension2());
        }
    }

    public void testSimplistic() throws DimensionMismatchException {
        VectorialMean stat = new VectorialMean(2);
        stat.increment(new double[] {-1.0,  1.0});
        stat.increment(new double[] { 1.0, -1.0});
        double[] mean = stat.getResult();
        assertEquals(0.0, mean[0], 1.0e-12);
        assertEquals(0.0, mean[1], 1.0e-12);
    }

    public void testBasicStats() throws DimensionMismatchException {

        VectorialMean stat = new VectorialMean(points[0].length);
        for (int i = 0; i < points.length; ++i) {
            stat.increment(points[i]);
        }

        assertEquals(points.length, stat.getN());

        double[] mean = stat.getResult();
        double[]   refMean = new double[] { 1.78, 1.62,  3.12};

        for (int i = 0; i < mean.length; ++i) {
            assertEquals(refMean[i], mean[i], 1.0e-12);
        }

    }

    public void testSerial() throws DimensionMismatchException {
        VectorialMean stat = new VectorialMean(points[0].length);
        for (int i = 0; i < points.length; ++i) {
            stat.increment(points[i]);
        }
        assertEquals(stat, TestUtils.serializeAndRecover(stat));
    }
    @Override
    public void setUp() {
        points = new double[][] {
                { 1.2, 2.3,  4.5},
                {-0.7, 2.3,  5.0},
                { 3.1, 0.0, -3.1},
                { 6.0, 1.2,  4.2},
                {-0.7, 2.3,  5.0}
        };
    }

    @Override
    public void tearDown() {
        points = null;
    }

    private double [][] points;

}
