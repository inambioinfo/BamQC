/**
 * Copyright Copyright 2010-14 Simon Andrews
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
/*
 * Changelog: 
 * - Piero Dalle Pezze: Imported from SeqMonk and adjusted for BamQC
 * - Simon Andrews: Class creation.
 */
package uk.ac.babraham.BamQC.Utilities.FileFilters;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * 
 * @author Simon Andrews
 *
 */
public class GFFFileFilter extends FileFilter {

	@Override
	public boolean accept(File f) {
		if (f.isDirectory() || f.getName().toLowerCase().endsWith(".gff") || f.getName().toLowerCase().endsWith(".gff3") || f.getName().toLowerCase().endsWith(".gtf")) {
			return true;
		}
		return false;
	}

	@Override
	public String getDescription() {
		return "GFF/GTF Files";
	}

}
