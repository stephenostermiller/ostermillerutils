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

import java.io.*;
import java.text.MessageFormat;
import java.net.URL;
import java.net.URLDecoder;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Properties;
import java.util.Vector;
import java.io.StringReader;
import java.lang.reflect.*;

/**
 * Allows URLs to be opened in the system browser on Windows and Unix.
 * This code is based on the methods in 
 * <a href="http://www.javaworld.com/javaworld/javatips/jw-javatip66.html">JavaWorld 
 * Java Tip 66: Control browsers from your Java application</a> but has more options, 
 * is written more cleanly and takes a bit of security into account. 
 */
public class Browser {

    /**
     * The dialog that allows user configuration of the options for this class.
     */
    protected static BrowserDialog dialog;
    
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
        exec = defaultCommands();
    }

    /**
     * Retrieve the default commands to open a browser for this system.
     */
    public static String[] defaultCommands(){
        String[] exec = null;
        if ( System.getProperty("os.name").startsWith("Windows")){
            exec = new String[1];
            exec[0] = "rundll32 url.dll,FileProtocolHandler {0}";
        } else if (System.getProperty("os.name").startsWith("Mac")){
             exec = null; 
        } else {
            Vector browsers = new Vector();
            try {
                Process p = Runtime.getRuntime().exec("which mozilla");
                if (p.waitFor() == 0){
                    browsers.add("mozilla -remote openURL({0})");
                    browsers.add("mozilla {0}");
                }
            } catch (IOException e){
            } catch (InterruptedException e){
            }
            try {
                Process p = Runtime.getRuntime().exec("which netscape");
                if (p.waitFor() == 0){
                    browsers.add("netscape -remote openURL({0})");
                    browsers.add("netscape {0}");
                }
            } catch (IOException e){
            } catch (InterruptedException e){
            }
            try {
                Process p = Runtime.getRuntime().exec("which xterm");
                if (p.waitFor() == 0){
                    p = Runtime.getRuntime().exec("which lynx");
                    if (p.waitFor() == 0){
                        browsers.add("xterm -e lynx {0}");
                    }
                }
            } catch (IOException e){
            } catch (InterruptedException e){
            }
            if (browsers.size() == 0){
                exec = null;
            } else {
                exec = new String[browsers.size()];
                exec = (String[])browsers.toArray(exec);
            }
        }
        return exec;
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
        	if (System.getProperty("os.name").startsWith("Mac")){
        		try {
        			Class mrjFileUtils = Class.forName("com.apple.mrj.MRJFileUtils");
        			Method openURL = mrjFileUtils.getMethod("openURL", new Class[] {Class.forName("java.lang.String")}); 
        			openURL.invoke(null, new Object[] {url});
        			//com.apple.mrj.MRJFileUtils.openURL(url);
        		} catch (Exception x){
        			System.err.println(x.getMessage());
        			throw new IOException("Browser launch failed");
        		}        		
        	} else {
            	throw new IOException("Browser execute command not defined");
            }
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
                    || c == '%' || c =='+' || c == '=' || c == '#'){
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
                    try {
                        // stick the url into the command
                        command = MessageFormat.format(exec[i], messageArray);
                        // parse the command line.
                        Vector argsVector = new Vector();
                        BrowserCommandLexer lex = new BrowserCommandLexer(new StringReader(command));
                        String t;
                        while ((t = lex.getNextToken()) != null) {
                            argsVector.add(t);
                        }
                        String[] args = new String[argsVector.size()];
                        args = (String[])argsVector.toArray(args);
                        // the windows urlprotocol handler doesn't work well with file urls.
                        // Correct those problems here before continuing
                        // Java File.toURL() gives only one / following file: but
                        // we need two.
                        // If there are escaped characters in the url, we will have
                        // to create an internet shortcut and open that, as the command
                        // line version of the rundll doesn't like them.
                        if (args[0].equals("rundll32") &&
							args[1].equals("url.dll,FileProtocolHandler") &&
                            args[2].startsWith("file:/")){
                            if (args[2].charAt(6) != '/'){
            					args[2] = "file://" + args[2].substring(6);
        					} 
							if (args[2].indexOf("%") != -1){
                                if (args[2].charAt(7) != '/'){
                                    args[2] = "file:///" + args[2].substring(7);
                                }
                                File shortcut = new File(System.getProperty("user.home"), "java");
                                shortcut = new File(shortcut, "Browser");
                                shortcut.mkdirs();
                                shortcut = new File(shortcut, "TempShortcut.url");
                                if (shortcut.exists()) shortcut.delete();
                                shortcut.createNewFile();
                                PrintWriter out = new PrintWriter(new FileWriter(shortcut));
                                out.println("[InternetShortcut]");
                                out.println("URL=" + args[2]);
                                out.close();
                                args[2] = shortcut.getCanonicalPath();
                            }
						}
                        // start the browser
                        Process p = Runtime.getRuntime().exec(args);

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
                    } catch (IOException x){
                        // the command was not a valid command.
                        System.err.println("Warning: " + x.getMessage());
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
            Browser.dialogConfiguration(null);
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
        System.exit(0);
    }

    /**
     * Show a dialog that allows the user to configure the
     * command lines used for starting a browser on their system.
     *
     * @param owner The frame that owns the dialog.
     */
    public static void dialogConfiguration(Frame owner){
        dialogConfiguration(owner, null);
    }

    /**
     * Show a dialog that allows the user to configure the
     * command lines used for starting a browser on their system.
     * String used in the dialog are taken from the given
     * properties.  This dialog can be customized or displayed in 
	 * multipl languages.
     * <P>
     * Properties that are used:
     * com.Ostermiller.util.BrowserDialog.title
     * com.Ostermiller.util.BrowserDialog.description
     * com.Ostermiller.util.BrowserDialog.label
     * com.Ostermiller.util.BrowserDialog.defaults
     * com.Ostermiller.util.BrowserDialog.browse
     * com.Ostermiller.util.BrowserDialog.ok
     * com.Ostermiller.util.BrowserDialog.cancel
     *
     * @param owner The frame that owns this dialog.
     * @param props contains the strings used in the dialog.
     */
    public static void dialogConfiguration(Frame owner, Properties props){
        if (Browser.dialog == null){
			Browser.dialog = new BrowserDialog(owner);
		}
        if (props != null){
            Browser.dialog.setProps(props);
        }
        Browser.dialog.show();
    }

    /**
     * A modal dialog that presents configuration option for this class.
     */
    private static class BrowserDialog extends JDialog {

        /**
         * Properties that are used:
         * com.Ostermiller.util.BrowserDialog.title
         * com.Ostermiller.util.BrowserDialog.description
         * com.Ostermiller.util.BrowserDialog.label
         * com.Ostermiller.util.BrowserDialog.defaults
         * com.Ostermiller.util.BrowserDialog.browse
         * com.Ostermiller.util.BrowserDialog.ok
         * com.Ostermiller.util.BrowserDialog.cancel
         */
        private void setProps(Properties props){
            if (props.containsKey("com.Ostermiller.util.BrowserDialog.title")){
				setTitle(props.getProperty("com.Ostermiller.util.BrowserDialog.title"));
            }
            if (props.containsKey("com.Ostermiller.util.BrowserDialog.description")){
				description.setText(props.getProperty("com.Ostermiller.util.BrowserDialog.description"));
            }
            if (props.containsKey("com.Ostermiller.util.BrowserDialog.label")){
                commandLinesLabel.setText(props.getProperty("com.Ostermiller.util.BrowserDialog.label"));
            }
            if (props.containsKey("com.Ostermiller.util.BrowserDialog.defaults")){
                resetButton.setText(props.getProperty("com.Ostermiller.util.BrowserDialog.defaults"));
            }
            if (props.containsKey("com.Ostermiller.util.BrowserDialog.browse")){
                browseButton.setText(props.getProperty("com.Ostermiller.util.BrowserDialog.browse"));
            }
            if (props.containsKey("com.Ostermiller.util.BrowserDialog.ok")){
                okButton.setText(props.getProperty("com.Ostermiller.util.BrowserDialog.ok"));
            }
            if (props.containsKey("com.Ostermiller.util.BrowserDialog.cancel")){
                cancelButton.setText(props.getProperty("com.Ostermiller.util.BrowserDialog.cancel"));
            }
			pack();
		}

        /**
         * Where the command lines are typed.
         */
        private JTextArea description;

        /**
         * Where the command lines are typed.
         */
        private JTextArea commandLinesArea;

        /**
         * The reset button.
         */
        private JButton resetButton;
        
        /**
         * The browse button.
         */
        private JButton browseButton;

        /**
         * The OK button.
         */
        private JButton okButton;

        /**
         * The cancel button.
         */
        private JButton cancelButton;

        /**
         * The label for the field in which the name is typed.
         */
        private JLabel commandLinesLabel;

        /**
         * update this variable when the user makes an action
         */
        private boolean pressed_OK = false;
        
        /**
         * File dialog for choosing a browser
         */
        private JFileChooser fileChooser;

        /**
         * Create this dialog with the given parent and title.
         *
         * @param parent window from which this dialog is launched
         * @param title the title for the dialog box window
         */
        public BrowserDialog(Frame parent) {
            super(parent, "Browser Configuration", true);
            setLocationRelativeTo(parent);
            // super calls dialogInit, so we don't need to do it again.
        }

        /**
         * Called by constructors to initialize the dialog.
         */
        protected void dialogInit(){

            commandLinesArea = new JTextArea("", 8, 40);
            JScrollPane scrollpane = new JScrollPane(commandLinesArea);
            okButton = new JButton("OK");
            cancelButton = new JButton("Cancel");
            resetButton = new JButton("Defaults");
            browseButton = new JButton("Browse");
            commandLinesLabel = new JLabel("Command lines: ");
            description = new JTextArea("When a web browser needs to be opened, these command lines will be\n" +
                "executed in the order they appear here until one of them works.\n" +
                "{0} is replaced by the URL to be opened.");
            description.setEditable(false);
            description.setOpaque( false );


            super.dialogInit();

            ActionListener actionListener = new ActionListener() {
                public void actionPerformed(ActionEvent e){
                    Object source = e.getSource();
                    if (source == resetButton){
                        setCommands(Browser.defaultCommands());
                    } else if (source == browseButton){
                        if (fileChooser == null){
                            fileChooser = new JFileChooser();
                        }
                        if (fileChooser.showOpenDialog(BrowserDialog.this) == JFileChooser.APPROVE_OPTION){
                            String app = fileChooser.getSelectedFile().getPath();
                            StringBuffer sb = new StringBuffer(2 * app.length());
                            for (int i=0; i<app.length(); i++){
                                char c = app.charAt(i);
                                // escape these two characters so that we can later parse the stuff
                                if (c == '\"' || c == '\\') {
                                    sb.append('\\');
                                }
                                sb.append(c);
							}
                            app = sb.toString();
                            if (app.indexOf(" ") != -1){
                                app = '"' + app + '"';
                            }
                            String commands = commandLinesArea.getText();
                            if (commands.length() != 0 && !commands.endsWith("\n") && !commands.endsWith("\r")){
                                commands += "\n";
                            }
                            commandLinesArea.setText(commands + app + " {0}");                            
                        }
                    } else {
                        pressed_OK = (source == okButton);
                        BrowserDialog.this.hide();
                    }
                }
            };

            GridBagLayout gridbag = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();
            c.insets.top = 5;
            c.insets.bottom = 5;
            JPanel pane = new JPanel(gridbag);
            pane.setBorder(BorderFactory.createEmptyBorder(10, 20, 5, 20));
            JLabel label;


            c.gridwidth = GridBagConstraints.REMAINDER;
            c.anchor = GridBagConstraints.WEST;
            gridbag.setConstraints(description, c);
            pane.add(description);

            c.gridy = 1;
            c.gridwidth = GridBagConstraints.RELATIVE;
            gridbag.setConstraints(commandLinesLabel, c);
            pane.add(commandLinesLabel);
            JPanel buttonPanel = new JPanel(); 
            c.anchor = GridBagConstraints.EAST;           
            browseButton.addActionListener(actionListener);
            buttonPanel.add(browseButton);            
            resetButton.addActionListener(actionListener);
            buttonPanel.add(resetButton);
            gridbag.setConstraints(buttonPanel, c);
            pane.add(buttonPanel);

            c.gridy = 2;
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.anchor = GridBagConstraints.WEST;
            gridbag.setConstraints(scrollpane, c);
            pane.add(scrollpane);

            c.gridy = 3;
            c.anchor = GridBagConstraints.CENTER;
            JPanel panel = new JPanel();
            okButton.addActionListener(actionListener);
            panel.add(okButton);
            cancelButton.addActionListener(actionListener);
            panel.add(cancelButton);
            gridbag.setConstraints(panel, c);
            pane.add(panel);

            getContentPane().add(pane);

            pack();
        }

        /**
         * Shows the dialog.
         */
        public void show(){
            setCommands(Browser.exec);
            super.show();
            if (pressed_OK){
                java.util.StringTokenizer tok = new java.util.StringTokenizer(commandLinesArea.getText(), "\r\n", false);
                int count = tok.countTokens();
                String[] exec = new String[count];
                for (int i=0; i < count; i++){
                    exec[i] = tok.nextToken();
                }
                Browser.exec = exec;
            }
        }

        private void setCommands(String[] exec){
            StringBuffer sb = new StringBuffer();
            for (int i=0; exec != null && i < exec.length; i++){
                sb.append(exec[i]).append('\n');
            }
            commandLinesArea.setText(sb.toString());
        }
    }
}

