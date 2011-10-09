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

package org.apache.commons.math.ode.sampling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.exception.util.Localizable;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.ode.sampling.AbstractStepInterpolator;
import org.apache.commons.math.ode.sampling.DummyStepInterpolator;
import org.apache.commons.math.util.FastMath;
import org.junit.Test;

public class DummyStepInterpolatorTest {

  @Test
  public void testNoReset() throws DerivativeException {

    double[]   y    =   { 0.0, 1.0, -2.0 };
    DummyStepInterpolator interpolator = new DummyStepInterpolator(y, new double[y.length], true);
    interpolator.storeTime(0);
    interpolator.shift();
    interpolator.storeTime(1);

    double[] result = interpolator.getInterpolatedState();
    for (int i = 0; i < result.length; ++i) {
      assertTrue(FastMath.abs(result[i] - y[i]) < 1.0e-10);
    }

  }

  @Test
  public void testFixedState()
    throws DerivativeException {

    double[]   y    =   { 1.0, 3.0, -4.0 };
    DummyStepInterpolator interpolator = new DummyStepInterpolator(y, new double[y.length], true);
    interpolator.storeTime(0);
    interpolator.shift();
    interpolator.storeTime(1);

    interpolator.setInterpolatedTime(0.1);
    double[] result = interpolator.getInterpolatedState();
    for (int i = 0; i < result.length; ++i) {
        assertTrue(FastMath.abs(result[i] - y[i]) < 1.0e-10);
    }

    interpolator.setInterpolatedTime(0.5);
    result = interpolator.getInterpolatedState();
    for (int i = 0; i < result.length; ++i) {
        assertTrue(FastMath.abs(result[i] - y[i]) < 1.0e-10);
    }

  }

  @Test
  public void testSerialization()
  throws DerivativeException, IOException, ClassNotFoundException {

    double[]   y    =   { 0.0, 1.0, -2.0 };
    DummyStepInterpolator interpolator = new DummyStepInterpolator(y, new double[y.length], true);
    interpolator.storeTime(0);
    interpolator.shift();
    interpolator.storeTime(1);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream    oos = new ObjectOutputStream(bos);
    oos.writeObject(interpolator);

    assertTrue(bos.size () > 200);
    assertTrue(bos.size () < 300);

    ByteArrayInputStream  bis = new ByteArrayInputStream(bos.toByteArray());
    ObjectInputStream     ois = new ObjectInputStream(bis);
    DummyStepInterpolator dsi = (DummyStepInterpolator) ois.readObject();

    dsi.setInterpolatedTime(0.5);
    double[] result = dsi.getInterpolatedState();
    for (int i = 0; i < result.length; ++i) {
        assertTrue(FastMath.abs(result[i] - y[i]) < 1.0e-10);
    }

  }

  @Test
  public void testImpossibleSerialization()
  throws IOException {

    double[] y = { 0.0, 1.0, -2.0 };
    AbstractStepInterpolator interpolator = new BadStepInterpolator(y, true);
    interpolator.storeTime(0);
    interpolator.shift();
    interpolator.storeTime(1);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream    oos = new ObjectOutputStream(bos);
    try {
        oos.writeObject(interpolator);
        fail("an exception should have been thrown");
    } catch (IOException ioe) {
        // expected behavior
        assertEquals(0, ioe.getMessage().length());
    }

  }

  private static class BadStepInterpolator extends DummyStepInterpolator {
      @SuppressWarnings("unused")
      public BadStepInterpolator() {
      }
      public BadStepInterpolator(double[] y, boolean forward) {
          super(y, new double[y.length], forward);
      }
      @Override
      protected void doFinalize() throws DerivativeException {
          throw new DerivativeException((Localizable) null, LocalizedFormats.SIMPLE_MESSAGE, "");
      }
  }

}
