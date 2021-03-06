/**
 * Copyright Copyright 2015 Simon Andrews
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
 * - Piero Dalle Pezze: Class creation. Code taken from Picard Library and adapted.
 */
package uk.ac.babraham.BamQC.Utilities.CigarMD;

/**
 * One component of a CigarMD string.
 */
public class CigarMDElement {
	
    private final int length;
    private final CigarMDOperator operator;
    private final String bases;

    /**
     * Constructor.
     * @param length The length of the CigarMD element.
     * @param operator The operator of the CigarMD element.
     * @param bases The bases for this CigarMD element.
     */
    public CigarMDElement(final int length, final CigarMDOperator operator, final String bases) {
        this.length = length;
        this.operator = operator;
        this.bases = bases;
    }

    /**
     * Return the length of the CigarMD element.
     * @return the length
     */
    public int getLength() {
        return length;
    }

    /**
     * Return the operator of the CigarMD element.
     * @return the operator
     */
    public CigarMDOperator getOperator() {
        return operator;
    }
    
    /**
     * Return the bases for the CigarMD element.
     * @return the bases
     */
    public String getBases() {
        return bases;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof CigarMDElement)) return false;

        final CigarMDElement that = (CigarMDElement) o;

        if (length != that.length) return false;
        if (operator != that.operator) return false;
        if (bases != that.bases) return false;
        
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = length;
        result = 31 * result + (operator != null ? operator.hashCode() : 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
    	return new StringBuilder().append(length).append(operator).append(bases).toString();
    }
    
}