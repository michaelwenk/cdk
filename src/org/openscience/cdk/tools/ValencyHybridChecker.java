/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2005  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
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
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.tools;

import java.io.IOException;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.Bond;
import org.openscience.cdk.interfaces.ChemObjectBuilder;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.CDKException;

/**
 * This class is an experimental alternative to the ValencyChecker.
 * The main difference is that this checker uses a different atom type
 * list and takes formal charges <i>and</i> hybridization into account: 
 * it first matches against element, formal charge and hybridization,
 * and then browses the list for possible matching types. It extends
 * ValencyChecker and only overwrites the <code>couldMatchAtomType</code>
 * method.
 *
 * <p>The atoms are matched against the hybridization_atomtype.xml list.
 *
 * @author       Egon Willighagen
 * @cdk.created  2004-06-12
 *
 * @cdk.keyword  atom, valency
 */
public class ValencyHybridChecker implements ValencyCheckerInterface {

	private String atomTypeList = null;
	protected AtomTypeFactory structgenATF;
	protected LoggingTool logger;

	public ValencyHybridChecker() throws IOException, ClassNotFoundException {
        this("org/openscience/cdk/config/data/hybridization_atomtypes.xml");
	}

	public ValencyHybridChecker(String atomTypeList) throws IOException, ClassNotFoundException {
		this.atomTypeList = atomTypeList;
		logger = new LoggingTool(this);
        logger.info("Using configuration file: ", atomTypeList);
	}

	/**
	 * Saturates a molecule by setting appropriate bond orders.
	 *
	 * @cdk.keyword            bond order, calculation
     *
     * @cdk.created 2003-10-03
	 */
    public void saturate(IAtomContainer atomContainer) throws CDKException {
        logger.info("Saturating atomContainer by adjusting bond orders...");
        boolean allSaturated = allSaturated(atomContainer);
        if (!allSaturated) {
            logger.info("Saturating bond orders is needed...");
            boolean succeeded = saturate(atomContainer.getBonds(), atomContainer);
            if (!succeeded) {
                throw new CDKException("Could not saturate this atomContainer!");
            }
        }
    }

    /**
     * Saturates a set of Bonds in an AtomContainer.
     */
    public boolean saturate(org.openscience.cdk.interfaces.Bond[] bonds, IAtomContainer atomContainer) throws CDKException {
        logger.debug("Saturating bond set of size: ", bonds.length);
        boolean bondsAreFullySaturated = false;
        if (bonds.length > 0) {
        	org.openscience.cdk.interfaces.Bond bond = bonds[0];

            // determine bonds left
            int leftBondCount = bonds.length-1;
            Bond[] leftBonds = new Bond[leftBondCount];
            System.arraycopy(bonds, 1, leftBonds, 0, leftBondCount);

            // examine this bond
            logger.debug("Examining this bond: ", bond);
            if (isSaturated(bond, atomContainer)) {
                logger.debug("OK, bond is saturated, now try to saturate remaining bonds (if needed)");
                bondsAreFullySaturated = saturate(leftBonds, atomContainer);
            } else if (isUnsaturated(bond, atomContainer)) {
                logger.debug("Ok, this bond is unsaturated, and can be saturated");
                // two options now: 
                // 1. saturate this one directly
                // 2. saturate this one by saturating the rest
                logger.debug("Option 1: Saturating this bond directly, then trying to saturate rest");
                // considering organic bonds, the max order is 3, so increase twice
                double increment = 1.0;
                boolean bondOrderIncreased = saturateByIncreasingBondOrder(bond, atomContainer, increment);
                bondsAreFullySaturated = bondOrderIncreased && saturate(bonds, atomContainer);
                if (bondsAreFullySaturated) {
                    logger.debug("Option 1: worked");
                } else {
                    logger.debug("Option 1: failed. Trying option 2.");
                    logger.debug("Option 2: Saturing this bond by saturating the rest");
                    // revert the increase (if succeeded), then saturate the rest
                    if (bondOrderIncreased) unsaturateByDecreasingBondOrder(bond, increment);
                    bondsAreFullySaturated = saturate(leftBonds, atomContainer) &&
                                             isSaturated(bond, atomContainer);
                    if (!bondsAreFullySaturated) logger.debug("Option 2: failed");
                }
            } else {
                logger.debug("Ok, this bond is unsaturated, but cannot be saturated");
                // try recursing and see if that fixes things
                bondsAreFullySaturated = saturate(leftBonds, atomContainer) &&
                                         isSaturated(bond, atomContainer);
            }
        } else {
            bondsAreFullySaturated = true; // empty is saturated by default
        }
        return bondsAreFullySaturated;
    }

