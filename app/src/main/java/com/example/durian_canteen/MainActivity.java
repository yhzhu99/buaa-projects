package com.example.durian_canteen;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;


public class MainActivity extends AppCompatActivity {
//    private static final string TAG　= "MainActivity";
    private ListView menu_view_left;
    private List<Map<String, String>> list = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(MainActivity.this, OrderActivity.class);
//
//
//        String[] data = {"a", "b", "c"};
//        ArrayAdapter adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, data);
//
//        menu_view_left = (ListView) findViewById(R.id.menu_view_left);
//        menu_view_left.setAdapter(adapter);
//        setContentView(R.layout.activity_canteen_order);
//        list = new ArrayList<Map<String, String>>();
//        for (int i = 0; i < 5; i++) {
//            Map<String, String> map = new HashMap<String, String>();
//            map.put("菜品", "红烧牛肉" + i);
//            map.put("价格", "888");
//            list.add(map);
//        }
        startActivity(intent);
    }
}
