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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ChromosomeTest {

    @Test
    public void testCompareTo() {
        Chromosome c1 = new Chromosome() {
            public double fitness() {
                return 0;
            }
        };
        Chromosome c2 = new Chromosome() {
            public double fitness() {
                return 10;
            }
        };
        Chromosome c3 = new Chromosome() {
            public double fitness() {
                return 10;
            }
        };

        assertTrue(c1.compareTo(c2) < 0);
        assertTrue(c2.compareTo(c1) > 0);
        assertEquals(0,c3.compareTo(c2));
        assertEquals(0,c2.compareTo(c3));
    }

    private abstract static class DummyChromosome extends Chromosome {
        private final int repr;

        public DummyChromosome(final int repr) {
            this.repr = repr;
        }
        @Override
        protected boolean isSame(Chromosome another) {
            return ((DummyChromosome) another).repr == repr;
        }
    }

    @Test
    public void testFindSameChromosome() {
        Chromosome c1 = new DummyChromosome(1) {
            public double fitness() {
                return 1;
            }
        };
        Chromosome c2 = new DummyChromosome(2) {
            public double fitness() {
                return 2;
            }
        };
        Chromosome c3 = new DummyChromosome(3) {
            public double fitness() {
                return 3;
            }
        };
        Chromosome c4 = new DummyChromosome(1) {
            public double fitness() {
                return 5;
            }
        };
        Chromosome c5 = new DummyChromosome(15) {
            public double fitness() {
                return 15;
            }
        };

        List<Chromosome> popChr = new ArrayList<Chromosome>();
        popChr.add(c1);
        popChr.add(c2);
        popChr.add(c3);
        Population pop = new ListPopulation(popChr,3) {
            public Population nextGeneration() {
                // not important
                return null;
            }
        };

        assertNull(c5.findSameChromosome(pop));
        assertEquals(c1, c4.findSameChromosome(pop));

        c4.searchForFitnessUpdate(pop);
        assertEquals(1, c4.getFitness(),0);
    }

}

