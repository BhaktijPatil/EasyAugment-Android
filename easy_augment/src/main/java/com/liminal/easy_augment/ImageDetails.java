package com.liminal.easy_augment;

class ImageDetails {

    // Store image details in these fields
    String imageID;
    String imageName;
    String redirectTo;
    String redirect;
    String imageHash;

    // Constructor to set fields
    ImageDetails(String imageID, String imageName, String redirectTo, String redirectURL, String imageHash)
    {
        this.imageID = imageID;
        this.imageName = imageName;
        this.redirectTo = redirectTo;
        this.redirect = redirectURL;
        this.imageHash = imageHash;
    }
}
