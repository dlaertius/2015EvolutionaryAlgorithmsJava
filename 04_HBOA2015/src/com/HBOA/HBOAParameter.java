package com.HBOA;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Scanner;


public class HBOAParameter{
	
	// NOTE: optionNames must coincide exactly with the option names in the 'Parameters.txt' file.
	private static final String optionNames[] =     
		{"nRuns", 
		 "problemType", "stringSize", "optimumValue", 
		 "N", "NS", "selectionMethod", "tourSize", "tau",
		 "bayesianMetric", "maxVertexDegree",
		 "replacementType", "windowSize", "offspringSize",
		 "maxNGen", "maxFitCalls", "allFitnessEqual", "epsilon", "maxOptimal", "foundBestFit", "foundOnes"
		};
	
	// Problem type
	private static int   problemType;		// Problem type. Check Parameters.txt for the option menu
	private static int   stringSize;		// Size of each individual. NOTE: This parameter must be set accordingly to the problem type.
	private static float optimumValue;		// Best fitness. NOTE: This parameter must be set accordingly to the problem type.
	//private static String problemName;	// Used to initialize the output file name.
	
	
	// Selector parameters
	private static float pNS;				// Size of selection set as a proportion of N. Default = 1 (The same value as N)
	private static int   NS;				// Size of the selection set. Depends on the chosen selection method.
	private static int   selectionMethod;	// Selection method. Default = 1 (Tournament selection)
	private static int   tourSize;			// Size of tournament. Use only with Tournament Selection (selection = 1 or 2)
	private static float tau;				// Proportion of truncated population. Use only with Truncation (selectionMethod = 3)
	
	// Bayesian Network parameters
	private static int   bayesianMetric;	// Type of score metric. Default = 1 (Bayesian-Dirichelet Metric)
	private static int   maxVertexDegree; 	// Maximum number of parents per vertex. Default = .6 (proportion of stringSize)
	private static float pOffspringSize;	// Size of the offspring set, generated by the bayesian network. Default = 1 (The same value as N)
	
	// Replacement parameters
	private static int replacementType;		// Replacement method. Default = 1 (Restricted replacement)
	private static int windowSize;			// Size of replacement tournament. Use only with RestrictedReplacement (replacement = 2)
	
	// Stopper parameters are public. No need for local variables.
		
	// Get Selector parameters
	public static float            getPNS(){return pNS;}
	public static int  getSelectionMethod(){return selectionMethod;}
	public static int         getTourSize(){return tourSize;}
	public static float            getTau(){return tau;}
	// Get Bayesian network parameters
	public static int getBayesianMetric(){return bayesianMetric;}
	public static int getMaxVertexDegree(){return maxVertexDegree;}	
	public static float getPOffspringSize(){return pOffspringSize;}
	// Get Replacement parameters
	public static int  getReplacementType(){return replacementType;}
	public static int       getWindowSize(){return windowSize;}
	
	
	// NOTE: Execute this initialization PRIOR to any other.
	public static void initializeParameters(String parameterFile){		
		try{
			// Open the file to be read
			FileInputStream fstream = new FileInputStream(parameterFile);
			// Create an object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader buff = new BufferedReader(new InputStreamReader(in));
			int nLine = 0; 						// Line number
			String line;
			while((line = buff.readLine())!= null){
				nLine++; 						// Increment line number
				if(line.length() > 0){		   	// Ignore empty lines
					if(line.charAt(0) != '#'){ 	// Ignore comments
						Scanner scanner = new Scanner(line);
						scanner.useDelimiter("=");
						// Get option name and value. If not valid, exit program!
						String optionName = scanner.next().trim();
						validateOptionName(line, optionName, nLine);
						String optionValue = scanner.next().trim();   
						validateOptionValue(optionName, optionValue, nLine); 
					}
				}
			}
			in.close();
		}  // Catch open file  error.
		catch(Exception e){
			System.err.println("Error: " + e.getMessage());
		}
	}	
	
