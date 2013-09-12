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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.bioclipse.business.BioclipsePlatformManager;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.IMaterial;
import net.bioclipse.core.domain.StringMatrix;
import net.bioclipse.jobs.IReturner;
import net.bioclipse.managers.business.IBioclipseManager;
import net.bioclipse.opentox.api.ModelAlgorithm;
import net.bioclipse.opentox.api.MolecularDescriptorAlgorithm;
import net.bioclipse.opentox.nm.api.Dataset;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

public class OpenToxNmManager implements IBioclipseManager {

	private static final Logger logger = Logger.getLogger(OpenToxNmManager.class);

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

    public void createDataset(String service, List<IMaterial> materials, IReturner<String> returner, IProgressMonitor monitor)
    throws BioclipseException {
    	if (monitor == null) monitor = new NullProgressMonitor();
    	
    	monitor.beginTask("Creating an OpenTox API data set ...", 1);
    	try {
			String dataset = Dataset.createNewDataset(service, materials, monitor);
			monitor.done();
			returner.completeReturn( dataset ); 
		} catch (Exception exc) {
			throw new BioclipseException(
				"Exception while creating dataset: " + exc.getMessage()
			);
		}
    }


    public List<String> predictWithModel(String service, String model, List<IMaterial> materials, IProgressMonitor monitor)
    throws Exception {
    	if (service == null) throw new BioclipseException("Service is null");
    	if (model == null) throw new BioclipseException("Model is null");

    	if (monitor == null) monitor = new NullProgressMonitor();
    	monitor.beginTask("Calculate model for dataset", materials.size());

    	List<String> calcResults = new ArrayList<String>();
    	for (IMaterial material : materials) {
    		List<IMaterial> shortMaterialList = new ArrayList<IMaterial>();
    		shortMaterialList.add(material);
    		String dataset = Dataset.createNewDataset(service, shortMaterialList, monitor);
    		if (dataset == null) {
        		logger.error("Failed to generate a data set");
        		return calcResults;
        	}
        	if (monitor.isCanceled()) return calcResults;
    		String results = ModelAlgorithm.calculate(service, model, dataset, monitor);    		
        	if (monitor.isCanceled()) return calcResults;
    		StringMatrix features = net.bioclipse.opentox.api.Dataset.listPredictedFeatures(results);
    		calcResults.addAll(removeDataType(features.getColumn("numval")));
    		net.bioclipse.opentox.api.Dataset.deleteDataset(dataset);
    		monitor.worked(1);
    	}
    	
    	return calcResults;
    }

    public List<String> predictWithModel(String service, String model,
    		IMaterial material, IProgressMonitor monitor)
        			throws Exception {
    	if (service == null) throw new BioclipseException("Service is null");
    	if (model == null) throw new BioclipseException("Model is null");

    	if (monitor == null) monitor = new NullProgressMonitor();
    	monitor.beginTask("Calculate model for molecule", 1);

    	List<String> calcResults = new ArrayList<String>();
    	List<IMaterial> shortMaterialList = new ArrayList<IMaterial>();
		shortMaterialList.add(material);
    	String dataset = Dataset.createNewDataset(service, shortMaterialList, monitor);
    	if (dataset == null) {
    		logger.error("Failed to generate a data set");
    		return calcResults;
    	}
    	if (monitor.isCanceled()) return calcResults;
    	String results = ModelAlgorithm.calculate(service, model, dataset, monitor);
    	if (monitor.isCanceled()) return calcResults;
    	StringMatrix features = net.bioclipse.opentox.api.Dataset.listPredictedFeatures(results);
    	calcResults.addAll(removeDataType(features.getColumn("numval")));
    	net.bioclipse.opentox.api.Dataset.deleteDataset(dataset);
    	monitor.worked(1);

    	return calcResults;
    }

    public List<String> calculateDescriptor(
    		String service, String descriptor,
    		IMaterial material, IProgressMonitor monitor)
    throws Exception {
    	if (monitor == null) monitor = new NullProgressMonitor();
    	monitor.beginTask("Calculate descriptor for material", 1);

    	List<String> calcResults = new ArrayList<String>();
    	logger.debug("Creating data set");
    	List<IMaterial> shortMaterialList = new ArrayList<IMaterial>();
		shortMaterialList.add(material);
    	String dataset = Dataset.createNewDataset(service, shortMaterialList, monitor);
    	if (dataset == null) {
    		logger.error("Failed to generate a data set");
    		return calcResults;
    	}
    	logger.debug("Calculating descriptor");
    	if (monitor.isCanceled()) return Collections.emptyList();
    	String results = MolecularDescriptorAlgorithm.calculate(
    		service, descriptor, dataset, monitor
    	);
    	if (monitor.isCanceled()) return Collections.emptyList();
    	logger.debug("Listing features");
    	StringMatrix features = net.bioclipse.opentox.api.Dataset.listPredictedFeatures(results);
    	logger.debug("Pred: " + features);
    	calcResults.addAll(removeDataType(features.getColumn("numval")));
    	logger.debug("Deleting data set");
    	net.bioclipse.opentox.api.Dataset.deleteDataset(dataset);
    	monitor.worked(1);
    	
    	return calcResults;
    }

    public List<String> calculateDescriptor(
    		String service, String descriptor,
    		List<IMaterial> materials, IProgressMonitor monitor)
    throws Exception {
    	if (service == null) throw new BioclipseException("Service is null");
    	if (descriptor== null) throw new BioclipseException("Descriptor is null");

    	if (monitor == null) monitor = new NullProgressMonitor();
    	monitor.beginTask("Calculate descriptor for dataset", materials.size());

    	List<String> calcResults = new ArrayList<String>();
    	for (IMaterial material : materials) {
    		List<IMaterial> shortMaterialList = new ArrayList<IMaterial>();
    		shortMaterialList.add(material);
    		String dataset = Dataset.createNewDataset(service, shortMaterialList, monitor);
    		if (monitor.isCanceled()) continue;
    		String results = MolecularDescriptorAlgorithm.calculate(
 				service, descriptor, dataset, monitor
    		);
    		if (monitor.isCanceled()) continue;
    		StringMatrix features = net.bioclipse.opentox.api.Dataset.listPredictedFeatures(results);
    		calcResults.addAll(removeDataType(features.getColumn("numval")));
    		net.bioclipse.opentox.api.Dataset.deleteDataset(dataset);
    		monitor.worked(1);
    	}
    	
    	return calcResults;
    }

    private List<String> removeDataType(List<String> column) {
		List<String> cleanedData = new ArrayList<String>(column.size());
		for (String value : column) {
			if (value.contains("^^")) {
				value = value.substring(0, value.indexOf("^^"));
			}
			cleanedData.add(value);
		}
		return cleanedData;
	}

	/**
     * Gives a short one word name of the manager used as variable name when
     * scripting.
     */
    public String getManagerName() {
        return "opentoxnm";
    }
}
