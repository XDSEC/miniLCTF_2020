/*
 * This file is a part of project QuickShop, the name is BlockListener.java
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

package org.abc.A.J;

import org.abc.A.util.MsgUtil;
import org.abc.A.util.Util;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.abc.A.Cache;
import org.abc.A.QuickShop;
import org.abc.A.N.Info;
import org.abc.A.N.Shop;
import org.abc.A.N.ShopAction;

public class BlockListener extends ProtectionListenerBase implements Listener {

    public BlockListener(@NotNull final QuickShop plugin, @Nullable final Cache cache) {
        super(plugin, cache);
    }

    /*
     * Removes chests when they're destroyed.
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onBreak(BlockBreakEvent e) {

        final Block b = e.getBlock();

        if (b.getState() instanceof Sign) {
            Sign sign = (Sign) b.getState();
            if (super.getPlugin().getConfig().getBoolean("lockette.enable")
                && sign.getLine(0).equals(super.getPlugin().getConfig().getString("lockette.private"))
                || sign.getLine(0).equals(super.getPlugin().getConfig().getString("lockette.more_users"))) {
                // Ignore break lockette sign
                super.getPlugin().getLogger()
                    .info("Skipped a dead-lock shop sign.(Lockette or other sign-lock plugin)");
                return;
            }
        }

        final Player p = e.getPlayer();
        // If the shop was a chest
        if (Util.canBeShop(b)) {
            final Shop shop = getShopPlayer(b.getLocation(), false);
            if (shop == null) {
                return;
            }
            // If they're either survival or the owner, they can break it
            if (p.getGameMode() == GameMode.CREATIVE && !p.getUniqueId().equals(shop.getOwner())) {
                // Check SuperTool
                if (p.getInventory().getItemInMainHand().getType() == Material.GOLDEN_AXE) {
                    if(getPlugin().getConfig().getBoolean("shop.disable-super-tool")){
                        e.setCancelled(true);
                        MsgUtil.sendMessage(p,MsgUtil.getMessage("supertool-is-disabled", p));
                        return;
                    }
                    MsgUtil.sendMessage(p,MsgUtil.getMessage("break-shop-use-supertool", p));
                    return;
                }
                e.setCancelled(true);
                MsgUtil.sendMessage(p,
                    MsgUtil.getMessage(
                        "no-creative-break", p, MsgUtil.getItemi18n(Material.GOLDEN_AXE.name())));
                return;
            }

            if (e.isCancelled()) {
                MsgUtil.sendMessage(p,MsgUtil.getMessage("no-permission", p));
                Util.debugLog("The action was cancelled by other plugin");
                return;
            }

            if (!shop.getModerator().isOwner(p.getUniqueId())
                && !QuickShop.getPermissionManager().hasPermission(p, "quickshop.other.destroy")) {
                e.setCancelled(true);
                MsgUtil.sendMessage(p,MsgUtil.getMessage("no-permission", p));
                return;
            }
            // Cancel their current menu... Doesnt cancel other's menu's.
            final Info action = super.getPlugin().getShopManager().getActions().get(p.getUniqueId());

            if (action != null) {
                action.setAction(ShopAction.CANCELLED);
            }

            shop.onUnload();
            shop.delete();
            MsgUtil.sendMessage(p,MsgUtil.getMessage("success-removed-shop", p));
        } else if (Util.isWallSign(b.getType())) {
            if (b.getState() instanceof Sign) {
                Sign sign = (Sign) b.getState();
                if (sign.getLine(0).equals(super.getPlugin().getConfig().getString("lockette.private"))
                    || sign.getLine(0).equals(super.getPlugin().getConfig().getString("lockette.more_users"))) {
                    // Ignore break lockette sign
                    return;
                }
            }

            final Shop shop = getShopNextTo(b.getLocation());

            if (shop == null) {
                return;
            }
            // If they're in creative and not the owner, don't let them
            // (accidents happen)
            if (p.getGameMode() == GameMode.CREATIVE && !p.getUniqueId().equals(shop.getOwner())) {
                // Check SuperTool
                if (p.getInventory().getItemInMainHand().getType() == Material.GOLDEN_AXE) {
                    if(getPlugin().getConfig().getBoolean("shop.disable-super-tool")){
                        e.setCancelled(true);
                        MsgUtil.sendMessage(p,MsgUtil.getMessage("supertool-is-disabled", p));
                        return;
                    }
                    MsgUtil.sendMessage(p,MsgUtil.getMessage("break-shop-use-supertool", p));
                    shop.delete();
                    return;
                }
                e.setCancelled(true);
                MsgUtil.sendMessage(p,
                    MsgUtil.getMessage(
                        "no-creative-break", p, MsgUtil.getItemi18n(Material.GOLDEN_AXE.name())));
            }
            //Allow Shop owner break the shop sign(for sign replacement)
            if(getPlugin().getConfig().getBoolean("shop.allow-owner-break-shop-sign")&&p.getUniqueId().equals(shop.getOwner())){
               return;
            }
            Util.debugLog("Cannot break the sign.");
            e.setCancelled(true);
        }
    }

    /**
     * Gets the shop a sign is attached to
     *
     * @param loc The location of the sign
     * @return The shop
     */
    @Nullable
    private Shop getShopNextTo(@NotNull Location loc) {
        final Block b = Util.getAttached(loc.getBlock());
        // Util.getAttached(b)
        if (b == null) {
            return null;
        }

        return getShopPlayer(b.getLocation(), false);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onInventoryMove(InventoryMoveItemEvent event) {
        if (!super.getPlugin().getConfig().getBoolean("shop.update-sign-when-inventory-moving", true)) {
            return;
        }

        final Inventory inventory = event.getDestination();
        final Location location = inventory.getLocation();

        if (location == null) {
            return;
        }

        // Delayed task. Event triggers when item is moved, not when it is received.
        final Shop shop = getShopRedstone(location, true);
        if (shop != null) {
            super.getPlugin().getSignUpdateWatcher().scheduleSignUpdate(shop);
        }
    }


    /*
     * Listens for chest placement, so a doublechest shop can't be created.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlace(BlockPlaceEvent e) {

        final Material type = e.getBlock().getType();
        final Block placingBlock = e.getBlock();
        final Player player = e.getPlayer();

        if (type != Material.CHEST) {
            return;
        }
        Block chest = null;
        //Chest combine mechanic based checking
        if (player.isSneaking()) {
            Block blockAgainst = e.getBlockAgainst();
            if (blockAgainst.getType() == Material.CHEST && placingBlock.getFace(blockAgainst) != BlockFace.UP && placingBlock.getFace(blockAgainst) != BlockFace.DOWN && !(((Chest) blockAgainst.getState()).getInventory() instanceof DoubleChestInventory)) {
                chest = e.getBlockAgainst();
            } else {
                return;
            }
        } else {
            //Get all chest in vertical Location
            BlockFace placingChestFacing = ((Directional) (placingBlock.getState().getBlockData())).getFacing();
            for (BlockFace face : Util.getVerticalFacing()) {
                //just check the right side and left side
                if (face != placingChestFacing && face != placingChestFacing.getOppositeFace()) {
                    Block nearByBlock = placingBlock.getRelative(face);
                    if (nearByBlock.getType() == Material.CHEST
                        //non double chest
                        && !(((Chest) nearByBlock.getState()).getInventory() instanceof DoubleChestInventory)
                        //same facing
                        && placingChestFacing == ((Directional) nearByBlock.getState().getBlockData()).getFacing()) {
                        if (chest == null) {
                            chest = nearByBlock;
                        } else {
                            //when multiply chests competed, minecraft will always combine with right side
                            if (placingBlock.getFace(nearByBlock) == Util.getRightSide(placingChestFacing)) {
                                chest = nearByBlock;
                            }
                        }
                    }
                }
            }
        }
        if (chest == null) {
            return;
        }

        Shop shop = getShopPlayer(chest.getLocation(), false);
        if (shop != null) {
            if (!QuickShop.getPermissionManager().hasPermission(player, "quickshop.create.double")) {
                e.setCancelled(true);
                MsgUtil.sendMessage(player,MsgUtil.getMessage("no-double-chests", player));

            } else if (!shop.getModerator().isModerator(player.getUniqueId())) {
                e.setCancelled(true);
                MsgUtil.sendMessage(player,MsgUtil.getMessage("not-managed-shop", player));
            }
        }
    }

}
