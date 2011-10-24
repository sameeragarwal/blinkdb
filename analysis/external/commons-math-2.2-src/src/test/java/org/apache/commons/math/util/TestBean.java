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

package org.apache.commons.math.util;

import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;

/**
 * @version $Revision: 983921 $ $Date: 2010-08-10 12:46:06 +0200 (mar. 10 août 2010) $
 */
public class TestBean {
    private Double x = Double.valueOf(1.0);

    private String y = "1.0";

    /**
     *
     */
    public Double getX() {
        return x;
    }

    /**
     *
     */
    public String getY() {
        return y;
    }

    /**
     *
     */
    public void setX(Double double1) {
        x = double1;
    }

    /**
     *
     */
    public void setY(String string) {
        y = string;
    }

    /**
     *
     */
    public Double getZ() {
        throw new MathRuntimeException(LocalizedFormats.SIMPLE_MESSAGE, "?");
    }

    /**
     *
     */
    public void setZ(Double double1) {
    }

}
