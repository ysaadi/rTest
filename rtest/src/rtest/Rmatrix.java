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
	        System.out.println(Arrays.toString((Object[]) testing.header));
	        
		
	}
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
			headcols=re.eval("temp[1,]").asStringArray();
		}
		Rmatrix ret= new Rmatrix(rowNames, headcols, matrix);
		return ret;
	}
}
