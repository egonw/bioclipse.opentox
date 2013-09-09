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

import net.bioclipse.business.BioclipsePlatformManager;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.managers.business.IBioclipseManager;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

public class OpenToxNmManager implements IBioclipseManager {

    private BioclipsePlatformManager bioclipse = new BioclipsePlatformManager();

    public String downloadMaterialAsNMXFile(String materialURI, IProgressMonitor monitor)
    		throws BioclipseException {
    	if (monitor == null) monitor = new NullProgressMonitor();

    	monitor.beginTask("Downloading material...", 1);
    	String result = bioclipse.download(
    		materialURI, "chemical/x-cml", monitor
    	);
    	monitor.done();

    	return result;
    }
    
    /**
     * Gives a short one word name of the manager used as variable name when
     * scripting.
     */
    public String getManagerName() {
        return "opentoxnm";
    }
}
