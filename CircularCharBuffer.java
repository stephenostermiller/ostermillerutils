/*
 * Circular Character Buffer
 * Copyright (C) 2002 Stephen Ostermiller <utils@Ostermiller.com>
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

import java.io.Reader;
import java.io.Writer;
import java.io.IOException;

/**
 * Implements the Circular Buffer producer/consumer model for characters.
 * More information about this class is available from <a href=
 * "http://ostermiller.org/utils/CircularCharBuffer.html">ostermiller.org</a>.
 * <p>
 * Using this class is a simpler alternative to using a PipedReader
 * and a PipedWriter. PipedReaders and PipedWriters don't support the
 * mark operation, don't allow you to control buffer sizes that they use,
 * and have a more complicated API that requires a instantiating two
 * classes and connecting them. 
 * <p>
 * This class is thread safe.
 *
 * @see CircularByteBuffer
 */
public class CircularCharBuffer {
    
    /** 
     * The default size for a circular character buffer.
     */
    private final static int DEFAULT_SIZE = 1024;

    /**
     * The circular buffer.
     * <p>
     * The actual capacity of the buffer is one less than the actual length
     * of the buffer so that an empty and a full buffer can be
     * distinguished.  An empty buffer will have the markPostion and the
     * writePosition equal to each other.  A full buffer will have
     * the writePosition one less than the markPostion.
     * <p>
     * There are three important indexes into the buffer:
     * The readPosition, the writePosition, and the markPosition.
     * If the Reader has never been marked, the readPosition and
     * the markPosition should always be the same.  The characters
     * available to be read go from the readPosition to the writePosition,
     * wrapping around the end of the buffer.  The space available for writing
     * goes from the write position to one less than the markPosition,
     * wrapping around the end of the buffer.  The characters that have
     * been saved to support a reset() of the Reader go from markPosition
     * to readPosition, wrapping around the end of the buffer.
     */
    protected char[] buffer;
    /**
     * Index of the first character available to be read.
     */
    protected volatile int readPosition = 0;
    /**
     * Index of the first character available to be written.
     */
    protected volatile int writePosition = 0;
    /**
     * Index of the first saved character. (To support stream marking.)
     */
    protected volatile int markPosition = 0;
    /**
     * Number of characters that have to be saved
     * to support mark() and reset() on the Reader.
     */
    protected volatile int markSize = 0;
    /**
     * True if a write to a full buffer should block until the buffer
     * has room, false if the write method should throw an IOException
     */
    protected boolean blockingWrite = true;
    /**
     * The Reader that can empty this buffer.
     */
    protected Reader reader = new CircularCharBufferReader();
    /**
     * true if the close() method has been called on the Reader
     */
    protected boolean readerClosed = false;
    /**
     * The Writer that can fill this buffer.
     */
    protected Writer writer = new CircularCharBufferWriter();
    /**
     * true if the close() method has been called on the writer
     */
    protected boolean writerClosed = false;

    /**
     * Retrieve a Writer that can be used to fill
     * this buffer.
     * <p>
     * Write methods may throw a BufferOverflowException if
     * the buffer is not large enough.  A large enough buffer
     * size must be chosen so that this does not happen or
     * the caller must be prepared to catch the exception and
     * try again once part of the buffer has been consumed.
     *
     *
     * @return the producer for this buffer.
     */
    public Writer getWriter(){
        return writer;
    }

    /**
     * Retrieve a Reader that can be used to empty
     * this buffer.
     * <p>
     * This Reader supports marks at the expense
     * of the buffer size.
     *
     * @return the consumer for this buffer.
     */
    public Reader getReader(){
        return reader;
    }

    /**
     * Get number of characters that are available to be read.
     * <p>
     * Note that the number of characters available plus
     * the number of characters free may not add up to the
     * capacity of this buffer, as the buffer may reserve some
     * space for other purposes.
     *
     * @return the size in characters of this buffer
     */
    public int getAvailable(){
        synchronized (this){
            return available();
        }
    }

    /**
     * Get the number of characters this buffer has free for
     * writing.
     * <p>
     * Note that the number of characters available plus
     * the number of characters free may not add up to the
     * capacity of this buffer, as the buffer may reserve some
     * space for other purposes.
     *
     * @return the available space in characters of this buffer
     */
    public int getSpaceLeft(){
        synchronized (this){
            return spaceLeft();
        }
    }

