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

package org.apache.commons.math.ode.nonstiff;

import org.apache.commons.math.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math.ode.events.EventHandler;


public class StepProblem
  implements FirstOrderDifferentialEquations, EventHandler {

  public StepProblem(double rateBefore, double rateAfter,
                     double switchTime) {
    this.rateAfter  = rateAfter;
    this.switchTime = switchTime;
    setRate(rateBefore);
  }

  public void computeDerivatives(double t, double[] y, double[] yDot) {
    yDot[0] = rate;
  }

  public int getDimension() {
    return 1;
  }

  public void setRate(double rate) {
    this.rate = rate;
  }

  public int eventOccurred(double t, double[] y, boolean increasing) {
    setRate(rateAfter);
    return RESET_DERIVATIVES;
  }

  public double g(double t, double[] y) {
    return t - switchTime;
  }

  public void resetState(double t, double[] y) {
  }

  private double rate;
  private double rateAfter;
  private double switchTime;

  private static final long serialVersionUID = 7590601995477504318L;

}
