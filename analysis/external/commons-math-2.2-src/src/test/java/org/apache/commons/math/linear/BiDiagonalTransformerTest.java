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

package org.apache.commons.math.linear;

import org.apache.commons.math.linear.BiDiagonalTransformer;
import org.apache.commons.math.linear.MatrixUtils;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

public class BiDiagonalTransformerTest {

    private double[][] testSquare = {
            { 24.0 / 25.0, 43.0 / 25.0 },
            { 57.0 / 25.0, 24.0 / 25.0 }
    };

    private double[][] testNonSquare = {
        {  -540.0 / 625.0,  963.0 / 625.0, -216.0 / 625.0 },
        { -1730.0 / 625.0, -744.0 / 625.0, 1008.0 / 625.0 },
        {  -720.0 / 625.0, 1284.0 / 625.0, -288.0 / 625.0 },
        {  -360.0 / 625.0,  192.0 / 625.0, 1756.0 / 625.0 },
    };

    @Test
    public void testDimensions() {
        checkdimensions(MatrixUtils.createRealMatrix(testSquare));
        checkdimensions(MatrixUtils.createRealMatrix(testNonSquare));
        checkdimensions(MatrixUtils.createRealMatrix(testNonSquare).transpose());
    }

    private void checkdimensions(RealMatrix matrix) {
        final int m = matrix.getRowDimension();
        final int n = matrix.getColumnDimension();
        BiDiagonalTransformer transformer = new BiDiagonalTransformer(matrix);
        Assert.assertEquals(m, transformer.getU().getRowDimension());
        Assert.assertEquals(m, transformer.getU().getColumnDimension());
        Assert.assertEquals(m, transformer.getB().getRowDimension());
        Assert.assertEquals(n, transformer.getB().getColumnDimension());
        Assert.assertEquals(n, transformer.getV().getRowDimension());
        Assert.assertEquals(n, transformer.getV().getColumnDimension());

    }

    @Test
    public void testAEqualUSVt() {
        checkAEqualUSVt(MatrixUtils.createRealMatrix(testSquare));
        checkAEqualUSVt(MatrixUtils.createRealMatrix(testNonSquare));
        checkAEqualUSVt(MatrixUtils.createRealMatrix(testNonSquare).transpose());
    }

    private void checkAEqualUSVt(RealMatrix matrix) {
        BiDiagonalTransformer transformer = new BiDiagonalTransformer(matrix);
        RealMatrix u = transformer.getU();
        RealMatrix b = transformer.getB();
        RealMatrix v = transformer.getV();
        double norm = u.multiply(b).multiply(v.transpose()).subtract(matrix).getNorm();
        Assert.assertEquals(0, norm, 1.0e-14);
    }

    @Test
    public void testUOrthogonal() {
        checkOrthogonal(new BiDiagonalTransformer(MatrixUtils.createRealMatrix(testSquare)).getU());
        checkOrthogonal(new BiDiagonalTransformer(MatrixUtils.createRealMatrix(testNonSquare)).getU());
        checkOrthogonal(new BiDiagonalTransformer(MatrixUtils.createRealMatrix(testNonSquare).transpose()).getU());
    }

    @Test
    public void testVOrthogonal() {
        checkOrthogonal(new BiDiagonalTransformer(MatrixUtils.createRealMatrix(testSquare)).getV());
        checkOrthogonal(new BiDiagonalTransformer(MatrixUtils.createRealMatrix(testNonSquare)).getV());
        checkOrthogonal(new BiDiagonalTransformer(MatrixUtils.createRealMatrix(testNonSquare).transpose()).getV());
    }

    private void checkOrthogonal(RealMatrix m) {
        RealMatrix mTm = m.transpose().multiply(m);
        RealMatrix id  = MatrixUtils.createRealIdentityMatrix(mTm.getRowDimension());
        Assert.assertEquals(0, mTm.subtract(id).getNorm(), 1.0e-14);
    }

    @Test
    public void testBBiDiagonal() {
        checkBiDiagonal(new BiDiagonalTransformer(MatrixUtils.createRealMatrix(testSquare)).getB());
        checkBiDiagonal(new BiDiagonalTransformer(MatrixUtils.createRealMatrix(testNonSquare)).getB());
        checkBiDiagonal(new BiDiagonalTransformer(MatrixUtils.createRealMatrix(testNonSquare).transpose()).getB());
    }

