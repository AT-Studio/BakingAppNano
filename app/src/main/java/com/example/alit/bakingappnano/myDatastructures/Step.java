package com.example.alit.bakingappnano.myDatastructures;

/**
 * Created by AliT on 10/7/17.
 */

public class Step {

    public String shortDescription;
    public String description;
    public String videoURL;
    public String thumbnailURL;

    public Step(String shortDescription, String description, String videoURL, String thumbnailURL) {

        this.shortDescription = shortDescription;
        this.description = description;
        this.videoURL = videoURL;
        this.thumbnailURL = thumbnailURL;

    }

}
