package com.tbright.algorithms;

public class BinaryTree {
    //创建二叉树
    public static void createBinaryTree() {
        BinaryTree binaryTree = new BinaryTree();
        BinaryTreeNode node1 = new BinaryTreeNode(1);
        BinaryTreeNode node2 = new BinaryTreeNode(2);
        BinaryTreeNode node3 = new BinaryTreeNode(3);
        node1.left = node2;
        node1.right = node3;
        BinaryTreeNode node4 = new BinaryTreeNode(4);
        BinaryTreeNode node5 = new BinaryTreeNode(5);
        node2.left = node4;
        node2.right = node5;
        BinaryTreeNode node6 = new BinaryTreeNode(6);
        BinaryTreeNode node7 = new BinaryTreeNode(7);
        node3.left = node6;
        node3.right = node7;
        BinaryTreeNode node8 = new BinaryTreeNode(8);
        node4.left = node8;
        binaryTree.setRoot(node1);
    }

    /**
     *
     *
     *                   1
     *            2             3
     *       4      5       6       7
     *   8      # #   #   #   #    #  #
     *
     */
    public static void createBinaryTree2() {

    }
    private static BinaryTreeNode root;

    void setRoot(BinaryTreeNode root) {
        this.root = root;
    }

    public static void main(String[] args) {
        createBinaryTree();
        System.out.println("-------开始前序遍历-------");
        preOrder(root);//前序遍历
        System.out.println("-------前序遍历结束-------");

        System.out.println("-------开始中序遍历-------");
        inOrder(root);//中序遍历
        System.out.println("-------中序遍历结束-------");

        System.out.println("-------开始后序遍历-------");
        postOrder(root);//后序遍历
        System.out.println("-------后序遍历结束-------");
    }

    //前序遍历
    static void preOrder(BinaryTreeNode node) {
        //1.输出父节点
        System.out.println(node.data);
        //2.遍历左子树。
        if (node.left != null) {
            preOrder(node.left);
        }
        //3.遍历右子树
        if (node.right != null) {
            preOrder(node.right);
        }
    }

    //中序遍历
    static void inOrder(BinaryTreeNode node){
        //1.遍历左子树。
        if (node.left != null) {
            inOrder(node.left);
        }
        //2.输出父节点
        System.out.println(node.data);
        //3.遍历右子树
        if (node.right != null) {
            inOrder(node.right);
        }
    }

    //后序遍历
    static void postOrder(BinaryTreeNode node) {
        //1.遍历左子树。
        if (node.left != null) {
            postOrder(node.left);
        }
        //2.遍历右子树
        if (node.right != null) {
            postOrder(node.right);
        }
        //3.输出父节点
        System.out.println(node.data);
    }
    public static class BinaryTreeNode {
        public BinaryTreeNode left;
        public BinaryTreeNode right;
        public int data;

        public BinaryTreeNode(int data) {
            this.data = data;
        }

        public void setLeft(BinaryTreeNode left) {
            this.left = left;
        }

        public void setRight(BinaryTreeNode right) {
            this.right = right;
        }
    }
}