	private static void validateOptionName(String line, String option, int nLine){
		if(option.length() >= line.length())
			exitError("Line " + nLine + " --> Missing equal sign '='");
		if(!validateName(option))
			exitError("Line " + nLine + " --> INVALID OPTION NAME '" + option + "'");
	}
	
	private static boolean validateName(String name){
		for(int i = 0; i < optionNames.length; i++)
			if(name.equals((String)optionNames[i]))
				return true;
		return false;
	}
	
	// Usage of 'switch' with 'String' only legal for Java SE 7 or later!
	private static void validateOptionValue(String optionName,String optionValue, int nLine)
	throws NumberFormatException{
		// RUNS
		if(optionName.equals("nRuns")){
			HBOA.nRuns = Integer.parseInt(optionValue);
			if(HBOA.nRuns <= 0)
				exitError("Line " + nLine + " --> Number of runs to perform must be a POSITIVE integer.");
			return; // Option validated!!
		}
		// PROBLEM
		if(optionName.equals("problemType")){
			problemType = Integer.parseInt(optionValue);  // NOTE: Add new option for each new problem
			if(!((problemType >= 0 && problemType <= 7) || (problemType >= 10 && problemType <= 17) || problemType == 21 || problemType == 22 || problemType == 30)) 
				exitError("Line " + nLine + " --> INVALID Problem Type. Please check 'Parameters.txt' for the complete list of valid problems.");
			return; // Option validated!!
		}
		// NOTE: Link string size to problem type!
		if(optionName.equals("stringSize")){
			stringSize = Integer.parseInt(optionValue);
			if(stringSize <= 0)
				exitError("Line " + nLine + " --> Individual string size must be a POSITIVE integer.");
			return; // Option validated!!
		}
		if(optionName.equals("optimumValue")){
			optimumValue = Float.parseFloat(optionValue);
			// optimumValue is problem dependent, there are no restrictions on possible values.
			return; // Option validated!!  
		}
		// POPULATION
		if(optionName.equals("N")){
			HBOASolver.N = Integer.parseInt(optionValue);
			if(HBOASolver.N <= 0)
				exitError("Line " + nLine + " --> Population size must be a POSITIVE integer.");
			return; // Option validated!!
		}
		// SELECTION METHOD
		if(optionName.equals("NS")){
			pNS = Float.parseFloat(optionValue);
			if(pNS <= 0)
				exitError("Line " + nLine + " --> Selection Set size must be a POSITIVE proportion.");
			return; // Option validated!!
		}
		if(optionName.equals("selectionMethod")){
			selectionMethod = Integer.parseInt(optionValue);
			if(selectionMethod != 1 && selectionMethod != 2 && selectionMethod != 3)
				exitError("Line " + nLine + " --> INVALID selection method.");
			return; // Option validated!!
		}
		if(optionName.equals("tourSize")){
			tourSize = Integer.parseInt(optionValue);
			if(tourSize < 2)
				exitError("Line " + nLine + " --> Tournament size must be GREATER than '1'.");
			return; // Option validated!!
		}
		if(optionName.equals("tau")){
			tau = Float.parseFloat(optionValue);
			if(tau <= 0 || tau > 1)
				exitError("Line " + nLine + " --> Truncated proportion must be a number BETWEEN '0' and '1'.");
			return; // Option validated!!
		}
		// BAYESIAN NETWORK
		if(optionName.equals("bayesianMetric")){
			bayesianMetric = Integer.parseInt(optionValue);
			if(bayesianMetric != 1 && bayesianMetric != 2)
				exitError("Line " + nLine + " --> INVALID bayesian metric.");
			return; // Option validated!!
		}
		if(optionName.equals("maxVertexDegree")){
			maxVertexDegree = Integer.parseInt(optionValue);
			if(maxVertexDegree <= 0)
				exitError("Line " + nLine + " --> Maximum vertex degree must be a positive integer.");
			return; // Option validated!!
		}
		// REPLACEMENT METHOD
		if(optionName.equals("replacementType")){
			replacementType = Integer.parseInt(optionValue);
			if(replacementType != 1 && replacementType != 2 && replacementType != 3)
				exitError("Line " + nLine + " --> INVALID selection method.");
			return; // Option validated!!
		}
		if(optionName.equals("windowSize")){
			double pWindow = Float.parseFloat(optionValue);
			if(pWindow <= 0)
				exitError("Line " + nLine + " --> Window size must be a POSITIVE proportion.");
			windowSize = (int)(pWindow*stringSize); // Define 'windowSize' as a proportion of the string size.
			return; // Option validated!!
		}
		if(optionName.equals("offspringSize")){
			pOffspringSize = Float.parseFloat(optionValue);
			if(pOffspringSize  <= 0)
				exitError("Line " + nLine + " --> Offspring set size must be a POSITIVE proportion.");
			return; // Option validated!!
		}
		// STOP CRITERIA
		if(optionName.equals("maxNGen")){
			Stopper.maxNGen = Integer.parseInt(optionValue);
			if(Stopper.maxNGen <= 0)
				exitError("Line " + nLine + " --> Maximal number of generations must be a POSITIVE integer.");
			return; // Option validated!!
		}
		if(optionName.equals("maxFitCalls")){
			Stopper.maxFitCalls = Long.parseLong(optionValue);
			if(Stopper.maxFitCalls <= 0 && Stopper.maxFitCalls != -1)
				exitError("Line " + nLine + " --> Maximal number of fitness calls must be either '-1' or a POSITIVE integer.");
			return; // Option validated!!
		}
		if(optionName.equals("allFitnessEqual")){
			Stopper.allFitnessEqual = Integer.parseInt(optionValue);
			if(!(Stopper.allFitnessEqual == -1 || Stopper.allFitnessEqual == 1))
				exitError("Line " + nLine + " --> 'allFitnessEqual' must be either '-1' or '1'.");
			return; // Option validated!!
		}
		if(optionName.equals("epsilon")){
			Stopper.epsilon = Float.parseFloat(optionValue);
			if((Stopper.epsilon < 0 || Stopper.epsilon > 1) && Stopper.epsilon != -1)
				exitError("Line " + nLine + " --> Termination threshold for the univariate frequencies must be either '-1' or a number BETWEEN '0' and '1'.");
			return; // Option validated!!
		}
		if(optionName.equals("maxOptimal")){
			Stopper.maxOptimal = Float.parseFloat(optionValue);
			if((Stopper.maxOptimal < 0 || Stopper.maxOptimal > 1) && Stopper.maxOptimal != -1)
				exitError("Line " + nLine + " --> Optimal and non-optimal ind. threshold must be either '-1' or a number BETWEEN '0' and '1'.");
			return; // Option validated!!
		}
		if(optionName.equals("foundBestFit")){
			Stopper.foundBestFit = Integer.parseInt(optionValue);
			if(Stopper.foundBestFit != -1 && Stopper.foundBestFit != 1)
				exitError("Line " + nLine + " --> 	INVALID option for stopWhenFoundOptimum.");
			return; // Option validated!!
		}
		if(optionName.equals("foundOnes")){
			Stopper.foundOnes = Integer.parseInt(optionValue);
			if(Stopper.foundOnes != -1 && Stopper.foundOnes != 0 && Stopper.foundOnes != 1)
				exitError("Line " + nLine + " --> 	INVALID option for stopWhenFoundOnes.");
			return; // Option validated!!
		}		
		if(true)
			exitError("Line" + nLine +
					  " --> If you are reading this message something is FUNDAMENTALLY WRONG with 'validateOptionValue(String, String, int)'.\n" + 
					  "You may contact the author at 'unidadeimaginaria@gmail.com'\n" +
					  "Sorry for the inconvenience!");
	}// End of validateOptionValue(...)
	
	
	
