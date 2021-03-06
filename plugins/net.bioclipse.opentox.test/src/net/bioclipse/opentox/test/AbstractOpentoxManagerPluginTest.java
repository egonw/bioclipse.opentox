/*******************************************************************************
 * Copyright (c) 2011  Egon Willighagen <egon.willighagen@ki.se>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.opentox.test;

import java.net.URI;
import java.util.List;

import net.bioclipse.cdk.business.CDKManager;
import net.bioclipse.cdk.domain.CDKMolecule;
import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.IStringMatrix;
import net.bioclipse.core.tests.AbstractManagerTest;
import net.bioclipse.inchi.InChI;
import net.bioclipse.managers.business.IBioclipseManager;
import net.bioclipse.opentox.business.IOpentoxManager;

import org.junit.Assert;
import org.junit.Test;

public abstract class AbstractOpentoxManagerPluginTest
extends AbstractManagerTest {

	private CDKManager cdk = new CDKManager();

	// the official test account
	private final static String TEST_ACCOUNT = "guest";
	private final static String TEST_ACCOUNT_PWD = "guest";

	private final static String TEST_SERVER_OT = "http://apps.ideaconsult.net:8080/ambit2/";
	private final static String TEST_SERVER_ONT = "http://apps.ideaconsult.net:8080/ontology/";
		
    protected static IOpentoxManager opentox;
    
    @Test public void testAuthentication() throws Exception {
        opentox.logout();
        Assert.assertNull(opentox.getToken());
        opentox.login(TEST_ACCOUNT, TEST_ACCOUNT_PWD);
        String token = opentox.getToken();
        Assert.assertNotNull(token);
        Assert.assertNotSame(0,token.length());
        opentox.logout();
        Assert.assertNull(opentox.getToken());
    }

    @Test public void testSearchDescriptors() throws Exception {
    	IStringMatrix descriptors = opentox.searchDescriptors(
    		TEST_SERVER_ONT, "LogP"
    	);
    	Assert.assertNotNull(descriptors);
    	// expect at least one hit:
    	Assert.assertNotSame(0, descriptors.getRowCount());
    }

    @Test public void testSearchModels() throws Exception {
    	IStringMatrix models = opentox.searchModels(
    		TEST_SERVER_ONT, "ToxTree"
    	);
    	Assert.assertNotNull(models);
    	// expect at least one hit:
    	Assert.assertNotSame(0, models.getRowCount());
    }

    @Test public void testSearchDataSets() throws Exception {
    	IStringMatrix models = opentox.searchDataSets(
    		TEST_SERVER_ONT, "EPA"
    	);
    	Assert.assertNotNull(models);
    	// expect at least one hit:
    	Assert.assertNotSame(0, models.getRowCount());
    }

    @Test public void testListDatasets() throws Exception {
    	List<String> sets = opentox.listDataSets(
    		TEST_SERVER_OT
    	);
    	Assert.assertNotNull(sets);
    	// expect at least one hit:
    	Assert.assertNotSame(0, sets.size());
    }

    @Test public void testListAlgorithms() throws Exception {
    	List<String> algos = opentox.listAlgorithms(
    		TEST_SERVER_ONT
    	);
    	Assert.assertNotNull(algos);
    	// expect at least one hit:
    	Assert.assertNotSame(0, algos.size());
    }

    @Test public void testGetAlgorithmsInfo() throws Exception {
    	List<String> algos = opentox.listAlgorithms(
    		TEST_SERVER_ONT
    	);
    	Assert.assertNotNull(algos);
    	Assert.assertNotSame(0, algos.size());
    	String algo = algos.get(0);
    	opentox.getAlgorithmInfo(TEST_SERVER_ONT, algo);
    }

    @Test public void testGetAlgorithmsInfos() throws Exception {
    	List<String> algos = opentox.listAlgorithms(
    		TEST_SERVER_ONT
    	);
    	Assert.assertNotNull(algos);
    	Assert.assertNotSame(0, algos.size());
    	opentox.getAlgorithmInfo(TEST_SERVER_ONT, algos);
    }

    @Test public void testSearchInChI() throws Exception {
    	List<String> hits = opentox.search(
    	    TEST_SERVER_OT,
    		"InChI=1/CH4/h1H4"
    	);
    	Assert.assertNotNull(hits);
    	// expect at least one hit:
    	Assert.assertNotSame(0, hits.size());
    }

    @Test public void testSearchMolecule() throws BioclipseException {
    	ICDKMolecule mol = cdk.fromSMILES("C");
    	mol.setProperty(
    		CDKMolecule.INCHI_OBJECT, new InChI(
    			"InChI=1S/CH4/h1H4",
    			"VNWKTOKETHGBQD-UHFFFAOYSA-N"
    		)
    	);
    	List<String> hits = opentox.search(
    	    TEST_SERVER_OT, mol
    	);
    	Assert.assertNotNull(hits);
    	// expect at least one hit:
    	Assert.assertNotSame(0, hits.size());
    }

    @Test public void testListDescriptors() throws Exception {
    	IStringMatrix descriptors = opentox.listDescriptors(
    		TEST_SERVER_ONT
    	);
    	Assert.assertNotNull(descriptors);
    	// expect at least one hit:
    	Assert.assertNotSame(0, descriptors.getRowCount());
    }

    @Test public void testListModels() throws Exception {
    	List<String> models = opentox.listModels(TEST_SERVER_ONT);
    	Assert.assertNotNull(models);
    	// expect at least one hit:
    	Assert.assertNotSame(0, models.size());
    }

    @Test public void testListFeatures() throws Exception {
    	List<String> features = opentox.listFeatures(TEST_SERVER_OT);
    	Assert.assertNotNull(features);
    	// expect at least one hit:
    	Assert.assertNotSame(0, features.size());
    }

    @Test public void testCreateEmptyDataSet() throws Exception {
    	String uriString = opentox.createDataset(TEST_SERVER_OT);
    	Assert.assertNotNull(uriString);
    	Assert.assertTrue(uriString.startsWith("http://"));
    	URI uri = new URI(uriString);
    	Assert.assertNotNull(uri);
    }

    @Test public void testAddMolecule() throws Exception {
    	String uriString = opentox.createDataset(TEST_SERVER_OT);
    	Assert.assertNotNull(uriString);
    	opentox.addMolecule(uriString, cdk.fromSMILES("COC"));
    }

    @Test public void testAddMolecules() throws Exception {
    	List<ICDKMolecule> molecules = cdk.createMoleculeList();
    	molecules.add(cdk.fromSMILES("COC"));
    	molecules.add(cdk.fromSMILES("CNC"));

    	String uriString = opentox.createDataset(TEST_SERVER_OT);
    	Assert.assertNotNull(uriString);
    	opentox.addMolecules(uriString, molecules);
    }

    @Test public void testListCompounds() throws Exception {
    	List<ICDKMolecule> molecules = cdk.createMoleculeList();
    	molecules.add(cdk.fromSMILES("COC"));
    	molecules.add(cdk.fromSMILES("CNC"));

    	String uriString = opentox.createDataset(TEST_SERVER_OT, molecules);

    	// now do the testing
    	List<String> compounds = opentox.listCompounds(uriString);
    	Assert.assertNotNull(compounds);
    	Assert.assertEquals(
    		"Incorrect molecule count for " + uriString,
    		2, compounds.size()
    	);
    }

    @Test public void testListCompoundsDataSet2() throws Exception {
    	List<Integer> compounds = opentox.listCompounds(TEST_SERVER_OT, 2);
    	Assert.assertNotNull(compounds);
    	Assert.assertNotSame(0, compounds.size());
    }

    @Test public void testDownloadAsMDLMolfile() throws Exception {
    	String mdlMolfile = opentox.downloadCompoundAsMDLMolfile(
    		TEST_SERVER_OT, "http://apps.ideaconsult.net:8080/ambit2/dataset/2", 1
    	);
    	Assert.assertNotNull(mdlMolfile);
    	Assert.assertTrue(mdlMolfile.contains("V2000"));
    }

    @Test public void testDownloadAsMDLMolfileFromURI() throws Exception {
    	String mdlMolfile = opentox.downloadCompoundAsMDLMolfile(
    		"http://apps.ideaconsult.net:8080/ambit2/dataset/2/compound/1"
    	);
    	Assert.assertNotNull(mdlMolfile);
    	Assert.assertTrue(mdlMolfile.contains("V2000"));
    }

    @Test public void testCreateDataSetFromSet() throws Exception {
    	List<ICDKMolecule> molecules = cdk.createMoleculeList();
    	molecules.add(cdk.fromSMILES("COC"));
    	molecules.add(cdk.fromSMILES("CNC"));

    	String uriString = opentox.createDataset(TEST_SERVER_OT, molecules);
    	Assert.assertNotNull(uriString);
    	URI uri = new URI(uriString);
    	Assert.assertNotNull(uri);
    }

    @Test public void testCreateDataSet() throws Exception {
    	String uriString = opentox.createDataset(TEST_SERVER_OT, cdk.fromSMILES("COC"));
    	Assert.assertNotNull(uriString);
    	URI uri = new URI(uriString);
    	Assert.assertNotNull(uri);
    }

    @Test public void testCalculateDescriptor_List_Molecule() throws Exception {
    	IStringMatrix stringMat = opentox.listDescriptors(TEST_SERVER_ONT);

    	String descriptor = stringMat.get(1, "algo");
    	Assert.assertNotNull(descriptor);

    	List<ICDKMolecule> molecules = cdk.createMoleculeList();
    	molecules.add(cdk.fromSMILES("COC"));
    	molecules.add(cdk.fromSMILES("CNC"));

    	List<String> descriptorVals = opentox.calculateDescriptor(
    		TEST_SERVER_OT, descriptor, molecules
    	);
    	Assert.assertNotNull(descriptorVals);
    	Assert.assertSame(2, descriptorVals.size());
    }
    
    @Test public void testCalculateDescriptor() throws Exception {
    	IStringMatrix stringMat = opentox.listDescriptors(TEST_SERVER_ONT);

    	String descriptor = stringMat.get(1, "algo");
    	Assert.assertNotNull(descriptor);

    	List<String> descriptorVals = opentox.calculateDescriptor(
    		TEST_SERVER_OT, descriptor, cdk.fromSMILES("ClCCl")
    	);
    	Assert.assertNotNull(descriptorVals);
    	Assert.assertSame(1, descriptorVals.size());
    }
    
    public Class<? extends IBioclipseManager> getManagerInterface() {
    	return IOpentoxManager.class;
    }
}
