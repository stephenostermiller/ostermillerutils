/*
 * Adjusts line endings.
 * Copyright (C) 2001 Stephen Ostermiller <utils@Ostermiller.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * See COPYING.TXT for details.
 */
 
package com.Ostermiller.util;

import java.io.*;
import gnu.getopt.*;

/**
 * Stream editor to alter the line separators on text to match
 * that of a given platform.
 */
public class LineEnds {
    public static String version = "1.0";

    /**
     * Converts the line ending on files, or standard input.
     * Run with --help argument for more information.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args){    
        // create the command line options that we are looking for
        LongOpt[] longopts = {
            new LongOpt("help", LongOpt.NO_ARGUMENT, null, 1),
            new LongOpt("version", LongOpt.NO_ARGUMENT, null, 2),
            new LongOpt("about", LongOpt.NO_ARGUMENT, null, 3),
            new LongOpt("windows", LongOpt.NO_ARGUMENT, null, 'd'),
            new LongOpt("dos", LongOpt.NO_ARGUMENT, null, 'd'),
            new LongOpt("unix", LongOpt.NO_ARGUMENT, null, 'n'),
            new LongOpt("java", LongOpt.NO_ARGUMENT, null, 'n'),
            new LongOpt("mac", LongOpt.NO_ARGUMENT, null, 'r'),             
            new LongOpt("system", LongOpt.NO_ARGUMENT, null, 's'),            
            new LongOpt("force", LongOpt.NO_ARGUMENT, null, 'f'),            
            new LongOpt("noforce", LongOpt.NO_ARGUMENT, null, 4),
        };
        Getopt opts = new Getopt("LineEnds", args, "d:n:r:s:f:", longopts);
        int style = STYLE_SYSTEM; 
        boolean force = false;
        int c;
        while ((c = opts.getopt()) != -1){
            switch(c){
          		case 1:{
                    // print out the help message
                	System.out.println(
                        "LineEnds [-dnrsf] <files>\n\n" +
                        "   Adjusts the line endings in files.\n" +
                        "   If no files are specified standard in and out will be used.\n\n" +
                        "   --help                 Print this help message.\n" +
                        "   --version              Print out the version number.\n" +
                        "   --about                Print out license and contact info.\n" +
                        "   -d --windows --dos     Use the Windows/DOS line ending.\n" +
                        "   -n --unix --java       Use the UNIX/Java line ending.\n" +
                        "   -r --mac               Use the Macintosh line ending.\n" +
                        "   -s --system            Use the current system's line ending. (default)\n" +
                        "   -f --force             Always modify files, even binary files.\n" +
                        "   --noforce              Don't modify binary files. (default)\n"
					);
                    System.exit(0);
                } break;
                case 2:{
                    // print out the version message
                    System.out.println("Version " + version);
                    System.exit(0);
                } break;
                case 3:{
                    System.out.println(
                        "LineEnds -- Adjusts the line endings in files.\n" +
                        "Copyright (c) 2001 by Stephen Ostermiller (utils@ostermiller.com)\n" +
                        "\n" +
                        "This program is free software; you can redistribute it and/or modify\n" +
                        "it under the terms of the GNU Library General Public License as published\n" +
                        "by  the Free Software Foundation; either version 2 of the License or\n" +
                        "(at your option) any later version.\n" +
                        "\n" +
                        "This program is distributed in the hope that it will be useful, but\n" +
                        "WITHOUT ANY WARRANTY; without even the implied warranty of\n" +
                        "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n" +
                        "GNU General Public License for more details.\n" +
                        "\n" +
                        "You should have received a copy of the GNU General Public License\n" +
                        "along with this program; see the file COPYING.TXT.  If not, write to\n" +
                        "the Free Software Foundation Inc., 59 Temple Place - Suite 330,\n" +
                        "Boston, MA  02111-1307 USA, or visit http://www.gnu.org/"
                    );
                    System.exit(0);
                } break;
                case 'd':{
                    style = STYLE_RN;
                } break;
                case 'n':{
                    style = STYLE_N;
                } break;
                case 'r':{
                    style = STYLE_R;
                } break;
                case 's':{
                    style = STYLE_SYSTEM;
                } break;
                case 'f':{
                    force = true;
                } break;
                case 4:{
                    force = false;
                } break;
            }
        } 
        
        int exitCond = 0;
        boolean done = false;
        for (int i=opts.getOptind(); i<args.length; i++){
            done = true;
            File temp = null;
            File source = null;
            InputStream in = null;
            OutputStream out = null;
            try {
                source = new File(args[i]); 
                source = source.getCanonicalFile();
                in = new FileInputStream(source);  
                temp = File.createTempFile("LineEnds", null, source.getParentFile());
                out = new FileOutputStream(temp);
                convert (in, out, style, !force);
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
                source.delete();    
                temp.renameTo(source);
                temp = null;                             
            } catch (Exception x){                
                if (in != null){
                    try { 
                        in.close(); 
                    } catch (IOException ignore){
                    }
                }                  
                if (out != null){
                    try { 
                        out.flush(); 
                        out.close(); 
                    } catch (IOException ignore){
                    }
                }  
                if (temp != null && source.exists() && temp.exists()){
                    temp.delete();                    
                }                               
                System.err.println(args[i] + ": " + x.getMessage());
                exitCond = 1; 
            }                        
        }
        if (!done){
            try {
                convert (System.in, System.out, style, !force);            
            } catch (IOException x){                
                System.err.println(x.getMessage());
                exitCond = 1;
            }
        }        
        System.exit(exitCond);    
    }
    
    /**
     * The system line ending as determined
     * by System.getProperty("line.separator")
     */
    public final static int STYLE_SYSTEM = 0;
    /**
     * The Windows and DOS line ending ("\r\n")
     */
    public final static int STYLE_WINDOWS = 1;
    /**
     * The Windows and DOS line ending ("\r\n")
     */
    public final static int STYLE_DOS = 1;
    /**
     * The Windows and DOS line ending ("\r\n")
     */
    public final static int STYLE_RN = 1;
    /**
     * The UNIX and Java line ending ("\n")
     */
    public final static int STYLE_UNIX = 2;
    /**
     * The UNIX and Java line ending ("\n")
     */
    public final static int STYLE_N = 2;
    /**
     * The UNIX and Java line ending ("\n")
     */
    public final static int STYLE_JAVA = 2;
    /**
     * The Macintosh line ending ("\r")
     */
    public final static int STYLE_MAC = 3;
    /**
     * The Macintosh line ending ("\r")
     */
    public final static int STYLE_R = 3;
    
