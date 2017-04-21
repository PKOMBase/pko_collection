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
            // 添加tail
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
            kvList.add(index, kv);
        }

        public KV<K, V> getTailKv() {
            if (null == kvList || kvList.size() <= 0) {
                return null;
            }
            return kvList.get(kvList.size() - 1);
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

    public V get(K key) {
        return get(root, key);
    }

    private V get(Node<K, V> node, K key) {
        if (null == node || node.kvList.size() <= 0) {
            return null;
        }
        int compareTo;
        for (KV<K, V> kv : node.kvList) {
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

    public void put(K key, V value) throws Exception {
        put(root, key, value);
    }

    private void put(Node<K, V> node, K key, V value) throws Exception {
        if (null == node || node.kvList.size() <= 0) {
            return;
        }
        if (null == key) {
            throw new Exception("key is null");
        }
        // 查询节点中的keylist
        int compareTo;
        int addIndex = -1;
        KV<K, V> kv = null;
        for (int i = 0; i < node.kvList.size(); i++) {
            kv = node.kvList.get(i);
            // tail节点
            if (null == kv.key) {
                // 如果tail没有子节点，直接添加至tail之前
                if (null == kv.childNode) {
                    addIndex = node.kvList.size() - 1;
                }
                // 有子节点，递归子节点
                else {
                    put(kv.childNode, key, value);
                }
                break;
            }
            compareTo = key.compareTo(kv.key);

            if (compareTo == 0) {
                kv.value = value;
            } else if (compareTo < 0) {
                // 如果没有子节点，直接添加至该节点之前
                if (null == kv.childNode) {
                    addIndex = i;
                }
                // 有子节点，递归子节点
                else {
                    put(kv.childNode, key, value);
                }
                break;
            } else if (compareTo > 0) {
                continue;
            }
        }

        if (addIndex >= 0) {
            node.addKv(addIndex, new KV<K, V>(key, value));
            // 平衡性操作
            if (node.kvList.size() >= m + 1) {
                this.splitNode(node);
            }
        }
    }

    private void splitNode(Node<K, V> node) {
        if (null == node) {
            return;
        }
        // 确定提升kv
        int promoteIndex = (m - 1) / 2;
        KV<K, V> promoteKv = node.kvList.get(promoteIndex);
        if (null == promoteKv) {
            return;
        }

        // 分裂
        List<KV<K, V>> leftList = new ArrayList<BTree.KV<K, V>>(node.kvList.subList(0, promoteIndex));
        List<KV<K, V>> rightList = new ArrayList<BTree.KV<K, V>>(node.kvList.subList(promoteIndex + 1,
                node.kvList.size()));

        Node<K, V> leftNode = new Node<K, V>(leftList, promoteKv);
        Node<K, V> rightNode = new Node<K, V>(rightList, promoteKv);

        Node<K, V> parentNode;
        if (null == node.parentKv) {
            List<KV<K, V>> kvList = new ArrayList<BTree.KV<K, V>>();
            kvList.add(promoteKv);
            parentNode = new Node<K, V>(kvList, null);

            // 提升kv的子节点处理
            leftNode.getTailKv().childNode = promoteKv.childNode;

            // 父kv的childnode处理
            promoteKv.childNode = leftNode;
            parentNode.getTailKv().childNode = rightNode;

            // 处理root
            root = null;// gc
            root = parentNode;
        } else {
            parentNode = node.parentKv.node;
            // 提升节点的子节点处理
            leftNode.getTailKv().childNode = promoteKv.childNode;

            // 父节点kv的childnode操作
            promoteKv.childNode = leftNode;
            parentNode.kvList.get(node.parentKv.getIndex() + 1).childNode = rightNode;

            // 提升至父节点
            parentNode.addKv(node.parentKv.getIndex() + 1, promoteKv);

            // 父节点递归分裂
            if (parentNode.kvList.size() >= m + 1) {
                this.splitNode(parentNode);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        List<KV<Integer, String>> kvList = new ArrayList<BTree.KV<Integer, String>>();
        kvList.add(new KV<Integer, String>(8, "8V"));
        Node<Integer, String> root = new Node<Integer, String>(kvList, null);
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

        // System.err.println(bTree.get(15));
    }

}
