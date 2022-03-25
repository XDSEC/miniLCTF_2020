/*
 * This file is a part of project QuickShop, the name is SubCommand_Amount.java
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
import org.abc.A.util.Util;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.abc.A.D.CommandProcesser;
import org.abc.A.QuickShop;

import java.io.File;
import java.util.List;

public class SubCommand_Recovery implements CommandProcesser {

    private final QuickShop plugin = QuickShop.instance;

    @Override
    public void onCommand(
        @NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] cmdArg) {
        if (!(sender instanceof ConsoleCommandSender)) {
            return;
        }
        File file = new File(plugin.getDataFolder(), "recovery.txt");
        if (!file.exists()) {
            MsgUtil.sendMessage(sender,"recovery.txt not exist, do not execute this command unless you know what are you doing.");
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    Util.backupDatabase();
                    plugin.getShopLoader().recoverFromFile(Util.readToString(file));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);

    }

    @NotNull
    @Override
    public List<String> onTabComplete(
        @NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] cmdArg) {
        return null;
    }

}
