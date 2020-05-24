/*
 * This file is a part of project QuickShop, the name is SubCommand_Buy.java
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
import org.abc.A.N.ShopType;
import org.abc.A.util.MsgUtil;
import org.abc.A.util.Util;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.jetbrains.annotations.NotNull;
import org.abc.A.D.CommandProcesser;
import org.abc.A.QuickShop;

import java.util.ArrayList;
import java.util.List;

public class SubCommand_Buy implements CommandProcesser {

    private final QuickShop plugin = QuickShop.instance;

    @Override
    public void onCommand(
        @NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] cmdArg) {
        if (!(sender instanceof Player)) {
            MsgUtil.sendMessage(sender,MsgUtil.getMessage("Can't run command by Console", sender));
            return;
        }

        final BlockIterator bIt = new BlockIterator((LivingEntity) sender, 10);

        if (!bIt.hasNext()) {
            MsgUtil.sendMessage(sender,MsgUtil.getMessage("not-looking-at-shop", sender));
            return;
        }

        while (bIt.hasNext()) {
            final Block b = bIt.next();
            final Shop shop = plugin.getShopManager().getShop(b.getLocation());

            if (shop != null) {
                if (shop.getModerator().isModerator(((Player) sender).getUniqueId())) {
                    shop.setShopType(ShopType.BUYING);
                    // shop.setSignText();
                    shop.update();
                    MsgUtil.sendMessage(sender,MsgUtil.getMessage("command.now-buying", sender, Util.getItemStackName(shop.getItem())));
                    return;
                } else {
                    MsgUtil.getMessage("not-managed-shop", sender);
                    return;
                }
            }
        }

        MsgUtil.sendMessage(sender,MsgUtil.getMessage("not-looking-at-shop", sender));
    }

    @NotNull
    @Override
    public List<String> onTabComplete(
        @NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] cmdArg) {
        return new ArrayList<>();
    }

}
