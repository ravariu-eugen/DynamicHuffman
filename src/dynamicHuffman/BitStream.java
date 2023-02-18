package dynamicHuffman;

public interface BitStream {
    String take(int nrBits);
    int size();
    BitStream append(BitStream bs);
    BitStream append(char c);
    BitStream append(boolean b);
    BitStream reverse();
}
