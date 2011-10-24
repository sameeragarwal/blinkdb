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

import java.util.Random;

/**
 * Test cases for the RandomAdaptor class
 *
 * @version $Revision: 902201 $ $Date: 2010-01-22 19:18:16 +0100 (ven. 22 janv. 2010) $
 */

public class RandomAdaptorTest extends RandomDataTest {

    public RandomAdaptorTest(String name) {
        super(name);
    }

    public void testAdaptor() {
        ConstantGenerator generator = new ConstantGenerator();
        Random random = RandomAdaptor.createAdaptor(generator);
        checkConstant(random);
        RandomAdaptor randomAdaptor = new RandomAdaptor(generator);
        checkConstant(randomAdaptor);
    }

    private void checkConstant(Random random) {
        byte[] bytes = new byte[] {0};
        random.nextBytes(bytes);
        assertEquals(0, bytes[0]);
        assertEquals(false, random.nextBoolean());
        assertEquals(0, random.nextDouble(), 0);
        assertEquals(0, random.nextFloat(), 0);
        assertEquals(0, random.nextGaussian(), 0);
        assertEquals(0, random.nextInt());
        assertEquals(0, random.nextInt(1));
        assertEquals(0, random.nextLong());
        random.setSeed(100);
        assertEquals(0, random.nextDouble(), 0);
    }

    /*
     * "Constant" generator to test Adaptor delegation.
     * "Powered by Eclipse ;-)"
     *
     */
    private static class ConstantGenerator implements RandomGenerator {

        public boolean nextBoolean() {
            return false;
        }

        public void nextBytes(byte[] bytes) {
        }

        public double nextDouble() {
            return 0;
        }

        public float nextFloat() {
            return 0;
        }

        public double nextGaussian() {
            return 0;
        }

        public int nextInt() {
            return 0;
        }

        public int nextInt(int n) {
            return 0;
        }

        public long nextLong() {
            return 0;
        }

        public void setSeed(int seed) {
        }

        public void setSeed(int[] seed) {
        }

        public void setSeed(long seed) {
        }

    }
}
