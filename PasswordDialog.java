/*
 * A password dialog box.
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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * A modal dialog that asks the user for a username and password.
 * More information about this class is available from <a href=
 * "http://ostermiller.org/utils/PasswordDialog.html">ostermiller.org</a>.
 *
 * <code>
 * <pre>
 * PasswordDialog p = new PasswordDialog(null, "Test");
 * if(p.showDialog()){
 *     System.out.println("Name: " + p.getName());
 *     System.out.println("Pass: " + p.getPass());
 * } else {
 *     System.out.println("User selected cancel");
 * }
 * </pre>
 * </code>
 */
public class PasswordDialog extends JDialog {

    /**
     * Where the name is typed.
     */
	protected JTextField name;
    /**
     * Where the password is typed.
     */
    protected JPasswordField pass;
    /**
     * The OK button.
     */
    protected JButton okButton;
    /**
     * The cancel button.
     */
    protected JButton cancelButton;
    /**
     * The label for the field in which the name is typed.
     */
    protected JLabel nameLabel;
    /**
     * The label for the field in which the password is typed.
     */
    protected JLabel passLabel;

    /**
     * Set the name that appears as the default
     * An empty string will be used if this in not specified
     * before the dialog is displayed.
     *
     * @param name default name to be displayed.
     */
    public void setName(String name){
        this.name.setText(name);
    }

    /**
     * Set the password that appears as the default
     * An empty string will be used if this in not specified
     * before the dialog is displayed.
     *
     * @param pass default password to be displayed.
     */
    public void setPass(String pass){
        this.pass.setText(pass);
    }

    /**
     * Set the label on the ok button.
     * "OK" is the default.
     *
     * @param ok label for the ok button.
     */
    public void setOKText(String ok){
        this.okButton.setText(ok);
    }

    /**
     * Set the label on the cancel button.
     * "Cancel" is the default.
     *
     * @param cancel label for the cancel button.
     */
    public void setCancelText(String cancel){
        this.cancelButton.setText(cancel);
    }

    /**
     * Set the label for the field in which the name is entered.
     * "Name: " is the default.
     *
     * @param name label for the name field.
     */
    public void setNameLabel(String name){
        this.nameLabel.setText(name);
    }

    /**
     * Set the label for the field in which the password is entered.
     * "Password: " is the default.
     *
     * @param pass label for the password field.
     */
    public void setPassLabel(String pass){
        this.passLabel.setText(pass);
    }

    /**
     * Get the name that was entered into the dialog before
     * the dialog was closed.
     *
     * @return the name from the name field.
     */
    public String getName(){
        return name.getText();
    }

    /**
     * Get the password that was entered into the dialog before
     * the dialog was closed.
     *
     * @return the password from the password field.
     */
    public String getPass(){
        return new String(pass.getPassword());
	}

    /**
     * Did the user use the OK button or an equivelant action
     * to close the dialog box?
     * Pressing enter in the password field may be the same as
     * 'OK' but closing the dialog and pressing the cancel button
     * are not.
     *
     * @return true if the the user hit OK, false if the user cancelled.
     */
    public boolean okPressed(){
        return pressed_OK;
    }

    /**
     * update this variable when the user makes an action
     */
	private boolean pressed_OK = false;

  	/**
     * Create this dialog with the given parent and title.
     *
     * @param parent window from which this dialog is launched
     * @param title the title for the dialog box window
     */
	public PasswordDialog(Frame parent, String title) {
        super(parent, title, true);
        setLocationRelativeTo(parent);
        // super calls dialogInit, so we don't need to do it again.
    }

    /**
     * Called by constructors to initialize the dialog.
     */
	protected void dialogInit(){

        name = new JTextField("", 20);
        pass = new JPasswordField("", 20);
        okButton = new JButton("OK");
		cancelButton = new JButton("Cancel");
        nameLabel = new JLabel("Name: ");
        passLabel = new JLabel("Password: ");

		super.dialogInit();

        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e){
                Object source = e.getSource();
                if (source == name){
                    // the user pressed enter in the name field.
                    name.transferFocus();
                } else {
                    // other actions close the dialog.
                    pressed_OK = (source == pass || source == okButton);
                    PasswordDialog.this.hide();
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

        c.anchor = GridBagConstraints.EAST;
        gridbag.setConstraints(nameLabel, c);
        pane.add(nameLabel);

        gridbag.setConstraints(name, c);
        name.addActionListener(actionListener);
        pane.add(name);

        c.gridy = 1;
        gridbag.setConstraints(passLabel, c);
        pane.add(passLabel);

        gridbag.setConstraints(pass, c);
        pass.addActionListener(actionListener);
        pane.add(pass);

        c.gridy = 2;
        c.gridwidth = GridBagConstraints.REMAINDER;
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
     * Shows the dialog and returns true if the user pressed ok.
     *
	 * @return true if the the user hit OK, false if the user cancelled.
     */
	public boolean showDialog(){
        show();
        return okPressed();
	}

    /**
     * A simple example to show how this might be used.
     * If there are arguments passed to this program, the first
     * is treated as the default name, the second as the default password
     *
     * @param args command line arguments: name and password (optional)
     */
    private static void main(String[] args){
        PasswordDialog p = new PasswordDialog(null, "Test");
        if(args.length > 0){
            p.setName(args[0]);
        }
        if(args.length > 1){
            p.setPass(args[1]);
        }
        if(p.showDialog()){
            System.out.println("Name: " + p.getName());
            System.out.println("Pass: " + p.getPass());
        } else {
            System.out.println("User selected cancel");
        }
        p.dispose();
        p = null;
        System.exit(0);
    }
}
