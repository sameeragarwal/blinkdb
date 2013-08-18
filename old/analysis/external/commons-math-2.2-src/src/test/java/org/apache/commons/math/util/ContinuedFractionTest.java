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
package org.apache.commons.math.util;

import org.apache.commons.math.MathException;

import junit.framework.TestCase;

/**
 * @version $Revision: 811685 $ $Date: 2009-09-05 19:36:48 +0200 (sam. 05 sept. 2009) $
 */
public class ContinuedFractionTest extends TestCase {
    /**
     * Constructor for ContinuedFractionTest.
     * @param name
     */
    public ContinuedFractionTest(String name) {
        super(name);
    }

    public void testGoldenRatio(){
        ContinuedFraction cf = new ContinuedFraction() {

            @Override
            public double getA(int n, double x) {
                return 1.0;
            }

            @Override
            public double getB(int n, double x) {
                return 1.0;
            }
        };

        try {
            double gr = cf.evaluate(0.0, 10e-9);
            assertEquals(1.61803399, gr, 10e-9);
        } catch (MathException e) {
            fail(e.getMessage());
        }
    }
}
