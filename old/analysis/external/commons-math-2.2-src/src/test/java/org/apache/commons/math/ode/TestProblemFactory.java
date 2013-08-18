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

package org.apache.commons.math.ode;

/**
 * This class is used in the junit tests for the ODE integrators.
 */
public class TestProblemFactory {

  /** Problems pool. */
  private static final TestProblemAbstract[] pool = {
    new TestProblem1(),
    new TestProblem2(),
    new TestProblem3(),
    new TestProblem4(),
    new TestProblem5(),
    new TestProblem6()
  };

  /**
   * Private constructor.
   * This is a utility class, so there are no instance at all.
   */
  private TestProblemFactory() {
  }

  /**
   * Get the problems.
   * @return array of problems to solve
   */
  public static TestProblemAbstract[] getProblems() {
    return pool;
  }

}
