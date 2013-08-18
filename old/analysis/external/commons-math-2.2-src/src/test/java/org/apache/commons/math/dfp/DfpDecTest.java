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

package org.apache.commons.math.dfp;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DfpDecTest {

    private DfpField field;
    private Dfp pinf;
    private Dfp ninf;

    @Before
    public void setUp() {
        // Some basic setup.  Define some constants and clear the status flags
        field = new DfpField(20);
        pinf = new DfpDec(field, 1).divide(new DfpDec(field, 0));
        ninf = new DfpDec(field, -1).divide(new DfpDec(field, 0));
        ninf.getField().clearIEEEFlags();
    }

    @After
    public void tearDown() {
        field = null;
        pinf    = null;
        ninf    = null;
    }

    // Generic test function.  Takes params x and y and tests them for 
    // equality.  Then checks the status flags against the flags argument.
    // If the test fail, it prints the desc string
    private void test(Dfp x, Dfp y, int flags, String desc) {
        boolean b = x.equals(y);

        if (!x.equals(y) && !x.unequal(y))  // NaNs involved 
            b = (x.toString().equals(y.toString()));

        if (x.equals(new DfpDec(field, 0)))  // distinguish +/- zero
            b = (b && (x.toString().equals(y.toString())));

        b = (b && x.getField().getIEEEFlags() == flags);

        if (!b)
            Assert.assertTrue("assersion failed "+desc+" x = "+x.toString()+" flags = "+x.getField().getIEEEFlags(), b);

        x.getField().clearIEEEFlags();
    }

    @Test
    public void testRound()
    {
        field.setRoundingMode(DfpField.RoundingMode.ROUND_HALF_EVEN);

        test(new DfpDec(field, "12345678901234567890"),
             new DfpDec(field, "12345678901234568000"),
             DfpField.FLAG_INEXACT, "Round #1");

        test(new DfpDec(field, "0.12345678901234567890"),
             new DfpDec(field, "0.12345678901234568"),
             DfpField.FLAG_INEXACT, "Round #2");

        test(new DfpDec(field, "0.12345678901234567500"),
             new DfpDec(field, "0.12345678901234568"),
             DfpField.FLAG_INEXACT, "Round #3");

        test(new DfpDec(field, "0.12345678901234568500"),
             new DfpDec(field, "0.12345678901234568"),
             DfpField.FLAG_INEXACT, "Round #4");

        test(new DfpDec(field, "0.12345678901234568501"),
             new DfpDec(field, "0.12345678901234569"),
             DfpField.FLAG_INEXACT, "Round #5");

        test(new DfpDec(field, "0.12345678901234568499"),
             new DfpDec(field, "0.12345678901234568"),
             DfpField.FLAG_INEXACT, "Round #6");

        test(new DfpDec(field, "1.2345678901234567890"),
             new DfpDec(field, "1.2345678901234568"),
             DfpField.FLAG_INEXACT, "Round #7");

        test(new DfpDec(field, "1.2345678901234567500"),
             new DfpDec(field, "1.2345678901234568"),
             DfpField.FLAG_INEXACT, "Round #8");

        test(new DfpDec(field, "1.2345678901234568500"),
             new DfpDec(field, "1.2345678901234568"),
             DfpField.FLAG_INEXACT, "Round #9");

        test(new DfpDec(field, "1.2345678901234568000").add(new DfpDec(field, ".0000000000000000501")),
             new DfpDec(field, "1.2345678901234569"),
             DfpField.FLAG_INEXACT, "Round #10");

        test(new DfpDec(field, "1.2345678901234568499"),
             new DfpDec(field, "1.2345678901234568"),
             DfpField.FLAG_INEXACT, "Round #11");

        test(new DfpDec(field, "12.345678901234567890"),
             new DfpDec(field, "12.345678901234568"),
             DfpField.FLAG_INEXACT, "Round #12");

        test(new DfpDec(field, "12.345678901234567500"),
             new DfpDec(field, "12.345678901234568"),
             DfpField.FLAG_INEXACT, "Round #13");

        test(new DfpDec(field, "12.345678901234568500"),
             new DfpDec(field, "12.345678901234568"),
             DfpField.FLAG_INEXACT, "Round #14");

        test(new DfpDec(field, "12.345678901234568").add(new DfpDec(field, ".000000000000000501")),
             new DfpDec(field, "12.345678901234569"),
             DfpField.FLAG_INEXACT, "Round #15");

        test(new DfpDec(field, "12.345678901234568499"),
             new DfpDec(field, "12.345678901234568"),
             DfpField.FLAG_INEXACT, "Round #16");

        test(new DfpDec(field, "123.45678901234567890"),
             new DfpDec(field, "123.45678901234568"),
             DfpField.FLAG_INEXACT, "Round #17");

        test(new DfpDec(field, "123.45678901234567500"),
             new DfpDec(field, "123.45678901234568"),
             DfpField.FLAG_INEXACT, "Round #18");

        test(new DfpDec(field, "123.45678901234568500"),
             new DfpDec(field, "123.45678901234568"),
             DfpField.FLAG_INEXACT, "Round #19");

        test(new DfpDec(field, "123.456789012345685").add(new DfpDec(field, ".00000000000000501")),
             new DfpDec(field, "123.45678901234569"),
             DfpField.FLAG_INEXACT, "Round #20");

        test(new DfpDec(field, "123.45678901234568499"),
             new DfpDec(field, "123.45678901234568"),
             DfpField.FLAG_INEXACT, "Round #21");

        field.setRoundingMode(DfpField.RoundingMode.ROUND_DOWN);

        // Round down
        test(new DfpDec(field, "12345678901234567").add(new DfpDec(field, "0.9")),
             new DfpDec(field, "12345678901234567"),
             DfpField.FLAG_INEXACT, "Round #22");

        test(new DfpDec(field, "12345678901234567").add(new DfpDec(field, "0.99999999")),
             new DfpDec(field, "12345678901234567"),
             DfpField.FLAG_INEXACT, "Round #23");

        test(new DfpDec(field, "-12345678901234567").add(new DfpDec(field, "-0.99999999")),
             new DfpDec(field, "-12345678901234567"),
             DfpField.FLAG_INEXACT, "Round #24");

        field.setRoundingMode(DfpField.RoundingMode.ROUND_UP);

        // Round up
        test(new DfpDec(field, "12345678901234567").add(new DfpDec(field, "0.1")),
             new DfpDec(field, "12345678901234568"),
             DfpField.FLAG_INEXACT, "Round #25");

        test(new DfpDec(field, "12345678901234567").add(new DfpDec(field, "0.0001")),
             new DfpDec(field, "12345678901234568"),
             DfpField.FLAG_INEXACT, "Round #26");

        test(new DfpDec(field, "-12345678901234567").add(new DfpDec(field, "-0.1")),
             new DfpDec(field, "-12345678901234568"),
             DfpField.FLAG_INEXACT, "Round #27");

        test(new DfpDec(field, "-12345678901234567").add(new DfpDec(field, "-0.0001")),
             new DfpDec(field, "-12345678901234568"),
             DfpField.FLAG_INEXACT, "Round #28");

        test(new DfpDec(field, "-12345678901234567").add(new DfpDec(field, "0")),
             new DfpDec(field, "-12345678901234567"),
             0, "Round #28.5");

        field.setRoundingMode(DfpField.RoundingMode.ROUND_HALF_UP);

        // Round half up
        test(new DfpDec(field, "12345678901234567").add(new DfpDec(field, "0.499999999999")),
             new DfpDec(field, "12345678901234567"),
             DfpField.FLAG_INEXACT, "Round #29");

        test(new DfpDec(field, "12345678901234567").add(new DfpDec(field, "0.50000001")),
             new DfpDec(field, "12345678901234568"),
             DfpField.FLAG_INEXACT, "Round #30");

        test(new DfpDec(field, "12345678901234567").add(new DfpDec(field, "0.5")),
             new DfpDec(field, "12345678901234568"),
             DfpField.FLAG_INEXACT, "Round #30.5");

        test(new DfpDec(field, "-12345678901234567").add(new DfpDec(field, "-0.499999999999")),
             new DfpDec(field, "-12345678901234567"),
             DfpField.FLAG_INEXACT, "Round #31");

        test(new DfpDec(field, "-12345678901234567").add(new DfpDec(field, "-0.50000001")),
             new DfpDec(field, "-12345678901234568"),
             DfpField.FLAG_INEXACT, "Round #32");

        field.setRoundingMode(DfpField.RoundingMode.ROUND_HALF_DOWN);

        // Round half down
        test(new DfpDec(field, "12345678901234567").add(new DfpDec(field, "0.5001")),
             new DfpDec(field, "12345678901234568"),
             DfpField.FLAG_INEXACT, "Round #33");

        test(new DfpDec(field, "12345678901234567").add(new DfpDec(field, "0.5000")),
             new DfpDec(field, "12345678901234567"),
             DfpField.FLAG_INEXACT, "Round #34");

        test(new DfpDec(field, "-12345678901234567").add(new DfpDec(field, "-0.5001")),
             new DfpDec(field, "-12345678901234568"),
             DfpField.FLAG_INEXACT, "Round #35");

        test(new DfpDec(field, "-12345678901234567").add(new DfpDec(field, "-0.6")),
             new DfpDec(field, "-12345678901234568"),
             DfpField.FLAG_INEXACT, "Round #35.5");

        test(new DfpDec(field, "-12345678901234567").add(new DfpDec(field, "-0.5000")),
             new DfpDec(field, "-12345678901234567"),
             DfpField.FLAG_INEXACT, "Round #36");

        field.setRoundingMode(DfpField.RoundingMode.ROUND_HALF_ODD);

        // Round half odd
        test(new DfpDec(field, "12345678901234568").add(new DfpDec(field, "0.5000")),
             new DfpDec(field, "12345678901234569"),
             DfpField.FLAG_INEXACT, "Round #37");

        test(new DfpDec(field, "12345678901234567").add(new DfpDec(field, "0.5000")),
             new DfpDec(field, "12345678901234567"),
             DfpField.FLAG_INEXACT, "Round #38");

        test(new DfpDec(field, "-12345678901234568").add(new DfpDec(field, "-0.5000")),
             new DfpDec(field, "-12345678901234569"),
             DfpField.FLAG_INEXACT, "Round #39");

        test(new DfpDec(field, "-12345678901234567").add(new DfpDec(field, "-0.5000")),
             new DfpDec(field, "-12345678901234567"),
             DfpField.FLAG_INEXACT, "Round #40");

        field.setRoundingMode(DfpField.RoundingMode.ROUND_CEIL);

        // Round ceil
        test(new DfpDec(field, "12345678901234567").add(new DfpDec(field, "0.0001")),
             new DfpDec(field, "12345678901234568"),
             DfpField.FLAG_INEXACT, "Round #41");

        test(new DfpDec(field, "-12345678901234567").add(new DfpDec(field, "-0.9999")),
             new DfpDec(field, "-12345678901234567"),
             DfpField.FLAG_INEXACT, "Round #42");

        field.setRoundingMode(DfpField.RoundingMode.ROUND_FLOOR);

        // Round floor
        test(new DfpDec(field, "12345678901234567").add(new DfpDec(field, "0.9999")),
             new DfpDec(field, "12345678901234567"),
             DfpField.FLAG_INEXACT, "Round #43");

        test(new DfpDec(field, "-12345678901234567").add(new DfpDec(field, "-0.0001")),
             new DfpDec(field, "-12345678901234568"),
             DfpField.FLAG_INEXACT, "Round #44");

        field.setRoundingMode(DfpField.RoundingMode.ROUND_HALF_EVEN);  // reset
    }

    @Test
    public void testRoundDecimal10()
    {
        field.setRoundingMode(DfpField.RoundingMode.ROUND_HALF_EVEN);

        test(new Decimal10(field, "1234567891234567890"),
             new Decimal10(field, "1234567891000000000"),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #1");

        test(new Decimal10(field, "0.1234567891634567890"),
             new Decimal10(field, "0.1234567892"),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #2");

        test(new Decimal10(field, "0.1234567891500000000"),
             new Decimal10(field, "0.1234567892"),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #3");

        test(new Decimal10(field, "0.1234567890500"),
             new Decimal10(field, "0.1234567890"),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #4");

        test(new Decimal10(field, "0.1234567890501"),
             new Decimal10(field, "0.1234567891"),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #5");

        test(new Decimal10(field, "0.1234567890499"),
             new Decimal10(field, "0.1234567890"),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #6");

        test(new Decimal10(field, "1.234567890890"),
             new Decimal10(field, "1.234567891"),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #7");

        test(new Decimal10(field, "1.234567891500"),
             new Decimal10(field, "1.234567892"),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #8");

        test(new Decimal10(field, "1.234567890500"),
             new Decimal10(field, "1.234567890"),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #9");

        test(new Decimal10(field, "1.234567890000").add(new Decimal10(field, ".000000000501")),
             new Decimal10(field, "1.234567891"),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #10");

        test(new Decimal10(field, "1.234567890499"),
             new Decimal10(field, "1.234567890"),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #11");

        test(new Decimal10(field, "12.34567890890"),
             new Decimal10(field, "12.34567891"),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #12");

        test(new Decimal10(field, "12.34567891500"),
             new Decimal10(field, "12.34567892"),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #13");

        test(new Decimal10(field, "12.34567890500"),
             new Decimal10(field, "12.34567890"),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #14");

        test(new Decimal10(field, "12.34567890").add(new Decimal10(field, ".00000000501")),
             new Decimal10(field, "12.34567891"),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #15");

        test(new Decimal10(field, "12.34567890499"),
             new Decimal10(field, "12.34567890"),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #16");

        test(new Decimal10(field, "123.4567890890"),
             new Decimal10(field, "123.4567891"),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #17");

        test(new Decimal10(field, "123.4567891500"),
             new Decimal10(field, "123.4567892"),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #18");

        test(new Decimal10(field, "123.4567890500"),
             new Decimal10(field, "123.4567890"),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #19");

        test(new Decimal10(field, "123.4567890").add(new Decimal10(field, ".0000000501")),
             new Decimal10(field, "123.4567891"),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #20");

        test(new Decimal10(field, "123.4567890499"),
             new Decimal10(field, "123.4567890"),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #21");

        field.setRoundingMode(DfpField.RoundingMode.ROUND_DOWN);

        // RoundDecimal10 down
        test(new Decimal10(field, "1234567890").add(new Decimal10(field, "0.9")),
             new Decimal10(field, "1234567890"),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #22");

        test(new Decimal10(field, "1234567890").add(new Decimal10(field, "0.99999999")),
             new Decimal10(field, "1234567890"),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #23");

        test(new Decimal10(field, "-1234567890").add(new Decimal10(field, "-0.99999999")),
             new Decimal10(field, "-1234567890"),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #24");

        field.setRoundingMode(DfpField.RoundingMode.ROUND_UP);

        // RoundDecimal10 up
        test(new Decimal10(field, 1234567890).add(new Decimal10(field, "0.1")),
             new Decimal10(field, 1234567891l),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #25");

        test(new Decimal10(field, "1234567890").add(new Decimal10(field, "0.0001")),
             new Decimal10(field, "1234567891"),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #26");

        test(new Decimal10(field, "-1234567890").add(new Decimal10(field, "-0.1")),
             new Decimal10(field, "-1234567891"),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #27");

        test(new Decimal10(field, "-1234567890").add(new Decimal10(field, "-0.0001")),
             new Decimal10(field, "-1234567891"),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #28");

        test(new Decimal10(field, "-1234567890").add(new Decimal10(field, "0")),
             new Decimal10(field, "-1234567890"),
             0, "RoundDecimal10 #28.5");

        field.setRoundingMode(DfpField.RoundingMode.ROUND_HALF_UP);

        // RoundDecimal10 half up
        test(new Decimal10(field, "1234567890").add(new Decimal10(field, "0.4999999999")),
             new Decimal10(field, "1234567890"),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #29");

        test(new Decimal10(field, "1234567890").add(new Decimal10(field, "0.50000001")),
             new Decimal10(field, "1234567891"),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #30");

        test(new Decimal10(field, "1234567890").add(new Decimal10(field, "0.5")),
             new Decimal10(field, "1234567891"),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #30.5");

        test(new Decimal10(field, "-1234567890").add(new Decimal10(field, "-0.4999999999")),
             new Decimal10(field, "-1234567890"),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #31");

        test(new Decimal10(field, "-1234567890").add(new Decimal10(field, "-0.50000001")),
             new Decimal10(field, "-1234567891"),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #32");

        field.setRoundingMode(DfpField.RoundingMode.ROUND_HALF_DOWN);

        // RoundDecimal10 half down
        test(new Decimal10(field, "1234567890").add(new Decimal10(field, "0.5001")),
             new Decimal10(field, "1234567890"),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #33");

        test(new Decimal10(field, "1234567890").add(new Decimal10(field, "0.5000")),
             new Decimal10(field, "1234567890"),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #34");

        test(new Decimal10(field, "-1234567890").add(new Decimal10(field, "-0.5001")),
             new Decimal10(field, "-1234567890"),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #35");

        test(new Decimal10(field, "-1234567890").add(new Decimal10(field, "-0.6")),
             new Decimal10(field, "-1234567891"),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #35.5");

        test(new Decimal10(field, "-1234567890").add(new Decimal10(field, "-0.5000")),
             new Decimal10(field, "-1234567890"),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #36");

        field.setRoundingMode(DfpField.RoundingMode.ROUND_HALF_ODD);

        // RoundDecimal10 half odd
        test(new Decimal10(field, "1234567890").add(new Decimal10(field, "0.5000")),
             new Decimal10(field, "1234567891"),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #37");

        test(new Decimal10(field, "1234567891").add(new Decimal10(field, "0.5000")),
             new Decimal10(field, "1234567891"),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #38");

        test(new Decimal10(field, "-1234567890").add(new Decimal10(field, "-0.5000")),
             new Decimal10(field, "-1234567891"),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #39");

        test(new Decimal10(field, "-1234567891").add(new Decimal10(field, "-0.5000")),
             new Decimal10(field, "-1234567891"),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #40");

        field.setRoundingMode(DfpField.RoundingMode.ROUND_CEIL);

        // RoundDecimal10 ceil
        test(new Decimal10(field, "1234567890").add(new Decimal10(field, "0.0001")),
             new Decimal10(field, "1234567891"),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #41");

        test(new Decimal10(field, "-1234567890").add(new Decimal10(field, "-0.9999")),
             new Decimal10(field, "-1234567890"),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #42");

        field.setRoundingMode(DfpField.RoundingMode.ROUND_FLOOR);

        // RoundDecimal10 floor
        test(new Decimal10(field, "1234567890").add(new Decimal10(field, "0.9999")),
             new Decimal10(field, "1234567890"),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #43");

        test(new Decimal10(field, "-1234567890").add(new Decimal10(field, "-0.0001")),
             new Decimal10(field, "-1234567891"),
             DfpField.FLAG_INEXACT, "RoundDecimal10 #44");

        field.setRoundingMode(DfpField.RoundingMode.ROUND_HALF_EVEN);  // reset
    }

    @Test
    public void testNextAfter()
    {
        test(new DfpDec(field, 1).nextAfter(pinf),
             new DfpDec(field, "1.0000000000000001"),
             0, "NextAfter #1");

        test(new DfpDec(field, "1.0000000000000001").nextAfter(ninf),
             new DfpDec(field, 1),
             0, "NextAfter #1.5");

        test(new DfpDec(field, 1).nextAfter(ninf),
             new DfpDec(field, "0.99999999999999999"),
             0, "NextAfter #2");

        test(new DfpDec(field, "0.99999999999999999").nextAfter(new DfpDec(field, 2)),
             new DfpDec(field, 1),
             0, "NextAfter #3");

        test(new DfpDec(field, -1).nextAfter(ninf),
             new DfpDec(field, "-1.0000000000000001"),
             0, "NextAfter #4");

        test(new DfpDec(field, -1).nextAfter(pinf),
             new DfpDec(field, "-0.99999999999999999"),
             0, "NextAfter #5");

        test(new DfpDec(field, "-0.99999999999999999").nextAfter(new DfpDec(field, -2)),
             new DfpDec(field, (byte) -1),
             0, "NextAfter #6");

        test(new DfpDec(field, (byte) 2).nextAfter(new DfpDec(field, 2)),
             new DfpDec(field, 2l),
             0, "NextAfter #7");

        test(new DfpDec(field, 0).nextAfter(new DfpDec(field, 0)),
             new DfpDec(field, 0),
             0, "NextAfter #8");

        test(new DfpDec(field, -2).nextAfter(new DfpDec(field, -2)),
             new DfpDec(field, -2),
             0, "NextAfter #9");

        test(new DfpDec(field, 0).nextAfter(new DfpDec(field, 1)),
             new DfpDec(field, "1e-131092"),
             DfpField.FLAG_UNDERFLOW, "NextAfter #10");

        test(new DfpDec(field, 0).nextAfter(new DfpDec(field, -1)),
             new DfpDec(field, "-1e-131092"),
             DfpField.FLAG_UNDERFLOW, "NextAfter #11");

        test(new DfpDec(field, "-1e-131092").nextAfter(pinf),
             new DfpDec(field, "-0"),
             DfpField.FLAG_UNDERFLOW|DfpField.FLAG_INEXACT, "Next After #12");

        test(new DfpDec(field, "1e-131092").nextAfter(ninf), 
             new DfpDec(field, "0"),
             DfpField.FLAG_UNDERFLOW|DfpField.FLAG_INEXACT, "Next After #13");

        test(new DfpDec(field, "9.9999999999999999e131078").nextAfter(pinf),
             pinf,
             DfpField.FLAG_OVERFLOW|DfpField.FLAG_INEXACT, "Next After #14");
    }

}
