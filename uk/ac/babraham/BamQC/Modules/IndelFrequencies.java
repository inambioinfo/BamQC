/**
 * Copyright Copyright 2015 Piero Dalle Pezze
 *
 *    This file is part of BamQC.
 *
 *    BamQC is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    BamQC is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with BamQC; if not, write to the Free Software
 *    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package uk.ac.babraham.BamQC.Modules;

import java.io.IOException;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.xml.stream.XMLStreamException;

import org.apache.log4j.Logger;

import net.sf.samtools.SAMRecord;
import uk.ac.babraham.BamQC.Annotation.AnnotationSet;
import uk.ac.babraham.BamQC.Report.HTMLReportArchive;
import uk.ac.babraham.BamQC.Sequence.SequenceFile;
import uk.ac.babraham.BamQC.Graphs.LineGraph;



/** 
 * This class re-uses the computation collected by the class VariantCallDetection
 * and plots the Indel Frequencies.
 * @author Piero Dalle Pezze
 */
public class IndelFrequencies extends AbstractQCModule {

	private static Logger log = Logger.getLogger(IndelFrequencies.class);	
	
	// original threshold for the plot y axis.
	private double maxY=1.0d; 
	
	// The analysis collecting all the results.
	VariantCallDetection variantCallDetection = null;	
	
	// data fields for plotting
	private static String[] indelNames = {
		"Insertions",
		"Deletions"};
	
	// Constructors
	/**
	 * Default constructor
	 */
	public IndelFrequencies() {	}

	
	/**
	 * Constructor. Reuse of the computation provided by VariantCallDetection analysis.
	 */
	public IndelFrequencies(VariantCallDetection vcd) {	
		variantCallDetection = vcd;
	}
	
	
	// Private methods
	
	/**
	 * Computes the maximum value for the x axis.
	 * @return xMaxValue
	 */
	private int computeXMaxValue() {
		HashMap<Integer, Long> hm = variantCallDetection.getContributingReadsPerPos();
		Integer[] readLengths = hm.keySet().toArray(new Integer[hm.size()]);
		Long[] readCounts = hm.values().toArray(new Long[hm.size()]);
		int xMaxValue = 5; // sequences long at least 5.
		long moreFrequentReadLength = 0;
		// Computes a variable threshold depending on the read length distribution of read library
		for(int i=0; i<readCounts.length; i++) {
			if(readCounts[i] > moreFrequentReadLength) {
				moreFrequentReadLength = readCounts[i];
			}
		}
		double threshold = moreFrequentReadLength * ModuleConfig.getParam("variant_call_position_indel_xaxis_threshold", "ignore").intValue() / 100d;
		// Filters the reads to show based on a the threshold computed previously.
		for(int i=0; i<readCounts.length; i++) {
			if(readCounts[i] >= threshold && xMaxValue < readLengths[i]) {
				xMaxValue = readLengths[i];
			}
			log.debug("Read Length: " + readLengths[i] + ", Num Reads: " + readCounts[i] + ", Min Accepted Length: " + threshold);
		}
		return xMaxValue+1;	//this will be used for array sizes (so +1).
	}
	
	
	
	
	// @Override methods
	
	@Override
	public void processSequence(SAMRecord read) { }
	
	
	@Override	
	public void processFile(SequenceFile file) { }

	@Override	
	public void processAnnotationSet(AnnotationSet annotation) {

	}		

