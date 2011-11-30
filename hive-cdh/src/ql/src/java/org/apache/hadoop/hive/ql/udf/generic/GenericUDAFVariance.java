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
package org.apache.hadoop.hive.ql.udf.generic;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentTypeException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.apache.hadoop.hive.serde2.io.DoubleWritable;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.DoubleObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.LongObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.util.StringUtils;

/**
 * Compute the variance. This class is extended by: GenericUDAFVarianceSample
 * GenericUDAFStd GenericUDAFStdSample
 * 
 */
@Description(name = "variance,var_pop",
    value = "_FUNC_(x) - Returns the variance of a set of numbers")
public class GenericUDAFVariance extends AbstractGenericUDAFResolver {

  static final Log LOG = LogFactory.getLog(GenericUDAFVariance.class.getName());

  @Override
  public GenericUDAFEvaluator getEvaluator(TypeInfo[] parameters) throws SemanticException {
    if (parameters.length != 1) {
      throw new UDFArgumentTypeException(parameters.length - 1,
          "Exactly one argument is expected.");
    }

    if (parameters[0].getCategory() != ObjectInspector.Category.PRIMITIVE) {
      throw new UDFArgumentTypeException(0,
          "Only primitive type arguments are accepted but "
          + parameters[0].getTypeName() + " is passed.");
    }
    switch (((PrimitiveTypeInfo) parameters[0]).getPrimitiveCategory()) {
    case BYTE:
    case SHORT:
    case INT:
    case LONG:
    case FLOAT:
    case DOUBLE:
    	return new GenericUDAFVarianceEvaluatorWithError();
    case STRING:
      //@sameerag: huh?
      //return new GenericUDAFVarianceEvaluator();
    case BOOLEAN:
    default:
      throw new UDFArgumentTypeException(0,
          "Only numeric or string type arguments are accepted but "
          + parameters[0].getTypeName() + " is passed.");
    }
  }
  
  /**
   * Evaluate the variance using the algorithm described by Chan, Golub, and LeVeque in
   * "Algorithms for computing the sample variance: analysis and recommendations"
   * The American Statistician, 37 (1983) pp. 242--247.
   * 
   * variance = variance1 + variance2 + n/(m*(m+n)) * pow(((m/n)*t1 - t2),2)
   * 
   * where: - variance is sum[x-avg^2] (this is actually n times the variance)
   * and is updated at every step. - n is the count of elements in chunk1 - m is
   * the count of elements in chunk2 - t1 = sum of elements in chunk1, t2 = 
   * sum of elements in chunk2.
   *
   * This algorithm was proven to be numerically stable by J.L. Barlow in
   * "Error analysis of a pairwise summation algorithm to compute sample variance"
   * Numer. Math, 58 (1991) pp. 583--590
   * 
   */
  public static class GenericUDAFVarianceEvaluator extends GenericUDAFEvaluator {

    // For PARTIAL1 and COMPLETE
    private PrimitiveObjectInspector inputOI;

    // For PARTIAL2 and FINAL
    private StructObjectInspector soi;
    private StructField countField;
    private StructField sumField;
    private StructField varianceField;
    private LongObjectInspector countFieldOI;
    private DoubleObjectInspector sumFieldOI;
    private DoubleObjectInspector varianceFieldOI;

    // For PARTIAL1 and PARTIAL2
    private Object[] partialResult;

    // For FINAL and COMPLETE
    private DoubleWritable result;

    @Override
    public ObjectInspector init(Mode m, ObjectInspector[] parameters) throws HiveException {
      assert (parameters.length == 1);
      super.init(m, parameters);

      // init input
      if (mode == Mode.PARTIAL1 || mode == Mode.COMPLETE) {
        inputOI = (PrimitiveObjectInspector) parameters[0];
      } else {
        soi = (StructObjectInspector) parameters[0];

        countField = soi.getStructFieldRef("count");
        sumField = soi.getStructFieldRef("sum");
        varianceField = soi.getStructFieldRef("variance");

        countFieldOI = (LongObjectInspector) countField
            .getFieldObjectInspector();
        sumFieldOI = (DoubleObjectInspector) sumField.getFieldObjectInspector();
        varianceFieldOI = (DoubleObjectInspector) varianceField
            .getFieldObjectInspector();
      }

      // init output
      if (mode == Mode.PARTIAL1 || mode == Mode.PARTIAL2) {
        // The output of a partial aggregation is a struct containing
        // a long count and doubles sum and variance.

        ArrayList<ObjectInspector> foi = new ArrayList<ObjectInspector>();

        foi.add(PrimitiveObjectInspectorFactory.writableLongObjectInspector);
        foi.add(PrimitiveObjectInspectorFactory.writableDoubleObjectInspector);
        foi.add(PrimitiveObjectInspectorFactory.writableDoubleObjectInspector);

        //@sameerag: Adding partial vars
        foi.add(PrimitiveObjectInspectorFactory.writableDoubleObjectInspector);
        foi.add(PrimitiveObjectInspectorFactory.writableDoubleObjectInspector);
        foi.add(PrimitiveObjectInspectorFactory.writableDoubleObjectInspector);
        foi.add(PrimitiveObjectInspectorFactory.writableDoubleObjectInspector);
        
        ArrayList<String> fname = new ArrayList<String>();
        fname.add("count");
        fname.add("sum");
        fname.add("variance");

        partialResult = new Object[3];
        partialResult[0] = new LongWritable(0);
        partialResult[1] = new DoubleWritable(0);
        partialResult[2] = new DoubleWritable(0);

        return ObjectInspectorFactory.getStandardStructObjectInspector(fname,
            foi);

      } else {
        setResult(new DoubleWritable(0));
        return PrimitiveObjectInspectorFactory.writableDoubleObjectInspector;
      }
    }

