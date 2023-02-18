package dynamicHuffman;

public class HuffmanTree <T>{
    public int weight;
    public final T val;
    public int order;
    public HuffmanTree<T> st,dr,parent;

    public HuffmanTree(T val, int order) {
        this.val = val;
        this.st = null;
        this.dr = null;
        this.parent = null;
        this.weight = 1;
        this.order = order;
    }
    public void swap(HuffmanTree<T> t){
        if(t == this) return;
        if(parent == null || t.parent == null) return;
        HuffmanTree<T> p1 = parent, p2 = t.parent;

        boolean p1dir = p1.st == this;
        boolean p2dir = p2.st == t;
        parent = p2;
        t.parent = p1;
        if(p1dir)
            p1.st = t;
        else
            p1.dr = t;
        if(p2dir)
            p2.st = this;
        else
            p2.dr = this;
        int aux = t.order;
        t.order = this.order;
        this.order = aux;
    }
    public boolean isLeaf(){
        return st == null && dr == null;
    }

    public boolean isRoot(){
        return parent == null;
    }

    @Override
    public String toString() {
        TreePrinter<T> t = new TreePrinter<>();
        return t.printTree(this);
    }
}
