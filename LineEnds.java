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
    public static String version = "1.1";

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
            new LongOpt("quiet", LongOpt.NO_ARGUMENT, null, 'q'),             
            new LongOpt("reallyquiet", LongOpt.NO_ARGUMENT, null, 'Q'),             
            new LongOpt("verbose", LongOpt.NO_ARGUMENT, null, 'v'),            
            new LongOpt("noforce", LongOpt.NO_ARGUMENT, null, 4),
        };
        Getopt opts = new Getopt("LineEnds", args, "dnrsfqQv", longopts);
        int style = STYLE_SYSTEM; 
        boolean force = false;
        boolean printMessages = false;
        boolean printErrors = true;
        int c;
        while ((c = opts.getopt()) != -1){
            switch(c){
          		case 1:{
                    // print out the help message
                	System.out.println(
                        "LineEnds [-dnrsfvqQ] <files>\n\n" +
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
                        "   --noforce              Don't modify binary files. (default)\n" +
                        "   -v --verbose           Print a message for each file modified.\n" +
                        "   -q --quiet             Print error messages. (default)\n" +
                        "   -Q --reallyquiet       Print nothing.\n"
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
                case 'v':{
                    printMessages = true;
                    printErrors = true;
                } break;
                case 'q':{
                    printMessages = false;
                    printErrors = true;
                } break;
                case 'Q':{
                    printMessages = false;
                    printErrors = false;
                } break;
                default:{
                    System.exit(1);
                }
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
            boolean modified = false;
            try {
                source = new File(args[i]); 
                source = source.getCanonicalFile();
                in = new FileInputStream(source);  
                temp = File.createTempFile("LineEnds", null, null);
                out = new FileOutputStream(temp);
                modified = convert (in, out, style, !force);
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
                if (modified){
                    if (!source.delete() || !temp.renameTo(source)){                        
                        String message = "Could not be modified";
                        try {
                            in = new FileInputStream(temp);  
                            out = new FileOutputStream(source);
                            message += " and is likely corrupted";
                            copy(in, out);
                            temp.deleteOnExit();
                        } catch (IOException x1){
                            throw new IOException(message + ". Modifications can be found in " + temp);
                        }                        
                    } // else temp renamed so don't delete 
                    if (printMessages){
                        System.out.println("Modified line breaks in: " + args[i]);
                    }  
                } else {
                    temp.deleteOnExit(); 
                }
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
                if (!modified && temp != null && temp.exists()){
                    temp.deleteOnExit();                    
                } 
                if(printErrors){                              
                    System.err.println(args[i] + ": " + x.getMessage());
                }
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
    
    private final static int MASK_N = 0x01;
    private final static int MASK_R = 0x02;
    private final static int MASK_RN = 0x04;
   
    /**
     * Change the line endings of the text on the input stream and write 
     * it to the output stream.
     * 
     * The current system's line separator is used.
     *
     * @param in stream that contains the text which needs line number conversion.
     * @param out stream where converted text is written.
     * @return true if the output was modified from the input, false if it is exactly the same
     * @throws BinaryDataException if non-text data is encountered.
     * @throws IOException if an input or output error occurs.
     */
    public static boolean convert(InputStream in, OutputStream out) throws IOException {
        return convert(in, out, STYLE_SYSTEM, true);
    }
    
    /**
     * Change the line endings of the text on the input stream and write 
     * it to the output stream.
     *
     * @param in stream that contains the text which needs line number conversion.
     * @param out stream where converted text is written.
     * @param style line separator style.
     * @return true if the output was modified from the input, false if it is exactly the same
     * @throws BinaryDataException if non-text data is encountered.
     * @throws IOException if an input or output error occurs.
     * @throws IllegalArgumentException if an unknown style is requested.
     */
    public static boolean convert(InputStream in, OutputStream out, int style) throws IOException {
        return convert(in, out, style, true);
    }
    
    /**
     * Change the line endings of the text on the input stream and write 
     * it to the output stream.
     *     
     * The current system's line separator is used.
     *
     * @param in stream that contains the text which needs line number conversion.
     * @param out stream where converted text is written.
     * @param binaryException throw an exception and abort the operation if binary data is encountered and binaryExcepion is false.
     * @return true if the output was modified from the input, false if it is exactly the same
     * @throws BinaryDataException if non-text data is encountered.
     * @throws IOException if an input or output error occurs.
     */
    public static boolean convert(InputStream in, OutputStream out, boolean binaryException) throws IOException {
        return convert(in, out, STYLE_SYSTEM, binaryException);
    }
    
    /**
     * Change the line endings of the text on the input stream and write 
     * it to the output stream.
     *
     * @param in stream that contains the text which needs line number conversion.
     * @param out stream where converted text is written.
     * @param style line separator style.
     * @param binaryException throw an exception and abort the operation if binary data is encountered and binaryExcepion is false.
     * @return true if the output was modified from the input, false if it is exactly the same
     * @throws BinaryDataException if non-text data is encountered.
     * @throws IOException if an input or output error occurs.
     * @throws IllegalArgumentException if an unknown style is requested.
     */
    public static boolean convert(InputStream in, OutputStream out, int style, boolean binaryException) throws IOException {
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
        int seen = 0x00;
        while((read = in.read(buffer)) != -1){
            for (int i=0; i<read; i++){
                byte b = buffer[i];
                if (state==STATE_R){
                    if(b!='\n'){
                        out.write(lineEnding);
                        seen |= MASK_R;
                    }
                }
                if (b=='\r'){
                    state = STATE_R;
                } else {
                    if (b=='\n'){
                        if (state==STATE_R){                            
                            seen |= MASK_RN;
                        } else {                            
                            seen |= MASK_N;
                        }
                        out.write(lineEnding);
                    } else if(binaryException && b!='\t' && b!='\f' && (b & 0xff)<32){
                        throw new BinaryDataException("Binary data encountered, line break replacement aborted.");
                    } else {
                        out.write(b);
                    }                    
                    state = STATE_INIT;
                }
            }
        }
        if (state==STATE_R){
            out.write(lineEnding);
            seen |= MASK_R;
        }
        if (lineEnding.length==2 && lineEnding[0]=='\r' && lineEnding[1]=='\n'){
            return ((seen & ~MASK_RN)!=0);
        } else if (lineEnding.length==1 && lineEnding[0]=='\r'){
            return ((seen & ~MASK_R)!=0);
        } else if (lineEnding.length==1 && lineEnding[0]=='\n'){
            return ((seen & ~MASK_N)!=0);
        } else {
            return true;
        }
    }
    
    /**
     * Copy the date from the input stream to the output stream.
     *
     * @param in data source
     * @param out data destination
     * @throws IOException in an input or output error occurs
     */
    private static void copy(InputStream in, OutputStream out) throws IOException{
        byte[] buffer = new byte[BUFFER_SIZE];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
}
