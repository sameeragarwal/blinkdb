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
 * Auxiliary class for testing solvers.
 *
 * @version $Revision: 1037327 $ $Date: 2010-11-20 21:57:37 +0100 (sam. 20 nov. 2010) $
 */
public class QuinticFunction implements DifferentiableUnivariateRealFunction {

    /* Evaluate quintic.
     * @see org.apache.commons.math.UnivariateRealFunction#value(double)
     */
    public double value(double x) {
        return (x-1)*(x-0.5)*x*(x+0.5)*(x+1);
    }

    public UnivariateRealFunction derivative() {
        return new UnivariateRealFunction() {
            public double value(double x) {
                return (5*x*x-3.75)*x*x+0.25;
            }
        };
    }
}
