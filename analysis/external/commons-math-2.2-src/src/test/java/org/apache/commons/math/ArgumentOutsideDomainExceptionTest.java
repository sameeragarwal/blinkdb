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

package org.apache.commons.math;

import java.util.Locale;

import org.apache.commons.math.util.FastMath;

import junit.framework.TestCase;

/**
 * @version $Revision: 1070725 $ $Date: 2011-02-15 02:31:12 +0100 (mar. 15 févr. 2011) $
 */
public class ArgumentOutsideDomainExceptionTest extends TestCase {

    public void testConstructor(){
        ArgumentOutsideDomainException ex = new ArgumentOutsideDomainException(FastMath.PI, 10.0, 20.0);
        assertNull(ex.getCause());
        assertNotNull(ex.getMessage());
        assertTrue(ex.getMessage().indexOf("3.14") > 0);
        assertEquals(FastMath.PI, ex.getArgument()[0], 0);
        assertFalse(ex.getMessage().equals(ex.getMessage(Locale.FRENCH)));
    }

}
