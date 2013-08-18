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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test to compare FastMath results against StrictMath results for boundary values.
 * <p>
 * Running all tests independently: <br/>
 * {@code mvn test -Dtest=FastMathStrictComparisonTest}<br/>
 * or just run tests against a single method (e.g. scalb):<br/>
 * {@code mvn test -Dtest=FastMathStrictComparisonTest -DargLine="-DtestMethod=scalb"}
 */
@RunWith(Parameterized.class)
public class FastMathStrictComparisonTest {

    // Values which often need special handling
    private static final Double[] DOUBLE_SPECIAL_VALUES = {
        -0.0, +0.0,                                         // 1,2
        Double.NaN,                                         // 3
        Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, // 4,5
        -Double.MAX_VALUE, Double.MAX_VALUE,                // 6,7
        // decreasing order of absolute value to help catch first failure
        -MathUtils.EPSILON, MathUtils.EPSILON,              // 8,9
        -MathUtils.SAFE_MIN, MathUtils.SAFE_MIN,            // 10,11
        -Double.MIN_VALUE, Double.MIN_VALUE,                // 12,13
    };

    private static final Float [] FLOAT_SPECIAL_VALUES = {
        -0.0f, +0.0f,                                       // 1,2
        Float.NaN,                                          // 3
        Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY,   // 4,5
        Float.MIN_VALUE, Float.MAX_VALUE,                   // 6,7
        -Float.MIN_VALUE, -Float.MAX_VALUE,                 // 8,9
    };

    private static final Object [] LONG_SPECIAL_VALUES = {
        -1,0,1,                                             // 1,2,3
        Long.MIN_VALUE, Long.MAX_VALUE,                     // 4,5
    };

    private static final Object[] INT_SPECIAL_VALUES = {
        -1,0,1,                                             // 1,2,3
        Integer.MIN_VALUE, Integer.MAX_VALUE,               // 4,5
    };

    private final Method mathMethod;
    private final Method fastMethod;
    private final Type[] types;
    private final Object[][] valueArrays;

    public FastMathStrictComparisonTest(Method m, Method f, Type[] types, Object[][] data) throws Exception{
        this.mathMethod=m;
        this.fastMethod=f;
        this.types=types;
        this.valueArrays=data;
    }

    @Test
    public void test1() throws Exception{
        setupMethodCall(mathMethod, fastMethod, types, valueArrays);
    }
    private static boolean isNumber(Double d) {
        return !(d.isInfinite() || d.isNaN());
    }

    private static boolean isNumber(Float f) {
        return !(f.isInfinite() || f.isNaN());
    }

