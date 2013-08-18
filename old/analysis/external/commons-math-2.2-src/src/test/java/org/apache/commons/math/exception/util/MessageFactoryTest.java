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
package org.apache.commons.math.exception.util;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

public class MessageFactoryTest {

    @Test
    public void testSpecificGeneral() {
        Localizable specific = new DummyLocalizable("specific {0} - {1} - {2}");
        Localizable general  = new DummyLocalizable("general  {0} / {1}");
        String message = MessageFactory.buildMessage(Locale.FRENCH, specific, general,
                                                     0, 1, 2, 'a', 'b');
        Assert.assertEquals("general  0 / 1: specific 0 - 1 - 2", message);
    }

    @Test
    public void testNullSpecific() {
        Localizable general  = new DummyLocalizable("general  {0} / {1}");
        String message = MessageFactory.buildMessage(Locale.FRENCH, null, general,
                                                     0, 1, 2, 'a', 'b');
        Assert.assertEquals("general  0 / 1", message);
    }

    @Test
    public void testNullGeneral() {
        Localizable specific = new DummyLocalizable("specific {0} - {1} - {2}");
        String message = MessageFactory.buildMessage(Locale.FRENCH, specific, null,
                                                     0, 1, 2, 'a', 'b');
        Assert.assertEquals("specific 0 - 1 - 2", message);
    }

    @Test
    public void testNull() {
        String message = MessageFactory.buildMessage(Locale.FRENCH, null, null, "nothing");
        Assert.assertEquals("", message);
    }

}
