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

package org.apache.commons.math.stat.clustering;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math.TestUtils;
import org.apache.commons.math.util.FastMath;
import org.junit.Test;

public class EuclideanIntegerPointTest {

    @Test
    public void testArrayIsReference() {
        int[] array = { -3, -2, -1, 0, 1 };
        assertTrue(array == new EuclideanIntegerPoint(array).getPoint());
    }

    @Test
    public void testDistance() {
        EuclideanIntegerPoint e1 = new EuclideanIntegerPoint(new int[] { -3, -2, -1, 0, 1 });
        EuclideanIntegerPoint e2 = new EuclideanIntegerPoint(new int[] {  1,  0, -1, 1, 1 });
        assertEquals(FastMath.sqrt(21.0), e1.distanceFrom(e2), 1.0e-15);
        assertEquals(0.0, e1.distanceFrom(e1), 1.0e-15);
        assertEquals(0.0, e2.distanceFrom(e2), 1.0e-15);
    }

    @Test
    public void testCentroid() {
        List<EuclideanIntegerPoint> list = new ArrayList<EuclideanIntegerPoint>();
        list.add(new EuclideanIntegerPoint(new int[] {  1,  3 }));
        list.add(new EuclideanIntegerPoint(new int[] {  2,  2 }));
        list.add(new EuclideanIntegerPoint(new int[] {  3,  3 }));
        list.add(new EuclideanIntegerPoint(new int[] {  2,  4 }));
        EuclideanIntegerPoint c = list.get(0).centroidOf(list);
        assertEquals(2, c.getPoint()[0]);
        assertEquals(3, c.getPoint()[1]);
    }

    @Test
    public void testSerial() {
        EuclideanIntegerPoint p = new EuclideanIntegerPoint(new int[] { -3, -2, -1, 0, 1 });
        assertEquals(p, TestUtils.serializeAndRecover(p));
    }

}
