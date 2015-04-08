package rtest;
import java.io.*;
import java.awt.Frame;
import java.awt.FileDialog;
import java.util.Arrays;
import java.util.Enumeration;

import org.rosuda.JRI.Rengine;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RList;
import org.rosuda.JRI.RVector;
import org.rosuda.JRI.RMainLoopCallbacks;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.Scanner;
public class hero {
	//path C:\Users\yaman\Documents\senior design\Sham_SDraw.csv
	public static void main(String args[]){
		
			// just making sure we have the right version of everything
			if (!Rengine.versionCheck()) {
			    System.err.println("** Version mismatch - Java files don't match library version.");
			    System.exit(1);
			}
		        System.out.println("Creating Rengine (with arguments)");
				// 1) we pass the arguments from the command line
				// 2) we won't use the main loop at first, we'll start it later
				//    (that's the "false" as second argument)
				// 3) the callbacks are implemented by the TextConsole class above
				Rengine re=new Rengine(args, false, new TextConsole());
				TextConsole he= new TextConsole();
				he.rFlushConsole(re);
		        System.out.println("Rengine created, waiting for R");
		        re.eval("library(coexGenes)");
		        String path= "C:\\\\Users\\\\yaman\\\\Documents\\\\senior design\\\\Sham_SDraw.csv";
		       //String a=pathConvert();
		       String a=path;
		       a= a.replace('\\', '/');
		       re.eval("filename =" + "'" + a + "'", false);
		       System.out.println(re.eval("filename"));
		       re.eval("data = read.table(filename, header = TRUE, sep = ',', row.names=1);", false);
		       re.eval("data = as.matrix(data);", false);
		       REXP x=re.eval("data");
		       double [][] dub=x.asDoubleMatrix();
		       System.out.println(x);
		       //printArray(dub);
		       re.assign("trying", x);
		       REXP trying=re.eval("trying");
		       double [][] dub2=trying.asDoubleMatrix();
		       if(dub2==dub){
		    	   System.out.println("success!");
		       }
//		       double[][] heroooo={{1.,2.,3.},{4.,5.,6.}};
//		       System.out.println(heroooo.length);
//		       System.out.println(heroooo[0].length);
//		       System.out.println(heroooo[0][2]);
//		createRMatrix(heroooo,re,"welp");
//		REXP welp=re.eval("welp");
//		double[][] crap= welp.asDoubleMatrix();
//		printArray(crap);
//		printArray(heroooo);
//		if(Arrays.deepEquals((Object[]) heroooo,(Object[]) crap)){
//			System.out.println("YES");
//		}
//		int[] testVectors= {1,2,3,4};
//		re.assign("face", testVectors);
//		REXP RtestVectors= re.eval("face");
//		int[] hereWeGo= RtestVectors.asIntArray();
//		System.out.println(Arrays.toString(testVectors));
//		System.out.println(Arrays.toString(hereWeGo));
		double[][] tryingHard= loadDataSetR(re, path, true, 1, "temp");
		int[] covariates= {2,2,2,1,1,1,2,2,2,1,1,1,3,3,3,1,1,1};
		//printArray(tryingHard);
		createRMatrix(tryingHard, re,"seeing" );
		double[][] seeing= re.eval("seeing").asDoubleMatrix();
		//printArray(seeing);
		if( Arrays.deepEquals((Object[]) tryingHard, (Object[]) seeing)){
			System.out.println("the two big test arrays are equal");
		}
		else{
			System.out.println("got here but your stupid");
		}
		ANOVA(re, tryingHard,covariates, .001, 0, "C:\\\\Users\\\\yaman\\\\Documents\\\\senior design\\\\SDanova" );
		double[][] res=loadDataSetR(re, "C:\\\\Users\\\\yaman\\\\Documents\\\\senior design\\\\SDanova", true, 1, "temp");
		//printArray(res);
	}
	
