package rtest;

import org.rosuda.JRI.Rengine;

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
public class Rmatrix {
	String[] rowNames;
	String[] header;
	double[][] matrix;
	public Rmatrix(String[] rowNames, String[] header, double[][] matrix){
		this.rowNames=rowNames; this.header=header; this.matrix=matrix;
	}
	public static void main(String args[]){
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
	        String file= "C:\\\\Users\\\\yaman\\\\Documents\\\\senior design\\\\Sham_SDraw.csv";
//	        hero.loadDataSetR(re, file, true, 1, "savethis");
//	        re.eval("X=rownames(savethis,do.NULL=FALSE)");
//	        REXP X= re.eval("X");
//	        String[] names= X.asStringArray();
//	        System.out.println(Arrays.toString((Object[])names));
//	        hero.loadDataSetR(re, file, false, 1, "saveAs");
//	        REXP Z=re.eval("Z=saveAs[1,]");
//	        double[] hmm= Z.asDoubleArray();
//	        System.out.println(Arrays.toString(hmm));
//	        int[] dim= re.eval("dim(saveAs)").asIntArray();
//	        System.out.println(Arrays.toString(dim));
//	        System.out.println(names.length);
	        Rmatrix testing= loadRDataSet(re,file,true,1,"savehere");
	        
//	        System.out.println(Arrays.toString((Object[]) testing.header));
//	        sendRDataFrame(re, testing, "shave");
//	        String[] backForth=re.eval("colnames(shave, do.NULL=FALSE)").asStringArray();
//	        System.out.println(Arrays.toString((Object[]) backForth));
//	        String[] testingString=null;
//	        String[] testingString2= {"a","b"};
//	        if(true){
//	        testingString= new String[2];
//	        testingString[1]="printg";
//	        System.out.println(testingString[1]);
	        }
	/*loads a dataset from a csv file into R and saves it as String saveas, then also saves all the assets of the csv into 
	 * an Rmatrix datatype.
	 */
	public static Rmatrix loadRDataSet(Rengine re, String file,boolean header, int rowName, String saveAs){
		//rowNames here will indicate the index of the rownames in the dataset.  R will store that column as the names for the rows
		//that follow it.
		String bool= "";
		if(header==true){
			bool="TRUE";
		}
		else{
			bool="FALSE";
		}
		file=hero.quote(file);
		re.eval("filename=" + file + ";");
		re.eval(saveAs+ "= read.table(" + file + ", header = " + bool + ", sep= \",\"," +"row.names=" + rowName+ ");");
		System.out.println(saveAs+ "= read.table(" + file + ", header =" + bool + ", sep= \",\"," +"row.names=" + rowName+ ");");
		re.eval(saveAs + "=" + "as.matrix(" + saveAs + ");");
		REXP x= re.eval(saveAs);
		double[][] matrix= x.asDoubleMatrix();
		REXP RrowNames=re.eval("rownames(" + saveAs +", do.NULL=FALSE);");
		String[] rowNames=RrowNames.asStringArray();
		String[] headcols= null;
		if(header){       /*if not header: ie if header is false */
			System.out.println("temp= read.table(" + file + ", header =FALSE, sep= \",\"," +" nrows=1);");
			re.eval("temp= read.table(" + file + ", header =FALSE, sep= \",\"," +", nrows=1);");
			re.eval("temp=as.matrix(temp)");
			re.eval("unname(temp)");
			headcols=re.eval("temp[1,]").asStringArray();
		}
		Rmatrix ret= new Rmatrix(rowNames, headcols, matrix);
		return ret;
	}

	public static void sendRDataFrame(Rengine re, Rmatrix mat, String saveAs){
		// takes a 2 d double array and saves it in R as the variable Name.
		int len= mat.matrix[0].length;
		int height= mat.matrix.length;
		String strLen=Integer.toString(len);
		String strHeight=Integer.toString(height);
		int colptr=0;
		int rowptr=0;
		int counter=0;
		double[] vec= new double[len*height];
		for(rowptr=0;rowptr<height;rowptr++){
			for(colptr=0; colptr<len; colptr++){
				vec[counter]=mat.matrix[rowptr][colptr];
				counter++;
				}
			}
		re.assign("vecTemp", vec);
		String evaluateThis=saveAs + " = matrix(vecTemp, nrow =" + strHeight + ", ncol=" +strLen + ", byrow = TRUE"+ ")";
		re.eval(evaluateThis);
		re.assign("rownames", mat.rowNames);
		re.assign("colnamess", makeColNames(mat.header));
		re.eval("row.names("+ saveAs +") <-rownames ");
		re.eval("colnames("+ saveAs +") <-colnamess ");
		//System.out.println(Arrays.toString(vec));
		//printArray(arr);
		System.out.println(evaluateThis);
	}
	public static Rmatrix loadRmatrixObj(Rengine re, String saved){
		Rmatrix returned= new Rmatrix(null,null,null);
		returned.matrix=re.eval(saved).asDoubleMatrix();
		returned.header=re.eval("colnames("+ saved +");").asStringArray();
		returned.rowNames=re.eval("rownames("+ saved +");").asStringArray();
		return returned;
		
	}
	public static String [] makeColNames(String[] colNames){
		//the behavior of this method is wierd, it creates a matrix one larger than what I initially wanted 
		//it to create, but for some reason, R accepts it?
		int ln= colNames.length;
		String[] col= new String[ln-1];
		String ind;
		for( int x=1; x<ln-1; x++){
		ind= colNames[x];
				/*R does not allow colnames to start with numbers, so i 
				 * check if its a number by converting it to ascii*/
		if((int) ind.charAt(0)>=46 && (int) ind.charAt(0)<=57){ 
			ind= "X." +ind;  
		}
		col[x]=ind;
		}
		return col;
	}
	public static String [] revertColNames(String[] jcolNames){
		int ln= jcolNames.length;
		String[] col= new String[ln];
		String ind;
		for( int x=0; x<ln; x++){
		ind= jcolNames[x];
		if( ind.substring(0,2).equals("X.")){
			ind= ind.substring(2);
		}
		col[x]=ind;
		}
		return col;
	}
	public boolean equals(Object other){
		if (other == null)
		   {
		      return false;
		   }

		   if (this.getClass() != other.getClass())
		   {
		      return false;
		   }
		   if(Arrays.equals(this.header, ((Rmatrix)other).header)  && Arrays.equals(this.rowNames, ((Rmatrix)other).rowNames) && Arrays.deepEquals(this.matrix,((Rmatrix)other).matrix)){
			   return true;
		   }
		return false;
	}
}
