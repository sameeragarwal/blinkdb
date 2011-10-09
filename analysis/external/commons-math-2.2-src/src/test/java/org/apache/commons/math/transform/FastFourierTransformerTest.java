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
package org.apache.commons.math.transform;

import org.apache.commons.math.analysis.*;
import org.apache.commons.math.complex.*;
import org.apache.commons.math.util.FastMath;
import junit.framework.TestCase;

/**
 * Testcase for fast Fourier transformer.
 * <p>
 * FFT algorithm is exact, the small tolerance number is used only
 * to account for round-off errors.
 *
 * @version $Revision: 1070725 $ $Date: 2011-02-15 02:31:12 +0100 (mar. 15 févr. 2011) $
 */
public final class FastFourierTransformerTest extends TestCase {

    /**
     * Test of transformer for the ad hoc data taken from Mathematica.
     */
    public void testAdHocData() {
        FastFourierTransformer transformer = new FastFourierTransformer();
        Complex result[]; double tolerance = 1E-12;

        double x[] = {1.3, 2.4, 1.7, 4.1, 2.9, 1.7, 5.1, 2.7};
        Complex y[] = {
            new Complex(21.9, 0.0),
            new Complex(-2.09497474683058, 1.91507575950825),
            new Complex(-2.6, 2.7),
            new Complex(-1.10502525316942, -4.88492424049175),
            new Complex(0.1, 0.0),
            new Complex(-1.10502525316942, 4.88492424049175),
            new Complex(-2.6, -2.7),
            new Complex(-2.09497474683058, -1.91507575950825)};

        result = transformer.transform(x);
        for (int i = 0; i < result.length; i++) {
            assertEquals(y[i].getReal(), result[i].getReal(), tolerance);
            assertEquals(y[i].getImaginary(), result[i].getImaginary(), tolerance);
        }

        result = transformer.inversetransform(y);
        for (int i = 0; i < result.length; i++) {
            assertEquals(x[i], result[i].getReal(), tolerance);
            assertEquals(0.0, result[i].getImaginary(), tolerance);
        }

        double x2[] = {10.4, 21.6, 40.8, 13.6, 23.2, 32.8, 13.6, 19.2};
        FastFourierTransformer.scaleArray(x2, 1.0 / FastMath.sqrt(x2.length));
        Complex y2[] = y;

        result = transformer.transform2(y2);
        for (int i = 0; i < result.length; i++) {
            assertEquals(x2[i], result[i].getReal(), tolerance);
            assertEquals(0.0, result[i].getImaginary(), tolerance);
        }

        result = transformer.inversetransform2(x2);
        for (int i = 0; i < result.length; i++) {
            assertEquals(y2[i].getReal(), result[i].getReal(), tolerance);
            assertEquals(y2[i].getImaginary(), result[i].getImaginary(), tolerance);
        }
    }

    public void test2DData() {
        FastFourierTransformer transformer = new FastFourierTransformer();
        double tolerance = 1E-12;
        Complex[][] input = new Complex[][] {new Complex[] {new Complex(1, 0),
                                                            new Complex(2, 0)},
                                             new Complex[] {new Complex(3, 1),
                                                            new Complex(4, 2)}};
        Complex[][] goodOutput = new Complex[][] {new Complex[] {new Complex(5,
                1.5), new Complex(-1, -.5)}, new Complex[] {new Complex(-2,
                -1.5), new Complex(0, .5)}};
        Complex[][] output = (Complex[][])transformer.mdfft(input, true);
        Complex[][] output2 = (Complex[][])transformer.mdfft(output, false);

        assertEquals(input.length, output.length);
        assertEquals(input.length, output2.length);
        assertEquals(input[0].length, output[0].length);
        assertEquals(input[0].length, output2[0].length);
        assertEquals(input[1].length, output[1].length);
        assertEquals(input[1].length, output2[1].length);

        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[0].length; j++) {
                assertEquals(input[i][j].getImaginary(), output2[i][j].getImaginary(),
                             tolerance);
                assertEquals(input[i][j].getReal(), output2[i][j].getReal(), tolerance);
                assertEquals(goodOutput[i][j].getImaginary(), output[i][j].getImaginary(),
                             tolerance);
                assertEquals(goodOutput[i][j].getReal(), output[i][j].getReal(), tolerance);
            }
        }
    }

    /**
     * Test of transformer for the sine function.
     */
    public void testSinFunction() throws Exception {
        UnivariateRealFunction f = new SinFunction();
        FastFourierTransformer transformer = new FastFourierTransformer();
        Complex result[]; int N = 1 << 8;
        double min, max, tolerance = 1E-12;

        min = 0.0; max = 2.0 * FastMath.PI;
        result = transformer.transform(f, min, max, N);
        assertEquals(0.0, result[1].getReal(), tolerance);
        assertEquals(-(N >> 1), result[1].getImaginary(), tolerance);
        assertEquals(0.0, result[N-1].getReal(), tolerance);
        assertEquals(N >> 1, result[N-1].getImaginary(), tolerance);
        for (int i = 0; i < N-1; i += (i == 0 ? 2 : 1)) {
            assertEquals(0.0, result[i].getReal(), tolerance);
            assertEquals(0.0, result[i].getImaginary(), tolerance);
        }

        min = -FastMath.PI; max = FastMath.PI;
        result = transformer.inversetransform(f, min, max, N);
        assertEquals(0.0, result[1].getReal(), tolerance);
        assertEquals(-0.5, result[1].getImaginary(), tolerance);
        assertEquals(0.0, result[N-1].getReal(), tolerance);
        assertEquals(0.5, result[N-1].getImaginary(), tolerance);
        for (int i = 0; i < N-1; i += (i == 0 ? 2 : 1)) {
            assertEquals(0.0, result[i].getReal(), tolerance);
            assertEquals(0.0, result[i].getImaginary(), tolerance);
        }
    }

    /**
     * Test of parameters for the transformer.
     */
    public void testParameters() throws Exception {
        UnivariateRealFunction f = new SinFunction();
        FastFourierTransformer transformer = new FastFourierTransformer();

        try {
            // bad interval
            transformer.transform(f, 1, -1, 64);
            fail("Expecting IllegalArgumentException - bad interval");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            // bad samples number
            transformer.transform(f, -1, 1, 0);
            fail("Expecting IllegalArgumentException - bad samples number");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            // bad samples number
            transformer.transform(f, -1, 1, 100);
            fail("Expecting IllegalArgumentException - bad samples number");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }
}
