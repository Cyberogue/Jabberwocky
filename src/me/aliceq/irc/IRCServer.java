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

/**
 * Wrapper for a IRCSocket instance acting as a central node for its children
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public class IRCServer {

    private final IRCSocket socket;

    /**
     * Basic constructor
     *
     * @param address the address to connect to
     * @param port the port to connect through
     */
    public IRCServer(String address, int port) {
        this.socket = new IRCSocket(address, port);
    }

    /**
     * Constructor for an SSL socket
     *
     * @param address the address to connect to
     * @param port the port to connect through
     * @param secure if true an SSL connection is attempted
     */
    public IRCServer(String address, int port, boolean secure) {
        this.socket = new IRCSocket(address, port, secure);
    }

    /**
     * Custom constructor
     *
     * @param socket a custom socket instance to connect through
     */
    public IRCServer(IRCSocket socket) {
        this.socket = socket;
    }

    /**
     * Returns true if a connection is established
     *
     * @return true if a connection is established
     */
    public boolean isConnected() {
        return socket.isConnected();
    }
    
    
}
