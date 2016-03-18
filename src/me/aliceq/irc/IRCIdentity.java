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
import java.util.List;

/**
 * Class containing information about a person's identity
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public class IRCIdentity {

    final private String password;
    final private String username;
    final private String realname;
    final private List<String> nicknames = new ArrayList(1);

    private boolean invisible = false;
    private boolean wallops = false;

    // Constructors
    public IRCIdentity(String username) {
        this(username, username, username, "");
    }

    public IRCIdentity(String username, String password) {
        this(username, username, username, password);
    }

    public IRCIdentity(String username, String nickname, String password) {
        this(username, nickname, username, password);
    }

    public IRCIdentity(String username, String nickname, String realname, String password) {
        this.nicknames.clear();
        this.username = username;
        this.nicknames.add(nickname);
        this.realname = realname;
        this.password = password;
    }

    public IRCIdentity(String username, Collection<String> nicknames, String password) {
        this(username, nicknames, username, password);
    }

    public IRCIdentity(String username, Collection<String> nicknames, String realname, String password) {
        this.nicknames.clear();
        this.username = username;
        this.nicknames.addAll(nicknames);
        this.realname = realname;
        this.password = password;
    }

    // Modifiers
    public void addNickname(String nick) {
        nicknames.add(nick);
    }

    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
    }

    public void setWallOps(boolean wallops) {
        this.wallops = wallops;
    }

    // Accessors
    public int nickCount() {
        return nicknames.size();
    }

    public String nickname() {
        return nicknames.get(0);
    }

    public String nickname(int index) {
        return nicknames.get(index);
    }

    public Collection<String> nicknames() {
        return nicknames;
    }

    public String username() {
        return username;
    }

    public String realname() {
        return realname;
    }

    public boolean invisible() {
        return invisible;
    }

    public boolean wallops() {
        return wallops;
    }

    protected String password() {
        return password;
    }

    // Extras
    public int getMode() {
        int mask = 0;
        if (invisible) {
            mask |= 8;
        }
        if (wallops) {
            mask |= 4;
        }
        return mask;
    }
}
