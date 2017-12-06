package com.huawei.cloud.util;

import com.huawei.cloud.metadata.IDPMetadataHandler;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.SignableSAMLObject;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.Signer;

import javax.xml.namespace.QName;

/**
 * Created by zhouyibin on 2017/12/6.
 */
public class OpenSamlUtil {

    public static <T> T buildSAMLObject(final Class<T> clazz) {
        T object = null;
        try {
            XMLObjectBuilderFactory builderFactory = XMLObjectProviderRegistrySupport.getBuilderFactory();
            QName defaultElementName = (QName)clazz.getDeclaredField("DEFAULT_ELEMENT_NAME").get(null);
            object = (T)builderFactory.getBuilder(defaultElementName).buildObject(defaultElementName);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Could not create SAML object");
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("Could not create SAML object");
        }

        return object;
    }

    public static void signObject(IDPMetadataHandler idpMetadataHandler, SignableSAMLObject object){
        Signature signature = idpMetadataHandler.getSignature();
        object.setSignature(signature);
        try {
            XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(object).marshall(object);
        } catch (MarshallingException e) {
            e.printStackTrace();
        }
        try {
            Signer.signObject(signature);
        } catch (SignatureException e) {
            e.printStackTrace();
        }
    }

}
