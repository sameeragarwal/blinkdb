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

import java.util.Random;

import org.apache.commons.math.linear.MatrixVisitorException;

import junit.framework.TestCase;

public class QRDecompositionImplTest extends TestCase {
    double[][] testData3x3NonSingular = {
            { 12, -51, 4 },
            { 6, 167, -68 },
            { -4, 24, -41 }, };

    double[][] testData3x3Singular = {
            { 1, 4, 7, },
            { 2, 5, 8, },
            { 3, 6, 9, }, };

    double[][] testData3x4 = {
            { 12, -51, 4, 1 },
            { 6, 167, -68, 2 },
            { -4, 24, -41, 3 }, };

    double[][] testData4x3 = {
            { 12, -51, 4, },
            { 6, 167, -68, },
            { -4, 24, -41, },
            { -5, 34, 7, }, };

    private static final double entryTolerance = 10e-16;

    private static final double normTolerance = 10e-14;

    public QRDecompositionImplTest(String name) {
        super(name);
    }

    /** test dimensions 
     * @throws MatrixVisitorException */
    public void testDimensions() throws MatrixVisitorException {
        checkDimension(MatrixUtils.createRealMatrix(testData3x3NonSingular));

        checkDimension(MatrixUtils.createRealMatrix(testData4x3));

        checkDimension(MatrixUtils.createRealMatrix(testData3x4));

        Random r = new Random(643895747384642l);
        int    p = (5 * BlockRealMatrix.BLOCK_SIZE) / 4;
        int    q = (7 * BlockRealMatrix.BLOCK_SIZE) / 4;
        checkDimension(createTestMatrix(r, p, q));
        checkDimension(createTestMatrix(r, q, p));

    }

    private void checkDimension(RealMatrix m) {
        int rows = m.getRowDimension();
        int columns = m.getColumnDimension();
        QRDecomposition qr = new QRDecompositionImpl(m);
        assertEquals(rows,    qr.getQ().getRowDimension());
        assertEquals(rows,    qr.getQ().getColumnDimension());
        assertEquals(rows,    qr.getR().getRowDimension());
        assertEquals(columns, qr.getR().getColumnDimension());
    }

    /** test A = QR 
     * @throws MatrixVisitorException */
    public void testAEqualQR() throws MatrixVisitorException {
        checkAEqualQR(MatrixUtils.createRealMatrix(testData3x3NonSingular));

        checkAEqualQR(MatrixUtils.createRealMatrix(testData3x3Singular));

        checkAEqualQR(MatrixUtils.createRealMatrix(testData3x4));

        checkAEqualQR(MatrixUtils.createRealMatrix(testData4x3));

        Random r = new Random(643895747384642l);
        int    p = (5 * BlockRealMatrix.BLOCK_SIZE) / 4;
        int    q = (7 * BlockRealMatrix.BLOCK_SIZE) / 4;
        checkAEqualQR(createTestMatrix(r, p, q));

        checkAEqualQR(createTestMatrix(r, q, p));

    }

    private void checkAEqualQR(RealMatrix m) {
        QRDecomposition qr = new QRDecompositionImpl(m);
        double norm = qr.getQ().multiply(qr.getR()).subtract(m).getNorm();
        assertEquals(0, norm, normTolerance);
    }

    /** test the orthogonality of Q 
     * @throws MatrixVisitorException */
    public void testQOrthogonal() throws MatrixVisitorException {
        checkQOrthogonal(MatrixUtils.createRealMatrix(testData3x3NonSingular));

        checkQOrthogonal(MatrixUtils.createRealMatrix(testData3x3Singular));

        checkQOrthogonal(MatrixUtils.createRealMatrix(testData3x4));

        checkQOrthogonal(MatrixUtils.createRealMatrix(testData4x3));

        Random r = new Random(643895747384642l);
        int    p = (5 * BlockRealMatrix.BLOCK_SIZE) / 4;
        int    q = (7 * BlockRealMatrix.BLOCK_SIZE) / 4;
        checkQOrthogonal(createTestMatrix(r, p, q));

        checkQOrthogonal(createTestMatrix(r, q, p));

    }

    private void checkQOrthogonal(RealMatrix m) {
        QRDecomposition qr = new QRDecompositionImpl(m);
        RealMatrix eye = MatrixUtils.createRealIdentityMatrix(m.getRowDimension());
        double norm = qr.getQT().multiply(qr.getQ()).subtract(eye).getNorm();
        assertEquals(0, norm, normTolerance);
    }

