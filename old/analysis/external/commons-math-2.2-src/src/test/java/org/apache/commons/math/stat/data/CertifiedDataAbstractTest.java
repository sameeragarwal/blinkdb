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

package org.apache.commons.math.stat.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.math.TestUtils;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;

/**
 * @version $Revision: 811685 $ $Date: 2009-09-05 19:36:48 +0200 (sam. 05 sept. 2009) $
 */
public abstract class CertifiedDataAbstractTest extends TestCase {

    private DescriptiveStatistics descriptives;

    private SummaryStatistics summaries;

    private Map<String, Double> certifiedValues;

    @Override
    protected void setUp() throws Exception {
        descriptives = new DescriptiveStatistics();
        summaries = new SummaryStatistics();
        certifiedValues = new HashMap<String, Double>();

        loadData();
    }

    private void loadData() throws IOException {
        BufferedReader in = null;

        try {
            URL resourceURL = getClass().getClassLoader().getResource(getResourceName());
            in = new BufferedReader(new InputStreamReader(resourceURL.openStream()));

            String line = in.readLine();
            while (line != null) {

                /* this call to StringUtils did little for the
                 * following conditional structure
                 */
                line = line.trim();

                // not empty line or comment
                if (!("".equals(line) || line.startsWith("#"))) {
                    int n = line.indexOf('=');
                    if (n == -1) {
                        // data value
                        double value = Double.parseDouble(line);
                        descriptives.addValue(value);
                        summaries.addValue(value);
                    } else {
                        // certified value
                        String name = line.substring(0, n).trim();
                        String valueString = line.substring(n + 1).trim();
                        Double value = Double.valueOf(valueString);
                        certifiedValues.put(name, value);
                    }
                }
                line = in.readLine();
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    protected abstract String getResourceName();

    protected double getMaximumAbsoluteError() {
        return 1.0e-5;
    }

    @Override
    protected void tearDown() throws Exception {
        descriptives.clear();
        descriptives = null;

        summaries.clear();
        summaries = null;

        certifiedValues.clear();
        certifiedValues = null;
    }

    public void testCertifiedValues() {
        for (String name : certifiedValues.keySet()) {
            Double expectedValue = certifiedValues.get(name);

            Double summariesValue = getProperty(summaries, name);
            if (summariesValue != null) {
                TestUtils.assertEquals("summary value for " + name + " is incorrect.",
                                       summariesValue.doubleValue(), expectedValue.doubleValue(),
                                       getMaximumAbsoluteError());
            }

            Double descriptivesValue = getProperty(descriptives, name);
            if (descriptivesValue != null) {
                TestUtils.assertEquals("descriptive value for " + name + " is incorrect.",
                                       descriptivesValue.doubleValue(), expectedValue.doubleValue(),
                                       getMaximumAbsoluteError());
            }
        }
    }


    protected Double getProperty(Object bean, String name) {
        try {
            // Get the value of prop
            String prop = "get" + name.substring(0,1).toUpperCase() + name.substring(1);
            Method meth = bean.getClass().getMethod(prop, new Class[0]);
            Object property = meth.invoke(bean, new Object[0]);
            if (meth.getReturnType().equals(Double.TYPE)) {
                return (Double) property;
            } else if (meth.getReturnType().equals(Long.TYPE)) {
                return Double.valueOf(((Long) property).doubleValue());
            } else {
                fail("wrong type: " + meth.getReturnType().getName());
            }
        } catch (NoSuchMethodException nsme) {
            // ignored
        } catch (InvocationTargetException ite) {
            fail(ite.getMessage());
        } catch (IllegalAccessException iae) {
            fail(iae.getMessage());
        }
        return null;
    }
}