	public static Problem initializeProblem(){													// Design Pattern Strategy
		switch(problemType){																	// NOTE: When implementing a new problem one must add a corresponding new case.
			// ZERO Problems		
			case 0: return new Problem(new ZeroMax(), stringSize, optimumValue, 0);
			case 1: return new Problem(new ZeroQuadratic(), stringSize, optimumValue, 0);
			case 2: return new Problem(new ZeroThreeDeceptive(), stringSize, optimumValue, 0);
			case 3: return new Problem(new ZeroThreeDeceptiveBiPolar(), stringSize, optimumValue, 0);
			case 4: return new Problem(new ZeroThreeDeceptiveOverlapping(), stringSize, optimumValue, 0);
			case 5: return new Problem(new ZeroTrapK(4), stringSize, optimumValue, 0);
			case 6: return new Problem(new ZeroUniformSixBlocks(), stringSize, optimumValue, 0);
		// ONE Problems
			case 10: return new Problem(new OneMax(), stringSize, optimumValue, 0);
			case 11: return new Problem(new Quadratic(), stringSize, optimumValue, 0);
			case 12: return new Problem(new ThreeDeceptive(), stringSize, optimumValue, 0);
			case 13: return new Problem(new ThreeDeceptiveBiPolar(), stringSize, optimumValue, 0);
			case 14: return new Problem(new ThreeDeceptiveOverlapping(), stringSize, optimumValue, 0);
			case 15: return new Problem(new TrapK(4), stringSize, optimumValue, 0);
			case 16: return new Problem(new UniformSixBlocks(), stringSize, optimumValue, 0);
			// OTHER Problems
			case 21: return new Problem(new HierarchicalTrapOne(), stringSize, optimumValue, 0);
			case 22: return new Problem(new HierarchicalTrapTwo(), stringSize, optimumValue, 0);
			case 30: return new Problem(new IsingSpin(), stringSize, optimumValue, 0);
			default: exitError("If you are reading this message something is FUNDAMENTALLY WRONG with the validation of the 'problemType' value.\n" + 
					   		"You may contact the author at 'unidadeimaginaria@gmail.com'\n" +
					   		"Sorry for the inconvenience!");
			 		 // This line is never executed!
					 return new Problem(new OneMax(), stringSize, optimumValue, 0);
		}
	}
	
