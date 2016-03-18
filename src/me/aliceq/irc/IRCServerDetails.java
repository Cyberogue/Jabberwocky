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
 * Data class containing details about an IRCServer. It is recommended to use
 * this as read-only.
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public class IRCServerDetails {

    /**
     * Identity used to connect to server
     */
    public IRCIdentity identity;

    /**
     * Server socket connection status
     */
    public boolean socketConnected;
    /**
     * Server socket connection port
     */
    public int socketPort;
    /**
     * Server socket connection address
     */
    public String socketAddress;

    /**
     * True when a connection to the IRC server is established
     */
    public boolean connected;
    /**
     * True when the current nickname is identified with NickServ
     */
    public boolean identified;
    /**
     * True if the current nickname is already registered with NickServ
     */
    public boolean registered;

    /**
     * True if the current nick was taken
     */
    public boolean nickIsTaken;

}
