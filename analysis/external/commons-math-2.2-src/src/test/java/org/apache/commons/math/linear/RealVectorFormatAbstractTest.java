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

package org.apache.commons.math.linear;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Locale;

import junit.framework.TestCase;

import org.apache.commons.math.util.CompositeFormat;

public abstract class RealVectorFormatAbstractTest extends TestCase {

    RealVectorFormat realVectorFormat = null;
    RealVectorFormat realVectorFormatSquare = null;

    protected abstract Locale getLocale();

    protected abstract char getDecimalCharacter();

    @Override
    public void setUp() throws Exception {
        realVectorFormat = RealVectorFormat.getInstance(getLocale());
        final NumberFormat nf = NumberFormat.getInstance(getLocale());
        nf.setMaximumFractionDigits(2);
        realVectorFormatSquare = new RealVectorFormat("[", "]", " : ", nf);
    }

    public void testSimpleNoDecimals() {
        ArrayRealVector c = new ArrayRealVector(new double[] {1, 1, 1});
        String expected = "{1; 1; 1}";
        String actual = realVectorFormat.format(c);
        assertEquals(expected, actual);
    }

    public void testSimpleWithDecimals() {
        ArrayRealVector c = new ArrayRealVector(new double[] {1.23, 1.43, 1.63});
        String expected =
            "{1"    + getDecimalCharacter() +
            "23; 1" + getDecimalCharacter() +
            "43; 1" + getDecimalCharacter() +
            "63}";
        String actual = realVectorFormat.format(c);
        assertEquals(expected, actual);
    }

    public void testSimpleWithDecimalsTrunc() {
        ArrayRealVector c = new ArrayRealVector(new double[] {1.2323, 1.4343, 1.6333});
        String expected =
            "{1"    + getDecimalCharacter() +
            "23; 1" + getDecimalCharacter() +
            "43; 1" + getDecimalCharacter() +
            "63}";
        String actual = realVectorFormat.format(c);
        assertEquals(expected, actual);
    }

    public void testNegativeX() {
        ArrayRealVector c = new ArrayRealVector(new double[] {-1.2323, 1.4343, 1.6333});
        String expected =
            "{-1"    + getDecimalCharacter() +
            "23; 1" + getDecimalCharacter() +
            "43; 1" + getDecimalCharacter() +
            "63}";
        String actual = realVectorFormat.format(c);
        assertEquals(expected, actual);
    }

    public void testNegativeY() {
        ArrayRealVector c = new ArrayRealVector(new double[] {1.2323, -1.4343, 1.6333});
        String expected =
            "{1"    + getDecimalCharacter() +
            "23; -1" + getDecimalCharacter() +
            "43; 1" + getDecimalCharacter() +
            "63}";
        String actual = realVectorFormat.format(c);
        assertEquals(expected, actual);
    }

    public void testNegativeZ() {
        ArrayRealVector c = new ArrayRealVector(new double[] {1.2323, 1.4343, -1.6333});
        String expected =
            "{1"    + getDecimalCharacter() +
            "23; 1" + getDecimalCharacter() +
            "43; -1" + getDecimalCharacter() +
            "63}";
        String actual = realVectorFormat.format(c);
        assertEquals(expected, actual);
    }

    public void testNonDefaultSetting() {
        ArrayRealVector c = new ArrayRealVector(new double[] {1, 1, 1});
        String expected = "[1 : 1 : 1]";
        String actual = realVectorFormatSquare.format(c);
        assertEquals(expected, actual);
    }

    public void testStaticFormatRealVectorImpl() {
        Locale defaultLocal = Locale.getDefault();
        Locale.setDefault(getLocale());

        ArrayRealVector c = new ArrayRealVector(new double[] {232.222, -342.33, 432.444});
        String expected =
            "{232"    + getDecimalCharacter() +
            "22; -342" + getDecimalCharacter() +
            "33; 432" + getDecimalCharacter() +
            "44}";
        String actual = RealVectorFormat.formatRealVector(c);
        assertEquals(expected, actual);

        Locale.setDefault(defaultLocal);
    }

    public void testNan() {
        ArrayRealVector c = new ArrayRealVector(new double[] {Double.NaN, Double.NaN, Double.NaN});
        String expected = "{(NaN); (NaN); (NaN)}";
        String actual = realVectorFormat.format(c);
        assertEquals(expected, actual);
    }

