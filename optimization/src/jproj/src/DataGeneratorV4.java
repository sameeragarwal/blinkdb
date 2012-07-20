import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import quicktime.std.movies.LoadSettings;


public class DataGeneratorV4 extends DataGenerator {

	
	public static void AMPLgen(String filename, String prefix) throws Exception {	
		BufferedWriter fid = new BufferedWriter(new FileWriter("./"+filename+".mod"));

		ArrayList<int[]> queries = loadStringSets(null, prefix + "queries.txt");
		double[][] freqs = load(prefix + "frequencies.txt");
		ArrayList<int[]> candidates = loadStringSets(null, prefix + "candidates.txt");
		double[][] storage = load(prefix + "storage.txt");
		double[][] delta = load(prefix + "delta.txt");
		double[][] total_storage = load(prefix + "total_storage.txt");
		double[][] T = load(prefix + "T.txt");

		int nQueries = queries.size();
		int nCandidates = candidates.size();
		int nCols = -1;		
		for (int i=0; i<nQueries; ++i) {
		    int v = max(queries.get(i));
		    if (v > nCols)
		        nCols = v;
		}
		for (int i=0; i<nCandidates; ++i) {
		    int v = max(candidates.get(i));
		    if (v > nCols)
		        nCols = v;
		}

		int maxCandCard = 0;
		HashMap<String, Integer> candidateSummaries = new HashMap<String, Integer>();
		for (int i=0; i< candidates.size(); ++i) {
		    candidateSummaries.put(summarize(candidates.get(i)), i);
		    int l = candidates.get(i).length;
		    if (l > maxCandCard)
		        maxCandCard=l;
		}

		if (freqs.length != nQueries || storage.length != nCandidates || delta.length != nQueries) {
		    throw new Exception("Inconsistent input.");
		}
	
		fid.write("param nQueries;\n");
		fid.write("param nViews; # one for each candidate view!\n");
		fid.write("param T;\n");
		fid.write("param freqs{i in 1..nQueries} default 1/nQueries; \n");
		fid.write("param delta{i in 1..nQueries};\n");
		fid.write("param qcard{i in 1..nQueries};\n");
		fid.write("param storage{j in 1..nViews};\n");
		fid.write("param vcard{j in 1..nViews};\n");
		//fprintf(fid, 'param Contains{i in 1..nQueries, j in 1..nViews};\n');
		fid.write("param total_storage;\n\n");
		fid.write("var coverage{i in 1..nQueries} >=0, <=1;\n\n");
		fid.write("var build{j in 1..nViews} integer binary;\n\n");
		fid.write("maximize TotalBenefit: sum{i in 1..nQueries} freqs[i]*coverage[i]*delta[i];\n\n");
		fid.write("subject to StorageC: sum{j in 1..nViews} storage[j]*build[j] <= total_storage;\n\n");
		//subject to Containment {i in 1..nQueries}: coverage[i] <= sum{j in 1..nViews} Contains[i,j] * build[j] * vcard[j] / qcard[i];

		for (int i=0; i<nQueries; ++i) {
		    fid.write("subject to Containment"+(i+1)+": coverage["+(i+1)+"] <= T*(0");
		    int q[] = queries.get(i);
		    int qLen = q.length;
		    for (int k=1; k<=Math.min(maxCandCard, qLen); ++k) {
		        ArrayList<String> subsetSummaries = nchoosek(q, k);
		        for (int s=0; s<subsetSummaries.size(); ++s) {
		           Integer idx = candidateSummaries.get(subsetSummaries.get(s));
		           if (idx!=null) {
		        	   if (candidates.get(idx).length < qLen)
		        		   fid.write("+(build["+(idx+1)+"] * "+candidates.get(idx).length+" / "+qLen+") ");
		        	   else
		        		   fid.write("+(build["+(idx+1)+"]/T) ");
		           }
				}	        
			}    
		    /*for c=1:nCandidates
		        if ismember(candidates{c}, queries{i})
		            fprintf(fid, '+(build[%d] * %d / %d)', c, length(candidates{c}), length(queries{i}));
		        end
		        if mod(c,10000)==0
		            fprintf(1,'query=%d can=%d\n', i, c);
		        end
		    end*/
		    fid.write(" );\n");
		}

		fid.close();
		
		BufferedWriter fidata = new BufferedWriter(new FileWriter("./"+filename+".dat"));

		fidata.write("param nQueries:= "+nQueries+";\n");
		fidata.write("param nViews:= "+nCandidates+";\n");
		fidata.write("param T:= "+T[0][0]+";\n");

		fidata.write("param freqs:=");
		for (int i=0; i<nQueries; ++i)
		    fidata.write(" " + (i+1) + " " + freqs[i][0]);
		fidata.write(";\n");

		fidata.write("param delta:=");
		for (int i=0; i<nQueries; ++i)
			fidata.write(" " + (i+1) + " " + delta[i][0]);
		fidata.write(";\n");

		fidata.write("param qcard:= ");
		for (int i=0; i<nQueries; ++i)
		    fidata.write(" " + (i+1) + " " + queries.get(i).length);
		fidata.write(";\n");

		fidata.write("param storage:= ");
		for (int i=0; i<nCandidates; ++i)
		    fidata.write(" "+ (i+1) + " " + storage[i][0]);
		fidata.write(";\n");

		fidata.write("param vcard:= ");
		for (int i=0; i<nCandidates; ++i)
		    fidata.write(" "+ (i+1) + " " + candidates.get(i).length);
		fidata.write(";\n");

		fidata.write("param total_storage := "+ total_storage[0][0]+ ";\n");

		fidata.close();		
	}
	
	


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (1==1) {
			System.err.println("WARNING: Version 4 of optimization has not been fully implemented yet.");
			return;
		}
		
		if (args.length != 2) { 
			System.err.println("Usgae: java DataGenerator outputModelPrefix inputParameterPrefix");
		} else {
			String outputModelPrefix = args[0], inputParameterPrefix = args[1];
			try {
				//AMPLgen("AMPL3-500", "300");
				AMPLgen(outputModelPrefix, inputParameterPrefix);
				System.out.println("Successfully done!");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
	}

}
