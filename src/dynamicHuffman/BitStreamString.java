package dynamicHuffman;

public class BitStreamString implements BitStream {
    private final StringBuilder bits = new StringBuilder();
    @Override
    public String take(int nrBits) {
        String rez = bits.substring(0,nrBits);
        bits.delete(0, nrBits);
        return rez;
    }

    @Override
    public int size() {
        return bits.length();
    }

    @Override
    public BitStream append(BitStream bs) {
        bits.append(bs.toString());
        return this;
    }

    @Override
    public BitStream append(char c) {
        bits.append(String.format("%8s", Integer.toBinaryString(c)).replace(" ", "0"));
        return this;
    }

    @Override
    public BitStream append(boolean b) {
        bits.append(b?"1":"0");
        return this;
    }

    @Override
    public BitStream reverse() {
        bits.reverse();
        return this;
    }

    @Override
    public String toString() {
        return bits.toString();
    }
}
