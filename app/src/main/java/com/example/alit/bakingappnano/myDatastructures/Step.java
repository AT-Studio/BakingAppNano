package com.example.alit.bakingappnano.myDatastructures;

/**
 * Created by AliT on 10/7/17.
 */

public class Step {

    public String shortDesc;
    public String longDesc;
    public String videoPath;
    public String thumbnailPath;

    public Step(String shortDesc, String longDesc, String videoPath, String thumbnailPath) {

        this.shortDesc = shortDesc;
        this.longDesc = longDesc;
        this.videoPath = videoPath;
        this.thumbnailPath = thumbnailPath;

    }

}
