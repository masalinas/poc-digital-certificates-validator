package io.oferto.pocdigitalcertificatesvalidator.xml;

import io.oferto.pocdigitalcertificatesvalidator.crypto.KryptoUtil;
import io.oferto.pocdigitalcertificatesvalidator.crypto.KeyValueKeySelector;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.util.Base64;
import java.util.List;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xml.security.Init;
import org.apache.xml.security.c14n.Canonicalizer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class is used to provide the functionality for the verification of a 
 * digitally signed XML document.
 */
public class XmlDigitalSignatureVerifier {
    /**
     * Method used to get the XML document object by parsing xml file
     * @param xmlFilePath
     * @return 
     */
    private static Document getXmlDocument(String xmlFilePath) {
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        
        dbf.setNamespaceAware(true);
        
        try {
            doc = dbf.newDocumentBuilder().parse(new FileInputStream(xmlFilePath));
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        return doc;
    }

    /**
     * Method used to verify the XML digital signature
     * @param signedXmlFilePath
     * @param pubicKeyFilePath
     * @return true or false
     * @throws Exception 
     */
    public static boolean isXmlDigitalSignatureValid(String signedXmlFilePath, String pubicKeyFilePath) throws Exception {
        boolean validFlag = false;
        
        Document doc = getXmlDocument(signedXmlFilePath);
        NodeList nl = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
        
        if (nl.getLength() == 0) {
            throw new Exception("No XML Digital Signature Found, document is discarded");
        }
        
        PublicKey publicKey = new KryptoUtil().getStoredPublicKey(pubicKeyFilePath);
        DOMValidateContext valContext = new DOMValidateContext(publicKey, nl.item(0));
        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
        XMLSignature signature = fac.unmarshalXMLSignature(valContext);
        
        validFlag = signature.validate(valContext);
        
        return validFlag;
    }
    
    /**
     * Method used to verify the XML digest
     * @param signedXmlFilePath
     * @return true or false
     * @throws Exception 
     */
    public static boolean isXmlDigestValid(String signedXmlFilePath) throws Exception {
        boolean validFlag = false;
        
        Document doc = getXmlDocument(signedXmlFilePath);
        NodeList nl = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
        
        if (nl.getLength() == 0) {
            throw new Exception("No XML Digital Signature Found, document is discarded");
        }
        
        // document containing the XMLSignature
        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
        
        // Create a DOMValidateContext and specify a KeyValue KeySelector and document context
        DOMValidateContext valContext = new DOMValidateContext
            (new KeyValueKeySelector(), nl.item(0));

        // unmarshal the XMLSignature
        XMLSignature signature = fac.unmarshalXMLSignature(valContext);
        List<Reference> references = signature.getSignedInfo().getReferences();
        Reference reference = references.get(0);
        
        String digestMethod = reference.getDigestMethod().getAlgorithm();
        byte[] digestValue = reference.getDigestValue();
    		   
        System.out.printf("Digest Value: %s %n", Base64.getEncoder().encodeToString(digestValue));
        
        //remove signature node from DOM ??????
        nl.item(0).getParentNode().removeChild(nl.item(0));
        
        // save xml file without the signature
        saveNode(doc);
        
        // Doing the actual canonicalization
        byte[] canonicalizeResult;
        Init.init();
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			final Canonicalizer canonicalizer = Canonicalizer.getInstance(CanonicalizationMethod.INCLUSIVE);
			canonicalizer.canonicalize(asByteArray(doc), baos, true);
			
			canonicalizeResult = baos.toByteArray();
		} catch (Exception e) {
			throw new Exception("Cannot canonicalize the binaries", e);
		}
                
        // digest the document without signature
        //byte[] result = MessageDigest.getInstance("SHA-256").digest(asByteArray(doc));
        byte[] result = MessageDigest.getInstance("SHA-256").digest(canonicalizeResult);
        
        // trace the digest base64 string result
        System.out.printf("Digest File Document: %s", Base64.getEncoder().encodeToString(result));
        
        // compare digests
        validFlag = MessageDigest.isEqual(digestValue, result);
        
        return validFlag;
    }
    
    public static void saveNode(Node node) {
        try {
        	// write dom document to a file
            try (FileOutputStream output =
                         new FileOutputStream("/Users/miguel/temp/igG_XM_Payload.xml")) {
                
            	// Remove unwanted whitespaces
                //node.normalize();
            	
            	TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(node);
                StreamResult result = new StreamResult(output);

                transformer.transform(source, result);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
     
    /**
     * Transforms a DOM document to a byte array.
     * @param doc DOM document
     * @return byte array
     * @throws javax.xml.transform.TransformerException
     * @throws InvalidCanonicalizerException 
     */
    public static byte[] asByteArray(Node document) throws TransformerException {
    	TransformerFactory transformerFactory = TransformerFactory.newInstance();
                
        Transformer transformer = transformerFactory.newTransformer();
        
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(bout);
        
        DOMSource source = new DOMSource(document);
    	    	
        transformer.transform(source, result);
            	
        return bout.toByteArray();
    }
 
}
