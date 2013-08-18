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

package org.apache.commons.math.ode.jacobians;

import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.FirstOrderIntegrator;
import org.apache.commons.math.ode.IntegratorException;
import org.apache.commons.math.ode.nonstiff.DormandPrince54Integrator;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;
import org.apache.commons.math.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

@Deprecated
public class FirstOrderIntegratorWithJacobiansTest {

    @Test
    public void testLowAccuracyExternalDifferentiation()
        throws IntegratorException, DerivativeException {
        // this test does not really test FirstOrderIntegratorWithJacobians,
        // it only shows that WITHOUT this class, attempting to recover
        // the jacobians from external differentiation on simple integration
        // results with low accuracy gives very poor results. In fact,
        // the curves dy/dp = g(b) when b varies from 2.88 to 3.08 are
        // essentially noise.
        // This test is taken from Hairer, Norsett and Wanner book
        // Solving Ordinary Differential Equations I (Nonstiff problems),
        // the curves dy/dp = g(b) are in figure 6.5
        FirstOrderIntegrator integ =
            new DormandPrince54Integrator(1.0e-8, 100.0, new double[] { 1.0e-4, 1.0e-4 }, new double[] { 1.0e-4, 1.0e-4 });
        double hP = 1.0e-12;
        SummaryStatistics residualsP0 = new SummaryStatistics();
        SummaryStatistics residualsP1 = new SummaryStatistics();
        for (double b = 2.88; b < 3.08; b += 0.001) {
            Brusselator brusselator = new Brusselator(b);
            double[] y = { 1.3, b };
            integ.integrate(brusselator, 0, y, 20.0, y);
            double[] yP = { 1.3, b + hP };
            brusselator.setParameter(0, b + hP);
            integ.integrate(brusselator, 0, yP, 20.0, yP);
            residualsP0.addValue((yP[0] - y[0]) / hP - brusselator.dYdP0());
            residualsP1.addValue((yP[1] - y[1]) / hP - brusselator.dYdP1());
        }
        Assert.assertTrue((residualsP0.getMax() - residualsP0.getMin()) > 600);
        Assert.assertTrue(residualsP0.getStandardDeviation() > 30);
        Assert.assertTrue((residualsP1.getMax() - residualsP1.getMin()) > 800);
        Assert.assertTrue(residualsP1.getStandardDeviation() > 50);
    }

    @Test
    public void testHighAccuracyExternalDifferentiation()
        throws IntegratorException, DerivativeException {
        FirstOrderIntegrator integ =
            new DormandPrince54Integrator(1.0e-8, 100.0, new double[] { 1.0e-10, 1.0e-10 }, new double[] { 1.0e-10, 1.0e-10 });
        double hP = 1.0e-12;
        SummaryStatistics residualsP0 = new SummaryStatistics();
        SummaryStatistics residualsP1 = new SummaryStatistics();
        for (double b = 2.88; b < 3.08; b += 0.001) {
            Brusselator brusselator = new Brusselator(b);
            double[] y = { 1.3, b };
            integ.integrate(brusselator, 0, y, 20.0, y);
            double[] yP = { 1.3, b + hP };
            brusselator.setParameter(0, b + hP);
            integ.integrate(brusselator, 0, yP, 20.0, yP);
            residualsP0.addValue((yP[0] - y[0]) / hP - brusselator.dYdP0());
            residualsP1.addValue((yP[1] - y[1]) / hP - brusselator.dYdP1());
        }
        Assert.assertTrue((residualsP0.getMax() - residualsP0.getMin()) > 0.02);
        Assert.assertTrue((residualsP0.getMax() - residualsP0.getMin()) < 0.03);
        Assert.assertTrue(residualsP0.getStandardDeviation() > 0.003);
        Assert.assertTrue(residualsP0.getStandardDeviation() < 0.004);
        Assert.assertTrue((residualsP1.getMax() - residualsP1.getMin()) > 0.04);
        Assert.assertTrue((residualsP1.getMax() - residualsP1.getMin()) < 0.05);
        Assert.assertTrue(residualsP1.getStandardDeviation() > 0.007);
        Assert.assertTrue(residualsP1.getStandardDeviation() < 0.008);
    }

