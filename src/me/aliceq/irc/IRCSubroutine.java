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
 * A custom subroutine monitored by a server which runs on its own thread. This
 * allows it to implement blocking methods for message retrieval. Overwrite the
 * run method for custom implementations.
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public abstract class IRCSubroutine {

    final private IRCServer server;

    /**
     * Default constructor not allowed
     *
     * @deprecated
     */
    @Deprecated
    protected IRCSubroutine() {
        throw new UnsupportedOperationException("Default constructor not allowed");
    }

    /**
     * Constructor which ties the subroutine to a server
     *
     * @param server
     */
    protected IRCSubroutine(IRCServer server) {
        this.server = server;
    }

    public IRCServer server() {
        return this.server;
    }

    public final synchronized String getNext() {
        return "";
    }

    public final synchronized String getNext(String source) {
        return "";
    }

    public final void send(String message) {
        server.send(message);
    }

    public final void send(String[] messages) {
        server.send(messages);
    }

    public abstract void run();
}
