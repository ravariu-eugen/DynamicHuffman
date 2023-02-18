package dynamicHuffman;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class HuffmanDecoder {
    private final HuffmanCoder<Character> hc = new HuffmanCoder<>();
    private final BitStream bs = new BitStreamString();
    private Character decodeFromBitStream() {

        return hc.getVal(bs);
    }
    public void decodeFile(String fileInName, String fileOutName){

        int nextCharInFile;
        final int EOFConst = -1;
        try {
            FileInputStream fin = new FileInputStream(fileInName);
            FileOutputStream fout = new FileOutputStream(fileOutName);
            while ((nextCharInFile = fin.read()) != EOFConst){
                char c = (char) nextCharInFile;
                bs.append(c);
                while (bs.size() > 32){
                    Character t = decodeFromBitStream();
                    if(t == null){ // am citit NYT
                        String ns = bs.take(8);
                        byte f = (byte) Integer.parseInt(ns, 2);
                        hc.addChar((char) f);
                        fout.write(f);
                    }else {
                        hc.addChar(t);
                        fout.write(t);

                    } // am citit un caracter
                }
            }
            while (bs.size() > 0){
                Character t = decodeFromBitStream();
                if(t == null){ // am citit NYT
                    if(bs.size() < 8) return;
                    String ns = bs.take(8);
                    byte f = (byte) Integer.parseInt(ns, 2);
                    hc.addChar((char) f);
                    fout.write(f);
                }else {
                    hc.addChar(t);
                    fout.write(t);

                } // am citit un caracter
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
