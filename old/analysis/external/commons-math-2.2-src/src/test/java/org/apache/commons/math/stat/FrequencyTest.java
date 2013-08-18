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
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

import junit.framework.TestCase;

import org.apache.commons.math.TestUtils;

/**
 * Test cases for the {@link Frequency} class.
 *
 * @version $Revision: 1044981 $ $Date: 2010-12-13 01:43:57 +0100 (lun. 13 déc. 2010) $
 */

public final class FrequencyTest extends TestCase {
    private long oneL = 1;
    private long twoL = 2;
    private long threeL = 3;
    private int oneI = 1;
    private int twoI = 2;
    private int threeI=3;
    private double tolerance = 10E-15;
    private Frequency f = null;

    public FrequencyTest(String name) {
        super(name);
    }

    @Override
    public void setUp() {
        f = new Frequency();
    }

    /** test freq counts */
    @SuppressWarnings("deprecation")
    public void testCounts() {
        assertEquals("total count",0,f.getSumFreq());
        f.addValue(oneL);
        f.addValue(twoL);
        f.addValue(1);
        f.addValue(oneI);
        assertEquals("one frequency count",3,f.getCount(1));
        assertEquals("two frequency count",1,f.getCount(2));
        assertEquals("three frequency count",0,f.getCount(3));
        assertEquals("total count",4,f.getSumFreq());
        assertEquals("zero cumulative frequency", 0, f.getCumFreq(0));
        assertEquals("one cumulative frequency", 3,  f.getCumFreq(1));
        assertEquals("two cumulative frequency", 4,  f.getCumFreq(2));
        assertEquals("Integer argument cum freq",4, f.getCumFreq(Integer.valueOf(2)));
        assertEquals("five cumulative frequency", 4,  f.getCumFreq(5));
        assertEquals("foo cumulative frequency", 0,  f.getCumFreq("foo"));

        f.clear();
        assertEquals("total count",0,f.getSumFreq());

        // userguide examples -------------------------------------------------------------------
        f.addValue("one");
        f.addValue("One");
        f.addValue("oNe");
        f.addValue("Z");
        assertEquals("one cumulative frequency", 1 ,  f.getCount("one"));
        assertEquals("Z cumulative pct", 0.5,  f.getCumPct("Z"), tolerance);
        assertEquals("z cumulative pct", 1.0,  f.getCumPct("z"), tolerance);
        assertEquals("Ot cumulative pct", 0.25,  f.getCumPct("Ot"), tolerance);
        f.clear();

        f = null;
        Frequency f = new Frequency();
        f.addValue(1);
        f.addValue(Integer.valueOf(1));
        f.addValue(Long.valueOf(1));
        f.addValue(2);
        f.addValue(Integer.valueOf(-1));
        assertEquals("1 count", 3, f.getCount(1));
        assertEquals("1 count", 3, f.getCount(Integer.valueOf(1)));
        assertEquals("0 cum pct", 0.2, f.getCumPct(0), tolerance);
        assertEquals("1 pct", 0.6, f.getPct(Integer.valueOf(1)), tolerance);
        assertEquals("-2 cum pct", 0, f.getCumPct(-2), tolerance);
        assertEquals("10 cum pct", 1, f.getCumPct(10), tolerance);

        f = null;
        f = new Frequency(String.CASE_INSENSITIVE_ORDER);
        f.addValue("one");
        f.addValue("One");
        f.addValue("oNe");
        f.addValue("Z");
        assertEquals("one count", 3 ,  f.getCount("one"));
        assertEquals("Z cumulative pct -- case insensitive", 1 ,  f.getCumPct("Z"), tolerance);
        assertEquals("z cumulative pct -- case insensitive", 1 ,  f.getCumPct("z"), tolerance);

        f = null;
        f = new Frequency();
        assertEquals(0L, f.getCount('a'));
        assertEquals(0L, f.getCumFreq('b'));
        TestUtils.assertEquals(Double.NaN, f.getPct('a'), 0.0);
        TestUtils.assertEquals(Double.NaN, f.getCumPct('b'), 0.0);
        f.addValue('a');
        f.addValue('b');
        f.addValue('c');
        f.addValue('d');
        assertEquals(1L, f.getCount('a'));
        assertEquals(2L, f.getCumFreq('b'));
        assertEquals(0.25, f.getPct('a'), 0.0);
        assertEquals(0.5, f.getCumPct('b'), 0.0);
        assertEquals(1.0, f.getCumPct('e'), 0.0);
    }

