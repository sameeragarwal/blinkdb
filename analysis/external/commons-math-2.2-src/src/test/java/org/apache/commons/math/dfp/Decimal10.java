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

package org.apache.commons.math.dfp;

public class Decimal10 extends DfpDec {

    Decimal10(final DfpField factory) {
        super(factory);
    }

    Decimal10(final DfpField factory, final byte x) {
        super(factory, x);
    }

    Decimal10(final DfpField factory, final int x) {
        super(factory, x);
    }

    Decimal10(final DfpField factory, final long x) {
        super(factory, x);
    }

    Decimal10(final DfpField factory, final double x) {
        super(factory, x);
    }

    public Decimal10(final Dfp d) {
        super(d);
    }

    public Decimal10(final DfpField factory, final String s) {
        super(factory, s);
    }

    protected Decimal10(final DfpField factory, final byte sign, final byte nans) {
        super(factory, sign, nans);
    }

    @Override
    public Dfp newInstance() {
        return new Decimal10(getField());
    }

    @Override
    public Dfp newInstance(final byte x) {
        return new Decimal10(getField(), x);
    }

    @Override
    public Dfp newInstance(final int x) {
        return new Decimal10(getField(), x);
    }

    @Override
    public Dfp newInstance(final long x) {
        return new Decimal10(getField(), x);
    }

    @Override
    public Dfp newInstance(final double x) {
        return new Decimal10(getField(), x);
    }

    @Override
    public Dfp newInstance(final Dfp d) {
        return new Decimal10(d);
    }

    @Override
    public Dfp newInstance(final String s) {
        return new Decimal10(getField(), s);
    }

    @Override
    public Dfp newInstance(final byte sign, final byte nans) {
        return new Decimal10(getField(), sign, nans);
    }

    @Override
    protected int getDecimalDigits() {
        return 10;
    }

}