    /**
     * Get the capacity of this buffer.
     * <p>
     * Note that the number of characters available plus
     * the number of characters free may not add up to the
     * capacity of this buffer, as the buffer may reserve some
     * space for other purposes.
     *
     * @return the size in characters of this buffer
     */
    public int getSize(){
        return buffer.length;
    }
 
    String toDebugString(){
        StringBuffer sb = new StringBuffer(buffer.length);
        for (int i=0; i<buffer.length; i++){
            if (i==writePosition){
                sb.append('$');
            }
            if (i==readPosition){
                sb.append('#');
            }
            if (i==markPosition){
                sb.append('!');
            }
            if (buffer[i] >= '%' && buffer[i] <= '~'){
                sb.append(buffer[i]);
            } else {
                sb.append(' ');
            }
        }
        sb.append('"');
        return sb.toString();
    }
    
    /**
     * Space available in the buffer which can be written.
     */
    private int spaceLeft(){
        if (writePosition < markPosition){
            // any space between the first write and
            // the mark except one character is available.
            // In this case it is all in one piece.
            return (markPosition - writePosition - 1);
        } else {
            // space at the beginning and end.
            return ((buffer.length - 1) - (writePosition - markPosition));
        }
    }

    /**
     * Characters available for reading.
     */
    private int available(){
        if (readPosition <= writePosition){
            // any space between the first read and
            // the first write is available.  In this case it
            // is all in one piece.
            return (writePosition - readPosition);
        } else {
            // space at the beginning and end.
            return (buffer.length - (readPosition - writePosition));
        }
    }

    /**
     * Characters saved for supporting marks.
     */
    private int marked(){
        if (markPosition <= readPosition){
            // any space between the markPosition and
            // the first write is marked.  In this case it
            // is all in one piece.
            return (readPosition - markPosition);
        } else {
            // space at the beginning and end.
            return (buffer.length - (markPosition - readPosition));
        }
    }

    /**
     * If we have passed the markSize reset the
     * mark so that the space can be used.
     */
    private void ensureMark(){
        if (marked() >= markSize){
            markPosition = readPosition;
            markSize = 0;
        }
    }
    
    /**
     * Create a new buffer with a default capacity.
     * Writing to a full buffer will block until space
     * is available rather than throw an exception.
     */
    public CircularCharBuffer(){
        this (DEFAULT_SIZE, true);
    }

    /**
     * Create a new buffer with given capacity.
     * Writing to a full buffer will block until space
     * is available rather than throw an exception.
     * <p>
     * Note that the buffer may reserve some characters for
     * special purposes and capacity number of characters may
     * not be able to be written to the buffer.
     *
     * @param size desired capacity of the buffer in characters.
     */
    public CircularCharBuffer(int size){
        this (size, true);
    }
    
    /**
     * Create a new buffer with a default capacity and
     * given blocking behavior.
     * 
     * @param blockingWrite true writing to a full buffer should block
     *        until space is available, false if an exception should
     *        be thrown instead.
     */
    public CircularCharBuffer(boolean blockingWrite){
        this (DEFAULT_SIZE, blockingWrite);
    }
    
    /**
     * Create a new buffer with the given capacity and
     * blocking behavior.
     * <p>
     * Note that the buffer may reserve some characters for
     * special purposes and capacity number of characters may
     * not be able to be written to the buffer.
     *
     * @param size desired capacity of the buffer in characters.
     * @param blockingWrite true writing to a full buffer should block
     *        until space is available, false if an exception should
     *        be thrown instead.
     */
    public CircularCharBuffer(int size, boolean blockingWrite){
        buffer = new char[size];
        this.blockingWrite = blockingWrite;
    }

    /**
     * Class for reading from a circular character buffer.
     */
    protected class CircularCharBufferReader extends Reader {

        /**
         * Close the stream. Once a stream has been closed, further read(), ready(),
         * mark(), or reset() invocations will throw an IOException. Closing a
         * previously-closed stream, however, has no effect.
         *
         * @throws IOException never.
         */
        public void close() throws IOException {
            synchronized (CircularCharBuffer.this){
                readerClosed = true;
            }
        }

