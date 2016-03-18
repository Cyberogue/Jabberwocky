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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.aliceq.irc.internal.IRCMessageRequest;
import me.aliceq.irc.subroutines.ChannelMonitoringSubroutine;
import me.aliceq.irc.subroutines.ConnectionSubroutine;

/**
 * Wrapper for a IRCSocket instance acting as a central node for its children.
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public final class IRCServer {

    private final IRCSocket socket;
    private PrintWriter outstream;
    private BufferedReader instream;

    private boolean verbose = false;

    private final List<IRCMessageRequest> requests = new ArrayList(3);

    private int activeThreadCount = 0;

    private final IRCServerDetails details = new IRCServerDetails();

    private final Map<String, IRCChannel> channels = new HashMap();

    /**
     * Basic constructor
     *
     * @param address the address to connect to
     * @param port the port to connect through
     */
    public IRCServer(String address, int port) {
        this(new IRCSocket(address, port));
    }

    /**
     * Constructor for an SSL socket
     *
     * @param address the address to connect to
     * @param port the port to connect through
     * @param secure if true an SSL connection is attempted
     */
    public IRCServer(String address, int port, boolean secure) {
        this(new IRCSocket(address, port, secure));
    }

    /**
     * Custom constructor
     *
     * @param socket a custom socket instance to connect through
     */
    public IRCServer(IRCSocket socket) {
        this.socket = socket;

        this.details.socketAddress = socket.getLocalAddress();
        this.details.socketPort = socket.getLocalPort();
    }

    /**
     * Enables printing of messages and exceptions to System.out
     */
    public void setVerbose() {
        this.verbose = true;
    }

    /**
     * Returns whether or not the server is isVerbose
     *
     * @return whether or not the server is isVerbose
     */
    public boolean isVerbose() {
        return this.verbose;
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

        // Set getDetails
        details.socketConnected = true;

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
        final IRCServer server = this;

        // Increment thread count
        activeThreadCount++;

        // Create a new thread to read messages
        Thread thread = new Thread(new Runnable() {
            // TODO: Move all this to a dedicated ServerReader class
            @Override
            public void run() {
                while (true) {
                    try {
                        for (String line = in.readLine(); line != null; line = in.readLine()) {
                            // PONG message handling
                            if (line.startsWith("PING")) {
                                send("PONG " + line.substring(5, line.length()));
                            } else {
                                // Otherwise parse the message
                                server.validate(IRCMessage.parseFrom(line));
                            }
                        }
                    } catch (IOException ex) {
                        if (server.verbose) {
                            System.out.println(ex);
                        }
                    }
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Sends the appropriate messages to identify and runs the authentication
     * subroutine. If the connection is not ready this does nothing.
     *
     * @param identity the identity to identify with
     */
    public void identify(IRCIdentity identity) {
        if (!isReady()) {
            return;
        }

        // Write messages to send
        if (identity.password() != null) {
            write("PASS " + identity.password());
        }

        write("NICK " + identity.nickname());
        write("USER " + identity.username()
                + " " + identity.getMode()
                + " * :" + identity.realname());

        // Set identity
        details.identity = identity;
        details.currentNick = identity.nickname();

        // Initialize subroutines
        runSubroutine(new ConnectionSubroutine());
        runSubroutine(new ChannelMonitoringSubroutine());

        // Flush messages
        flush();
    }

    /**
     * Returns the current number of queued requests
     *
     * @return the current number of queued requests
     */
    public int activeRequests() {
        return requests.size();
    }

    /**
     * Sends and flushes a single message
     *
     * @param message message to send
     */
    public void send(String message) {
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
    public void send(String[] messages) {
        for (String message : messages) {
            if (verbose) {
                System.out.println("[>] " + message);
            }
            outstream.write(message + "\r\n");
        }
        outstream.flush();
    }

    /**
     * Sends a join message to the server and adds the required channels to a
     * list of channels
     *
     * @param channels the channels to join, comma-separated
     */
    public void join(String channels) {
        join(channels, "");
    }

    /**
     * Sends a join message to the server and adds the required channels to a
     * list of channels
     *
     * @param channels the channels to join, comma-separated
     * @param passwords the channel passwords, comma-separated
     */
    public void join(String channels, String passwords) {
        send("JOIN " + channels + " " + passwords);
    }

    /**
     * Sends a message to part from a channel. If the channel is not monitored,
     * this does nothing.
     *
     * @param channel the channel to leave
     * @param message parting message
     */
    public void part(String channel, String message) {
        part(channels.get(channel.toLowerCase()), message);
    }

    /**
     * Sends a message to part from a channel. If the channel is not monitored,
     * this does nothing.
     *
     * @param channel the channel to leave
     */
    public void part(String channel) {
        part(channels.get(channel.toLowerCase()), null);
    }

    /**
     * Sends a message to part from a channel.
     *
     * @param channel the channel to leave
     * @param message parting message
     */
    protected void part(IRCChannel channel, String message) {
        if (channel == null) {
            return;
        }

        channels.remove(channel.getName());
        send("PART " + channel.getName() + (message == null ? "" : " :" + message));
    }

    /**
     * Quits the server
     *
     * @param message
     */
    public void quit(String message) {
        channels.clear();
        details.connected = false;
        details.identified = false;
        send("QUIT " + (message == null ? "" : " :" + message));
    }

    /**
     * Quits the server
     *
     */
    public void quit() {
        quit(null);
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

    /**
     * Pushes a request into the stack
     *
     * @param request the message request to add
     */
    public synchronized void addRequest(IRCMessageRequest request) {
        requests.add(request);
    }

    /**
     * Removes a request from the stack
     *
     * @param request the message request to remove
     */
    public synchronized void removeRequest(IRCMessageRequest request) {
        requests.remove(request);
    }

    /**
     * Compares an incoming message to all of the current requests. If any
     * requests match they are cleared and unblocked.
     *
     * @param message message to validate
     */
    protected synchronized void validate(IRCMessage message) {
        if (verbose) {
            System.out.println(message + " [" + requests.size() + "]");
        }

        // Iterate through the requests using a for-loop to avoid concurrent modification
        for (int i = 0; i < requests.size(); i++) {
            if (requests.get(i).validate(message)) {
                requests.remove(i);
            }
        }
    }

    /**
     * Places a low-priority subroutine on its own thread and runs it,
     * monitoring it
     *
     * @param subroutine the subroutine to run
     * @throws UnsupportedOperationException if this is called before starting
     * the server
     */
    public void runSubroutine(IRCSubroutine subroutine) {
        runSubroutine(subroutine, Thread.MIN_PRIORITY);
    }

    /**
     * Places a subroutine on its own thread and runs it, monitoring it
     *
     * @param subroutine the subroutine to run
     * @param priority the Thread priority to give the subroutine
     * @throws UnsupportedOperationException if this is called before starting
     * the server
     */
    public void runSubroutine(IRCSubroutine subroutine, int priority) {
        final IRCSubroutine sub = subroutine;
        sub.server = this;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                activeThreadCount++;
                sub.run();
                activeThreadCount--;
            }
        });

        thread.setPriority(priority);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Returns the number of active threads managed by the system
     *
     * @return the number of active threads managed by the system
     */
    public int activeThreads() {
        return activeThreadCount;
    }

    /**
     * Returns a class containing data about the server connection
     *
     * @return a class containing data about the server connection
     */
    public IRCServerDetails getDetails() {
        return details;
    }

    /**
     * Returns the IRCChannel entity of the specified name. If the channel does
     * not exist, a new instance is made for it.
     *
     * @param name The name of the channel
     * @return an IRCChannel entity or null
     */
    public IRCChannel getChannel(String name) {
        String key = name.toLowerCase();
        IRCChannel instance = channels.get(key);
        if (instance == null) {
            instance = new IRCChannel(name);
            channels.put(key, instance);
        }

        return instance;
    }

    /**
     * Returns a collection of all IRCChannel entries
     *
     * @return a collection of IRCChannels
     */
    public Collection<IRCChannel> getChannels() {
        return channels.values();
    }

    /**
     * Registers a new channel with the server, creating an IRCChannel instance
     * for it. If there already exists a channel under that name, this does
     * nothing.
     *
     * @param name the name of the channel
     * @return an iRCInstance corresponding to the channel
     */
    public IRCChannel registerChannel(String name) {
        IRCChannel current = channels.get(name.toLowerCase());
        if (current != null) {
            return current;
        }

        IRCChannel c = new IRCChannel(name);
        channels.put(name.toLowerCase(), c);
        return c;
    }

    /**
     * Removes an IRCChannel from the server, preventing it from continuing to
     * be monitored
     *
     * @param name the name of the channel
     */
    public void unregisterChannel(String name) {
        channels.remove(name);
    }

    @Override
    public String toString() {
        return "Server@" + socket.getLocalAddress() + ":" + socket.getRemotePort() + " <" + channels.size() + ">";
    }
}
