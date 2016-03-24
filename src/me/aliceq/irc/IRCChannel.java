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

import java.util.ArrayList;
import java.util.Collection;

/**
 * Channel construct which contains information about a specific channel
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public class IRCChannel {

    final public IRCServer server;

    final private String name;
    final private ArrayList<String> users = new ArrayList();

    private String topic = "";
    private int status = 0;

    public IRCChannel(String name, IRCServer server) {
        this.name = name;
        this.server = server;
    }

    /**
     * Returns an array of the nicks currently in the channel
     *
     * @return an array of the nicks currently in the channel
     */
    public Collection<String> getUsers() {
        return users;
    }

    /**
     * Adds an user to the list of getUsers
     *
     * @param nick
     */
    public void addUser(String nick) {
        users.add(nick);
    }

    /**
     * Removes an user from the list of getUsers
     *
     * @param nick
     */
    public void removeUser(String nick) {
        // Has to be manually done in case of @ or +
        for (int i = 0; i < users.size(); i++) {
            String user = users.get(i);
            if (user.charAt(0) == '@' || user.charAt(0) == '+') {
                user = user.substring(1);
            }
            if (user.equals(nick)) {
                users.remove(i);
                return;
            }
        }
    }

    /**
     * Removes an user and adds a new user in their place
     *
     * @param remove
     * @param add
     */
    public void replaceUser(String remove, String add) {
        // Has to be manually done in case of @ or +
        for (int i = 0; i < users.size(); i++) {
            String user = users.get(i);
            if (user.charAt(0) == '@' || user.charAt(0) == '+') {
                user = user.substring(1);
            }
            if (user.equals(remove)) {
                users.remove(i);
                users.add(i, add);
                return;
            }
        }
    }

    /**
     * Returns the getName of the channel
     *
     * @return the getName of the channel
     */
    public String getName() {
        return name;
    }

    /**
     * Checks if there was an error with the channel (status is between 400 and
     * 599)
     *
     * @return
     */
    public boolean hasError() {
        return status >= 400 && status <= 599;
    }

    /**
     * Checks if you're banned from the channel (status 474)
     *
     * @return
     */
    public boolean isBanned() {
        return status == IRCCode.ERR_BANNEDFROMCHAN || status == IRCCode.ERR_YOUREBANNEDCREEP || status == IRCCode.ERR_YOUWILLBEBANNED;
    }

    /**
     * Returns the channel status
     *
     * @return
     */
    public int getStatus() {
        return status;
    }

    /**
     * Sets the channel status
     *
     * @param mode
     */
    public void setStatus(int mode) {
        status = mode;
    }

    /**
     * Sets the channel topic
     *
     * @param topic the topic
     */
    public void setTopic(String topic) {
        this.topic = topic;
    }

    /**
     * Returns the channel topic
     *
     * @return the channel topic
     */
    public String getTopic() {
        return topic;
    }

    /**
     * Sends a message to the channel. This is equivalent to IRCServer.send with
     * the target being the channel name.
     *
     * @param message the message to send
     */
    public void message(String message) {
        server.message(name, message);
    }

    @Override
    public String toString() {
        return "[" + name + " (" + users.size() + ")]";
    }
}
