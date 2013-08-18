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

import org.apache.commons.math.FunctionEvaluationException;

/**
 * Auxiliary class for testing optimizers.
 *
 * @version $Revision$ $Date$
 */
public class SumSincFunction implements DifferentiableMultivariateRealFunction {
    private static final DifferentiableUnivariateRealFunction sinc = new SincFunction();
    private static final UnivariateRealFunction sincDeriv = sinc.derivative();

    /**
     * Factor that will multiply each term of the sum.
     */
    private final double factor;

    /**
     * @param factor Factor that will multiply each term of the sum.
     */
    public SumSincFunction(double factor) {
        this.factor = factor;
    }

    /**
     * @param point Argument.
     * @return the value of this function at point {@code x}.
     */
    public double value(double[] point) throws FunctionEvaluationException {
        double sum = 0;
        for (int i = 0, max = point.length; i < max; i++) {
            final double x = point[i];
            final double v = sinc.value(x);
            sum += v;
        }
        return factor * sum;
    }

    /**
     * {@inheritDoc}
     */
    public MultivariateRealFunction partialDerivative(final int k) {
        return new MultivariateRealFunction() {
            public double value(double[] point) throws FunctionEvaluationException {
                return sincDeriv.value(point[k]);
            }
        };
    }

    /**                                                                            
     * {@inheritDoc}
     */
    public MultivariateVectorialFunction gradient() {
        return new MultivariateVectorialFunction() {
            public double[] value(double[] point)
                throws FunctionEvaluationException {
                final int n = point.length;
                final double[] r = new double[n];
                for (int i = 0; i < n; i++) {
                    final double x = point[i];
                    r[i] = factor * sincDeriv.value(x);
                }
                return r;
            }
        };
    }
}
