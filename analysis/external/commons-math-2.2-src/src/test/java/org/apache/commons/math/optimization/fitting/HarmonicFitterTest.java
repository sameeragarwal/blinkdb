// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.apache.commons.math.optimization.fitting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer;
import org.apache.commons.math.util.FastMath;
import org.apache.commons.math.util.MathUtils;
import org.junit.Test;

public class HarmonicFitterTest {

    @Test
    public void testNoError() throws OptimizationException {
        HarmonicFunction f = new HarmonicFunction(0.2, 3.4, 4.1);

        HarmonicFitter fitter =
            new HarmonicFitter(new LevenbergMarquardtOptimizer());
        for (double x = 0.0; x < 1.3; x += 0.01) {
            fitter.addObservedPoint(1.0, x, f.value(x));
        }

        HarmonicFunction fitted = fitter.fit();
        assertEquals(f.getAmplitude(), fitted.getAmplitude(), 1.0e-13);
        assertEquals(f.getPulsation(), fitted.getPulsation(), 1.0e-13);
        assertEquals(f.getPhase(),     MathUtils.normalizeAngle(fitted.getPhase(), f.getPhase()), 1.0e-13);

        for (double x = -1.0; x < 1.0; x += 0.01) {
            assertTrue(FastMath.abs(f.value(x) - fitted.value(x)) < 1.0e-13);
        }

    }

    @Test
    public void test1PercentError() throws OptimizationException {
        Random randomizer = new Random(64925784252l);
        HarmonicFunction f = new HarmonicFunction(0.2, 3.4, 4.1);

        HarmonicFitter fitter =
            new HarmonicFitter(new LevenbergMarquardtOptimizer());
        for (double x = 0.0; x < 10.0; x += 0.1) {
            fitter.addObservedPoint(1.0, x,
                                   f.value(x) + 0.01 * randomizer.nextGaussian());
        }

        HarmonicFunction fitted = fitter.fit();
        assertEquals(f.getAmplitude(), fitted.getAmplitude(), 7.6e-4);
        assertEquals(f.getPulsation(), fitted.getPulsation(), 2.7e-3);
        assertEquals(f.getPhase(),     MathUtils.normalizeAngle(fitted.getPhase(), f.getPhase()), 1.3e-2);

    }

    @Test
    public void testInitialGuess() throws OptimizationException {
        Random randomizer = new Random(45314242l);
        HarmonicFunction f = new HarmonicFunction(0.2, 3.4, 4.1);

        HarmonicFitter fitter =
            new HarmonicFitter(new LevenbergMarquardtOptimizer(), new double[] { 0.15, 3.6, 4.5 });
        for (double x = 0.0; x < 10.0; x += 0.1) {
            fitter.addObservedPoint(1.0, x,
                                   f.value(x) + 0.01 * randomizer.nextGaussian());
        }

        HarmonicFunction fitted = fitter.fit();
        assertEquals(f.getAmplitude(), fitted.getAmplitude(), 1.2e-3);
        assertEquals(f.getPulsation(), fitted.getPulsation(), 3.3e-3);
        assertEquals(f.getPhase(),     MathUtils.normalizeAngle(fitted.getPhase(), f.getPhase()), 1.7e-2);

    }

    @Test
    public void testUnsorted() throws OptimizationException {
        Random randomizer = new Random(64925784252l);
        HarmonicFunction f = new HarmonicFunction(0.2, 3.4, 4.1);

        HarmonicFitter fitter =
            new HarmonicFitter(new LevenbergMarquardtOptimizer());

        // build a regularly spaced array of measurements
        int size = 100;
        double[] xTab = new double[size];
        double[] yTab = new double[size];
        for (int i = 0; i < size; ++i) {
            xTab[i] = 0.1 * i;
            yTab[i] = f.value(xTab[i]) + 0.01 * randomizer.nextGaussian();
        }

        // shake it
        for (int i = 0; i < size; ++i) {
            int i1 = randomizer.nextInt(size);
            int i2 = randomizer.nextInt(size);
            double xTmp = xTab[i1];
            double yTmp = yTab[i1];
            xTab[i1] = xTab[i2];
            yTab[i1] = yTab[i2];
            xTab[i2] = xTmp;
            yTab[i2] = yTmp;
        }

        // pass it to the fitter
        for (int i = 0; i < size; ++i) {
            fitter.addObservedPoint(1.0, xTab[i], yTab[i]);
        }

        HarmonicFunction fitted = fitter.fit();
        assertEquals(f.getAmplitude(), fitted.getAmplitude(), 7.6e-4);
        assertEquals(f.getPulsation(), fitted.getPulsation(), 3.5e-3);
        assertEquals(f.getPhase(),     MathUtils.normalizeAngle(fitted.getPhase(), f.getPhase()), 1.5e-2);

    }

}