    static class StdAgg implements AggregationBuffer {
      long count; // number of elements
      double sum; // sum of elements
      double variance; // sum[x-avg^2] (this is actually n times the variance)
     };

    @Override
    public AggregationBuffer getNewAggregationBuffer() throws HiveException {
      StdAgg result = new StdAgg();
      reset(result);
      return result;
    }

    @Override
    public void reset(AggregationBuffer agg) throws HiveException {
      StdAgg myagg = (StdAgg) agg;
      myagg.count = 0;
      myagg.sum = 0;
      myagg.variance = 0;
    }

    private boolean warned = false;

    @Override
    public void iterate(AggregationBuffer agg, Object[] parameters)
        throws HiveException {
      assert (parameters.length == 1);
      Object p = parameters[0];
      if (p != null) {
        StdAgg myagg = (StdAgg) agg;
        try {
          double v = PrimitiveObjectInspectorUtils.getDouble(p, inputOI);
          myagg.count++;
          myagg.sum += v;
          if(myagg.count > 1) {
            double t = myagg.count*v - myagg.sum;
            myagg.variance += (t*t) / ((double)myagg.count*(myagg.count-1));
          }
        } catch (NumberFormatException e) {
          if (!warned) {
            warned = true;
            LOG.warn(getClass().getSimpleName() + " "
                + StringUtils.stringifyException(e));
            LOG.warn(getClass().getSimpleName()
                + " ignoring similar exceptions.");
          }
        }
      }
    }

    @Override
    public Object terminatePartial(AggregationBuffer agg) throws HiveException {
      StdAgg myagg = (StdAgg) agg;
      ((LongWritable) partialResult[0]).set(myagg.count);
      ((DoubleWritable) partialResult[1]).set(myagg.sum);
      ((DoubleWritable) partialResult[2]).set(myagg.variance);

      return partialResult;
    }

    @Override
    public void merge(AggregationBuffer agg, Object partial) throws HiveException {
      if (partial != null) {
        StdAgg myagg = (StdAgg) agg;

        Object partialCount = soi.getStructFieldData(partial, countField);
        Object partialSum = soi.getStructFieldData(partial, sumField);
        Object partialVariance = soi.getStructFieldData(partial, varianceField);

        long n = myagg.count;
        long m = countFieldOI.get(partialCount);

        if (n == 0) {
          // Just copy the information since there is nothing so far
          myagg.variance = sumFieldOI.get(partialVariance);
          myagg.count = countFieldOI.get(partialCount);
          myagg.sum = sumFieldOI.get(partialSum);
          
        }

        if (m != 0 && n != 0) {
          // Merge the two partials

          double a = myagg.sum;
          double b = sumFieldOI.get(partialSum);

          myagg.count += m;
          myagg.sum += b;
          double t = (m/(double)n)*a - b;
          myagg.variance += sumFieldOI.get(partialVariance) + ((n/(double)m)/((double)n+m)) * t * t;
        }
      }
    }

    @Override
    public Object terminate(AggregationBuffer agg) throws HiveException {
      StdAgg myagg = (StdAgg) agg;

      if (myagg.count == 0) { // SQL standard - return null for zero elements
        return null;
      } else {
        if (myagg.count > 1) {
          getResult().set(myagg.variance / (myagg.count));
        } else { // for one element the variance is always 0
          getResult().set(0);
        }
        return getResult();
      }
    }

    public void setResult(DoubleWritable result) {
      this.result = result;
    }

