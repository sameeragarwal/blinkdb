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

import java.util.List;

/**
 * Implementation of RandomKey for testing purposes
 */
public class DummyRandomKey extends RandomKey<String> {

    public DummyRandomKey(List<Double> representation) {
        super(representation);
    }

    public DummyRandomKey(Double[] representation) {
        super(representation);
    }

    @Override
    public AbstractListChromosome<Double> newFixedLengthChromosome(List<Double> chromosomeRepresentation) {
        return new DummyRandomKey(chromosomeRepresentation);
    }

    public double fitness() {
        // unimportant
        return 0;
    }

}
