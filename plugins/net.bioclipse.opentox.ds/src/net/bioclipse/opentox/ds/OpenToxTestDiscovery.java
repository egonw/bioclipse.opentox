package net.bioclipse.opentox.ds;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.IBioObject;
import net.bioclipse.core.domain.IMaterial;
import net.bioclipse.core.domain.IMolecule;
import net.bioclipse.ds.Activator;
import net.bioclipse.ds.business.DSBusinessModel;
import net.bioclipse.ds.business.IDSManager;
import net.bioclipse.ds.model.Endpoint;
import net.bioclipse.ds.model.IConsensusCalculator;
import net.bioclipse.ds.model.IDSTest;
import net.bioclipse.ds.model.ITestDiscovery;
import net.bioclipse.opentox.OpenToxService;
import net.bioclipse.opentox.ServiceReader;
import net.bioclipse.opentox.business.IOpentoxManager;
import net.bioclipse.opentox.nm.business.IOpenToxNmManager;

import org.apache.log4j.Logger;

/**
 * Discover OpenTox Models (tests) dynamically.
 * 
 * @author ola
 *
 */
public class OpenToxTestDiscovery implements ITestDiscovery {

	private static final Logger logger = Logger.getLogger(OpenToxTestDiscovery.class);
	
	public OpenToxTestDiscovery() {
	}

	/**
	 * Discover and provide a list of OpenTox tests
	 * @throws BioclipseException 
	 */
	@Override
	public List<IDSTest> discoverTests() throws BioclipseException {

		List<IDSTest> discoveredTests=new ArrayList<IDSTest>();
		
		//We need OpenToxManager to discover models
		IOpentoxManager opentox = net.bioclipse.opentox.Activator
										  .getDefault().getJavaOpentoxManager();

		//We need OpenToxManager to discover NM models
		IOpenToxNmManager opentoxnm = net.bioclipse.opentox.nm.Activator
										  .getDefault().getJavaOpenToxNmManager();

		//Get the registered services
		List<OpenToxService> OTservices = ServiceReader.readServicesFromPreferences();
		
		if (OTservices==null) throw new BioclipseException("No OpenTox " +
				"services available. Cannot discover services.");
		
		for (OpenToxService service : OTservices){

			//Discover models for this service
			if (service.getServiceSPARQL()!=null && service.getServiceSPARQL().length()>3){
				List<String> otmodels = opentox.listModels(service.getServiceSPARQL());
				List<String> nmmodels = opentoxnm.listModels(service.getServiceSPARQL());
//				if (otmodels!=null){
//					logger.debug("Discovered " + otmodels.size() + 
//							" models for service: " + service);
//					
//					for (String model : otmodels){
//						//Add this model as a test if basic criteria are met
//						Map<String,String> props = opentox.getModelInfo(service.getServiceSPARQL(), model);
//						String title = props.get("http://purl.org/dc/elements/1.1/title");
//						if (title.endsWith("^^http://www.w3.org/2001/XMLSchema#string")) {
//							title = title.substring(0, title.indexOf("^^"));
//						}
//						IDSTest test = createOpenToxTest(model, title, IMolecule.class);
//						discoveredTests.add(test);
//						
//						logger.debug("Added OpenTox model as DSTest: " + test );
//
//					}
//					
//				}else{
//					logger.debug("No models discovered for service: " + service);
//				}

				//Work on NM models
				if (nmmodels!=null){
					logger.debug("Discovered " + nmmodels.size() + 
							" NM models for service: " + service);
					
					for (String model : nmmodels){
						//Add this model as a test if basic criteria are met
						Map<String,String> props = opentox.getModelInfo(service.getServiceSPARQL(), model);
//						String title = props.get("http://purl.org/dc/elements/1.1/title");
//						if (title.endsWith("^^http://www.w3.org/2001/XMLSchema#string")) {
//							title = title.substring(0, title.indexOf("^^"));
//						}
						String title="wee";
						IDSTest test = createOpenToxTest(model, title, IMaterial.class);
						discoveredTests.add(test);
						
						logger.debug("Added OpenToxNM model as DSTest: " + test );

					}
					
				}else{
					logger.debug("No models discovered for service: " + service);
				}
			
			}
			
		}

		return discoveredTests;
	}

	private IDSTest createOpenToxTest(String model, String title, Class<? extends IBioObject> worksOn) throws BioclipseException {

		
		//First, hardcoded one to demonstrate functionality
		IDSTest test= new OpenToxModel(model, worksOn);
		
		if (title != null && title.length() > 0) {
			test.setName(title);
		} else {
			test.setName(model.substring(model.lastIndexOf("/")+1));
		}
		
		test.setId("ot.test."+model);
		test.setIcon("icons/biohazard.png");
		test.setDescription("N/A");

		test.setOverride(false);
		test.setInformative(false);
		test.setVisible( true );
		test.setPluginID( net.bioclipse.opentox.ds.Activator.PLUGIN_ID);

		//Endpoints are required to exist prior to test discovery
		//now we add them using an EP extension
		//Could add discovery later too..
		String endpoint="net.bioclipse.ds.opentox";

		//We need DSManager to look up existing endpoints
		IDSManager ds = Activator.getDefault().getJavaManager();

		//Look up endpoint by id and add to test
		for (Endpoint ep : ds.getFullEndpoints()){
			if (ep.getId().equals( endpoint )){
				test.setEndpoint( ep );
				ep.addTest(test);
			}
		}

		//Consensus calculator... use default for now
		IConsensusCalculator conscalc = DSBusinessModel
		.createNewConsCalc(null);
		test.setConsensusCalculator( conscalc );

		//TODO: unused for now..                
		//              test.setPropertycalculator( ppropcalc);
		//              test.setHelppage( phelppage);
		//              test.addParameter(name,path);
		
		return test;
		
	}

}
