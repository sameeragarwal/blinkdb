/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.hive.ql.plan;

import java.io.Serializable;
import java.util.List;

/**
 * SampleDesc.
 *
 */
@Explain(displayName = "Sample Operator")
public class SampleDesc implements Serializable {

  private static final long serialVersionUID = 1L;
  //private org.apache.hadoop.hive.ql.plan.ExprNodeDesc predicate;
  double probability;
  private boolean isSamplingPred;
  //private transient sampleDesc sampleDescr;

  public SampleDesc() {
  }

  public SampleDesc(
//      final org.apache.hadoop.hive.ql.plan.ExprNodeDesc predicate,
	  double probability,
      boolean isSamplingPred) {
    this.probability = probability;
    this.isSamplingPred = isSamplingPred;
    //sampleDescr = null;
  }

  /*
  public SampleDesc(
      final org.apache.hadoop.hive.ql.plan.ExprNodeDesc predicate,
      boolean isSamplingPred, final sampleDesc sampleDescr) {
    this.predicate = predicate;
    this.isSamplingPred = isSamplingPred;
    //this.sampleDescr = sampleDescr;
  }
  */

  @Explain(displayName = "probability")
  public double getProbability() {
    return probability;
  }

  public void setProbability(
  final double probability) {
	  this.probability = probability;
  }
  
  /*
  public void setPredicate(
      final org.apache.hadoop.hive.ql.plan.ExprNodeDesc predicate) {
    this.predicate = predicate;
  }
  */

  /*
  @Explain(displayName = "isSamplingPred", normalExplain = false)
  public boolean getIsSamplingPred() {
    return isSamplingPred;
  }

  public void setIsSamplingPred(final boolean isSamplingPred) {
    this.isSamplingPred = isSamplingPred;
  }

  @Explain(displayName = "sampleDesc", normalExplain = false)
  public sampleDesc getSampleDescr() {
    //return sampleDescr;
	  return null;
  }

  public void setSampleDescr(final sampleDesc sampleDescr) {
    //this.sampleDescr = sampleDescr;
	  return;
  }
  */

}
