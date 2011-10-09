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
package org.apache.commons.math.stat;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import junit.framework.TestCase;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;

/**
 * Certified data test cases.
 * @version $Revision: 902201 $ $Date: 2010-01-22 19:18:16 +0100 (ven. 22 janv. 2010) $
 */
public class CertifiedDataTest extends TestCase  {

    protected double mean = Double.NaN;

    protected double std = Double.NaN;

    /**
     * Certified Data Test Constructor
     * @param name
     */
    public CertifiedDataTest(String name) {
        super(name);
    }

    /**
     * Test SummaryStatistics - implementations that do not store the data
     * and use single pass algorithms to compute statistics
    */
    public void testSummaryStatistics() throws Exception {
        SummaryStatistics u = new SummaryStatistics();
        loadStats("data/PiDigits.txt", u);
        assertEquals("PiDigits: std", std, u.getStandardDeviation(), 1E-13);
        assertEquals("PiDigits: mean", mean, u.getMean(), 1E-13);

        loadStats("data/Mavro.txt", u);
        assertEquals("Mavro: std", std, u.getStandardDeviation(), 1E-14);
        assertEquals("Mavro: mean", mean, u.getMean(), 1E-14);

        loadStats("data/Michelso.txt", u);
        assertEquals("Michelso: std", std, u.getStandardDeviation(), 1E-13);
        assertEquals("Michelso: mean", mean, u.getMean(), 1E-13);

        loadStats("data/NumAcc1.txt", u);
        assertEquals("NumAcc1: std", std, u.getStandardDeviation(), 1E-14);
        assertEquals("NumAcc1: mean", mean, u.getMean(), 1E-14);

        loadStats("data/NumAcc2.txt", u);
        assertEquals("NumAcc2: std", std, u.getStandardDeviation(), 1E-14);
        assertEquals("NumAcc2: mean", mean, u.getMean(), 1E-14);
    }

    /**
     * Test DescriptiveStatistics - implementations that store full array of
     * values and execute multi-pass algorithms
     */
    public void testDescriptiveStatistics() throws Exception {

        DescriptiveStatistics u = new DescriptiveStatistics();

        loadStats("data/PiDigits.txt", u);
        assertEquals("PiDigits: std", std, u.getStandardDeviation(), 1E-14);
        assertEquals("PiDigits: mean", mean, u.getMean(), 1E-14);

        loadStats("data/Mavro.txt", u);
        assertEquals("Mavro: std", std, u.getStandardDeviation(), 1E-14);
        assertEquals("Mavro: mean", mean, u.getMean(), 1E-14);

        loadStats("data/Michelso.txt", u);
        assertEquals("Michelso: std", std, u.getStandardDeviation(), 1E-14);
        assertEquals("Michelso: mean", mean, u.getMean(), 1E-14);

        loadStats("data/NumAcc1.txt", u);
        assertEquals("NumAcc1: std", std, u.getStandardDeviation(), 1E-14);
        assertEquals("NumAcc1: mean", mean, u.getMean(), 1E-14);

        loadStats("data/NumAcc2.txt", u);
        assertEquals("NumAcc2: std", std, u.getStandardDeviation(), 1E-14);
        assertEquals("NumAcc2: mean", mean, u.getMean(), 1E-14);
    }

    /**
     * loads a DescriptiveStatistics off of a test file
     * @param file
     * @param statistical summary
     */
    private void loadStats(String resource, Object u) throws Exception {

        DescriptiveStatistics d = null;
        SummaryStatistics s = null;
        if (u instanceof DescriptiveStatistics) {
            d = (DescriptiveStatistics) u;
        } else {
            s = (SummaryStatistics) u;
        }
        u.getClass().getDeclaredMethod(
                "clear", new Class[]{}).invoke(u, new Object[]{});
        mean = Double.NaN;
        std = Double.NaN;

        BufferedReader in =
            new BufferedReader(
                    new InputStreamReader(
                            CertifiedDataTest.class.getResourceAsStream(resource)));

        String line = null;

        for (int j = 0; j < 60; j++) {
            line = in.readLine();
            if (j == 40) {
                mean =
                    Double.parseDouble(
                            line.substring(line.lastIndexOf(":") + 1).trim());
            }
            if (j == 41) {
                std =
                    Double.parseDouble(
                            line.substring(line.lastIndexOf(":") + 1).trim());
            }
        }

        line = in.readLine();

        while (line != null) {
            if (d != null) {
                d.addValue(Double.parseDouble(line.trim()));
            }  else {
                s.addValue(Double.parseDouble(line.trim()));
            }
            line = in.readLine();
        }

        in.close();
    }
}
