package org.apache.hadoop.hive.ql.costmodel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;

public class CostModel {
	
  HashMap<Double, HashMap<Double, String>> CostModelHashMap = null;

  private static final CostModel _instance = new CostModel();
  
  public static CostModel getInstance() {
	  return _instance;
  }
  
  private CostModel() {
    CostModelHashMap = new HashMap<Double, HashMap<Double, String>>();
    Map<String, String> envMap = System.getenv();
    String filePath = envMap.get("HIVE_HOME") + "/lib/dummy.csv";
    try { 
    	populateCostModelHashMap(filePath);
    } catch (Exception ex) {
    	System.out.println("populateCostModelHashMap returned exception");
    }
  }

  /**
   * Populate the cost model HashMap from the file.
   * @param fileName
   */
  private void populateCostModelHashMap(String fileName) throws IOException {

    String lineRead = "";
    StringTokenizer strToken = null;
    Double errorBound, timeBound;
    String sampleDataset;

    // This file has data in the form:
    // <errorBound, timeBound, sampleDataset>
    InputStream inStream = null;
    try {
    	//inStream = getClass().getResourceAsStream(fileName);
    	inStream = new FileInputStream(new File(fileName));
    } catch (Exception ex) {
    	System.out.println("Cannot load " + fileName + "!");
    	return;
    }
    
    BufferedReader bufReader = new BufferedReader(new InputStreamReader(inStream));
    while ((lineRead = bufReader.readLine()) != null) {

      strToken = new StringTokenizer(lineRead, ",");

      errorBound = timeBound = 0.0;
      sampleDataset = "";
      errorBound = Double.parseDouble(strToken.nextToken());
      timeBound = Double.parseDouble(strToken.nextToken());
      sampleDataset = strToken.nextToken();

      HashMap<Double, String> innerHashMap;
      if (!CostModelHashMap.containsKey(errorBound)) {
        innerHashMap = new HashMap<Double, String>();
        CostModelHashMap.put(errorBound, innerHashMap);
      } else {
        innerHashMap = CostModelHashMap.get(errorBound);
      }
      innerHashMap.put(timeBound, sampleDataset);
    }
  }

  /**
   * Find the sampled dataset table corresponding to the given error and time bounds.
   * @param errorBound
   * @param timeBound
   * @return
   */
  public String getSampledTableName(Double errorBound, Double timeBound) {
	  if (CostModelHashMap.get(errorBound) != null)
		  return (CostModelHashMap.get(errorBound)).get(timeBound);
	  else
		  return null;
  }
}