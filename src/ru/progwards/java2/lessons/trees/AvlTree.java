package ru.progwards.java2.lessons.trees;

import java.util.function.Consumer;

public class AvlTree<K extends Comparable<K>, V> {
    private static final String KEYNOTEXIST = "Key not exist";

    class TreeLeaf<K extends Comparable<K>, V> {
        int height;
        K key;
        V value;
        AvlTree.TreeLeaf parent;
        AvlTree.TreeLeaf left;
        AvlTree.TreeLeaf right;

        public TreeLeaf(K key, V value) {
            this.key = key;
            this.value = value;
        }

        private TreeLeaf<K, V> find(K key) {
            int cmp = key.compareTo(this.key);
            if (cmp > 0)
                if (right != null)
                    return right.find(key);
                else
                    return this;
            if (cmp < 0)
                if (left != null)
                    return left.find(key);
                else
                    return this;
            return this;
        }

        void put(TreeLeaf<K, V> leaf) {
            int cmp = leaf.key.compareTo(key);
            if (cmp == 0) {
                this.value = leaf.value;
            } else if (cmp > 0) {
                right = leaf;
                leaf.parent = this;
                leaf.height = 1;
                recalcHeight();
                makeBallance();
            } else {
                left = leaf;
                leaf.parent = this;
                leaf.height = 1;
                recalcHeight();
                makeBallance();
            }
        }

        private void recalcHeight() {
            AvlTree.TreeLeaf node = this;
            while (node != null) {
                int left = node.getHeight(node.left);
                int right = node.getHeight(node.right);
                node.height = Math.max(left, right) + 1;
//                }
                node = node.parent;
            }
        }

        int getBallance() {
            return getHeight(left) - getHeight(right);
        }

        private int getHeight(TreeLeaf node) {
            return node == null ? 0 : node.height;
        }

        private void smallRightRotate() {
            // Малое правое вращение
            AvlTree.TreeLeaf b = left;
            AvlTree.TreeLeaf c = b == null ? null : b.right;
            left = c;
            b.right = this;
            b.parent = parent;
            if (parent != null) {
                if (parent.right == this)
                    parent.right = b;
                else
                    parent.left = b;
            }
            parent = b;
            if (c != null)
                c.parent = this;
            if (root == this)
                root = b;
            recalcHeight();
        }

        private void smallLeftRotate() {
            // Малое левое вращение
            AvlTree.TreeLeaf b = right;
            AvlTree.TreeLeaf c = b == null ? null : b.left;
            right = c;
            b.left = this;
            b.parent = parent;
            if (parent != null) {
                if (parent.right == this)
                    parent.right = b;
                else
                    parent.left = b;
            }
            parent = b;
            if (c != null)
                c.parent = this;
            if (root == this)
                root = b;
            recalcHeight();
        }

        private void bigRightRotate() {
            // Большое правое вращение
            AvlTree.TreeLeaf b = left;
            AvlTree.TreeLeaf c = b == null ? null : b.right;
            AvlTree.TreeLeaf n = c == null ? null : c.right;
            AvlTree.TreeLeaf m = c == null ? null : c.left;
            left = n;
            b.right = m;
            c.right = this;
            c.left = b;
            c.parent = parent;
            if (parent != null) {
                if (parent.right == this)
                    parent.right = c;
                else
                    parent.left = c;
            }
            parent = c;
            b.parent = c;
            if (n != null)
                n.parent = this;
            if (m != null)
                m.parent = b;
            if (root == this)
                root = c;
            recalcHeight();
            b.recalcHeight();
        }

        private void bigLeftRotate() {
            // Большое левое вращение
            AvlTree.TreeLeaf b = right;
            AvlTree.TreeLeaf c = b.left;
            AvlTree.TreeLeaf n = c == null ? null : c.right;
            AvlTree.TreeLeaf m = c == null ? null : c.left;
            right = m;
            b.left = n;
            c.left = this;
            c.right = b;
            c.parent = parent;
            if (parent != null) {
                if (parent.right == this)
                    parent.right = c;
                else
                    parent.left = c;
            }
            parent = c;
            b.parent = c;
            if (n != null)
                n.parent = b;
            if (m != null)
                m.parent = this;
            if (root == this)
                root = c;
            recalcHeight();
            b.recalcHeight();
        }

        private void makeBallance() {
            AvlTree.TreeLeaf node = this;
            while (node != null) {
                if (Math.abs(node.getBallance()) > 1) {
                    AvlTree.TreeLeaf b = node.left;
                    AvlTree.TreeLeaf c = b == null ? null : b.right;
                    if ((getHeight(b) - getHeight(node.right)) == 2) {
                        if (getHeight(c) <= getHeight(b.left)) {
                            node.smallRightRotate();
                        } else {
                            node.bigRightRotate();
                        }
                    }
                    b = node.right;
                    c = b == null ? null : b.left;
                    if ((getHeight(b) - getHeight(node.left)) == 2) {
                        if (getHeight(c) <= getHeight(b.right)) {
                            node.smallLeftRotate();
                        } else {
                            node.bigLeftRotate();
                        }
                    }
                }
                node = node.parent;
            }
        }

