package com.tree.binary;

public class BinaryTree<K extends Comparable<K>, V> {

    private Node<K, V> root;

    public BinaryTree(Node<K, V> root) {
        super();
        this.root = root;
    }

    public V get(K key) {
        return get(root, key);
    }

    private V get(Node<K, V> node, K key) {
        if (null == node) {
            return null;
        }
        int compareTo = key.compareTo(node.getKey());
        if (compareTo == 0) {
            return node.getValue();
        } else if (compareTo < 0) {
            return get(node.getLeft(), key);
        } else if (compareTo > 0) {
            return get(node.getRight(), key);
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
        int compareTo = key.compareTo(node.getKey());
        if (compareTo == 0) {
            node.setValue(value);
        } else if (compareTo < 0) {
            node.setLeft(put(node.getLeft(), key, value));
        } else if (compareTo > 0) {
            node.setRight(put(node.getRight(), key, value));
        }
        return node;
    }

    public void preOrder(Node<K, V> node) {
        if (null == node) {
            return;
        }

        print(node);
        preOrder(node.getLeft());
        preOrder(node.getRight());

    }

    public void inOrder(Node<K, V> node) {
        if (null == node) {
            return;
        }
        inOrder(node.getLeft());
        print(node);
        inOrder(node.getRight());
    }

    private void print(Node<K, V> node) {
        System.out.print("[" + node.getKey() + "]=" + node.getValue() + ", ");
    }

    public static void main(String[] args) {
        Node<Integer, String> root = new Node<Integer, String>(12, "t12");
        BinaryTree<Integer, String> binaryTree = new BinaryTree<Integer, String>(root);
        binaryTree.put(8, "t8");
        binaryTree.put(5, "t5");
        binaryTree.put(6, "t6");
        binaryTree.put(19, "t19");
        binaryTree.put(15, "t15");
        binaryTree.put(21, "t21");
        System.out.println(binaryTree.root.toString());

        System.out.println("search:" + binaryTree.get(19));

        binaryTree.preOrder(root);
        System.out.println();
        binaryTree.inOrder(root);
        System.out.println();
    }

    static class Node<K, V> {

        private Node<K, V> left;
        private Node<K, V> right;
        private K key;
        private V value;

        public Node<K, V> getLeft() {
            return left;
        }

        public void setLeft(Node<K, V> left) {
            this.left = left;
        }

        public Node<K, V> getRight() {
            return right;
        }

        public void setRight(Node<K, V> right) {
            this.right = right;
        }

        public K getKey() {
            return key;
        }

        public void setKey(K key) {
            this.key = key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        public Node(K key, V value) {
            super();
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return "{\"" + key + "\":\"" + value + "\", \"left\"=" + (null == left ? "\"\"" : left.toString())
                    + ", \"right\"=" + (null == right ? "\"\"" : right.toString()) + "}";
        }
    }
}
