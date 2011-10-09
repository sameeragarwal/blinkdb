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
package org.apache.commons.math.stat.correlation;

import org.apache.commons.math.TestUtils;
import org.apache.commons.math.linear.BlockRealMatrix;
import org.apache.commons.math.linear.RealMatrix;

/**
 * Test cases for Spearman's rank correlation
 *
 * @since 2.0
 * @version $Revision: 811685 $ $Date: 2009-09-05 19:36:48 +0200 (sam. 05 sept. 2009) $
 */
public class SpearmansRankCorrelationTest extends PearsonsCorrelationTest {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test Longley dataset against R.
     */
    @Override
    public void testLongly() throws Exception {
        RealMatrix matrix = createRealMatrix(longleyData, 16, 7);
        SpearmansCorrelation corrInstance = new SpearmansCorrelation(matrix);
        RealMatrix correlationMatrix = corrInstance.getCorrelationMatrix();
        double[] rData = new double[] {
                1, 0.982352941176471, 0.985294117647059, 0.564705882352941, 0.2264705882352941, 0.976470588235294,
                0.976470588235294, 0.982352941176471, 1, 0.997058823529412, 0.664705882352941, 0.2205882352941176,
                0.997058823529412, 0.997058823529412, 0.985294117647059, 0.997058823529412, 1, 0.638235294117647,
                0.2235294117647059, 0.9941176470588236, 0.9941176470588236, 0.564705882352941, 0.664705882352941,
                0.638235294117647, 1, -0.3411764705882353, 0.685294117647059, 0.685294117647059, 0.2264705882352941,
                0.2205882352941176, 0.2235294117647059, -0.3411764705882353, 1, 0.2264705882352941, 0.2264705882352941,
                0.976470588235294, 0.997058823529412, 0.9941176470588236, 0.685294117647059, 0.2264705882352941, 1, 1,
                0.976470588235294, 0.997058823529412, 0.9941176470588236, 0.685294117647059, 0.2264705882352941, 1, 1
        };
        TestUtils.assertEquals("Spearman's correlation matrix", createRealMatrix(rData, 7, 7), correlationMatrix, 10E-15);
    }

    /**
     * Test R swiss fertility dataset.
     */
    public void testSwiss() throws Exception {
        RealMatrix matrix = createRealMatrix(swissData, 47, 5);
        SpearmansCorrelation corrInstance = new SpearmansCorrelation(matrix);
        RealMatrix correlationMatrix = corrInstance.getCorrelationMatrix();
        double[] rData = new double[] {
                1, 0.2426642769364176, -0.660902996352354, -0.443257690360988, 0.4136455623012432,
                0.2426642769364176, 1, -0.598859938748963, -0.650463814145816, 0.2886878090882852,
               -0.660902996352354, -0.598859938748963, 1, 0.674603831406147, -0.4750575257171745,
               -0.443257690360988, -0.650463814145816, 0.674603831406147, 1, -0.1444163088302244,
                0.4136455623012432, 0.2886878090882852, -0.4750575257171745, -0.1444163088302244, 1
        };
        TestUtils.assertEquals("Spearman's correlation matrix", createRealMatrix(rData, 5, 5), correlationMatrix, 10E-15);
    }

    /**
     * Constant column
     */
    @Override
    public void testConstant() {
        double[] noVariance = new double[] {1, 1, 1, 1};
        double[] values = new double[] {1, 2, 3, 4};
        assertTrue(Double.isNaN(new SpearmansCorrelation().correlation(noVariance, values)));
    }

    /**
     * Insufficient data
     */
    @Override
    public void testInsufficientData() {
        double[] one = new double[] {1};
        double[] two = new double[] {2};
        try {
            new SpearmansCorrelation().correlation(one, two);
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // Expected
        }
        RealMatrix matrix = new BlockRealMatrix(new double[][] {{0},{1}});
        try {
            new SpearmansCorrelation(matrix);
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // Expected
        }
    }

    @Override
    public void testConsistency() {
        RealMatrix matrix = createRealMatrix(longleyData, 16, 7);
        SpearmansCorrelation corrInstance = new SpearmansCorrelation(matrix);
        double[][] data = matrix.getData();
        double[] x = matrix.getColumn(0);
        double[] y = matrix.getColumn(1);
        assertEquals(new SpearmansCorrelation().correlation(x, y),
                corrInstance.getCorrelationMatrix().getEntry(0, 1), Double.MIN_VALUE);
        TestUtils.assertEquals("Correlation matrix", corrInstance.getCorrelationMatrix(),
                new SpearmansCorrelation().computeCorrelationMatrix(data), Double.MIN_VALUE);
    }

    // Not relevant here
    @Override
    public void testStdErrorConsistency() throws Exception {}
    @Override
    public void testCovarianceConsistency() throws Exception {}

}
