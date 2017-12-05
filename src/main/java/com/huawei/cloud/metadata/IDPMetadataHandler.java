package com.huawei.cloud.metadata;

import com.huawei.cloud.util.FileUtil;
import com.huawei.cloud.util.OpenSamlUtil;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;

import java.io.File;
import java.io.IOException;

/**
 * Created by zhouyibin on 2017/12/5.
 */
public class IDPMetadataHandler extends AbstractMetadataHandler {

    private String filePath;
    private EntityDescriptor entityDescriptor;

    public IDPMetadataHandler(String path){
        String abPath = FileUtil.class.getClassLoader().getResource("").getPath() + path;
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
        EntityDescriptor entityDescriptor = generateEntityDescriptor();
        super.putEntityDescriptorToXml(entityDescriptor, path);
    }

    private void loadFromFile(String path) {
        this.filePath = path;
        this.entityDescriptor = super.getEntityDescriptorFromXml(path);
    }

    private EntityDescriptor generateEntityDescriptor() {
        EntityDescriptor entityDescriptor = OpenSamlUtil.buildSAMLObject(EntityDescriptor.class);

        entityDescriptor.setEntityID("entityId");
        entityDescriptor.setID("id");

        SPSSODescriptor spssoDescriptor = OpenSamlUtil.buildSAMLObject(SPSSODescriptor.class);
        spssoDescriptor.setAuthnRequestsSigned(true);
        entityDescriptor.getRoleDescriptors().add(spssoDescriptor);

        return entityDescriptor;
    }
}
