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
import org.apache.commons.math.random.RandomDataImpl;
import org.apache.commons.math.random.RandomData;


/**
 * This class contains test cases for the ResizableDoubleArray.
 *
 * @version $Revision: 1053282 $ $Date: 2010-12-28 10:03:40 +0100 (mar. 28 déc. 2010) $
 */
public class ResizableDoubleArrayTest extends DoubleArrayAbstractTest {

    public ResizableDoubleArrayTest(String name) {
        super( name );
    }

    @Override
    protected void tearDown() throws Exception {
        da = null;
        ra = null;
    }

    @Override
    protected void setUp() throws Exception {
        da = new ResizableDoubleArray();
        ra = new ResizableDoubleArray();
    }

    public void testConstructors() {
        float defaultExpansionFactor = 2.0f;
        float defaultContractionCriteria = 2.5f;
        int defaultMode = ResizableDoubleArray.MULTIPLICATIVE_MODE;

        ResizableDoubleArray testDa = new ResizableDoubleArray(2);
        assertEquals(0, testDa.getNumElements());
        assertEquals(2, testDa.getInternalLength());
        assertEquals(defaultExpansionFactor, testDa.getExpansionFactor(), 0);
        assertEquals(defaultContractionCriteria, testDa.getContractionCriteria(), 0);
        assertEquals(defaultMode, testDa.getExpansionMode());
        try {
            da = new ResizableDoubleArray(-1);
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        
        testDa = new ResizableDoubleArray((double[]) null);
        assertEquals(0, testDa.getNumElements());
        
        double[] initialArray = new double[] { 0, 1, 2 };        
        testDa = new ResizableDoubleArray(initialArray);
        assertEquals(3, testDa.getNumElements());

        testDa = new ResizableDoubleArray(2, 2.0f);
        assertEquals(0, testDa.getNumElements());
        assertEquals(2, testDa.getInternalLength());
        assertEquals(defaultExpansionFactor, testDa.getExpansionFactor(), 0);
        assertEquals(defaultContractionCriteria, testDa.getContractionCriteria(), 0);
        assertEquals(defaultMode, testDa.getExpansionMode());

        try {
            da = new ResizableDoubleArray(2, 0.5f);
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }

        testDa = new ResizableDoubleArray(2, 3.0f);
        assertEquals(3.0f, testDa.getExpansionFactor(), 0);
        assertEquals(3.5f, testDa.getContractionCriteria(), 0);

        testDa = new ResizableDoubleArray(2, 2.0f, 3.0f);
        assertEquals(0, testDa.getNumElements());
        assertEquals(2, testDa.getInternalLength());
        assertEquals(defaultExpansionFactor, testDa.getExpansionFactor(), 0);
        assertEquals(3.0f, testDa.getContractionCriteria(), 0);
        assertEquals(defaultMode, testDa.getExpansionMode());

        try {
            da = new ResizableDoubleArray(2, 2.0f, 1.5f);
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }

        testDa = new ResizableDoubleArray(2, 2.0f, 3.0f,
                ResizableDoubleArray.ADDITIVE_MODE);
        assertEquals(0, testDa.getNumElements());
        assertEquals(2, testDa.getInternalLength());
        assertEquals(defaultExpansionFactor, testDa.getExpansionFactor(), 0);
        assertEquals(3.0f, testDa.getContractionCriteria(), 0);
        assertEquals(ResizableDoubleArray.ADDITIVE_MODE,
                testDa.getExpansionMode());

        try {
            da = new ResizableDoubleArray(2, 2.0f, 2.5f, -1);
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }

        // Copy constructor
        testDa = new ResizableDoubleArray(2, 2.0f, 3.0f,
                ResizableDoubleArray.ADDITIVE_MODE);
        testDa.addElement(2.0);
        testDa.addElement(3.2);
        ResizableDoubleArray copyDa = new ResizableDoubleArray(testDa);
        assertEquals(copyDa, testDa);
        assertEquals(testDa, copyDa);
    }


    public void testSetElementArbitraryExpansion() {

        // MULTIPLICATIVE_MODE
        da.addElement(2.0);
        da.addElement(4.0);
        da.addElement(6.0);
        da.setElement(1, 3.0);

        // Expand the array arbitrarily to 1000 items
        da.setElement(1000, 3.4);

        assertEquals( "The number of elements should now be 1001, it isn't",
                da.getNumElements(), 1001);

        assertEquals( "Uninitialized Elements are default value of 0.0, index 766 wasn't", 0.0,
                da.getElement( 760 ), Double.MIN_VALUE );

        assertEquals( "The 1000th index should be 3.4, it isn't", 3.4, da.getElement(1000),
                Double.MIN_VALUE );
        assertEquals( "The 0th index should be 2.0, it isn't", 2.0, da.getElement(0),
                Double.MIN_VALUE);

        // Make sure numElements and expansion work correctly for expansion boundary cases
        da.clear();
        da.addElement(2.0);
        da.addElement(4.0);
        da.addElement(6.0);
        assertEquals(4, ((ResizableDoubleArray) da).getInternalLength());
        assertEquals(3, da.getNumElements());
        da.setElement(3, 7.0);
        assertEquals(4, ((ResizableDoubleArray) da).getInternalLength());
        assertEquals(4, da.getNumElements());
        da.setElement(10, 10.0);
        assertEquals(11, ((ResizableDoubleArray) da).getInternalLength());
        assertEquals(11, da.getNumElements());
        da.setElement(9, 10.0);
        assertEquals(11, ((ResizableDoubleArray) da).getInternalLength());
        assertEquals(11, da.getNumElements());

        try {
            da.setElement(-2, 3);
            fail("Expecting ArrayIndexOutOfBoundsException for negative index");
        } catch (ArrayIndexOutOfBoundsException ex) {
            // expected
        }

        // ADDITIVE_MODE

        ResizableDoubleArray testDa = new ResizableDoubleArray(2, 2.0f, 3.0f,
                ResizableDoubleArray.ADDITIVE_MODE);
        assertEquals(2, testDa.getInternalLength());
        testDa.addElement(1d);
        testDa.addElement(1d);
        assertEquals(2, testDa.getInternalLength());
        testDa.addElement(1d);
        assertEquals(4, testDa.getInternalLength());
    }

    @Override
    public void testAdd1000() {
        super.testAdd1000();
        assertEquals("Internal Storage length should be 1024 if we started out with initial capacity of " +
                "16 and an expansion factor of 2.0",
                1024, ((ResizableDoubleArray) da).getInternalLength());
    }
    
    public void testAddElements() {
        ResizableDoubleArray testDa = new ResizableDoubleArray();
        
        // MULTIPLICATIVE_MODE
        testDa.addElements(new double[] {4, 5, 6});
        assertEquals(3, testDa.getNumElements(), 0);
        assertEquals(4, testDa.getElement(0), 0);
        assertEquals(5, testDa.getElement(1), 0);
        assertEquals(6, testDa.getElement(2), 0);
        
        testDa.addElements(new double[] {4, 5, 6});
        assertEquals(6, testDa.getNumElements());

        // ADDITIVE_MODE  (x's are occupied storage locations, 0's are open)
        testDa = new ResizableDoubleArray(2, 2.0f, 2.5f,
                ResizableDoubleArray.ADDITIVE_MODE);        
        assertEquals(2, testDa.getInternalLength());
        testDa.addElements(new double[] { 1d }); // x,0
        testDa.addElements(new double[] { 2d }); // x,x
        testDa.addElements(new double[] { 3d }); // x,x,x,0 -- expanded
        assertEquals(1d, testDa.getElement(0), 0);
        assertEquals(2d, testDa.getElement(1), 0);
        assertEquals(3d, testDa.getElement(2), 0);
        assertEquals(4, testDa.getInternalLength());  // x,x,x,0
        assertEquals(3, testDa.getNumElements());
    }

    @Override
    public void testAddElementRolling() {
        super.testAddElementRolling();

        // MULTIPLICATIVE_MODE
        da.clear();
        da.addElement(1);
        da.addElement(2);
        da.addElementRolling(3);
        assertEquals(3, da.getElement(1), 0);
        da.addElementRolling(4);
        assertEquals(3, da.getElement(0), 0);
        assertEquals(4, da.getElement(1), 0);
        da.addElement(5);
        assertEquals(5, da.getElement(2), 0);
        da.addElementRolling(6);
        assertEquals(4, da.getElement(0), 0);
        assertEquals(5, da.getElement(1), 0);
        assertEquals(6, da.getElement(2), 0);

        // ADDITIVE_MODE  (x's are occupied storage locations, 0's are open)
        ResizableDoubleArray testDa = new ResizableDoubleArray(2, 2.0f, 2.5f,
                ResizableDoubleArray.ADDITIVE_MODE);
        assertEquals(2, testDa.getInternalLength());
        testDa.addElement(1d); // x,0
        testDa.addElement(2d); // x,x
        testDa.addElement(3d); // x,x,x,0 -- expanded
        assertEquals(1d, testDa.getElement(0), 0);
        assertEquals(2d, testDa.getElement(1), 0);
        assertEquals(3d, testDa.getElement(2), 0);
        assertEquals(4, testDa.getInternalLength());  // x,x,x,0
        assertEquals(3, testDa.getNumElements());
        testDa.addElementRolling(4d);
        assertEquals(2d, testDa.getElement(0), 0);
        assertEquals(3d, testDa.getElement(1), 0);
        assertEquals(4d, testDa.getElement(2), 0);
        assertEquals(4, testDa.getInternalLength());  // 0,x,x,x
        assertEquals(3, testDa.getNumElements());
        testDa.addElementRolling(5d);   // 0,0,x,x,x,0 -- time to contract
        assertEquals(3d, testDa.getElement(0), 0);
        assertEquals(4d, testDa.getElement(1), 0);
        assertEquals(5d, testDa.getElement(2), 0);
        assertEquals(4, testDa.getInternalLength());  // contracted -- x,x,x,0
        assertEquals(3, testDa.getNumElements());
        try {
            testDa.getElement(4);
            fail("Expecting ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException ex) {
            // expected
        }
        try {
            testDa.getElement(-1);
            fail("Expecting ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException ex) {
            // expected
        }
    }

    public void testSetNumberOfElements() {
        da.addElement( 1.0 );
        da.addElement( 1.0 );
        da.addElement( 1.0 );
        da.addElement( 1.0 );
        da.addElement( 1.0 );
        da.addElement( 1.0 );
        assertEquals( "Number of elements should equal 6", da.getNumElements(), 6);

        ((ResizableDoubleArray) da).setNumElements( 3 );
        assertEquals( "Number of elements should equal 3", da.getNumElements(), 3);

        try {
            ((ResizableDoubleArray) da).setNumElements( -3 );
            fail( "Setting number of elements to negative should've thrown an exception");
        } catch( IllegalArgumentException iae ) {
        }

        ((ResizableDoubleArray) da).setNumElements(1024);
        assertEquals( "Number of elements should now be 1024", da.getNumElements(), 1024);
        assertEquals( "Element 453 should be a default double", da.getElement( 453 ), 0.0, Double.MIN_VALUE);

    }

    public void testWithInitialCapacity() {

        ResizableDoubleArray eDA2 = new ResizableDoubleArray(2);
        assertEquals("Initial number of elements should be 0", 0, eDA2.getNumElements());

        RandomData randomData = new RandomDataImpl();
        int iterations = randomData.nextInt(100, 1000);

        for( int i = 0; i < iterations; i++) {
            eDA2.addElement( i );
        }

        assertEquals("Number of elements should be equal to " + iterations, iterations, eDA2.getNumElements());

        eDA2.addElement( 2.0 );

        assertEquals("Number of elements should be equals to " + (iterations +1),
                iterations + 1 , eDA2.getNumElements() );
    }

    public void testWithInitialCapacityAndExpansionFactor() {

        ResizableDoubleArray eDA3 = new ResizableDoubleArray(3, 3.0f, 3.5f);
        assertEquals("Initial number of elements should be 0", 0, eDA3.getNumElements() );

        RandomData randomData = new RandomDataImpl();
        int iterations = randomData.nextInt(100, 3000);

        for( int i = 0; i < iterations; i++) {
            eDA3.addElement( i );
        }

        assertEquals("Number of elements should be equal to " + iterations, iterations,eDA3.getNumElements());

        eDA3.addElement( 2.0 );

        assertEquals("Number of elements should be equals to " + (iterations +1),
                iterations +1, eDA3.getNumElements() );

        assertEquals("Expansion factor should equal 3.0", 3.0f, eDA3.getExpansionFactor(), Double.MIN_VALUE);
    }

    public void testDiscard() {
        da.addElement(2.0);
        da.addElement(2.0);
        da.addElement(2.0);
        da.addElement(2.0);
        da.addElement(2.0);
        da.addElement(2.0);
        da.addElement(2.0);
        da.addElement(2.0);
        da.addElement(2.0);
        da.addElement(2.0);
        da.addElement(2.0);
        assertEquals( "Number of elements should be 11", 11, da.getNumElements());

        ((ResizableDoubleArray)da).discardFrontElements(5);
        assertEquals( "Number of elements should be 6", 6, da.getNumElements());

        da.addElement(2.0);
        da.addElement(2.0);
        da.addElement(2.0);
        da.addElement(2.0);
        assertEquals( "Number of elements should be 10", 10, da.getNumElements());

        ((ResizableDoubleArray)da).discardMostRecentElements(2);
        assertEquals( "Number of elements should be 8", 8, da.getNumElements());

        try {
            ((ResizableDoubleArray)da).discardFrontElements(-1);
            fail( "Trying to discard a negative number of element is not allowed");
        } catch( Exception e ){
        }

        try {
            ((ResizableDoubleArray)da).discardMostRecentElements(-1);
            fail( "Trying to discard a negative number of element is not allowed");
        } catch( Exception e ){
        }

        try {
            ((ResizableDoubleArray)da).discardFrontElements( 10000 );
            fail( "You can't discard more elements than the array contains");
        } catch( Exception e ){
        }

        try {
            ((ResizableDoubleArray)da).discardMostRecentElements( 10000 );
            fail( "You can't discard more elements than the array contains");
        } catch( Exception e ){
        }

    }

    public void testSubstitute() {

        da.addElement(2.0);
        da.addElement(2.0);
        da.addElement(2.0);
        da.addElement(2.0);
        da.addElement(2.0);
        da.addElement(2.0);
        da.addElement(2.0);
        da.addElement(2.0);
        da.addElement(2.0);
        da.addElement(2.0);
        da.addElement(2.0);
        assertEquals( "Number of elements should be 11", 11, da.getNumElements());

        ((ResizableDoubleArray)da).substituteMostRecentElement(24);

        assertEquals( "Number of elements should be 11", 11, da.getNumElements());

        try {
            ((ResizableDoubleArray)da).discardMostRecentElements(10);
        } catch( Exception e ){
            fail( "Trying to discard a negative number of element is not allowed");
        }

        ((ResizableDoubleArray)da).substituteMostRecentElement(24);

        assertEquals( "Number of elements should be 1", 1, da.getNumElements());

    }

    public void testMutators() {
        ((ResizableDoubleArray)da).setContractionCriteria(10f);
        assertEquals(10f, ((ResizableDoubleArray)da).getContractionCriteria(), 0);
        ((ResizableDoubleArray)da).setExpansionFactor(8f);
        assertEquals(8f, ((ResizableDoubleArray)da).getExpansionFactor(), 0);
        try {
            ((ResizableDoubleArray)da).setExpansionFactor(11f);  // greater than contractionCriteria
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        ((ResizableDoubleArray)da).setExpansionMode(
                ResizableDoubleArray.ADDITIVE_MODE);
        assertEquals(ResizableDoubleArray.ADDITIVE_MODE,
                ((ResizableDoubleArray)da).getExpansionMode());
        try {
            ((ResizableDoubleArray)da).setExpansionMode(-1);
            fail ("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    public void testEqualsAndHashCode() throws Exception {

        // Wrong type
        ResizableDoubleArray first = new ResizableDoubleArray();
        Double other = new Double(2);
        assertFalse(first.equals(other));

        // Null
        other = null;
        assertFalse(first.equals(other));

        // Reflexive
        assertTrue(first.equals(first));

        // Argumentless constructor
        ResizableDoubleArray second = new ResizableDoubleArray();
        verifyEquality(first, second);

        // Equals iff same data, same properties
        ResizableDoubleArray third = new ResizableDoubleArray(3, 2.0f, 2.0f);
        verifyInequality(third, first);
        ResizableDoubleArray fourth = new ResizableDoubleArray(3, 2.0f, 2.0f);
        ResizableDoubleArray fifth = new ResizableDoubleArray(2, 2.0f, 2.0f);
        verifyEquality(third, fourth);
        verifyInequality(third, fifth);
        third.addElement(4.1);
        third.addElement(4.2);
        third.addElement(4.3);
        fourth.addElement(4.1);
        fourth.addElement(4.2);
        fourth.addElement(4.3);
        verifyEquality(third, fourth);

        // expand
        fourth.addElement(4.4);
        verifyInequality(third, fourth);
        third.addElement(4.4);
        verifyEquality(third, fourth);
        fourth.addElement(4.4);
        verifyInequality(third, fourth);
        third.addElement(4.4);
        verifyEquality(third, fourth);
        fourth.addElementRolling(4.5);
        third.addElementRolling(4.5);
        verifyEquality(third, fourth);

        // discard
        third.discardFrontElements(1);
        verifyInequality(third, fourth);
        fourth.discardFrontElements(1);
        verifyEquality(third, fourth);

        // discard recent
        third.discardMostRecentElements(2);
        fourth.discardMostRecentElements(2);
        verifyEquality(third, fourth);

        // wrong order
        third.addElement(18);
        fourth.addElement(17);
        third.addElement(17);
        fourth.addElement(18);
        verifyInequality(third, fourth);

        // copy
        ResizableDoubleArray.copy(fourth, fifth);
        verifyEquality(fourth, fifth);

        // Copy constructor
        verifyEquality(fourth, new ResizableDoubleArray(fourth));

        // Instance copy
        verifyEquality(fourth, fourth.copy());

    }

    private void verifyEquality(ResizableDoubleArray a, ResizableDoubleArray b) {
        assertTrue(b.equals(a));
        assertTrue(a.equals(b));
        assertEquals(a.hashCode(), b.hashCode());
    }

    private void verifyInequality(ResizableDoubleArray a, ResizableDoubleArray b) {
        assertFalse(b.equals(a));
        assertFalse(a.equals(b));
        assertFalse(a.hashCode() == b.hashCode());
    }

}
