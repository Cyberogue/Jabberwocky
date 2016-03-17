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
 * Class containing information about a person's identity
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public class IRCIdentity {

    final private String password;
    final private String nickname;
    final private String username;
    final private String realname;

    final private boolean invisible;
    final private boolean wallops;

    // Constructors
    public IRCIdentity(String nickname) {
        this(nickname, nickname, nickname, "", false, false);
    }

    public IRCIdentity(String nickname, String password) {
        this(nickname, nickname, nickname, password, false, false);
    }

    public IRCIdentity(String nickname, String username, String password) {
        this(nickname, username, username, password, false, false);
    }

    public IRCIdentity(String nickname, String username, String realname, String password) {
        this(nickname, username, realname, password, false, false);
    }

    public IRCIdentity(String nickname, String username, String realname, String password, boolean invisible) {
        this(nickname, username, realname, password, invisible, false);
    }

    public IRCIdentity(String nickname, String username, String realname, String password, boolean invisible, boolean wallops) {
        this.password = password;
        this.nickname = nickname;
        this.username = username;
        this.realname = realname;
        this.invisible = invisible;
        this.wallops = wallops;
    }

    // Gets
    public String nickname() {
        return nickname;
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
