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
# R source file to validate F distribution tests in
# org.apache.commons.math.distribution.TDistributionTest
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
verifyDistribution <- function(points, expected, numeratorDf, denominatorDf, tol) {
 rDistValues <- rep(0, length(points))
    i <- 0
    for (point in points) {
        i <- i + 1
        rDistValues[i] <- pf(point, numeratorDf, denominatorDf, log = FALSE)
    }
    output <- c("Distribution test numerator df = ", numeratorDf, " denominator df = ", denominatorDf)
    if (assertEquals(expected, rDistValues, tol, "Distribution Values")) {
        displayPadded(output, SUCCEEDED, WIDTH)
    } else {
        displayPadded(output, FAILED, WIDTH)
    }       
}

# function to verify density computations
verifyDensity <- function(points, expected, numeratorDf, denominatorDf, tol) {
 rDensityValues <- rep(0, length(points))
    i <- 0
    for (point in points) {
        i <- i + 1
        rDensityValues[i] <- df(point, numeratorDf, denominatorDf, log = FALSE)
    }
    output <- c("Density test numerator df = ", numeratorDf, " denominator df = ", denominatorDf)
    if (assertEquals(expected, rDensityValues, tol, "Density Values")) {
        displayPadded(output, SUCCEEDED, WIDTH)
    } else {
        displayPadded(output, FAILED, WIDTH)
    }       
}

# function to verify quantiles
verifyQuantiles <- function(points, expected, numeratorDf, denominatorDf, tol) {
	rQuantileValues <- rep(0, length(points))
    i <- 0
    for (point in points) {
        i <- i + 1
        rQuantileValues[i] <- qf(point, numeratorDf, denominatorDf, log = FALSE)
    }
    output <- c("Quantile test numerator df = ", numeratorDf, " denominator df = ", denominatorDf)
    if (assertEquals(expected, rQuantileValues, tol, "Quantile Values")) {
        displayPadded(output, SUCCEEDED, WIDTH)
    } else {
        displayPadded(output, FAILED, WIDTH)
    }    
}

#--------------------------------------------------------------------------
cat("F Distribution test cases\n")

numeratorDf <- 5
denominatorDf <- 6
distributionValues <- c(0.001, 0.01, 0.025, 0.05, 0.1, 0.999, 0.990, 0.975, 0.950, 0.900)
densityValues <- c(0.0689156576706, 0.236735653193, 0.364074131941, 0.481570789649, 0.595880479994,
                 0.000133443915657, 0.00286681303403, 0.00969192007502, 0.0242883861471, 0.0605491314658)
distributionPoints <- c(0.0346808448626, 0.0937009113303, 0.143313661184, 0.202008445998, 0.293728320107,
                20.8026639595, 8.74589525602, 5.98756512605, 4.38737418741, 3.10751166664)              
verifyQuantiles(distributionValues, distributionPoints, numeratorDf, denominatorDf, tol)
verifyDistribution(distributionPoints, distributionValues, numeratorDf, denominatorDf, tol)
verifyDensity(distributionPoints, densityValues, numeratorDf, denominatorDf, tol)

displayDashes(WIDTH)
