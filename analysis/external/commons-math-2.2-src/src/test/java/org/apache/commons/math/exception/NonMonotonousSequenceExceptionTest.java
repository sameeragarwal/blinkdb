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
package org.apache.commons.math.exception;

import org.apache.commons.math.util.MathUtils;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test for {@link NonMonotonousSequenceException}.
 * 
 * @version $Revision$ $Date$ 
 */
public class NonMonotonousSequenceExceptionTest {
    @Test
    public void testAccessors() {
        NonMonotonousSequenceException e
            = new NonMonotonousSequenceException(0, -1, 1, MathUtils.OrderDirection.DECREASING, false);
        Assert.assertEquals(0, e.getArgument());
        Assert.assertEquals(-1, e.getPrevious());
        Assert.assertEquals(1, e.getIndex());
        Assert.assertTrue(e.getDirection() == MathUtils.OrderDirection.DECREASING);
        Assert.assertFalse(e.getStrict());

        e = new NonMonotonousSequenceException(-1, 0, 1);
        Assert.assertEquals(-1, e.getArgument());
        Assert.assertEquals(0, e.getPrevious());
        Assert.assertEquals(1, e.getIndex());
        Assert.assertTrue(e.getDirection() == MathUtils.OrderDirection.INCREASING);
        Assert.assertTrue(e.getStrict());
    }
}
