/*******************************************************************************
 * Copyright (c) 2013  Egon Willighagen <egon.willighagen@gmail.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.opentox.nm.business;

import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.Recorded;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.managers.business.IBioclipseManager;

@PublishedClass(
    value="OpenTox support for nanomaterials."
)
public interface IOpenToxNmManager extends IBioclipseManager {

    @Recorded
    @PublishedMethod(
        methodSummary=
            "Downloads a material and returns it as a NMX formated String.",
        params="String compoundURI"
    )
    public String downloadMaterialAsNMXFile(String materialURI) throws BioclipseException;
	
}
