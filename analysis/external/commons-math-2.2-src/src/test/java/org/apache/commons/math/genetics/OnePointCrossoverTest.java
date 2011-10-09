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
package org.apache.commons.math.genetics;

import static org.junit.Assert.*;
import org.junit.Test;

public class OnePointCrossoverTest {

    @Test
    public void testCrossover() {
        Integer[] p1 = new Integer[] {1,0,1,0,0,1,0,1,1};
        Integer[] p2 = new Integer[] {0,1,1,0,1,0,1,1,1};

        BinaryChromosome p1c = new DummyBinaryChromosome(p1);
        BinaryChromosome p2c = new DummyBinaryChromosome(p2);

        OnePointCrossover<Integer> opc = new OnePointCrossover<Integer>();

        // how to test a stochastic method?
        for (int i=0; i<20; i++) {
            ChromosomePair pair = opc.crossover(p1c,p2c);

            Integer[] c1 = new Integer[p1.length];
            Integer[] c2 = new Integer[p2.length];

            c1 = ((BinaryChromosome) pair.getFirst()).getRepresentation().toArray(c1);
            c2 = ((BinaryChromosome) pair.getSecond()).getRepresentation().toArray(c2);

            // first and last values will be the same
            assertEquals((int) p1[0], (int) c1[0]);
            assertEquals((int) p2[0], (int) c2[0]);
            assertEquals((int) p1[p1.length-1], (int) c1[c1.length-1]);
            assertEquals((int) p2[p2.length-1], (int) c2[c2.length-1]);
            // moreover, in the above setting, the 2nd, 3rd and 7th values will be the same
            assertEquals((int) p1[2], (int) c1[2]);
            assertEquals((int) p2[2], (int) c2[2]);
            assertEquals((int) p1[3], (int) c1[3]);
            assertEquals((int) p2[3], (int) c2[3]);
            assertEquals((int) p1[7], (int) c1[7]);
            assertEquals((int) p2[7], (int) c2[7]);
        }
    }

}
