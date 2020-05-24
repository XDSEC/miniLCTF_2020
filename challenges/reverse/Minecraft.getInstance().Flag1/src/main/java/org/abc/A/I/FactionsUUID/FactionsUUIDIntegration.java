/*
 * This file is a part of project QuickShop, the name is FactionsUUIDIntegration.java
 * Copyright (C) Ghost_chu <https://github.com/Ghost-chu>
 * Copyright (C) Bukkit Commons Studio and contributors
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

package org.abc.A.I.FactionsUUID;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.perms.PermissibleAction;
import java.util.List;

import org.abc.A.QuickShop;
import org.abc.A.I.IntegrateStage;
import org.abc.A.I.IntegratedPlugin;
import org.abc.A.I.IntegrationStage;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@IntegrationStage(loadStage = IntegrateStage.onEnableAfter)
public class FactionsUUIDIntegration implements IntegratedPlugin {
    private final List<String> createFlags;

    private final List<String> tradeFlags;

    private final boolean createRequireOpen;

    private final boolean createRequireNormal;

    private final boolean createRequireWilderness;

    private final boolean createRequirePeaceful;

    private final boolean createRequirePermanent;

    private final boolean createRequireSafeZone;

    private final boolean createRequireOwn;

    private final boolean createRequireWarZone;

    private final boolean tradeRequireOpen;

    private final boolean tradeRequireNormal;

    private final boolean tradeRequireWilderness;

    private final boolean tradeRequirePeaceful;

    private final boolean tradeRequirePermanent;

    private final boolean tradeRequireSafeZone;

    private final boolean tradeRequireOwn;

    private final boolean tradeRequireWarZone;

    public FactionsUUIDIntegration(QuickShop plugin) {
        this.createFlags = plugin.getConfig().getStringList("integration.factions.create.flags");
        this.tradeFlags = plugin.getConfig().getStringList("integration.factions.trade.flags");

        this.createRequireOpen =
            plugin.getConfig().getBoolean("integration.factions.create.require.open");
        this.createRequireNormal =
            plugin.getConfig().getBoolean("integration.factions.create.require.normal");
        this.createRequireWilderness =
            plugin.getConfig().getBoolean("integration.factions.create.require.wilderness");
        this.createRequirePeaceful =
            plugin.getConfig().getBoolean("integration.factions.create.require.peaceful");
        this.createRequirePermanent =
            plugin.getConfig().getBoolean("integration.factions.create.require.permanent");
        this.createRequireSafeZone =
            plugin.getConfig().getBoolean("integration.factions.create.require.safezone");
        this.createRequireOwn =
            plugin.getConfig().getBoolean("integration.factions.create.require.own");
        this.createRequireWarZone =
            plugin.getConfig().getBoolean("integration.factions.create.require.warzone");

        this.tradeRequireOpen =
            plugin.getConfig().getBoolean("integration.factions.trade.require.open");
        this.tradeRequireNormal =
            plugin.getConfig().getBoolean("integration.factions.trade.require.normal");
        this.tradeRequireWilderness =
            plugin.getConfig().getBoolean("integration.factions.trade.require.wilderness");
        this.tradeRequirePeaceful =
            plugin.getConfig().getBoolean("integration.factions.trade.require.peaceful");
        this.tradeRequirePermanent =
            plugin.getConfig().getBoolean("integration.factions.trade.require.permanent");
        this.tradeRequireSafeZone =
            plugin.getConfig().getBoolean("integration.factions.trade.require.safezone");
        this.tradeRequireOwn = plugin.getConfig().getBoolean("integration.factions.trade.require.own");
        this.tradeRequireWarZone =
            plugin.getConfig().getBoolean("integration.factions.trade.require.warzone");
    }

    @Override
    public @NotNull String getName() {
        return "Factions";
    }

    @Override
    public boolean canCreateShopHere(@NotNull Player player, @NotNull Location location) {
        Faction faction = Board.getInstance().getFactionAt(new FLocation(location));
        if (faction == null) {
            return true;
        }
        if (createRequireOpen && !faction.getOpen()) {
            return false;
        }
        if (createRequireSafeZone && !faction.isSafeZone()) {
            return false;
        }
        if (createRequirePermanent && !faction.isPermanent()) {
            return false;
        }
        if (createRequirePeaceful && !faction.isPeaceful()) {
            return false;
        }
        if (createRequireWilderness && !faction.isWilderness()) {
            return false;
        }
        if (createRequireOpen && !faction.getOpen()) {
            return false;
        }
        if (createRequireWarZone && !faction.isWarZone()) {
            return false;
        }
        if (createRequireNormal && !faction.isNormal()) {
            return false;
        }
        if (createRequireOwn
            && !faction.getOwnerList(new FLocation(location)).contains(player.getName())) {
            return false;
        }
        for (String flag : createFlags) {
            if (!faction.hasAccess(
                FPlayers.getInstance().getByPlayer(player), PermissibleAction.fromString(flag))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean canTradeShopHere(@NotNull Player player, @NotNull Location location) {

        Faction faction = Board.getInstance().getFactionAt(new FLocation(location));
        if (faction == null) {
            return true;
        }
        if (tradeRequireOpen && !faction.getOpen()) {
            return false;
        }
        if (tradeRequireSafeZone && !faction.isSafeZone()) {
            return false;
        }
        if (tradeRequirePermanent && !faction.isPermanent()) {
            return false;
        }
        if (tradeRequirePeaceful && !faction.isPeaceful()) {
            return false;
        }
        if (tradeRequireWilderness && !faction.isWilderness()) {
            return false;
        }
        if (tradeRequireOpen && !faction.getOpen()) {
            return false;
        }
        if (tradeRequireWarZone && !faction.isWarZone()) {
            return false;
        }
        if (tradeRequireNormal && !faction.isNormal()) {
            return false;
        }
        if (tradeRequireOwn
            && !faction.getOwnerList(new FLocation(location)).contains(player.getName())) {
            return false;
        }
        for (String flag : tradeFlags) {
            if (!faction.hasAccess(
                FPlayers.getInstance().getByPlayer(player), PermissibleAction.fromString(flag))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void load() {
    }

    @Override
    public void unload() {
    }

}
