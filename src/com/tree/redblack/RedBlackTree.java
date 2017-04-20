package com.tree.redblack;

public class RedBlackTree<K extends Comparable<K>, V> {

    private Node<K, V> root;

    public RedBlackTree(Node<K, V> root) {
        super();
        this.root = root;
    }

    static enum EnumRedBlack {
        red, black;
    }

    static class Node<K, V> {

        private Node<K, V> left;
        private Node<K, V> right;
        private K key;
        private V value;
        private EnumRedBlack color;

        public Node(K key, V value) {
            super();
            this.key = key;
            this.value = value;
            this.color = EnumRedBlack.red;
        }
    }

    private Node<K, V> rotateLeft(Node<K, V> node) {
        Node<K, V> resultNode = node.right;
        // 颜色
        resultNode.color = node.color;
        node.color = EnumRedBlack.red;

        // 子节点变化
        node.right = resultNode.left;
        resultNode.left = node;
        return resultNode;
    }

    private Node<K, V> rotateRight(Node<K, V> node) {
        Node<K, V> resultNode = node.left;
        resultNode.color = node.color;
        node.color = EnumRedBlack.red;

        node.left = resultNode.right;
        resultNode.right = node;
        return resultNode;
    }

    private Node<K, V> flipColor(Node<K, V> node) {
        node.right.color = node.color;
        node.left.color = node.color;
        node.color = EnumRedBlack.red;
        return node;
    }

    public V get(K key) {
        return get(root, key);
    }

    private V get(Node<K, V> node, K key) {
        if (null == node) {
            return null;
        }
        int compareTo = key.compareTo(node.key);
        if (compareTo == 0) {
            return node.value;
        } else if (compareTo < 0) {
            return get(node.left, key);
        } else if (compareTo > 0) {
            return get(node.right, key);
        }
        return null;
    }

    public void put(K key, V value) {
        put(root, key, value);
    }

    private Node<K, V> put(Node<K, V> node, K key, V value) {
        if (null == node) {
            return new Node<K, V>(key, value);
        }
        int compareTo = key.compareTo(node.key);
        if (compareTo == 0) {
            node.value = value;
        } else if (compareTo < 0) {
            node.left = put(node.left, key, value);
        } else if (compareTo > 0) {
            node.right = put(node.right, key, value);
        }

        // 2-4 平衡操作
        if (isRed(node.left) && !isRed(node.right)) {
            node = this.rotateLeft(node);
        }
        if (isRed(node.left) && isRed(node.left.left)) {
            node = this.rotateRight(node);
        }
        if (isRed(node.left) && isRed(node.right)) {
            node = this.flipColor(node);
        }
        return node;
    }

    private boolean isRed(Node<K, V> node) {
        if (null == node) {
            return false;
        }
        return EnumRedBlack.red == node.color ? true : false;
    }
}
