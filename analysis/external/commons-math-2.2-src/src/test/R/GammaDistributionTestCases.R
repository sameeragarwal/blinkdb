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
# R source file to validate Gamma distribution tests in
# org.apache.commons.math.distribution.GammaDistributionTest
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
verifyDistribution <- function(points, expected, alpha, beta, tol) {
 rDistValues <- rep(0, length(points))
    i <- 0
    for (point in points) {
        i <- i + 1
        rDistValues[i] <- pgamma(point, shape=alpha, scale=beta, log = FALSE)
    }
    output <- c("Distribution test shape = ", shape, " scale = ", scale)
    if (assertEquals(expected, rDistValues, tol, "Distribution Values")) {
        displayPadded(output, SUCCEEDED, WIDTH)
    } else {
        displayPadded(output, FAILED, WIDTH)
    }       
}

# function to verify density computations
verifyDensity <- function(points, expected, alpha, beta, tol) {
 rDensityValues <- rep(0, length(points))
    i <- 0
    for (point in points) {
        i <- i + 1
        rDensityValues[i] <- dgamma(point, shape=alpha, scale=beta, log = FALSE)
    }
    output <- c("Density test shape = ", shape, " scale = ", scale)
    if (assertEquals(expected, rDensityValues, tol, "Density Values")) {
        displayPadded(output, SUCCEEDED, WIDTH)
    } else {
        displayPadded(output, FAILED, WIDTH)
    }       
}

# function to verify quantiles
verifyQuantiles <- function(points, expected, alpha, beta, tol) {
	rQuantileValues <- rep(0, length(points))
    i <- 0
    for (point in points) {
        i <- i + 1
        rQuantileValues[i] <- qgamma(point, shape=alpha, scale=beta, log = FALSE)
    }
    output <- c("Quantile test shape = ", shape, " scale = ", scale)
    if (assertEquals(expected, rQuantileValues, tol, "Quantile Values")) {
        displayPadded(output, SUCCEEDED, WIDTH)
    } else {
        displayPadded(output, FAILED, WIDTH)
    }    
}

#--------------------------------------------------------------------------
cat("Gamma Distribution test cases\n")

shape <- 4
scale <- 2
distributionValues <- c(0.001, 0.01, 0.025, 0.05, 0.1, 0.999, 0.990, 0.975, 0.950, 0.900)
densityValues <- c(0.00427280075546, 0.0204117166709, 0.0362756163658, 0.0542113174239, 0.0773195272491,
                   0.000394468852816, 0.00366559696761, 0.00874649473311, 0.0166712508128, 0.0311798227954)
distributionPoints <- c(0.857104827257, 1.64649737269, 2.17973074725, 2.7326367935, 3.48953912565,
                   26.1244815584, 20.0902350297, 17.5345461395, 15.5073130559, 13.3615661365)              
verifyQuantiles(distributionValues, distributionPoints, shape, scale, tol)
verifyDistribution(distributionPoints, distributionValues, shape, scale, tol)
verifyDensity(distributionPoints, densityValues, shape, scale, tol)

displayDashes(WIDTH)