    public void testPositiveInfinity() {
        ArrayRealVector c = new ArrayRealVector(new double[] {
                Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY
        });
        String expected = "{(Infinity); (Infinity); (Infinity)}";
        String actual = realVectorFormat.format(c);
        assertEquals(expected, actual);
    }

    public void tesNegativeInfinity() {
        ArrayRealVector c = new ArrayRealVector(new double[] {
                Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY
        });
        String expected = "{(-Infinity); (-Infinity); (-Infinity)}";
        String actual = realVectorFormat.format(c);
        assertEquals(expected, actual);
    }

    public void testParseSimpleNoDecimals() {
        String source = "{1; 1; 1}";
        ArrayRealVector expected = new ArrayRealVector(new double[] {1, 1, 1});
        try {
            ArrayRealVector actual = (ArrayRealVector) realVectorFormat.parseObject(source);
            assertEquals(expected, actual);
        } catch (ParseException ex) {
            fail(ex.getMessage());
        }
    }

    public void testParseIgnoredWhitespace() {
        ArrayRealVector expected = new ArrayRealVector(new double[] {1, 1, 1});
        ParsePosition pos1 = new ParsePosition(0);
        String source1 = "{1;1;1}";
        assertEquals(expected, realVectorFormat.parseObject(source1, pos1));
        assertEquals(source1.length(), pos1.getIndex());
        ParsePosition pos2 = new ParsePosition(0);
        String source2 = " { 1 ; 1 ; 1 } ";
        assertEquals(expected, realVectorFormat.parseObject(source2, pos2));
        assertEquals(source2.length() - 1, pos2.getIndex());
    }

    public void testParseSimpleWithDecimals() {
        String source =
            "{1" + getDecimalCharacter() +
            "23; 1" + getDecimalCharacter() +
            "43; 1" + getDecimalCharacter() +
            "63}";
        ArrayRealVector expected = new ArrayRealVector(new double[] {1.23, 1.43, 1.63});
        try {
            ArrayRealVector actual = (ArrayRealVector) realVectorFormat.parseObject(source);
            assertEquals(expected, actual);
        } catch (ParseException ex) {
            fail(ex.getMessage());
        }
    }

    public void testParseSimpleWithDecimalsTrunc() {
        String source =
            "{1" + getDecimalCharacter() +
            "2323; 1" + getDecimalCharacter() +
            "4343; 1" + getDecimalCharacter() +
            "6333}";
        ArrayRealVector expected = new ArrayRealVector(new double[] {1.2323, 1.4343, 1.6333});
        try {
            ArrayRealVector actual = (ArrayRealVector) realVectorFormat.parseObject(source);
            assertEquals(expected, actual);
        } catch (ParseException ex) {
            fail(ex.getMessage());
        }
    }

    public void testParseNegativeX() {
        String source =
            "{-1" + getDecimalCharacter() +
            "2323; 1" + getDecimalCharacter() +
            "4343; 1" + getDecimalCharacter() +
            "6333}";
        ArrayRealVector expected = new ArrayRealVector(new double[] {-1.2323, 1.4343, 1.6333});
        try {
            ArrayRealVector actual = (ArrayRealVector) realVectorFormat.parseObject(source);
            assertEquals(expected, actual);
        } catch (ParseException ex) {
            fail(ex.getMessage());
        }
    }

    public void testParseNegativeY() {
        String source =
            "{1" + getDecimalCharacter() +
            "2323; -1" + getDecimalCharacter() +
            "4343; 1" + getDecimalCharacter() +
            "6333}";
        ArrayRealVector expected = new ArrayRealVector(new double[] {1.2323, -1.4343, 1.6333});
        try {
            ArrayRealVector actual = (ArrayRealVector) realVectorFormat.parseObject(source);
            assertEquals(expected, actual);
        } catch (ParseException ex) {
            fail(ex.getMessage());
        }
    }

    public void testParseNegativeZ() {
        String source =
            "{1" + getDecimalCharacter() +
            "2323; 1" + getDecimalCharacter() +
            "4343; -1" + getDecimalCharacter() +
            "6333}";
        ArrayRealVector expected = new ArrayRealVector(new double[] {1.2323, 1.4343, -1.6333});
        try {
            ArrayRealVector actual = (ArrayRealVector) realVectorFormat.parseObject(source);
            assertEquals(expected, actual);
        } catch (ParseException ex) {
            fail(ex.getMessage());
        }
    }

