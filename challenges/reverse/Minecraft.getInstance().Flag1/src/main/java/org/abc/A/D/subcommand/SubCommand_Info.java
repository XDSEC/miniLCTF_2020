/*
 * This file is a part of project QuickShop, the name is SubCommand_Info.java
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

package org.abc.A.D.subcommand;

import org.abc.A.N.ContainerShop;
import org.abc.A.N.Shop;
import org.abc.A.N.ShopChunk;
import org.abc.A.util.MsgUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.abc.A.D.CommandProcesser;
import org.abc.A.QuickShop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubCommand_Info implements CommandProcesser {

    private final QuickShop plugin = QuickShop.instance;

    @Override
    public void onCommand(
        @NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] cmdArg) {
        int buying, selling, doubles, chunks, worlds, doubleschests;
        buying = selling = doubles = chunks = worlds = doubleschests = 0;
        int nostock = 0;

        for (Map<ShopChunk, HashMap<Location, Shop>> inWorld :
            plugin.getShopManager().getShops().values()) {
            worlds++;

            for (Map<Location, Shop> inChunk : inWorld.values()) {
                chunks++;
                //noinspection unchecked
                for (Shop shop : (ArrayList<Shop>) new ArrayList<>(inChunk.values()).clone()) {
                    if (shop.isBuying()) {
                        buying++;
                    } else if (shop.isSelling()) {
                        selling++;
                    }

                    if (shop instanceof ContainerShop && ((ContainerShop) shop).isDoubleShop()) {
                        doubles++;
                    } else if (shop.isSelling() && shop.getRemainingStock() == 0) {
                        nostock++;
                    }

                    if (shop instanceof ContainerShop && ((ContainerShop) shop).isDoubleChestShop()) {
                        doubleschests++;
                    }
                }
            }
        }

        MsgUtil.sendMessage(sender,ChatColor.RED + "QuickShop Statistics...");
        MsgUtil.sendMessage(sender,ChatColor.GREEN + "Server UniqueID: " + plugin.getServerUniqueID());
        MsgUtil.sendMessage(sender,
            ChatColor.GREEN
                + ""
                + (buying + selling)
                + " shops in "
                + chunks
                + " chunks spread over "
                + worlds
                + " worlds.");
        MsgUtil.sendMessage(sender,
            ChatColor.GREEN
                + ""
                + doubles
                + " double shops. ("
                + doubleschests
                + " shops create on double chest.)");
        MsgUtil.sendMessage(sender,
            ChatColor.GREEN
                + ""
                + nostock
                + " nostock selling shops (excluding doubles) which will be removed by /qs clean.");
        MsgUtil.sendMessage(sender,ChatColor.GREEN + "QuickShop " + QuickShop.getVersion());
    }

    @NotNull
    @Override
    public List<String> onTabComplete(
        @NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] cmdArg) {
        return new ArrayList<>();
    }

}