	public static Selection initializeSelection(int N){
		switch(selectionMethod){
			case 1: NS = (int)(pNS*N);
					return new TourWithReplacement(NS, tourSize);
			case 2: NS = (int)(pNS*N);
					return new TourWithoutReplacement(NS, tourSize);
			case 3: NS = (int)(tau*N);
					return new Truncation(NS);
			default: exitError("If you are reading this message something is FUNDAMENTALLY WRONG with the validation of the 'selectionMethod' value.\n" + 
							   "You may contact the author at 'unidadeimaginaria@gmail.com'\n" +
							   "Sorry for the inconvenience!");
					 return new Truncation((int)(tau*N));												//  NOTE: This line is never executed!
		}
	}
	
	public static BayesianNetwork initializeBayesianNetwork(int N){
		switch(bayesianMetric){
			case 1: return new BayesianNetwork(new BDMetric(NS), (int)(pOffspringSize*N), maxVertexDegree); 
			case 2: return new BayesianNetwork(new BICMetric(), (int)(pOffspringSize*N), maxVertexDegree); 
			default: exitError("If you are reading this message something is FUNDAMENTALLY WRONG with the validation of the 'bayesianMetric' value.\n" + 
					   "You may contact the author at 'unidadeimaginaria@gmail.com'\n" +
					   "Sorry for the inconvenience!");
			 return new BayesianNetwork(new BDMetric(NS), (int)(pOffspringSize*N), maxVertexDegree);	//  NOTE: This line is never executed!
		}
	}
	
	
	public static IReplacement initializeReplacement(){
		switch(replacementType){
			case 1: return new RestrictedReplacement(windowSize, HBOASolver.N);
			case 2: return new WorstReplacement();
			case 3: return new FullReplacement();
			default: exitError(" If you are reading this message something is FUNDAMENTALLY WRONG with the validation of the 'replacementType' value.\n" + 
					   "You may contact the author at 'unidadeimaginaria@gmail.com'\n" +
					   "Sorry for the inconvenience!");
					 return new WorstReplacement();														//  NOTE: This line is never executed!
		}
	}
	
	// Input error found!! Exit program!
	public static void exitError(String message){
		System.err.println(new Error(message));
		System.exit(1);
	}
	
}// End of class Parameter





