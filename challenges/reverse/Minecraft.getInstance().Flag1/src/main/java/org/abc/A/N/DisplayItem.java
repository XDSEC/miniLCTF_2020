/*
 * This file is a part of project QuickShop, the name is DisplayItem.java
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

package org.abc.A.N;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;

import org.abc.A.util.Util;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.abc.A.QuickShop;

/**
 * @author Netherfoam A display item, that spawns a block above the chest and cannot be interacted
 * with.
 */
public abstract class DisplayItem {

    protected static final QuickShop plugin = QuickShop.instance;

    private static final Gson gson = new Gson();

    protected final ItemStack originalItemStack;

    protected final Shop shop;

    @Nullable
    protected ItemStack guardedIstack;

    private boolean pendingRemoval;

    protected DisplayItem(Shop shop) {
        this.shop = shop;
        this.originalItemStack = new ItemStack(shop.getItem());
        this.originalItemStack.setAmount(1);
    }


    /**
     * Check the itemStack is contains protect flag.
     *
     * @param itemStack Target ItemStack
     * @return Contains protect flag.
     */
    public static boolean checkIsGuardItemStack(@Nullable ItemStack itemStack) {

        if (!plugin.isDisplay()) {
            return false;
        }
        if (getNowUsing() == DisplayType.VIRTUALITEM) {
            return false;
        }

        if (itemStack == null) {
            return false;
        }
        //    itemStack = itemStack.clone();
        //    itemStack.setAmount(1);
        if (!itemStack.hasItemMeta()) {
            return false;
        }
        ItemMeta iMeta = itemStack.getItemMeta();
        if (!iMeta.hasLore()) {
            return false;
        }
        String defaultMark = ShopProtectionFlag.getDefaultMark();
        //noinspection ConstantConditions
        for (String lore : iMeta.getLore()) {
            try {
                if (!lore.startsWith("{")) {
                    continue;
                }
                ShopProtectionFlag shopProtectionFlag = gson.fromJson(lore, ShopProtectionFlag.class);
                if (shopProtectionFlag == null) {
                    continue;
                }
                if (defaultMark.equals(shopProtectionFlag.getMark())) {
                    return true;
                }
                if (shopProtectionFlag.getShopLocation() != null) {
                    return true;
                }
                if (shopProtectionFlag.getItemStackString() != null) {
                    return true;
                }
            } catch (JsonSyntaxException e) {
                // Ignore
            }
        }

        return false;
    }

    /**
     * Get plugin now is using which one DisplayType
     *
     * @return Using displayType.
     */
    public static DisplayType getNowUsing() {
        return DisplayType.fromID(QuickShop.instance.getConfig().getInt("shop.display-type"));
    }

    /**
     * Check the itemStack is target shop's display
     *
     * @param itemStack Target ItemStack
     * @param shop Target shop
     * @return Is target shop's display
     */
    public static boolean checkIsTargetShopDisplay(@NotNull ItemStack itemStack, @NotNull Shop shop) {
        if (!plugin.isDisplay()) {
            return false;
        }
        if (getNowUsing() == DisplayType.VIRTUALITEM) {
            return false;
        }

        if (!itemStack.hasItemMeta()) {
            return false;
        }
        ItemMeta iMeta = itemStack.getItemMeta();
        if (!iMeta.hasLore()) {
            return false;
        }
        String defaultMark = ShopProtectionFlag.getDefaultMark();
        String shopLocation = shop.getLocation().toString();
        //noinspection ConstantConditions
        for (String lore : iMeta.getLore()) {
            try {
                if (!lore.startsWith("{")) {
                    continue;
                }
                ShopProtectionFlag shopProtectionFlag = gson.fromJson(lore, ShopProtectionFlag.class);
                if (shopProtectionFlag == null) {
                    continue;
                }
                if (!shopProtectionFlag.getMark().equals(defaultMark)) {
                    continue;
                }
                if (shopProtectionFlag.getShopLocation().equals(shopLocation)) {
                    return true;
                }
            } catch (JsonSyntaxException e) {
                // Ignore
            }
        }
        return false;
    }

    /**
     * Create a new itemStack with protect flag.
     *
     * @param itemStack Old itemStack
     * @param shop The shop
     * @return New itemStack with protect flag.
     */
    public static ItemStack createGuardItemStack(@NotNull ItemStack itemStack, @NotNull Shop shop) {
        itemStack = new ItemStack(itemStack);
        itemStack.setAmount(1);
        ItemMeta iMeta = itemStack.getItemMeta();
        if (QuickShop.instance.getConfig().getBoolean("shop.display-item-use-name")) {
            if (iMeta.hasDisplayName()) {
                iMeta.setDisplayName(iMeta.getDisplayName());
            } else {
                iMeta.setDisplayName(Util.getItemStackName(itemStack));
            }
        } else {
            iMeta.setDisplayName(null);
        }
        java.util.List<String> lore = new ArrayList<>();
        ShopProtectionFlag shopProtectionFlag = createShopProtectionFlag(itemStack, shop);
        String protectFlag = gson.toJson(shopProtectionFlag);
        for (int i = 0; i < 21; i++) {
            lore.add(
                protectFlag); // Create 20 lines lore to make sure no stupid plugin accident remove mark.
        }
        iMeta.setLore(lore);
        itemStack.setItemMeta(iMeta);
        return itemStack;
    }

    /**
     * Create the shop protection flag for display item.
     *
     * @param itemStack The item stack
     * @param shop The shop
     * @return ShopProtectionFlag obj
     */
    public static ShopProtectionFlag createShopProtectionFlag(
        @NotNull ItemStack itemStack, @NotNull Shop shop) {
        return new ShopProtectionFlag(shop.getLocation().toString(), Util.serialize(itemStack));
    }

    /**
     * Check the display is or not moved.
     *
     * @return Moved
     */
    public abstract boolean checkDisplayIsMoved();

    /**
     * Check the display is or not need respawn
     *
     * @return Need
     */
    public abstract boolean checkDisplayNeedRegen();

    /**
     * Check target Entity is or not a QuickShop display Entity.
     *
     * @param entity Target entity
     * @return Is or not
     */
    public abstract boolean checkIsShopEntity(Entity entity);

    /**
     * Fix the display moved issue.
     */
    public abstract void fixDisplayMoved();

    /**
     * Fix display need respawn issue.
     */
    public abstract void fixDisplayNeedRegen();

    /**
     * Remove the display entity.
     */
    public abstract void remove();

    /**
     * Remove this shop's display in the whole world.(Not whole server)
     *
     * @return Success
     */
    public abstract boolean removeDupe();

    /**
     * Respawn the displays, if it not exist, it will spawn new one.
     */
    public abstract void respawn();

    /**
     * Add the protect flags for entity or entity's hand item. Target entity will got protect by
     * QuickShop
     *
     * @param entity Target entity
     */
    public abstract void safeGuard(@NotNull Entity entity);

    /**
     * Spawn new Displays
     */
    public abstract void spawn();

    /**
     * Get the display entity
     *
     * @return Target entity
     */
    public abstract Entity getDisplay();

    /**
     * Get display should at location. Not display current location.
     *
     * @return Should at
     */
    public abstract Location getDisplayLocation();

    /**
     * Check the display is or not already spawned
     *
     * @return Spawned
     */
    public abstract boolean isSpawned();

    public void pendingRemoval() {
        pendingRemoval = true;
    }

    public boolean isPendingRemoval() {
        return pendingRemoval;
    }

}