	@Override	
	public JPanel getResultsPanel() {
		
		if(variantCallDetection == null) { 
			return new LineGraph(new double [][]{
					new double[ModuleConfig.getParam("variant_call_position_length", "ignore").intValue()],
					new double[ModuleConfig.getParam("variant_call_position_length", "ignore").intValue()]},
					0d, 100d, "Position in read (bp)", indelNames, 
					new String[ModuleConfig.getParam("variant_call_position_length", "ignore").intValue()], 
					"Indel Frequencies ( total insertions: 0.000 %, total deletions: 0.000 % )");
		}		
		
		long totIns = variantCallDetection.getTotalInsertions(),
				 totDel = variantCallDetection.getTotalDeletions(), 
				 totBases = variantCallDetection.getTotal();
		
		log.info("A insertions: " + variantCallDetection.getAInsertions());
		log.info("C insertions: " + variantCallDetection.getCInsertions());
		log.info("G insertions: " + variantCallDetection.getGInsertions());
		log.info("T insertions: " + variantCallDetection.getTInsertions());
		log.info("N insertions: " + variantCallDetection.getNInsertions());
		log.info("Total insertions: " + totIns + " ( " + totIns*100f/totBases + " )");
		log.info("A deletions: " + variantCallDetection.getADeletions());
		log.info("C deletions: " + variantCallDetection.getCDeletions());
		log.info("G deletions: " + variantCallDetection.getGDeletions());
		log.info("T deletions: " + variantCallDetection.getTDeletions());
		log.info("N deletions: " + variantCallDetection.getNDeletions());		
		log.info("Total deletions: " + totDel + " ( " + totDel*100f/totBases + " )");
		log.info("Skipped regions on the reads: " + variantCallDetection.getReadSkippedRegions());
		log.info("Skipped regions on the reference: " + variantCallDetection.getReferenceSkippedRegions());
		log.info("Skipped reads: " + variantCallDetection.getSkippedReads() + " ( "+ (variantCallDetection.getSkippedReads()*100.0f)/variantCallDetection.getTotalReads() + "% )");
		
		
		
		// We do not need a BaseGroup here
		// These two arrays have same length.
		long[] insertionPos = variantCallDetection.getInsertionPos();
		long[] deletionPos = variantCallDetection.getDeletionPos();
		
//		//////////////////////
//		// OLD PLOT 
//		//////////////////////
//		// initialise and configure the LineGraph
//		// compute the maximum value for the X axis
//		int maxX = computeXMaxValue();
//		String[] xCategories = new String[maxX];		
//		double[] dInsertionPos = new double[maxX];
//		double[] dDeletionPos = new double[maxX];
//		maxY = 0.0d;
//		for(int i=0; i<maxX && i<insertionPos.length; i++) {
//			dInsertionPos[i]= (double)insertionPos[i];
//			dDeletionPos[i]= (double)deletionPos[i];
//			if(dInsertionPos[i] > maxY) { maxY = dInsertionPos[i]; }
//			if(dDeletionPos[i] > maxY) { maxY = dDeletionPos[i]; }
//			xCategories[i] = String.valueOf(i+1);
//		}
//		// add 10% to the maximum for improving the plot rendering
//		maxY = maxY + maxY*0.05; 
		
		long[] totalPos = variantCallDetection.getTotalPos();
     	// initialise and configure the LineGraph
		// compute the maximum value for the X axis
		int maxX = computeXMaxValue();
		//maxX = insertionPos.length;
		String[] xCategories = new String[maxX];		
		double[] dInsertionPos = new double[maxX];
		double[] dDeletionPos = new double[maxX];
		for(int i=0; i<maxX && i<insertionPos.length; i++) {
			dInsertionPos[i]= (insertionPos[i] * 100d) / totalPos[i];
			dDeletionPos[i]= (deletionPos[i] * 100d) / totalPos[i];
			if(dInsertionPos[i] > maxY) { maxY = dInsertionPos[i]; }
			if(dDeletionPos[i] > maxY) { maxY = dDeletionPos[i]; }
			xCategories[i] = String.valueOf(i+1);
		}
		

		double[][] indelData = new double [][] {dInsertionPos,dDeletionPos};
		String title = String.format("Indel Frequencies ( total insertions: %.3f %%, total deletions: %.3f %% )", 
				totIns*100.0f/totBases,totDel*100.0f/totBases);		
	
		return new LineGraph(indelData, 0d, Math.ceil(maxY), "Position in read (bp)", indelNames, xCategories, title);

	}

	@Override	
	public String name() {
		return "Indel Frequencies";
	}

	@Override	
	public String description() {
		return "Looks at the Indel frequencies in the data";
	}

	@Override	
	public void reset() { }

	@Override	
	public boolean raisesError() {
		if(maxY > ModuleConfig.getParam("variant_call_position_indel_threshold", "error"))
			return true;		
		return false;
	}

	@Override	
	public boolean raisesWarning() {
		if(maxY > ModuleConfig.getParam("variant_call_position_indel_threshold", "warn"))
			return true;		
		return false;
	}

	@Override	
	public boolean needsToSeeSequences() {
		return false;
	}

	@Override	
	public boolean needsToSeeAnnotation() {
		return false;
	}

	@Override	
	public boolean ignoreInReport() {
		if(variantCallDetection == null) { return true; }
		return variantCallDetection.getTotal() == 0;
	}

	@Override	
	public void makeReport(HTMLReportArchive report) throws XMLStreamException, IOException {
		super.writeDefaultImage(report, "indel_frequencies.png", "Indel Frequencies", 800, 600);
		
	
//		StringBuffer sb = report.dataDocument();
//		
//		sb.append("#Total Deduplicated Percentage\t");
//		sb.append(percentDifferentSeqs);
//		sb.append("\n");
//		
//		sb.append("#Duplication Level\tPercentage of deduplicated\tPercentage of total\n");
//		for (int i=0;i<labels.length;i++) {
//			sb.append(labels[i]);
//			if (i == labels.length-1) {
//				sb.append("+");
//			}
//			sb.append("\t");
//			sb.append(deduplicatedPercentages[i]);
//			sb.append("\t");
//			sb.append(totalPercentages[i]);
//			sb.append("\n");
//		}
				
		
		
		
	}
	
}
