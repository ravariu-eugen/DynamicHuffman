package dynamicHuffman;

public class TreePrinter <T>{

    private HuffmanTree<T> root;

    private int indent = 5;
    
    public String printTree(HuffmanTree<T> root) {

        this.root = root;

        return preorder(root, true);
    }


    public String preorder(HuffmanTree<T> currentNode, boolean lastChild) {
        String out = "";
        if(currentNode != null) {

            if (currentNode == root) {
                out += (String.format("%" + this.indent + "s", "") +  "└── " + printNode(currentNode) + '\n');
            }
            else if (lastChild) {
               out += (String.format("%" + this.indent + "s", "") +  "└── " + printNode(currentNode) + '\n');

            }
            else {
                out += (String.format("%" + this.indent + "s", "") +  "├── " + printNode(currentNode) + '\n');
            }


            this.indent += 8;
            out += preorder(currentNode.st, false);
            out += preorder(currentNode.dr, true);
            this.indent -= 8;
        }
        return out;
    }

    private String printNode(HuffmanTree<T> node) {
        return node.weight + " |" + node.val + "| " + node.order;
    }


}