    public DoubleWritable getResult() {
      return result;
    }
  }


  /**
   * Evaluate the variance using the algorithm described by Chan, Golub, and LeVeque in
   * "Algorithms for computing the sample variance: analysis and recommendations"
   * The American Statistician, 37 (1983) pp. 242--247.
   * 
   * variance = variance1 + variance2 + n/(m*(m+n)) * pow(((m/n)*t1 - t2),2)
   * 
   * where: - variance is sum[x-avg^2] (this is actually n times the variance)
   * and is updated at every step. - n is the count of elements in chunk1 - m is
   * the count of elements in chunk2 - t1 = sum of elements in chunk1, t2 = 
   * sum of elements in chunk2.
   *
   * This algorithm was proven to be numerically stable by J.L. Barlow in
   * "Error analysis of a pairwise summation algorithm to compute sample variance"
   * Numer. Math, 58 (1991) pp. 583--590
   * 
   */
  public static class GenericUDAFVarianceEvaluatorWithError extends GenericUDAFEvaluator {

    // For PARTIAL1 and COMPLETE
    private PrimitiveObjectInspector inputOI;

    // For PARTIAL2 and FINAL
    private StructObjectInspector soi;
    private StructField countField;
    private StructField sumField;
    private StructField varianceField;
    private StructField meanField;
    private StructField M2Field;
    private StructField M3Field;
    private StructField M4Field;
    private LongObjectInspector countFieldOI;
    private DoubleObjectInspector sumFieldOI;
    private DoubleObjectInspector varianceFieldOI;
    private DoubleObjectInspector meanFieldOI;
    private DoubleObjectInspector M2FieldOI;
    private DoubleObjectInspector M3FieldOI;
    private DoubleObjectInspector M4FieldOI;

    // For PARTIAL1 and PARTIAL2
    private Object[] partialResult;

    // For FINAL and COMPLETE
    private ArrayList<DoubleWritable> result;

    @Override
    public ObjectInspector init(Mode m, ObjectInspector[] parameters) throws HiveException {
      assert (parameters.length == 1);
      super.init(m, parameters);

      // init input
      if (mode == Mode.PARTIAL1 || mode == Mode.COMPLETE) {
        inputOI = (PrimitiveObjectInspector) parameters[0];
      } else {
        soi = (StructObjectInspector) parameters[0];

        countField = soi.getStructFieldRef("count");
        sumField = soi.getStructFieldRef("sum");
        varianceField = soi.getStructFieldRef("variance");
        meanField = soi.getStructFieldRef("mean");
        M2Field = soi.getStructFieldRef("M2");
        M3Field = soi.getStructFieldRef("M3");
        M4Field = soi.getStructFieldRef("M4");

        countFieldOI = (LongObjectInspector) countField
            .getFieldObjectInspector();
        sumFieldOI = (DoubleObjectInspector) sumField.getFieldObjectInspector();
        varianceFieldOI = (DoubleObjectInspector) varianceField
            .getFieldObjectInspector();
        meanFieldOI = (DoubleObjectInspector) meanField.getFieldObjectInspector();
        M2FieldOI = (DoubleObjectInspector) M2Field.getFieldObjectInspector();
        M3FieldOI = (DoubleObjectInspector) M3Field.getFieldObjectInspector();
        M4FieldOI = (DoubleObjectInspector) M4Field.getFieldObjectInspector();
        
      }

      // init output
      if (mode == Mode.PARTIAL1 || mode == Mode.PARTIAL2) {
        // The output of a partial aggregation is a struct containing
        // a long count and doubles sum and variance.

        ArrayList<ObjectInspector> foi = new ArrayList<ObjectInspector>();

        foi.add(PrimitiveObjectInspectorFactory.writableLongObjectInspector);
        foi.add(PrimitiveObjectInspectorFactory.writableDoubleObjectInspector);
        foi.add(PrimitiveObjectInspectorFactory.writableDoubleObjectInspector);
        foi.add(PrimitiveObjectInspectorFactory.writableDoubleObjectInspector);
        foi.add(PrimitiveObjectInspectorFactory.writableDoubleObjectInspector);
        foi.add(PrimitiveObjectInspectorFactory.writableDoubleObjectInspector);
        foi.add(PrimitiveObjectInspectorFactory.writableDoubleObjectInspector);

        ArrayList<String> fname = new ArrayList<String>();
        fname.add("count");
        fname.add("sum");
        fname.add("variance");
        fname.add("mean");
        fname.add("M2");
        fname.add("M3");
        fname.add("M4");

        partialResult = new Object[7];
        partialResult[0] = new LongWritable(0);
        partialResult[1] = new DoubleWritable(0);
        partialResult[2] = new DoubleWritable(0);
        partialResult[3] = new DoubleWritable(0);
        partialResult[4] = new DoubleWritable(0);
        partialResult[5] = new DoubleWritable(0);
        partialResult[6] = new DoubleWritable(0);

        return ObjectInspectorFactory.getStandardStructObjectInspector(fname,
            foi);

      } else {
    	   ArrayList<String> fname = new ArrayList<String>();
           fname.add("var");
           fname.add("error");
           fname.add("ci");
           fname.add("mean");
           fname.add("M2");
           fname.add("M3");
           fname.add("M4");
           ArrayList<ObjectInspector> foi = new ArrayList<ObjectInspector>();
           foi.add(PrimitiveObjectInspectorFactory.writableDoubleObjectInspector);
           foi.add(PrimitiveObjectInspectorFactory.writableDoubleObjectInspector);
           foi.add(PrimitiveObjectInspectorFactory.writableDoubleObjectInspector);
           foi.add(PrimitiveObjectInspectorFactory.writableDoubleObjectInspector);
           foi.add(PrimitiveObjectInspectorFactory.writableDoubleObjectInspector);
           foi.add(PrimitiveObjectInspectorFactory.writableDoubleObjectInspector);
           foi.add(PrimitiveObjectInspectorFactory.writableDoubleObjectInspector);

           result = new ArrayList<DoubleWritable>();
           return ObjectInspectorFactory.getStandardStructObjectInspector(fname, foi);
      }
    }

