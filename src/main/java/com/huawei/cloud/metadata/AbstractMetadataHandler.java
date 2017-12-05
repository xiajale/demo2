package com.huawei.cloud.metadata;

import com.huawei.cloud.util.FileUtil;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.*;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created by zhouyibin on 2017/12/5.
 */
public abstract class AbstractMetadataHandler implements MetadataHandler{

    @Override
    public EntityDescriptor getEntityDescriptorFromXml(String path) {
        Document document = FileUtil.readDocumentFromFile(path);
        Element element = document.getDocumentElement();
        UnmarshallerFactory unmarshallerFactory = XMLObjectProviderRegistrySupport.getUnmarshallerFactory();
        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(element);
        EntityDescriptor entityDescriptor = null;
        try {
            entityDescriptor = (EntityDescriptor)unmarshaller.unmarshall(element);
        } catch (UnmarshallingException e) {
            e.printStackTrace();
        }
        return entityDescriptor;
    }

    @Override
    public void putEntityDescriptorToXml(EntityDescriptor entityDescriptor, String path) {
        MarshallerFactory marshallerFactory = XMLObjectProviderRegistrySupport.getMarshallerFactory();
        Marshaller marshaller = marshallerFactory.getMarshaller(entityDescriptor);
        Document document = null;
        try {
            Element element = marshaller.marshall(entityDescriptor);
            document = element.getOwnerDocument();
        } catch (MarshallingException e) {
            e.printStackTrace();
        }
        FileUtil.writeDocumentToFile(document, path);
    }

}
