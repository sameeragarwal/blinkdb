import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import quicktime.std.movies.LoadSettings;


public class DataGeneratorV3 extends DataGenerator {

	/**
	 * 
	 * @param outputAMPLprefix the method will create outputAMPLprefix.mod and outputAMPLprefix.dat
	 * @param prefix
	 * @throws Exception
	 */
	public static void AMPLgen(String nameMapFile, String outputAMPLprefix, String prefix) throws Exception {	
		BufferedWriter fid = new BufferedWriter(new FileWriter("./"+outputAMPLprefix+".mod"));

		ColumnMap columnMap = new ColumnMap(nameMapFile); 
		
		ArrayList<int[]> queries = loadStringSets(columnMap.getName2NumberMapping(), prefix + "queries.txt");
		double[][] freqs = load(prefix + "frequencies.txt");
		ArrayList<int[]> candidates = loadStringSets(columnMap.getName2NumberMapping(), prefix + "candidates.txt");
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
		
		BufferedWriter fidata = new BufferedWriter(new FileWriter("./"+outputAMPLprefix+".dat"));

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
	 * This method loads the solution of the GLPK package from a file and displays it in a human-readable fashion
	 * @param nameMapFile the filename of the mapping between the column names and column numbers
	 * @param potentialSolutionFile the filename of a set of views for which we would like to see the cost and goal function value
	 * @param inputParameterPrefix the prefix used in the name convention of the input files that contain the model parameters
	 * @throws Exception
	 */
	public static void AMPLeval(String nameMapFile, String potentialSolutionFile, String inputParameterPrefix) throws Exception {
		ColumnMap columnMap = new ColumnMap(nameMapFile);
		Map<Integer, String> nameMap = columnMap.getNumber2NameMapping();

		ArrayList<int[]> proposal = loadStringSets(columnMap.getName2NumberMapping(), potentialSolutionFile);	
		ArrayList<int[]> candidates = loadStringSets(columnMap.getName2NumberMapping(), inputParameterPrefix + "candidates.txt");
		double[][] storage = load(inputParameterPrefix + "storage.txt");
		double[][] total_storage = load(inputParameterPrefix + "total_storage.txt");
		double[][] T = load(inputParameterPrefix + "T.txt");
		ArrayList<int[]> queries = loadStringSets(columnMap.getName2NumberMapping(), inputParameterPrefix + "queries.txt");
		double[][] freqs = load(inputParameterPrefix + "frequencies.txt");
		double[][] delta = load(inputParameterPrefix + "delta.txt");

		int nQueries = queries.size();
		int nCandidates = candidates.size();
		int nProposal = proposal.size();

		int[] index = new int[nProposal];
		int[] build = new int[nCandidates];
		
		for (int i=0; i<nCandidates; ++i) {
			build[i] = 0;
			Arrays.sort(candidates.get(i));
		}
		
		for (int i=0; i<nProposal; ++i) {
			Arrays.sort(proposal.get(i));
			index[i] = -1;
			for (int j=0; j<nCandidates; ++j) {
				boolean found = true;
				int L = candidates.get(j).length; 
				if (L != proposal.get(i).length)
					continue;
				for (int k=0; k<L; ++k)
					if (candidates.get(j)[k] != proposal.get(i)[k]) {
						found = false;
						break;
					}
				if (found) {
					index[i] = j;
					build[j] = 1;
					break;
				}
			}
			if (index[i] == -1) 
				throw new Exception("Invalid proposal "+proposal.get(i).toString()+" was not among the candidates!\n");				
		}
		// Now we have found the index of every view in the proposal
		
		//calculate the total storage cost of the proposal
		double proposal_cost = 0.0;
		for (int i=0; i<index.length; ++i) {
			proposal_cost += storage[index[i]][0];
 		}
		System.out.println("With T="+T[0][0]+", this proposal chooses " + index.length + " views, with a utilization of "+proposal_cost/total_storage[0][0] + 
				" of the storage budget of "+ total_storage[0][0] + " (proposal used a cost of "+ proposal_cost + ").");
		
		//Now let us calculate the objective function for the given proposal

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
		
		double[] coverage = new double[nQueries];		
		for (int i=0; i<nQueries; ++i) {
			double mysum = 0;
		    int q[] = queries.get(i);
		    int qLen = q.length;
		    for (int k=1; k<=Math.min(maxCandCard, qLen); ++k) {
		        ArrayList<String> subsetSummaries = nchoosek(q, k);
		        for (int s=0; s<subsetSummaries.size(); ++s) {
		           Integer idx = candidateSummaries.get(subsetSummaries.get(s));
		           if (idx!=null) {
		        	   if (candidates.get(idx).length < qLen)
		        		   mysum += build[idx] * candidates.get(idx).length/ qLen;
		        	   else
		        		   mysum += build[idx]/T[0][0];
		           }
				}	        
			}
		    coverage[i] = T[0][0]*mysum;
		}
		double TotalBenefit = 0;
		for (int i=0; i<nQueries; ++i)
			TotalBenefit += freqs[i][0]*coverage[i]*delta[i][0];
		System.out.println("Total benefit was: "+TotalBenefit +"\n============================\nDetails (freq[i], coverage[i], delta[i]) for i'th query:");
		for (int i=0; i<nQueries; ++i)
			System.out.println(freqs[i][0]+", "+coverage[i] +", "+ delta[i][0]);
		
	}

	
	static void showHelp() {
		String msg = "prefix-queries.txt, prefix-frequencies.txt, prefix-candidates.txt, prefix-storage.txt, prefix-delta.txt, prefix-total_storage.txt, prefix-T.txt\n";
		String Usage1 = "java DataGenerator gen nameMapFile outputModelPrefix inputParameterPrefix\n";
		String Usage2 = "java DataGenerator show nameMapFile solutionFile inputParameterPrefix\n";
		String Usage3 = "java DataGenerator eval nameMapFile potentialSolution inputParameterPrefix\n";
		System.err.println("Usgae: " + Usage1 + "or: "+ Usage2 + "or: "+ Usage3 + " where all the following must be present:\n" + msg);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			
			if (args.length == 4 && args[0].equals("gen")) {
				String nameMapFile = args[1], outputModelPrefix = args[2], inputParameterPrefix = args[3];
				AMPLgen(nameMapFile, outputModelPrefix, inputParameterPrefix);
				System.out.println("Successfully done!");
			} else if (args.length == 4 && args[0].equals("show")) {
				String nameMapFile = args[1], solutionFile = args[2], inputParameterPrefix = args[3];
				AMPLshow(nameMapFile, solutionFile, inputParameterPrefix);
				System.out.println("\n<<Successfully done!>>");
			} else if (args.length == 4 && args[0].equals("eval")) {
				String nameMapFile = args[1], solutionFile = args[2], inputParameterPrefix = args[3];
				AMPLeval(nameMapFile, solutionFile, inputParameterPrefix);
				System.out.println("\n<<Successfully done!>>");
			} else {
				showHelp();
					//AMPLgen("AMPL3-500", "300");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}
