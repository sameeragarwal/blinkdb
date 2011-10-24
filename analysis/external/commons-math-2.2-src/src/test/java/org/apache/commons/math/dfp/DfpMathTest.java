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

public class DfpMathTest {

    private DfpField factory;
    private Dfp pinf;
    private Dfp ninf;
    private Dfp nan;
    private Dfp qnan;

    @Before
    public void setUp() {
        // Some basic setup.  Define some constants and clear the status flags
        factory = new DfpField(20);
        pinf = factory.newDfp("1").divide(factory.newDfp("0"));
        ninf = factory.newDfp("-1").divide(factory.newDfp("0"));
        nan = factory.newDfp("0").divide(factory.newDfp("0"));
        qnan = factory.newDfp((byte)1, Dfp.QNAN);
        ninf.getField().clearIEEEFlags();

        // force loading of dfpmath
        Dfp pi = factory.getPi();
        pi.getField().clearIEEEFlags();
    }

    @After
    public void tearDown() {
        pinf = null;
        ninf = null;
        nan  = null;
        qnan = null;
    }

    // Generic test function.  Takes params x and y and tests them for 
    // equality.  Then checks the status flags against the flags argument.
    // If the test fail, it prints the desc string
    private void test(Dfp x, Dfp y, int flags, String desc)
    {
        boolean b = x.equals(y);

        if (!x.equals(y) && !x.unequal(y))  // NaNs involved 
            b = (x.toString().equals(y.toString()));

        if (x.equals(factory.newDfp("0")))  // distinguish +/- zero
            b = (b && (x.toString().equals(y.toString())));

        b = (b && x.getField().getIEEEFlags() == flags);

        if (!b)
            Assert.assertTrue("assersion failed "+desc+" x = "+x.toString()+" flags = "+x.getField().getIEEEFlags(), b);

        x.getField().clearIEEEFlags();
    }

