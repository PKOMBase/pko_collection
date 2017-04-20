package com.collection.map;

import com.collection.map.MyHashMap.MyIterator;

public class Main {
    public static void main(String[] args) {

        MyHashMap<String, Integer> map = new MyHashMap<String, Integer>();
        map.put("a1", 1);
        map.put("a2", 2);
        map.put("a3", 3);
        map.put("a4", 4);
        map.put("a5", 5);
        map.put("a6", 6);
        map.put("a7", 7);
        map.put("a8", 8);
        map.put("a9", 9);
        map.put("a10", 10);
        map.put("a11", 11);
        // System.out.println("length:" + map.table.length);
        // System.out.println(map);
        map.put("a12", 12);
        // System.out.println("length:" + map.table.length);
        // System.out.println(map);
        map.put("a13", 13);
        map.put("a14", 14);
        map.put("a15", 15);
        map.put("a16", 16);
        map.put("a17", 17);
        map.put("a22", 13);
        map.put("a23", 14);
        map.put("a24", 15);
        map.put("a25", 16);
        map.put("a26", 17);
        map.put("a36", 17);
        // map.put(null, 444);
        System.out.println("length:" + map.table.length + ", size:" + map.size());
        System.out.println(map);

        MyIterator myIterator = map.MyIterator();
        int count = 0;
        while (myIterator.hasNext()) {
            count++;
            System.out.println(myIterator.next() + ", count:" + count);
        }

    }
}
