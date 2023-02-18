package dynamicHuffman;

import java.util.*;


public class HuffmanCoder<T>{
    private final HuffmanTree<T> root;
    private final Map<T, HuffmanTree<T>> alphabet = new HashMap<>();
    private HuffmanTree<T> NYTLeaf;
    private final Map<Integer, List<HuffmanTree<T>>> leafWeightLists = new HashMap<>();
    private final Map<Integer, List<HuffmanTree<T>>> innerWeightLists = new HashMap<>();

    private HuffmanTree<T> NYT(int order){
        // genereaza un nod frunza NYT;
        HuffmanTree<T> t = new HuffmanTree<>(null, order);
        t.weight = 0;
        return t;
    }
    private void increment(HuffmanTree<T> tree){
        // incrementeaza valoarea order a arborelui,
        // mutandu-l in lista corespunzatoare
        List<HuffmanTree<T>> firstList;
        if(tree.isLeaf())
            firstList = leafWeightLists.get(tree.weight);
        else
            firstList = innerWeightLists.get(tree.weight);
        if(firstList == null) return;
        firstList.remove(tree);
        if(firstList.isEmpty()){
            if(tree.isLeaf())
                leafWeightLists.remove(tree.weight);
            else
                innerWeightLists.remove(tree.weight);
        }
        tree.weight++;
        addToWeightLists(tree);
    }
    private void decrement(HuffmanTree<T> tree){
        // decrementeaza valoarea order a arborelui,
        // mutandu-l in lista corespunzatoare
        List<HuffmanTree<T>> firstList;
        if(tree.isLeaf())
            firstList = leafWeightLists.get(tree.weight);
        else
            firstList = innerWeightLists.get(tree.weight);
        if(firstList == null) return;
        firstList.remove(tree);
        if(firstList.isEmpty()){
            if(tree.isLeaf())
                leafWeightLists.remove(tree.weight);
            else
                innerWeightLists.remove(tree.weight);
        }
        tree.weight--;
        addToWeightLists(tree);
    }
    private HuffmanTree<T> splitNYT(T val){
        // transforma un nod frunza NYT in 3 noduri:
        //  - un nod intern cu weight 0
        //      - st: un nod frunza NYT
        //      - dr: un nod frunza cu valoarea val
        HuffmanTree<T> newLeaf = new HuffmanTree<>(val, NYTLeaf.order - 1);
        newLeaf.parent = NYTLeaf;
        HuffmanTree<T> newNYT = NYT(NYTLeaf.order-2);
        newNYT.parent = NYTLeaf;
        NYTLeaf.st = newNYT;
        NYTLeaf.dr = newLeaf;
        this.NYTLeaf = newNYT;

        addToWeightLists(newLeaf);
        addToWeightLists(newLeaf.parent);
        return NYTLeaf.parent;
    }
    private void addToWeightLists(HuffmanTree<T> tree){
        // adauga arborele in lista de weights corespunzatoare
        List<HuffmanTree<T>> list;
        if(tree.isLeaf())
            list = leafWeightLists.get(tree.weight);
        else
            list = innerWeightLists.get(tree.weight);
        if (list != null) {
            list.add(tree);
        } else {
            list = new ArrayList<>();
            list.add(tree);
            if(tree.isLeaf()){
                leafWeightLists.put(tree.weight, list);
            }else {
                innerWeightLists.put(tree.weight, list);
            }
        }
    }
    private HuffmanTree<T> findBlockLeader(int weight, boolean isLeaf){
        // gaseste arborele din lista de weight aleasa cu order maxim,
        // in functie de isLeaf
        List<HuffmanTree<T>> list;
        if(isLeaf)
            list = leafWeightLists.get(weight);
        else
            list = innerWeightLists.get(weight);
        if (list == null) return null;
        int max = 0;
        HuffmanTree<T> rez = null;
        for(HuffmanTree<T> t:list){
            if(t.order > max){
                max = t.order;
                rez = t;
            }
        }
        return rez;
    }

