# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
#------------------------------------------------------------------------------
# R source file to validate ChiSquare distribution tests in
# org.apache.commons.math.distribution.ChiSquareDistributionTest
#
# To run the test, install R, put this file and testFunctions
# into the same directory, launch R from this directory and then enter
# source("<name-of-this-file>")
#
#-----------------------------------------------------------------------------
tol <- 1E-9

# Function definitions
source("testFunctions")           # utility test functions

# function to verify distribution computations
verifyDistribution <- function(points, expected, df, tol) {
 rDistValues <- rep(0, length(points))
    i <- 0
    for (point in points) {
        i <- i + 1
        rDistValues[i] <- pchisq(point, df, log = FALSE)
    }
    output <- c("Distribution test df = ", df)
    if (assertEquals(expected, rDistValues, tol, "Distribution Values")) {
        displayPadded(output, SUCCEEDED, WIDTH)
    } else {
        displayPadded(output, FAILED, WIDTH)
    }       
}

# function to verify density computations
verifyDensity <- function(points, expected, df, tol) {
 rDensityValues <- rep(0, length(points))
    i <- 0
    for (point in points) {
        i <- i + 1
        rDensityValues[i] <- dchisq(point, df, log = FALSE)
    }
    output <- c("Density test df = ", df)
    if (assertEquals(expected, rDensityValues, tol, "Density Values")) {
        displayPadded(output, SUCCEEDED, WIDTH)
    } else {
        displayPadded(output, FAILED, WIDTH)
    }       
}

# function to verify quantiles
verifyQuantiles <- function(points, expected, df, tol) {
	rQuantileValues <- rep(0, length(points))
    i <- 0
    for (point in points) {
        i <- i + 1
        rQuantileValues[i] <- qchisq(point, df, log = FALSE)
    }
    output <- c("Quantile test df = ", df)
    if (assertEquals(expected, rQuantileValues, tol, "Quantile Values")) {
        displayPadded(output, SUCCEEDED, WIDTH)
    } else {
        displayPadded(output, FAILED, WIDTH)
    }    
}

#--------------------------------------------------------------------------
cat("ChiSquare Distribution test cases\n")

df <- 5
distributionValues <- c(0.001, 0.01, 0.025, 0.05, 0.1, 0.999, 0.990, 0.975, 0.950, 0.900)
densityValues <- c(0.0115379817652, 0.0415948507811, 0.0665060119842, 0.0919455953114, 0.121472591024,
                 0.000433630076361, 0.00412780610309, 0.00999340341045, 0.0193246438937, 0.0368460089216)
distributionPoints <- c(0.210212602629, 0.554298076728, 0.831211613487, 1.14547622606, 1.61030798696,
                20.5150056524, 15.0862724694, 12.8325019940, 11.0704976935, 9.23635689978)              
verifyQuantiles(distributionValues, distributionPoints, df, tol)
verifyDistribution(distributionPoints, distributionValues, df, tol)
verifyDensity(distributionPoints, densityValues, df, tol)

df <- .1
distributionPoints <- c(1.16892641146e-60, 1.16892641146e-40, 1.06313237798e-32, 1.11477509638e-26, 1.16892641146e-20,
                5.47291719746, 2.17525480018, 1.13434752351, 0.531864604852, 0.152634227818)
verifyQuantiles(distributionValues, distributionPoints, df, tol)
verifyDistribution(distributionPoints, distributionValues, df, tol)

displayDashes(WIDTH)
