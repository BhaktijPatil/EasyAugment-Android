package com.liminal.easy_augment;

class ImageDetails {

    // Store image details in these fields
    String imageID;
    String imageName;
    String redirectTo;
    String redirectURL;
    String imageHash;

    // Constructor to set fields
    ImageDetails(String imageID, String imageName, String redirectTo, String redirectURL, String imageHash)
    {
        this.imageID = imageID;
        this.imageName = imageName;
        this.redirectTo = redirectTo;
        this.redirectURL = redirectURL;
        this.imageHash = imageHash;
    }
}
