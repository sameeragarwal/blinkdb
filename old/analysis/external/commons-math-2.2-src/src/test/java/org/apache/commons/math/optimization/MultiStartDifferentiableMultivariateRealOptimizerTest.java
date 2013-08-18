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

package org.apache.commons.math.optimization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.apache.commons.math.analysis.DifferentiableMultivariateRealFunction;
import org.apache.commons.math.analysis.MultivariateRealFunction;
import org.apache.commons.math.analysis.MultivariateVectorialFunction;
import org.apache.commons.math.analysis.solvers.BrentSolver;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.optimization.general.ConjugateGradientFormula;
import org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizer;
import org.apache.commons.math.random.GaussianRandomGenerator;
import org.apache.commons.math.random.JDKRandomGenerator;
import org.apache.commons.math.random.RandomVectorGenerator;
import org.apache.commons.math.random.UncorrelatedRandomVectorGenerator;
import org.junit.Test;

public class MultiStartDifferentiableMultivariateRealOptimizerTest {

    @Test
    public void testCircleFitting() throws Exception {
        Circle circle = new Circle();
        circle.addPoint( 30.0,  68.0);
        circle.addPoint( 50.0,  -6.0);
        circle.addPoint(110.0, -20.0);
        circle.addPoint( 35.0,  15.0);
        circle.addPoint( 45.0,  97.0);
        NonLinearConjugateGradientOptimizer underlying =
            new NonLinearConjugateGradientOptimizer(ConjugateGradientFormula.POLAK_RIBIERE);
        JDKRandomGenerator g = new JDKRandomGenerator();
        g.setSeed(753289573253l);
        RandomVectorGenerator generator =
            new UncorrelatedRandomVectorGenerator(new double[] { 50.0, 50.0 }, new double[] { 10.0, 10.0 },
                                                  new GaussianRandomGenerator(g));
        MultiStartDifferentiableMultivariateRealOptimizer optimizer =
            new MultiStartDifferentiableMultivariateRealOptimizer(underlying, 10, generator);
        optimizer.setMaxIterations(100);
        assertEquals(100, optimizer.getMaxIterations());
        optimizer.setMaxEvaluations(100);
        assertEquals(100, optimizer.getMaxEvaluations());
        optimizer.setConvergenceChecker(new SimpleScalarValueChecker(1.0e-10, 1.0e-10));
        BrentSolver solver = new BrentSolver();
        solver.setAbsoluteAccuracy(1.0e-13);
        solver.setRelativeAccuracy(1.0e-15);
        RealPointValuePair optimum =
            optimizer.optimize(circle, GoalType.MINIMIZE, new double[] { 98.680, 47.345 });
        RealPointValuePair[] optima = optimizer.getOptima();
        for (RealPointValuePair o : optima) {
            Point2D.Double center = new Point2D.Double(o.getPointRef()[0], o.getPointRef()[1]);
            assertEquals(69.960161753, circle.getRadius(center), 1.0e-8);
            assertEquals(96.075902096, center.x, 1.0e-8);
            assertEquals(48.135167894, center.y, 1.0e-8);
        }
        assertTrue(optimizer.getGradientEvaluations() > 650);
        assertTrue(optimizer.getGradientEvaluations() < 700);
        assertTrue(optimizer.getEvaluations() > 70);
        assertTrue(optimizer.getEvaluations() < 90);
        assertTrue(optimizer.getIterations() > 70);
        assertTrue(optimizer.getIterations() < 90);
        assertEquals(3.1267527, optimum.getValue(), 1.0e-8);
    }

    private static class Circle implements DifferentiableMultivariateRealFunction {

        private ArrayList<Point2D.Double> points;

        public Circle() {
            points  = new ArrayList<Point2D.Double>();
        }

        public void addPoint(double px, double py) {
            points.add(new Point2D.Double(px, py));
        }

        public double getRadius(Point2D.Double center) {
            double r = 0;
            for (Point2D.Double point : points) {
                r += point.distance(center);
            }
            return r / points.size();
        }

        private double[] gradient(double[] point) {

            // optimal radius
            Point2D.Double center = new Point2D.Double(point[0], point[1]);
            double radius = getRadius(center);

            // gradient of the sum of squared residuals
            double dJdX = 0;
            double dJdY = 0;
            for (Point2D.Double pk : points) {
                double dk = pk.distance(center);
                dJdX += (center.x - pk.x) * (dk - radius) / dk;
                dJdY += (center.y - pk.y) * (dk - radius) / dk;
            }
            dJdX *= 2;
            dJdY *= 2;

            return new double[] { dJdX, dJdY };

        }

        public double value(double[] variables)
        throws IllegalArgumentException, FunctionEvaluationException {

            Point2D.Double center = new Point2D.Double(variables[0], variables[1]);
            double radius = getRadius(center);

            double sum = 0;
            for (Point2D.Double point : points) {
                double di = point.distance(center) - radius;
                sum += di * di;
            }

            return sum;

        }

        public MultivariateVectorialFunction gradient() {
            return new MultivariateVectorialFunction() {
                public double[] value(double[] point) {
                    return gradient(point);
                }
            };
        }

        public MultivariateRealFunction partialDerivative(final int k) {
            return new MultivariateRealFunction() {
                public double value(double[] point) {
                    return gradient(point)[k];
                }
            };
        }

    }

}
