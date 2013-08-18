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

package org.apache.commons.math.complex;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Locale;

import org.apache.commons.math.util.CompositeFormat;
import org.apache.commons.math.util.FastMath;

import junit.framework.TestCase;

public abstract class ComplexFormatAbstractTest extends TestCase {

    CompositeFormat complexFormat = null;
    ComplexFormat complexFormatJ = null;

    protected abstract Locale getLocale();

    protected abstract char getDecimalCharacter();

    @Override
    protected void setUp() throws Exception {
        complexFormat = ComplexFormat.getInstance(getLocale());
        complexFormatJ = ComplexFormat.getInstance(getLocale());
        complexFormatJ.setImaginaryCharacter("j");
    }

    public void testSimpleNoDecimals() {
        Complex c = new Complex(1, 1);
        String expected = "1 + 1i";
        String actual = complexFormat.format(c);
        assertEquals(expected, actual);
    }

    public void testSimpleWithDecimals() {
        Complex c = new Complex(1.23, 1.43);
        String expected = "1" + getDecimalCharacter() + "23 + 1" + getDecimalCharacter() + "43i";
        String actual = complexFormat.format(c);
        assertEquals(expected, actual);
    }

    public void testSimpleWithDecimalsTrunc() {
        Complex c = new Complex(1.2323, 1.4343);
        String expected = "1" + getDecimalCharacter() + "23 + 1" + getDecimalCharacter() + "43i";
        String actual = complexFormat.format(c);
        assertEquals(expected, actual);
    }

    public void testNegativeReal() {
        Complex c = new Complex(-1.2323, 1.4343);
        String expected = "-1" + getDecimalCharacter() + "23 + 1" + getDecimalCharacter() + "43i";
        String actual = complexFormat.format(c);
        assertEquals(expected, actual);
    }

    public void testNegativeImaginary() {
        Complex c = new Complex(1.2323, -1.4343);
        String expected = "1" + getDecimalCharacter() + "23 - 1" + getDecimalCharacter() + "43i";
        String actual = complexFormat.format(c);
        assertEquals(expected, actual);
    }

    public void testNegativeBoth() {
        Complex c = new Complex(-1.2323, -1.4343);
        String expected = "-1" + getDecimalCharacter() + "23 - 1" + getDecimalCharacter() + "43i";
        String actual = complexFormat.format(c);
        assertEquals(expected, actual);
    }

    public void testZeroReal() {
        Complex c = new Complex(0.0, -1.4343);
        String expected = "0 - 1" + getDecimalCharacter() + "43i";
        String actual = complexFormat.format(c);
        assertEquals(expected, actual);
    }

    public void testZeroImaginary() {
        Complex c = new Complex(30.233, 0);
        String expected = "30" + getDecimalCharacter() + "23";
        String actual = complexFormat.format(c);
        assertEquals(expected, actual);
    }

    public void testDifferentImaginaryChar() {
        Complex c = new Complex(1, 1);
        String expected = "1 + 1j";
        String actual = complexFormatJ.format(c);
        assertEquals(expected, actual);
    }

    public void testStaticFormatComplex() {
        Locale defaultLocal = Locale.getDefault();
        Locale.setDefault(getLocale());

        Complex c = new Complex(232.222, -342.33);
        String expected = "232" + getDecimalCharacter() + "22 - 342" + getDecimalCharacter() + "33i";
        String actual = ComplexFormat.formatComplex(c);
        assertEquals(expected, actual);

        Locale.setDefault(defaultLocal);
    }

    public void testNan() {
        Complex c = new Complex(Double.NaN, Double.NaN);
        String expected = "(NaN) + (NaN)i";
        String actual = complexFormat.format(c);
        assertEquals(expected, actual);
    }

