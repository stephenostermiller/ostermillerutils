/*
 * Interface to verify passwords.
 * Copyright (C) 2001-2002 Stephen Ostermiller <utils@Ostermiller.com>
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

/**
 * Interface to verify passwords.
 */
 public interface PasswordVerifier {
    /**
     * Verify that this password is an ok password.  If a password
     * is not verified it is thrown out and a new password is tried.
     * Always returning false from this method will cause an infinite
     * loop.
     * 
     * @param password an array of characters representing a password.
     * @returns true iff this password is ok.
     */
    public boolean verify(char[] password);
 
 }
