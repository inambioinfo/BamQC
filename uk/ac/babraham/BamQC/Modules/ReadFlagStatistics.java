/**
 * Copyright Copyright 2014 Bart Ailey Eagle Genomics Ltd
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

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.xml.stream.XMLStreamException;

import net.sf.samtools.SAMRecord;

import org.apache.log4j.Logger;

import uk.ac.babraham.BamQC.Annotation.AnnotationSet;
import uk.ac.babraham.BamQC.Report.HTMLReportArchive;
import uk.ac.babraham.BamQC.Sequence.SequenceFile;

public class ReadFlagStatistics extends AbstractQCModule {

	private static final int FIRST_BIT = 0x01;
	private static final int SECOND_BIT = 0x02;
	private static final int THIRD_BIT = 0x04;
	private static final int TENTH_BIT = 0x200;
	private static final int ELEVENTH_BIT = 0x400;
	private static final int ROWS = 6;

	private static Logger log = Logger.getLogger(ReadFlagStatistics.class);

	private int readNumber = 0;
	private int pairNumber = 0;
	private int mappedPairNumber = 0;
	private int mappedNumber = 0;
	private int duplicateNumber = 0;
	private int failedQualityControlNumber = 0;

	private String[] resultNames = new String[] { "Read Number", "Reads mapped %", "Mapped pair %", "Properly mapped Pair %","FailedQuality Control %" ,"Duplicate %"};
	
	@Override
	public void reset() {
		readNumber = 0;
		pairNumber = 0;
		mappedPairNumber = 0;
		failedQualityControlNumber = 0;
		duplicateNumber = 0;
	}

	@Override
	public void processSequence(SAMRecord read) {
		int flag = read.getFlags();
		/*
		boolean pair = (flag & FIRST_BIT) == FIRST_BIT;
		boolean mapped = (flag & THIRD_BIT) != THIRD_BIT;
		boolean mappedPair = (flag & SECOND_BIT) == SECOND_BIT;
		boolean failedQualityControl = (flag & TENTH_BIT) == TENTH_BIT;
		boolean duplicate = (flag & ELEVENTH_BIT) == ELEVENTH_BIT;
		*/

		readNumber++;

		if (read.getReadPairedFlag()) pairNumber++;
		if (! read.getReadUnmappedFlag()) mappedNumber++;
		if (read.getProperPairFlag()) mappedPairNumber++;
		if (read.getReadFailsVendorQualityCheckFlag()) failedQualityControlNumber++;
		if (read.getDuplicateReadFlag()) duplicateNumber++;

		log.debug("flag = " + flag);
	}

	private String getPercentage(int count, int total) {
		return String.format("%.3f", 100 * (double) count / (double) total);
	}

	@Override
	public void processFile(SequenceFile file) {
		log.info("processFile called");
	}

	@Override
	public void processAnnotationSet(AnnotationSet annotation) {
		throw new UnsupportedOperationException("processAnnotationSet called");
	}

	@Override
	public JPanel getResultsPanel() {
		JPanel returnPanel = new JPanel();
		returnPanel.setLayout(new BorderLayout());
		returnPanel.add(new JLabel("Read Flag Statistics", JLabel.CENTER), BorderLayout.NORTH);

		TableModel model = new ResultsTable();

		returnPanel.add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);

		return returnPanel;
	}

	private class ResultsTable extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
		private String[] results = new String[ROWS];

		public ResultsTable() {
			results[0] = String.format("%d", readNumber);
			results[1] = getPercentage(mappedNumber, readNumber);
			results[2] = getPercentage(pairNumber, readNumber);
			results[3] = getPercentage(mappedPairNumber, readNumber);
			results[4] = getPercentage(failedQualityControlNumber, readNumber);
			results[5] = getPercentage(duplicateNumber, readNumber);
		}

		@Override
		public int getRowCount() {
			return ROWS;
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (columnIndex == 0) {
				return resultNames[rowIndex];
			}
			else {
				return results[rowIndex];
			}
		}

		public String getColumnName(int columnIndex) {
			switch (columnIndex) {
			case 0:
				return "Measure";
			case 1:
				return "Value";
			}
			return null;
		}

		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case 0:
				return String.class;
			case 1:
				return Long.class;
			}
			return null;
		}
	}

	@Override
	public String name() {
		return "Read Flag Statistics";
	}

	@Override
	public String description() {
		return "Statistics of the Read's Flag field";
	}

	@Override
	public boolean raisesError() {
		return false;
	}

	@Override
	public boolean raisesWarning() {
		return false;
	}

	@Override
	public boolean needsToSeeSequences() {
		return true;
	}

	@Override
	public boolean needsToSeeAnnotation() {
		return false;
	}

	@Override
	public boolean ignoreInReport() {
		return false;
	}

	@Override
	public void makeReport(HTMLReportArchive report) throws XMLStreamException, IOException {
		super.writeTable(report, new ResultsTable());
	}

	public int getReadNumber() {
		return readNumber;
	}

	public int getPairNumber() {
		return pairNumber;
	}

	public int getDuplicateNumber() {
		return duplicateNumber;
	}

	public int getFailedQualityControlNumber() {
		return failedQualityControlNumber;
	}

	public int getMappedPairNumber() {
		return mappedPairNumber;
	}

}
