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
package me.aliceq.irc.internal;

import me.aliceq.irc.IRCMessage;
import me.aliceq.irc.IRCMessageListener;
import me.aliceq.irc.IRCServer;

/**
 * A request placed by a subroutine for a message
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public class IRCMessageRequest {

    private final IRCServer endpoint;
    private final IRCMessageListener exchange;
    private IRCMessage callback = null;

    /**
     * Constructor
     *
     * @param server the server endpoint
     * @param exchange the listener monitoring the exchange
     */
    public IRCMessageRequest(IRCServer server, IRCMessageListener exchange) {
        this.endpoint = server;
        this.exchange = exchange;
    }

    /**
     * Pushes the request into the server, blocking the calling thread until it
     * is woken. This will not wake up until it is woken by a separate thread.
     *
     * @return the obtained IRCMessage
     * @throws InterruptedException when the wait times out before it can find a
     * message
     */
    public synchronized IRCMessage push() throws InterruptedException {
        // Push request into the server
        endpoint.addRequest(this);

        // Timeout
        try {
            this.wait();
        } catch (InterruptedException e) {
            if (callback == null) {
                throw e;
            }
        }

        // Remove request from server
        endpoint.removeRequest(this);

        // Callback should be set by now
        return callback;
    }

    /**
     * Pushes the request into the server, blocking the calling thread until it
     * is woken or the wait times out
     *
     * @param timeout the amount of time to wait
     * @return the obtained IRCMessage
     * @throws InterruptedException when the wait times out before it can find a
     * message
     */
    public synchronized IRCMessage push(long timeout) throws InterruptedException {
        // Push request into the server
        endpoint.addRequest(this);

        // Timeout
        try {
            this.wait(timeout);
        } catch (InterruptedException e) {
            if (callback == null) {

                // Remove request from server
                endpoint.removeRequest(this);
                throw e;
            }
        }

        // Remove request from server
        endpoint.removeRequest(this);

        // Callback should be set by now
        return callback;
    }

    /**
     * Validates an IRC message with the listener. If valid, it wakes up the
     * thread after setting the callback.
     *
     * @param message Message to check
     * @return true if the message matched the listener
     */
    public synchronized boolean validate(IRCMessage message) {
        if (exchange.check(message)) {
            callback = message;
            this.notify();
            return true;
        }
        return false;
    }
}
