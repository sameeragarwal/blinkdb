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

package org.apache.commons.math.estimation;

import java.util.Arrays;

import org.apache.commons.math.estimation.EstimatedParameter;
import org.apache.commons.math.estimation.EstimationException;
import org.apache.commons.math.estimation.EstimationProblem;
import org.apache.commons.math.estimation.LevenbergMarquardtEstimator;
import org.apache.commons.math.estimation.WeightedMeasurement;
import org.apache.commons.math.util.FastMath;

import junit.framework.*;

/**
 * <p>Some of the unit tests are re-implementations of the MINPACK <a
 * href="http://www.netlib.org/minpack/ex/file17">file17</a> and <a
 * href="http://www.netlib.org/minpack/ex/file22">file22</a> test files.
 * The redistribution policy for MINPACK is available <a
 * href="http://www.netlib.org/minpack/disclaimer">here</a>, for
 * convenience, it is reproduced below.</p>

 * <table border="0" width="80%" cellpadding="10" align="center" bgcolor="#E0E0E0">
 * <tr><td>
 *    Minpack Copyright Notice (1999) University of Chicago.
 *    All rights reserved
 * </td></tr>
 * <tr><td>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * <ol>
 *  <li>Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer.</li>
 * <li>Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions and the following
 *     disclaimer in the documentation and/or other materials provided
 *     with the distribution.</li>
 * <li>The end-user documentation included with the redistribution, if any,
 *     must include the following acknowledgment:
 *     <code>This product includes software developed by the University of
 *           Chicago, as Operator of Argonne National Laboratory.</code>
 *     Alternately, this acknowledgment may appear in the software itself,
 *     if and wherever such third-party acknowledgments normally appear.</li>
 * <li><strong>WARRANTY DISCLAIMER. THE SOFTWARE IS SUPPLIED "AS IS"
 *     WITHOUT WARRANTY OF ANY KIND. THE COPYRIGHT HOLDER, THE
 *     UNITED STATES, THE UNITED STATES DEPARTMENT OF ENERGY, AND
 *     THEIR EMPLOYEES: (1) DISCLAIM ANY WARRANTIES, EXPRESS OR
 *     IMPLIED, INCLUDING BUT NOT LIMITED TO ANY IMPLIED WARRANTIES
 *     OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, TITLE
 *     OR NON-INFRINGEMENT, (2) DO NOT ASSUME ANY LEGAL LIABILITY
 *     OR RESPONSIBILITY FOR THE ACCURACY, COMPLETENESS, OR
 *     USEFULNESS OF THE SOFTWARE, (3) DO NOT REPRESENT THAT USE OF
 *     THE SOFTWARE WOULD NOT INFRINGE PRIVATELY OWNED RIGHTS, (4)
 *     DO NOT WARRANT THAT THE SOFTWARE WILL FUNCTION
 *     UNINTERRUPTED, THAT IT IS ERROR-FREE OR THAT ANY ERRORS WILL
 *     BE CORRECTED.</strong></li>
 * <li><strong>LIMITATION OF LIABILITY. IN NO EVENT WILL THE COPYRIGHT
 *     HOLDER, THE UNITED STATES, THE UNITED STATES DEPARTMENT OF
 *     ENERGY, OR THEIR EMPLOYEES: BE LIABLE FOR ANY INDIRECT,
 *     INCIDENTAL, CONSEQUENTIAL, SPECIAL OR PUNITIVE DAMAGES OF
 *     ANY KIND OR NATURE, INCLUDING BUT NOT LIMITED TO LOSS OF
 *     PROFITS OR LOSS OF DATA, FOR ANY REASON WHATSOEVER, WHETHER
 *     SUCH LIABILITY IS ASSERTED ON THE BASIS OF CONTRACT, TORT
 *     (INCLUDING NEGLIGENCE OR STRICT LIABILITY), OR OTHERWISE,
 *     EVEN IF ANY OF SAID PARTIES HAS BEEN WARNED OF THE
 *     POSSIBILITY OF SUCH LOSS OR DAMAGES.</strong></li>
 * <ol></td></tr>
 * </table>

 * @author Argonne National Laboratory. MINPACK project. March 1980 (original fortran minpack tests)
 * @author Burton S. Garbow (original fortran minpack tests)
 * @author Kenneth E. Hillstrom (original fortran minpack tests)
 * @author Jorge J. More (original fortran minpack tests)
 * @author Luc Maisonobe (non-minpack tests and minpack tests Java translation)
 */