    public HuffmanCoder() {
        this.root = NYT(512);
        this.NYTLeaf = this.root;
    }
    public boolean isAdded(T val){
        return alphabet.containsKey(val);
    }
    public void addChar(T val){
        // adauga un caracter la arbore
        HuffmanTree<T> l = alphabet.get(val);
        HuffmanTree<T> current;
        if(l == null){
            current = splitNYT(val);
            alphabet.put(val, current.dr);
        }
        else {
            current = l;
        }
        while (current != null){

            if (current.isRoot()) {
                increment(current);
                current = null;
            } else {
                HuffmanTree<T> leader, next;
                // pentru arborele curent cu greutatea weight, incercam sa-l mutam in
                // blockul corespunzator cu greutatea weight + 1
                if(current.isLeaf()){
                    // daca e frunza
                    // gasim arborele cu aceeasi greutate si cu cel mai mare order
                    // pentru a le interschimba
                    // astfel, dupa incrementare, ajunge in blockul leaf urmator

                    leader = findBlockLeader(current.weight, false);
                    // cautam mai intai in blockul inner
                    if (leader != null) {
                        // daca il gasim
                        if (leader == current.parent) {
                            // daca e fiu al liderului gasit
                            // incercam sa-l interschimbam cu al doilea cel mai inaintat

                            increment(leader);
                            // incrementam temporar pe lider pentru a-l muta in lista urmatoare

                            HuffmanTree<T> aux = findBlockLeader(current.weight, false);
                            // cautam mai intai in blockul inner

                            if(aux == null)
                                aux = findBlockLeader(current.weight, true);
                                // daca nu gasim, cautam in blocul leaf

                            decrement(leader);
                            // decrementam liderul la loc


                            // categoria de greutate weight are cel putin 2 arbori
                            if(aux != current){
                                // daca gasim un arbore valid
                                current.swap(aux);
                                current.swap(leader);
                            }


                        } else {
                            // altfel le interschimbam
                            current.swap(leader);
                        }
                        increment(current);
                        // incrementam nodul curent si marcam parintele lui ca urmatorul
                        next = current.parent;

                        // dupa interschimbare este posibil ca liderul(inner) sa ajunga in mijlocul
                        // unui block de tip leaf. De aceea, cautam liderul blockului
                        HuffmanTree<T> aux = findBlockLeader(leader.weight, true);
                        if(aux != null )
                            if(leader.order < aux.order)
                                // daca ordinea liderului este mai mare, inseamna suntem la
                                // marginea dreapta, langa blockul inner urmator
                                leader.swap(aux);
                    } else {
                        // daca nu exista blockul inner, cautam in blocul leaf
                        leader = findBlockLeader(current.weight, true);

                        if (leader != current ){
                            // daca arborele curent este deja lider,
                            // nu mai trebuie sa facem interschimbarea
                            current.swap(leader);
                        }
                        increment(current);
                        // incrementam nodul curent si marcam parintele lui ca urmatorul
                        next = current.parent;
                    }

                } else { // inner
                    // cautam liderul blocului leaf cu greutate weight+1
                    // daca punem arborele curent la sfarsitul acelui bloc
                    // el va ajunge langa blocul inner cu greutate weight+1
                    leader = findBlockLeader(current.weight + 1, true);
                    if (leader != null) {
                        // daca l-am gasit
                        if(leader.parent != current){

                            current.swap(leader);
                            // interschimbam cu liderul si incrementam.
                            // intrucat arborele curent ajunge intr-un bloc cu weight+1,
                            // nu trebuie sa modificam noul parinte
                            increment(current);
                            HuffmanTree<T> aux = findBlockLeader(leader.weight-1, false);
                            // incercam sa mutam liderul la marginea blockului inner in care se afla
                            if (aux != null)
                                if (leader.order < aux.order)
                                    leader.swap(aux);
                            // marcam noul sau parinte ca urmatorul
                            // facem acest lucru intrucat parintele are acum un fiu
                            // cu o greutate mai mare cu 1, deci trebuie incrementat
                            next = leader.parent;
                        }else {
                            // daca liderul este fiul sau
                            increment(current);
                            // incrementam si marcam parintele ca urmatorul
                            next = current.parent;
                        }




                    } else {
                        // daca nu exista, incercam sa-l punem la sfarsitul blocului inner curent
                        leader = findBlockLeader(current.weight, false);

                        if (leader != current){
                            // daca nu este lider, il interschimbam
                            current.swap(leader);
                        }
                        increment(current);
                        // il incrementam si marcam printele lui ca vizitat
                        next = current.parent;

                    }
                }

                // trecem la urmatorul
                current = next;
            }

        }
       //TreePrinter<T> t = new TreePrinter<>();
       //System.out.println("______________________________________________");
       //System.out.println(t.printTree(root));
    }

    private BitStream getBits(HuffmanTree<T> tree){

        if(tree.isRoot()) return new BitStreamString();
        return getBits(tree.parent).append(tree.parent.st != tree);
    }

    public BitStream getBits(T val){

        if(isAdded(val))
            return getBits(alphabet.get(val));
        else
            return null;
    }
    public BitStream getBitsNYT(){

        return getBits(NYTLeaf);
    }
    public T getVal(BitStream bs){
        return getVal(bs, root);

    }
    private T getVal(BitStream bs, HuffmanTree<T> tree){

        if (tree.isLeaf())
            return tree.val;
        if(bs.take(1).equals("0")){
            return getVal(bs, tree.st);
        }else
            return getVal(bs, tree.dr);
    }

    public static void main(String[] args) {
        if(args.length <3) {
            System.out.println("<E/D> <InFile> <OutFile>");
        }
        if(args[0].equals("E")){
            HuffmanEncoder huffmanEncoder = new HuffmanEncoder();
            huffmanEncoder.encodeFile(args[1], args[2]);


        }else if(args[0].equals("D")){
            HuffmanDecoder huffmanDecoder = new HuffmanDecoder();
            huffmanDecoder.decodeFile(args[1], args[2]);
        }

    }
}
