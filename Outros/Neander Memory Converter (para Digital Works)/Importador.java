/*
 * Decompiled with CFR 0_118.
 */
package nmc;

import java.awt.Component;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class Importador {
    final int[] cabMEM = new int[]{3, 78, 68, 82};
    final int[] cabMAP = new int[]{205, 171, 0, 0, 0, 1, 0, 0};
    final int lMEM = 4;
    final int lMAP = 8;
    final JFileChooser fc = new JFileChooser();
    BufferedInputStream arqIn;
    BufferedOutputStream arqOut;
    File arquivo;
    File arquivoOut;
    int[] cabTemp = new int[8];
    int[] v = new int[256];

    public void abreArq() {
        int returnValue = this.fc.showOpenDialog(null);
        if (returnValue == 1) {
            return;
        }
        this.arquivo = this.fc.getSelectedFile();
        if (!this.arquivo.canRead()) {
            JOptionPane.showMessageDialog(null, "O arquivo " + this.arquivo.getName() + " n\u00e3o pode ser lido!\n" + "Verifique se este n\u00e3o se encontra aberto em outro programa", "Erro de acesso!", 0);
            return;
        }
        try {
            this.arqIn = new BufferedInputStream(new FileInputStream(this.arquivo));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (this.arquivo.getName().toUpperCase().contains(".MAP") && this.verificaArqMAP()) {
            this.lerArq(true);
            this.gravaArq(true);
        }
        if (this.arquivo.getName().toUpperCase().contains(".MEM") && this.verificaArqMEM()) {
            this.lerArq(false);
            this.gravaArq(false);
        }
    }

    private boolean verificaArqMEM() {
        int i = 0;
        try {
            for (i = 0; i < 4; ++i) {
                this.cabTemp[i] = this.arqIn.read();
                if (this.cabTemp[i] == this.cabMEM[i]) continue;
                JOptionPane.showMessageDialog(null, this.arquivo.getName() + " n\u00e3o pode ser lido!\n" + "Pois este n\u00e3o \u00e9 um arquivo .mem v\u00e1lido", "Erro de compatibilidade!", 0);
                return false;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean verificaArqMAP() {
        int i = 0;
        try {
            for (i = 0; i < 8; ++i) {
                this.cabTemp[i] = this.arqIn.read();
                if (this.cabTemp[i] == this.cabMAP[i]) continue;
                JOptionPane.showMessageDialog(null, this.arquivo.getName() + " n\u00e3o pode ser lido!\n" + "Pois este n\u00e3o \u00e9 um arquivo .map v\u00e1lido", "Erro de compatibilidade!", 0);
                return false;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private void lerArq(boolean map) {
        int k = 0;
        int i = 0;
        int j = 0;
        int salto = map ? 4 : 2;
        do {
            try {
                k = this.arqIn.read();
                if (k == -1) {
                    break;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            if (j % salto == 0) {
                this.v[i] = k;
                ++i;
            }
            ++j;
        } while (true);
        try {
            this.arqIn.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int[] convertTOMEM(int[] v) {
        int i;
        int[] vOut = new int[v.length + 4];
        for (i = 0; i < 4; ++i) {
            vOut[i] = this.cabMEM[i];
        }
        for (i = 4; i < vOut.length; ++i) {
            vOut[i] = v[i - 4];
        }
        return vOut;
    }

    public int[] convertTOMAP(int[] v) {
        int i;
        int[] vOut = new int[v.length + 8];
        for (i = 0; i < 8; ++i) {
            vOut[i] = this.cabMAP[i];
        }
        for (i = 8; i < vOut.length; ++i) {
            vOut[i] = v[i - 8];
        }
        return vOut;
    }

    private void gravaArq(boolean map) {
        String path = this.arquivo.getAbsolutePath();
        path = path.substring(0, path.length() - 3);
        path = path + (map ? "mem" : "map");
        this.arquivoOut = new File(path);
        int[] vFinal = map ? this.convertTOMEM(this.v) : this.convertTOMAP(this.v);
        int cab = map ? 4 : 8;
        int k = map ? 2 : 4;
        try {
            int i;
            this.arqOut = new BufferedOutputStream(new FileOutputStream(this.arquivoOut));
            for (i = 0; i < cab; ++i) {
                this.arqOut.write(vFinal[i]);
            }
            this.arqOut.flush();
            for (i = cab; i < vFinal.length; ++i) {
                this.arqOut.write(vFinal[i]);
                for (int j = 1; j < k; ++j) {
                    this.arqOut.write(0);
                }
            }
            this.arqOut.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        JOptionPane.showMessageDialog(null, "Arquivo Convertido com Sucesso!\n" + this.arquivoOut.getAbsolutePath(), "Opera\u00e7\u00e3o conlu\u00edda", 1);
    }

    private void imprimeV(int[] v) {
        for (int i = 0; i < v.length; ++i) {
            System.out.println(v[i]);
        }
    }
}
