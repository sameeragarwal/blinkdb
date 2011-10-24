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

package org.apache.commons.math.estimation;

import org.apache.commons.math.estimation.EstimatedParameter;
import org.apache.commons.math.estimation.WeightedMeasurement;
import org.apache.commons.math.util.FastMath;

import junit.framework.*;

@Deprecated
public class WeightedMeasurementTest
  extends TestCase {

  public WeightedMeasurementTest(String name) {
    super(name);
    p1 = null;
    p2 = null;
  }

  public void testConstruction() {
    WeightedMeasurement m = new MyMeasurement(3.0, theoretical() + 0.1, this);
    checkValue(m.getWeight(), 3.0);
    checkValue(m.getMeasuredValue(), theoretical() + 0.1);
  }

  public void testIgnored() {
    WeightedMeasurement m = new MyMeasurement(3.0, theoretical() + 0.1, this);
    assertTrue(!m.isIgnored());
    m.setIgnored(true);
    assertTrue(m.isIgnored());
    m.setIgnored(false);
    assertTrue(!m.isIgnored());
  }

  public void testTheory() {
    WeightedMeasurement m = new MyMeasurement(3.0, theoretical() + 0.1, this);
    checkValue(m.getTheoreticalValue(), theoretical());
    checkValue(m.getResidual(), 0.1);

    double oldP1 = p1.getEstimate();
    p1.setEstimate(oldP1 + m.getResidual() / m.getPartial(p1));
    checkValue(m.getResidual(), 0.0);
    p1.setEstimate(oldP1);
    checkValue(m.getResidual(), 0.1);

    double oldP2 = p2.getEstimate();
    p2.setEstimate(oldP2 + m.getResidual() / m.getPartial(p2));
    checkValue(m.getResidual(), 0.0);
    p2.setEstimate(oldP2);
    checkValue(m.getResidual(), 0.1);

  }

  @Override
  public void setUp() {
    p1 = new EstimatedParameter("p1", 1.0);
    p2 = new EstimatedParameter("p2", 2.0);
  }

  @Override
  public void tearDown() {
    p1 = null;
    p2 = null;
  }

  private void checkValue(double value, double expected) {
   assertTrue(FastMath.abs(value - expected) < 1.0e-10);
  }

  private double theoretical() {
   return 3 * p1.getEstimate() - p2.getEstimate();
  }

  private double partial(EstimatedParameter p) {
    if (p == p1) {
      return 3.0;
    } else if (p == p2) {
      return -1.0;
    } else {
      return 0.0;
    }
  }

  private static class MyMeasurement
    extends WeightedMeasurement {

    public MyMeasurement(double weight, double measuredValue,
                         WeightedMeasurementTest testInstance) {
      super(weight, measuredValue);
      this.testInstance = testInstance;
    }

    @Override
    public double getTheoreticalValue() {
      return testInstance.theoretical();
    }

    @Override
    public double getPartial(EstimatedParameter p) {
      return testInstance.partial(p);
    }

    private transient WeightedMeasurementTest testInstance;

    private static final long serialVersionUID = -246712922500792332L;

  }

  private EstimatedParameter p1;
  private EstimatedParameter p2;

}