    private static void reportFailedResults(Method mathMethod, Object[] params, Object expected, Object actual, int[] entries){
        final String methodName = mathMethod.getName();
        String format = null;
        long actL=0;
        long expL=0;
        if (expected instanceof Double) {
            Double exp = (Double) expected;
            Double act = (Double) actual;
            if (isNumber(exp) && isNumber(act) && exp != 0) { // show difference as hex
                actL = Double.doubleToLongBits(act);
                expL = Double.doubleToLongBits(exp);
                if (Math.abs(actL-expL)==1) {
                    // Not 100% sure off-by-one errors are allowed everywhere, so only allow for these methods
                    if (methodName.equals("toRadians") || methodName.equals("atan2")) {
                        return;
                    }
                }
                format = "%016x";
            }
        } else if (expected instanceof Float ){
            Float exp = (Float) expected;
            Float act = (Float) actual;
            if (isNumber(exp) && isNumber(act) && exp != 0) { // show difference as hex
                actL = Float.floatToIntBits(act);
                expL = Float.floatToIntBits(exp);
                format = "%08x";
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append(mathMethod.getReturnType().getSimpleName());
        sb.append(" ");
        sb.append(methodName);
        sb.append("(");
        String sep = "";
        for(Object o : params){
            sb.append(sep);
            sb.append(o);
            sep=", ";
        }
        sb.append(") expected ");
        if (format != null){
            sb.append(String.format(format, expL));
        } else {
            sb.append(expected);
        }
        sb.append(" actual ");
        if (format != null){
            sb.append(String.format(format, actL));
        } else {
            sb.append(actual);
        }
        sb.append(" entries ");
        sb.append(Arrays.toString(entries));
        String message = sb.toString();
        final boolean fatal = true;
        if (fatal) {
            Assert.fail(message);
        } else {
            System.out.println(message);
        }
    }

    private static void callMethods(Method mathMethod, Method fastMethod,
            Object[] params, int[] entries) throws IllegalAccessException,
            InvocationTargetException {
        try {
            Object expected = mathMethod.invoke(mathMethod, params);
            Object actual = fastMethod.invoke(mathMethod, params);
            if (!expected.equals(actual)) {
                reportFailedResults(mathMethod, params, expected, actual, entries);
            }
        } catch (IllegalArgumentException e) {
            Assert.fail(mathMethod+" "+e);
        }
    }

    private static void setupMethodCall(Method mathMethod, Method fastMethod,
            Type[] types, Object[][] valueArrays) throws Exception {
        Object[] params = new Object[types.length];
        int entry1 = 0;
        int[] entries = new int[types.length];
        for(Object d : valueArrays[0]) {
            entry1++;
            params[0] = d;
            entries[0] = entry1;
            if (params.length > 1){
                int entry2 = 0;
                for(Object d1 : valueArrays[1]) {
                    entry2++;
                    params[1] = d1;
                    entries[1] = entry2;
                    callMethods(mathMethod, fastMethod, params, entries);
                }
            } else {
                callMethods(mathMethod, fastMethod, params, entries);
            }
        }
    }

    @Parameters
    public static List<Object[]> data() throws Exception {
        String singleMethod = System.getProperty("testMethod");
        List<Object[]> list = new ArrayList<Object[]>();
        for(Method mathMethod : StrictMath.class.getDeclaredMethods()) {
            method:
            if (Modifier.isPublic(mathMethod.getModifiers())){// Only test public methods
                Type []types = mathMethod.getGenericParameterTypes();
                if (types.length >=1) { // Only check methods with at least one parameter
                    try {
                        // Get the corresponding FastMath method
                        Method fastMethod = FastMath.class.getDeclaredMethod(mathMethod.getName(), (Class[]) types);
                        if (Modifier.isPublic(fastMethod.getModifiers())) { // It must be public too
                            if (singleMethod != null && !fastMethod.getName().equals(singleMethod)) {
                                break method;
                            }
                            Object [][] values = new Object[types.length][];
                            int index = 0;
                            for(Type t : types) {
                                if (t.equals(double.class)){
                                    values[index]=DOUBLE_SPECIAL_VALUES;
                                } else if (t.equals(float.class)) {
                                    values[index]=FLOAT_SPECIAL_VALUES;
                                } else if (t.equals(long.class)) {
                                    values[index]=LONG_SPECIAL_VALUES;
                                } else if (t.equals(int.class)) {
                                    values[index]=INT_SPECIAL_VALUES;
                                } else {
                                    System.out.println("Cannot handle class "+t+" for "+mathMethod);
                                    break method;
                                }
                                index++;
                            }
//                            System.out.println(fastMethod);
                            /*
                             * The current implementation runs each method as a separate test.
                             * Could be amended to run each value as a separate test
                             */
                            list.add(new Object[]{mathMethod, fastMethod, types, values});
//                            setupMethodCall(mathMethod, fastMethod, params, data);
                        } else {
                            System.out.println("Cannot find public FastMath method corresponding to: "+mathMethod);
                        }
                    } catch (NoSuchMethodException e) {
                        System.out.println("Cannot find FastMath method corresponding to: "+mathMethod);
                    }
                }
            }
        }
        return list;
    }
}