    static class StdAgg implements AggregationBuffer {
      long count; // number of elements
      double sum; // sum of elements
      double variance; // sum[x-avg^2] (this is actually n times the variance)
            
      //@sameerag: Our implementation mirrors the logic here
      //		   http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance
      double mean;
      double M2;
      double M3;
      double M4;

    };

    @Override
    public AggregationBuffer getNewAggregationBuffer() throws HiveException {
      StdAgg result = new StdAgg();
      reset(result);
      return result;
    }

    @Override
    public void reset(AggregationBuffer agg) throws HiveException {
      StdAgg myagg = (StdAgg) agg;
      myagg.count = 0;
      myagg.sum = 0;
      myagg.variance = 0;
      myagg.mean = 0;
      myagg.M2 = 0;
      myagg.M3 = 0;
      myagg.M4 = 0;
    }

    private boolean warned = false;

    @Override
    public void iterate(AggregationBuffer agg, Object[] parameters)
        throws HiveException {
      assert (parameters.length == 1);
      Object p = parameters[0];
      if (p != null) {
        StdAgg myagg = (StdAgg) agg;
        try {
          double v = PrimitiveObjectInspectorUtils.getDouble(p, inputOI);
          double temp_count = myagg.count;
          myagg.count++;
          double n = myagg.count;
          double delta = v - myagg.mean;
          double delta_n = delta/myagg.count;
          double delta_n2 = delta_n*delta_n;
          double term1 = delta*delta_n*temp_count;
          myagg.mean += delta_n;
          myagg.M4 = myagg.M4 + (term1*delta_n2*(n*n-3*n+3)) + (6*delta_n2*myagg.M2) - (4*delta_n*myagg.M3);
          myagg.M3 = myagg.M3 + (term1*delta_n*(n-2)) -(3*delta_n*myagg.M2);
          myagg.M2 = myagg.M2 + term1;
          myagg.sum += v;
          if(myagg.count > 1) {
            double t = myagg.count*v - myagg.sum;
            myagg.variance += (t*t) / ((double)myagg.count*(myagg.count-1));
          }
        } catch (NumberFormatException e) {
          if (!warned) {
            warned = true;
            LOG.warn(getClass().getSimpleName() + " "
                + StringUtils.stringifyException(e));
            LOG.warn(getClass().getSimpleName()
                + " ignoring similar exceptions.");
          }
        }
      }
    }

    @Override
    public Object terminatePartial(AggregationBuffer agg) throws HiveException {
      StdAgg myagg = (StdAgg) agg;
      ((LongWritable) partialResult[0]).set(myagg.count);
      ((DoubleWritable) partialResult[1]).set(myagg.sum);
      ((DoubleWritable) partialResult[2]).set(myagg.variance);
      ((DoubleWritable) partialResult[3]).set(myagg.mean);
      ((DoubleWritable) partialResult[4]).set(myagg.M2);
      ((DoubleWritable) partialResult[5]).set(myagg.M3);
      ((DoubleWritable) partialResult[6]).set(myagg.M4);
      return partialResult;
    }

