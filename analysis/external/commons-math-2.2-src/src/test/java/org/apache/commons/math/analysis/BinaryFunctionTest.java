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
package org.apache.commons.math.analysis;

import org.apache.commons.math.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

public class BinaryFunctionTest {

    @Test
    public void testAdd() throws Exception {
        Assert.assertEquals(5.0, BinaryFunction.ADD.value(2, 3), 1.0e-15);
        Assert.assertEquals(0.0, BinaryFunction.ADD.value(-1, 1), 1.0e-15);
    }

    @Test
    public void testSubtract() throws Exception {
        Assert.assertEquals(-1.0, BinaryFunction.SUBTRACT.value(2, 3), 1.0e-15);
        Assert.assertEquals(-2.0, BinaryFunction.SUBTRACT.value(-1, 1), 1.0e-15);
    }

    @Test
    public void testMultiply() throws Exception {
        Assert.assertEquals(6.0, BinaryFunction.MULTIPLY.value(2, 3), 1.0e-15);
        Assert.assertEquals(-1.0, BinaryFunction.MULTIPLY.value(-1, 1), 1.0e-15);
    }

    @Test
    public void testDivide() throws Exception {
        Assert.assertEquals(1.5, BinaryFunction.DIVIDE.value(3, 2), 1.0e-15);
        Assert.assertEquals(-1.0, BinaryFunction.DIVIDE.value(-1, 1), 1.0e-15);
    }

    @Test
    public void testPow() throws Exception {
        Assert.assertEquals(9.0, BinaryFunction.POW.value(3, 2), 1.0e-15);
        Assert.assertEquals(-1.0, BinaryFunction.POW.value(-1, 1), 1.0e-15);
    }

    @Test
    public void testAtan2() throws Exception {
        Assert.assertEquals(FastMath.PI / 4, BinaryFunction.ATAN2.value(1, 1), 1.0e-15);
        Assert.assertEquals(-FastMath.PI / 4, BinaryFunction.ATAN2.value(-1, 1), 1.0e-15);
    }

    @Test
    public void testFix1st() throws Exception {
        ComposableFunction f = BinaryFunction.POW.fix1stArgument(2);
        for (double x = 0.0; x < 1.0; x += 0.01) {
            Assert.assertEquals(FastMath.pow(2.0, x), f.value(x), 1.0e-15);
        }
    }

    @Test
    public void testFix2nd() throws Exception {
        ComposableFunction f = BinaryFunction.POW.fix2ndArgument(2);
        for (double y = 0.0; y < 1.0; y += 0.01) {
            Assert.assertEquals(y * y, f.value(y), 1.0e-15);
        }
    }

}
