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

import java.util.Locale;

import org.apache.commons.math.exception.util.LocalizedFormats;

/**
 * @version $Revision: 1035475 $ $Date: 2010-11-15 23:39:25 +0100 (lun. 15 nov. 2010) $
 */
public class MathConfigurationExceptionTest extends TestCase {

    public void testConstructor(){
        MathConfigurationException ex = new MathConfigurationException();
        assertNull(ex.getCause());
        assertEquals("", ex.getMessage());
        assertEquals("", ex.getMessage(Locale.FRENCH));
    }

    public void testConstructorPatternArguments(){
        LocalizedFormats pattern = LocalizedFormats.ROTATION_MATRIX_DIMENSIONS;
        Object[] arguments = { Integer.valueOf(6), Integer.valueOf(4) };
        MathConfigurationException ex = new MathConfigurationException(pattern, arguments);
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
        MathConfigurationException ex = new MathConfigurationException(cause);
        assertEquals(cause, ex.getCause());
    }

    public void testConstructorPatternArgumentsCause(){
        LocalizedFormats pattern = LocalizedFormats.ROTATION_MATRIX_DIMENSIONS;
        Object[] arguments = { Integer.valueOf(6), Integer.valueOf(4) };
        String inMsg = "inner message";
        Exception cause = new Exception(inMsg);
        MathConfigurationException ex = new MathConfigurationException(cause, pattern, arguments);
        assertEquals(cause, ex.getCause());
        assertEquals(pattern, ex.getGeneralPattern());
        assertEquals(arguments.length, ex.getArguments().length);
        for (int i = 0; i < arguments.length; ++i) {
            assertEquals(arguments[i], ex.getArguments()[i]);
        }
        assertFalse(pattern.equals(ex.getMessage()));
        assertFalse(ex.getMessage().equals(ex.getMessage(Locale.FRENCH)));
    }

}