        /**
         * Mark the present position in the stream. Subsequent calls to reset() will
         * attempt to reposition the stream to this point.
         * <p>
         * The readAheadLimit must be less than the size of circular buffer.
         *
         * @param readAheadLimit Limit on the number of characters that may be read while
         *    still preserving the mark. After reading this many characters, attempting to
         *    reset the stream will fail.
         * @throws IOException if the stream is closed, or the buffer size is greater
         * than or equal to the readAheadLimit.
         */
        public void mark(int readAheadLimit) throws IOException {
            synchronized (CircularCharBuffer.this){
                if (readerClosed) throw new IOException("Reader has been closed; cannot mark a closed Reader.");
                if (buffer.length - 1 <= readAheadLimit) throw new IOException("Cannot mark stream, readAheadLimit bigger than buffer size.");
                markSize = readAheadLimit;
                markPosition = readPosition;
            }
        }

        /**
         * Tell whether this stream supports the mark() operation.
         *
         * @return true, mark is supported.
         */
        public boolean markSupported() {
            return true;
        }

        /**
         * Read a single character.
         * This method will block until a character is available, an I/O error occurs,
         * or the end of the stream is reached.
         *
         * @return The character read, as an integer in the range 0 to 65535 (0x00-0xffff),
         *     or -1 if the end of the stream has been reached
         * @throws IOException if the stream is closed.
         */
        public int read() throws IOException {
            while (true){
                synchronized (CircularCharBuffer.this){
                    if (readerClosed) throw new IOException("Reader has been closed; cannot read from a closed Reader.");
                    int available = available();
                    if (available > 0){
                        int result = buffer[readPosition] & 0xffff;
                        readPosition++;
                        if (readPosition == buffer.length){
                            readPosition = 0;
                        }
                        ensureMark();
                        return result;
                    } else if (writerClosed){
                        return -1;
                    }
                }
                try {
                    Thread.sleep(100);
                } catch(Exception x){
                    throw new IOException("Blocking read operation interrupted.");
                }
            }
        }

        /**
         * Read characters into an array.
         * This method will block until some input is available,
         * an I/O error occurs, or the end of the stream is reached.
         *
         * @param cbuf Destination buffer.
         * @return The number of characters read, or -1 if the end of
         *   the stream has been reached
         * @throws IOException if the stream is closed.
         */
        public int read(char[] cbuf) throws IOException {
            return read(cbuf, 0, cbuf.length);
        }

        /**
         * Read characters into a portion of an array.
         * This method will block until some input is available,
         * an I/O error occurs, or the end of the stream is reached.
         *
         * @param cbuf Destination buffer.
         * @param off Offset at which to start storing characters.
         * @param len Maximum number of characters to read.
         * @return The number of characters read, or -1 if the end of
         *   the stream has been reached
         * @throws IOException if the stream is closed.
         */
        public int read(char[] cbuf, int off, int len) throws IOException {
            while (true){
                synchronized (CircularCharBuffer.this){
                    if (readerClosed) throw new IOException("Reader has been closed; cannot read from a closed Reader.");
                    int available = available();
                    if (available > 0){
                        int length = Math.min(len, available);
                        int firstLen = Math.min(length, buffer.length - readPosition);
                        int secondLen = length - firstLen;
                        System.arraycopy(buffer, readPosition, cbuf, off, firstLen);
                        if (secondLen > 0){
                            System.arraycopy(buffer, 0, cbuf, off+firstLen,  secondLen);
                            readPosition = secondLen;
                        } else {
                            readPosition += length;
                        }
                        if (readPosition == buffer.length) {
                            readPosition = 0;
                        }
                        ensureMark();
                        return length;
                    } else if (writerClosed){
                        return -1;
                    }
                }
                try { 
                    Thread.sleep(100);
                } catch(Exception x){
                    throw new IOException("Blocking read operation interrupted.");
                }
            } 
        }        
        
        /**
         * Tell whether this stream is ready to be read. 
         *
         * @return True if the next read() is guaranteed not to block for input, 
         *    false otherwise. Note that returning false does not guarantee that 
         *    the next read will block.
         * @throws IOException if the stream is closed.
         */
        public boolean ready() throws IOException {
            synchronized (CircularCharBuffer.this){
                if (readerClosed) throw new IOException("Reader has been closed, it is not ready.");
                return (available() > 0);
            }
        }

        /**
         * Reset the stream.
         * If the stream has been marked, then attempt to reposition it
         * at the mark. If the stream has not been marked, or more characters
         * than the readAheadLimit have been read, this method has no effect.
         *
         * @throws IOException if the stream is closed.
         */
        public void reset() throws IOException {
            synchronized (CircularCharBuffer.this){
                if (readerClosed) throw new IOException("Reader has been closed; cannot reset a closed Reader.");
                readPosition = markPosition;
            }
        }

