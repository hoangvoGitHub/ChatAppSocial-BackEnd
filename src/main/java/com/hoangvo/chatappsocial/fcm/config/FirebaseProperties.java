package com.hoangvo.chatappsocial.fcm.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

@ConfigurationProperties(prefix = "social.firebase")
public class FirebaseProperties {
    private Resource serviceAccount;
    private String bucketName;


    /**
     * @return the serviceAccount
     */
    public Resource getServiceAccount() {
        return serviceAccount;
    }

    /**
     * @return the bucketUrl
     */
    public String getBucketName() {
        return bucketName;
    }



    /**
     * @param bucketName the bucketName to set
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }


    /**
     * @param serviceAccount the serviceAccount to set
     */
    public void setServiceAccount(Resource serviceAccount) {
        this.serviceAccount = serviceAccount;
    }

}