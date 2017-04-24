/*
 * Copyright (c) 2017 Lucko (Luck) <luck@lucko.me>
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package me.lucko.helper.scoreboard;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Packet wrapper for PacketPlayOutScoreboardTeam
 *
 * http://wiki.vg/Protocol#Teams
 */
public class PacketScoreboardTeam {
    private static final int MODE_CREATE = 0;
    private static final int MODE_REMOVE = 1;
    private static final int MODE_UPDATE = 2;
    private static final int MODE_ADD_PLAYERS = 3;
    private static final int MODE_REMOVE_PLAYERS = 4;

    private static final int MAX_PREFIX_LENGTH = 16;
    private static final int MAX_SUFFIX_LENGTH = MAX_PREFIX_LENGTH;

    private final Set<Player> players = Collections.synchronizedSet(new HashSet<>());
    private final Set<Player> subscribed = Collections.synchronizedSet(new HashSet<>());

    private final PacketScoreboard scoreboard;
    private final String id;
    private String displayName;
    private String prefix = "";
    private String suffix = "";
    private boolean allowFriendlyFire = true;
    private boolean canSeeFriendlyInvisibles = true;
    private NameTagVisibility nameTagVisibility = NameTagVisibility.ALWAYS;
    private CollisionRule collisionRule = CollisionRule.ALWAYS;

    public PacketScoreboardTeam(PacketScoreboard scoreboard, String id, String displayName) {
        Preconditions.checkNotNull(scoreboard, "scoreboard");
        Preconditions.checkNotNull(id, "id");
        Preconditions.checkNotNull(displayName, "displayName");
        this.scoreboard = scoreboard;
        this.id = id;
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        Preconditions.checkNotNull(displayName);
        if (this.displayName.equals(displayName)) {
            return;
        }

        this.displayName = displayName;
        scoreboard.broadcastPacket(subscribed, newUpdatePacket());
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        if (prefix.length() > MAX_PREFIX_LENGTH) {
            prefix = prefix.substring(0, MAX_PREFIX_LENGTH);
        }
        if (this.prefix.equals(prefix)) {
            return;
        }

        this.prefix = prefix;
        scoreboard.broadcastPacket(subscribed, newUpdatePacket());
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        if (suffix.length() > MAX_SUFFIX_LENGTH) {
            suffix = suffix.substring(0, MAX_SUFFIX_LENGTH);
        }
        if (this.suffix.equals(suffix)) {
            return;
        }

        this.suffix = suffix;
        scoreboard.broadcastPacket(subscribed, newUpdatePacket());
    }

    public boolean isAllowFriendlyFire() {
        return allowFriendlyFire;
    }

    public void setAllowFriendlyFire(boolean allowFriendlyFire) {
        if (this.allowFriendlyFire == allowFriendlyFire) {
            return;
        }

        this.allowFriendlyFire = allowFriendlyFire;
        scoreboard.broadcastPacket(subscribed, newUpdatePacket());
    }

    public boolean isCanSeeFriendlyInvisibles() {
        return canSeeFriendlyInvisibles;
    }

