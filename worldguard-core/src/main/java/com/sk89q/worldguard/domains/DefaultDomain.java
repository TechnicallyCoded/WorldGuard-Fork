/*
 * WorldGuard, a suite of tools for Minecraft
 * Copyright (C) sk89q <http://www.sk89q.com>
 * Copyright (C) WorldGuard team and contributors
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.sk89q.worldguard.domains;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sk89q.worldguard.util.profile.Profile;
import com.sk89q.worldguard.util.profile.cache.ProfileCache;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.event.ClickEvent;
import com.sk89q.worldedit.util.formatting.text.event.HoverEvent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.util.ChangeTracked;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A combination of a {@link PlayerDomain} and a {@link GroupDomain}.
 */
public class DefaultDomain implements Domain, ChangeTracked {

    private final Object domainLock = new Object();
    private PlayerDomain playerDomain = new PlayerDomain();
    private GroupDomain groupDomain = new GroupDomain();

    /**
     * Create a new domain.
     */
    public DefaultDomain() {
    }

    /**
     * Create a new domain from an existing one, making a copy of all values.
     *
     * @param existing the other domain to copy values from
     */
    public DefaultDomain(DefaultDomain existing) {
        setPlayerDomain(existing.getPlayerDomain());
        setGroupDomain(existing.getGroupDomain());
    }

    /**
     * Get the domain that holds the players.
     *
     * @return a domain
     */
    public PlayerDomain getPlayerDomain() {
        synchronized (domainLock) {
            return playerDomain;
        }
    }

    /**
     * Set a new player domain.
     *
     * @param playerDomain a domain
     */
    public void setPlayerDomain(PlayerDomain playerDomain) {
        checkNotNull(playerDomain);
        synchronized (domainLock) {
            this.playerDomain = new PlayerDomain(playerDomain);
        }
    }

    /**
     * Set the domain that holds the groups.
     *
     * @return a domain
     */
    public GroupDomain getGroupDomain() {
        synchronized (domainLock) {
            return groupDomain;
        }
    }

    /**
     * Set a new group domain.
     *
     * @param groupDomain a domain
     */
    public void setGroupDomain(GroupDomain groupDomain) {
        checkNotNull(groupDomain);
        synchronized (domainLock) {
            this.groupDomain = new GroupDomain(groupDomain);
        }
    }

    /**
     * Add the given player to the domain, identified by the player's name.
     *
     * @param name the name of the player
     */
    public void addPlayer(String name) {
        getPlayerDomain().addPlayer(name);
    }

    /**
     * Remove the given player from the domain, identified by the player's name.
     *
     * @param name the name of the player
     */
    public void removePlayer(String name) {
        getPlayerDomain().removePlayer(name);
    }

    /**
     * Remove the given player from the domain, identified by the player's UUID.
     *
     * @param uuid the UUID of the player
     */
    public void removePlayer(UUID uuid) {
        getPlayerDomain().removePlayer(uuid);
    }

    /**
     * Add the given player to the domain, identified by the player's UUID.
     *
     * @param uniqueId the UUID of the player
     */
    public void addPlayer(UUID uniqueId) {
        getPlayerDomain().addPlayer(uniqueId);
    }

    /**
     * Remove the given player from the domain, identified by either the
     * player's name, the player's unique ID, or both.
     *
     * @param player the player
     */
    public void removePlayer(LocalPlayer player) {
        getPlayerDomain().removePlayer(player);
    }

    /**
     * Add the given player to the domain, identified by the player's UUID.
     *
     * @param player the player
     */
    public void addPlayer(LocalPlayer player) {
        getPlayerDomain().addPlayer(player);
    }

    /**
     * Add all the entries from another domain.
     *
     * @param other the other domain
     */
    public void addAll(DefaultDomain other) {
        checkNotNull(other);
        for (String player : other.getPlayers()) {
            addPlayer(player);
        }
        for (UUID uuid : other.getUniqueIds()) {
            addPlayer(uuid);
        }
        for (String group : other.getGroups()) {
            addGroup(group);
        }
    }