    @Test
    public void testInternalDifferentiation()
        throws IntegratorException, DerivativeException {
        FirstOrderIntegrator integ =
            new DormandPrince54Integrator(1.0e-8, 100.0, new double[] { 1.0e-4, 1.0e-4 }, new double[] { 1.0e-4, 1.0e-4 });
        double hP = 1.0e-12;
        SummaryStatistics residualsP0 = new SummaryStatistics();
        SummaryStatistics residualsP1 = new SummaryStatistics();
        for (double b = 2.88; b < 3.08; b += 0.001) {
            Brusselator brusselator = new Brusselator(b);
            brusselator.setParameter(0, b);
            double[] z = { 1.3, b };
            double[][] dZdZ0 = new double[2][2];
            double[][] dZdP  = new double[2][1];
            double hY = 1.0e-12;
            FirstOrderIntegratorWithJacobians extInt =
                new FirstOrderIntegratorWithJacobians(integ, brusselator, new double[] { b },
                                                      new double[] { hY, hY }, new double[] { hP });
            extInt.setMaxEvaluations(5000);
            extInt.integrate(0, z, new double[][] { { 0.0 }, { 1.0 } }, 20.0, z, dZdZ0, dZdP);
            Assert.assertEquals(5000, extInt.getMaxEvaluations());
            Assert.assertTrue(extInt.getEvaluations() > 1400);
            Assert.assertTrue(extInt.getEvaluations() < 2000);
            Assert.assertEquals(4 * integ.getEvaluations(), extInt.getEvaluations());
            residualsP0.addValue(dZdP[0][0] - brusselator.dYdP0());
            residualsP1.addValue(dZdP[1][0] - brusselator.dYdP1());
        }
        Assert.assertTrue((residualsP0.getMax() - residualsP0.getMin()) < 0.02);
        Assert.assertTrue(residualsP0.getStandardDeviation() < 0.003);
        Assert.assertTrue((residualsP1.getMax() - residualsP1.getMin()) < 0.05);
        Assert.assertTrue(residualsP1.getStandardDeviation() < 0.01);
    }

    @Test
    public void testAnalyticalDifferentiation()
        throws IntegratorException, DerivativeException {
        FirstOrderIntegrator integ =
            new DormandPrince54Integrator(1.0e-8, 100.0, new double[] { 1.0e-4, 1.0e-4 }, new double[] { 1.0e-4, 1.0e-4 });
        SummaryStatistics residualsP0 = new SummaryStatistics();
        SummaryStatistics residualsP1 = new SummaryStatistics();
        for (double b = 2.88; b < 3.08; b += 0.001) {
            Brusselator brusselator = new Brusselator(b);
            brusselator.setParameter(0, b);
            double[] z = { 1.3, b };
            double[][] dZdZ0 = new double[2][2];
            double[][] dZdP  = new double[2][1];
            FirstOrderIntegratorWithJacobians extInt =
                new FirstOrderIntegratorWithJacobians(integ, brusselator);
            extInt.setMaxEvaluations(5000);
            extInt.integrate(0, z, new double[][] { { 0.0 }, { 1.0 } }, 20.0, z, dZdZ0, dZdP);
            Assert.assertEquals(5000, extInt.getMaxEvaluations());
            Assert.assertTrue(extInt.getEvaluations() > 350);
            Assert.assertTrue(extInt.getEvaluations() < 510);
            Assert.assertEquals(integ.getEvaluations(), extInt.getEvaluations());
            residualsP0.addValue(dZdP[0][0] - brusselator.dYdP0());
            residualsP1.addValue(dZdP[1][0] - brusselator.dYdP1());
        }
        Assert.assertTrue((residualsP0.getMax() - residualsP0.getMin()) < 0.014);
        Assert.assertTrue(residualsP0.getStandardDeviation() < 0.003);
        Assert.assertTrue((residualsP1.getMax() - residualsP1.getMin()) < 0.05);
        Assert.assertTrue(residualsP1.getStandardDeviation() < 0.01);
    }

    @Test
    public void testFinalResult() throws IntegratorException, DerivativeException {
        FirstOrderIntegrator integ =
            new DormandPrince54Integrator(1.0e-8, 100.0, new double[] { 1.0e-10, 1.0e-10 }, new double[] { 1.0e-10, 1.0e-10 });
        double[] y = new double[] { 0.0, 1.0 };
        Circle circle = new Circle(y, 1.0, 1.0, 0.1);
        double[][] dydy0 = new double[2][2];
        double[][] dydp  = new double[2][3];
        double t = 18 * FastMath.PI;
        FirstOrderIntegratorWithJacobians extInt =
            new FirstOrderIntegratorWithJacobians(integ, circle);
        extInt.integrate(0, y, circle.exactDyDp(0), t, y, dydy0, dydp);
        for (int i = 0; i < y.length; ++i) {
            Assert.assertEquals(circle.exactY(t)[i], y[i], 1.0e-9);
        }
        for (int i = 0; i < dydy0.length; ++i) {
            for (int j = 0; j < dydy0[i].length; ++j) {
                Assert.assertEquals(circle.exactDyDy0(t)[i][j], dydy0[i][j], 1.0e-9);
            }
        }
        for (int i = 0; i < dydp.length; ++i) {
            for (int j = 0; j < dydp[i].length; ++j) {
                Assert.assertEquals(circle.exactDyDp(t)[i][j], dydp[i][j], 1.0e-7);
            }
        }
    }

