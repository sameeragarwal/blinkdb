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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.math.util.FastMath;
import org.apache.commons.math.util.NumberTransformer;
import org.apache.commons.math.util.TransformerMap;

/**
 * Test cases for the {@link ListUnivariateImpl} class.
 *
 * @version $Revision: 990655 $ $Date: 2010-08-29 23:49:40 +0200 (dim. 29 août 2010) $
 */

public final class MixedListUnivariateImplTest extends TestCase {
    private double one = 1;
    private float two = 2;
    private int three = 3;

    private double mean = 2;
    private double sumSq = 18;
    private double sum = 8;
    private double var = 0.666666666666666666667;
    private double std = FastMath.sqrt(var);
    private double n = 4;
    private double min = 1;
    private double max = 3;
    private double tolerance = 10E-15;

    private TransformerMap transformers = new TransformerMap();

    public MixedListUnivariateImplTest(String name) {
        super(name);
        transformers = new TransformerMap();

        transformers.putTransformer(Foo.class, new FooTransformer());

        transformers.putTransformer(Bar.class, new BarTransformer());

    }

    /** test stats */
    public void testStats() {
        List<Object> externalList = new ArrayList<Object>();

        DescriptiveStatistics u = new ListUnivariateImpl(externalList,transformers);

        assertEquals("total count", 0, u.getN(), tolerance);
        u.addValue(one);
        u.addValue(two);
        u.addValue(two);
        u.addValue(three);
        assertEquals("N", n, u.getN(), tolerance);
        assertEquals("sum", sum, u.getSum(), tolerance);
        assertEquals("sumsq", sumSq, u.getSumsq(), tolerance);
        assertEquals("var", var, u.getVariance(), tolerance);
        assertEquals("std", std, u.getStandardDeviation(), tolerance);
        assertEquals("mean", mean, u.getMean(), tolerance);
        assertEquals("min", min, u.getMin(), tolerance);
        assertEquals("max", max, u.getMax(), tolerance);
        u.clear();
        assertEquals("total count", 0, u.getN(), tolerance);
    }

    public void testN0andN1Conditions() throws Exception {
        DescriptiveStatistics u = new ListUnivariateImpl(new ArrayList<Object>(),transformers);

        assertTrue(
            "Mean of n = 0 set should be NaN",
            Double.isNaN(u.getMean()));
        assertTrue(
            "Standard Deviation of n = 0 set should be NaN",
            Double.isNaN(u.getStandardDeviation()));
        assertTrue(
            "Variance of n = 0 set should be NaN",
            Double.isNaN(u.getVariance()));

        u.addValue(one);

        assertTrue(
            "Mean of n = 1 set should be value of single item n1, instead it is " + u.getMean() ,
            u.getMean() == one);

        assertTrue(
            "StdDev of n = 1 set should be zero, instead it is: "
                + u.getStandardDeviation(),
            u.getStandardDeviation() == 0);
        assertTrue(
            "Variance of n = 1 set should be zero",
            u.getVariance() == 0);
    }

    public void testSkewAndKurtosis() {
        ListUnivariateImpl u =
            new ListUnivariateImpl(new ArrayList<Object>(), transformers);

        u.addObject("12.5");
        u.addObject(Integer.valueOf(12));
        u.addObject("11.8");
        u.addObject("14.2");
        u.addObject(new Foo());
        u.addObject("14.5");
        u.addObject(Long.valueOf(21));
        u.addObject("8.2");
        u.addObject("10.3");
        u.addObject("11.3");
        u.addObject(Float.valueOf(14.1f));
        u.addObject("9.9");
        u.addObject("12.2");
        u.addObject(new Bar());
        u.addObject("12.1");
        u.addObject("11");
        u.addObject(Double.valueOf(19.8));
        u.addObject("11");
        u.addObject("10");
        u.addObject("8.8");
        u.addObject("9");
        u.addObject("12.3");


        assertEquals("mean", 12.40455, u.getMean(), 0.0001);
        assertEquals("variance", 10.00236, u.getVariance(), 0.0001);
        assertEquals("skewness", 1.437424, u.getSkewness(), 0.0001);
        assertEquals("kurtosis", 2.37719, u.getKurtosis(), 0.0001);
    }

    public void testProductAndGeometricMean() throws Exception {
        ListUnivariateImpl u = new ListUnivariateImpl(new ArrayList<Object>(),transformers);
        u.setWindowSize(10);

        u.addValue(1.0);
        u.addValue(2.0);
        u.addValue(3.0);
        u.addValue(4.0);

        assertEquals(
            "Geometric mean not expected",
            2.213364,
            u.getGeometricMean(),
            0.00001);

        // Now test rolling - StorelessDescriptiveStatistics should discount the contribution
        // of a discarded element
        for (int i = 0; i < 10; i++) {
            u.addValue(i + 2);
        }
        // Values should be (2,3,4,5,6,7,8,9,10,11)
        assertEquals(
            "Geometric mean not expected",
            5.755931,
            u.getGeometricMean(),
            0.00001);

    }

    public static final class Foo {
        public String heresFoo() {
            return "14.9";
        }
    }

    public static final class FooTransformer implements NumberTransformer, Serializable {
        private static final long serialVersionUID = -4252248129291326127L;
        public double transform(Object o) {
            return Double.parseDouble(((Foo) o).heresFoo());
        }
    }

    public static final class Bar {
        public String heresBar() {
            return "12.0";
        }
    }

    public static final class BarTransformer implements NumberTransformer, Serializable {
        private static final long serialVersionUID = -1768345377764262043L;
        public double transform(Object o) {
            return Double.parseDouble(((Bar) o).heresBar());
        }
    }

}
