package com.huawei.cloud.controller;

import com.huawei.cloud.metadata.IDPMetadataHandler;
import com.huawei.cloud.metadata.IDPMetadataHandlerFact;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;

/**
 * Created by zhouyibin on 2017/12/6.
 */
@RestController
public class MetadataController {

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String sayHello(){
        return "hello";
    }

    @RequestMapping(value = "/metadata", method = RequestMethod.GET, produces = "application/xml")
    public String getMetadata(){
        IDPMetadataHandler idpMetadataHandler = IDPMetadataHandlerFact.getInstance();
        return idpMetadataHandler.getIDPMetadata();
    }

}
