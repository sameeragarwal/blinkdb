/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.commons.math.analysis.solvers;

import junit.framework.TestCase;

/**
 * @version $Revision: 811685 $ $Date: 2009-09-05 19:36:48 +0200 (sam. 05 sept. 2009) $
 */
public class UnivariateRealSolverFactoryImplTest extends TestCase {

    /** solver factory */
    private UnivariateRealSolverFactory factory;

    /**
     * @throws java.lang.Exception
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        factory = new UnivariateRealSolverFactoryImpl();
    }

    /**
     * @throws java.lang.Exception
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        factory = null;
        super.tearDown();
    }

    public void testNewBisectionSolverValid() {
        UnivariateRealSolver solver = factory.newBisectionSolver();
        assertNotNull(solver);
        assertTrue(solver instanceof BisectionSolver);
    }

    public void testNewNewtonSolverValid() {
        UnivariateRealSolver solver = factory.newNewtonSolver();
        assertNotNull(solver);
        assertTrue(solver instanceof NewtonSolver);
    }

    public void testNewBrentSolverValid() {
        UnivariateRealSolver solver = factory.newBrentSolver();
        assertNotNull(solver);
        assertTrue(solver instanceof BrentSolver);
    }

    public void testNewSecantSolverValid() {
        UnivariateRealSolver solver = factory.newSecantSolver();
        assertNotNull(solver);
        assertTrue(solver instanceof SecantSolver);
    }

}
