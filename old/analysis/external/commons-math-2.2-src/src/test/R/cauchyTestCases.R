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
# R source file to validate Cauchy distribution tests in
# org.apache.commons.math.distribution.CauchyDistributionTest
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

verifyDistribution <- function(points, expected, median, scale, tol) {
 rDistValues <- rep(0, length(points))
    i <- 0
    for (point in points) {
        i <- i + 1
        rDistValues[i] <- pcauchy(point, median, scale, log = FALSE)
    }
    output <- c("Distribution test median = ",median,", scale = ", scale)
    if (assertEquals(expected, rDistValues, tol, "Distribution Values")) {
        displayPadded(output, SUCCEEDED, WIDTH)
    } else {
        displayPadded(output, FAILED, WIDTH)
    }       
}

# function to verify density computations

verifyDensity <- function(points, expected, median, scale, tol) {
 rDensityValues <- rep(0, length(points))
    i <- 0
    for (point in points) {
        i <- i + 1
        rDensityValues[i] <- dcauchy(point, median, scale, log = FALSE)
    }
    output <- c("Density test median = ",median,", scale = ", scale)
    if (assertEquals(expected, rDensityValues, tol, "Density Values")) {
        displayPadded(output, SUCCEEDED, WIDTH)
    } else {
        displayPadded(output, FAILED, WIDTH)
    }       
}

# function to verify quantiles

verifyQuantiles <- function(points, expected, median, scale, tol) {
	rQuantileValues <- rep(0, length(points))
    i <- 0
    for (point in points) {
        i <- i + 1
        rQuantileValues[i] <- qcauchy(point, median, scale, log = FALSE)
    }
    output <- c("Quantile test median = ",median,", scale = ", scale)
    if (assertEquals(expected, rQuantileValues, tol, "Quantile Values")) {
        displayPadded(output, SUCCEEDED, WIDTH)
    } else {
        displayPadded(output, FAILED, WIDTH)
    }    
}

#--------------------------------------------------------------------------
cat("Cauchy test cases\n")

median <- 1.2
scale <- 2.1
distributionValues <- c(0.001, 0.01, 0.025, 0.05, 0.1, 0.999,
                0.990, 0.975, 0.950, 0.900)
densityValues <- c(1.49599158008e-06, 0.000149550440335, 0.000933076881878, 0.00370933207799, 0.0144742330437,
                1.49599158008e-06, 0.000149550440335, 0.000933076881878, 0.00370933207799, 0.0144742330437)
distributionPoints <- c(-667.24856187, -65.6230835029, -25.4830299460, -12.0588781808, -5.26313542807,
                669.64856187, 68.0230835029, 27.8830299460, 14.4588781808, 7.66313542807)
verifyDistribution(distributionPoints, distributionValues, median, scale, tol)
verifyDensity(distributionPoints, densityValues, median, scale, tol)
verifyQuantiles(distributionValues, distributionPoints, median, scale, tol)

displayDashes(WIDTH)
