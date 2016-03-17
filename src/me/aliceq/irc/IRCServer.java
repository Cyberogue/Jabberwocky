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

import me.aliceq.irc.internal.IRCSocket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * Wrapper for a IRCSocket instance acting as a central node for its children
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public class IRCServer {

    private final IRCSocket socket;
    private PrintWriter outstream;
    private BufferedReader instream;
    private boolean verbose = false;

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
     * Enables printing of messages and exceptions to System.out
     */
    public void setVerbose() {
        this.verbose = true;
    }

    /**
     * Disables printing of messages and exceptions to System.out
     */
    public void setQuiet() {
        this.verbose = false;
    }

    /**
     * Returns true if a connection is established
     *
     * @return true if a connection is established
     */
    public boolean isConnected() {
        return socket.isConnected();
    }

    /**
     * Returns true if the server connection is set up and ready for usage
     *
     * @return in/out are initialized and a connection exists
     */
    public boolean isReady() {
        return socket.isConnected() && outstream != null && instream != null;
    }

    /**
     * Starts the server
     */
    public void start() {
        if (!socket.isConnected()) {
            throw new IRCException("Server is not connected");
        }

        // Create output writer
        try {
            outstream = new PrintWriter(this.socket.getOutputStream(), true);
            instream = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        } catch (IOException e) {
            outstream = null;
            instream = null;
            if (verbose) {
                System.out.println(e);
            }
        }

        final BufferedReader in = instream;

        // Create a new thread to read messages
        Thread thread = new Thread(new Runnable() {

            // TODO: Move all this to a dedicated ServerReader class
            @Override
            public void run() {
                while (true) {
                    try {
                        for (String line = in.readLine(); line != null; line = in.readLine()) {
                            if (verbose) {
                                System.out.println("[<] " + line);
                            }
                            // PONG message
                            if (line.startsWith("PING")) {
                                send("PONG " + line.substring(5, line.length()));
                            }
                        }

                        Thread.sleep(100);
                    } catch (IOException | InterruptedException e) {

                    }
                }
            }
        });
        thread.start();
    }

    /**
     * Sends the appropriate messages to identify. If the connection is not
     * ready this does nothing.
     *
     * @param identity the identity to identify with
     */
    public void identify(IRCIdentity identity) {
        if (!isReady()) {
            return;
        }

        if (identity.password() != null) {
            write("PASS " + identity.password());
        }

        write("NICK " + identity.nickname());
        write("USER " + identity.username()
                + " " + identity.getMode()
                + " * :" + identity.realname());
        flush();
    }

    /**
     * Sends and flushes a single message
     *
     * @param message message to send
     */
    protected void send(String message) {
        if (verbose) {
            System.out.println("[>] " + message);
        }

        outstream.write(message + "\r\n");
        outstream.flush();
    }

    /**
     * Sends and flushes a group of messages
     *
     * @param messages messages to send
     */
    protected void send(String[] messages) {
        for (String message : messages) {
            if (verbose) {
                System.out.println("[>] " + message);
            }
            outstream.write(message + "\r\n");
        }
        outstream.flush();
    }

    /**
     * Writes a message for sending. Note that this doesn't flush the stream.
     *
     * @param message message to write
     */
    protected void write(String message) {
        if (verbose) {
            System.out.println("[~] " + message);
        }
        outstream.write(message + "\r\n");
    }

    /**
     * FLushes the output stream
     */
    protected void flush() {
        if (verbose) {
            System.out.println("[^] ");
        }
        outstream.flush();
    }
}
