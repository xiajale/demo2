package com.huawei.cloud.metadata;

import com.huawei.cloud.constants.MetadataConstants;
import com.huawei.cloud.util.FileUtil;
import com.huawei.cloud.util.OpenSamlUtil;
import com.huawei.cloud.util.PropertityUtil;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.Criterion;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import org.apache.commons.lang.StringUtils;
import org.apache.xml.security.utils.*;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.saml2.metadata.*;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.credential.impl.KeyStoreCredentialResolver;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.soap.wstrust.KeyType;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.X509Certificate;
import org.opensaml.xmlsec.signature.X509Data;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.apache.xml.security.utils.Base64;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.Signer;

import java.io.File;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.CertificateEncodingException;
import java.util.*;

/**
 * Created by zhouyibin on 2017/12/5.
 */
public class IDPMetadataHandler extends AbstractMetadataHandler {

    private String filePath;
    private EntityDescriptor entityDescriptor;
    private String entityId;
    private String id;
    private List<RoleDescriptor> roleDescriptorList;
    private List<KeyDescriptor> keyDescriptorList;
    private List<Endpoint> endpointList;
    private List<X509Credential> credentialList;
    private Signature signature;

    public IDPMetadataHandler(String path){
        String abPath = IDPMetadataHandler.class.getClassLoader().getResource("").getPath() + path;
        File file = new File(abPath);
        if(!file.exists()){
            try {
                file.createNewFile();
                writeToFile(abPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        loadFromFile(abPath);
    }

    public String getIDPMetadata(){
        return FileUtil.readFile(filePath);
    }

    private void writeToFile(String path) {
        EntityDescriptor entityDescriptor = OpenSamlUtil.buildSAMLObject(EntityDescriptor.class);
        entityDescriptor.setID(getId());
        entityDescriptor.setEntityID(getEntityId());
        entityDescriptor.getRoleDescriptors().addAll(getIDPRoleDescriptors());
        entityDescriptor.setSignature(getSignature());

        try {
            XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(entityDescriptor).marshall(entityDescriptor);
            Signer.signObject(signature);
        } catch (MarshallingException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }

        super.putEntityDescriptorToXml(entityDescriptor, path);
    }

    public List<RoleDescriptor> getIDPRoleDescriptors() {
        if(null == roleDescriptorList){
            List<RoleDescriptor> roleDescriptors = new ArrayList<RoleDescriptor>();
            IDPSSODescriptor idpssoDescriptor = OpenSamlUtil.buildSAMLObject(IDPSSODescriptor.class);
            idpssoDescriptor.setWantAuthnRequestsSigned(true);
            idpssoDescriptor.getKeyDescriptors().addAll(getKeyDescriptorList());
            idpssoDescriptor.getSingleSignOnServices().addAll(EndpointGenerator.getSingleSignOnServices());
            idpssoDescriptor.getArtifactResolutionServices().addAll(EndpointGenerator.getArtifactResolutionServices());
            roleDescriptors.add(idpssoDescriptor);
            roleDescriptorList = roleDescriptors;
        }
        return roleDescriptorList;
    }

    public List<Endpoint> getEndpointList() {
        if(null == endpointList){
            List<Endpoint> endpoints = new ArrayList<Endpoint>();
            for(RoleDescriptor roleDescriptor : getIDPRoleDescriptors()){
                if(null != roleDescriptor){
                    endpoints.addAll(roleDescriptor.getEndpoints());
                }
            }
            endpointList = endpoints;
        }
        return endpointList;
    }

    public List<KeyDescriptor> getKeyDescriptorList(){
        if(null == keyDescriptorList){
            List<KeyDescriptor> keyDescriptors = new ArrayList<KeyDescriptor>();

            KeyDescriptor signingKeyDescriptor = OpenSamlUtil.buildSAMLObject(KeyDescriptor.class);
            signingKeyDescriptor.setUse(UsageType.SIGNING);
            signingKeyDescriptor.setKeyInfo(getKeyInfo());
            keyDescriptors.add(signingKeyDescriptor);

            KeyDescriptor encryptionKeyDescriptor = OpenSamlUtil.buildSAMLObject(KeyDescriptor.class);
            encryptionKeyDescriptor.setUse(UsageType.ENCRYPTION);
            encryptionKeyDescriptor.setKeyInfo(getKeyInfo());
            keyDescriptors.add(encryptionKeyDescriptor);

            keyDescriptorList = keyDescriptors;
        }
        return  keyDescriptorList;
    }

    private KeyInfo getKeyInfo(){
        KeyInfo keyInfo = OpenSamlUtil.buildSAMLObject(KeyInfo.class);
        X509Data x509Data = OpenSamlUtil.buildSAMLObject(X509Data.class);

        for(X509Credential x509Credential : getCredential()){
            try {
                X509Certificate x509Certificate = OpenSamlUtil.buildSAMLObject(X509Certificate.class);
                x509Certificate.setValue(Base64.encode(x509Credential.getEntityCertificate().getEncoded()));
                x509Data.getX509Certificates().add(x509Certificate);
            } catch (CertificateEncodingException e) {
                e.printStackTrace();
            }
        }

        keyInfo.getX509Datas().add(x509Data);
        return keyInfo;
    }

    public List<X509Credential> getCredential(){
        if(null == credentialList){
            List<X509Credential> credentials = new ArrayList<X509Credential>();

            String keystoreName = PropertityUtil.getProperty("idp.keystore.name");
            String keystorePass = PropertityUtil.getProperty("idp.keystore.pass");
            String path = IDPMetadataHandler.class.getClassLoader().getResource("").getPath() + keystoreName;
            char[] chars = keystorePass.toCharArray();
            KeyStore keyStore = FileUtil.getKeyStore(path, chars);

            Map<String,String> passwordMap = new HashMap<String,String>();
            CriteriaSet criteriaSet = new CriteriaSet();
            try {
                Enumeration<String> aliases =  keyStore.aliases();
                while (aliases.hasMoreElements()){
                    String alias = aliases.nextElement();
                    passwordMap.put(alias, keystorePass);
                    criteriaSet.add(new EntityIdCriterion(alias));
                }
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }

            try {
                KeyStoreCredentialResolver resolver = new KeyStoreCredentialResolver(keyStore, passwordMap);
                Iterable<Credential> credentialIterable = resolver.resolve(criteriaSet);
                credentialIterable.forEach(s -> credentials.add((X509Credential)s));
            } catch (ResolverException e) {
                e.printStackTrace();
            }

            credentialList = credentials;
        }


        return credentialList;
    }

    public Signature getSignature() {
        if(null == signature){
            X509Credential x509Credential = credentialList.get(0);
            signature = OpenSamlUtil.buildSAMLObject(Signature.class);
            signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
            signature.setSignatureAlgorithm(getSignatureAlg());
            signature.setSigningCredential(x509Credential);

            KeyInfo keyInfo = OpenSamlUtil.buildSAMLObject(KeyInfo.class);
            X509Data x509Data = OpenSamlUtil.buildSAMLObject(X509Data.class);
            X509Certificate x509Certificate = OpenSamlUtil.buildSAMLObject(X509Certificate.class);
            try {
                x509Certificate.setValue(Base64.encode(x509Credential.getEntityCertificate().getEncoded()));
            } catch (CertificateEncodingException e) {
                e.printStackTrace();
            }
            x509Data.getX509Certificates().add(x509Certificate);
            keyInfo.getX509Datas().add(x509Data);
            signature.setKeyInfo(keyInfo);

        }
        return signature;
    }

    private String getSignatureAlg(){
        String alg = PropertityUtil.getProperty("idp.sig.alg");
        if(StringUtils.equals(MetadataConstants.RSA_SHA1, alg)){
            return SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1;
        }
        else if(StringUtils.equals(MetadataConstants.RSA_SHA256, alg)){
            return SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256;
        }
        else {
            return SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1;
        }
    }


    public String getId(){
        if(StringUtils.isEmpty(id)){
            id = UUID.randomUUID().toString();
        }
        return id;
    }

    public String getEntityId(){
        if(StringUtils.isEmpty(entityId)){
            entityId = "https://" + PropertityUtil.getProperty("idp.domain.name") + "/";
        }
        return entityId;
    }

    private void loadFromFile(String path) {
        this.filePath = path;
        this.entityDescriptor = super.getEntityDescriptorFromXml(path);
    }

}
