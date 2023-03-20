package io.oferto.pocdigitalcertificatesvalidator;

import java.net.URL;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.oferto.pocdigitalcertificatesvalidator.xml.XmlDigitalSignatureGenerator;
import io.oferto.pocdigitalcertificatesvalidator.xml.XmlDigitalSignatureVerifier;

@SpringBootApplication
public class PocDigitalCertificatesValidatorApplication implements CommandLineRunner {

	private static Logger LOG = LoggerFactory.getLogger(PocDigitalCertificatesValidatorApplication.class);
	  
	@Value("${action:sign}")
    private String action;
	
	@Value("${file:/Users/miguel/git/poc-digital-certificates-validator/target/classes/xml/employeesalary.xml}")
    private String xmlFilePath;
	
	@Value("${destination:/Users/miguel/temp/result.xml}")
    private String destnSignedXmlFilePath;
	
	public static void main(String[] args) {
		SpringApplication.run(PocDigitalCertificatesValidatorApplication.class, args);
	}

	@Override
	public void run(String... args) {
		LOG.info("EXECUTING : command line runner with these parameters");
	 
	    for (int i = 0; i < args.length; ++i) {
	    	LOG.info("args[{}]: {}", i, args[i]);
	    }
	    
	    try {
		    boolean isValid = false;
		    
	    	// get private key
	    	URL privatekeyResource = PocDigitalCertificatesValidatorApplication.class.getResource("/keys/privatekey.key");
	    	String privatekeFilePath = Paths.get(privatekeyResource.toURI()).toFile().toPath().toString();

	    	// get public key
	    	URL publickeyResource = PocDigitalCertificatesValidatorApplication.class.getResource("/keys/publickey.key");
	    	String publicKeyFilePath = Paths.get(publickeyResource.toURI()).toFile().toPath().toString();

	    		    				    	
	    	if (action.equals("sign")) 		    	
	    		// sign the xml file and save
	    		XmlDigitalSignatureGenerator.generateXMLDigitalSignature(xmlFilePath, destnSignedXmlFilePath, privatekeFilePath, publicKeyFilePath);
	    	else
	    		// validate the xml signed file
	    		isValid = XmlDigitalSignatureVerifier.isXmlDigitalSignatureValid(xmlFilePath, publicKeyFilePath);
	    	
		    if(isValid)
		    	LOG.info("File {} is valid", xmlFilePath);
		    else
		    	LOG.error("File {} is NOT valid", xmlFilePath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
