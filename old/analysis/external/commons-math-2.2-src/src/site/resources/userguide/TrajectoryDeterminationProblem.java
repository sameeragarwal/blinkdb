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

import org.apache.commons.math.optimization.general.EstimationException;
import org.apache.commons.math.optimization.general.EstimatedParameter;
import org.apache.commons.math.optimization.general.EstimationProblem;
import org.apache.commons.math.optimization.general.LevenbergMarquardtEstimator;
import org.apache.commons.math.optimization.general.SimpleEstimationProblem;
import org.apache.commons.math.optimization.general.WeightedMeasurement;

public class TrajectoryDeterminationProblem extends SimpleEstimationProblem {

    public static void main(String[] args) {
        try {
            TrajectoryDeterminationProblem problem =
              new TrajectoryDeterminationProblem(0.0, 100.0, 800.0, 1.0, 0.0);

            double[][] distances = {
                    {   0.0, 806.5849 }, {  20.0, 796.8148 }, {  40.0, 791.0833 }, {  60.0, 789.6712 },
                    {  80.0, 793.1334 }, { 100.0, 797.7248 }, { 120.0, 803.2785 }, { 140.0, 813.4939 },
                    { 160.0, 826.9295 }, { 180.0, 844.0640 }, { 200.0, 863.3829 }, { 220.0, 883.3143 },
                    { 240.0, 908.6867 }, { 260.0, 934.8561 }, { 280.0, 964.0730 }, { 300.0, 992.1033 },
                    { 320.0, 1023.998 }, { 340.0, 1057.439 }, { 360.0, 1091.912 }, { 380.0, 1125.968 },
                    { 400.0, 1162.789 }, { 420.0, 1201.517 }, { 440.0, 1239.176 }, { 460.0, 1279.347 } };
            for (int i = 0; i < distances.length; ++i) {
                problem.addDistanceMeasurement(1.0,  distances[i][0], distances[i][1]);
            };

            double[][] angles = {
                    { 10.0, 1.415423 }, { 30.0, 1.352643 }, { 50.0, 1.289290 }, { 70.0, 1.225249 },
                    { 90.0, 1.161203 }, {110.0, 1.098538 }, {130.0, 1.036263 }, {150.0, 0.976052 },
                    {170.0, 0.917921 }, {190.0, 0.861830 }, {210.0, 0.808237 }, {230.0, 0.757043 },
                    {250.0, 0.708650 }, {270.0, 0.662949 }, {290.0, 0.619903 }, {310.0, 0.579160 },
                    {330.0, 0.541033 }, {350.0, 0.505590 }, {370.0, 0.471746 }, {390.0, 0.440155 },
                    {410.0, 0.410522 }, {430.0, 0.382701 }, {450.0, 0.356957 }, {470.0, 0.332400 } };
            for (int i = 0; i < angles.length; ++i) {
                problem.addAngularMeasurement(3.0e7, angles[i][0], angles[i][1]);
            };

            LevenbergMarquardtEstimator estimator = new LevenbergMarquardtEstimator();
            estimator.estimate(problem);
            System.out.println("initial position: " + problem.getX0() + " " + problem.getY0());
            System.out.println("velocity: " + problem.getVx0() + " " + problem.getVy0());

        } catch (EstimationException ee) {
          System.err.println(ee.getMessage());
        }
    }

    public TrajectoryDeterminationProblem(double t0,
                                          double  x0Guess, double  y0Guess,
                                          double vx0Guess, double vy0Guess) {
        this.t0 = t0;
         x0 = new EstimatedParameter( "x0",  x0Guess);
         y0 = new EstimatedParameter( "y0",  y0Guess);
        vx0 = new EstimatedParameter("vx0", vx0Guess);
        vy0 = new EstimatedParameter("vy0", vy0Guess);

        // inform the base class about the parameters
        addParameter(x0);
        addParameter(y0);
        addParameter(vx0);
        addParameter(vy0);

    }

    public double getX0() {
        return x0.getEstimate();
    }

    public double getY0() {
        return y0.getEstimate();
    }

    public double getVx0() {
        return vx0.getEstimate();
    }

    public double getVy0() {
        return vy0.getEstimate();
    }

    public void addAngularMeasurement(double wi, double ti, double ai) {
        // let the base class handle the measurement
        addMeasurement(new AngularMeasurement(wi, ti, ai));
    }

    public void addDistanceMeasurement(double wi, double ti, double di) {
        // let the base class handle the measurement
        addMeasurement(new DistanceMeasurement(wi, ti, di));
    }

    public double x(double t) {
        return x0.getEstimate() + (t - t0) * vx0.getEstimate();
    }

    public double y(double t) {
        return y0.getEstimate() + (t - t0) * vy0.getEstimate();
    }

    private class AngularMeasurement extends WeightedMeasurement {

        public AngularMeasurement(double weight, double t, double angle) {
            super(weight, angle);
            this.t = t;
        }

        public double getTheoreticalValue() {
            return Math.atan2(y(t), x(t));
        }

        public double getPartial(EstimatedParameter parameter) {
            double xt = x(t);
            double yt = y(t);
            double r  = Math.sqrt(xt * xt + yt * yt);
            double u  = yt / (r + xt);
            double c  = 2 * u / (1 + u * u);
            if (parameter == x0) {
                return -c;
            } else if (parameter == vx0) {
                return -c * t;
            } else if (parameter == y0) {
                return c * xt / yt;
            } else {
                return c * t * xt / yt;
            }
        }

        private final double t;
        private static final long serialVersionUID = -5990040582592763282L;

    }

    private class DistanceMeasurement extends WeightedMeasurement {

        public DistanceMeasurement(double weight, double t, double angle) {
            super(weight, angle);
            this.t = t;
        }

        public double getTheoreticalValue() {
            double xt = x(t);
            double yt = y(t);
            return Math.sqrt(xt * xt + yt * yt);
        }

        public double getPartial(EstimatedParameter parameter) {
            double xt = x(t);
            double yt = y(t);
            double r  = Math.sqrt(xt * xt + yt * yt);
            if (parameter == x0) {
                return xt / r;
            } else if (parameter == vx0) {
                return xt * t / r;
            } else if (parameter == y0) {
                return yt / r;
            } else {
                return yt * t / r;
            }
        }

        private final double t;
        private static final long serialVersionUID = 3257286197740459503L;

    }

    private double t0;
    private EstimatedParameter x0;
    private EstimatedParameter y0;
    private EstimatedParameter vx0;
    private EstimatedParameter vy0;

}
