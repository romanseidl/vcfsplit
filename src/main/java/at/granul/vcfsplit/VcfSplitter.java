package at.granul.vcfsplit;

/**
 * Vcf Splitter
 *
 * (c) 2016 by Roman Seidl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VcfSplitter {
    final static String startTag = "BEGIN:VCARD";
    final static String endTag = "END:VCARD";

    BufferedReader vcfReader = null;
    String outPath;

    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        if (args.length < 1) {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            JDialog vcfDialog = new VcfDialog();
            vcfDialog.setTitle("VCF Split");
            vcfDialog.setSize(500, 500);

            final Toolkit toolkit = Toolkit.getDefaultToolkit();
            final Dimension screenSize = toolkit.getScreenSize();
            final int x = (screenSize.width - vcfDialog.getWidth()) / 2;
            final int y = (screenSize.height - vcfDialog.getHeight()) / 2;
            vcfDialog.setLocation(x, y);

            vcfDialog.setVisible(true);
        } else {

            String inputFile = args[0];

            String directory;
            if (args.length < 2) {
                File file = new File("temp");
                directory = file.getAbsolutePath().replace("temp", "");

            } else {
                directory = args[1];
            }

            String filePath = directory + inputFile;

            VcfSplitter dBvcfSplitter = new VcfSplitter();
            dBvcfSplitter.split(filePath, directory);
        }
    }

    public VcfSplitter() {
    }


    public void split(String filePath, String outPath) {
        try {
            System.out.println(filePath);
            this.vcfReader = new BufferedReader(new FileReader(filePath));
            if (outPath.trim().length() > 0)
                outPath = outPath + File.separator;
            this.outPath = outPath;


            StringWriter vcfBuffer = null;

            String version = "";
            String fileName = "";

            try {
                while (this.vcfReader.ready()) {
                    String inLine = this.vcfReader.readLine();
                    if (inLine != null) {
                        if (inLine.equalsIgnoreCase(startTag)) {
                            vcfBuffer = new StringWriter();
                        } else if (inLine.equalsIgnoreCase(endTag)) {
                            File file = this.uniqueOutfile(fileName);
                            FileWriter fileWriter = new FileWriter(file);
                            fileWriter.write("BEGIN:VCARD\n");
                            fileWriter.write(version + "\n");

                            fileWriter.write(vcfBuffer.toString());
                            vcfBuffer.close();

                            fileWriter.write(endTag);
                            fileWriter.flush();
                            fileWriter.close();
                            log(Level.INFO, "Created " + file.getName());
                        } else if (inLine.startsWith("VERSION:")) {
                            version = inLine;
                        } else {
                            //Parse functional name to Filename
                            if (inLine.startsWith("FN:")) {
                                try {
                                    fileName = URLEncoder.encode(inLine.substring(3), "UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                            vcfBuffer.write(inLine + "\n");
                        }
                    }
                }
            } catch (IOException ex) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                log(Level.SEVERE, sw.toString());
            }
        } catch (FileNotFoundException var2) {
            log(Level.SEVERE, ("File " + filePath + " not found!\nExiting..."));
        }

    }

    protected void log(Level level, String text) {
        Logger.getLogger(VcfSplitter.class.getName()).log(level, text);
    }

    /**
     * Givers a unique file for the filename
     *
     * @param name
     * @return
     */
    private File uniqueOutfile(String name) {

        File file = new File(outPath + name + ".vcf");
        int count = 0;
        while (file.exists()) {
            count++;
            file = new File(outPath + name + "(" + count + ").vcf");
        }
        return file;
    }

}
