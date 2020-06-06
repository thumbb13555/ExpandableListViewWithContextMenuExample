package com.jetec.expandablelistviewwithcontextmenuexample;

import java.util.ArrayList;
import java.util.HashMap;

/**實體類(getter-setter)*/
class MyInfo {

    private String title;//大標題的文字
    private ArrayList<HashMap<String,String>> arrayList;//每個群組的內容

    public MyInfo(String title, ArrayList<HashMap<String,String>> arrayList) {
        this.title = title;
        this.arrayList = arrayList;
    }
    public String getTitle() {
        return title;
    }
    public ArrayList<HashMap<String, String>> getArrayList() {
        return arrayList;
    }
}
