package iss.workshop.ca_team5;

import android.graphics.Bitmap;

public class GridItem {
    private Bitmap image;

    public GridItem(Bitmap image) {
        super();
        this.image = image;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}