    public void testPositiveInfinity() {
        Complex c = new Complex(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        String expected = "(Infinity) + (Infinity)i";
        String actual = complexFormat.format(c);
        assertEquals(expected, actual);
    }

    public void testNegativeInfinity() {
        Complex c = new Complex(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
        String expected = "(-Infinity) - (Infinity)i";
        String actual = complexFormat.format(c);
        assertEquals(expected, actual);
    }

    public void testParseSimpleNoDecimals() {
        String source = "1 + 1i";
        Complex expected = new Complex(1, 1);
        try {
            Complex actual = (Complex)complexFormat.parseObject(source);
            assertEquals(expected, actual);
        } catch (ParseException ex) {
            fail(ex.getMessage());
        }
    }

    public void testParseSimpleWithDecimals() {
        String source = "1" + getDecimalCharacter() + "23 + 1" + getDecimalCharacter() + "43i";
        Complex expected = new Complex(1.23, 1.43);
        try {
            Complex actual = (Complex)complexFormat.parseObject(source);
            assertEquals(expected, actual);
        } catch (ParseException ex) {
            fail(ex.getMessage());
        }
    }

    public void testParseSimpleWithDecimalsTrunc() {
        String source = "1" + getDecimalCharacter() + "2323 + 1" + getDecimalCharacter() + "4343i";
        Complex expected = new Complex(1.2323, 1.4343);
        try {
            Complex actual = (Complex)complexFormat.parseObject(source);
            assertEquals(expected, actual);
        } catch (ParseException ex) {
            fail(ex.getMessage());
        }
    }

    public void testParseNegativeReal() {
        String source = "-1" + getDecimalCharacter() + "2323 + 1" + getDecimalCharacter() + "4343i";
        Complex expected = new Complex(-1.2323, 1.4343);
        try {
            Complex actual = (Complex)complexFormat.parseObject(source);
            assertEquals(expected, actual);
        } catch (ParseException ex) {
            fail(ex.getMessage());
        }
    }

    public void testParseNegativeImaginary() {
        String source = "1" + getDecimalCharacter() + "2323 - 1" + getDecimalCharacter() + "4343i";
        Complex expected = new Complex(1.2323, -1.4343);
        try {
            Complex actual = (Complex)complexFormat.parseObject(source);
            assertEquals(expected, actual);
        } catch (ParseException ex) {
            fail(ex.getMessage());
        }
    }

    public void testParseNegativeBoth() {
        String source = "-1" + getDecimalCharacter() + "2323 - 1" + getDecimalCharacter() + "4343i";
        Complex expected = new Complex(-1.2323, -1.4343);
        try {
            Complex actual = (Complex)complexFormat.parseObject(source);
            assertEquals(expected, actual);
        } catch (ParseException ex) {
            fail(ex.getMessage());
        }
    }

    public void testParseZeroReal() {
        String source = "0" + getDecimalCharacter() + "0 - 1" + getDecimalCharacter() + "4343i";
        Complex expected = new Complex(0.0, -1.4343);
        try {
            Complex actual = (Complex)complexFormat.parseObject(source);
            assertEquals(expected, actual);
        } catch (ParseException ex) {
            fail(ex.getMessage());
        }
    }

    public void testParseZeroImaginary() {
        String source = "-1" + getDecimalCharacter() + "2323";
        Complex expected = new Complex(-1.2323, 0);
        try {
            Complex actual = (Complex)complexFormat.parseObject(source);
            assertEquals(expected, actual);
        } catch (ParseException ex) {
            fail(ex.getMessage());
        }
    }

    public void testParseDifferentImaginaryChar() {
        String source = "-1" + getDecimalCharacter() + "2323 - 1" + getDecimalCharacter() + "4343j";
        Complex expected = new Complex(-1.2323, -1.4343);
        try {
            Complex actual = (Complex)complexFormatJ.parseObject(source);
            assertEquals(expected, actual);
        } catch (ParseException ex) {
            fail(ex.getMessage());
        }
    }

    public void testParseNan() {
        String source = "(NaN) + (NaN)i";
        Complex expected = new Complex(Double.NaN, Double.NaN);
        try {
            Complex actual = (Complex)complexFormat.parseObject(source);
            assertEquals(expected, actual);
        } catch (ParseException ex) {
            fail(ex.getMessage());
        }
    }

    public void testParsePositiveInfinity() {
        String source = "(Infinity) + (Infinity)i";
        Complex expected = new Complex(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        try {
            Complex actual = (Complex)complexFormat.parseObject(source);
            assertEquals(expected, actual);
        } catch (ParseException ex) {
            fail(ex.getMessage());
        }
    }

    public void testPaseNegativeInfinity() {
        String source = "(-Infinity) - (Infinity)i";
        Complex expected = new Complex(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
        try {
            Complex actual = (Complex)complexFormat.parseObject(source);
            assertEquals(expected, actual);
        } catch (ParseException ex) {
            fail(ex.getMessage());
        }
    }

    public void testConstructorSingleFormat() {
        NumberFormat nf = NumberFormat.getInstance();
        ComplexFormat cf = new ComplexFormat(nf);
        assertNotNull(cf);
        assertEquals(nf, cf.getRealFormat());
    }

    public void testGetImaginaryFormat() {
        NumberFormat nf = NumberFormat.getInstance();
        ComplexFormat cf = new ComplexFormat();

        assertNotSame(nf, cf.getImaginaryFormat());
        cf.setImaginaryFormat(nf);
        assertSame(nf, cf.getImaginaryFormat());
    }

    public void testSetImaginaryFormatNull() {
        try {
            ComplexFormat cf = new ComplexFormat();
            cf.setImaginaryFormat(null);
            fail();
        } catch (IllegalArgumentException ex) {
            // success
        }
    }

    public void testSetRealFormatNull() {
        try {
            ComplexFormat cf = new ComplexFormat();
            cf.setRealFormat(null);
            fail();
        } catch (IllegalArgumentException ex) {
            // success
        }
    }

    public void testGetRealFormat() {
        NumberFormat nf = NumberFormat.getInstance();
        ComplexFormat cf = new ComplexFormat();

        assertNotSame(nf, cf.getRealFormat());
        cf.setRealFormat(nf);
        assertSame(nf, cf.getRealFormat());
    }

    public void testSetImaginaryCharacterNull() {
        try {
            ComplexFormat cf = new ComplexFormat();
            cf.setImaginaryCharacter(null);
            fail();
        } catch (IllegalArgumentException ex) {
            // success
        }
    }

    public void testSetImaginaryCharacterEmpty() {
        try {
            ComplexFormat cf = new ComplexFormat();
            cf.setImaginaryCharacter("");
            fail();
        } catch (IllegalArgumentException ex) {
            // success
        }
    }

    public void testFormatNumber() {
        CompositeFormat cf = ComplexFormat.getInstance(getLocale());
        Double pi = Double.valueOf(FastMath.PI);
        String text = cf.format(pi);
        assertEquals("3" + getDecimalCharacter() + "14", text);
    }

    public void testFormatObject() {
        try {
            CompositeFormat cf = new ComplexFormat();
            Object object = new Object();
            cf.format(object);
            fail();
        } catch (IllegalArgumentException ex) {
            // success
        }
    }

    public void testForgottenImaginaryCharacter() {
        ParsePosition pos = new ParsePosition(0);
        assertNull(new ComplexFormat().parse("1 + 1", pos));
        assertEquals(5, pos.getErrorIndex());
    }
}