    /**
     * Remove all the entries from another domain.
     *
     * @param other the other domain
     */
    public void removeAll(DefaultDomain other) {
        checkNotNull(other);
        for (String player : other.getPlayers()) {
            removePlayer(player);
        }
        for (UUID uuid : other.getUniqueIds()) {
            removePlayer(uuid);
        }
        for (String group : other.getGroups()) {
            removeGroup(group);
        }
    }

    /**
     * Get the set of player names.
     *
     * @return the set of player names
     */
    public Set<String> getPlayers() {
        return getPlayerDomain().getPlayers();
    }

    /**
     * Get the set of player UUIDs.
     *
     * @return the set of player UUIDs
     */
    public Set<UUID> getUniqueIds() {
        return getPlayerDomain().getUniqueIds();
    }

    /**
     * Add the name of the group to the domain.
     *
     * @param name the name of the group.
     */
    public void addGroup(String name) {
        getGroupDomain().addGroup(name);
    }

    /**
     * Remove the given group from the domain.
     *
     * @param name the name of the group
     */
    public void removeGroup(String name) {
        getGroupDomain().removeGroup(name);
    }

    /**
     * Get the set of group names.
     *
     * @return the set of group names
     */
    public Set<String> getGroups() {
        return getGroupDomain().getGroups();
    }

    @Override
    public boolean contains(LocalPlayer player) {
        return getPlayerDomain().contains(player) || getGroupDomain().contains(player);
    }

    @Override
    public boolean contains(UUID uniqueId) {
        return getPlayerDomain().contains(uniqueId);
    }

    @Override
    public boolean contains(String playerName) {
        return getPlayerDomain().contains(playerName);
    }

    @Override
    public int size() {
        return getGroupDomain().size() + getPlayerDomain().size();
    }

    @Override
    public void clear() {
        getPlayerDomain().clear();
        getGroupDomain().clear();
    }

    public void removeAll() {
        clear();
    }

    public String toPlayersString() {
        return toPlayersString(null);
    }

    public String toPlayersString(@Nullable ProfileCache cache) {
        StringBuilder str = new StringBuilder();
        List<String> output = new ArrayList<>();

        for (String name : getPlayerDomain().getPlayers()) {
            output.add("name:" + name);
        }

        if (cache != null) {
            ImmutableMap<UUID, Profile> results = cache.getAllPresent(getPlayerDomain().getUniqueIds());
            for (UUID uuid : getPlayerDomain().getUniqueIds()) {
                Profile profile = results.get(uuid);
                if (profile != null) {
                    output.add(profile.getName() + "*");
                } else {
                    output.add("uuid:" + uuid);
                }
            }
        } else {
            for (UUID uuid : getPlayerDomain().getUniqueIds()) {
                output.add("uuid:" + uuid);
            }
        }

        output.sort(String.CASE_INSENSITIVE_ORDER);
        for (Iterator<String> it = output.iterator(); it.hasNext();) {
            str.append(it.next());
            if (it.hasNext()) {
                str.append(", ");
            }
        }
        return str.toString();
    }
    
    public String toGroupsString() {
        StringBuilder str = new StringBuilder();
        for (Iterator<String> it = getGroupDomain().getGroups().iterator(); it.hasNext(); ) {
            str.append("g:");
            str.append(it.next());
            if (it.hasNext()) {
                str.append(", ");
            }
        }
        return str.toString();
    }

    public String toUserFriendlyString() {
        StringBuilder str = new StringBuilder();

        if (getPlayerDomain().size() > 0) {
            str.append(toPlayersString());
        }

        if (getGroupDomain().size() > 0) {
            if (str.length() > 0) {
                str.append("; ");
            }

            str.append(toGroupsString());
        }

        return str.toString();
    }

    public String toUserFriendlyString(ProfileCache cache) {
        StringBuilder str = new StringBuilder();

        if (getPlayerDomain().size() > 0) {
            str.append(toPlayersString(cache));
        }

        if (getGroupDomain().size() > 0) {
            if (str.length() > 0) {
                str.append("; ");
            }

            str.append(toGroupsString());
        }

        return str.toString();
    }

