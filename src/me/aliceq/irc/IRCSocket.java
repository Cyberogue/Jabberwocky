/*
 * The MIT License
 *
 * Copyright 2016 Alice Quiros <email@aliceq.me>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package me.aliceq.irc;

import java.io.IOException;
import java.net.Socket;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

/**
 * Main IRC server socket
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public final class IRCSocket {

    public static final int DEFAULT_PORT = 6667;
    public static final int DEFAULT_SSL_PORT = 6697;
    public static final int DEFAULT_TIMEOUT = 5000;

    private Socket socket;

       /**
     * Basic constructor for an unprotected socket on the default port
     * @param address the address to connect to
     * @throws IRCException 
     */
    public IRCSocket(String address) throws IRCException {
        this(address, DEFAULT_PORT, false);
    }
   /**
     * Constructor which connects to either the default port or the default SSL port
     * @param address the address to connect to
     * @param secure if true, an SSL connection is attempted
     * @throws IRCException 
     */
    public IRCSocket(String address, boolean secure) throws IRCException {
        this(address, secure ? DEFAULT_SSL_PORT : DEFAULT_PORT, false);
    }

    /**
     * Constructor for an unprotected socket
     * @param address the address to connect to
     * @param port the port to connect with
     * @throws IRCException 
     */
    public IRCSocket(String address, int port) throws IRCException {
        this(address, port, false);
    }

    /**
     * Full constructor
     * @param address the address to connect to
     * @param port the port to connect with
     * @param secure if true, an SSL connection is attempted
     * @throws IRCException 
     */
    public IRCSocket(String address, int port, boolean secure) throws IRCException {
        try {
            if (secure) {
                socket = SSLSocketFactory.getDefault().createSocket(address, port);
            } else {
                socket = SocketFactory.getDefault().createSocket(address, port);
            }
        } catch (IOException e) {
            throw new IRCException(e);
        }
    }

    /**
     * Returns true if a connection is established
     * @return true if a connection is established
     */
    public boolean isConnected(){
        return socket.isConnected();
    }
}