    @Test
    public void testStepHandlerResult() throws IntegratorException, DerivativeException {
        FirstOrderIntegrator integ =
            new DormandPrince54Integrator(1.0e-8, 100.0, new double[] { 1.0e-10, 1.0e-10 }, new double[] { 1.0e-10, 1.0e-10 });
        double[] y = new double[] { 0.0, 1.0 };
        final Circle circle = new Circle(y, 1.0, 1.0, 0.1);
        double[][] dydy0 = new double[2][2];
        double[][] dydp  = new double[2][3];
        double t = 18 * FastMath.PI;
        final FirstOrderIntegratorWithJacobians extInt =
            new FirstOrderIntegratorWithJacobians(integ, circle);
        extInt.addStepHandler(new StepHandlerWithJacobians() {

            public void reset() {
            }

            public boolean requiresDenseOutput() {
                return false;
            }

            public void handleStep(StepInterpolatorWithJacobians interpolator, boolean isLast)
                throws DerivativeException {
                double     t     = interpolator.getCurrentTime();
                double[]   y     = interpolator.getInterpolatedY();
                double[][] dydy0 = interpolator.getInterpolatedDyDy0();
                double[][] dydp  = interpolator.getInterpolatedDyDp();
                Assert.assertEquals(interpolator.getPreviousTime(), extInt.getCurrentStepStart(), 1.0e-10);
                Assert.assertTrue(extInt.getCurrentSignedStepsize() < 0.5);
                for (int i = 0; i < y.length; ++i) {
                    Assert.assertEquals(circle.exactY(t)[i], y[i], 1.0e-9);
                }
                for (int i = 0; i < dydy0.length; ++i) {
                    for (int j = 0; j < dydy0[i].length; ++j) {
                        Assert.assertEquals(circle.exactDyDy0(t)[i][j], dydy0[i][j], 1.0e-9);
                    }
                }
                for (int i = 0; i < dydp.length; ++i) {
                    for (int j = 0; j < dydp[i].length; ++j) {
                        Assert.assertEquals(circle.exactDyDp(t)[i][j], dydp[i][j], 3.0e-8);
                    }
                }

                double[]   yDot     = interpolator.getInterpolatedYDot();
                double[][] dydy0Dot = interpolator.getInterpolatedDyDy0Dot();
                double[][] dydpDot  = interpolator.getInterpolatedDyDpDot();

                for (int i = 0; i < yDot.length; ++i) {
                    Assert.assertEquals(circle.exactYDot(t)[i], yDot[i], 1.0e-10);
                }
                for (int i = 0; i < dydy0Dot.length; ++i) {
                    for (int j = 0; j < dydy0Dot[i].length; ++j) {
                        Assert.assertEquals(circle.exactDyDy0Dot(t)[i][j], dydy0Dot[i][j], 1.0e-10);
                    }
                }
                for (int i = 0; i < dydpDot.length; ++i) {
                    for (int j = 0; j < dydpDot[i].length; ++j) {
                        Assert.assertEquals(circle.exactDyDpDot(t)[i][j], dydpDot[i][j], 3.0e-9);
                    }
                }
            }
        });
        extInt.integrate(0, y, circle.exactDyDp(0), t, y, dydy0, dydp);
    }

    @Test
    public void testEventHandler() throws IntegratorException, DerivativeException {
        FirstOrderIntegrator integ =
            new DormandPrince54Integrator(1.0e-8, 100.0, new double[] { 1.0e-10, 1.0e-10 }, new double[] { 1.0e-10, 1.0e-10 });
        double[] y = new double[] { 0.0, 1.0 };
        final Circle circle = new Circle(y, 1.0, 1.0, 0.1);
        double[][] dydy0 = new double[2][2];
        double[][] dydp  = new double[2][3];
        double t = 18 * FastMath.PI;
        final FirstOrderIntegratorWithJacobians extInt =
            new FirstOrderIntegratorWithJacobians(integ, circle);
        extInt.addEventHandler(new EventHandlerWithJacobians() {

            public int eventOccurred(double t, double[] y, double[][] dydy0,
                                     double[][] dydp, boolean increasing) {
                Assert.assertEquals(0.1, y[1], 1.0e-11);
                Assert.assertTrue(!increasing);
                return STOP;
            }

            public double g(double t, double[] y, double[][] dydy0,
                            double[][] dydp) {
                return y[1] - 0.1;
            }

            public void resetState(double t, double[] y, double[][] dydy0,
                                   double[][] dydp) {
            }
        }, 10.0, 1.0e-10, 1000);
        double stopTime = extInt.integrate(0, y, circle.exactDyDp(0), t, y, dydy0, dydp);
        Assert.assertTrue(stopTime < 5.0 * FastMath.PI);
    }