    public void setCanSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles) {
        if (this.canSeeFriendlyInvisibles == canSeeFriendlyInvisibles) {
            return;
        }

        this.canSeeFriendlyInvisibles = canSeeFriendlyInvisibles;
        scoreboard.broadcastPacket(subscribed, newUpdatePacket());
    }

    public NameTagVisibility getNameTagVisibility() {
        return nameTagVisibility;
    }

    public void setNameTagVisibility(NameTagVisibility nameTagVisibility) {
        Preconditions.checkNotNull(nameTagVisibility);
        if (this.nameTagVisibility.equals(nameTagVisibility)) {
            return;
        }

        this.nameTagVisibility = nameTagVisibility;
        scoreboard.broadcastPacket(subscribed, newUpdatePacket());
    }

    public CollisionRule getCollisionRule() {
        return collisionRule;
    }

    public void setCollisionRule(CollisionRule collisionRule) {
        Preconditions.checkNotNull(collisionRule);
        if (this.collisionRule.equals(collisionRule)) {
            return;
        }

        this.collisionRule = collisionRule;
        scoreboard.broadcastPacket(subscribed, newUpdatePacket());
    }

    public void addPlayer(Player player) {
        if (!players.add(player)) {
            return;
        }

        scoreboard.broadcastPacket(subscribed, newPlayerPacket(player, false));
    }

    public boolean removePlayer(Player player) {
        if (!players.remove(player)) {
            return false;
        }

        scoreboard.broadcastPacket(subscribed, newPlayerPacket(player, true));
        return true;
    }

    public boolean hasPlayer(Player player) {
        return players.contains(player);
    }

    public Set<Player> getPlayers() {
        return ImmutableSet.copyOf(players);
    }
    
    public void subscribe(Player player) {
        scoreboard.sendPacket(newCreatePacket(), player);
        subscribed.add(player);
    }

    public void unsubscribe(Player player) {
        unsubscribe(player, false);
    }

    public void unsubscribe(Player player, boolean fast) {
        if (!subscribed.remove(player) || fast) {
            return;
        }

        scoreboard.sendPacket(newRemovePacket(), player);
    }

    public void unsubscribeAll() {
        scoreboard.broadcastPacket(subscribed, newRemovePacket());
        subscribed.clear();
    }

    private PacketContainer newCreatePacket() {
        PacketContainer packet = newUpdatePacket();
        
        // get players
        List<String> players = new ArrayList<>();
        for (Player player : getPlayers()) {
            players.add(player.getName());
        }

        // set players
        packet.getSpecificModifier(Collection.class).write(0, players);

        // set mode
        packet.getIntegers().write(1, MODE_CREATE);
        
        return packet;
    }

    private PacketContainer newRemovePacket() {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);

        // set name
        packet.getStrings().write(0, getId());

        // set mode
        packet.getIntegers().write(1, MODE_REMOVE);

        return packet;
    }

    private PacketContainer newUpdatePacket() {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);

        // set name
        packet.getStrings().write(0, getId());

        // set display name
        packet.getStrings().write(1, getDisplayName());

        // set prefix
        packet.getStrings().write(2, getPrefix());

        // set suffix
        packet.getStrings().write(3, getSuffix());

        // set nametag visibility
        packet.getStrings().write(4, getNameTagVisibility().getProtocolName());

        // set collision rule
        packet.getStrings().write(5, getCollisionRule().getProtocolName());

        // set color
        packet.getIntegers().write(0, -1); // ChatColor RESET

        // set mode
        packet.getIntegers().write(1, MODE_UPDATE);

        // pack option data
        int data = 0;
        if (isAllowFriendlyFire()) {
            data |= 1;
        }
        if (isCanSeeFriendlyInvisibles()) {
            data |= 2;
        }

        // set pack data
        packet.getIntegers().write(2, data);
        
        return packet;
    }

    private PacketContainer newPlayerPacket(Player player, boolean remove) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);

        // set name
        packet.getStrings().write(0, getId());

        // set players
        packet.getSpecificModifier(Collection.class).write(0, Collections.singletonList(player.getName()));

        // set mode
        if (remove) {
            packet.getIntegers().write(1, MODE_REMOVE_PLAYERS);
        } else {
            packet.getIntegers().write(1, MODE_ADD_PLAYERS);
        }
        
        return packet;
    }

    public enum CollisionRule {
        ALWAYS("always"),
        NEVER("never"),
        HIDE_FOR_OTHER_TEAMS("pushOtherTeams"),
        HIDE_FOR_OWN_TEAM("pushOwnTeam");

        private final String protocolName;

        CollisionRule(String protocolName) {
            this.protocolName = protocolName;
        }

        public String getProtocolName() {
            return protocolName;
        }
    }

    public enum NameTagVisibility {
        ALWAYS("always"),
        NEVER("never"),
        HIDE_FOR_OTHER_TEAMS("hideForOtherTeams"),
        HIDE_FOR_OWN_TEAM("hideForOwnTeam");

        private final String protocolName;

        NameTagVisibility(String protocolName) {
            this.protocolName = protocolName;
        }

        public String getProtocolName() {
            return protocolName;
        }
    }

}