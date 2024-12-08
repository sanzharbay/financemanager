package models;
public class Category {
    public String name;
    int imgID, moneySpentOn, color;
    public int getImgID() {
        return imgID;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public Category(int imgID, String name, int color) {
        this.imgID = imgID;
        this.name = name;
        this.color = color;
        this.moneySpentOn = 0;
    }

    public Category() {
    }
}
