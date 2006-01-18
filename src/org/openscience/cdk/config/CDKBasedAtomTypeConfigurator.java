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
package org.openscience.cdk.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import org.openscience.cdk.interfaces.ChemObjectBuilder;
import org.openscience.cdk.config.atomtypes.AtomTypeReader;
import org.openscience.cdk.tools.LoggingTool;

/**
 * AtomType resource that reads the atom type configuration from an XML file.
 * The format is required to be in the STMML format {@cdk.cite PMR2002}; examples
 * can be found in CVS in the src/org/openscience/cdk/config/data directory.
 *
 * @cdk.module core
 */
public class CDKBasedAtomTypeConfigurator implements AtomTypeConfigurator {

    private String configFile = "org.openscience.cdk.config.data.structgen_atomtypes.xml";
    private InputStream ins = null;
    
    private LoggingTool logger;
    
    public CDKBasedAtomTypeConfigurator() {
        logger = new LoggingTool(this);
    }
    
    public void setInputStream(InputStream ins) {
        this.ins = ins;
    }
    
    public Vector readAtomTypes(ChemObjectBuilder builder) throws IOException {
        Vector atomTypes = new Vector(0);
        if (ins == null) {
            try {
                ins = this.getClass().getClassLoader().getResourceAsStream(configFile);
            } catch(Exception exc) {
                logger.error(exc.getMessage());
                logger.debug(exc);
                throw new IOException("There was a problem getting a stream for " + configFile +
                                      " with getClass.getClassLoader.getResourceAsStream");
            }
            if (ins == null) {
                try {
                    ins = this.getClass().getResourceAsStream(configFile);
                } catch(Exception exc) {
                    logger.error(exc.getMessage());
                    logger.debug(exc);
                    throw new IOException("There was a problem getting a stream for " + configFile +
                                          " with getClass.getResourceAsStream");
                }
            }
        }
        if (ins == null) throw new IOException("There was a problem getting an input stream");
        AtomTypeReader reader = new AtomTypeReader(new InputStreamReader(ins));
        atomTypes = reader.readAtomTypes(builder);
        for (int f = 0; f < atomTypes.size(); f++) {
            Object object = atomTypes.elementAt(f);
            if (object == null) {
                System.out.println("Expecting an object but found null!");
                if (!(object instanceof org.openscience.cdk.interfaces.IAtomType)) {
                    System.out.println("Expecting cdk.AtomType class, but got: " + object.getClass().getName());
                }
            }
        }
        return atomTypes;
    }
   
}