@Deprecated
public class MinpackTest
  extends TestCase {

  public MinpackTest(String name) {
    super(name);
  }

  public void testMinpackLinearFullRank() {
    minpackTest(new LinearFullRankFunction(10, 5, 1.0,
                                           5.0, 2.23606797749979), false);
    minpackTest(new LinearFullRankFunction(50, 5, 1.0,
                                           8.06225774829855, 6.70820393249937), false);
  }

  public void testMinpackLinearRank1() {
    minpackTest(new LinearRank1Function(10, 5, 1.0,
                                        291.521868819476, 1.4638501094228), false);
    minpackTest(new LinearRank1Function(50, 5, 1.0,
                                        3101.60039334535, 3.48263016573496), false);
  }

  public void testMinpackLinearRank1ZeroColsAndRows() {
    minpackTest(new LinearRank1ZeroColsAndRowsFunction(10, 5, 1.0), false);
    minpackTest(new LinearRank1ZeroColsAndRowsFunction(50, 5, 1.0), false);
  }

  public void testMinpackRosenbrok() {
    minpackTest(new RosenbrockFunction(new double[] { -1.2, 1.0 },
                                       FastMath.sqrt(24.2)), false);
    minpackTest(new RosenbrockFunction(new double[] { -12.0, 10.0 },
                                       FastMath.sqrt(1795769.0)), false);
    minpackTest(new RosenbrockFunction(new double[] { -120.0, 100.0 },
                                       11.0 * FastMath.sqrt(169000121.0)), false);
  }

  public void testMinpackHelicalValley() {
    minpackTest(new HelicalValleyFunction(new double[] { -1.0, 0.0, 0.0 },
                                          50.0), false);
    minpackTest(new HelicalValleyFunction(new double[] { -10.0, 0.0, 0.0 },
                                          102.95630140987), false);
    minpackTest(new HelicalValleyFunction(new double[] { -100.0, 0.0, 0.0},
                                          991.261822123701), false);
  }

  public void testMinpackPowellSingular() {
    minpackTest(new PowellSingularFunction(new double[] { 3.0, -1.0, 0.0, 1.0 },
                                           14.6628782986152), false);
    minpackTest(new PowellSingularFunction(new double[] { 30.0, -10.0, 0.0, 10.0 },
                                           1270.9838708654), false);
    minpackTest(new PowellSingularFunction(new double[] { 300.0, -100.0, 0.0, 100.0 },
                                           126887.903284750), false);
  }

  public void testMinpackFreudensteinRoth() {
    minpackTest(new FreudensteinRothFunction(new double[] { 0.5, -2.0 },
                                             20.0124960961895, 6.99887517584575,
                                             new double[] {
                                               11.4124844654993,
                                               -0.896827913731509
                                             }), false);
    minpackTest(new FreudensteinRothFunction(new double[] { 5.0, -20.0 },
                                             12432.833948863, 6.9988751744895,
                                             new double[] {
                                               11.4130046614746,
                                               -0.896796038685958
                                             }), false);
    minpackTest(new FreudensteinRothFunction(new double[] { 50.0, -200.0 },
                                             11426454.595762, 6.99887517242903,
                                             new double[] {
                                               11.4127817857886,
                                               -0.89680510749204
                                             }), false);
  }

  public void testMinpackBard() {
    minpackTest(new BardFunction(1.0, 6.45613629515967, 0.0906359603390466,
                                 new double[] {
                                   0.0824105765758334,
                                   1.1330366534715,
                                   2.34369463894115
                                 }), false);
    minpackTest(new BardFunction(10.0, 36.1418531596785, 4.17476870138539,
                                 new double[] {
                                   0.840666673818329,
                                   -158848033.259565,
                                   -164378671.653535
                                 }), false);
    minpackTest(new BardFunction(100.0, 384.114678637399, 4.17476870135969,
                                 new double[] {
                                   0.840666673867645,
                                   -158946167.205518,
                                   -164464906.857771
                                 }), false);
  }

  public void testMinpackKowalikOsborne() {
    minpackTest(new KowalikOsborneFunction(new double[] { 0.25, 0.39, 0.415, 0.39 },
                                           0.0728915102882945,
                                           0.017535837721129,
                                           new double[] {
                                             0.192807810476249,
                                             0.191262653354071,
                                             0.123052801046931,
                                             0.136053221150517
                                           }), false);
    minpackTest(new KowalikOsborneFunction(new double[] { 2.5, 3.9, 4.15, 3.9 },
                                           2.97937007555202,
                                           0.032052192917937,
                                           new double[] {
                                             728675.473768287,
                                             -14.0758803129393,
                                             -32977797.7841797,
                                             -20571594.1977912
                                           }), false);
    minpackTest(new KowalikOsborneFunction(new double[] { 25.0, 39.0, 41.5, 39.0 },
                                           29.9590617016037,
                                           0.0175364017658228,
                                           new double[] {
                                             0.192948328597594,
                                             0.188053165007911,
                                             0.122430604321144,
                                             0.134575665392506
                                           }), true);
  }

  public void testMinpackMeyer() {
    minpackTest(new MeyerFunction(new double[] { 0.02, 4000.0, 250.0 },
                                  41153.4665543031, 9.37794514651874,
                                  new double[] {
                                    0.00560963647102661,
                                    6181.34634628659,
                                    345.223634624144
                                  }), false);
    minpackTest(new MeyerFunction(new double[] { 0.2, 40000.0, 2500.0 },
                                  4168216.89130846, 792.917871779501,
                                  new double[] {
                                    1.42367074157994e-11,
                                    33695.7133432541,
                                    901.268527953801
                                  }), true);
  }

  public void testMinpackWatson() {

    minpackTest(new WatsonFunction(6, 0.0,
                                   5.47722557505166, 0.0478295939097601,
                                   new double[] {
                                     -0.0157249615083782, 1.01243488232965,
                                     -0.232991722387673,  1.26043101102818,
                                     -1.51373031394421,   0.99299727291842
                                   }), false);
    minpackTest(new WatsonFunction(6, 10.0,
                                   6433.12578950026, 0.0478295939096951,
                                   new double[] {
                                     -0.0157251901386677, 1.01243485860105,
                                     -0.232991545843829,  1.26042932089163,
                                     -1.51372776706575,   0.99299573426328
                                   }), false);
    minpackTest(new WatsonFunction(6, 100.0,
                                   674256.040605213, 0.047829593911544,
                                   new double[] {
                                    -0.0157247019712586, 1.01243490925658,
                                    -0.232991922761641,  1.26043292929555,
                                    -1.51373320452707,   0.99299901922322
                                   }), false);

    minpackTest(new WatsonFunction(9, 0.0,
                                   5.47722557505166, 0.00118311459212420,
                                   new double[] {
                                    -0.153070644166722e-4, 0.999789703934597,
                                     0.0147639634910978,   0.146342330145992,
                                     1.00082109454817,    -2.61773112070507,
                                     4.10440313943354,    -3.14361226236241,
                                     1.05262640378759
                                   }), false);
    minpackTest(new WatsonFunction(9, 10.0,
                                   12088.127069307, 0.00118311459212513,
                                   new double[] {
                                   -0.153071334849279e-4, 0.999789703941234,
                                    0.0147639629786217,   0.146342334818836,
                                    1.00082107321386,    -2.61773107084722,
                                    4.10440307655564,    -3.14361222178686,
                                    1.05262639322589
                                   }), false);
    minpackTest(new WatsonFunction(9, 100.0,
                                   1269109.29043834, 0.00118311459212384,
                                   new double[] {
                                    -0.153069523352176e-4, 0.999789703958371,
                                     0.0147639625185392,   0.146342341096326,
                                     1.00082104729164,    -2.61773101573645,
                                     4.10440301427286,    -3.14361218602503,
                                     1.05262638516774
                                   }), false);

    minpackTest(new WatsonFunction(12, 0.0,
                                   5.47722557505166, 0.217310402535861e-4,
                                   new double[] {
                                    -0.660266001396382e-8, 1.00000164411833,
                                    -0.000563932146980154, 0.347820540050756,
                                    -0.156731500244233,    1.05281515825593,
                                    -3.24727109519451,     7.2884347837505,
                                   -10.271848098614,       9.07411353715783,
                                    -4.54137541918194,     1.01201187975044
                                   }), false);
    minpackTest(new WatsonFunction(12, 10.0,
                                   19220.7589790951, 0.217310402518509e-4,
                                   new double[] {
                                    -0.663710223017410e-8, 1.00000164411787,
                                    -0.000563932208347327, 0.347820540486998,
                                    -0.156731503955652,    1.05281517654573,
                                    -3.2472711515214,      7.28843489430665,
                                   -10.2718482369638,      9.07411364383733,
                                    -4.54137546533666,     1.01201188830857
                                   }), false);
    minpackTest(new WatsonFunction(12, 100.0,
                                   2018918.04462367, 0.217310402539845e-4,
                                   new double[] {
                                    -0.663806046485249e-8, 1.00000164411786,
                                    -0.000563932210324959, 0.347820540503588,
                                    -0.156731504091375,    1.05281517718031,
                                    -3.24727115337025,     7.28843489775302,
                                   -10.2718482410813,      9.07411364688464,
                                    -4.54137546660822,     1.0120118885369
                                   }), false);

  }

  public void testMinpackBox3Dimensional() {
    minpackTest(new Box3DimensionalFunction(10, new double[] { 0.0, 10.0, 20.0 },
                                            32.1115837449572), false);
  }

  public void testMinpackJennrichSampson() {
    minpackTest(new JennrichSampsonFunction(10, new double[] { 0.3, 0.4 },
                                            64.5856498144943, 11.1517793413499,
                                            new double[] {
                                             0.257819926636811, 0.257829976764542
                                            }), false);
  }

  public void testMinpackBrownDennis() {
    minpackTest(new BrownDennisFunction(20,
                                        new double[] { 25.0, 5.0, -5.0, -1.0 },
                                        2815.43839161816, 292.954288244866,
                                        new double[] {
                                         -11.59125141003, 13.2024883984741,
                                         -0.403574643314272, 0.236736269844604
                                        }), false);
    minpackTest(new BrownDennisFunction(20,
                                        new double[] { 250.0, 50.0, -50.0, -10.0 },
                                        555073.354173069, 292.954270581415,
                                        new double[] {
                                         -11.5959274272203, 13.2041866926242,
                                         -0.403417362841545, 0.236771143410386
                                       }), false);
    minpackTest(new BrownDennisFunction(20,
                                        new double[] { 2500.0, 500.0, -500.0, -100.0 },
                                        61211252.2338581, 292.954306151134,
                                        new double[] {
                                         -11.5902596937374, 13.2020628854665,
                                         -0.403688070279258, 0.236665033746463
                                        }), false);
  }

  public void testMinpackChebyquad() {
    minpackTest(new ChebyquadFunction(1, 8, 1.0,
                                      1.88623796907732, 1.88623796907732,
                                      new double[] { 0.5 }), false);
    minpackTest(new ChebyquadFunction(1, 8, 10.0,
                                      5383344372.34005, 1.88424820499951,
                                      new double[] { 0.9817314924684 }), false);
    minpackTest(new ChebyquadFunction(1, 8, 100.0,
                                      0.118088726698392e19, 1.88424820499347,
                                      new double[] { 0.9817314852934 }), false);
    minpackTest(new ChebyquadFunction(8, 8, 1.0,
                                      0.196513862833975, 0.0593032355046727,
                                      new double[] {
                                        0.0431536648587336, 0.193091637843267,
                                        0.266328593812698,  0.499999334628884,
                                        0.500000665371116,  0.733671406187302,
                                        0.806908362156733,  0.956846335141266
                                      }), false);
    minpackTest(new ChebyquadFunction(9, 9, 1.0,
                                      0.16994993465202, 0.0,
                                      new double[] {
                                        0.0442053461357828, 0.199490672309881,
                                        0.23561910847106,   0.416046907892598,
                                        0.5,                0.583953092107402,
                                        0.764380891528940,  0.800509327690119,
                                        0.955794653864217
                                      }), false);
    minpackTest(new ChebyquadFunction(10, 10, 1.0,
                                      0.183747831178711, 0.0806471004038253,
                                      new double[] {
                                        0.0596202671753563, 0.166708783805937,
                                        0.239171018813509,  0.398885290346268,
                                        0.398883667870681,  0.601116332129320,
                                        0.60111470965373,   0.760828981186491,
                                        0.833291216194063,  0.940379732824644
                                      }), false);
  }

  public void testMinpackBrownAlmostLinear() {
    minpackTest(new BrownAlmostLinearFunction(10, 0.5,
                                              16.5302162063499, 0.0,
                                              new double[] {
                                                0.979430303349862, 0.979430303349862,
                                                0.979430303349862, 0.979430303349862,
                                                0.979430303349862, 0.979430303349862,
                                                0.979430303349862, 0.979430303349862,
                                                0.979430303349862, 1.20569696650138
                                              }), false);
    minpackTest(new BrownAlmostLinearFunction(10, 5.0,
                                              9765624.00089211, 0.0,
                                              new double[] {
                                               0.979430303349865, 0.979430303349865,
                                               0.979430303349865, 0.979430303349865,
                                               0.979430303349865, 0.979430303349865,
                                               0.979430303349865, 0.979430303349865,
                                               0.979430303349865, 1.20569696650135
                                              }), false);
    minpackTest(new BrownAlmostLinearFunction(10, 50.0,
                                              0.9765625e17, 0.0,
                                              new double[] {
                                                1.0, 1.0, 1.0, 1.0, 1.0,
                                                1.0, 1.0, 1.0, 1.0, 1.0
                                              }), false);
    minpackTest(new BrownAlmostLinearFunction(30, 0.5,
                                              83.476044467848, 0.0,
                                              new double[] {
                                                0.997754216442807, 0.997754216442807,
                                                0.997754216442807, 0.997754216442807,
                                                0.997754216442807, 0.997754216442807,
                                                0.997754216442807, 0.997754216442807,
                                                0.997754216442807, 0.997754216442807,
                                                0.997754216442807, 0.997754216442807,
                                                0.997754216442807, 0.997754216442807,
                                                0.997754216442807, 0.997754216442807,
                                                0.997754216442807, 0.997754216442807,
                                                0.997754216442807, 0.997754216442807,
                                                0.997754216442807, 0.997754216442807,
                                                0.997754216442807, 0.997754216442807,
                                                0.997754216442807, 0.997754216442807,
                                                0.997754216442807, 0.997754216442807,
                                                0.997754216442807, 1.06737350671578
                                              }), false);
    minpackTest(new BrownAlmostLinearFunction(40, 0.5,
                                              128.026364472323, 0.0,
                                              new double[] {
                                                1.00000000000002, 1.00000000000002,
                                                1.00000000000002, 1.00000000000002,
                                                1.00000000000002, 1.00000000000002,
                                                1.00000000000002, 1.00000000000002,
                                                1.00000000000002, 1.00000000000002,
                                                1.00000000000002, 1.00000000000002,
                                                1.00000000000002, 1.00000000000002,
                                                1.00000000000002, 1.00000000000002,
                                                1.00000000000002, 1.00000000000002,
                                                1.00000000000002, 1.00000000000002,
                                                1.00000000000002, 1.00000000000002,
                                                1.00000000000002, 1.00000000000002,
                                                1.00000000000002, 1.00000000000002,
                                                1.00000000000002, 1.00000000000002,
                                                1.00000000000002, 1.00000000000002,
                                                1.00000000000002, 1.00000000000002,
                                                1.00000000000002, 1.00000000000002,
                                                0.999999999999121
                                              }), false);
    }

  public void testMinpackOsborne1() {
      minpackTest(new Osborne1Function(new double[] { 0.5, 1.5, -1.0, 0.01, 0.02, },
                                       0.937564021037838, 0.00739249260904843,
                                       new double[] {
                                         0.375410049244025, 1.93584654543108,
                                        -1.46468676748716, 0.0128675339110439,
                                         0.0221227011813076
                                       }), false);
    }

  public void testMinpackOsborne2() {

    minpackTest(new Osborne2Function(new double[] {
                                       1.3, 0.65, 0.65, 0.7, 0.6,
                                       3.0, 5.0, 7.0, 2.0, 4.5, 5.5
                                     },
                                     1.44686540984712, 0.20034404483314,
                                     new double[] {
                                       1.30997663810096,  0.43155248076,
                                       0.633661261602859, 0.599428560991695,
                                       0.754179768272449, 0.904300082378518,
                                       1.36579949521007, 4.82373199748107,
                                       2.39868475104871, 4.56887554791452,
                                       5.67534206273052
                                     }), false);
  }

  private void minpackTest(MinpackFunction function, boolean exceptionExpected) {
    LevenbergMarquardtEstimator estimator = new LevenbergMarquardtEstimator();
    estimator.setMaxCostEval(100 * (function.getN() + 1));
    estimator.setCostRelativeTolerance(FastMath.sqrt(2.22044604926e-16));
    estimator.setParRelativeTolerance(FastMath.sqrt(2.22044604926e-16));
    estimator.setOrthoTolerance(2.22044604926e-16);
    assertTrue(function.checkTheoreticalStartCost(estimator.getRMS(function)));
    try {
      estimator.estimate(function);
      assertFalse(exceptionExpected);
    } catch (EstimationException lsse) {
      assertTrue(exceptionExpected);
    }
    assertTrue(function.checkTheoreticalMinCost(estimator.getRMS(function)));
    assertTrue(function.checkTheoreticalMinParams());
  }

  private static abstract class MinpackFunction implements EstimationProblem {

    protected MinpackFunction(int m,
                              double[] startParams,
                              double   theoreticalStartCost,
                              double   theoreticalMinCost,
                              double[] theoreticalMinParams) {
      this.m = m;
      this.n = startParams.length;
      parameters = new EstimatedParameter[n];
      for (int i = 0; i < n; ++i) {
        parameters[i] = new EstimatedParameter("p" + i, startParams[i]);
      }
      this.theoreticalStartCost = theoreticalStartCost;
      this.theoreticalMinCost   = theoreticalMinCost;
      this.theoreticalMinParams = theoreticalMinParams;
      this.costAccuracy         = 1.0e-8;
      this.paramsAccuracy       = 1.0e-5;
    }

    protected static double[] buildArray(int n, double x) {
      double[] array = new double[n];
      Arrays.fill(array, x);
      return array;
    }

    protected void setCostAccuracy(double costAccuracy) {
      this.costAccuracy = costAccuracy;
    }

    protected void setParamsAccuracy(double paramsAccuracy) {
      this.paramsAccuracy = paramsAccuracy;
    }

    public int getN() {
      return parameters.length;
    }

    public boolean checkTheoreticalStartCost(double rms) {
      double threshold = costAccuracy * (1.0 + theoreticalStartCost);
      return FastMath.abs(FastMath.sqrt(m) * rms - theoreticalStartCost) <= threshold;
    }

    public boolean checkTheoreticalMinCost(double rms) {
      double threshold = costAccuracy * (1.0 + theoreticalMinCost);
     return FastMath.abs(FastMath.sqrt(m) * rms - theoreticalMinCost) <= threshold;
    }

    public boolean checkTheoreticalMinParams() {
      if (theoreticalMinParams != null) {
        for (int i = 0; i < theoreticalMinParams.length; ++i) {
          double mi = theoreticalMinParams[i];
          double vi = parameters[i].getEstimate();
          if (FastMath.abs(mi - vi) > (paramsAccuracy * (1.0 + FastMath.abs(mi)))) {
            return false;
          }
        }
      }
      return true;
    }

    public WeightedMeasurement[] getMeasurements() {
      WeightedMeasurement[] measurements = new WeightedMeasurement[m];
      for (int i = 0; i < m; ++i) {
        measurements[i] = new MinpackMeasurement(this, i);
      }
      return measurements;
    }

    public EstimatedParameter[] getUnboundParameters() {
      return parameters;
    }

    public EstimatedParameter[] getAllParameters() {
      return parameters;
    }

    protected abstract double[][] getJacobian();

    protected abstract double[] getResiduals();

    private static class MinpackMeasurement extends WeightedMeasurement {

      public MinpackMeasurement(MinpackFunction f, int index) {
        super(1.0, 0.0);
        this.index = index;
        this.f = f;
      }

      @Override
      public double getTheoreticalValue() {
        // this is obviously NOT efficient as we recompute the whole vector
        // each time we need only one element, but it is only for test
        // purposes and is simpler to check.
        // This implementation should NOT be taken as an example, it is ugly!
        return f.getResiduals()[index];
      }

      @Override
      public double getPartial(EstimatedParameter parameter) {
        // this is obviously NOT efficient as we recompute the whole jacobian
        // each time we need only one element, but it is only for test
        // purposes and is simpler to check.
        // This implementation should NOT be taken as an example, it is ugly!
        for (int j = 0; j < f.n; ++j) {
          if (parameter == f.parameters[j]) {
            return f.getJacobian()[index][j];
          }
        }
        return 0;
      }

      private int index;
      private transient final MinpackFunction f;
      private static final long serialVersionUID = 1L;

    }

    protected int                  n;
    protected int                  m;
    protected EstimatedParameter[] parameters;
    protected double               theoreticalStartCost;
    protected double               theoreticalMinCost;
    protected double[]             theoreticalMinParams;
    protected double               costAccuracy;
    protected double               paramsAccuracy;

  }

  private static class LinearFullRankFunction extends MinpackFunction {

    public LinearFullRankFunction(int m, int n, double x0,
                                  double theoreticalStartCost,
                                  double theoreticalMinCost) {
      super(m, buildArray(n, x0), theoreticalStartCost,
            theoreticalMinCost, buildArray(n, -1.0));
    }

    @Override
    protected double[][] getJacobian() {
      double t = 2.0 / m;
      double[][] jacobian = new double[m][];
      for (int i = 0; i < m; ++i) {
        jacobian[i] = new double[n];
        for (int j = 0; j < n; ++j) {
          jacobian[i][j] = (i == j) ? (1 - t) : -t;
        }
      }
      return jacobian;
    }

    @Override
    protected double[] getResiduals() {
      double sum = 0;
      for (int i = 0; i < n; ++i) {
        sum += parameters[i].getEstimate();
      }
      double t  = 1 + 2 * sum / m;
      double[] f = new double[m];
      for (int i = 0; i < n; ++i) {
        f[i] = parameters[i].getEstimate() - t;
      }
      Arrays.fill(f, n, m, -t);
      return f;
    }

  }

  private static class LinearRank1Function extends MinpackFunction {

    public LinearRank1Function(int m, int n, double x0,
                                  double theoreticalStartCost,
                                  double theoreticalMinCost) {
      super(m, buildArray(n, x0), theoreticalStartCost, theoreticalMinCost, null);
    }

    @Override
    protected double[][] getJacobian() {
      double[][] jacobian = new double[m][];
      for (int i = 0; i < m; ++i) {
        jacobian[i] = new double[n];
        for (int j = 0; j < n; ++j) {
          jacobian[i][j] = (i + 1) * (j + 1);
        }
      }
      return jacobian;
    }

    @Override
    protected double[] getResiduals() {
      double[] f = new double[m];
      double sum = 0;
      for (int i = 0; i < n; ++i) {
        sum += (i + 1) * parameters[i].getEstimate();
      }
      for (int i = 0; i < m; ++i) {
        f[i] = (i + 1) * sum - 1;
      }
      return f;
    }

  }

  private static class LinearRank1ZeroColsAndRowsFunction extends MinpackFunction {

    public LinearRank1ZeroColsAndRowsFunction(int m, int n, double x0) {
      super(m, buildArray(n, x0),
            FastMath.sqrt(m + (n+1)*(n-2)*(m-2)*(m-1) * ((n+1)*(n-2)*(2*m-3) - 12) / 24.0),
            FastMath.sqrt((m * (m + 3) - 6) / (2.0 * (2 * m - 3))),
            null);
    }

    @Override
    protected double[][] getJacobian() {
      double[][] jacobian = new double[m][];
      for (int i = 0; i < m; ++i) {
        jacobian[i] = new double[n];
        jacobian[i][0] = 0;
        for (int j = 1; j < (n - 1); ++j) {
          if (i == 0) {
            jacobian[i][j] = 0;
          } else if (i != (m - 1)) {
            jacobian[i][j] = i * (j + 1);
          } else {
            jacobian[i][j] = 0;
          }
        }
        jacobian[i][n - 1] = 0;
      }
      return jacobian;
    }

    @Override
    protected double[] getResiduals() {
      double[] f = new double[m];
      double sum = 0;
      for (int i = 1; i < (n - 1); ++i) {
        sum += (i + 1) * parameters[i].getEstimate();
      }
      for (int i = 0; i < (m - 1); ++i) {
        f[i] = i * sum - 1;
      }
      f[m - 1] = -1;
      return f;
    }

  }

  private static class RosenbrockFunction extends MinpackFunction {

    public RosenbrockFunction(double[] startParams, double theoreticalStartCost) {
      super(2, startParams, theoreticalStartCost, 0.0, buildArray(2, 1.0));
    }

    @Override
    protected double[][] getJacobian() {
      double x1 = parameters[0].getEstimate();
      return new double[][] { { -20 * x1, 10 }, { -1, 0 } };
    }

    @Override
    protected double[] getResiduals() {
      double x1 = parameters[0].getEstimate();
      double x2 = parameters[1].getEstimate();
      return new double[] { 10 * (x2 - x1 * x1), 1 - x1 };
    }

  }

  private static class HelicalValleyFunction extends MinpackFunction {

    public HelicalValleyFunction(double[] startParams,
                                 double theoreticalStartCost) {
      super(3, startParams, theoreticalStartCost, 0.0,
            new double[] { 1.0, 0.0, 0.0 });
    }

    @Override
    protected double[][] getJacobian() {
      double x1 = parameters[0].getEstimate();
      double x2 = parameters[1].getEstimate();
      double tmpSquare = x1 * x1 + x2 * x2;
      double tmp1 = twoPi * tmpSquare;
      double tmp2 = FastMath.sqrt(tmpSquare);
      return new double[][] {
        {  100 * x2 / tmp1, -100 * x1 / tmp1, 10 },
        { 10 * x1 / tmp2, 10 * x2 / tmp2, 0 },
        { 0, 0, 1 }
      };
    }

    @Override
    protected double[] getResiduals() {
      double x1 = parameters[0].getEstimate();
      double x2 = parameters[1].getEstimate();
      double x3 = parameters[2].getEstimate();
      double tmp1;
      if (x1 == 0) {
        tmp1 = (x2 >= 0) ? 0.25 : -0.25;
      } else {
        tmp1 = FastMath.atan(x2 / x1) / twoPi;
        if (x1 < 0) {
          tmp1 += 0.5;
        }
      }
      double tmp2 = FastMath.sqrt(x1 * x1 + x2 * x2);
      return new double[] {
        10.0 * (x3 - 10 * tmp1),
        10.0 * (tmp2 - 1),
        x3
      };
    }

    private static final double twoPi = 2.0 * FastMath.PI;

  }

  private static class PowellSingularFunction extends MinpackFunction {

    public PowellSingularFunction(double[] startParams,
                                  double theoreticalStartCost) {
      super(4, startParams, theoreticalStartCost, 0.0, buildArray(4, 0.0));
    }

    @Override
    protected double[][] getJacobian() {
      double x1 = parameters[0].getEstimate();
      double x2 = parameters[1].getEstimate();
      double x3 = parameters[2].getEstimate();
      double x4 = parameters[3].getEstimate();
      return new double[][] {
        { 1, 10, 0, 0 },
        { 0, 0, sqrt5, -sqrt5 },
        { 0, 2 * (x2 - 2 * x3), -4 * (x2 - 2 * x3), 0 },
        { 2 * sqrt10 * (x1 - x4), 0, 0, -2 * sqrt10 * (x1 - x4) }
      };
    }

    @Override
    protected double[] getResiduals() {
      double x1 = parameters[0].getEstimate();
      double x2 = parameters[1].getEstimate();
      double x3 = parameters[2].getEstimate();
      double x4 = parameters[3].getEstimate();
      return new double[] {
        x1 + 10 * x2,
        sqrt5 * (x3 - x4),
        (x2 - 2 * x3) * (x2 - 2 * x3),
        sqrt10 * (x1 - x4) * (x1 - x4)
      };
    }

    private static final double sqrt5  = FastMath.sqrt( 5.0);
    private static final double sqrt10 = FastMath.sqrt(10.0);

  }

  private static class FreudensteinRothFunction extends MinpackFunction {

    public FreudensteinRothFunction(double[] startParams,
                                    double theoreticalStartCost,
                                    double theoreticalMinCost,
                                    double[] theoreticalMinParams) {
      super(2, startParams, theoreticalStartCost,
            theoreticalMinCost, theoreticalMinParams);
    }

    @Override
    protected double[][] getJacobian() {
      double x2 = parameters[1].getEstimate();
      return new double[][] {
        { 1, x2 * (10 - 3 * x2) -  2 },
        { 1, x2 * ( 2 + 3 * x2) - 14, }
      };
    }

    @Override
    protected double[] getResiduals() {
      double x1 = parameters[0].getEstimate();
      double x2 = parameters[1].getEstimate();
      return new double[] {
       -13.0 + x1 + ((5.0 - x2) * x2 -  2.0) * x2,
       -29.0 + x1 + ((1.0 + x2) * x2 - 14.0) * x2
      };
    }

  }

  private static class BardFunction extends MinpackFunction {

    public BardFunction(double x0,
                        double theoreticalStartCost,
                        double theoreticalMinCost,
                        double[] theoreticalMinParams) {
      super(15, buildArray(3, x0), theoreticalStartCost,
            theoreticalMinCost, theoreticalMinParams);
    }

    @Override
    protected double[][] getJacobian() {
      double   x2 = parameters[1].getEstimate();
      double   x3 = parameters[2].getEstimate();
      double[][] jacobian = new double[m][];
      for (int i = 0; i < m; ++i) {
        double tmp1 = i  + 1;
        double tmp2 = 15 - i;
        double tmp3 = (i <= 7) ? tmp1 : tmp2;
        double tmp4 = x2 * tmp2 + x3 * tmp3;
        tmp4 *= tmp4;
        jacobian[i] = new double[] { -1, tmp1 * tmp2 / tmp4, tmp1 * tmp3 / tmp4 };
      }
      return jacobian;
    }

    @Override
    protected double[] getResiduals() {
      double   x1 = parameters[0].getEstimate();
      double   x2 = parameters[1].getEstimate();
      double   x3 = parameters[2].getEstimate();
      double[] f = new double[m];
      for (int i = 0; i < m; ++i) {
        double tmp1 = i + 1;
        double tmp2 = 15 - i;
        double tmp3 = (i <= 7) ? tmp1 : tmp2;
        f[i] = y[i] - (x1 + tmp1 / (x2 * tmp2 + x3 * tmp3));
      }
      return f;
    }

    private static final double[] y = {
      0.14, 0.18, 0.22, 0.25, 0.29,
      0.32, 0.35, 0.39, 0.37, 0.58,
      0.73, 0.96, 1.34, 2.10, 4.39
    };

  }

  private static class KowalikOsborneFunction extends MinpackFunction {

    public KowalikOsborneFunction(double[] startParams,
                                  double theoreticalStartCost,
                                  double theoreticalMinCost,
                                  double[] theoreticalMinParams) {
      super(11, startParams, theoreticalStartCost,
            theoreticalMinCost, theoreticalMinParams);
      if (theoreticalStartCost > 20.0) {
        setCostAccuracy(2.0e-4);
        setParamsAccuracy(5.0e-3);
      }
    }

    @Override
    protected double[][] getJacobian() {
      double   x1 = parameters[0].getEstimate();
      double   x2 = parameters[1].getEstimate();
      double   x3 = parameters[2].getEstimate();
      double   x4 = parameters[3].getEstimate();
      double[][] jacobian = new double[m][];
      for (int i = 0; i < m; ++i) {
        double tmp = v[i] * (v[i] + x3) + x4;
        double j1  = -v[i] * (v[i] + x2) / tmp;
        double j2  = -v[i] * x1 / tmp;
        double j3  = j1 * j2;
        double j4  = j3 / v[i];
        jacobian[i] = new double[] { j1, j2, j3, j4 };
      }
      return jacobian;
    }

    @Override
    protected double[] getResiduals() {
      double x1 = parameters[0].getEstimate();
      double x2 = parameters[1].getEstimate();
      double x3 = parameters[2].getEstimate();
      double x4 = parameters[3].getEstimate();
      double[] f = new double[m];
      for (int i = 0; i < m; ++i) {
        f[i] = y[i] - x1 * (v[i] * (v[i] + x2)) / (v[i] * (v[i] + x3) + x4);
      }
      return f;
    }

    private static final double[] v = {
      4.0, 2.0, 1.0, 0.5, 0.25, 0.167, 0.125, 0.1, 0.0833, 0.0714, 0.0625
    };

    private static final double[] y = {
      0.1957, 0.1947, 0.1735, 0.1600, 0.0844, 0.0627,
      0.0456, 0.0342, 0.0323, 0.0235, 0.0246
    };

  }

  private static class MeyerFunction extends MinpackFunction {

    public MeyerFunction(double[] startParams,
                         double theoreticalStartCost,
                         double theoreticalMinCost,
                         double[] theoreticalMinParams) {
      super(16, startParams, theoreticalStartCost,
            theoreticalMinCost, theoreticalMinParams);
      if (theoreticalStartCost > 1.0e6) {
        setCostAccuracy(7.0e-3);
        setParamsAccuracy(2.0e-2);
      }
    }

    @Override
    protected double[][] getJacobian() {
      double   x1 = parameters[0].getEstimate();
      double   x2 = parameters[1].getEstimate();
      double   x3 = parameters[2].getEstimate();
      double[][] jacobian = new double[m][];
      for (int i = 0; i < m; ++i) {
        double temp = 5.0 * (i + 1) + 45.0 + x3;
        double tmp1 = x2 / temp;
        double tmp2 = FastMath.exp(tmp1);
        double tmp3 = x1 * tmp2 / temp;
        jacobian[i] = new double[] { tmp2, tmp3, -tmp1 * tmp3 };
      }
      return jacobian;
    }

    @Override
    protected double[] getResiduals() {
      double x1 = parameters[0].getEstimate();
      double x2 = parameters[1].getEstimate();
      double x3 = parameters[2].getEstimate();
      double[] f = new double[m];
      for (int i = 0; i < m; ++i) {
        f[i] = x1 * FastMath.exp(x2 / (5.0 * (i + 1) + 45.0 + x3)) - y[i];
      }
     return f;
    }

    private static final double[] y = {
      34780.0, 28610.0, 23650.0, 19630.0,
      16370.0, 13720.0, 11540.0,  9744.0,
       8261.0,  7030.0,  6005.0,  5147.0,
       4427.0,  3820.0,  3307.0,  2872.0
    };

  }

  private static class WatsonFunction extends MinpackFunction {

    public WatsonFunction(int n, double x0,
                          double theoreticalStartCost,
                          double theoreticalMinCost,
                          double[] theoreticalMinParams) {
      super(31, buildArray(n, x0), theoreticalStartCost,
            theoreticalMinCost, theoreticalMinParams);
    }

    @Override
    protected double[][] getJacobian() {

      double[][] jacobian = new double[m][];

      for (int i = 0; i < (m - 2); ++i) {
        double div = (i + 1) / 29.0;
        double s2  = 0.0;
        double dx  = 1.0;
        for (int j = 0; j < n; ++j) {
          s2 += dx * parameters[j].getEstimate();
          dx *= div;
        }
        double temp= 2 * div * s2;
        dx = 1.0 / div;
        jacobian[i] = new double[n];
        for (int j = 0; j < n; ++j) {
          jacobian[i][j] = dx * (j - temp);
          dx *= div;
        }
      }

      jacobian[m - 2]    = new double[n];
      jacobian[m - 2][0] = 1;

      jacobian[m - 1]   = new double[n];
      jacobian[m - 1][0]= -2 * parameters[0].getEstimate();
      jacobian[m - 1][1]= 1;

      return jacobian;

    }

    @Override
    protected double[] getResiduals() {
     double[] f = new double[m];
     for (int i = 0; i < (m - 2); ++i) {
       double div = (i + 1) / 29.0;
       double s1 = 0;
       double dx = 1;
       for (int j = 1; j < n; ++j) {
         s1 += j * dx * parameters[j].getEstimate();
         dx *= div;
       }
       double s2 =0;
       dx =1;
       for (int j = 0; j < n; ++j) {
         s2 += dx * parameters[j].getEstimate();
         dx *= div;
       }
       f[i] = s1 - s2 * s2 - 1;
     }

     double x1 = parameters[0].getEstimate();
     double x2 = parameters[1].getEstimate();
     f[m - 2] = x1;
     f[m - 1] = x2 - x1 * x1 - 1;

     return f;

    }

  }

  private static class Box3DimensionalFunction extends MinpackFunction {

    public Box3DimensionalFunction(int m, double[] startParams,
                                   double theoreticalStartCost) {
      super(m, startParams, theoreticalStartCost,
            0.0, new double[] { 1.0, 10.0, 1.0 });
   }

    @Override
    protected double[][] getJacobian() {
      double   x1 = parameters[0].getEstimate();
      double   x2 = parameters[1].getEstimate();
      double[][] jacobian = new double[m][];
      for (int i = 0; i < m; ++i) {
        double tmp = (i + 1) / 10.0;
        jacobian[i] = new double[] {
          -tmp * FastMath.exp(-tmp * x1),
           tmp * FastMath.exp(-tmp * x2),
          FastMath.exp(-i - 1) - FastMath.exp(-tmp)
        };
      }
      return jacobian;
    }

    @Override
    protected double[] getResiduals() {
      double x1 = parameters[0].getEstimate();
      double x2 = parameters[1].getEstimate();
      double x3 = parameters[2].getEstimate();
      double[] f = new double[m];
      for (int i = 0; i < m; ++i) {
        double tmp = (i + 1) / 10.0;
        f[i] = FastMath.exp(-tmp * x1) - FastMath.exp(-tmp * x2)
             + (FastMath.exp(-i - 1) - FastMath.exp(-tmp)) * x3;
      }
      return f;
    }

  }

  private static class JennrichSampsonFunction extends MinpackFunction {

    public JennrichSampsonFunction(int m, double[] startParams,
                                   double theoreticalStartCost,
                                   double theoreticalMinCost,
                                   double[] theoreticalMinParams) {
      super(m, startParams, theoreticalStartCost,
            theoreticalMinCost, theoreticalMinParams);
    }

    @Override
    protected double[][] getJacobian() {
      double   x1 = parameters[0].getEstimate();
      double   x2 = parameters[1].getEstimate();
      double[][] jacobian = new double[m][];
      for (int i = 0; i < m; ++i) {
        double t = i + 1;
        jacobian[i] = new double[] { -t * FastMath.exp(t * x1), -t * FastMath.exp(t * x2) };
      }
      return jacobian;
    }

    @Override
    protected double[] getResiduals() {
      double x1 = parameters[0].getEstimate();
      double x2 = parameters[1].getEstimate();
      double[] f = new double[m];
      for (int i = 0; i < m; ++i) {
        double temp = i + 1;
        f[i] = 2 + 2 * temp - FastMath.exp(temp * x1) - FastMath.exp(temp * x2);
      }
      return f;
    }

  }

  private static class BrownDennisFunction extends MinpackFunction {

    public BrownDennisFunction(int m, double[] startParams,
                               double theoreticalStartCost,
                               double theoreticalMinCost,
                               double[] theoreticalMinParams) {
      super(m, startParams, theoreticalStartCost,
            theoreticalMinCost, theoreticalMinParams);
      setCostAccuracy(2.5e-8);
    }

    @Override
    protected double[][] getJacobian() {
      double   x1 = parameters[0].getEstimate();
      double   x2 = parameters[1].getEstimate();
      double   x3 = parameters[2].getEstimate();
      double   x4 = parameters[3].getEstimate();
      double[][] jacobian = new double[m][];
      for (int i = 0; i < m; ++i) {
        double temp = (i + 1) / 5.0;
        double ti   = FastMath.sin(temp);
        double tmp1 = x1 + temp * x2 - FastMath.exp(temp);
        double tmp2 = x3 + ti   * x4 - FastMath.cos(temp);
        jacobian[i] = new double[] {
          2 * tmp1, 2 * temp * tmp1, 2 * tmp2, 2 * ti * tmp2
        };
      }
      return jacobian;
    }

    @Override
    protected double[] getResiduals() {
      double x1 = parameters[0].getEstimate();
      double x2 = parameters[1].getEstimate();
      double x3 = parameters[2].getEstimate();
      double x4 = parameters[3].getEstimate();
      double[] f = new double[m];
      for (int i = 0; i < m; ++i) {
        double temp = (i + 1) / 5.0;
        double tmp1 = x1 + temp * x2 - FastMath.exp(temp);
        double tmp2 = x3 + FastMath.sin(temp) * x4 - FastMath.cos(temp);
        f[i] = tmp1 * tmp1 + tmp2 * tmp2;
      }
      return f;
    }

  }

  private static class ChebyquadFunction extends MinpackFunction {

    private static double[] buildChebyquadArray(int n, double factor) {
      double[] array = new double[n];
      double inv = factor / (n + 1);
      for (int i = 0; i < n; ++i) {
        array[i] = (i + 1) * inv;
      }
      return array;
    }

    public ChebyquadFunction(int n, int m, double factor,
                             double theoreticalStartCost,
                             double theoreticalMinCost,
                             double[] theoreticalMinParams) {
      super(m, buildChebyquadArray(n, factor), theoreticalStartCost,
            theoreticalMinCost, theoreticalMinParams);
    }

    @Override
    protected double[][] getJacobian() {

      double[][] jacobian = new double[m][];
      for (int i = 0; i < m; ++i) {
        jacobian[i] = new double[n];
      }

      double dx = 1.0 / n;
      for (int j = 0; j < n; ++j) {
        double tmp1 = 1;
        double tmp2 = 2 * parameters[j].getEstimate() - 1;
        double temp = 2 * tmp2;
        double tmp3 = 0;
        double tmp4 = 2;
        for (int i = 0; i < m; ++i) {
          jacobian[i][j] = dx * tmp4;
          double ti = 4 * tmp2 + temp * tmp4 - tmp3;
          tmp3 = tmp4;
          tmp4 = ti;
          ti   = temp * tmp2 - tmp1;
          tmp1 = tmp2;
          tmp2 = ti;
        }
      }

      return jacobian;

    }

    @Override
    protected double[] getResiduals() {

      double[] f = new double[m];

      for (int j = 0; j < n; ++j) {
        double tmp1 = 1;
        double tmp2 = 2 * parameters[j].getEstimate() - 1;
        double temp = 2 * tmp2;
        for (int i = 0; i < m; ++i) {
          f[i] += tmp2;
          double ti = temp * tmp2 - tmp1;
          tmp1 = tmp2;
          tmp2 = ti;
        }
      }

      double dx = 1.0 / n;
      boolean iev = false;
      for (int i = 0; i < m; ++i) {
        f[i] *= dx;
        if (iev) {
          f[i] += 1.0 / (i * (i + 2));
        }
        iev = ! iev;
      }

      return f;

    }

  }

  private static class BrownAlmostLinearFunction extends MinpackFunction {

    public BrownAlmostLinearFunction(int m, double factor,
                                     double theoreticalStartCost,
                                     double theoreticalMinCost,
                                     double[] theoreticalMinParams) {
      super(m, buildArray(m, factor), theoreticalStartCost,
            theoreticalMinCost, theoreticalMinParams);
    }

    @Override
    protected double[][] getJacobian() {
      double[][] jacobian = new double[m][];
      for (int i = 0; i < m; ++i) {
        jacobian[i] = new double[n];
      }

      double prod = 1;
      for (int j = 0; j < n; ++j) {
        prod *= parameters[j].getEstimate();
        for (int i = 0; i < n; ++i) {
          jacobian[i][j] = 1;
        }
        jacobian[j][j] = 2;
      }

      for (int j = 0; j < n; ++j) {
        EstimatedParameter vj = parameters[j];
        double temp = vj.getEstimate();
        if (temp == 0) {
          temp = 1;
          prod = 1;
          for (int k = 0; k < n; ++k) {
            if (k != j) {
              prod *= parameters[k].getEstimate();
            }
          }
        }
        jacobian[n - 1][j] = prod / temp;
      }

      return jacobian;

    }

    @Override
    protected double[] getResiduals() {
      double[] f = new double[m];
      double sum  = -(n + 1);
      double prod = 1;
      for (int j = 0; j < n; ++j) {
        sum  += parameters[j].getEstimate();
        prod *= parameters[j].getEstimate();
      }
      for (int i = 0; i < n; ++i) {
        f[i] = parameters[i].getEstimate() + sum;
      }
      f[n - 1] = prod - 1;
      return f;
    }

  }

  private static class Osborne1Function extends MinpackFunction {

    public Osborne1Function(double[] startParams,
                            double theoreticalStartCost,
                            double theoreticalMinCost,
                            double[] theoreticalMinParams) {
      super(33, startParams, theoreticalStartCost,
            theoreticalMinCost, theoreticalMinParams);
    }

    @Override
    protected double[][] getJacobian() {
      double   x2 = parameters[1].getEstimate();
      double   x3 = parameters[2].getEstimate();
      double   x4 = parameters[3].getEstimate();
      double   x5 = parameters[4].getEstimate();
      double[][] jacobian = new double[m][];
      for (int i = 0; i < m; ++i) {
        double temp = 10.0 * i;
        double tmp1 = FastMath.exp(-temp * x4);
        double tmp2 = FastMath.exp(-temp * x5);
        jacobian[i] = new double[] {
          -1, -tmp1, -tmp2, temp * x2 * tmp1, temp * x3 * tmp2
        };
      }
      return jacobian;
    }

    @Override
    protected double[] getResiduals() {
      double x1 = parameters[0].getEstimate();
      double x2 = parameters[1].getEstimate();
      double x3 = parameters[2].getEstimate();
      double x4 = parameters[3].getEstimate();
      double x5 = parameters[4].getEstimate();
      double[] f = new double[m];
      for (int i = 0; i < m; ++i) {
        double temp = 10.0 * i;
        double tmp1 = FastMath.exp(-temp * x4);
        double tmp2 = FastMath.exp(-temp * x5);
        f[i] = y[i] - (x1 + x2 * tmp1 + x3 * tmp2);
      }
      return f;
    }

    private static final double[] y = {
      0.844, 0.908, 0.932, 0.936, 0.925, 0.908, 0.881, 0.850, 0.818, 0.784, 0.751,
      0.718, 0.685, 0.658, 0.628, 0.603, 0.580, 0.558, 0.538, 0.522, 0.506, 0.490,
      0.478, 0.467, 0.457, 0.448, 0.438, 0.431, 0.424, 0.420, 0.414, 0.411, 0.406
    };

  }

  private static class Osborne2Function extends MinpackFunction {

    public Osborne2Function(double[] startParams,
                            double theoreticalStartCost,
                            double theoreticalMinCost,
                            double[] theoreticalMinParams) {
      super(65, startParams, theoreticalStartCost,
            theoreticalMinCost, theoreticalMinParams);
    }

    @Override
    protected double[][] getJacobian() {
      double   x01 = parameters[0].getEstimate();
      double   x02 = parameters[1].getEstimate();
      double   x03 = parameters[2].getEstimate();
      double   x04 = parameters[3].getEstimate();
      double   x05 = parameters[4].getEstimate();
      double   x06 = parameters[5].getEstimate();
      double   x07 = parameters[6].getEstimate();
      double   x08 = parameters[7].getEstimate();
      double   x09 = parameters[8].getEstimate();
      double   x10 = parameters[9].getEstimate();
      double   x11 = parameters[10].getEstimate();
      double[][] jacobian = new double[m][];
      for (int i = 0; i < m; ++i) {
        double temp = i / 10.0;
        double tmp1 = FastMath.exp(-x05 * temp);
        double tmp2 = FastMath.exp(-x06 * (temp - x09) * (temp - x09));
        double tmp3 = FastMath.exp(-x07 * (temp - x10) * (temp - x10));
        double tmp4 = FastMath.exp(-x08 * (temp - x11) * (temp - x11));
        jacobian[i] = new double[] {
          -tmp1,
          -tmp2,
          -tmp3,
          -tmp4,
          temp * x01 * tmp1,
          x02 * (temp - x09) * (temp - x09) * tmp2,
          x03 * (temp - x10) * (temp - x10) * tmp3,
          x04 * (temp - x11) * (temp - x11) * tmp4,
          -2 * x02 * x06 * (temp - x09) * tmp2,
          -2 * x03 * x07 * (temp - x10) * tmp3,
          -2 * x04 * x08 * (temp - x11) * tmp4
        };
      }
      return jacobian;
    }

    @Override
    protected double[] getResiduals() {
      double x01 = parameters[0].getEstimate();
      double x02 = parameters[1].getEstimate();
      double x03 = parameters[2].getEstimate();
      double x04 = parameters[3].getEstimate();
      double x05 = parameters[4].getEstimate();
      double x06 = parameters[5].getEstimate();
      double x07 = parameters[6].getEstimate();
      double x08 = parameters[7].getEstimate();
      double x09 = parameters[8].getEstimate();
      double x10 = parameters[9].getEstimate();
      double x11 = parameters[10].getEstimate();
      double[] f = new double[m];
      for (int i = 0; i < m; ++i) {
        double temp = i / 10.0;
        double tmp1 = FastMath.exp(-x05 * temp);
        double tmp2 = FastMath.exp(-x06 * (temp - x09) * (temp - x09));
        double tmp3 = FastMath.exp(-x07 * (temp - x10) * (temp - x10));
        double tmp4 = FastMath.exp(-x08 * (temp - x11) * (temp - x11));
        f[i] = y[i] - (x01 * tmp1 + x02 * tmp2 + x03 * tmp3 + x04 * tmp4);
      }
      return f;
    }

    private static final double[] y = {
      1.366, 1.191, 1.112, 1.013, 0.991,
      0.885, 0.831, 0.847, 0.786, 0.725,
      0.746, 0.679, 0.608, 0.655, 0.616,
      0.606, 0.602, 0.626, 0.651, 0.724,
      0.649, 0.649, 0.694, 0.644, 0.624,
      0.661, 0.612, 0.558, 0.533, 0.495,
      0.500, 0.423, 0.395, 0.375, 0.372,
      0.391, 0.396, 0.405, 0.428, 0.429,
      0.523, 0.562, 0.607, 0.653, 0.672,
      0.708, 0.633, 0.668, 0.645, 0.632,
      0.591, 0.559, 0.597, 0.625, 0.739,
      0.710, 0.729, 0.720, 0.636, 0.581,
      0.428, 0.292, 0.162, 0.098, 0.054
    };

  }

}