    @Test
    public void testPow()  
    {
        // Test special cases  exponent of zero
        test(DfpMath.pow(factory.newDfp("0"), factory.newDfp("0")),      
             factory.newDfp("1"), 
             0, "pow #1");

        test(DfpMath.pow(factory.newDfp("0"), factory.newDfp("-0")),      
             factory.newDfp("1"), 
             0, "pow #2");

        test(DfpMath.pow(factory.newDfp("2"), factory.newDfp("0")),      
             factory.newDfp("1"), 
             0, "pow #3");

        test(DfpMath.pow(factory.newDfp("-2"), factory.newDfp("-0")),      
             factory.newDfp("1"), 
             0, "pow #4");

        test(DfpMath.pow(pinf, factory.newDfp("-0")),      
             factory.newDfp("1"), 
             0, "pow #5");

        test(DfpMath.pow(pinf, factory.newDfp("0")),
             factory.newDfp("1"), 
             0, "pow #6");

        test(DfpMath.pow(ninf, factory.newDfp("-0")),      
             factory.newDfp("1"), 
             0, "pow #7");

        test(DfpMath.pow(ninf, factory.newDfp("0")),
             factory.newDfp("1"), 
             0, "pow #8");

        test(DfpMath.pow(qnan, factory.newDfp("0")),
             factory.newDfp("1"), 
             0, "pow #8");

        // exponent of one
        test(DfpMath.pow(factory.newDfp("0"), factory.newDfp("1")),
             factory.newDfp("0"), 
             0, "pow #9");

        test(DfpMath.pow(factory.newDfp("-0"), factory.newDfp("1")),      
             factory.newDfp("-0"), 
             0, "pow #10");

        test(DfpMath.pow(factory.newDfp("2"), factory.newDfp("1")),
             factory.newDfp("2"), 
             0, "pow #11");

        test(DfpMath.pow(factory.newDfp("-2"), factory.newDfp("1")),
             factory.newDfp("-2"), 
             0, "pow #12");

        test(DfpMath.pow(pinf, factory.newDfp("1")),      
             pinf, 
             0, "pow #13");

        test(DfpMath.pow(ninf, factory.newDfp("1")),
             ninf, 
             0, "pow #14");

        test(DfpMath.pow(qnan, factory.newDfp("1")),
             qnan, 
             DfpField.FLAG_INVALID, "pow #14.1");

        // exponent of NaN
        test(DfpMath.pow(factory.newDfp("0"), qnan),
             qnan, 
             DfpField.FLAG_INVALID, "pow #15");

        test(DfpMath.pow(factory.newDfp("-0"), qnan),      
             qnan, 
             DfpField.FLAG_INVALID, "pow #16");

        test(DfpMath.pow(factory.newDfp("2"), qnan),
             qnan, 
             DfpField.FLAG_INVALID, "pow #17");

        test(DfpMath.pow(factory.newDfp("-2"), qnan),
             qnan, 
             DfpField.FLAG_INVALID, "pow #18");

        test(DfpMath.pow(pinf, qnan),      
             qnan, 
             DfpField.FLAG_INVALID, "pow #19");

        test(DfpMath.pow(ninf, qnan),
             qnan, 
             DfpField.FLAG_INVALID, "pow #20");

        test(DfpMath.pow(qnan, qnan),
             qnan, 
             DfpField.FLAG_INVALID, "pow #21");

        // radix of NaN
        test(DfpMath.pow(qnan, factory.newDfp("1")),
             qnan, 
             DfpField.FLAG_INVALID, "pow #22");

        test(DfpMath.pow(qnan, factory.newDfp("-1")),      
             qnan,
             DfpField.FLAG_INVALID, "pow #23");

        test(DfpMath.pow(qnan, pinf),
             qnan,
             DfpField.FLAG_INVALID, "pow #24");

        test(DfpMath.pow(qnan, ninf),
             qnan, 
             DfpField.FLAG_INVALID, "pow #25");

        test(DfpMath.pow(qnan, qnan),
             qnan, 
             DfpField.FLAG_INVALID, "pow #26");

        // (x > 1) ^ pinf = pinf,    (x < -1) ^ pinf = pinf
        test(DfpMath.pow(factory.newDfp("2"), pinf),
             pinf, 
             0, "pow #27");

        test(DfpMath.pow(factory.newDfp("-2"), pinf),      
             pinf,
             0, "pow #28");

        test(DfpMath.pow(pinf, pinf),
             pinf,
             0, "pow #29");

        test(DfpMath.pow(ninf, pinf),
             pinf, 
             0, "pow #30");

        // (x > 1) ^ ninf = +0,    (x < -1) ^ ninf = +0
        test(DfpMath.pow(factory.newDfp("2"), ninf),
             factory.getZero(), 
             0, "pow #31");

        test(DfpMath.pow(factory.newDfp("-2"), ninf),      
             factory.getZero(),
             0, "pow #32");

        test(DfpMath.pow(pinf, ninf),
             factory.getZero(),
             0, "pow #33");

        test(DfpMath.pow(ninf, ninf),
             factory.getZero(), 
             0, "pow #34");

        // (-1 < x < 1) ^ pinf = 0
        test(DfpMath.pow(factory.newDfp("0.5"), pinf),
             factory.getZero(), 
             0, "pow #35");

        test(DfpMath.pow(factory.newDfp("-0.5"), pinf),      
             factory.getZero(),
             0, "pow #36");

        // (-1 < x < 1) ^ ninf = pinf 
        test(DfpMath.pow(factory.newDfp("0.5"), ninf),
             pinf, 
             0, "pow #37");

        test(DfpMath.pow(factory.newDfp("-0.5"), ninf),      
             pinf,
             0, "pow #38");

        // +/- 1  ^ +/-inf  = NaN
        test(DfpMath.pow(factory.getOne(), pinf),
             qnan, 
             DfpField.FLAG_INVALID, "pow #39");

        test(DfpMath.pow(factory.getOne(), ninf),      
             qnan,
             DfpField.FLAG_INVALID, "pow #40");

        test(DfpMath.pow(factory.newDfp("-1"), pinf),
             qnan, 
             DfpField.FLAG_INVALID, "pow #41");

        test(DfpMath.pow(factory.getOne().negate(), ninf),      
             qnan,
             DfpField.FLAG_INVALID, "pow #42");

        // +0  ^ +anything except 0, NAN  = +0

        test(DfpMath.pow(factory.newDfp("0"), factory.newDfp("1")),
             factory.newDfp("0"),
             0, "pow #43");

        test(DfpMath.pow(factory.newDfp("0"), factory.newDfp("1e30")),
             factory.newDfp("0"),
             0, "pow #44");

        test(DfpMath.pow(factory.newDfp("0"), factory.newDfp("1e-30")),
             factory.newDfp("0"),
             0, "pow #45");

        test(DfpMath.pow(factory.newDfp("0"), pinf),
             factory.newDfp("0"),
             0, "pow #46");

        // -0  ^ +anything except 0, NAN, odd integer  = +0

        test(DfpMath.pow(factory.newDfp("-0"), factory.newDfp("2")),
             factory.newDfp("0"),
             0, "pow #47");

        test(DfpMath.pow(factory.newDfp("-0"), factory.newDfp("1e30")),
             factory.newDfp("0"),
             0, "pow #48");

        test(DfpMath.pow(factory.newDfp("-0"), factory.newDfp("1e-30")),
             factory.newDfp("0"),
             DfpField.FLAG_INEXACT, "pow #49");

        test(DfpMath.pow(factory.newDfp("-0"), pinf),
             factory.newDfp("0"),
             0, "pow #50");

        // +0  ^ -anything except 0, NAN  = +INF

        test(DfpMath.pow(factory.newDfp("0"), factory.newDfp("-1")),
             pinf,
             0, "pow #51");

        test(DfpMath.pow(factory.newDfp("0"), factory.newDfp("-1e30")),
             pinf,
             0, "pow #52");

        test(DfpMath.pow(factory.newDfp("0"), factory.newDfp("-1e-30")),
             pinf,
             0, "pow #53");

        test(DfpMath.pow(factory.newDfp("0"), ninf),
             pinf,
             0, "pow #54");

        // -0  ^ -anything except 0, NAN, odd integer  = +INF

        test(DfpMath.pow(factory.newDfp("-0"), factory.newDfp("-2")),
             pinf,
             0, "pow #55");

        test(DfpMath.pow(factory.newDfp("-0"), factory.newDfp("-1e30")),
             pinf,
             0, "pow #56");

        test(DfpMath.pow(factory.newDfp("-0"), factory.newDfp("-1e-30")),
             pinf,
             DfpField.FLAG_INEXACT, "pow #57");

        test(DfpMath.pow(factory.newDfp("-0"), ninf),
             pinf,
             0, "pow #58");

        // -0  ^ -odd integer   =  -INF
        test(DfpMath.pow(factory.newDfp("-0"), factory.newDfp("-1")),
             ninf,
             DfpField.FLAG_INEXACT, "pow #59");

        test(DfpMath.pow(factory.newDfp("-0"), factory.newDfp("-12345")),
             ninf,
             DfpField.FLAG_INEXACT, "pow #60");

        // -0  ^ +odd integer   =  -0
        test(DfpMath.pow(factory.newDfp("-0"), factory.newDfp("3")),
             factory.newDfp("-0"),
             DfpField.FLAG_INEXACT, "pow #61");

        test(DfpMath.pow(factory.newDfp("-0"), factory.newDfp("12345")),
             factory.newDfp("-0"),
             DfpField.FLAG_INEXACT, "pow #62");

        // pinf  ^ +anything   = pinf 
        test(DfpMath.pow(pinf, factory.newDfp("3")),
             pinf,
             0, "pow #63");

        test(DfpMath.pow(pinf, factory.newDfp("1e30")),
             pinf,
             0, "pow #64");

        test(DfpMath.pow(pinf, factory.newDfp("1e-30")),
             pinf,
             0, "pow #65");

        test(DfpMath.pow(pinf, pinf),
             pinf,
             0, "pow #66");

        // pinf  ^ -anything   = +0 

        test(DfpMath.pow(pinf, factory.newDfp("-3")),
             factory.getZero(),
             0, "pow #67");

        test(DfpMath.pow(pinf, factory.newDfp("-1e30")),
             factory.getZero(),
             0, "pow #68");

        test(DfpMath.pow(pinf, factory.newDfp("-1e-30")),
             factory.getZero(),
             0, "pow #69");

        test(DfpMath.pow(pinf, ninf),
             factory.getZero(),
             0, "pow #70");

        // ninf  ^ anything   = -0 ^ -anything
        // ninf  ^ -anything except 0, NAN, odd integer  = +0

        test(DfpMath.pow(ninf, factory.newDfp("-2")),
             factory.newDfp("0"),
             0, "pow #71");

        test(DfpMath.pow(ninf, factory.newDfp("-1e30")),
             factory.newDfp("0"),
             0, "pow #72");

        test(DfpMath.pow(ninf, factory.newDfp("-1e-30")),
             factory.newDfp("0"),
             DfpField.FLAG_INEXACT, "pow #73");

        test(DfpMath.pow(ninf, ninf),
             factory.newDfp("0"),
             0, "pow #74");

        // ninf  ^ +anything except 0, NAN, odd integer  = +INF

        test(DfpMath.pow(ninf, factory.newDfp("2")),
             pinf,
             0, "pow #75");

        test(DfpMath.pow(ninf, factory.newDfp("1e30")),
             pinf,
             0, "pow #76");

        test(DfpMath.pow(ninf, factory.newDfp("1e-30")),
             pinf,
             DfpField.FLAG_INEXACT, "pow #77");

        test(DfpMath.pow(ninf, pinf),
             pinf,
             0, "pow #78");

        // ninf  ^ +odd integer   =  -INF
        test(DfpMath.pow(ninf, factory.newDfp("3")),
             ninf,
             DfpField.FLAG_INEXACT, "pow #79");

        test(DfpMath.pow(ninf, factory.newDfp("12345")),
             ninf,
             DfpField.FLAG_INEXACT, "pow #80");

        // ninf  ^ -odd integer   =  -0
        test(DfpMath.pow(ninf, factory.newDfp("-3")),
             factory.newDfp("-0"),
             DfpField.FLAG_INEXACT, "pow #81");

        test(DfpMath.pow(ninf, factory.newDfp("-12345")),
             factory.newDfp("-0"),
             DfpField.FLAG_INEXACT, "pow #82");

        // -anything ^ integer 
        test(DfpMath.pow(factory.newDfp("-2"), factory.newDfp("3")),
             factory.newDfp("-8"),
             DfpField.FLAG_INEXACT, "pow #83");

        test(DfpMath.pow(factory.newDfp("-2"), factory.newDfp("16")),
             factory.newDfp("65536"),
             0, "pow #84");

        test(DfpMath.pow(factory.newDfp("-2"), factory.newDfp("-3")),
             factory.newDfp("-0.125"),
             DfpField.FLAG_INEXACT, "pow #85");

        test(DfpMath.pow(factory.newDfp("-2"), factory.newDfp("-4")),
             factory.newDfp("0.0625"),
             0, "pow #86");

        // -anything ^ noninteger = NaN

        test(DfpMath.pow(factory.newDfp("-2"), factory.newDfp("-4.1")),
             qnan,
             DfpField.FLAG_INVALID|DfpField.FLAG_INEXACT, "pow #87");

        // Some fractional cases.
        test(DfpMath.pow(factory.newDfp("2"),factory.newDfp("1.5")),
             factory.newDfp("2.8284271247461901"), 
             DfpField.FLAG_INEXACT, "pow #88");
    }