    public void testParseNegativeAll() {
        String source =
            "{-1" + getDecimalCharacter() +
            "2323; -1" + getDecimalCharacter() +
            "4343; -1" + getDecimalCharacter() +
            "6333}";
        ArrayRealVector expected = new ArrayRealVector(new double[] {-1.2323, -1.4343, -1.6333});
        try {
            ArrayRealVector actual = (ArrayRealVector) realVectorFormat.parseObject(source);
            assertEquals(expected, actual);
        } catch (ParseException ex) {
            fail(ex.getMessage());
        }
    }

    public void testParseZeroX() {
        String source =
            "{0" + getDecimalCharacter() +
            "0; -1" + getDecimalCharacter() +
            "4343; 1" + getDecimalCharacter() +
            "6333}";
        ArrayRealVector expected = new ArrayRealVector(new double[] {0.0, -1.4343, 1.6333});
        try {
            ArrayRealVector actual = (ArrayRealVector) realVectorFormat.parseObject(source);
            assertEquals(expected, actual);
        } catch (ParseException ex) {
            fail(ex.getMessage());
        }
    }

    public void testParseNonDefaultSetting() {
        String source =
            "[1" + getDecimalCharacter() +
            "2323 : 1" + getDecimalCharacter() +
            "4343 : 1" + getDecimalCharacter() +
            "6333]";
        ArrayRealVector expected = new ArrayRealVector(new double[] {1.2323, 1.4343, 1.6333});
        try {
            ArrayRealVector actual = (ArrayRealVector) realVectorFormatSquare.parseObject(source);
            assertEquals(expected, actual);
        } catch (ParseException ex) {
            fail(ex.getMessage());
        }
    }

    public void testParseNan() {
        String source = "{(NaN); (NaN); (NaN)}";
        try {
            ArrayRealVector actual = (ArrayRealVector) realVectorFormat.parseObject(source);
            assertEquals(new ArrayRealVector(new double[] {Double.NaN, Double.NaN, Double.NaN}), actual);
        } catch (ParseException ex) {
            fail(ex.getMessage());
        }
    }

    public void testParsePositiveInfinity() {
        String source = "{(Infinity); (Infinity); (Infinity)}";
        try {
            ArrayRealVector actual = (ArrayRealVector)realVectorFormat.parseObject(source);
            assertEquals(new ArrayRealVector(new double[] {
                    Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY
            }), actual);
        } catch (ParseException ex) {
            fail(ex.getMessage());
        }
    }

    public void testParseNegativeInfinity() {
        String source = "{(-Infinity); (-Infinity); (-Infinity)}";
        try {
            ArrayRealVector actual = (ArrayRealVector)realVectorFormat.parseObject(source);
            assertEquals(new ArrayRealVector(new double[] {
                    Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY
            }), actual);
        } catch (ParseException ex) {
            fail(ex.getMessage());
        }
    }

    public void testParseNoComponents() {
        try {
            realVectorFormat.parseObject("{ }");
            fail("Expecting ParseException");
        } catch (ParseException pe) {
            // expected behavior
        }
    }

    public void testParseManyComponents() throws ParseException {
        ArrayRealVector parsed =
            (ArrayRealVector) realVectorFormat.parseObject("{0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0}");
        assertEquals(24, parsed.getDimension());
    }

    public void testConstructorSingleFormat() {
        NumberFormat nf = NumberFormat.getInstance();
        RealVectorFormat cf = new RealVectorFormat(nf);
        assertNotNull(cf);
        assertEquals(nf, cf.getFormat());
    }

    public void testFormatObject() {
        try {
            CompositeFormat cf = new RealVectorFormat();
            Object object = new Object();
            cf.format(object);
            fail();
        } catch (IllegalArgumentException ex) {
            // success
        }
    }

    public void testForgottenPrefix() {
        ParsePosition pos = new ParsePosition(0);
        final String source = "1; 1; 1}";
        assertNull("Should not parse <"+source+">",new RealVectorFormat().parse(source, pos));
        assertEquals(0, pos.getErrorIndex());
    }

    public void testForgottenSeparator() {
        ParsePosition pos = new ParsePosition(0);
        final String source = "{1; 1 1}";
        assertNull("Should not parse <"+source+">",new RealVectorFormat().parse(source, pos));
        assertEquals(6, pos.getErrorIndex());
    }

    public void testForgottenSuffix() {
        ParsePosition pos = new ParsePosition(0);
        final String source = "{1; 1; 1 ";
        assertNull("Should not parse <"+source+">",new RealVectorFormat().parse(source, pos));
        assertEquals(8, pos.getErrorIndex());
    }

}
