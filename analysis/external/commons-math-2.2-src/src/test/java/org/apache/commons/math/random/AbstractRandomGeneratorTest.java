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
package org.apache.commons.math.random;

import org.apache.commons.math.stat.Frequency;


/**
 * Test cases for the AbstractRandomGenerator class
 *
 * @version $Revision: 902201 $ $Date: 2010-01-22 19:18:16 +0100 (ven. 22 janv. 2010) $
 */

public class AbstractRandomGeneratorTest extends RandomDataTest {

    protected TestRandomGenerator testGenerator = new TestRandomGenerator();

    public AbstractRandomGeneratorTest(String name) {
        super(name);
        randomData = new RandomDataImpl(testGenerator);
    }

    @Override
    public void testNextInt() {
        try {
            testGenerator.nextInt(-1);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // ignored
        }
        Frequency freq = new Frequency();
        int value = 0;
        for (int i=0; i<smallSampleSize; i++) {
            value = testGenerator.nextInt(4);
            assertTrue("nextInt range",(value >= 0) && (value <= 3));
            freq.addValue(value);
        }
        long[] observed = new long[4];
        for (int i=0; i<4; i++) {
            observed[i] = freq.getCount(i);
        }

        /* Use ChiSquare dist with df = 4-1 = 3, alpha = .001
         * Change to 11.34 for alpha = .01
         */
        assertTrue("chi-square test -- will fail about 1 in 1000 times",
                testStatistic.chiSquare(expected,observed) < 16.27);
    }

    @Override
    public void testNextLong() {
        long q1 = Long.MAX_VALUE/4;
        long q2 = 2 *  q1;
        long q3 = 3 * q1;

        Frequency freq = new Frequency();
        long val = 0;
        int value = 0;
        for (int i=0; i<smallSampleSize; i++) {
            val = testGenerator.nextLong();
            if (val < q1) {
                value = 0;
            } else if (val < q2) {
                value = 1;
            } else if (val < q3) {
                value = 2;
            } else {
                value = 3;
            }
            freq.addValue(value);
        }
        long[] observed = new long[4];
        for (int i=0; i<4; i++) {
            observed[i] = freq.getCount(i);
        }

        /* Use ChiSquare dist with df = 4-1 = 3, alpha = .001
         * Change to 11.34 for alpha = .01
         */
        assertTrue("chi-square test -- will fail about 1 in 1000 times",
                testStatistic.chiSquare(expected,observed) < 16.27);
    }

    public void testNextBoolean() {
        long halfSampleSize = smallSampleSize / 2;
        double[] expected = {halfSampleSize, halfSampleSize};
        long[] observed = new long[2];
        for (int i=0; i<smallSampleSize; i++) {
            if (testGenerator.nextBoolean()) {
                observed[0]++;
            } else {
                observed[1]++;
            }
        }
        /* Use ChiSquare dist with df = 2-1 = 1, alpha = .001
         * Change to 6.635 for alpha = .01
         */
        assertTrue("chi-square test -- will fail about 1 in 1000 times",
                testStatistic.chiSquare(expected,observed) < 10.828);
    }

    public void testNextFloat() {
        Frequency freq = new Frequency();
        float val = 0;
        int value = 0;
        for (int i=0; i<smallSampleSize; i++) {
            val = testGenerator.nextFloat();
            if (val < 0.25) {
                value = 0;
            } else if (val < 0.5) {
                value = 1;
            } else if (val < 0.75) {
                value = 2;
            } else {
                value = 3;
            }
            freq.addValue(value);
        }
        long[] observed = new long[4];
        for (int i=0; i<4; i++) {
            observed[i] = freq.getCount(i);
        }

        /* Use ChiSquare dist with df = 4-1 = 3, alpha = .001
         * Change to 11.34 for alpha = .01
         */
        assertTrue("chi-square test -- will fail about 1 in 1000 times",
                testStatistic.chiSquare(expected,observed) < 16.27);
    }
}
