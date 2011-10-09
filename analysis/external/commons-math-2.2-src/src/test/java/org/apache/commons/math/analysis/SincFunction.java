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
package org.apache.commons.math.analysis;


/**
 * Auxiliary class for testing optimizers.
 *
 * @version $Revision$ $Date$
 */
public class SincFunction implements DifferentiableUnivariateRealFunction {
    private static final double EPS = 1e-12;

    /**
     * @param x Argument.
     * @return the value of this function at point {@code x}.
     */
    public double value(double x) {
        return (Math.abs(x) < EPS ?
                1 :
                Math.sin(x) / x);
    }

    /**
     * {@inheritDoc}
     */
    public UnivariateRealFunction derivative() {
        return new UnivariateRealFunction() {
            public double value(double x) {
                return (Math.abs(x) < EPS ?
                        0 :
                        (x * Math.cos(x) - Math.sin(x)) / (x * x));
            }
        };
    }
}
