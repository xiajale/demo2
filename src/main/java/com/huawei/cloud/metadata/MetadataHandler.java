package com.huawei.cloud.metadata;

import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;

/**
 * Created by zhouyibin on 2017/12/5.
 */
public interface MetadataHandler {

    EntityDescriptor getEntityDescriptorFromXml(String content);
    void putEntityDescriptorToXml(EntityDescriptor nntityDescriptor, String path);
}
