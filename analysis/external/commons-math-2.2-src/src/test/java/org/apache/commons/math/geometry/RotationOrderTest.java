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

package org.apache.commons.math.geometry;

import java.lang.reflect.Field;

import org.apache.commons.math.geometry.RotationOrder;

import junit.framework.*;

public class RotationOrderTest
  extends TestCase {

  public RotationOrderTest(String name) {
    super(name);
  }

  public void testName() {

    RotationOrder[] orders = {
      RotationOrder.XYZ, RotationOrder.XZY, RotationOrder.YXZ,
      RotationOrder.YZX, RotationOrder.ZXY, RotationOrder.ZYX,
      RotationOrder.XYX, RotationOrder.XZX, RotationOrder.YXY,
      RotationOrder.YZY, RotationOrder.ZXZ, RotationOrder.ZYZ
    };

    for (int i = 0; i < orders.length; ++i) {
      assertEquals(getFieldName(orders[i]), orders[i].toString());
    }

  }

  private String getFieldName(RotationOrder order) {
    try {
      Field[] fields = RotationOrder.class.getFields();
      for (int i = 0; i < fields.length; ++i) {
        if (fields[i].get(null) == order) {
          return fields[i].getName();
        }
      }
    } catch (IllegalAccessException iae) {
      // ignored
    }
    return "unknown";
  }

}
