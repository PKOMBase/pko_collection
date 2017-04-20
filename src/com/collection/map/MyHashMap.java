package com.collection.map;

import java.util.Iterator;
import java.util.Objects;

public class MyHashMap<K, V> {

    public Node<K, V>[] table;

    private static final int DEFAULT_INITAIL_CAPACITY = 1 << 4;// 16

    private static final float DEFAULT_LOAD_FECTOR = 0.75f;

    private static int size;

    private static int theshold;

    static class Node<K, V> {

        private int hash;

        private K key;

        private V value;

        public Node<K, V> next;

        public int getHash() {
            return hash;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public Node<K, V> getNext() {
            return next;
        }

        public Node(int hash, K key, V value, Node<K, V> next) {
            super();
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        @Override
        public String toString() {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(key);
            stringBuffer.append("=");
            stringBuffer.append(value);
            stringBuffer.append(",");
            if (null != next) {
                stringBuffer.append("->" + next.toString());
            }
            return stringBuffer.toString();
        }
    }

    public int size() {
        return size;
    }

    public MyIterator MyIterator() {
        return new MyIterator();
    }

    class MyIterator implements Iterator<K> {

        private Node<K, V> current;

        private Node<K, V> next;

        private int index;

        public MyIterator() {
            current = null;
            next = null;
            index = 0;
            // 第一个非null的table数组元素
            if (table == null || size <= 0) {
                return;
            }
            for (int count = 0;; count++) {
                if (index >= table.length) {
                    break;
                }
                next = table[index++];
                if (next != null) {
                    break;
                }
            }
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public K next() {
            // 处理current
            current = next;

            // 处理next
            if (table != null && current.next != null) {
                next = current.next;
            } else {
                for (int count = 0;; count++) {
                    if (index >= table.length) {
                        break;
                    }
                    next = table[index++];
                    if (next != null) {
                        break;
                    }
                }
            }
            return current.key;
        }

    }

    public void put(K key, V value) {
        // 获取hash
        int hash = Objects.hashCode(key);

        // 指定数组长度，初始化数组
        int length = DEFAULT_INITAIL_CAPACITY;

        if (table == null) {
            theshold = (int) (DEFAULT_INITAIL_CAPACITY * DEFAULT_LOAD_FECTOR);// 12
            table = new Node[length];
        }

        // 根据key，算key的哈希值, 更优算法
        // &
        // 101110101 & 1111 = 0101 A & B = C C<=A C<=B
        // int i = hash % length;
        int i = hash & (length - 1); // 0000 0010 0100 1000 0110 1000 1100 1010

        // 判断table[i]是否存在
        if (null == table[i]) {
            table[i] = new Node<K, V>(hash, key, value, null);
        } else {
            Node<K, V> node = table[i];
            // 判断table[i].key是否等于传入的key
            if ((node.hash == hash) && (node.key == key || (key != null && node.key.equals(key)))) {
                node.value = value;
            } else {
                for (int count = 0;; count++) {
                    // 判断next
                    if (null == node.next) {
                        node.next = new Node<K, V>(hash, key, value, null);
                        break;
                    }
                    if ((node.next.hash == hash)
                            && (node.next.key == key || (key != null && node.next.key.equals(key)))) {
                        node.next.value = value;
                        break;
                    }
                    node = node.next;
                }
            }
        }
        size++;
        if (size >= theshold) {
            resize();
        }
    }

    private void resize() {
        // 扩容
        int newCapacity = table.length << 1;
        theshold = (int) (newCapacity * DEFAULT_LOAD_FECTOR);
        Node<K, V>[] newTable = new Node[newCapacity];

        // 转移数据
        for (Node<K, V> oldNode : table) {
            if (null == oldNode) {
                continue;
            }
            for (int count = 0;; count++) {
                if (oldNode == null) {
                    break;
                }
                Node<K, V> next = oldNode.next;
                // 新table的下标
                int i = oldNode.hash & (newCapacity - 1);

                oldNode.next = newTable[i];
                newTable[i] = oldNode;

                oldNode = next;
            }
        }

        // 替换table
        table = newTable;
    }

    public V get(K key) {
        // 获取key的hash
        int hash = Objects.hashCode(key);
        // 判断table
        if (table == null || table.length <= 0) {
            return null;
        }
        // 哈希运算table数组下标
        int i = hash & (table.length - 1);

        Node<K, V> node = table[i];
        if (node == null) {
            return null;
        }
        // 判断key是否相等
        if ((node.hash == hash) && ((node.key == key) || (key != null && node.key.equals(key)))) {
            return node.value;
        } else {
            for (int count = 0;; count++) {
                if (node.next != null) {
                    if ((node.next.hash == hash)
                            && ((node.next.key == key) || (key != null && node.next.key.equals(key)))) {
                        return node.next.value;
                    }
                    node = node.next;
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer("{");
        for (Node<K, V> node : table) {
            if (null == node) {
                continue;
            }
            stringBuffer.append(node.toString());
        }
        stringBuffer.append("}");
        return stringBuffer.toString();
    }

}
