/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2002  The Chemistry Development Kit (CKD) project
 *
 *  Contact: steinbeck@ice.mpg.de
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All I ask is that proper credit is given for my work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk.test;

import org.openscience.cdk.*;
import org.openscience.cdk.tools.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.renderer.*;
import org.openscience.cdk.layout.*;
import org.openscience.cdk.templates.*;
import org.openscience.cdk.geometry.*;
import org.openscience.cdk.aromaticity.*;
import org.openscience.cdk.smiles.*;

import java.io.*;
import javax.vecmath.*;
import javax.swing.*;
import javax.swing.tree.*;
import java.util.*;
import java.awt.*;

import junit.framework.*;

/**
 *  Tests the HOSECode genertor. This is *not* a JUnit test class!
 *
 *@author     steinbeck
 *@created    November 16, 2002
 */
public class HOSECodeTest extends TestCase
{
	
	static boolean standAlone = false;
	

	/**
	 *  Constructor for the HOSECodeTest object
	 *
	 *@param  name  Description of the Parameter
	 */
	public HOSECodeTest(String name) {
		super(name);
	}

		/**
	 *  A unit test suite for JUnit
	 *
	 *@return    The test suite
	 */
	public static Test suite()
	{
		return new TestSuite(HOSECodeTest.class);
	}

	
	
	/**
	 *  A unit test for JUnit
	 *
	 *@return    Description of the Return Value
	 */
	public void test1()
	{
		Molecule molecule;
		String s = null;
		boolean isAromatic = false;
		try
		{
			molecule = MoleculeFactory.makeIndole();
			//display(molecule);
			isAromatic = HueckelAromaticityDetector.detectAromaticity(molecule);
			HOSECodeGenerator hcg = new HOSECodeGenerator();
			//System.out.println("Listing 1-sphere HOSE codes for Indole:\n");
			for (int f = 0; f < molecule.getAtomCount(); f++)
			{
				s = hcg.getHOSECode(molecule, molecule.getAtomAt(f), 1);
				//System.out.println("Atom " + (f + 1) + ": " + s);
			}
		} catch (Exception exc)
		{
			exc.printStackTrace();
		}
	}
	


	/**
	 *  A unit test for JUnit
	 *
	 *@return    Description of the Return Value
	 */
	public void test2()
	{
		try
		{
			Molecule molecule = (new SmilesParser()).parseSmiles("O=c1ccc2ccc4c5ccccc5Oc3c(OC)cc1c2c34");
			//display(molecule);
			boolean isAromatic = HueckelAromaticityDetector.detectAromaticity(molecule);
			HOSECodeGenerator hcg = new HOSECodeGenerator();
			//System.out.println("Listing 1-sphere HOSE codes for Indole:\n");
			String s = null;
			for (int f = 0; f < molecule.getAtomCount(); f++)
			{
				s = hcg.getHOSECode(molecule, molecule.getAtomAt(f), 4);
				//System.out.println("Atom " + (f + 1) + ": " + s);
			}
		} catch (Exception exc)
		{
			exc.printStackTrace();
		}

	}

	/**
	 *  A unit test for JUnit
	 *
	 *@return    Description of the Return Value
	 */
	public void test3()
	{
		try
		{
			Molecule molecule = (new SmilesParser()).parseSmiles("C12=CC=CC=C1NC=C2");
			//display(molecule);
			boolean isAromatic = HueckelAromaticityDetector.detectAromaticity(molecule);
			HOSECodeGenerator hcg = new HOSECodeGenerator();
			String s = null;
			for (int f = 0; f < molecule.getAtomCount(); f++)
			{
				s = hcg.getHOSECode(molecule, molecule.getAtomAt(f), 4);
				//System.out.println("Atom " + (f + 1) + ": " + s);
			}
		} catch (Exception exc)
		{
			exc.printStackTrace();
		}
}

	/**
	 *  A unit test for JUnit
	 *
	 *@return    Description of the Return Value
	 */
	public void test4()
	{
		try
		{
			Molecule molecule = (new SmilesParser()).parseSmiles("C1(C=CN2)=C2C=CC=C1");
			//display(molecule);
			boolean isAromatic = HueckelAromaticityDetector.detectAromaticity(molecule);
			HOSECodeGenerator hcg = new HOSECodeGenerator();
			String s = null;
			for (int f = 0; f < molecule.getAtomCount(); f++)
			{
				s = hcg.getHOSECode(molecule, molecule.getAtomAt(f), 4);
				//System.out.println("Atom " + (f + 1) + ": " + s);
			}
		} catch (Exception exc)
		{
			exc.printStackTrace();

		}

	}
	
	public void testBug655169()
	{
		Molecule molecule = null;
		HOSECodeGenerator hcg = null;

		try
		{
			molecule = (new SmilesParser()).parseSmiles("CC=CBr");
			boolean isAromatic = HueckelAromaticityDetector.detectAromaticity(molecule);
			hcg = new HOSECodeGenerator();
			String s = null;
			for (int f = 0; f < molecule.getAtomCount(); f++)
			{
				//System.out.println("Atom " + molecule.getAtomAt(f).getSymbol() + "-" + (f + 1));
				s = hcg.getHOSECode(molecule, molecule.getAtomAt(f), 4);
				//System.out.println(molecule.getAtomAt(f).flags[CDKConstants.ISAROMATIC]);
				//System.out.println(s);
			}
            Bond[] bonds = molecule.getBonds();
			for (int f = 0; f < bonds.length; f++)
			{
				//System.out.println(bonds[f].flags[CDKConstants.ISAROMATIC]);
			}

		} catch (Exception exc)
		{
			exc.printStackTrace();
		}
		
		JFrame frame = new JFrame("HOSECodeTest");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());
		DefaultMutableTreeNode top = hcg.getRootNode();
		StructureDiagramGenerator sdg = new StructureDiagramGenerator();
		MoleculeViewer2D mv = new MoleculeViewer2D();
		Renderer2DModel r2dm = mv.getRenderer2DModel();
		r2dm.setDrawNumbers(true);

		try
		{
			sdg.setMolecule((Molecule) molecule.clone());
			sdg.generateCoordinates(new Vector2d(0, 1));
			mv.setAtomContainer(sdg.getMolecule());
		} catch (Exception exc)
		{
			//System.out.println("*** Exit due to an unexpected error during coordinate generation ***");
			exc.printStackTrace();
		}
		
		final JTree tree = new JTree(top);
		JScrollPane treeView = new JScrollPane(tree);
		frame.getContentPane().add("West", treeView);
		mv.setPreferredSize(new Dimension(400,400));
		frame.getContentPane().add("Center", mv);
		for (int f = 0; f < tree.getRowCount(); f ++)
		{
			tree.expandRow(f);	
		}
		frame.pack();
		frame.show();
	}

	private void assembleNodes(DefaultMutableTreeNode top, HOSECodeGenerator hcg)
	{
		DefaultMutableTreeNode node = null;
		
	}
	
	
	/**
	 *  The main program for the HOSECodeTest class
	 *
	 *@param  args  The command line arguments
	 */
	public static void main(String[] args)
	{
		standAlone = true;
		HOSECodeTest hct = new HOSECodeTest("HOSECodeTest");
		//hct.test1();
		//hct.test2();
		//hct.test3();
		//hct.test4();
		//hct.testBug655169();
	}
}

