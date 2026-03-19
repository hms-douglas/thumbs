package dev.dect.thumbs.model;

import java.util.ArrayList;

public class FontModel {
    private int ID;

    private final String NAME,
                         PATH;

    private FontModel(int id, String name, String path) {
        this.ID = id;
        this.NAME = name;
        this.PATH = path;
    }

    public void setId(int id) {
        ID = id;
    }

    public int getId() {
        return ID;
    }

    public String getName() {
        return NAME;
    }

    public String getPath() {
        return PATH;
    }

    public static FontModel getFontFromId(int id) {
        for(FontModel font : getFontsFromAsset()) {
            if(font.getId() == id) {
                return font;
            }
        }

        return getFontsFromAsset().get(0);
    }

    public static ArrayList<FontModel> getFontsFromAsset() {
        final ArrayList<FontModel> fonts = new ArrayList<>();

        fonts.add(getRobotoFromAsset());
        fonts.add(getRobotoMonoFromAsset());
        fonts.add(getUbuntuFromAsset());

        return fonts;
    }

    public static FontModel getRobotoFromAsset() {
        return new FontModel(
            -1,
            "Roboto",
            "roboto.ttf"
        );
    }

    public static FontModel getRobotoMonoFromAsset() {
        return new FontModel(
            -2,
            "Roboto Mono",
            "roboto_mono.ttf"
        );
    }

    public static FontModel getUbuntuFromAsset() {
        return new FontModel(
            -3,
            "Ubuntu",
            "ubuntu.ttf"
        );
    }
}
