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

package org.apache.commons.math;

import java.util.Locale;

import org.apache.commons.math.exception.util.LocalizedFormats;

import junit.framework.TestCase;

/**
 * @version $Revision: 1042336 $ $Date: 2010-12-05 13:40:48 +0100 (dim. 05 déc. 2010) $
 * @deprecated in 2.2 (to be removed in 3.0)
 */
@Deprecated
public class FunctionEvaluationExceptionTest extends TestCase {

    public void testConstructor(){
        FunctionEvaluationException ex = new FunctionEvaluationException(0.0);
        assertNull(ex.getCause());
        assertNotNull(ex.getMessage());
        assertTrue(ex.getMessage().indexOf("0") > 0);
        assertEquals(0.0, ex.getArgument()[0], 0);
    }

    public void testConstructorArray(){
        FunctionEvaluationException ex =
            new FunctionEvaluationException(new double[] { 0, 1, 2 });
        assertNull(ex.getCause());
        assertNotNull(ex.getMessage());
        assertTrue(ex.getMessage().indexOf("0") > 0);
        assertEquals(0.0, ex.getArgument()[0], 0);
        assertEquals(1.0, ex.getArgument()[1], 0);
        assertEquals(2.0, ex.getArgument()[2], 0);
    }

    public void testConstructorPatternArguments(){
        LocalizedFormats pattern = LocalizedFormats.EVALUATION_FAILED;
        Object[] arguments = { Double.valueOf(0.0) };
        FunctionEvaluationException ex = new FunctionEvaluationException(0.0, pattern, arguments);
        assertNull(ex.getCause());
        assertEquals(pattern, ex.getGeneralPattern());
        assertEquals(arguments.length, ex.getArguments().length);
        for (int i = 0; i < arguments.length; ++i) {
            assertEquals(arguments[i], ex.getArguments()[i]);
        }
        assertFalse(pattern.equals(ex.getMessage()));
        assertFalse(ex.getMessage().equals(ex.getMessage(Locale.FRENCH)));
    }

    public void testConstructorArrayPatternArguments(){
        LocalizedFormats pattern = LocalizedFormats.EVALUATION_FAILED;
        Object[] arguments = { Double.valueOf(0.0) };
        FunctionEvaluationException ex =
            new FunctionEvaluationException(new double[] { 0, 1, 2 }, pattern, arguments);
        assertNull(ex.getCause());
        assertEquals(pattern, ex.getGeneralPattern());
        assertEquals(arguments.length, ex.getArguments().length);
        for (int i = 0; i < arguments.length; ++i) {
            assertEquals(arguments[i], ex.getArguments()[i]);
        }
        assertFalse(pattern.equals(ex.getMessage()));
        assertFalse(ex.getMessage().equals(ex.getMessage(Locale.FRENCH)));
        assertEquals(0.0, ex.getArgument()[0], 0);
        assertEquals(1.0, ex.getArgument()[1], 0);
        assertEquals(2.0, ex.getArgument()[2], 0);
    }

    public void testConstructorPatternArgumentsCause(){
        LocalizedFormats pattern = LocalizedFormats.EVALUATION_FAILED;
        Object[] arguments = { Double.valueOf(0.0) };
        String inMsg = "inner message";
        Exception cause = new Exception(inMsg);
        FunctionEvaluationException ex = new FunctionEvaluationException(cause, 0.0, pattern, arguments);
        assertEquals(cause, ex.getCause());
        assertEquals(pattern, ex.getGeneralPattern());
        assertEquals(arguments.length, ex.getArguments().length);
        for (int i = 0; i < arguments.length; ++i) {
            assertEquals(arguments[i], ex.getArguments()[i]);
        }
        assertFalse(pattern.equals(ex.getMessage()));
        assertFalse(ex.getMessage().equals(ex.getMessage(Locale.FRENCH)));
    }

    public void testConstructorArrayPatternArgumentsCause(){
        LocalizedFormats pattern = LocalizedFormats.EVALUATION_FAILED;
        Object[] arguments = { Double.valueOf(0.0) };
        String inMsg = "inner message";
        Exception cause = new Exception(inMsg);
        FunctionEvaluationException ex =
            new FunctionEvaluationException(cause, new double[] { 0, 1, 2 }, pattern, arguments);
        assertEquals(cause, ex.getCause());
        assertEquals(pattern, ex.getGeneralPattern());
        assertEquals(arguments.length, ex.getArguments().length);
        for (int i = 0; i < arguments.length; ++i) {
            assertEquals(arguments[i], ex.getArguments()[i]);
        }
        assertFalse(pattern.equals(ex.getMessage()));
        assertFalse(ex.getMessage().equals(ex.getMessage(Locale.FRENCH)));
        assertEquals(0.0, ex.getArgument()[0], 0);
        assertEquals(1.0, ex.getArgument()[1], 0);
        assertEquals(2.0, ex.getArgument()[2], 0);
    }

    public void testConstructorArgumentCause(){
        String inMsg = "inner message";
        Exception cause = new Exception(inMsg);
        FunctionEvaluationException ex = new FunctionEvaluationException(cause, 0.0);
        assertEquals(cause, ex.getCause());
        assertTrue(ex.getMessage().equals(ex.getMessage(Locale.FRENCH)));
    }

    public void testConstructorArrayArgumentCause(){
        String inMsg = "inner message";
        Exception cause = new Exception(inMsg);
        FunctionEvaluationException ex =
            new FunctionEvaluationException(cause, new double[] { 0, 1, 2 });
        assertEquals(cause, ex.getCause());
        assertTrue(ex.getMessage().equals(ex.getMessage(Locale.FRENCH)));
        assertEquals(0.0, ex.getArgument()[0], 0);
        assertEquals(1.0, ex.getArgument()[1], 0);
        assertEquals(2.0, ex.getArgument()[2], 0);
    }

}