    private static class Brusselator implements ParameterizedODE, ODEWithJacobians {

        private double b;

        public Brusselator(double b) {
            this.b = b;
        }

        public int getDimension() {
            return 2;
        }

        public void setParameter(int i, double p) {
            b = p;
        }

        public int getParametersDimension() {
            return 1;
        }

        public void computeDerivatives(double t, double[] y, double[] yDot) {
            double prod = y[0] * y[0] * y[1];
            yDot[0] = 1 + prod - (b + 1) * y[0];
            yDot[1] = b * y[0] - prod;
        }

        public void computeJacobians(double t, double[] y, double[] yDot, double[][] dFdY, double[][] dFdP) {
            double p = 2 * y[0] * y[1];
            double y02 = y[0] * y[0];
            dFdY[0][0] = p - (1 + b);
            dFdY[0][1] = y02;
            dFdY[1][0] = b - p;
            dFdY[1][1] = -y02;
            dFdP[0][0] = -y[0];
            dFdP[1][0] = y[0];
        }

        public double dYdP0() {
            return -1088.232716447743 + (1050.775747149553 + (-339.012934631828 + 36.52917025056327 * b) * b) * b;
        }

        public double dYdP1() {
            return 1502.824469929139 + (-1438.6974831849952 + (460.959476642384 - 49.43847385647082 * b) * b) * b;
        }

    }

    /** ODE representing a point moving on a circle with provided center and angular rate. */
    private static class Circle implements ODEWithJacobians {

        private final double[] y0;
        private double cx;
        private double cy;
        private double omega;

        public Circle(double[] y0, double cx, double cy, double omega) {
            this.y0    = y0.clone();
            this.cx    = cx;
            this.cy    = cy;
            this.omega = omega;
        }

        public int getDimension() {
            return 2;
        }

        public int getParametersDimension() {
            return 3;
        }

        public void computeDerivatives(double t, double[] y, double[] yDot) {
            yDot[0] = omega * (cy - y[1]);
            yDot[1] = omega * (y[0] - cx);
        }

        public void computeJacobians(double t, double[] y, double[] yDot, double[][] dFdY, double[][] dFdP) {

            dFdY[0][0] = 0;
            dFdY[0][1] = -omega;
            dFdY[1][0] = omega;
            dFdY[1][1] = 0;

            dFdP[0][0] = 0;
            dFdP[0][1] = omega;
            dFdP[0][2] = cy - y[1];
            dFdP[1][0] = -omega;
            dFdP[1][1] = 0;
            dFdP[1][2] = y[0] - cx;

        }

        public double[] exactY(double t) {
            double cos = FastMath.cos(omega * t);
            double sin = FastMath.sin(omega * t);
            double dx0 = y0[0] - cx;
            double dy0 = y0[1] - cy;
            return new double[] {
                cx + cos * dx0 - sin * dy0,
                cy + sin * dx0 + cos * dy0
            };
        }

        public double[][] exactDyDy0(double t) {
            double cos = FastMath.cos(omega * t);
            double sin = FastMath.sin(omega * t);
            return new double[][] {
                { cos, -sin },
                { sin,  cos }
            };
        }

        public double[][] exactDyDp(double t) {
            double cos = FastMath.cos(omega * t);
            double sin = FastMath.sin(omega * t);
            double dx0 = y0[0] - cx;
            double dy0 = y0[1] - cy;
            return new double[][] {
                { 1 - cos, sin,    -t * (sin * dx0 + cos * dy0) },
                { -sin,    1 - cos, t * (cos * dx0 - sin * dy0) }
            };
        }

        public double[] exactYDot(double t) {
            double oCos = omega * FastMath.cos(omega * t);
            double oSin = omega * FastMath.sin(omega * t);
            double dx0 = y0[0] - cx;
            double dy0 = y0[1] - cy;
            return new double[] {
                -oSin * dx0 - oCos * dy0,
                 oCos * dx0 - oSin * dy0
            };
        }

        public double[][] exactDyDy0Dot(double t) {
            double oCos = omega * FastMath.cos(omega * t);
            double oSin = omega * FastMath.sin(omega * t);
            return new double[][] {
                { -oSin, -oCos },
                {  oCos, -oSin }
            };
        }

        public double[][] exactDyDpDot(double t) {
            double cos  = FastMath.cos(omega * t);
            double sin  = FastMath.sin(omega * t);
            double oCos = omega * cos;
            double oSin = omega * sin;
            double dx0  = y0[0] - cx;
            double dy0  = y0[1] - cy;
            return new double[][] {
                {  oSin, oCos, -sin * dx0 - cos * dy0 - t * ( oCos * dx0 - oSin * dy0) },
                { -oCos, oSin,  cos * dx0 - sin * dy0 + t * (-oSin * dx0 - oCos * dy0) }
            };
        }

    }

}
