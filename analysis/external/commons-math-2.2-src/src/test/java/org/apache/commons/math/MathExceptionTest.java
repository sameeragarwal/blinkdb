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

import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Locale;

import org.apache.commons.math.exception.util.DummyLocalizable;
import org.apache.commons.math.exception.util.Localizable;
import org.apache.commons.math.exception.util.LocalizedFormats;

/**
 * @version $Revision: 1035475 $ $Date: 2010-11-15 23:39:25 +0100 (lun. 15 nov. 2010) $
 */
public class MathExceptionTest extends TestCase {

    public void testConstructor(){
        MathException ex = new MathException();
        assertNull(ex.getCause());
        assertEquals("", ex.getMessage());
        assertEquals("", ex.getMessage(Locale.FRENCH));
    }

    public void testConstructorPatternArguments(){
        LocalizedFormats pattern = LocalizedFormats.ROTATION_MATRIX_DIMENSIONS;
        Object[] arguments = { Integer.valueOf(6), Integer.valueOf(4) };
        MathException ex = new MathException(pattern, arguments);
        assertNull(ex.getCause());
        assertEquals(pattern, ex.getGeneralPattern());
        assertEquals(arguments.length, ex.getArguments().length);
        for (int i = 0; i < arguments.length; ++i) {
            assertEquals(arguments[i], ex.getArguments()[i]);
        }
        assertFalse(pattern.equals(ex.getMessage()));
        assertFalse(ex.getMessage().equals(ex.getMessage(Locale.FRENCH)));
    }

    public void testConstructorCause(){
        String inMsg = "inner message";
        Exception cause = new Exception(inMsg);
        MathException ex = new MathException(cause);
        assertEquals(cause, ex.getCause());
    }

    public void testConstructorPatternArgumentsCause(){
        LocalizedFormats pattern = LocalizedFormats.ROTATION_MATRIX_DIMENSIONS;
        Object[] arguments = { Integer.valueOf(6), Integer.valueOf(4) };
        String inMsg = "inner message";
        Exception cause = new Exception(inMsg);
        MathException ex = new MathException(cause, pattern, arguments);
        assertEquals(cause, ex.getCause());
        assertEquals(pattern, ex.getGeneralPattern());
        assertEquals(arguments.length, ex.getArguments().length);
        for (int i = 0; i < arguments.length; ++i) {
            assertEquals(arguments[i], ex.getArguments()[i]);
        }
        assertFalse(pattern.equals(ex.getMessage()));
        assertFalse(ex.getMessage().equals(ex.getMessage(Locale.FRENCH)));
    }

    /**
     * Tests the printStackTrace() operation.
     */
    public void testPrintStackTrace() {
        Localizable outMsg = new DummyLocalizable("outer message");
        Localizable inMsg = new DummyLocalizable("inner message");
        MathException cause = new MathConfigurationException(inMsg);
        MathException ex = new MathException(cause, outMsg);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        ex.printStackTrace(ps);
        String stack = baos.toString();
        String outerMsg = "org.apache.commons.math.MathException: outer message";
        String innerMsg = "Caused by: " +
        "org.apache.commons.math.MathConfigurationException: inner message";
        assertTrue(stack.startsWith(outerMsg));
        assertTrue(stack.indexOf(innerMsg) > 0);

        PrintWriter pw = new PrintWriter(ps, true);
        ex.printStackTrace(pw);
        stack = baos.toString();
        assertTrue(stack.startsWith(outerMsg));
        assertTrue(stack.indexOf(innerMsg) > 0);
    }

    /**
     * Test serialization
     */
    public void testSerialization() {
        Localizable outMsg = new DummyLocalizable("outer message");
        Localizable inMsg = new DummyLocalizable("inner message");
        MathException cause = new MathConfigurationException(inMsg);
        MathException ex = new MathException(cause, outMsg);
        MathException image = (MathException) TestUtils.serializeAndRecover(ex);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        ex.printStackTrace(ps);
        String stack = baos.toString();

        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        PrintStream ps2 = new PrintStream(baos2);
        image.printStackTrace(ps2);
        String stack2 = baos2.toString();

        // See if JDK supports nested exceptions.  If not, stack trace of
        // inner exception will not be serialized
        boolean jdkSupportsNesting = false;
        try {
            Throwable.class.getDeclaredMethod("getCause", new Class[0]);
            jdkSupportsNesting = true;
        } catch (NoSuchMethodException e) {
            jdkSupportsNesting = false;
        }

        if (jdkSupportsNesting) {
            assertEquals(stack, stack2);
        } else {
            assertTrue(stack2.indexOf(inMsg.getSourceString()) != -1);
            assertTrue(stack2.indexOf("MathConfigurationException") != -1);
        }
    }
}
