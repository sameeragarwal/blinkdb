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

public class TournamentSelectionTest {

    private static int counter = 0;

    @Test
    public void testSelect() {
        TournamentSelection ts = new TournamentSelection(2);
        ElitisticListPopulation pop = new ElitisticListPopulation(100, 0.203);

        for (int i=0; i<pop.getPopulationLimit(); i++) {
            pop.addChromosome(new DummyChromosome());
        }
        // how to write a test for stochastic method?
        for (int i=0; i<20; i++) {
            ChromosomePair pair = ts.select(pop);
            // the worst chromosome should NEVER be selected
            assertTrue(pair.getFirst().getFitness() > 0);
            assertTrue(pair.getSecond().getFitness() > 0);
        }
    }

    private static class DummyChromosome extends Chromosome {
        private final int fitness;

        public DummyChromosome() {
            this.fitness = counter;
            counter++;
        }

        public double fitness() {
            return this.fitness;
        }
    }

}
