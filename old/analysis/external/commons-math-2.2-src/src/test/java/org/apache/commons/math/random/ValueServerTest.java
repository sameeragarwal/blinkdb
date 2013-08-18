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
package org.apache.commons.math.random;

import java.io.EOFException;
import java.net.URL;

import org.apache.commons.math.RetryTestCase;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;

/**
 * Test cases for the ValueServer class.
 *
 * @version $Revision: 1003907 $ $Date: 2010-10-03 00:23:34 +0200 (dim. 03 oct. 2010) $
 */

public final class ValueServerTest extends RetryTestCase {

    private ValueServer vs = new ValueServer();

    public ValueServerTest(String name) {
        super(name);
    }

    @Override
    public void setUp() {
        vs.setMode(ValueServer.DIGEST_MODE);
        URL url = getClass().getResource("testData.txt");
        vs.setValuesFileURL(url);
    }

    /**
      * Generate 1000 random values and make sure they look OK.<br>
      * Note that there is a non-zero (but very small) probability that
      * these tests will fail even if the code is working as designed.
      */
    public void testNextDigest() throws Exception{
        double next = 0.0;
        double tolerance = 0.1;
        vs.computeDistribution();
        assertTrue("empirical distribution property",
            vs.getEmpiricalDistribution() != null);
        SummaryStatistics stats = new SummaryStatistics();
        for (int i = 1; i < 1000; i++) {
            next = vs.getNext();
            stats.addValue(next);
        }
        assertEquals("mean", 5.069831575018909, stats.getMean(), tolerance);
        assertEquals
         ("std dev", 1.0173699343977738, stats.getStandardDeviation(),
            tolerance);

        vs.computeDistribution(500);
        stats = new SummaryStatistics();
        for (int i = 1; i < 1000; i++) {
            next = vs.getNext();
            stats.addValue(next);
        }
        assertEquals("mean", 5.069831575018909, stats.getMean(), tolerance);
        assertEquals
         ("std dev", 1.0173699343977738, stats.getStandardDeviation(),
            tolerance);

    }

    /**
      * Make sure exception thrown if digest getNext is attempted
      * before loading empiricalDistribution.
      */
    public void testNextDigestFail() throws Exception {
        try {
            vs.getNext();
            fail("Expecting IllegalStateException");
        } catch (IllegalStateException ex) {}
    }

    public void testEmptyReplayFile() throws Exception {
        try {
            URL url = getClass().getResource("emptyFile.txt");
            vs.setMode(ValueServer.REPLAY_MODE);
            vs.setValuesFileURL(url);
            vs.getNext();
            fail("an exception should have been thrown");
        } catch (EOFException eof) {
            // expected behavior
        }
    }

    public void testEmptyDigestFile() throws Exception {
        try {
            URL url = getClass().getResource("emptyFile.txt");
            vs.setMode(ValueServer.DIGEST_MODE);
            vs.setValuesFileURL(url);
            vs.computeDistribution();
            fail("an exception should have been thrown");
        } catch (EOFException eof) {
            // expected behavior
        }
    }

    /**
     * Test ValueServer REPLAY_MODE using values in testData file.<br>
     * Check that the values 1,2,1001,1002 match data file values 1 and 2.
     * the sample data file.
     */
    public void testReplay() throws Exception {
        double firstDataValue = 4.038625496201205;
        double secondDataValue = 3.6485326248346936;
        double tolerance = 10E-15;
        double compareValue = 0.0d;
        vs.setMode(ValueServer.REPLAY_MODE);
        vs.resetReplayFile();
        compareValue = vs.getNext();
        assertEquals(compareValue,firstDataValue,tolerance);
        compareValue = vs.getNext();
        assertEquals(compareValue,secondDataValue,tolerance);
        for (int i = 3; i < 1001; i++) {
           compareValue = vs.getNext();
        }
        compareValue = vs.getNext();
        assertEquals(compareValue,firstDataValue,tolerance);
        compareValue = vs.getNext();
        assertEquals(compareValue,secondDataValue,tolerance);
        vs.closeReplayFile();
        // make sure no NPE
        vs.closeReplayFile();
    }

    /**
     * Test other ValueServer modes
     */
    public void testModes() throws Exception {
        vs.setMode(ValueServer.CONSTANT_MODE);
        vs.setMu(0);
        assertEquals("constant mode test",vs.getMu(),vs.getNext(),Double.MIN_VALUE);
        vs.setMode(ValueServer.UNIFORM_MODE);
        vs.setMu(2);
        double val = vs.getNext();
        assertTrue(val > 0 && val < 4);
        vs.setSigma(1);
        vs.setMode(ValueServer.GAUSSIAN_MODE);
        val = vs.getNext();
        assertTrue("gaussian value close enough to mean",
            val < vs.getMu() + 100*vs.getSigma());
        vs.setMode(ValueServer.EXPONENTIAL_MODE);
        val = vs.getNext();
        assertTrue(val > 0);
        try {
            vs.setMode(1000);
            vs.getNext();
            fail("bad mode, expecting IllegalStateException");
        } catch (IllegalStateException ex) {
            // ignored
        }
    }

    /**
     * Test fill
     */
    public void testFill() throws Exception {
        vs.setMode(ValueServer.CONSTANT_MODE);
        vs.setMu(2);
        double[] val = new double[5];
        vs.fill(val);
        for (int i = 0; i < 5; i++) {
            assertEquals("fill test in place",2,val[i],Double.MIN_VALUE);
        }
        double v2[] = vs.fill(3);
        for (int i = 0; i < 3; i++) {
            assertEquals("fill test in place",2,v2[i],Double.MIN_VALUE);
        }
    }

    /**
     * Test getters to make Clover happy
     */
    public void testProperties() throws Exception {
        vs.setMode(ValueServer.CONSTANT_MODE);
        assertEquals("mode test",ValueServer.CONSTANT_MODE,vs.getMode());
        vs.setValuesFileURL("http://www.apache.org");
        URL url = vs.getValuesFileURL();
        assertEquals("valuesFileURL test","http://www.apache.org",url.toString());
    }

}
