package com.huawei.cloud.metadata;

import com.huawei.cloud.constants.MetadataConstants;
import com.huawei.cloud.util.OpenSamlUtil;
import com.huawei.cloud.util.PropertityUtil;
import org.opensaml.saml.saml2.metadata.ArtifactResolutionService;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.opensaml.saml.saml2.metadata.SingleSignOnService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouyibin on 2017/12/7.
 */
public class EndpointGenerator {

    private static final String pathPrefix = "https://" + PropertityUtil.getProperty("idp.domain.name") + "/idp/";

    private static ArtifactResolutionService getHttpRedirectArtifactResolutionService() {
        ArtifactResolutionService endpoint = OpenSamlUtil.buildSAMLObject(ArtifactResolutionService.class);
        endpoint.setBinding(MetadataConstants.HTTP_REDIRECT_BINDING);
        endpoint.setLocation(pathPrefix + ArtifactResolutionService.DEFAULT_ELEMENT_LOCAL_NAME);
        return endpoint;
    }

    private static SingleSignOnService getHttpPostSingleSignOnService() {
        SingleSignOnService endpoint = OpenSamlUtil.buildSAMLObject(SingleSignOnService.class);
        endpoint.setBinding(MetadataConstants.HTTP_POST_BINDING);
        endpoint.setLocation(pathPrefix + SingleSignOnService.DEFAULT_ELEMENT_LOCAL_NAME);
        return endpoint;
    }

    public static List<SingleSignOnService> getSingleSignOnServices(){
        List<SingleSignOnService> singleSignOnServices = new ArrayList<SingleSignOnService>();
        singleSignOnServices.add(getHttpPostSingleSignOnService());
        return singleSignOnServices;
    }

    public static List<ArtifactResolutionService> getArtifactResolutionServices(){
        List<ArtifactResolutionService> artifactResolutionService = new ArrayList<ArtifactResolutionService>();
        artifactResolutionService.add(getHttpRedirectArtifactResolutionService());
        return artifactResolutionService;
    }
}
