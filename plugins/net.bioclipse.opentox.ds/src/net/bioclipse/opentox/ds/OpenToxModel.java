package net.bioclipse.opentox.ds;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.core.domain.IBioObject;
import net.bioclipse.core.domain.IMaterial;
import net.bioclipse.ds.model.AbstractDSMolModel;
import net.bioclipse.ds.model.AbstractDSTest;
import net.bioclipse.ds.model.DSException;
import net.bioclipse.ds.model.ITestResult;
import net.bioclipse.opentox.Activator;
import net.bioclipse.opentox.OpenToxService;
import net.bioclipse.opentox.ServiceReader;
import net.bioclipse.opentox.business.IOpentoxManager;
import net.bioclipse.opentox.nm.business.IOpenToxNmManager;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * DSModel for predicting an OpenTox model
 * 
 * @author ola
 *
 */
public class OpenToxModel extends AbstractDSMolModel {

    private static final Logger logger = Logger.getLogger(OpenToxModel.class);

	IOpentoxManager opentox;
	IOpenToxNmManager opentoxnm;
	private String model;
	private Class<? extends IBioObject> worksOn;
	
	public OpenToxModel(String model, Class<? extends IBioObject> worksOn) {
		this.model=model;
		this.worksOn=worksOn;
	}

	@Override
	public void initialize(IProgressMonitor monitor) throws DSException {
		opentox = Activator.getDefault().getJavaOpentoxManager();
		opentoxnm = net.bioclipse.opentox.nm.Activator.getDefault().getJavaOpenToxNmManager();
	}

	@Override
	protected List<? extends ITestResult> doRunTest(IBioObject input,
			IProgressMonitor monitor) {
		
//		if (!(input.getClass().equals(worksOn)))
//			return returnError("Expected input of type " + worksOn.getName(), "");
		
		ICDKMolecule cdkmol = null;
		IMaterial material = null;
		if (input instanceof ICDKMolecule)
			cdkmol = (ICDKMolecule) input;
		else if (input instanceof IMaterial)
			material = (IMaterial) input;
		else 
			returnError("Input neither mol nor material", "");

		//Use the currently selected OpenTox service
	    List<OpenToxService> otservices = ServiceReader.readServicesFromPreferences();
		OpenToxService otservice;
		if ( otservices.isEmpty() || (otservice = otservices.get(0))==null){
			logger.error("No OpenTox service found");
			return returnError("No OpenTox service found", "No OpenTox service found");
		}

		String service=otservice.getService();
		if (service==null){
			logger.error("Current OpenTox service has no service URL");
			return returnError("Current OpenTox service has no service URL", 
					"Current OpenTox service has no service URL");
		}

		//Predict!
        ArrayList<net.bioclipse.ds.model.result.SimpleResult> results 
        = new ArrayList<net.bioclipse.ds.model.result.SimpleResult>();

        
		//Invoke calculation
		logger.debug("Invoking model: " + model + " for service: " + service);
		Map<String, String> OTres = null;
		//retry 3 times, looks like a server issue
		try{
			if (cdkmol != null)
				OTres = opentox.predictWithModelWithLabel(service, model, cdkmol, monitor);
			else if (material != null)
				OTres = opentoxnm.predictWithModelWithLabel(service, model, material, monitor);

//		} catch (GeneralSecurityException e) {
//			logger.error("  == Opentox model without access: " + model);
//			String errorMessage = "No access: " + e.getMessage().toLowerCase();
//			return returnError(errorMessage, errorMessage);
		} catch (UnsupportedOperationException e) {
			logger.error("  == Opentox model unavailable: " + model);
			String errorMessage = "Unavailable service: " + e.getMessage().toLowerCase();
			return returnError(errorMessage, errorMessage);
		}catch(Exception e){
			logger.error("  == Opentox model calculation failed for: " + model);
			logger.debug(e);
			String errorMessage =
					"Error during calculation: " + e.getMessage();
			return returnError(errorMessage, errorMessage);
		}
		
		if (OTres==null || OTres.size()<=0){
			return returnError("No results", "No results");
		}

		for (String label : OTres.keySet()){
			//FIXME here the labels of results are constructed
			String name=label.substring(label.lastIndexOf("/")+1);
			results.add(new net.bioclipse.ds.model.result.SimpleResult(
					name+ " = " + OTres.get(label), ITestResult.INFORMATIVE));
		}
        

		return results;
	}

	@Override
	public List<String> getRequiredParameters() {
		return new ArrayList<String>();
	}

}
