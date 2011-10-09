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

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.math.Field;
import org.apache.commons.math.fraction.Fraction;
import org.apache.commons.math.fraction.FractionConversionException;
import org.apache.commons.math.fraction.FractionField;

import junit.framework.TestCase;

public class OpenIntToFieldTest extends TestCase {

    private Map<Integer, Fraction> javaMap = new HashMap<Integer, Fraction>();
    private FractionField field = FractionField.getInstance();

    @Override
    protected void setUp() throws Exception {
        javaMap.put(50, new Fraction(100.0));
        javaMap.put(75, new Fraction(75.0));
        javaMap.put(25, new Fraction(500.0));
        javaMap.put(Integer.MAX_VALUE, new Fraction(Integer.MAX_VALUE));
        javaMap.put(0, new Fraction(-1.0));
        javaMap.put(1, new Fraction(0.0));
        javaMap.put(33, new Fraction(-0.1));
        javaMap.put(23234234, new Fraction(-242343.0));
        javaMap.put(23321, new Fraction (Integer.MIN_VALUE));
        javaMap.put(-4444, new Fraction(332.0));
        javaMap.put(-1, new Fraction(-2323.0));
        javaMap.put(Integer.MIN_VALUE, new Fraction(44.0));

        /* Add a few more to cause the table to rehash */
        javaMap.putAll(generate());

    }

    private Map<Integer, Fraction> generate() {
        Map<Integer, Fraction> map = new HashMap<Integer, Fraction>();
        Random r = new Random();
        double dd=0;
        for (int i = 0; i < 2000; ++i)
            dd = r.nextDouble();
            try {
                map.put(r.nextInt(), new Fraction(dd));
            } catch (FractionConversionException e) {
                throw new IllegalStateException("Invalid :"+dd, e);
            }
        return map;
    }

    private OpenIntToFieldHashMap<Fraction> createFromJavaMap(Field<Fraction> field) {
        OpenIntToFieldHashMap<Fraction> map = new OpenIntToFieldHashMap<Fraction>(field);
        for (Map.Entry<Integer, Fraction> mapEntry : javaMap.entrySet()) {
            map.put(mapEntry.getKey(), mapEntry.getValue());
        }
        return map;
    }

    public void testPutAndGetWith0ExpectedSize() {
        OpenIntToFieldHashMap<Fraction> map = new OpenIntToFieldHashMap<Fraction>(field,0);
        assertPutAndGet(map);
    }

    public void testPutAndGetWithExpectedSize() {
        OpenIntToFieldHashMap<Fraction> map = new OpenIntToFieldHashMap<Fraction>(field,500);
        assertPutAndGet(map);
    }

    public void testPutAndGet() {
        OpenIntToFieldHashMap<Fraction> map = new OpenIntToFieldHashMap<Fraction>(field);
        assertPutAndGet(map);
    }

    private void assertPutAndGet(OpenIntToFieldHashMap<Fraction> map) {
        assertPutAndGet(map, 0, new HashSet<Integer>());
    }

    private void assertPutAndGet(OpenIntToFieldHashMap<Fraction> map, int mapSize,
            Set<Integer> keysInMap) {
        assertEquals(mapSize, map.size());
        for (Map.Entry<Integer, Fraction> mapEntry : javaMap.entrySet()) {
            map.put(mapEntry.getKey(), mapEntry.getValue());
            if (!keysInMap.contains(mapEntry.getKey()))
                ++mapSize;
            assertEquals(mapSize, map.size());
            assertEquals(mapEntry.getValue(), map.get(mapEntry.getKey()));
        }
    }

    public void testPutAbsentOnExisting() {
        OpenIntToFieldHashMap<Fraction> map = createFromJavaMap(field);
        int size = javaMap.size();
        for (Map.Entry<Integer, Fraction> mapEntry : generateAbsent().entrySet()) {
            map.put(mapEntry.getKey(), mapEntry.getValue());
            assertEquals(++size, map.size());
            assertEquals(mapEntry.getValue(), map.get(mapEntry.getKey()));
        }
    }

    public void testPutOnExisting() {
        OpenIntToFieldHashMap<Fraction> map = createFromJavaMap(field);
        for (Map.Entry<Integer, Fraction> mapEntry : javaMap.entrySet()) {
            map.put(mapEntry.getKey(), mapEntry.getValue());
            assertEquals(javaMap.size(), map.size());
            assertEquals(mapEntry.getValue(), map.get(mapEntry.getKey()));
        }
    }

    public void testGetAbsent() {
        Map<Integer, Fraction> generated = generateAbsent();
        OpenIntToFieldHashMap<Fraction> map = createFromJavaMap(field);

        for (Map.Entry<Integer, Fraction> mapEntry : generated.entrySet())
            assertTrue(field.getZero().equals(map.get(mapEntry.getKey())));
    }

    public void testGetFromEmpty() {
        OpenIntToFieldHashMap<Fraction> map = new OpenIntToFieldHashMap<Fraction>(field);
        assertTrue(field.getZero().equals(map.get(5)));
        assertTrue(field.getZero().equals(map.get(0)));
        assertTrue(field.getZero().equals(map.get(50)));
    }

    public void testRemove() {
        OpenIntToFieldHashMap<Fraction> map = createFromJavaMap(field);
        int mapSize = javaMap.size();
        assertEquals(mapSize, map.size());
        for (Map.Entry<Integer, Fraction> mapEntry : javaMap.entrySet()) {
            map.remove(mapEntry.getKey());
            assertEquals(--mapSize, map.size());
            assertTrue(field.getZero().equals(map.get(mapEntry.getKey())));
        }

        /* Ensure that put and get still work correctly after removals */
        assertPutAndGet(map);
    }

