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

INTRODUCTION

The purpose of the R programs included in this directory is to validate 
the target values used in Apache commons math unit tests. Success running the 
R and commons-math tests on a platform (OS and R version) means that R and 
commons-math give results for the test cases that are close in value.  The 
tests include configurable tolerance levels; but care must be taken in changing 
these, since in most cases the pre-set tolerance is close to the number of 
decimal digits used in expressing the expected values (both here and in the 
corresponding commons-math unit tests).

Of course it is always possible that both R and commons-math give incorrect 
values for test cases, so these tests should not be interpreted as definitive 
in any absolute sense. The value of developing and running the tests is really  
to generate questions (and answers!) when the two systems give different 
results.

Contributions of additional test cases (both R and Junit code) or just 
R programs to validate commons-math tests that are not covered here would be 
greatly appreciated.

SETUP

0) Download and install R.  You can get R here
http://www.r-project.org/
Follow the install instructions and make sure that you can launch R from this  
(i.e., either explitly add R to your OS path or let the install package do it 
for you).  

1) Launch R from this directory and type 
> source("testAll")
to an R prompt.  This should produce output to the console similar to this:

Binomial test cases
Density test n = 10, p = 0.7...........................................SUCCEEDED
Distribution test n = 10, p = 0.7......................................SUCCEEDED
Inverse Distribution test n = 10, p = 0.7..............................SUCCEEDED
Density test n = 5, p = 0..............................................SUCCEEDED
Distribution test n = 5, p = 0.........................................SUCCEEDED
Density test n = 5, p = 1..............................................SUCCEEDED
Distribution test n = 5, p = 1.........................................SUCCEEDED
--------------------------------------------------------------------------------
Normal test cases
Distribution test mu = 2.1, sigma = 1.4................................SUCCEEDED
Distribution test mu = 2.1, sigma = 1.4................................SUCCEEDED
Distribution test mu = 0, sigma = 1....................................SUCCEEDED
Distribution test mu = 0, sigma = 0.1..................................SUCCEEDED
--------------------------------------------------------------------------------
...
<more test reports>


WORKING WITH THE TESTS

The R distribution comes with online manuals that you can view by launching 
a browser instance and then entering

> help.start()

at an R prompt. Poking about in the test case files and the online docs should 
bring you up to speed fairly quickly.  Here are some basic things to get 
you started. I should note at this point that I am by no means an expert R 
programmer, so some things may not be implemented in the the nicest way. 
Comments / suggestions for improvement are welcome!

All of the test cases use some basic functions and global constants (screen
width and success / failure strings) defined in "testFunctions." The  
R "source" function is used to "import" these functions into each of the test 
programs.  The "testAll" program pulls together and executes all of the 
individual test programs.  You can execute any one of them by just entering

> source(<program-name>).

The "assertEquals" function in the testFunctions file mimics the similarly 
named function used by Junit:

assertEquals <- function(expected, observed, tol, message) {
    if(any(abs(expected - observed) > tol)) {
        cat("FAILURE: ",message,"\n")
        cat("EXPECTED: ",expected,"\n")
        cat("OBSERVED: ",observed,"\n")
        return(0)
    } else {
        return(1)
    }
}

The <expected> and <observed> arguments can be scalar values, vectors or 
matrices. If the arguments are vectors or matrices, corresponding entries
are compared.

The standard pattern used throughout the tests looks like this (from 
binomialTestCases):

Start by defining a "verification function" -- in this example a function to
verify computation of binomial probabilities. The <points> argument is a vector
of integer values to feed into the density function, <expected> is a vector of
the computed probabilies from the commons-math Junit tests, <n> and <p> are
parameters of the distribution and <tol> is the error tolerance of the test.
The function computes the probabilities using R and compares the values that
R produces with those in the <expected> vector.

verifyDensity <- function(points, expected, n, p, tol) {
    rDensityValues <- rep(0, length(points))
    i <- 0
    for (point in points) {
        i <- i + 1
        rDensityValues[i] <- dbinom(point, n, p, log = FALSE)
    }
    output <- c("Density test n = ", n, ", p = ", p)
    if (assertEquals(expected,rDensityValues,tol,"Density Values")) {
        displayPadded(output, SUCCEEDED, WIDTH)
    } else {
        displayPadded(output, FAILED, WIDTH)
    }       
}

The displayPadded function just displays its first and second arguments with
enough dots in between to make the whole string WIDTH characters long. It is 
defined in testFunctions.

Then call this function with different parameters corresponding to the different
Junit test cases:

size <- 10.0
probability <- 0.70

densityPoints <- c(-1,0,1,2,3,4,5,6,7,8,9,10,11)
densityValues <- c(0, 0.0000, 0.0001, 0.0014, 0.0090, 0.0368, 0.1029, 
                0.2001, 0.2668, 0.2335, 0.1211, 0.0282, 0)
...
verifyDensity(densityPoints, densityValues, size, probability, tol)

If the values computed by R match the target values in densityValues, this will
produce one line of output to the console:

Density test n = 10, p = 0.7...........................................SUCCEEDED

If you modify the value of tol set at the top of binomialTestCases to make the 
test more sensitive than the number of digits specified in the densityValues 
vector, it will fail, producing the following output, showing the failure and
the expected and observed values:

FAILURE:  Density Values 
EXPECTED:  0 0 1e-04 0.0014 0.009 0.0368 0.1029 0.2001 0.2668 0.2335 0.1211 /
 0.0282 0 
OBSERVED:  0 5.9049e-06 0.000137781 0.0014467005 0.009001692 0.036756909 /
0.1029193452 0.200120949 0.266827932 0.2334744405 0.121060821 0.0282475249 0 
Density test n = 10, p = 0.7..............................................FAILED


