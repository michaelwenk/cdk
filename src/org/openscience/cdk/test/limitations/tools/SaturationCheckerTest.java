/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2003-2005  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.test.limitations.tools;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.Ring;
import org.openscience.cdk.interfaces.RingSet;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.SaturationChecker;

/**
 * This class shows some limitations of algorithms in the SaturationChecker
 * class.
 *
 * @cdk.module test
 * 
 * @author     egonw
 * @cdk.created    2003-11-02
 *
 * @see org.openscience.cdk.tools.SaturationChecker
 */
public class SaturationCheckerTest extends CDKTestCase {

    SaturationChecker satcheck = null;
    
    public SaturationCheckerTest(String name) {
        super(name);
    }
    
    public void setUp() {
        try {
            satcheck = new SaturationChecker();
        } catch (Exception e) {
            fail();
        }
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(SaturationCheckerTest.class);
        return suite;
    }

    /**
     * Tests the method saturate().
     */
    public void testSaturate_WithNitrate() throws CDKException {
        Molecule mol = new Molecule();
        Atom a1 = new Atom("O");
        mol.addAtom(a1);
        Atom a2 = new Atom("N");
        mol.addAtom(a2);
        Atom a3 = new Atom("O");
        mol.addAtom(a3);
        Atom a4 = new Atom("C");
        mol.addAtom(a4);
        Atom a5 = new Atom("C");
        mol.addAtom(a5);
        Atom a6 = new Atom("C");
        mol.addAtom(a6);
        Atom a7 = new Atom("H");
        mol.addAtom(a7);
        Atom a8 = new Atom("C");
        mol.addAtom(a8);
        Atom a9 = new Atom("C");
        mol.addAtom(a9);
        Atom a10 = new Atom("H");
        mol.addAtom(a10);
        Atom a11 = new Atom("H");
        mol.addAtom(a11);
        Atom a12 = new Atom("C");
        mol.addAtom(a12);
        Atom a13 = new Atom("H");
        mol.addAtom(a13);
        Atom a14 = new Atom("N");
        mol.addAtom(a14);
        Atom a15 = new Atom("H");
        mol.addAtom(a15);
        Atom a16 = new Atom("H");
        mol.addAtom(a16);
        Bond b1 = new Bond(a2, a1, 1.0);
        mol.addBond(b1);
        Bond b2 = new Bond(a3, a2, 1.0);
        mol.addBond(b2);
        Bond b3 = new Bond(a2, a4, 1.0);
        mol.addBond(b3);
        Bond b4 = new Bond(a5, a4, 1.0);
        mol.addBond(b4);
        Bond b5 = new Bond(a4, a6, 1.0);
        mol.addBond(b5);
        Bond b6 = new Bond(a7, a5, 1.0);
        mol.addBond(b6);
        Bond b7 = new Bond(a8, a5, 1.0);
        mol.addBond(b7);
        Bond b8 = new Bond(a6, a9, 1.0);
        mol.addBond(b8);
        Bond b9 = new Bond(a6, a10, 1.0);
        mol.addBond(b9);
        Bond b10 = new Bond(a11, a8, 1.0);
        mol.addBond(b10);
        Bond b11 = new Bond(a8, a12, 1.0);
        mol.addBond(b11);
        Bond b12 = new Bond(a9, a13, 1.0);
        mol.addBond(b12);
        Bond b13 = new Bond(a12, a9, 1.0);
        mol.addBond(b13);
        Bond b14 = new Bond(a14, a12, 1.0);
        mol.addBond(b14);
        Bond b15 = new Bond(a15, a14, 1.0);
        mol.addBond(b15);
        Bond b16 = new Bond(a14, a16, 1.0);
        mol.addBond(b16);
        satcheck.saturate(mol);
        assertEquals(2.0, b1.getOrder(), 0.0001);
        assertEquals(2.0, b2.getOrder(), 0.0001);
    }
    
