package dynamicHuffman;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class HuffmanEncoder {
    private HuffmanCoder<Character> hc = new HuffmanCoder<>();
    private BitStream bs = new BitStreamString();
    private void encodeToBitStream(char c){
        if(hc.isAdded(c)){
            bs.append(hc.getBits(c));
        }
        else {
            bs.append(hc.getBitsNYT());
            bs.append(c);
        }
        hc.addChar(c);
    }
    public void encodeFile(String fileInName, String fileOutName){
        hc = new HuffmanCoder<>();
        bs = new BitStreamString();
        int nextCharInFile;
        final int EOFConst = -1;
        try {
            FileInputStream fin = new FileInputStream(fileInName);
            FileOutputStream fout = new FileOutputStream(fileOutName);
            while ((nextCharInFile = fin.read()) != EOFConst){
                char c = (char) nextCharInFile;
                encodeToBitStream(c);
                while (bs.size() >= 8){ // scrie in fisier
                    String ns = bs.take(8);
                    byte f = (byte) Integer.parseInt(ns, 2);
                    fout.write(f);
                }
            }
            bs.append(hc.getBitsNYT());
            // scrie NYT pentru a marca sfarsitul si umple pana la octet
            if(bs.size() > 0){

                while (bs.size() >= 8){ // scrie in fisier
                    String ns = bs.take(8);
                    byte f = (byte) Integer.parseInt(ns, 2);
                    fout.write(f);
                }
                int size = bs.size();
                if(size > 0){
                    for(int i = 0; i < (8-size)%8; i++)
                        bs.append(false); // filler

                    String ns = bs.take(8);
                    byte f = (byte) Integer.parseInt(ns, 2);
                    fout.write(f);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
