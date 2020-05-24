/*
 * This file is a part of project QuickShop, the name is SubCommand_Update.java
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

import org.abc.A.util.MsgUtil;
import org.abc.A.util.UpdateInfomation;
import org.abc.A.util.Updater;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.abc.A.D.CommandProcesser;
import org.abc.A.QuickShop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SubCommand_Update implements CommandProcesser {

    private final QuickShop plugin = QuickShop.instance;

    @Override
    public void onCommand(
        @NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] cmdArg) {
        new BukkitRunnable() {
            @Override
            public void run() {
                MsgUtil.sendMessage(sender,ChatColor.YELLOW + "Checking for updates...");

                final UpdateInfomation updateInfomation = Updater.checkUpdate();
                final String updateVersion = updateInfomation.getVersion();

                if (updateVersion == null) {
                    MsgUtil.sendMessage(sender,ChatColor.RED + "Failed check the update, connection issue?");
                    return;
                }

                if (updateVersion.equals(plugin.getDescription().getVersion())) {
                    MsgUtil.sendMessage(sender,ChatColor.GREEN + "No updates can update now.");
                    return;
                }

                MsgUtil.sendMessage(sender,ChatColor.YELLOW + "Downloading update, this may need a while...");

                final byte[] pluginBin;

                try {
                    pluginBin = Updater.downloadUpdatedJar();
                } catch (IOException e) {
                    MsgUtil.sendMessage(sender,ChatColor.RED + "Update failed, get details to look the console.");
                    plugin.getSentryErrorReporter().ignoreThrow();
                    e.printStackTrace();
                    return;
                }

                if (pluginBin.length < 1) {
                    MsgUtil.sendMessage(sender,
                        ChatColor.RED + "Download failed, check your connection before contact the author.");
                    return;
                }

                MsgUtil.sendMessage(sender,ChatColor.YELLOW + "Installing update...");

                try {
                    Updater.replaceTheJar(pluginBin);
                } catch (IOException ioe) {
                    MsgUtil.sendMessage(sender,ChatColor.RED + "Update failed, get details to look the console.");
                    plugin.getSentryErrorReporter().ignoreThrow();
                    ioe.printStackTrace();
                    return;
                } catch (RuntimeException re) {
                    MsgUtil.sendMessage(sender,ChatColor.RED + "Update failed, " + re.getMessage());
                    return;
                }

                MsgUtil.sendMessage(sender,
                    ChatColor.GREEN + "Successfully, restart your server to apply the changes!");
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