    /** test pcts */
    @SuppressWarnings("deprecation")
    public void testPcts() {
        f.addValue(oneL);
        f.addValue(twoL);
        f.addValue(oneI);
        f.addValue(twoI);
        f.addValue(threeL);
        f.addValue(threeL);
        f.addValue(3);
        f.addValue(threeI);
        assertEquals("one pct",0.25,f.getPct(1),tolerance);
        assertEquals("two pct",0.25,f.getPct(Long.valueOf(2)),tolerance);
        assertEquals("three pct",0.5,f.getPct(threeL),tolerance);
        // MATH-329
        assertEquals("three (Object) pct",0.5,f.getPct((Object) (Integer.valueOf(3))),tolerance);
        assertEquals("five pct",0,f.getPct(5),tolerance);
        assertEquals("foo pct",0,f.getPct("foo"),tolerance);
        assertEquals("one cum pct",0.25,f.getCumPct(1),tolerance);
        assertEquals("two cum pct",0.50,f.getCumPct(Long.valueOf(2)),tolerance);
        assertEquals("Integer argument",0.50,f.getCumPct(Integer.valueOf(2)),tolerance);
        assertEquals("three cum pct",1.0,f.getCumPct(threeL),tolerance);
        assertEquals("five cum pct",1.0,f.getCumPct(5),tolerance);
        assertEquals("zero cum pct",0.0,f.getCumPct(0),tolerance);
        assertEquals("foo cum pct",0,f.getCumPct("foo"),tolerance);
    }

    /** test adding incomparable values */
    @SuppressWarnings("deprecation")
    public void testAdd() {
        char aChar = 'a';
        char bChar = 'b';
        String aString = "a";
        f.addValue(aChar);
        f.addValue(bChar);
        try {
            f.addValue(aString);
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            f.addValue(2);
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        assertEquals("a pct",0.5,f.getPct(aChar),tolerance);
        assertEquals("b cum pct",1.0,f.getCumPct(bChar),tolerance);
        assertEquals("a string pct",0.0,f.getPct(aString),tolerance);
        assertEquals("a string cum pct",0.0,f.getCumPct(aString),tolerance);

        f = new Frequency();
        f.addValue("One");
        try {
            f.addValue(new Integer("One"));
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    // Check what happens when non-Comparable objects are added
    @SuppressWarnings("deprecation")
    public void testAddNonComparable(){
        try {
            f.addValue(new Object()); // This was previously OK
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
        f.clear();
        f.addValue(1);
        try {
            f.addValue(new Object());
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    /** test empty table */
    public void testEmptyTable() {
        assertEquals("freq sum, empty table", 0, f.getSumFreq());
        assertEquals("count, empty table", 0, f.getCount(0));
        assertEquals("count, empty table",0, f.getCount(Integer.valueOf(0)));
        assertEquals("cum freq, empty table", 0, f.getCumFreq(0));
        assertEquals("cum freq, empty table", 0, f.getCumFreq("x"));
        assertTrue("pct, empty table", Double.isNaN(f.getPct(0)));
        assertTrue("pct, empty table", Double.isNaN(f.getPct(Integer.valueOf(0))));
        assertTrue("cum pct, empty table", Double.isNaN(f.getCumPct(0)));
        assertTrue("cum pct, empty table", Double.isNaN(f.getCumPct(Integer.valueOf(0))));
    }

    /**
     * Tests toString()
     */
    public void testToString(){
        f.addValue(oneL);
        f.addValue(twoL);
        f.addValue(oneI);
        f.addValue(twoI);

        String s = f.toString();
        //System.out.println(s);
        assertNotNull(s);
        BufferedReader reader = new BufferedReader(new StringReader(s));
        try {
            String line = reader.readLine(); // header line
            assertNotNull(line);

            line = reader.readLine(); // one's or two's line
            assertNotNull(line);

            line = reader.readLine(); // one's or two's line
            assertNotNull(line);

            line = reader.readLine(); // no more elements
            assertNull(line);
        } catch(IOException ex){
            fail(ex.getMessage());
        }
    }
    @SuppressWarnings("deprecation")
    public void testIntegerValues() {
        Comparable<?> obj1 = null;
        obj1 = Integer.valueOf(1);
        Integer int1 = Integer.valueOf(1);
        f.addValue(obj1);
        f.addValue(int1);
        f.addValue(2);
        f.addValue(Long.valueOf(2));
        assertEquals("Integer 1 count", 2, f.getCount(1));
        assertEquals("Integer 1 count", 2, f.getCount(Integer.valueOf(1)));
        assertEquals("Integer 1 count", 2, f.getCount(Long.valueOf(1)));
        assertEquals("Integer 1 cumPct", 0.5, f.getCumPct(1), tolerance);
        assertEquals("Integer 1 cumPct", 0.5, f.getCumPct(Long.valueOf(1)), tolerance);
        assertEquals("Integer 1 cumPct", 0.5, f.getCumPct(Integer.valueOf(1)), tolerance);
        Iterator<?> it = f.valuesIterator();
        while (it.hasNext()) {
            assertTrue(it.next() instanceof Long);
        }
    }

    public void testSerial() {
        f.addValue(oneL);
        f.addValue(twoL);
        f.addValue(oneI);
        f.addValue(twoI);
        assertEquals(f, TestUtils.serializeAndRecover(f));
    }
    
    public void testGetUniqueCount() {
        assertEquals(0, f.getUniqueCount());
        f.addValue(oneL);
        assertEquals(1, f.getUniqueCount());
        f.addValue(oneL);
        assertEquals(1, f.getUniqueCount());
        f.addValue(twoI);
        assertEquals(2, f.getUniqueCount());
    }
}