	public static void printArray(double matrix[][]) {
	    for (double[] row : matrix) 
	        System.out.println(Arrays.toString(row));       
	}
	public static void createRMatrix(double[][] arr, Rengine re, String varName){
		// takes a 2 d double array and saves it in R as the variable Name.
		int len= arr[0].length;
		int height=arr.length;
		String strLen=Integer.toString(len);
		String strHeight=Integer.toString(height);
		int colptr=0;
		int rowptr=0;
		int counter=0;
		double[] vec= new double[len*height];
		for(rowptr=0;rowptr<height;rowptr++){
			for(colptr=0; colptr<len; colptr++){
				vec[counter]=arr[rowptr][colptr];
				counter++;
				}
			}
		re.assign("vecTemp", vec);
		String evaluateThis=varName + " = matrix(vecTemp, nrow =" + strHeight + ", ncol=" +strLen + ", byrow = TRUE"+ ")";
		re.eval(evaluateThis);
		
		//System.out.println(Arrays.toString(vec));
		//printArray(arr);
		System.out.println(evaluateThis);
		
	}
	public static void ANOVA( Rengine re, double[][] matrix, int[] covariates, double pvalue, double truncValue, String outFile){
		
		createRMatrix(matrix, re, "shamwow");
		String Spval= Double.toString(pvalue);
		String Strunc= Double.toString(truncValue);
		re.assign("cov",covariates);
		System.out.println("filter.ANOVA(shamwow, cov, pvalue = " +Spval + ",trunc_val=" + Strunc +", outfile= \"" + outFile + "\"" + ");");
		re.eval("filter.ANOVA(shamwow, cov, pvalue = " +Spval + ",trunc_val=" + Strunc +", outfile= \"" + outFile + "\"" + ");" );
		
	}
//	public static void ANOVA(Rengine re, String data, int[]covariates, double pvalue, double truncValue, String outFile){
//		//data is the path for the file/csv
//		
//	}
	public static double[][] loadDataSetR(Rengine re, String file,boolean header, int rowName, String saveAs){
		//takes a path of a csv and creates a matrix in R.  readTable asks for header and rowName.
		String bool;
		if(header==true){
			bool="TRUE";
		}
		else{
			bool="FALSE";
		}
		file=quote(file);
		re.eval("filename=" + file + ";");
		re.eval(saveAs+ "= read.table(" + file + ", header = " + bool + ", sep= \",\"," +"row.names=" + rowName+ ");");
		System.out.println(saveAs+ "= read.table(" + file + ", header =" + bool + " sep= \",\"," +"row.names=" + rowName+ ");");
		re.eval(saveAs + "=" + "as.matrix(" + saveAs + ");");
		REXP x= re.eval(saveAs);
		double[][] matrix= x.asDoubleMatrix();
		return matrix;
	}
	public static double optNumClust(Rengine re, double[][] data, String metric, double bLog, double bScale ){
		createRMatrix(data, re, "temp");
		String SbScale= Double.toString(bScale);
		String SbLog= Double.toString(bLog);
		re.eval("optTem = optNumClust(temp , metric = c(\"" + metric + "\"), bLog = " +SbLog + ", bScale=" +SbScale);
		REXP temp= re.eval("optTem");
		double d= temp.asDouble();
		return d;
	}
	
	public static String pathConvert(){
		System.out.println("input path");
		Scanner sc = new Scanner(System.in);
		String path="";
		String p=sc.nextLine();
		int ptr=0;
		while(ptr<p.length()){
			char c= p.charAt(ptr);
			ptr++;
			if(c== '\\'){
				path=path+ "\\";
			}
			else{
				path= path+c;
			}
		}
		sc.close();
		return path;
	}
	
	public static double[][] coexCluster(Rengine re, double[][] data,  double NumCluster, double FagrLevel, boolean GS, String Metric, double bLog, double bScale, double nResamplingTimes, double pValue){
		String SNumCluster= Double.toString(NumCluster);
		String SFagrLevel= Double.toString(FagrLevel);
		String SGS= Boolean.toString(GS).toUpperCase();
		String SbLog= Double.toString(bLog);
		String SbScale= Double.toString(bScale);
		String SsampleTime= Double.toString(nResamplingTimes);
		String SpValue= Double.toString(pValue);
		createRMatrix(data, re, "data");
		re.eval("results= coexPatt(data, nCluster=" + SNumCluster +", fAgrLevel=" +SFagrLevel + ", GS= " +SGS +", metric= c(\"" +Metric + "\"), bLog = " + SbLog + ", bScale=" +SbScale + ", nResamplingTimes = " + SsampleTime + ", pValue=" + SpValue + ");");
		REXP res= re.eval("results");
		double[][] here= res.asDoubleMatrix();
		return here;
		
	}
	public static String quote(String s){
		//surrounds a string with quotes.
		s= "\"" + s + "\"";
		return s;
	}
//		public static String anova(Rengine re, int[] covariates, int pvalue,  ){
//			
//		}
}