        private TreeLeaf<K, V> findMin() {
            AvlTree.TreeLeaf node = this;
            while (node.left != null) {
                node = node.left;
            }
            return node;
        }

        private TreeLeaf<K, V> findMax() {
            AvlTree.TreeLeaf node = this;
            while (node.right != null) {
                node = node.right;
            }
            return node;
        }

        void delete() {
            // Если удаляемый узел не терминальный
            if (left != null || right != null) {
                int ballance = getBallance();
                AvlTree.TreeLeaf node = ballance > 0 ? left.findMax() : right.findMin();
                // с узла nodeToStartBallance начинаем пересчет высот и балансировку при необходимости
                AvlTree.TreeLeaf nodeToStartBallance = this == node.parent ? node : node.parent;
                // подставляем node на место this
                // если у node есть потомки, то в subTreeToPaste сохраним поддерево удаляемого узла(this) для последующей вставки
                AvlTree.TreeLeaf subTreeToPaste = null;
                if (node.right == null)
                    node.right = node != right ? right : null;
                else
                    subTreeToPaste = right;
                if (node.left == null)
                    node.left = node != left ? left : null;
                else
                    subTreeToPaste = left;
                left = null;
                right = null;
                // удаляем ссылку у старого родителя на node
                if (node.parent.right == node)
                    node.parent.right = null;
                else
                    node.parent.left = null;
                // добавляем нового родителя у node
                node.parent = parent;
                if (parent != null) {
                    if (parent.right == this)
                        parent.right = node;
                    else
                        parent.left = node;
                    parent = null;
                } else {
                    AvlTree.this.root = node;
                }
                if (subTreeToPaste != null)
                    node.find(subTreeToPaste.key).put(subTreeToPaste);
                nodeToStartBallance.recalcHeight();
                nodeToStartBallance.makeBallance();
            } else {
                // else - иначе удаляемый узел терминальный
                if (parent != null) {
                    AvlTree.TreeLeaf nodeToStartBallance = parent;
                    if (parent.right == this)
                        parent.right = null;
                    else
                        parent.left = null;
                    parent = null;
                    nodeToStartBallance.recalcHeight();
                    nodeToStartBallance.makeBallance();
                } else {
                    AvlTree.this.root = null;
                }
            }
        }

        public String toString() {
            return "(" + key + "," + value + ")";
        }

        public void process(Consumer<TreeLeaf<K, V>> consumer) {
            if (left != null)
                left.process(consumer);
            consumer.accept(this);
            if (right != null)
                right.process(consumer);
        }
    }

    TreeLeaf<K, V> root;

    public V find(K key) {
        if (root == null)
            return null;
        TreeLeaf found = root.find(key);
        return found.key.compareTo(key) == 0 ? (V) found.value : null;
    }

    public void put(TreeLeaf<K, V> leaf) {
        if (root == null)
            root = leaf;
        else
            root.find(leaf.key).put(leaf);
    }

    public void put(K key, V value) {
        put(new TreeLeaf<>(key, value));
    }

    public void delete(K key) throws TreeException {
        internalDelete(key);
    }

    public TreeLeaf<K, V> internalDelete(K key) throws TreeException {
        if (root == null)
            throw new TreeException(KEYNOTEXIST);

        TreeLeaf found = root.find(key);
        int cmp = found.key.compareTo(key);
        if (cmp != 0)
            throw new TreeException(KEYNOTEXIST);
        found.delete();
        return found;
    }

    public void change(K oldKey, K newKey) throws TreeException {
        TreeLeaf<K, V> current = internalDelete(oldKey);
        current.key = newKey;
        put(current);
    }

    public void process(Consumer<TreeLeaf<K, V>> consumer) {
        if (root != null)
            root.process(consumer);
    }

    private void printTree() {
        System.out.println("                         " + (root));
        System.out.println("                 " + getChildren(root));
        if (root != null)
            System.out.println("          " + getChildren(root.left) + " " + getChildren(root.right));
        if (root.left != null)
            System.out.print(getChildren(root.left.left) + " " + getChildren(root.left.right));
        if (root.right != null)
            System.out.println("   " + getChildren(root.right.left) + " " + getChildren(root.right.right));
        System.out.println("--------------------------");
    }

    private String getChildren(TreeLeaf node) {
        if (node == null)
            return "null    null";
        String left = node.left == null ? "null" : node.left.toString();
        String right = node.right == null ? "null" : node.right.toString();
        return left + " " + right;
    }
}
