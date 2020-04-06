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

        private TreeLeaf<K,V> find(K key) {
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
            while(node != null) {
                int left = node.left == null ? 0 : node.left.height;
                int right = node.right == null ? 0 : node.right.height;
                node.height = Math.max(left, right) + 1;
                node = node.parent;
            }
        }

        private int getBallance() {
            return getHeight(left) - getHeight(right);
        }

        private int getHeight(TreeLeaf node) {
            return node == null ? 0 : node.height;
        }

        private void smallRotate() {
            // Малое правое вращение
            AvlTree.TreeLeaf b = left;
            AvlTree.TreeLeaf c = b.right;
            if (((b.height-this.right.height) == 2) && c.height <= b.left.height) {
                left = c;
                b.right = this;
                b.parent = parent;
                parent = b;
                c.parent = this;
                return;
            }
            // Малое левое вращение
            b = right;
            c = b.left;
            if (((b.height-this.left.height) == 2) && c.height <= b.right.height) {
                right = c;
                b.left = this;
                b.parent = parent;
                parent = b;
                c.parent = this;
            }
        }

        private void bigRotate() {
            // Большое правое вращение
            AvlTree.TreeLeaf b = left;
            AvlTree.TreeLeaf c = b.right;
            AvlTree.TreeLeaf n = c.right;
            AvlTree.TreeLeaf m = c.left;
            if (((b.height-this.right.height) == 2) && c.height > b.left.height) {
                left = n;
                b.right = m;
                c.right = this;
                c.left = b;
                c.parent = parent;
                parent = c;
                b.parent = c;
                n.parent = this;
                m.parent = b;
                return;
            }
            // Большое левое вращение
            b = right;
            c = b.left;
            n = c.right;
            m = c.left;
            if (((b.height-this.left.height) == 2) && c.height > b.right.height) {
                right = m;
                b.left = n;
                c.left = this;
                c.right = b;
                c.parent = parent;
                parent = c;
                b.parent = c;
                n.parent = b;
                m.parent = this;
            }
        }
        private void makeBallance() {
            AvlTree.TreeLeaf node = this;
            while (node != null) {
                if (Math.abs(node.getBallance()) > 1) {
                    node.smallRotate();
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

        void delete() throws TreeException {
            if (left != null || right != null) {
                int ballance = getBallance();
                AvlTree.TreeLeaf node;
//                AvlTree.TreeLeaf nodeToStartBallance;
                if (ballance > 0) {
                    node = left.findMax();
                    nodeToStartBallance
                } else {
                    node = right.findMin();

                }
                if (node.right == null)
                    node.right = right;
                if (node.left == null)
                    node.left = left;

                if (node.parent.right == node)
                    node.parent.right = null;
                else
                    node.parent.left = null;

                if (parent != null) {
                    if (parent.right == this)
                        parent.right = node;
                    else
                        parent.left = node;
                }
                node.parent = parent;
                node.recalcHeght();
                makeBallance(); // Для какого узла балансировку и пересчет высоты ?
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
            return "("+key+","+value+")";
        }

        public void process(Consumer<TreeLeaf<K,V>> consumer) {
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
        return found.key.compareTo(key) == 0 ? (V)found.value : null;
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
//        if (found.parent == null) {
//            if (found.right != null) {
//                root = found.right;
//                found.right.parent = null;  // без этой строки не удаляется правое поддерево
//                if (found.left != null) {
//                    put(found.left);
//                }
//            } else if (found.left != null) {
//                root = found.left;
//            } else
//                root = null;
//        } else
            found.delete();
        return found;
    }

    public void change(K oldKey, K newKey) throws TreeException {
        TreeLeaf<K, V> current = internalDelete(oldKey);
        current.key = newKey;
        put(current);
    }

    public void process(Consumer<TreeLeaf<K,V>> consumer) {
        if (root != null)
            root.process(consumer);
    }
}