    /**
     * Tests the method saturate().
     */
    public void testSaturation_S4AtomType() throws CDKException {
        Molecule mol = new Molecule();
        Atom a1 = new Atom("N");
        mol.addAtom(a1);
        Atom a2 = new Atom("H");
        mol.addAtom(a2);
        Atom a3 = new Atom("C");
        mol.addAtom(a3);
        Atom a4 = new Atom("S");
        mol.addAtom(a4);
        Atom a5 = new Atom("O");
        mol.addAtom(a5);
        Atom a6 = new Atom("C");
        mol.addAtom(a6);
        Atom a7 = new Atom("O");
        mol.addAtom(a7);
        Atom a8 = new Atom("O");
        mol.addAtom(a8);
        Atom a9 = new Atom("C");
        mol.addAtom(a9);
        Atom a10 = new Atom("H");
        mol.addAtom(a10);
        Atom a11 = new Atom("H");
        mol.addAtom(a11);
        Atom a12 = new Atom("H");
        mol.addAtom(a12);
        Atom a13 = new Atom("C");
        mol.addAtom(a13);
        Atom a14 = new Atom("C");
        mol.addAtom(a14);
        Atom a15 = new Atom("H");
        mol.addAtom(a15);
        Atom a16 = new Atom("C");
        mol.addAtom(a16);
        Atom a17 = new Atom("H");
        mol.addAtom(a17);
        Atom a18 = new Atom("C");
        mol.addAtom(a18);
        Atom a19 = new Atom("C");
        mol.addAtom(a19);
        Atom a20 = new Atom("H");
        mol.addAtom(a20);
        Atom a21 = new Atom("H");
        mol.addAtom(a21);
        Atom a22 = new Atom("N");
        mol.addAtom(a22);
        Atom a23 = new Atom("H");
        mol.addAtom(a23);
        Atom a24 = new Atom("H");
        mol.addAtom(a24);
        Bond b1 = new Bond(a2, a1, 1.0);
        mol.addBond(b1);
        Bond b2 = new Bond(a3, a1, 1.0);
        mol.addBond(b2);
        Bond b3 = new Bond(a1, a4, 1.0);
        mol.addBond(b3);
        Bond b4 = new Bond(a3, a5, 1.0);
        mol.addBond(b4);
        Bond b5 = new Bond(a6, a3, 1.0);
        mol.addBond(b5);
        Bond b6 = new Bond(a7, a4, 1.0);
        mol.addBond(b6);
        Bond b7 = new Bond(a8, a4, 1.0);
        mol.addBond(b7);
        Bond b8 = new Bond(a4, a9, 1.0);
        mol.addBond(b8);
        Bond b9 = new Bond(a10, a6, 1.0);
        mol.addBond(b9);
        Bond b10 = new Bond(a6, a11, 1.0);
        mol.addBond(b10);
        Bond b11 = new Bond(a6, a12, 1.0);
        mol.addBond(b11);
        Bond b12 = new Bond(a9, a13, 1.0);
        mol.addBond(b12);
        Bond b13 = new Bond(a9, a14, 1.0);
        mol.addBond(b13);
        Bond b14 = new Bond(a15, a13, 1.0);
        mol.addBond(b14);
        Bond b15 = new Bond(a13, a16, 1.0);
        mol.addBond(b15);
        Bond b16 = new Bond(a17, a14, 1.0);
        mol.addBond(b16);
        Bond b17 = new Bond(a14, a18, 1.0);
        mol.addBond(b17);
        Bond b18 = new Bond(a16, a19, 1.0);
        mol.addBond(b18);
        Bond b19 = new Bond(a16, a20, 1.0);
        mol.addBond(b19);
        Bond b20 = new Bond(a18, a19, 1.0);
        mol.addBond(b20);
        Bond b21 = new Bond(a18, a21, 1.0);
        mol.addBond(b21);
        Bond b22 = new Bond(a19, a22, 1.0);
        mol.addBond(b22);
        Bond b23 = new Bond(a22, a23, 1.0);
        mol.addBond(b23);
        Bond b24 = new Bond(a22, a24, 1.0);
        mol.addBond(b24);
        satcheck.saturate(mol);
        assertEquals(2.0, b6.getOrder(), 0.001);
        assertEquals(2.0, b7.getOrder(), 0.001);
    }
    
    /**
     * Tests the method saturate().
     */
    public void testSaturate_NumberingProblem() throws CDKException {
        Molecule mol = new Molecule();
        Atom a1 = new Atom("C");
        mol.addAtom(a1);
        Atom a2 = new Atom("C");
        mol.addAtom(a2);
        Atom a3 = new Atom("C");
        mol.addAtom(a3);
        Atom a4 = new Atom("H");
        mol.addAtom(a4);
        Atom a5 = new Atom("C");
        mol.addAtom(a5);
        Atom a6 = new Atom("H");
        mol.addAtom(a6);
        Atom a7 = new Atom("S");
        mol.addAtom(a7);
        Atom a8 = new Atom("H");
        mol.addAtom(a8);
        Atom a9 = new Atom("H");
        mol.addAtom(a9);
        Bond b1 = new Bond(a1, a2, 1.0);
        mol.addBond(b1);
        Bond b2 = new Bond(a1, a3, 1.0);
        mol.addBond(b2);
        Bond b3 = new Bond(a1, a4, 1.0);
        mol.addBond(b3);
        Bond b4 = new Bond(a5, a2, 1.0);
        mol.addBond(b4);
        Bond b5 = new Bond(a2, a6, 1.0);
        mol.addBond(b5);
        Bond b6 = new Bond(a3, a7, 1.0);
        mol.addBond(b6);
        Bond b7 = new Bond(a3, a8, 1.0);
        mol.addBond(b7);
        Bond b8 = new Bond(a7, a5, 1.0);
        mol.addBond(b8);
        Bond b9 = new Bond(a5, a9, 1.0);
        mol.addBond(b9);
        satcheck.saturate(mol);
        assertEquals(1.0, b1.getOrder(), 0.001);
        assertEquals(2.0, b2.getOrder(), 0.001);
        assertEquals(1.0, b6.getOrder(), 0.001);
        assertEquals(1.0, b8.getOrder(), 0.001);
        assertEquals(2.0, b4.getOrder(), 0.001);
    }
    
    /**
     * Tests wether the saturation checker gets a proton right.
     */
	public void testIsSaturated_Proton() throws CDKException {
		// test H+
		Molecule m = new Molecule();
		Atom h = new Atom("H");
        h.setFormalCharge(+1);
		m.addAtom(h);
		assertTrue(satcheck.isSaturated(h, m));
	}
    
    public void testCalculateMissingHydrogens_Aromatic() throws CDKException{
	    Molecule pyrrole = MoleculeFactory.makePyrrole();
	    org.openscience.cdk.interfaces.IAtom n = pyrrole.getAtomAt(1);
	    RingSet rs = (new SSSRFinder(pyrrole)).findSSSR();
	    Ring ring = (Ring) rs.get(0);
	    for (int j=0 ; j<ring.getBondCount(); j++)
	    {
		    ring.getBondAt(j).setOrder(1.5);
	    }
	    assertEquals(5, ring.getBondCount());
	    assertEquals(1, satcheck.calculateNumberOfImplicitHydrogens(n, pyrrole));
    }

}