    private void checkBiDiagonal(RealMatrix m) {
        final int rows = m.getRowDimension();
        final int cols = m.getColumnDimension();
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j) {
                if (rows < cols) {
                    if ((i < j) || (i > j + 1)) {
                        Assert.assertEquals(0, m.getEntry(i, j), 1.0e-16);
                    }
                } else {
                    if ((i < j - 1) || (i > j)) {
                        Assert.assertEquals(0, m.getEntry(i, j), 1.0e-16);
                    }
                }
            }
        }
    }

    @Test
    public void testSingularMatrix() {
       BiDiagonalTransformer transformer =
            new BiDiagonalTransformer(MatrixUtils.createRealMatrix(new double[][] {
                { 1.0, 2.0, 3.0 },
                { 2.0, 3.0, 4.0 },
                { 3.0, 5.0, 7.0 }
            }));
       final double s3  = FastMath.sqrt(3.0);
       final double s14 = FastMath.sqrt(14.0);
       final double s1553 = FastMath.sqrt(1553.0);
       RealMatrix uRef = MatrixUtils.createRealMatrix(new double[][] {
           {  -1.0 / s14,  5.0 / (s3 * s14),  1.0 / s3 },
           {  -2.0 / s14, -4.0 / (s3 * s14),  1.0 / s3 },
           {  -3.0 / s14,  1.0 / (s3 * s14), -1.0 / s3 }
       });
       RealMatrix bRef = MatrixUtils.createRealMatrix(new double[][] {
           { -s14, s1553 / s14,   0.0 },
           {  0.0, -87 * s3 / (s14 * s1553), -s3 * s14 / s1553 },
           {  0.0, 0.0, 0.0 }
       });
       RealMatrix vRef = MatrixUtils.createRealMatrix(new double[][] {
           { 1.0,   0.0,         0.0        },
           { 0.0,  -23 / s1553,  32 / s1553 },
           { 0.0,  -32 / s1553, -23 / s1553 }
       });

       // check values against known references
       RealMatrix u = transformer.getU();
       Assert.assertEquals(0, u.subtract(uRef).getNorm(), 1.0e-14);
       RealMatrix b = transformer.getB();
       Assert.assertEquals(0, b.subtract(bRef).getNorm(), 1.0e-14);
       RealMatrix v = transformer.getV();
       Assert.assertEquals(0, v.subtract(vRef).getNorm(), 1.0e-14);

       // check the same cached instance is returned the second time
       Assert.assertTrue(u == transformer.getU());
       Assert.assertTrue(b == transformer.getB());
       Assert.assertTrue(v == transformer.getV());

    }

    @Test
    public void testMatricesValues() {
       BiDiagonalTransformer transformer =
            new BiDiagonalTransformer(MatrixUtils.createRealMatrix(testSquare));
       final double s17 = FastMath.sqrt(17.0);
        RealMatrix uRef = MatrixUtils.createRealMatrix(new double[][] {
                {  -8 / (5 * s17), 19 / (5 * s17) },
                { -19 / (5 * s17), -8 / (5 * s17) }
        });
        RealMatrix bRef = MatrixUtils.createRealMatrix(new double[][] {
                { -3 * s17 / 5, 32 * s17 / 85 },
                {      0.0,     -5 * s17 / 17 }
        });
        RealMatrix vRef = MatrixUtils.createRealMatrix(new double[][] {
                { 1.0,  0.0 },
                { 0.0, -1.0 }
        });

        // check values against known references
        RealMatrix u = transformer.getU();
        Assert.assertEquals(0, u.subtract(uRef).getNorm(), 1.0e-14);
        RealMatrix b = transformer.getB();
        Assert.assertEquals(0, b.subtract(bRef).getNorm(), 1.0e-14);
        RealMatrix v = transformer.getV();
        Assert.assertEquals(0, v.subtract(vRef).getNorm(), 1.0e-14);

        // check the same cached instance is returned the second time
        Assert.assertTrue(u == transformer.getU());
        Assert.assertTrue(b == transformer.getB());
        Assert.assertTrue(v == transformer.getV());

    }

    @Test
    public void testUpperOrLower() {
        Assert.assertTrue(new BiDiagonalTransformer(MatrixUtils.createRealMatrix(testSquare)).isUpperBiDiagonal());
        Assert.assertTrue(new BiDiagonalTransformer(MatrixUtils.createRealMatrix(testNonSquare)).isUpperBiDiagonal());
        Assert.assertFalse(new BiDiagonalTransformer(MatrixUtils.createRealMatrix(testNonSquare).transpose()).isUpperBiDiagonal());
    }

}