    @Override
    public void merge(AggregationBuffer agg, Object partial) throws HiveException {
      if (partial != null) {
        StdAgg myagg = (StdAgg) agg;

        Object partialCount = soi.getStructFieldData(partial, countField);
        Object partialSum = soi.getStructFieldData(partial, sumField);
        Object partialVariance = soi.getStructFieldData(partial, varianceField);
        Object partialMean = soi.getStructFieldData(partial, meanField);
        Object partialM2 = soi.getStructFieldData(partial, M2Field);
        Object partialM3 = soi.getStructFieldData(partial, M3Field);
        Object partialM4 = soi.getStructFieldData(partial, M4Field);

        long n = myagg.count;
        long m = countFieldOI.get(partialCount);

        if (n == 0) {
          // Just copy the information since there is nothing so far
          myagg.variance = sumFieldOI.get(partialVariance);
          myagg.count = countFieldOI.get(partialCount);
          myagg.sum = sumFieldOI.get(partialSum);
          myagg.mean = meanFieldOI.get(partialMean); 
          myagg.M2 = M2FieldOI.get(partialM2); 
          myagg.M3 = M3FieldOI.get(partialM3); 
          myagg.M4 = M4FieldOI.get(partialM4); 
        }

        if (m != 0 && n != 0) {
          // Merge the two partials

          double a = myagg.sum;
          double b = sumFieldOI.get(partialSum);

          double _M2 = M2FieldOI.get(partialM2);
          double _M3 = M3FieldOI.get(partialM3);
          double _M4 = M4FieldOI.get(partialM4);
          
          myagg.count += m;
          myagg.sum += b;
          double t = (m/(double)n)*a - b;
          myagg.variance += sumFieldOI.get(partialVariance) + ((n/(double)m)/((double)n+m)) * t * t;
          double delta = meanFieldOI.get(partialMean) - myagg.mean;
          double M2_temp  = myagg.M2 + M2FieldOI.get(partialM2) + ((delta*delta*n*m)/(n+m));
          myagg.mean = (myagg.mean*n + meanFieldOI.get(partialMean)*m)/(n+m);
          double M3_temp = myagg.M3 + _M3 + (((delta*delta*delta)*n*m*(n-m))/((n+m)*(n+m))) + (3*delta*(n*_M2-m*myagg.M2)/(n+m));
          double M4_temp = myagg.M4 + _M4 + (Math.pow(delta,4.0)*n*m*(n*n-n*m+m*m)/(Math.pow(n+m,3.0))) + 
          			 (6*delta*delta*(n*n*_M2+m*m*myagg.M2)/(Math.pow(n+m, 2))) + (4*delta*(n*_M3-m*myagg.M3)/(n+m));
          myagg.M2 = M2_temp;
          myagg.M3 = M3_temp;
          myagg.M4 = M4_temp;
        }
      }
    }

    @Override
    public Object terminate(AggregationBuffer agg) throws HiveException {
      /*      
       *      StdAgg myagg = (StdAgg) agg;

     if (myagg.count == 0) { // SQL standard - return null for zero elements
       return null;
     } else {
       result.add(new DoubleWritable(myagg.sum / myagg.count));
       result.add(new DoubleWritable(1.96*Math.sqrt(myagg.variance)));
       result.add(new DoubleWritable(95.0));
       return result;
     }

       */
      StdAgg myagg = (StdAgg) agg;

      if (myagg.count == 0) { // SQL standard - return null for zero elements
        return null;
      } else {
        if (myagg.count > 1) {
          double kurtosis = (myagg.count * myagg.M4 / (myagg.M2 * myagg.M2)) - 3.0;
          double n = myagg.count;
          double var = myagg.variance / (myagg.count);
          double varOfVar = (((n-1) * (n-1) * kurtosis) - (n - 3) * (var * var)) / (n*n*n);
          result.add(new DoubleWritable(myagg.variance / (myagg.count)));
          result.add(new DoubleWritable(1.96*Math.sqrt(Math.abs(varOfVar))));
          result.add(new DoubleWritable(95.0));
          result.add(new DoubleWritable(myagg.mean));
          result.add(new DoubleWritable(myagg.M2));
          result.add(new DoubleWritable(myagg.M3));
          result.add(new DoubleWritable(myagg.M4));
        } else { // for one element the variance is always 0
        	result.add(new DoubleWritable(0.0));
            result.add(new DoubleWritable(0.0));
            result.add(new DoubleWritable(0.0));
            return result;
        }
        return getResult();
      }
    }

    public void setResult(ArrayList<DoubleWritable> result) {
      this.result = result;
    }

    public ArrayList<DoubleWritable> getResult() {
      return result;
    }
  } 
 }
