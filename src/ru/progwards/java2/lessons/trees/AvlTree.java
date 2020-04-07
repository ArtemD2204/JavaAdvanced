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
                leaf.height = 0;
                recalcHeght();
                makeBallance();
            } else {
                left = leaf;
                leaf.parent = this;
                leaf.height = 0;
                recalcHeght();
                makeBallance();
            }
        }

        private void recalcHeght() {
            AvlTree.TreeLeaf node = this;
            while (node != null) {
                int left = node.left == null ? 0 : node.left.height;
                int right = node.right == null ? 0 : node.right.height;
                node.height = Math.max(left, right) + 1;
                node = node.parent;
            }
        }

        int getBallance() {
            return getHeight(left) - getHeight(right);
        }

        private int getHeight(TreeLeaf node) {
            return node == null ? 0 : node.height;
        }

        private void smallRotate() {
            // Малое правое вращение
            AvlTree.TreeLeaf b = left;
            AvlTree.TreeLeaf c = b==null ? null : b.right;
            if (((getHeight(b) - getHeight(right)) == 2) && getHeight(c) <= getHeight(b.left)) {
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
                recalcHeght();
                return;
            }
            // Малое левое вращение
            b = right;
            c = b==null ? null : b.left;
            if (((getHeight(b) - getHeight(left)) == 2) && getHeight(c) <= getHeight(b.right)) {
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
                recalcHeght();
            }
        }

        private void bigRotate() {
            // Большое правое вращение
            AvlTree.TreeLeaf b = left;
            AvlTree.TreeLeaf c = b==null ? null : b.right;
            AvlTree.TreeLeaf n = c==null ? null : c.right;
            AvlTree.TreeLeaf m = c==null ? null : c.left;
            if (((getHeight(b) - getHeight(right)) == 2) && getHeight(c) > getHeight(b.left)) {
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
                recalcHeght();
                return;
            }
            // Большое левое вращение
            b = right;
            c = b.left;
            n = c==null ? null : c.right;
            m = c==null ? null : c.left;
            if (((getHeight(b) - getHeight(left)) == 2) && getHeight(c) > getHeight(b.right)) {
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
                recalcHeght();
            }
        }

        private void makeBallance() {
            AvlTree.TreeLeaf node = this;
            while (node != null) {
                if (Math.abs(node.getBallance()) > 1) {
                    node.smallRotate();
                }
                if (Math.abs(node.getBallance()) > 1) {
                    node.bigRotate();
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
            System.out.println(this);
//            System.out.println("root" + root);
            System.out.println("root.left" + root.left);
            System.out.println("root.left.left" + root.left.left + "   " + "root.left.right" + root.left.right);
            if (left != null || right != null) {
                int ballance = getBallance();
                AvlTree.TreeLeaf node = ballance > 0 ? left.findMax() : right.findMin();
                AvlTree.TreeLeaf nodeToStartBallance = node.parent;
                // подставляем node на место this
                // если у node есть потомки, то в subTreeToPaste сохраним поддерево удаляемого узла(this) для последующей вставки
                AvlTree.TreeLeaf subTreeToPaste = null;
                if (node.right == null)
                    node.right = right;
                else
                    subTreeToPaste = right;
                if (node.left == null)
                    node.left = left;
                else
                    subTreeToPaste = left;
                left = null;
                right = null;

                // удаляем ссылку у старого родителя на node
                if (node.parent.right == node)
                    node.parent.right = null;
                else
                    node.parent.left = null;

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

                nodeToStartBallance.recalcHeght();
                nodeToStartBallance.makeBallance();
            } else {
                if (parent != null) {
                    if (parent.right == this)
                        parent.right = null;
                    else
                        parent.left = null;
                    parent = null;
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

    private TreeLeaf<K, V> root;

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
}
