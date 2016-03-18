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

import me.aliceq.irc.internal.IRCMessageRequest;

/**
 * A custom subroutine monitored by a server which runs on its own thread. This
 * allows it to implement blocking methods for message retrieval. Overwrite the
 * run method for custom implementations.
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public abstract class IRCSubroutine {

    protected IRCServer server;

    /**
     * Default constructor not allowed
     *
     * @deprecated
     */
    @Deprecated
    protected IRCSubroutine() {

    }

    public IRCServer server() {
        return this.server;
    }

    // Send messages
    public final void send(String message) {
        server.send(message);
    }

    public final void send(String[] messages) {
        server.send(messages);
    }

    // Receive messages
    public final synchronized IRCMessage getMessage() {
        try {
            return getMessage(0);
        } catch (InterruptedException e) {
            return null;
        }
    }

    public final synchronized IRCMessage getMessage(String sender) {
        try {
            return getMessage(sender, 0);
        } catch (InterruptedException e) {
            return null;
        }
    }

    public final synchronized IRCMessage getMessage(IRCMessageListener listener) {
        try {
            return getMessage(listener, 0);
        } catch (InterruptedException e) {
            return null;
        }
    }

    public final synchronized IRCMessage getMessage(int timeout) throws InterruptedException {
        IRCMessageRequest request = new IRCMessageRequest(server, IRCMessageListener.ANY);
        return request.push(timeout);
    }

    public final synchronized IRCMessage getMessage(final String sender, int timeout) throws InterruptedException {
        final String s = sender;
        IRCMessageRequest request = new IRCMessageRequest(server, new IRCMessageListener() {
            @Override
            public boolean check(IRCMessage message) {
                return message.getSender().equals(s);
            }
        });
        return request.push(timeout);
    }

    public final synchronized IRCMessage getMessage(IRCMessageListener listener, int timeout) throws InterruptedException {
        IRCMessageRequest request = new IRCMessageRequest(server, listener);
        return request.push(timeout);
    }

    // Abstract
    public abstract void run();
}
