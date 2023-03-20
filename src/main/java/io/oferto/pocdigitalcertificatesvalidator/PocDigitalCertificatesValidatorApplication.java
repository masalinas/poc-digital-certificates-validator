package io.oferto.pocdigitalcertificatesvalidator;

import java.net.URL;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.oferto.pocdigitalcertificatesvalidator.xml.XmlDigitalSignatureGenerator;
import io.oferto.pocdigitalcertificatesvalidator.xml.XmlDigitalSignatureVerifier;

@SpringBootApplication
public class PocDigitalCertificatesValidatorApplication implements CommandLineRunner {

	private static Logger LOG = LoggerFactory.getLogger(PocDigitalCertificatesValidatorApplication.class);
	  
	public static void main(String[] args) {
		SpringApplication.run(PocDigitalCertificatesValidatorApplication.class, args);
	}

	@Override
	public void run(String... args) {
		LOG.info("EXECUTING : command line runner");
	 
	    for (int i = 0; i < args.length; ++i) {
	    	LOG.info("args[{}]: {}", i, args[i]);
	    }

    	
	    // Validate signed file
	    boolean isValid = false;
	    
	    try {
	    	// get private key
	    	URL privatekeyResource = PocDigitalCertificatesValidatorApplication.class.getResource("/keys/privatekey.key");
	    	String privatekeFilePath = Paths.get(privatekeyResource.toURI()).toFile().toPath().toString();

	    	// get public key
	    	URL publickeyResource = PocDigitalCertificatesValidatorApplication.class.getResource("/keys/publickey.key");
	    	String pubicKeyFilePath = Paths.get(publickeyResource.toURI()).toFile().toPath().toString();
	    	
	    	// get plain xml file
	    	URL xmlFileResource = PocDigitalCertificatesValidatorApplication.class.getResource("/xml/employeesalary.xml");
	    	String xmlFilePath = Paths.get(xmlFileResource.toURI()).toFile().toPath().toString();
	    	
	    	// get destination folder for the signed file
	    	String xmlFileDestinationPath = "/Users/miguel/temp/result.xml";
	    				    	
	    	// validate file signed using the public key
	    	XmlDigitalSignatureGenerator.generateXMLDigitalSignature(xmlFilePath, xmlFileDestinationPath, privatekeFilePath, pubicKeyFilePath);
	    	
	    	// validate file signed using the public key
	    	isValid = XmlDigitalSignatureVerifier.isXmlDigitalSignatureValid(xmlFileDestinationPath, pubicKeyFilePath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    if(isValid)
	    	LOG.info("File validated");
	    else
	    	LOG.error("File not validated");
	}
}
