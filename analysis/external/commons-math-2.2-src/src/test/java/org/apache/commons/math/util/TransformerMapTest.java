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

package org.apache.commons.math.util;

import org.apache.commons.math.TestUtils;

import junit.framework.TestCase;

/**
 * @version $Revision: 811685 $ $Date: 2009-09-05 19:36:48 +0200 (sam. 05 sept. 2009) $
 */
public class TransformerMapTest extends TestCase {
    /**
     *
     */
    public void testPutTransformer(){
        NumberTransformer expected = new DefaultTransformer();

        TransformerMap map = new TransformerMap();
        map.putTransformer(TransformerMapTest.class, expected);
        assertEquals(expected, map.getTransformer(TransformerMapTest.class));
    }

    /**
     *
     */
    public void testContainsClass(){
        NumberTransformer expected = new DefaultTransformer();
        TransformerMap map = new TransformerMap();
        map.putTransformer(TransformerMapTest.class, expected);
        assertTrue(map.containsClass(TransformerMapTest.class));
    }

    /**
     *
     */
    public void testContainsTransformer(){
        NumberTransformer expected = new DefaultTransformer();
        TransformerMap map = new TransformerMap();
        map.putTransformer(TransformerMapTest.class, expected);
        assertTrue(map.containsTransformer(expected));
    }

    /**
     *
     */
    public void testRemoveTransformer(){
        NumberTransformer expected = new DefaultTransformer();

        TransformerMap map = new TransformerMap();
        map.putTransformer(TransformerMapTest.class, expected);
        assertTrue(map.containsClass(TransformerMapTest.class));
        assertTrue(map.containsTransformer(expected));
        map.removeTransformer(TransformerMapTest.class);
        assertFalse(map.containsClass(TransformerMapTest.class));
        assertFalse(map.containsTransformer(expected));
    }

    /**
     *
     */
    public void testClear(){
        NumberTransformer expected = new DefaultTransformer();

        TransformerMap map = new TransformerMap();
        map.putTransformer(TransformerMapTest.class, expected);
        assertTrue(map.containsClass(TransformerMapTest.class));
        map.clear();
        assertFalse(map.containsClass(TransformerMapTest.class));
    }

    /**
     *
     */
    public void testClasses(){
        NumberTransformer expected = new DefaultTransformer();
        TransformerMap map = new TransformerMap();
        map.putTransformer(TransformerMapTest.class, expected);
        assertTrue(map.classes().contains(TransformerMapTest.class));
    }

    /**
     *
     */
    public void testTransformers(){
        NumberTransformer expected = new DefaultTransformer();
        TransformerMap map = new TransformerMap();
        map.putTransformer(TransformerMapTest.class, expected);
        assertTrue(map.transformers().contains(expected));
    }

    public void testSerial(){
        NumberTransformer expected = new DefaultTransformer();
        TransformerMap map = new TransformerMap();
        map.putTransformer(TransformerMapTest.class, expected);
        assertEquals(map, TestUtils.serializeAndRecover(map));
    }

}