        /**
         * Skip characters.
         * This method will block until some characters are available,
         * an I/O error occurs, or the end of the stream is reached.
         *
         * @param n The number of characters to skip
         * @return The number of characters actually skipped
         * @throws IllegalArgumentException if n is negative.
         * @throws IOException if the stream is closed.
         */
        public long skip(long n) throws IOException, IllegalArgumentException {
            while (true){
                synchronized (CircularCharBuffer.this){
                    if (readerClosed) throw new IOException("Reader has been closed; cannot skip characters on a closed Reader.");
                    int available = available();
                    if (available > 0){
                        int length = Math.min((int)n, available);
                        int firstLen = Math.min(length, buffer.length - readPosition);
                        int secondLen = length - firstLen;
                        if (secondLen > 0){
                            readPosition = secondLen;
                        } else {
                            readPosition += length;
                        }
                        if (readPosition == buffer.length) {
                            readPosition = 0;
                        }
                        return length;
                    } else if (writerClosed){
                        return 0;
                    }
                }
                try {
                    Thread.sleep(100);
                } catch(Exception x){
                    throw new IOException("Blocking read operation interrupted.");
                }
            }
        }
    }

    /**
     * Class for writing to a circular character buffer.
     * If the buffer is full, the writes will either block
     * until there is some space available or throw an IOException
     * based on the CircularCharBuffer's preference.
     */
    protected class CircularCharBufferWriter extends Writer {

        /**
         * Close the stream, flushing it first.
         * This will cause the reader associated with this circular buffer
         * to read its last characters once it empties the buffer.
         * Once a stream has been closed, further write() or flush() invocations
         * will cause an IOException to be thrown. Closing a previously-closed stream,
         * however, has no effect.
         *
         * @throws IOException never.
         */
        public void close() throws IOException {
            synchronized (CircularCharBuffer.this){
                if (!writerClosed){
                    flush();
                }
                writerClosed = true;
            }
        }

        /**
         * Flush the stream.
         *
         * @throws IOException if the stream is closed.
         */
        public void flush() throws IOException {
            if (writerClosed) throw new IOException("Writer has been closed; cannot flush a closed Writer.");
            if (readerClosed) throw new IOException("Buffer closed by Reader; cannot flush.");
            // this method needs to do nothing
        }

        /**
         * Write an array of characters.
         * If the buffer allows blocking writes, this method will block until
         * all the data has been written rather than throw an IOException.
         *
         * @param cbuf Array of characters to be written
         * @throws BufferOverflowException if buffer does not allow blocking writes
         *   and the buffer is full.  If the exception is thrown, no data
         *   will have been written since the buffer was set to be non-blocking.
         * @throws IOException if the stream is closed, or the write is interrupted.
         */
        public void write(char[] cbuf) throws IOException {
            write(cbuf, 0, cbuf.length);
        }

        /**
         * Write a portion of an array of characters.
         * If the buffer allows blocking writes, this method will block until
         * all the data has been written rather than throw an IOException.
         *
         * @param cbuf Array of characters
         * @param off Offset from which to start writing characters
         * @param len - Number of characters to write
         * @throws BufferOverflowException if buffer does not allow blocking writes
         *   and the buffer is full.  If the exception is thrown, no data
         *   will have been written since the buffer was set to be non-blocking.
         * @throws IOException if the stream is closed, or the write is interrupted.
         */
        public void write(char[] cbuf, int off, int len) throws IOException {
            while (len > 0){
                synchronized (CircularCharBuffer.this){
                    if (writerClosed) throw new IOException("Writer has been closed; cannot write to a closed Writer.");
                    if (readerClosed) throw new IOException("Buffer closed by Reader; cannot write to a closed buffer.");
                    int spaceLeft = spaceLeft();
                    if (!blockingWrite && spaceLeft < len) throw new BufferOverflowException("CircularCharBuffer is full; cannot write " + len + " characters");
                    int realLen = Math.min(len, spaceLeft);
                    int firstLen = Math.min(realLen, buffer.length - writePosition);
                    int secondLen = Math.min(realLen - firstLen, buffer.length - markPosition - 1);
                    int written = firstLen + secondLen;
                    if (firstLen > 0){
                        System.arraycopy(cbuf, off, buffer, writePosition, firstLen);
                    }
                    if (secondLen > 0){
                        System.arraycopy(cbuf, off+firstLen, buffer, 0, secondLen);
                        writePosition = secondLen;
                    } else {
                        writePosition += written;
                    }
                    if (writePosition == buffer.length) {
                        writePosition = 0;
                    }
                    off += written;
                    len -= written;
                }
                if (len > 0){
                    try {   
                        Thread.sleep(100);
                    } catch(Exception x){
                        throw new IOException("Waiting for available space in buffer interrupted.");
                    }
                }
            }
        }

