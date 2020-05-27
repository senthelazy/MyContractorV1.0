package com.sen.mycontractor.image;

import android.app.Activity;

import com.sen.mycontractor.data.LocalImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sen on 2017/9/1.
 */

public class ImageHelper extends Activity {
    private final List<LocalImage> imagesArrayList=new ArrayList<>();
    private final Map<String, List<LocalImage>> imageFoldersHashMap = new HashMap<>();
    private static ImageHelper imageHelper;

    public List<LocalImage> getImagesArrayList() {
        return imagesArrayList;
    }

    public Map<String, List<LocalImage>> getFolderMap() {
        return imageFoldersHashMap;
    }

    public static ImageHelper getImageHelper() {
        return imageHelper;
    }


}
