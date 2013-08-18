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
# R source file to validate T distribution tests in
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
verifyDistribution <- function(points, expected, df, tol) {
 rDistValues <- rep(0, length(points))
    i <- 0
    for (point in points) {
        i <- i + 1
        rDistValues[i] <- pt(point, df, log = FALSE)
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
        rDensityValues[i] <- dt(point, df, log = FALSE)
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
        rQuantileValues[i] <- qt(point, df, log = FALSE)
    }
    output <- c("Quantile test df = ", df)
    if (assertEquals(expected, rQuantileValues, tol, "Quantile Values")) {
        displayPadded(output, SUCCEEDED, WIDTH)
    } else {
        displayPadded(output, FAILED, WIDTH)
    }    
}

#--------------------------------------------------------------------------
cat("T Distribution test cases\n")

df <- 5
distributionValues <- c(0.001, 0.01, 0.025, 0.05, 0.1, 0.999, 0.990, 0.975, 0.950, 0.900)
densityValues <- c(0.000756494565517, 0.0109109752919, 0.0303377878006, 0.0637967988952, 0.128289492005,
                0.000756494565517, 0.0109109752919, 0.0303377878006, 0.0637967988952, 0.128289492005)
distributionPoints <- c(-5.89342953136, -3.36492999891, -2.57058183564, -2.01504837333, -1.47588404882,
                5.89342953136, 3.36492999891, 2.57058183564, 2.01504837333, 1.47588404882)              
verifyQuantiles(distributionValues, distributionPoints, df, tol)
verifyDistribution(distributionPoints, distributionValues, df, tol)
verifyDensity(distributionPoints, densityValues, df, tol)

df <- 1
densityValues <- c(3.14158231817e-06, 0.000314055924703, 0.00195946145194, 0.00778959736375, 0.0303958893917,
                3.14158231817e-06, 0.000314055924703, 0.00195946145194, 0.00778959736375, 0.0303958893917)
distributionPoints <- c(-318.308838986, -31.8205159538, -12.7062047362, -6.31375151468, -3.07768353718,
                318.308838986, 31.8205159538, 12.7062047362, 6.31375151468, 3.07768353718)
verifyQuantiles(distributionValues, distributionPoints, df, tol)
verifyDistribution(distributionPoints, distributionValues, df, tol)
verifyDensity(distributionPoints, densityValues, df, tol)

displayDashes(WIDTH)
