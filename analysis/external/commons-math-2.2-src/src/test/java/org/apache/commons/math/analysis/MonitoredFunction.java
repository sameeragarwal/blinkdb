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
package org.apache.commons.math.analysis;

import org.apache.commons.math.FunctionEvaluationException;
/**
 * Wrapper class for counting functions calls.
 *
 * @version $Revision: 1070725 $ $Date: 2011-02-15 02:31:12 +0100 (mar. 15 févr. 2011) $
 */
public class MonitoredFunction implements UnivariateRealFunction {

    public MonitoredFunction(UnivariateRealFunction f) {
        callsCount = 0;
        this.f = f;
    }

    public void setCallsCount(int callsCount) {
        this.callsCount = callsCount;
    }

    public int getCallsCount() {
        return callsCount;
    }

    public double value(double x) throws FunctionEvaluationException {
        ++callsCount;
        return f.value(x);
    }

    private int callsCount;
    private UnivariateRealFunction f;

}
