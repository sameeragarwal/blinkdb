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

import org.apache.commons.math.stat.StatUtils;

import junit.framework.TestCase;

/**
 * This class contains test cases for the ExpandableDoubleArray.
 *
 * @version $Revision: 811685 $ $Date: 2009-09-05 19:36:48 +0200 (sam. 05 sept. 2009) $
 */
public abstract class DoubleArrayAbstractTest extends TestCase {

    protected DoubleArray da = null;

    // Array used to test rolling
    protected DoubleArray ra = null;

    public DoubleArrayAbstractTest(String name) {
        super(name);
    }

    public void testAdd1000() {

        for (int i = 0; i < 1000; i++) {
            da.addElement(i);
        }

        assertEquals(
            "Number of elements should be equal to 1000 after adding 1000 values",
            1000,
            da.getNumElements());

        assertEquals(
            "The element at the 56th index should be 56",
            56.0,
            da.getElement(56),
            Double.MIN_VALUE);

    }

    public void testGetValues() {
        double[] controlArray = { 2.0, 4.0, 6.0 };

        da.addElement(2.0);
        da.addElement(4.0);
        da.addElement(6.0);
        double[] testArray = da.getElements();

        for (int i = 0; i < da.getNumElements(); i++) {
            assertEquals(
                "The testArray values should equal the controlArray values, index i: "
                    + i
                    + " does not match",
                testArray[i],
                controlArray[i],
                Double.MIN_VALUE);
        }

    }

    public void testAddElementRolling() {
        ra.addElement(0.5);
        ra.addElement(1.0);
        ra.addElement(1.0);
        ra.addElement(1.0);
        ra.addElement(1.0);
        ra.addElement(1.0);
        ra.addElementRolling(2.0);

        assertEquals(
            "There should be 6 elements in the eda",
            6,
            ra.getNumElements());
        assertEquals(
            "The max element should be 2.0",
            2.0,
            StatUtils.max(ra.getElements()),
            Double.MIN_VALUE);
        assertEquals(
            "The min element should be 1.0",
            1.0,
            StatUtils.min(ra.getElements()),
            Double.MIN_VALUE);

        for (int i = 0; i < 1024; i++) {
            ra.addElementRolling(i);
        }

        assertEquals(
            "We just inserted 1024 rolling elements, num elements should still be 6",
            6,
            ra.getNumElements());
    }

    public void testMinMax() {
        da.addElement(2.0);
        da.addElement(22.0);
        da.addElement(-2.0);
        da.addElement(21.0);
        da.addElement(22.0);
        da.addElement(42.0);
        da.addElement(62.0);
        da.addElement(22.0);
        da.addElement(122.0);
        da.addElement(1212.0);

        assertEquals("Min should be -2.0", -2.0, StatUtils.min(da.getElements()), Double.MIN_VALUE);
        assertEquals(
            "Max should be 1212.0",
            1212.0,
            StatUtils.max(da.getElements()),
            Double.MIN_VALUE);
    }

}