    /* This time only remove some entries */
    public void testRemove2() {
        OpenIntToFieldHashMap<Fraction> map = createFromJavaMap(field);
        int mapSize = javaMap.size();
        int count = 0;
        Set<Integer> keysInMap = new HashSet<Integer>(javaMap.keySet());
        for (Map.Entry<Integer, Fraction> mapEntry : javaMap.entrySet()) {
            keysInMap.remove(mapEntry.getKey());
            map.remove(mapEntry.getKey());
            assertEquals(--mapSize, map.size());
            assertTrue(field.getZero().equals(map.get(mapEntry.getKey())));
            if (count++ > 5)
                break;
        }

        /* Ensure that put and get still work correctly after removals */
        assertPutAndGet(map, mapSize, keysInMap);
    }

    public void testRemoveFromEmpty() {
        OpenIntToFieldHashMap<Fraction> map = new OpenIntToFieldHashMap<Fraction>(field);
        assertTrue(field.getZero().equals(map.remove(50)));
    }

    public void testRemoveAbsent() {
        Map<Integer, Fraction> generated = generateAbsent();

        OpenIntToFieldHashMap<Fraction> map = createFromJavaMap(field);
        int mapSize = map.size();

        for (Map.Entry<Integer, Fraction> mapEntry : generated.entrySet()) {
            map.remove(mapEntry.getKey());
            assertEquals(mapSize, map.size());
            assertTrue(field.getZero().equals(map.get(mapEntry.getKey())));
        }
    }

    /**
     * Returns a map with at least 100 elements where each element is absent from javaMap.
     */
    private Map<Integer, Fraction> generateAbsent() {
        Map<Integer, Fraction> generated = new HashMap<Integer, Fraction>();
        do {
            generated.putAll(generate());
            for (Integer key : javaMap.keySet())
                generated.remove(key);
        } while (generated.size() < 100);
        return generated;
    }

    public void testCopy() {
        OpenIntToFieldHashMap<Fraction> copy =
            new OpenIntToFieldHashMap<Fraction>(createFromJavaMap(field));
        assertEquals(javaMap.size(), copy.size());

        for (Map.Entry<Integer, Fraction> mapEntry : javaMap.entrySet())
            assertEquals(mapEntry.getValue(), copy.get(mapEntry.getKey()));
    }

    public void testContainsKey() {
        OpenIntToFieldHashMap<Fraction> map = createFromJavaMap(field);
        for (Entry<Integer, Fraction> mapEntry : javaMap.entrySet()) {
            assertTrue(map.containsKey(mapEntry.getKey()));
        }
        for (Map.Entry<Integer, Fraction> mapEntry : generateAbsent().entrySet()) {
            assertFalse(map.containsKey(mapEntry.getKey()));
        }
        for (Entry<Integer, Fraction> mapEntry : javaMap.entrySet()) {
            int key = mapEntry.getKey();
            assertTrue(map.containsKey(key));
            map.remove(key);
            assertFalse(map.containsKey(key));
        }
    }

    public void testIterator() {
        OpenIntToFieldHashMap<Fraction> map = createFromJavaMap(field);
        OpenIntToFieldHashMap<Fraction>.Iterator iterator = map.iterator();
        for (int i = 0; i < map.size(); ++i) {
            assertTrue(iterator.hasNext());
            iterator.advance();
            int key = iterator.key();
            assertTrue(map.containsKey(key));
            assertEquals(javaMap.get(key), map.get(key));
            assertEquals(javaMap.get(key), iterator.value());
            assertTrue(javaMap.containsKey(key));
        }
        assertFalse(iterator.hasNext());
        try {
            iterator.advance();
            fail("an exception should have been thrown");
        } catch (NoSuchElementException nsee) {
            // expected
        }
    }

    public void testConcurrentModification() {
        OpenIntToFieldHashMap<Fraction> map = createFromJavaMap(field);
        OpenIntToFieldHashMap<Fraction>.Iterator iterator = map.iterator();
        map.put(3, new Fraction(3));
        try {
            iterator.advance();
            fail("an exception should have been thrown");
        } catch (ConcurrentModificationException cme) {
            // expected
        }
    }

    /**
     * Regression test for a bug in findInsertionIndex where the hashing in the second probing
     * loop was inconsistent with the first causing duplicate keys after the right sequence
     * of puts and removes.
     */
    public void testPutKeysWithCollisions() {
        OpenIntToFieldHashMap<Fraction> map = new OpenIntToFieldHashMap<Fraction>(field);
        int key1 = -1996012590;
        Fraction value1 = new Fraction(1);
        map.put(key1, value1);
        int key2 = 835099822;
        map.put(key2, value1);
        int key3 = 1008859686;
        map.put(key3, value1);
        assertEquals(value1, map.get(key3));
        assertEquals(3, map.size());

        map.remove(key2);
        Fraction value2 = new Fraction(2);
        map.put(key3, value2);
        assertEquals(value2, map.get(key3));
        assertEquals(2, map.size());
    }

    /**
     * Similar to testPutKeysWithCollisions() but exercises the codepaths in a slightly
     * different manner.
     */
    public void testPutKeysWithCollision2() {
        OpenIntToFieldHashMap<Fraction>map = new OpenIntToFieldHashMap<Fraction>(field);
        int key1 = 837989881;
        Fraction value1 = new Fraction(1);
        map.put(key1, value1);
        int key2 = 476463321;
        map.put(key2, value1);
        assertEquals(2, map.size());
        assertEquals(value1, map.get(key2));

        map.remove(key1);
        Fraction value2 = new Fraction(2);
        map.put(key2, value2);
        assertEquals(1, map.size());
        assertEquals(value2, map.get(key2));
    }


}