    public boolean unsaturateByDecreasingBondOrder(org.openscience.cdk.interfaces.Bond bond, double decrement) {
        if (bond.getOrder() > decrement) {
            bond.setOrder(bond.getOrder() - decrement);
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Returns wether a bond is unsaturated. A bond is unsaturated if 
     * <b>all</b> Atoms in the bond are unsaturated.
     */
    public boolean isUnsaturated(org.openscience.cdk.interfaces.Bond bond, IAtomContainer atomContainer) throws CDKException {
        logger.debug("isBondUnsaturated?: ", bond);
        org.openscience.cdk.interfaces.IAtom[] atoms = bond.getAtoms();
        boolean isUnsaturated = true;
        for (int i=0; i<atoms.length && isUnsaturated; i++) {
            isUnsaturated = isUnsaturated && !isSaturated(atoms[i], atomContainer);
        }
        logger.debug("Bond is unsaturated?: ", isUnsaturated);
        return isUnsaturated;
    }

    /**
     * Tries to saturate a bond by increasing its bond orders by 1.0.
     *
     * @return true if the bond could be increased
     */
    public boolean saturateByIncreasingBondOrder(org.openscience.cdk.interfaces.Bond bond, IAtomContainer atomContainer, double increment) throws CDKException {
    	org.openscience.cdk.interfaces.IAtom[] atoms = bond.getAtoms();
    	org.openscience.cdk.interfaces.IAtom atom = atoms[0];
    	org.openscience.cdk.interfaces.IAtom partner = atoms[1];
        logger.debug("  saturating bond: ", atom.getSymbol(), "-", partner.getSymbol());
        org.openscience.cdk.interfaces.IAtomType[] atomTypes1 = getAtomTypeFactory(bond.getBuilder()).getAtomTypes(atom.getSymbol());
        org.openscience.cdk.interfaces.IAtomType[] atomTypes2 = getAtomTypeFactory(bond.getBuilder()).getAtomTypes(partner.getSymbol());
        for (int atCounter1=0; atCounter1<atomTypes1.length; atCounter1++) {
            org.openscience.cdk.interfaces.IAtomType aType1 = atomTypes1[atCounter1];
            logger.debug("  condidering atom type: ", aType1);
            if (couldMatchAtomType(atomContainer, atom, aType1)) {
                logger.debug("  trying atom type: ", aType1);
                for (int atCounter2=0; atCounter2<atomTypes2.length; atCounter2++) {
                    org.openscience.cdk.interfaces.IAtomType aType2 = atomTypes2[atCounter2];
                    logger.debug("  condidering partner type: ", aType1);
                    if (couldMatchAtomType(atomContainer, partner, atomTypes2[atCounter2])) {
                        logger.debug("    with atom type: ", aType2);
                        if (bond.getOrder() < aType2.getMaxBondOrder() && 
                        bond.getOrder() < aType1.getMaxBondOrder()) {
                            bond.setOrder(bond.getOrder() + increment);
                            logger.debug("Bond order now ", bond.getOrder());
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns wether a bond is saturated. A bond is saturated if 
     * <b>both</b> Atoms in the bond are saturated.
     */
    public boolean isSaturated(org.openscience.cdk.interfaces.Bond bond, org.openscience.cdk.interfaces.IAtomContainer atomContainer) throws CDKException {
        logger.debug("isBondSaturated?: ", bond);
        org.openscience.cdk.interfaces.IAtom[] atoms = bond.getAtoms();
        boolean isSaturated = true;
        for (int i=0; i<atoms.length; i++) {
            logger.debug("isSaturated(Bond, AC): atom I=", i);
            isSaturated = isSaturated && isSaturated(atoms[i], atomContainer);
        }
        logger.debug("isSaturated(Bond, AC): result=", isSaturated);
        return isSaturated;
    }

    /**
     * Determines of all atoms on the AtomContainer are saturated.
     */
	public boolean isSaturated(org.openscience.cdk.interfaces.IAtomContainer container) throws CDKException {
        return allSaturated(container);
    }
	public boolean allSaturated(org.openscience.cdk.interfaces.IAtomContainer ac) throws CDKException {
        logger.debug("Are all atoms saturated?");
        for (int f = 0; f < ac.getAtomCount(); f++) {
            if (!isSaturated(ac.getAtomAt(f), ac)) {
                return false;
            }
        }
        return true;
    }

	/**
     * Determines if the atom can be of type AtomType. That is, it sees if this
     * AtomType only differs in bond orders, or implicit hydrogen count.
     */
    public boolean couldMatchAtomType(IAtom atom, double bondOrderSum, double maxBondOrder, IAtomType type) {
        logger.debug("couldMatchAtomType:   ... matching atom ", atom, " vs ", type);
        int hcount = atom.getHydrogenCount();
        int charge = atom.getFormalCharge();
        if (charge == type.getFormalCharge()) {
            logger.debug("couldMatchAtomType:     formal charge matches...");
            if (atom.getHybridization() == type.getHybridization()) {
                logger.debug("couldMatchAtomType:     hybridization is OK...");
                if (bondOrderSum + hcount <= type.getBondOrderSum()) {
                    logger.debug("couldMatchAtomType:     bond order sum is OK...");
                    if (maxBondOrder <= type.getMaxBondOrder()) {
                        logger.debug("couldMatchAtomType:     max bond order is OK... We have a match!");
                        return true;
                    }
                } else {
                    logger.debug("couldMatchAtomType:      no match", "" + 
                        (bondOrderSum + hcount), " > ", "" + type.getBondOrderSum());
                }
            }
        } else {
            logger.debug("couldMatchAtomType:     formal charge does NOT match...");
        }
        logger.debug("couldMatchAtomType:    No Match");
        return false;
    }

    /** 
     * Calculates the number of hydrogens that can be added to the given atom to fullfil
     * the atom's valency. It will return 0 for PseudoAtoms, and for atoms for which it
     * does not have an entry in the configuration file.
     */
	public int calculateNumberOfImplicitHydrogens(IAtom atom, double bondOrderSum, double maxBondOrder, int neighbourCount) 
        throws CDKException {

        int missingHydrogens = 0;
        if (atom instanceof PseudoAtom) {
            logger.debug("don't figure it out... it simply does not lack H's");
            return 0;
        }
        
        logger.debug("Calculating number of missing hydrogen atoms");
        // get default atom
        org.openscience.cdk.interfaces.IAtomType[] atomTypes = getAtomTypeFactory(atom.getBuilder()).getAtomTypes(atom.getSymbol());
        if (atomTypes.length == 0) {
            logger.warn("Element not found in configuration file: ", atom);
            return 0;
        }

        logger.debug("Found atomtypes: ", atomTypes.length);
        for (int f = 0; f < atomTypes.length; f++) {
            org.openscience.cdk.interfaces.IAtomType type = atomTypes[f];
            if (couldMatchAtomType(atom, bondOrderSum, maxBondOrder, type)) {
                logger.debug("This type matches: ", type);
                int formalNeighbourCount = type.getFormalNeighbourCount();
                if (type.getHybridization() == CDKConstants.HYBRIDIZATION_UNSET) {
                    missingHydrogens = (int) (type.getBondOrderSum() - bondOrderSum);
                } else {
                    switch (atom.getHybridization()) {
                        case CDKConstants.HYBRIDIZATION_SP3:
                            missingHydrogens = formalNeighbourCount - neighbourCount; break;
                        case CDKConstants.HYBRIDIZATION_SP2:
                            missingHydrogens = formalNeighbourCount - neighbourCount; break;
                        case CDKConstants.HYBRIDIZATION_SP1:
                            missingHydrogens = formalNeighbourCount - neighbourCount; break;
                        default:
                            missingHydrogens = (int) (type.getBondOrderSum() - bondOrderSum);
                    }
                }
                break;
            }
        }
        
        logger.debug("missing hydrogens: ", missingHydrogens);
        return missingHydrogens;
    }

    /**
     * Checks wether an Atom is saturated by comparing it with known AtomTypes.
     * It returns true if the atom is an PseudoAtom and when the element is not in the list.
     */
	public boolean isSaturated(IAtom atom, IAtomContainer container) throws CDKException {
        if (atom instanceof PseudoAtom) {
            logger.debug("don't figure it out... it simply does not lack H's");
            return true;
        }

		org.openscience.cdk.interfaces.IAtomType[] atomTypes = getAtomTypeFactory(atom.getBuilder()).getAtomTypes(atom.getSymbol());
        if (atomTypes.length == 0) {
            logger.warn("Missing entry in atom type list for ", atom.getSymbol());
            return true;
        }
        double bondOrderSum = container.getBondOrderSum(atom);
        double maxBondOrder = container.getMaximumBondOrder(atom);
        int hcount = atom.getHydrogenCount();
        int charge = atom.getFormalCharge();

        logger.debug("Checking saturation of atom ", atom.getSymbol());
        logger.debug("bondOrderSum: ", bondOrderSum);
        logger.debug("maxBondOrder: ", maxBondOrder);
        logger.debug("hcount: ", hcount);
        logger.debug("charge: ", charge);

        boolean elementPlusChargeMatches = false;
        for (int f = 0; f < atomTypes.length; f++) {
            org.openscience.cdk.interfaces.IAtomType type = atomTypes[f];
            if (couldMatchAtomType(atom, bondOrderSum, maxBondOrder, type)) {
                if (bondOrderSum + hcount == type.getBondOrderSum() && 
                    maxBondOrder <= type.getMaxBondOrder()) {
                    logger.debug("We have a match: ", type);
                    logger.debug("Atom is saturated: ", atom.getSymbol());
                    return true;
                } else {
                    // ok, the element and charge matche, but unfulfilled
                    elementPlusChargeMatches = true;
                }
            } // else: formal charges don't match
        }
        
        if (elementPlusChargeMatches) {
            logger.debug("No, atom is not saturated.");
            return false;
        }
        
        // ok, the found atom was not in the list
        throw new CDKException("The atom with element " + atom.getSymbol() +
                               " and charge " + charge + " is not found.");
    }

	public int calculateNumberOfImplicitHydrogens(org.openscience.cdk.interfaces.IAtom atom, org.openscience.cdk.interfaces.IAtomContainer container) throws CDKException {
        return this.calculateNumberOfImplicitHydrogens(atom, 
            container.getBondOrderSum(atom),
            container.getMaximumBondOrder(atom),
            container.getConnectedAtoms(atom).length
        );
    }

    protected AtomTypeFactory getAtomTypeFactory(ChemObjectBuilder builder) throws CDKException {
        if (structgenATF == null) {
            try {
                structgenATF = AtomTypeFactory.getInstance(atomTypeList, builder);
            } catch (Exception exception) {
                logger.debug(exception);
                throw new CDKException("Could not instantiate AtomTypeFactory!", exception);
            }
        }
        return structgenATF;
    }

    /**
     * Determines if the atom can be of type AtomType.
     */
    public boolean couldMatchAtomType(org.openscience.cdk.interfaces.IAtomContainer container, org.openscience.cdk.interfaces.IAtom atom, IAtomType type) {
        double bondOrderSum = container.getBondOrderSum(atom);
        double maxBondOrder = container.getMaximumBondOrder(atom);
        return couldMatchAtomType(atom, bondOrderSum, maxBondOrder, type);
    }

}