    @Test
    public void testSin()
    {
        test(DfpMath.sin(pinf),
             nan,
             DfpField.FLAG_INVALID|DfpField.FLAG_INEXACT, "sin #1");

        test(DfpMath.sin(nan),
             nan,
             DfpField.FLAG_INVALID|DfpField.FLAG_INEXACT, "sin #2");

        test(DfpMath.sin(factory.getZero()),
             factory.getZero(),
             DfpField.FLAG_INEXACT, "sin #3");

        test(DfpMath.sin(factory.getPi()),
             factory.getZero(),
             DfpField.FLAG_INEXACT, "sin #4");

        test(DfpMath.sin(factory.getPi().negate()),
             factory.newDfp("-0"),
             DfpField.FLAG_INEXACT, "sin #5");

        test(DfpMath.sin(factory.getPi().multiply(2)),
             factory.getZero(),
             DfpField.FLAG_INEXACT, "sin #6");

        test(DfpMath.sin(factory.getPi().divide(2)),
             factory.getOne(),
             DfpField.FLAG_INEXACT, "sin #7");

        test(DfpMath.sin(factory.getPi().divide(2).negate()),
             factory.getOne().negate(),
             DfpField.FLAG_INEXACT, "sin #8");

        test(DfpMath.sin(DfpMath.atan(factory.getOne())),  // pi/4
             factory.newDfp("0.5").sqrt(),
             DfpField.FLAG_INEXACT, "sin #9");

        test(DfpMath.sin(DfpMath.atan(factory.getOne())).negate(),  // -pi/4
             factory.newDfp("0.5").sqrt().negate(),
             DfpField.FLAG_INEXACT, "sin #10");

        test(DfpMath.sin(DfpMath.atan(factory.getOne())).negate(),  // -pi/4
             factory.newDfp("0.5").sqrt().negate(),
             DfpField.FLAG_INEXACT, "sin #11");

        test(DfpMath.sin(factory.newDfp("0.1")),
             factory.newDfp("0.0998334166468281523"),
             DfpField.FLAG_INEXACT, "sin #12");

        test(DfpMath.sin(factory.newDfp("0.2")),
             factory.newDfp("0.19866933079506121546"),
             DfpField.FLAG_INEXACT, "sin #13");

        test(DfpMath.sin(factory.newDfp("0.3")),
             factory.newDfp("0.2955202066613395751"),
             DfpField.FLAG_INEXACT, "sin #14");

        test(DfpMath.sin(factory.newDfp("0.4")),
             factory.newDfp("0.38941834230865049166"),
             DfpField.FLAG_INEXACT, "sin #15");

        test(DfpMath.sin(factory.newDfp("0.5")),
             factory.newDfp("0.47942553860420300026"),  // off by one ULP
             DfpField.FLAG_INEXACT, "sin #16");

        test(DfpMath.sin(factory.newDfp("0.6")),
             factory.newDfp("0.56464247339503535721"),  // off by one ULP
             DfpField.FLAG_INEXACT, "sin #17");

        test(DfpMath.sin(factory.newDfp("0.7")),
             factory.newDfp("0.64421768723769105367"),  
             DfpField.FLAG_INEXACT, "sin #18");

        test(DfpMath.sin(factory.newDfp("0.8")),        
             factory.newDfp("0.71735609089952276163"),
             DfpField.FLAG_INEXACT, "sin #19");

        test(DfpMath.sin(factory.newDfp("0.9")),        // off by one ULP
             factory.newDfp("0.78332690962748338847"),
             DfpField.FLAG_INEXACT, "sin #20");

        test(DfpMath.sin(factory.newDfp("1.0")),
             factory.newDfp("0.84147098480789650666"),
             DfpField.FLAG_INEXACT, "sin #21");

        test(DfpMath.sin(factory.newDfp("1.1")),
             factory.newDfp("0.89120736006143533995"),
             DfpField.FLAG_INEXACT, "sin #22");

        test(DfpMath.sin(factory.newDfp("1.2")),
             factory.newDfp("0.93203908596722634968"),
             DfpField.FLAG_INEXACT, "sin #23");

        test(DfpMath.sin(factory.newDfp("1.3")),
             factory.newDfp("0.9635581854171929647"),
             DfpField.FLAG_INEXACT, "sin #24");

        test(DfpMath.sin(factory.newDfp("1.4")),
             factory.newDfp("0.98544972998846018066"),
             DfpField.FLAG_INEXACT, "sin #25");

        test(DfpMath.sin(factory.newDfp("1.5")),
             factory.newDfp("0.99749498660405443096"),
             DfpField.FLAG_INEXACT, "sin #26");

        test(DfpMath.sin(factory.newDfp("1.6")),
             factory.newDfp("0.99957360304150516323"),
             DfpField.FLAG_INEXACT, "sin #27");
    }


}