        /**
         * Write a single character.
         * The character to be written is contained in the 16 low-order bits of the
         * given integer value; the 16 high-order bits are ignored.
         * If the buffer allows blocking writes, this method will block until
         * all the data has been written rather than throw an IOException.
         *
         * @param c int specifying a character to be written.
         * @throws BufferOverflowException if buffer does not allow blocking writes
         *   and the buffer is full.
         * @throws IOException if the stream is closed, or the write is interrupted.
         */
        public void write(int c) throws IOException {
            boolean written = false;
            while (!written){
                synchronized (CircularCharBuffer.this){
                    if (writerClosed) throw new IOException("Writer has been closed; cannot write to a closed Writer.");
                    if (readerClosed) throw new IOException("Buffer closed by Reader; cannot write to a closed buffer.");
                    int spaceLeft = spaceLeft();
                    if (!blockingWrite && spaceLeft < 1) throw new BufferOverflowException("CircularCharBuffer is full; cannot write 1 character");
                    if (spaceLeft > 0){
                        buffer[writePosition] = (char)(c & 0xffff);
                        writePosition++;
                        if (writePosition == buffer.length) {
                            writePosition = 0;
                        }
                        written = true;
                    }
                }
                if (!written){ 
                    try { 
                        Thread.sleep(100);
                    } catch(Exception x){
                        throw new IOException("Waiting for available space in buffer interrupted.");
                    }
                }
            }
        }

        /**
         * Write a string.
         * If the buffer allows blocking writes, this method will block until
         * all the data has been written rather than throw an IOException.
         *
         * @param str String to be written
         * @throws BufferOverflowException if buffer does not allow blocking writes
         *   and the buffer is full.  If the exception is thrown, no data
         *   will have been written since the buffer was set to be non-blocking.
         * @throws IOException if the stream is closed, or the write is interrupted.
         */
        public void write(String str) throws IOException {
            write(str, 0, str.length());
        }

        /**
         * Write a portion of a string.
         * If the buffer allows blocking writes, this method will block until
         * all the data has been written rather than throw an IOException.
         *
         * @param str A String
         * @param off Offset from which to start writing characters
         * @param len Number of characters to write
         * @throws BufferOverflowException if buffer does not allow blocking writes
         *   and the buffer is full.  If the exception is thrown, no data
         *   will have been written since the buffer was set to be non-blocking.
         * @throws IOException if the stream is closed, or the write is interrupted.
         */    
        public void write(String str, int off, int len) throws IOException {        
            while (len > 0){
                synchronized (CircularCharBuffer.this){
                    if (writerClosed) throw new IOException("Writer has been closed; cannot write to a closed Writer.");
                    if (readerClosed) throw new IOException("Buffer closed by Reader; cannot write to a closed buffer.");
                    int spaceLeft = spaceLeft();
                    if (!blockingWrite && spaceLeft < len) throw new BufferOverflowException("CircularCharBuffer is full; cannot write " + len + " characters");
                    int realLen = Math.min(len, spaceLeft);
                    int firstLen = Math.min(realLen, buffer.length - writePosition);
                    int secondLen = Math.min(realLen - firstLen, buffer.length - markPosition - 1);
                    int written = firstLen + secondLen;
                    for (int i=0; i<firstLen; i++){
                        buffer[writePosition + i] = str.charAt(off+i);
                    }
                    if (secondLen > 0){
                        for (int i=0; i<secondLen; i++){
                            buffer[i] = str.charAt(off+firstLen+i);
                        }
                        writePosition = secondLen;
                    } else {
                        writePosition += written;
                    }
                    if (writePosition == buffer.length) {
                        writePosition = 0;
                    }
                    off += written;
                    len -= written;
                }
                if (len > 0){
                    try { 
                        Thread.sleep(100);
                    } catch(Exception x){
                        throw new IOException("Waiting for available space in buffer interrupted.");
                    }
                }
            }
        } 
    }
}
