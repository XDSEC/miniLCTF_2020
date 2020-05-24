/*
 * This file is a part of project QuickShop, the name is SubCommand_CleanGhost.java
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

import org.abc.A.N.Shop;
import org.abc.A.util.MsgUtil;
import org.abc.A.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.abc.A.D.CommandProcesser;
import org.abc.A.QuickShop;

import java.util.ArrayList;
import java.util.List;

public class SubCommand_CleanGhost implements CommandProcesser {

    private final QuickShop plugin = QuickShop.instance;

    @Override
    public void onCommand(
        @NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] cmdArg) {
        if (cmdArg.length < 1) {
            MsgUtil.sendMessage(sender,
                ChatColor.YELLOW
                    + "This command will purge all data damaged shop, create in disallow world shop, create disallow sell items shop and IN NOT LOADED WORLD SHOPS, make sure you have backup your shops data, and use /qs cleanghost confirm to continue.");
            return;
        }

        if (!"confirm".equalsIgnoreCase(cmdArg[0])) {
            MsgUtil.sendMessage(sender,
                ChatColor.YELLOW
                    + "This command will purge all data damaged shop, create in disallow world shop, create disallow sell items shop and IN NOT LOADED WORLD SHOPS, make sure you have backup your shops data, and use /qs cleanghost confirm to continue.");
            return;
        }

        MsgUtil.sendMessage(sender,
            ChatColor.GREEN
                + "Starting checking the shop be ghost, all does not exist shop will be removed...");

        new BukkitRunnable() {
            @Override
            public void run() {
                MsgUtil.sendMessage(sender,ChatColor.GREEN + "Async thread is started, please wait...");
                Util.backupDatabase(); // Already warn the user, don't care about backup result.
                for (Shop shop : plugin.getShopLoader().getShopsInDatabase()) {
                    MsgUtil.sendMessage(sender,
                        ChatColor.GRAY
                            + "Checking the shop "
                            + shop
                            + " metadata and location block state...");
                    if (shop == null) {
                        continue; // WTF
                    }
          /*
          shop.getItem() is a constant that has NotNull annotations so.
          if (shop.getItem() == null) {
              MsgUtil.sendMessage(sender,ChatColor.YELLOW + "Shop " + shop + " removing cause item data is damaged.");
              shop.delete();
              continue;
          }*/
                    if (shop.getItem().getType() == Material.AIR) {
                        MsgUtil.sendMessage(sender,
                            ChatColor.YELLOW + "Shop " + shop + " removing cause item data is damaged.");
                        shop.delete();
                        continue;
                    }
          /*
          shop.getLocation() is a constant that has NotNull annotations so.
          if (shop.getLocation() == null) {
              MsgUtil.sendMessage(sender,ChatColor.YELLOW + "Shop " + shop + " removing cause location data is damaged.");
              shop.delete();
              continue;
          }*/
                    if (shop.getLocation().getWorld() == null) {
                        MsgUtil.sendMessage(sender,
                            ChatColor.YELLOW + "Shop " + shop + " removing cause target world not loaded.");
                        shop.delete();
                        continue;
                    }
                    //noinspection ConstantConditions
                    if (shop.getOwner() == null) {
                        MsgUtil.sendMessage(sender,
                            ChatColor.YELLOW + "Shop " + shop + " removing cause owner data is damaged.");
                        shop.delete();
                        continue;
                    }
                    // Shop exist check
                    plugin
                        .getServer()
                        .getScheduler()
                        .runTask(
                            plugin,
                            () -> {
                                Util.debugLog(
                                    "Posted to main server thread to continue access Bukkit API for shop "
                                        + shop);
                                if (!Util.canBeShop(shop.getLocation().getBlock())) {
                                    MsgUtil.sendMessage(sender,
                                        ChatColor.YELLOW
                                            + "Shop "
                                            + shop
                                            + " removing cause target location nolonger is a shop or disallow create the shop.");
                                    shop.delete();
                                }
                            }); // Post to server main thread to check.
                    try {
                        Thread.sleep(50); // Have a rest, don't blow up the main server thread.
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                MsgUtil.sendMessage(sender,ChatColor.GREEN + "All shops completed checks.");
            }
        }.runTaskAsynchronously(plugin);
    }

    @NotNull
    @Override
    public List<String> onTabComplete(
        @NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] cmdArg) {
        return new ArrayList<>();
    }

}
