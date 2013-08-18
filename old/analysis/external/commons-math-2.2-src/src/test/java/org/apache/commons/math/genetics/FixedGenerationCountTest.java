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

import java.util.Iterator;

import org.junit.Test;

public class FixedGenerationCountTest {

    @Test
    public void testIsSatisfied() {
        FixedGenerationCount fgc = new FixedGenerationCount(20);

        int cnt = 0;
        Population pop = new Population() {
            public void addChromosome(Chromosome chromosome) {
                // unimportant
            }
            public Chromosome getFittestChromosome() {
                // unimportant
                return null;
            }
            public int getPopulationLimit() {
                // unimportant
                return 0;
            }
            public int getPopulationSize() {
                // unimportant
                return 0;
            }
            public Population nextGeneration() {
                // unimportant
                return null;
            }
            public Iterator<Chromosome> iterator() {
                // unimportant
                return null;
            }
        };

        while (!fgc.isSatisfied(pop))
            cnt++;
        assertEquals(20, cnt);
    }

}