    /**
     * Buffer size when reading from input stream.
     */
    private final static int BUFFER_SIZE = 1024;
    
    private final static int STATE_INIT = 0;
    private final static int STATE_R = 1;
    
    /**
     * Change the line endings of the text on the input stream and write 
     * it to the output stream.
     * 
     * The current system's line separator is used.
     *
     * @param in stream that contains the text which needs line number conversion.
     * @param out stream where converted text is written.
     * @throws BinaryDataException if non-text data is encountered.
     * @throws IOException if an input or output error occurs.
     */
    public static void convert(InputStream in, OutputStream out) throws IOException {
        convert(in, out, STYLE_SYSTEM, true);
    }
    
    /**
     * Change the line endings of the text on the input stream and write 
     * it to the output stream.
     *
     * @param in stream that contains the text which needs line number conversion.
     * @param out stream where converted text is written.
     * @param style line separator style.
     * @param binaryException throw an exception and abort the operation if binary data is encountered.
     * @throws BinaryDataException if non-text data is encountered.
     * @throws IOException if an input or output error occurs.
     * @throws IllegalArgumentException if an unknown style is requested.
     */
    public static void convert(InputStream in, OutputStream out, int style) throws IOException {
        convert(in, out, style, true);
    }
    
    /**
     * Change the line endings of the text on the input stream and write 
     * it to the output stream.
     *     
     * The current system's line separator is used.
     *
     * @param in stream that contains the text which needs line number conversion.
     * @param out stream where converted text is written.
     * @param style line separator style.
     * @param binaryException throw an exception and abort the operation if binary data is encountered and binaryExcepion is false.
     * @throws BinaryDataException if non-text data is encountered.
     * @throws IOException if an input or output error occurs.
     */
    public static void convert(InputStream in, OutputStream out, boolean binaryException) throws IOException {
        convert(in, out, STYLE_SYSTEM, binaryException);
    }
    
    /**
     * Change the line endings of the text on the input stream and write 
     * it to the output stream.
     *
     * @param in stream that contains the text which needs line number conversion.
     * @param out stream where converted text is written.
     * @param style line separator style.
     * @param binaryException throw an exception and abort the operation if binary data is encountered and binaryExcepion is false.
     * @throws BinaryDataException if non-text data is encountered.
     * @throws IOException if an input or output error occurs.
     * @throws IllegalArgumentException if an unknown style is requested.
     */
    public static void convert(InputStream in, OutputStream out, int style, boolean binaryException) throws IOException {
        byte[] lineEnding;
        switch (style) {
            case STYLE_SYSTEM: {
               lineEnding = System.getProperty("line.separator").getBytes(); 
            } break;
            case STYLE_RN: {
               lineEnding = new byte[]{'\r','\n'}; 
            } break;
            case STYLE_R: {
               lineEnding = new byte[]{'\r'}; 
            } break;
            case STYLE_N: {
               lineEnding = new byte[]{'\n'};
            } break;
            default: {
                throw new IllegalArgumentException("Unknown line break style: " + style);
            }
        }
        byte[] buffer = new byte[BUFFER_SIZE];
        int read;
        int state = STATE_INIT;
        while((read = in.read(buffer)) != -1){
            for (int i=0; i<read; i++){
                byte b = buffer[i];
                if (state==STATE_R){
                    if(b!='\n'){
                        out.write(lineEnding);
                    }                    
                }
                if (b=='\r'){
                    state = STATE_R;
                } else {                    
                    state = STATE_INIT;
                    if (b=='\n'){
                        out.write(lineEnding);
                    } else if(binaryException && b!='\t' && b!='\f' && b<32){
                        throw new BinaryDataException("Binary data encountered, line break replacement aborted.");
                    } else {
                        out.write(b);
                    }
                }
            }
        }
        if (state==STATE_R){
            out.write(lineEnding);
        }   
    }
}
