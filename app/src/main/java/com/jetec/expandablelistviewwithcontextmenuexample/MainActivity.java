package com.jetec.expandablelistviewwithcontextmenuexample;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ExpandableListAdapter mAdapter;//為使方便，設置ExpandableListAdapter為全域變數

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ExpandableListView expandableListView = findViewById(R.id.expandListView);
        mAdapter = new ExpandableListAdapter(setData());
        expandableListView.setAdapter(mAdapter);
        /**將指定的ExpandableListView綁定使之可被原生的ContextMenu操作*/
        registerForContextMenu(expandableListView);
    }//onCreate
    /**使用實體類MyInfo.java製作資料*/
    private static List<MyInfo> setData() {
        ArrayList<HashMap<String,String>> arrayList = new ArrayList<>();
        for (int i = 0; i <3 ; i++) {
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("First","內容A-"+i);
            hashMap.put("Second","內容B-"+i);
            arrayList.add(hashMap);
        }
        List<MyInfo> myInfo = new ArrayList<>();
        for (int i = 0; i <4 ; i++) {
            myInfo.add(new MyInfo("標題"+i,arrayList));
        }
        return myInfo;
    }//setData
    /**複寫新增長按顯示選單視窗*/
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        /**取得所點選到的ExpandableListView的父層位置or子層位置*/
        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
        int type = ExpandableListView.getPackedPositionType(info.packedPosition);
        int groupPosition = ExpandableListView.getPackedPositionGroup(info.packedPosition);
        int childPosition = ExpandableListView.getPackedPositionChild(info.packedPosition);

        /**此處為判斷點到的ContextMenu是屬於大群組(父層)還是小群組(子層)*/
        if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
            menu.setHeaderTitle(mAdapter.getInfoArray().get(groupPosition).getTitle()+ "操作");
            menu.add(0, 0, 1, "取得資訊");
            menu.add(0, 1, 2, "刪除群組");
            menu.add(0, 2, 3, "取消操作");
        } else if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {//目前沒開啟child點擊功能
            menu.setHeaderTitle("小標題" + childPosition + "資訊");
            menu.add(0, 0, 1, "小資訊");
        }
    }
    /**複寫點擊選單視窗的內容後要做的事*/
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        /**取得所點選到的ExpandableListView的父層位置or子層位置*/
        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) item
                .getMenuInfo();
        int type = ExpandableListView.getPackedPositionType(info.packedPosition);
        int groupPosition = ExpandableListView.getPackedPositionGroup(info.packedPosition);
        int childPosition = ExpandableListView.getPackedPositionChild(info.packedPosition);
        /**如果點選的是大群組(父層)的視窗操作*/
        if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
            switch (item.getItemId()) {
                case 0://點選的是"取得資訊"
                    Toast.makeText(this, mAdapter.getInfoArray().get(groupPosition).getTitle(), Toast.LENGTH_SHORT).show();
                    break;
                case 1://點選的是"刪除"
                    mAdapter.removeByPosition(groupPosition);
                    break;
                case 2://點選的是"取消操作"
                    Toast.makeText(this, "什麼都不做", Toast.LENGTH_SHORT).show();
                    break;
            }
        } /**如果點選的是小群組(子層)的視窗操作*/
        else if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            // 原理做法跟上面一樣，大家可以試著加入要做的事哦:D
        }
        return super.onContextItemSelected(item);
    }
    /**
     * ====================================ExpandableList的適配器===================================
     */
    private class ExpandableListAdapter extends BaseExpandableListAdapter {
        private List<MyInfo> infoArray;
        /**將現在顯示的內容陣列傳出去*/
        public List<MyInfo> getInfoArray() {
            return infoArray;
        }
        /**移除所選定的群組*/
        public void removeByPosition(int selectedPosition) {
            infoArray.remove(selectedPosition);
            notifyDataSetChanged();
        }
        /**建構子*/
        public ExpandableListAdapter(List<MyInfo> infoArray) {
            this.infoArray = infoArray;
        }

        @Override
        public int getGroupCount() {
            return infoArray.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return infoArray.get(groupPosition).getArrayList().size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groupPosition;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
        /**設置大群組(父層)的介面*/
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) MainActivity.this
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.expandlistview_item, null);
            }
            convertView.setTag(R.layout.expandlistview_item, groupPosition);
            TextView textView = convertView.findViewById(R.id.textView_ItemTitle);
            textView.setText(infoArray.get(groupPosition).getTitle());
            return convertView;
        }
        /**設置小群組(子層)的介面*/
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) MainActivity.this
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.expandlistview_child, null);
            }
            convertView.setTag(R.layout.expandlistview_child, groupPosition);
            TextView child1 = convertView.findViewById(R.id.textView_child1);
            TextView child2 = convertView.findViewById(R.id.textView_child2);
            child1.setText(infoArray.get(groupPosition).getArrayList().get(childPosition).get("First"));
            child2.setText(infoArray.get(groupPosition).getArrayList().get(childPosition).get("Second"));
            return convertView;
        }
        /**可以在這邊設置是否要讓內容項目可以被點擊顯示ContextView*/
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }//ExpandableListAdapter
}