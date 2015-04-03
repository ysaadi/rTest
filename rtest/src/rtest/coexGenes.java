package rtest;
import java.io.*;
import java.awt.*;

import javax.swing.*;

import org.rosuda.JRI.RVector;
import org.rosuda.JRI.Rengine;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.RConsoleOutputStream;

import java.util.Scanner;

//path C:\Users\yaman\Documents\senior design\Sham_SDraw.csv
public class coexGenes {
	public static void main(String [ ] args) {
		Rengine re = new Rengine(args, false, new TextConsole()); 
		
        
		re.eval("library(coexGenes)");
		
		REXP x;
		REXP y;
		
        String path1 = "C:\\Users\\Brian\\Documents\\R\\filtered.csv";
        String paff1 = path1.replace('\\', '/');
        
        re.eval("filename =" + "'" + paff1 + "'", false);
        
        System.out.println(re.eval("filename"));
        // re.eval("filename= 'C:/Users/Brian/Documents/R/filtered.csv';", false);
        re.eval("data = read.table(filename, header = TRUE, sep = ',', row.names=1);", false);
		re.eval("data = as.matrix(data);", false);

		// System.out.println(x=re.eval("data"));
		System.out.println();
		System.out.println(y=re.eval("dim(data)"));
		System.out.println();
		re.eval("opt = optNumClust(data, metric = c('default'), bLog = 0, bScale = 1)", false);
		re.eval("opt$optNo", false);

		re.eval("k = opt$optNo;", false);
		// re.eval("fAgrLevel = 0.70;", false);
		String agrLvlS = "0.70";
        double agrLvlD = Double.valueOf(agrLvlS);
        
        System.out.println();
        System.out.println("The agreement level chosen is: ");
        re.eval("fAgrLevel = " + agrLvlD + ";", false);
        
        re.eval("print(fAgrLevel)", false);
        // System.out.println(re.eval("print(fAgrLevel)", false));
        System.out.println();
        
        
        //
		re.eval("results = coexPatt(data, nCluster=k, fAgrLevel=fAgrLevel, GS = TRUE, metric = c('default'), bLog = 0, bScale = 1, nResamplingTimes = 20, pValue=1);", false);
		re.eval("str(results);", false);
		
		re.eval("dput(results, paste(filename,'_',k,'_',100*fAgrLevel,'.txt',sep=''));", false);
        
		// PARSE results from .txt to java to get view of data
		
		
		re.eval("sdata = cbind(data,0);", false);
		re.eval("colnames(sdata)[ncol(sdata)] = 'cluster';", false);
		re.eval("for (i in 1:length(results$flstGroup)){sdata[results$flstGroup[[i]],ncol(sdata)] = i;}", false);
		re.eval("selData = data.getSelDomain(sdata, results$flstGroup);", false);
		re.eval("write.table(selData, paste(filename,'_sel_',100*fAgrLevel,'.csv',sep=''), sep = ',', col.names = NA, quote = FALSE);", false);
        
		     //####   " '_sel_' "  <- opportunity to iterate filenames
		
		// !!! plotting of the clusters !!! \\
		re.eval("data_scale = t(scale(t(data)));", false);
		String tpoints = "0,0,0,2,2,2,4,4,4,8,8,8,16,16,16,24,24,24";
        re.eval("xarray = c(" + tpoints + ");", false);
        //re.eval("xarray = c(0,0,0,2,2,2,4,4,4,8,8,8,16,16,16,24,24,24);", false);
		re.eval("yrange = c(-2,2);", false);
		
		// re.eval("myplot.avgdataND(data_scale, results$flstGroup, color=c(), xarray=xarray, yrange=yrange, overlapped=F);", false);

		// NEED- Way to get covariates from prior filtered data
		
		
		
	re.startMainLoop();
    }
}
