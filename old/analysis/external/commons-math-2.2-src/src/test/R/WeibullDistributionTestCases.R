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
# R source file to validate Weibull distribution tests in
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
        rDistValues[i] <- pweibull(point, shape=alpha, scale=beta, log = FALSE)
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
        rDensityValues[i] <- dweibull(point, shape=alpha, scale=beta, log = FALSE)
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
        rQuantileValues[i] <- qweibull(point, shape=alpha, scale=beta, log = FALSE)
    }
    output <- c("Quantile test shape = ", shape, " scale = ", scale)
    if (assertEquals(expected, rQuantileValues, tol, "Quantile Values")) {
        displayPadded(output, SUCCEEDED, WIDTH)
    } else {
        displayPadded(output, FAILED, WIDTH)
    }    
}

#--------------------------------------------------------------------------
cat("Weibull Distribution test cases\n")

shape <- 1.2
scale <- 2.1
distributionValues <- c(0.001, 0.01, 0.025, 0.05, 0.1, 0.999, 0.990, 0.975, 0.950, 0.900)
densityValues <- c(0.180535929306, 0.262801138133, 0.301905425199, 0.330899152971, 0.353441418887, 0.000788590320203,
                 0.00737060094841, 0.0177576041516, 0.0343043442574, 0.065664589369)
distributionPoints <- c(0.00664355180993, 0.0454328283309, 0.0981162737374, 0.176713524579, 0.321946865392,
                 10.5115496887, 7.4976304671, 6.23205600701, 5.23968436955, 4.20790282578)              
verifyQuantiles(distributionValues, distributionPoints, shape, scale, tol)
verifyDistribution(distributionPoints, distributionValues, shape, scale, tol)
verifyDensity(distributionPoints, densityValues, shape, scale, tol)

displayDashes(WIDTH)