    public Component toUserFriendlyComponent(@Nullable ProfileCache cache) {
        final TextComponent.Builder builder = TextComponent.builder("");
        if (getPlayerDomain().size() > 0) {
            builder.append(toPlayersComponent(cache));
        }
        if (getGroupDomain().size() > 0) {
            if (getPlayerDomain().size() > 0) {
                builder.append(TextComponent.of("; "));
            }
            builder.append(toGroupsComponent());
        }
        return builder.build();
    }

    private Component toGroupsComponent() {
        final TextComponent.Builder builder = TextComponent.builder("");
        for (Iterator<String> it = getGroupDomain().getGroups().iterator(); it.hasNext(); ) {
            builder.append(TextComponent.of("g:", TextColor.GRAY))
                    .append(TextComponent.of(it.next(), TextColor.GOLD));
            if (it.hasNext()) {
                builder.append(TextComponent.of(", "));
            }
        }
        return builder.build().hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, TextComponent.of("Groups")));
    }

    private Component toPlayersComponent(ProfileCache cache) {
        List<String> uuids = Lists.newArrayList();
        Map<String, UUID> profileMap = Maps.newHashMap();

        for (String name : getPlayerDomain().getPlayers()) {
            profileMap.put(name, null);
        }

        if (cache != null) {
            ImmutableMap<UUID, Profile> results = cache.getAllPresent(getPlayerDomain().getUniqueIds());
            for (UUID uuid : getPlayerDomain().getUniqueIds()) {
                Profile profile = results.get(uuid);
                if (profile != null) {
                    profileMap.put(profile.getName(), uuid);
                } else {
                    uuids.add(uuid.toString());
                }
            }
        } else {
            for (UUID uuid : getPlayerDomain().getUniqueIds()) {
                uuids.add(uuid.toString());
            }
        }

        final TextComponent.Builder builder = TextComponent.builder("");
        final Iterator<TextComponent> profiles = profileMap.keySet().stream().sorted().map(name -> {
            final UUID uuid = profileMap.get(name);
            if (uuid == null) {
                return TextComponent.of(name, TextColor.YELLOW)
                        .hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, TextComponent.of("Name only", TextColor.GRAY)
                            .append(TextComponent.newline()).append(TextComponent.of("Click to copy"))))
                        .clickEvent(ClickEvent.of(ClickEvent.Action.COPY_TO_CLIPBOARD, name));
            } else {
                return TextComponent.of(name, TextColor.YELLOW)
                        .hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, TextComponent.of("Last known name of uuid: ", TextColor.GRAY)
                            .append(TextComponent.of(uuid.toString(), TextColor.WHITE))
                            .append(TextComponent.newline()).append(TextComponent.of("Click to copy"))))
                        .clickEvent(ClickEvent.of(ClickEvent.Action.COPY_TO_CLIPBOARD, uuid.toString()));
            }
        }).iterator();
        while (profiles.hasNext()) {
            builder.append(profiles.next());
            if (profiles.hasNext() || !uuids.isEmpty()) {
                builder.append(TextComponent.of(", "));
            }
        }

        if (!uuids.isEmpty()) {
            builder.append(TextComponent.of(uuids.size() + " unknown uuid" + (uuids.size() == 1 ? "" : "s"), TextColor.GRAY)
                    .hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, TextComponent.of("Unable to resolve the name for:", TextColor.GRAY)
                        .append(TextComponent.newline())
                        .append(TextComponent.of(String.join("\n", uuids), TextColor.WHITE))
                        .append(TextComponent.newline().append(TextComponent.of("Click to copy")))))
                    .clickEvent(ClickEvent.of(ClickEvent.Action.COPY_TO_CLIPBOARD, String.join(",", uuids))));
        }


        return builder.build();
    }

    @Override
    public boolean isDirty() {
        return getPlayerDomain().isDirty() || getGroupDomain().isDirty();
    }

    @Override
    public void setDirty(boolean dirty) {
        getPlayerDomain().setDirty(dirty);
        getGroupDomain().setDirty(dirty);
    }

    @Override
    public String toString() {
        return "{players=" + getPlayerDomain() +
                ", groups=" + getGroupDomain() +
                '}';
    }

}
