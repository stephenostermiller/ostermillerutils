/*
 * Control a web browser from your java application.
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

import java.io.IOException;
import java.text.MessageFormat;
import java.net.URL;

/**
 * Allows URLs to be opened in the system browser on Windows and Unix.
 * This code is based on the methods in 
 * <a href="http://www.javaworld.com/javaworld/javatips/jw-javatip66.html">JavaWorld 
 * Java Tip 66: Control browsers from your Java application</a> but has more options, 
 * is written more cleanly and takes a bit of security into account. 
 */
public class Browser {
    
    /**
     * A list of commands to try in order to display the url.
     * The url is put into the command using MessageFormat, so
     * the URL will be specified as {0} in the command.
     * Some examples of commands to try might be:<br>
     * <code>rundll32 url.dll,FileProtocolHandler {0}</code></br>
     * <code>netscape {0}</code><br>
     * These commands are passed in order to exec until something works
     * when displayURL is used.
     */
    public static String[] exec = null;

    /** 
     * Determine appropriate commands to start a browser on the current
     * operating system.  On windows: <br>
     * <code>rundll32 url.dll,FileProtocolHandler {0}</code></br>
     * On other operating systems, the "which" command is used to 
     * test if mozilla, netscape, and lynx(xterm) are available (in that
     * order).
     */
    public static void init(){
        if ( System.getProperty("os.name").startsWith("Windows")){
            exec = new String[1];
            exec[0] = "rundll32 url.dll,FileProtocolHandler {0}";
        } else {
            boolean found = false;
            if (!found){
                try {
                    Process p = Runtime.getRuntime().exec("which mozilla");
                    if (p.waitFor() == 0){
                        exec = new String[1];
                        exec[0] = "mozilla {0}";
                        found = true;
                    }
                } catch (IOException e){
                } catch (InterruptedException e){
                }
            }
            if (!found){
                try {
                    Process p = Runtime.getRuntime().exec("which netscape");
                    if (p.waitFor() == 0){
                        exec = new String[2];
                        exec[0] = "netscape -remote openURL({0})";
                        exec[1] = "netscape {0}";
                        found = true;
                    }
                } catch (IOException e){
                } catch (InterruptedException e){
                }
            }
            if (!found){
                try {
                    Process p = Runtime.getRuntime().exec("which xterm");
                    if (p.waitFor() == 0){
                        p = Runtime.getRuntime().exec("which lynx");
                        if (p.waitFor() == 0){
                            exec = new String[1];
                            exec[0] = "xterm -e lynx {0}";
                            found = true;
                        }
                    }
                } catch (IOException e){
                } catch (InterruptedException e){
                }               
            }
            if (!found){
                exec = null;
            }
        }
    }
    
    /**
     * Display a URL in the system browser.  
     * 
     * Browser.init() should be called before calling this function or
     * Browser.exec should be set explicitly.
     * 
     * For security reasons, the URL will may not be passed directly to the
     * browser as it is passed to this method.  The URL may be made safe for 
     * the exec command by URLEncoding the URL before passing it.
     *
     * @param url the url to display
     * @param throws IOException if the url is not valid or the browser fails to start
     */
    public static void displayURL(String url) throws IOException {
        if (exec == null || exec.length == 0){
            throw new IOException("Browser execute command not defined");
        } else {
            // for security, see if the url is valid.  
            // this is primarily to catch an attack in which the url 
            // starts with a - to fool the command line flags, but 
            // it could catch other stuff as well, and will throw a
            // MalformedURLException which will give the caller of this
            // function useful information.
            new URL(url);
            // escape any weird characters in the url.  This is primarily
            // to prevent an attacker from putting in spaces
            // that might fool exec into allowing
            // the attacker to execute arbitrary code.
            StringBuffer sb = new StringBuffer(url.length());
            for (int i=0; i<url.length(); i++){
                char c = url.charAt(i);
                if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')
                    || c == '.' || c == ':' || c == '&' || c == '@' || c == '/' || c == '?' 
                    || c == '%' || c =='+' || c == '='){
                    //characters that are nessesary for urls and should be safe
                    //to pass to exec.  Exec uses a default string tokenizer with
                    //the default arguments (whitespace) to separate command line
                    //arguments, so there should be no problem with anything but 
                    //whitespace.
                    sb.append(c);
                } else{
                    c = (char)(c & 0xFF); // get the lowest 8 bits (URLEncoding)
                    if (c < 0x10) {                    
                        sb.append("%0" + Integer.toHexString(c));
                    } else {
                        sb.append("%" + Integer.toHexString(c));
                    }

                }                 
            }
            String[] messageArray = new String[1];
        	messageArray[0] = sb.toString();
            String command = null;
            boolean found = false;
            // try each of the exec commands until something works
            try {
                for (int i=0; i<exec.length && !found; i++){
                    // stick the url into the command
                    command = MessageFormat.format(exec[i], messageArray);
                    // start the browser
                    Process p = Runtime.getRuntime().exec(command);
                    // give the browser a bit of time to fail.
                    // I have found that sometimes sleep doesn't work 
                    // the first time, so do it twice.  My tests 
                    // seem to show that 1000 milisec is enough 
                    // time for the browsers I'm using.
                    for (int j=0; j<2; j++){
                        try{
            	            Thread.currentThread().sleep(1000);
                        } catch (InterruptedException inte){
                        }
                    }
                    if (p.exitValue() == 0){
                        // this is a weird case.  The browser exited after 
                        // a couple seconds saying that it successfully
                        // displayed the url.  Either the browser is lying
                        // or the user closed it *really* quickly.  Oh well.
                        found = true;
                    }
                }
                if (!found){
                    // we never found a command that didn't terminate with an error.
                    throw new IOException("Browser launch failed.");
                }

            } catch (IllegalThreadStateException e){      
                // the browser is still running.  This is a good sign.
                // lets just say that it is displaying the url right now!
            }
        }
    }

    /**
     * Open the url(s) specified on the command line in your browser.
     *
     * @param args Command line arguments (URLs)
     */
    public static void main(String[] args){       
        try {
            Browser.init();
            if (args.length == 0){
        	    Browser.displayURL("http://www.javaworld.com/");
            } else {
                for (int i=0; i<args.length; i++){
                    Browser.displayURL(args[i]);
                }
            }
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}
