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
package org.apache.commons.math.stat.descriptive;

import org.apache.commons.math.TestUtils;
import org.apache.commons.math.stat.descriptive.moment.SecondMoment;
import org.apache.commons.math.util.FastMath;

/**
 * Test cases for {@link StorelessUnivariateStatistic} classes.
 * @version $Revision: 990655 $ $Date: 2010-08-29 23:49:40 +0200 (dim. 29 août 2010) $
 */
public abstract class StorelessUnivariateStatisticAbstractTest
    extends UnivariateStatisticAbstractTest {

    public StorelessUnivariateStatisticAbstractTest(String name) {
        super(name);
    }

    /** Small sample arrays */
    protected double[][] smallSamples = {{}, {1}, {1,2}, {1,2,3}, {1,2,3,4}};

    /** Return a new instance of the statistic */
    @Override
    public abstract UnivariateStatistic getUnivariateStatistic();

    /**Expected value for  the testArray defined in UnivariateStatisticAbstractTest */
    @Override
    public abstract double expectedValue();

    /**
     *  Verifies that increment() and incrementAll work properly.
     */
    public void testIncrementation() throws Exception {

        StorelessUnivariateStatistic statistic =
            (StorelessUnivariateStatistic) getUnivariateStatistic();

        // Add testArray one value at a time and check result
        for (int i = 0; i < testArray.length; i++) {
            statistic.increment(testArray[i]);
        }

        assertEquals(expectedValue(), statistic.getResult(), getTolerance());
        assertEquals(testArray.length, statistic.getN());

        statistic.clear();

        // Add testArray all at once and check again
        statistic.incrementAll(testArray);
        assertEquals(expectedValue(), statistic.getResult(), getTolerance());
        assertEquals(testArray.length, statistic.getN());

        statistic.clear();

        // Cleared
        assertTrue(Double.isNaN(statistic.getResult()));
        assertEquals(0, statistic.getN());

    }

    public void testSerialization() throws Exception {

        StorelessUnivariateStatistic statistic =
            (StorelessUnivariateStatistic) getUnivariateStatistic();

        TestUtils.checkSerializedEquality(statistic);

        statistic.clear();

        for (int i = 0; i < testArray.length; i++) {
            statistic.increment(testArray[i]);
            if(i % 5 == 0)
                statistic = (StorelessUnivariateStatistic)TestUtils.serializeAndRecover(statistic);
        }

        TestUtils.checkSerializedEquality(statistic);

        assertEquals(expectedValue(), statistic.getResult(), getTolerance());

        statistic.clear();

        assertTrue(Double.isNaN(statistic.getResult()));

    }

    public void testEqualsAndHashCode() {
        StorelessUnivariateStatistic statistic =
            (StorelessUnivariateStatistic) getUnivariateStatistic();
        StorelessUnivariateStatistic statistic2 = null;

        assertTrue("non-null, compared to null", !statistic.equals(statistic2));
        assertTrue("reflexive, non-null", statistic.equals(statistic));

        int emptyHash = statistic.hashCode();
        statistic2 = (StorelessUnivariateStatistic) getUnivariateStatistic();
        assertTrue("empty stats should be equal", statistic.equals(statistic2));
        assertEquals("empty stats should have the same hashcode",
                emptyHash, statistic2.hashCode());

        statistic.increment(1d);
        assertTrue("reflexive, non-empty", statistic.equals(statistic));
        assertTrue("non-empty, compared to empty", !statistic.equals(statistic2));
        assertTrue("non-empty, compared to empty", !statistic2.equals(statistic));
        assertTrue("non-empty stat should have different hashcode from empty stat",
                statistic.hashCode() != emptyHash);

        statistic2.increment(1d);
        assertTrue("stats with same data should be equal", statistic.equals(statistic2));
        assertEquals("stats with same data should have the same hashcode",
                statistic.hashCode(), statistic2.hashCode());

        statistic.increment(Double.POSITIVE_INFINITY);
        assertTrue("stats with different n's should not be equal", !statistic2.equals(statistic));
        assertTrue("stats with different n's should have different hashcodes",
                statistic.hashCode() != statistic2.hashCode());

        statistic2.increment(Double.POSITIVE_INFINITY);
        assertTrue("stats with same data should be equal", statistic.equals(statistic2));
        assertEquals("stats with same data should have the same hashcode",
                statistic.hashCode(), statistic2.hashCode());

        statistic.clear();
        statistic2.clear();
        assertTrue("cleared stats should be equal", statistic.equals(statistic2));
        assertEquals("cleared stats should have thashcode of empty stat",
                emptyHash, statistic2.hashCode());
        assertEquals("cleared stats should have thashcode of empty stat",
                emptyHash, statistic.hashCode());

    }

    public void testMomentSmallSamples() {
        UnivariateStatistic stat = getUnivariateStatistic();
        if (stat instanceof SecondMoment) {
            SecondMoment moment = (SecondMoment) getUnivariateStatistic();
            assertTrue(Double.isNaN(moment.getResult()));
            moment.increment(1d);
            assertEquals(0d, moment.getResult(), 0);
        }
    }

    /**
     * Make sure that evaluate(double[]) and inrementAll(double[]),
     * getResult() give same results.
     */
    public void testConsistency() {
        StorelessUnivariateStatistic stat = (StorelessUnivariateStatistic) getUnivariateStatistic();
        stat.incrementAll(testArray);
        assertEquals(stat.getResult(), stat.evaluate(testArray), getTolerance());
        for (int i = 0; i < smallSamples.length; i++) {
            stat.clear();
            for (int j =0; j < smallSamples[i].length; j++) {
                stat.increment(smallSamples[i][j]);
            }
            TestUtils.assertEquals(stat.getResult(), stat.evaluate(smallSamples[i]), getTolerance());
        }
    }

    /**
     * Verifies that copied statistics remain equal to originals when
     * incremented the same way.
     *
     */
    public void testCopyConsistency() {

        StorelessUnivariateStatistic master =
            (StorelessUnivariateStatistic) getUnivariateStatistic();

        StorelessUnivariateStatistic replica = null;

        // Randomly select a portion of testArray to load first
        long index = FastMath.round((FastMath.random()) * testArray.length);

        // Put first half in master and copy master to replica
        master.incrementAll(testArray, 0, (int) index);
        replica = master.copy();

        // Check same
        assertTrue(replica.equals(master));
        assertTrue(master.equals(replica));

        // Now add second part to both and check again
        master.incrementAll(testArray,
                (int) index, (int) (testArray.length - index));
        replica.incrementAll(testArray,
                (int) index, (int) (testArray.length - index));
        assertTrue(replica.equals(master));
        assertTrue(master.equals(replica));
    }

    public void testSerial() {
        StorelessUnivariateStatistic s =
            (StorelessUnivariateStatistic) getUnivariateStatistic();
        assertEquals(s, TestUtils.serializeAndRecover(s));
    }
}
