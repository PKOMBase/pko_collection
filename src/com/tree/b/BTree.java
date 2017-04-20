package com.tree.b;

import java.util.ArrayList;
import java.util.List;

public class BTree<K extends Comparable<K>, V> {

    private int m;

    private Node<K, V> root;

    public BTree(int m, Node<K, V> root) {
        super();
        this.m = m;
        this.root = root;
    }

    static class Node<K, V> {

        public List<KV<K, V>> kvList;

        public KV<K, V> parentKv;

        public Node(List<KV<K, V>> kvList, KV<K, V> parentKv) {
            super();
            if (kvList.size() > 0 && null != kvList.get(kvList.size() - 1).key) {
                kvList.add(new KV<K, V>(null, null));
            }
            this.kvList = kvList;
            for (KV<K, V> kv : this.kvList) {
                kv.node = this;
            }
            this.parentKv = parentKv;
        }

        public void addKv(int index, KV<K, V> kv) {
            if (null == kv) {
                return;
            }
            kv.node = this;
            this.kvList.add(index, kv);
        }

        @Override
        public String toString() {
            String kvListString = "[";
            if (null != kvList && kvList.size() > 0) {
                for (KV<K, V> kv : kvList) {
                    kvListString += kv.toString() + ",";
                }
                kvListString = kvListString.substring(0, kvListString.length() - 1);
            }
            kvListString += "]";
            return "{\"kvList\":" + kvListString + "}";
        }
    }

    static class KV<K, V> {
        public K key;

        public V value;

        public Node<K, V> childNode;

        public Node<K, V> node;

        public KV(K key, V value) {
            super();
            this.key = key;
            this.value = value;
        }

        public int getIndex() {
            return this.node.kvList.indexOf(this);
        }

        @Override
        public String toString() {
            return "{\"key\":" + key + ", \"value\":\"" + value + "\", \"childNode\":"
                    + (null == childNode ? "{}" : childNode.toString()) + "}";
        }

    }

    public V get(K key) {
        return get(root, key);
    }

    private V get(Node<K, V> node, K key) {
        if (null == node || node.kvList.size() == 0) {
            return null;
        }
        int compareTo;
        for (KV<K, V> kv : node.kvList) {
            // 如果是tail
            if (null == kv.key) {
                return get(kv.childNode, key);
            }
            compareTo = key.compareTo(kv.key);
            if (compareTo == 0) {
                return kv.value;
            } else if (compareTo < 0) {
                return get(kv.childNode, key);
            } else if (compareTo > 0) {
                continue;
            }
        }
        return null;
    }

    public void put(K key, V value) throws Exception {
        put(root, key, value, null);
    }

    private void put(Node<K, V> node, K key, V value, KV<K, V> parentKv) throws Exception {
        if (null == node || node.kvList.size() == 0) {
            return;
        }
        if (null == key) {
            throw new Exception("key is null");
        }
        // 查找节点中keys
        int compareTo = 0;
        int addIndex = -1;
        KV<K, V> kv = null;
        for (int i = 0; i < node.kvList.size(); i++) {
            kv = node.kvList.get(i);
            // 如果是tail
            if (null == kv.key) {
                // 如果没有子节点，则直接添加至tail之前
                if (null == kv.childNode) {
                    addIndex = node.kvList.size() - 1;
                }
                // 如果有子节点，则递归子节点
                else {
                    put(kv.childNode, key, value, kv);
                }
                break;
            }
            // 若相等，则替换value
            compareTo = key.compareTo(kv.key);
            if (compareTo == 0) {
                kv.value = value;
            } else
            // 若小于，则替换value
            if (compareTo < 0) {
                // 如果没有子节点，则直接添加至该kv之前
                if (null == kv.childNode) {
                    addIndex = i;
                }
                // 如果有子节点，则递归子节点
                else {
                    put(kv.childNode, key, value, kv);
                }
                break;
            } else
            // 若大于，则继续查找下一个key
            if (compareTo > 0) {
                continue;
            }
        }
        // 添加元素
        if (addIndex >= 0 || addIndex >= node.kvList.size()) {
            node.addKv(addIndex, new KV<K, V>(key, value));
        }

        // 若节点key的数量，达到满子节点m+1（包含tail节点），则进行提升分裂
        if (node.kvList.size() >= m + 1) {
            splitNode(node);
        }
    }

    /**
     * 
     * 提升分裂节点
     *
     * @author sunjie at 2017年4月18日
     *
     * @param node
     */
    private void splitNode(Node<K, V> node) {
        if (null == node) {
            return;
        }
        // 确定提升kv
        int promoteIndex = (m - 1) / 2;
        KV<K, V> promoteKv = node.kvList.get(promoteIndex);
        if (null == promoteKv.key) {
            return;
        }

        // 分裂节点
        List<KV<K, V>> leftList = new ArrayList<KV<K, V>>(node.kvList.subList(0, promoteIndex));
        Node<K, V> leftNode = new Node<K, V>(leftList, promoteKv);
        List<KV<K, V>> rightList = new ArrayList<KV<K, V>>(node.kvList.subList(promoteIndex + 1, node.kvList.size()));
        Node<K, V> rightNode = new Node<K, V>(rightList, promoteKv);

        Node<K, V> parentNode;
        if (null == node.parentKv) {
            List<KV<K, V>> kvList = new ArrayList<KV<K, V>>();
            kvList.add(promoteKv);
            parentNode = new Node<K, V>(kvList, null);
            // 父节点kv的chlidnode变化
            promoteKv.childNode = leftNode;
            parentNode.kvList.get(1).childNode = rightNode;

            root = null;// 协助gc
            root = parentNode;
        } else {
            parentNode = node.parentKv.node;
            // 父节点kv的chlidnode变化
            promoteKv.childNode = leftNode;
            parentNode.kvList.get(node.parentKv.getIndex() + 1).childNode = rightNode;
            // 提升至父节点
            parentNode.addKv(node.parentKv.getIndex() + 1, promoteKv);
            // 父节点提升分裂
            if (parentNode.kvList.size() >= m + 1) {
                splitNode(parentNode);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        List<KV<Integer, String>> keyValues = new ArrayList<KV<Integer, String>>();
        keyValues.add(new KV<Integer, String>(8, "8V"));
        Node<Integer, String> root = new Node<Integer, String>(keyValues, null);
        BTree<Integer, String> bTree = new BTree<Integer, String>(4, root);
        bTree.put(12, "12V");
        bTree.put(21, "21V");
        bTree.put(5, "5V");
        bTree.put(6, "6V");
        bTree.put(13, "13V");
        bTree.put(15, "15V");
        bTree.put(19, "19V");
        bTree.put(28, "28V");
        bTree.put(20, "20V");
        bTree.put(25, "25V");
        System.out.println(bTree.root.toString());
    }
}
