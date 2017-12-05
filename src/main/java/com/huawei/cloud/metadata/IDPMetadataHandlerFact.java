package com.huawei.cloud.metadata;


import com.huawei.cloud.util.PropertityUtil;

/**
 * Created by zhouyibin on 2017/12/6.
 */
public class IDPMetadataHandlerFact {

    private static IDPMetadataHandler idpMetadataHandler;
    private static final String IDP_METADATA_PATH = PropertityUtil.getProperty("idp.metadata.file");

    private IDPMetadataHandlerFact(){

    }

    public static IDPMetadataHandler getInstance(){
        if (null == idpMetadataHandler){
            idpMetadataHandler = new IDPMetadataHandler(IDP_METADATA_PATH);
        }
        return idpMetadataHandler;
    }
}