    /** test that R is upper triangular */
    public void testRUpperTriangular() throws MatrixVisitorException {
        RealMatrix matrix = MatrixUtils.createRealMatrix(testData3x3NonSingular);
        checkUpperTriangular(new QRDecompositionImpl(matrix).getR());

        matrix = MatrixUtils.createRealMatrix(testData3x3Singular);
        checkUpperTriangular(new QRDecompositionImpl(matrix).getR());

        matrix = MatrixUtils.createRealMatrix(testData3x4);
        checkUpperTriangular(new QRDecompositionImpl(matrix).getR());

        matrix = MatrixUtils.createRealMatrix(testData4x3);
        checkUpperTriangular(new QRDecompositionImpl(matrix).getR());

        Random r = new Random(643895747384642l);
        int    p = (5 * BlockRealMatrix.BLOCK_SIZE) / 4;
        int    q = (7 * BlockRealMatrix.BLOCK_SIZE) / 4;
        matrix = createTestMatrix(r, p, q);
        checkUpperTriangular(new QRDecompositionImpl(matrix).getR());

        matrix = createTestMatrix(r, p, q);
        checkUpperTriangular(new QRDecompositionImpl(matrix).getR());

    }

    private void checkUpperTriangular(RealMatrix m) throws MatrixVisitorException {
        m.walkInOptimizedOrder(new DefaultRealMatrixPreservingVisitor() {
            @Override
            public void visit(int row, int column, double value) {
                if (column < row) {
                    assertEquals(0.0, value, entryTolerance);
                }
            }
        });
    }

    /** test that H is trapezoidal 
     * @throws MatrixVisitorException */
    public void testHTrapezoidal() throws MatrixVisitorException {
        RealMatrix matrix = MatrixUtils.createRealMatrix(testData3x3NonSingular);
        checkTrapezoidal(new QRDecompositionImpl(matrix).getH());

        matrix = MatrixUtils.createRealMatrix(testData3x3Singular);
        checkTrapezoidal(new QRDecompositionImpl(matrix).getH());

        matrix = MatrixUtils.createRealMatrix(testData3x4);
        checkTrapezoidal(new QRDecompositionImpl(matrix).getH());

        matrix = MatrixUtils.createRealMatrix(testData4x3);
        checkTrapezoidal(new QRDecompositionImpl(matrix).getH());

        Random r = new Random(643895747384642l);
        int    p = (5 * BlockRealMatrix.BLOCK_SIZE) / 4;
        int    q = (7 * BlockRealMatrix.BLOCK_SIZE) / 4;
        matrix = createTestMatrix(r, p, q);
        checkTrapezoidal(new QRDecompositionImpl(matrix).getH());

        matrix = createTestMatrix(r, p, q);
        checkTrapezoidal(new QRDecompositionImpl(matrix).getH());

    }

    private void checkTrapezoidal(RealMatrix m) throws MatrixVisitorException {
        m.walkInOptimizedOrder(new DefaultRealMatrixPreservingVisitor() {
            @Override
            public void visit(int row, int column, double value) {
                if (column > row) {
                    assertEquals(0.0, value, entryTolerance);
                }
            }
        });
    }
    /** test matrices values */
    public void testMatricesValues() {
        QRDecomposition qr =
            new QRDecompositionImpl(MatrixUtils.createRealMatrix(testData3x3NonSingular));
        RealMatrix qRef = MatrixUtils.createRealMatrix(new double[][] {
                { -12.0 / 14.0,   69.0 / 175.0,  -58.0 / 175.0 },
                {  -6.0 / 14.0, -158.0 / 175.0,    6.0 / 175.0 },
                {   4.0 / 14.0,  -30.0 / 175.0, -165.0 / 175.0 }
        });
        RealMatrix rRef = MatrixUtils.createRealMatrix(new double[][] {
                { -14.0,  -21.0, 14.0 },
                {   0.0, -175.0, 70.0 },
                {   0.0,    0.0, 35.0 }
        });
        RealMatrix hRef = MatrixUtils.createRealMatrix(new double[][] {
                { 26.0 / 14.0, 0.0, 0.0 },
                {  6.0 / 14.0, 648.0 / 325.0, 0.0 },
                { -4.0 / 14.0,  36.0 / 325.0, 2.0 }
        });

        // check values against known references
        RealMatrix q = qr.getQ();
        assertEquals(0, q.subtract(qRef).getNorm(), 1.0e-13);
        RealMatrix qT = qr.getQT();
        assertEquals(0, qT.subtract(qRef.transpose()).getNorm(), 1.0e-13);
        RealMatrix r = qr.getR();
        assertEquals(0, r.subtract(rRef).getNorm(), 1.0e-13);
        RealMatrix h = qr.getH();
        assertEquals(0, h.subtract(hRef).getNorm(), 1.0e-13);

        // check the same cached instance is returned the second time
        assertTrue(q == qr.getQ());
        assertTrue(r == qr.getR());
        assertTrue(h == qr.getH());

    }

    private RealMatrix createTestMatrix(final Random r, final int rows, final int columns) throws MatrixVisitorException {
        RealMatrix m = MatrixUtils.createRealMatrix(rows, columns);
        m.walkInOptimizedOrder(new DefaultRealMatrixChangingVisitor(){
            @Override
            public double visit(int row, int column, double value) {
                return 2.0 * r.nextDouble() - 1.0;
            }
        });
        return m;
    }

}
