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

import java.util.List;

import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.Recorded;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.IMaterial;
import net.bioclipse.jobs.BioclipseUIJob;
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
	
    @Recorded
    @PublishedMethod(
        methodSummary="Creates a new dataset.",
        params="String service, List<? extends IMaterial>  materials"
    )
    public String createDataset(String service, List<? extends IMaterial>  materials) throws BioclipseException;
    public void createDataset(String service, List<? extends IMaterial> materials, BioclipseUIJob<String> uiJob) throws BioclipseException;

    @Recorded
    @PublishedMethod(
        methodSummary=
            "Predicts modeled properties for the given list of materials.",
        params="String service, String model, List<? extends IMaterial> materials"
    )
    public List<String> predictWithModel(String service, String model, List<? extends IMaterial> materials);

    @Recorded
    @PublishedMethod(
        methodSummary=
            "Predicts modeled properties for the given material.",
        params="String service, String model, IMaterial material"
    )
    public List<String> predictWithModel(String service, String model, IMaterial material) throws Exception;

    @Recorded
    @PublishedMethod(
        methodSummary=
            "Calculates a descriptor value for a single material.",
        params="String service, String descriptor, IMaterial material"
    )
    public List<String> calculateDescriptor(String service, String descriptor, IMaterial material) throws Exception;

    @Recorded
    @PublishedMethod(
        methodSummary=
            "Calculates a descriptor value for a set of molecules.",
        params="String service, String descriptor, List<? extends IMaterial> materials"
    )
    public List<String> calculateDescriptor(String service, String descriptor, List<? extends IMaterial> materials) throws Exception;

}
