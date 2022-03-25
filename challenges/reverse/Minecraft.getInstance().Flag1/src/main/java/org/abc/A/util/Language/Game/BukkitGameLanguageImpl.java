package org.abc.A.util.Language.Game;

import org.abc.A.QuickShop;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class BukkitGameLanguageImpl extends InternalGameLanguageImpl implements GameLanguage {
    private QuickShop plugin;

    public BukkitGameLanguageImpl(@NotNull QuickShop plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getName() {
        return "Bukkit";
    }

    @Override
    public @NotNull Plugin getPlugin() {
        return plugin;
    }

    @Override
    public @NotNull String getItem(@NotNull ItemStack itemStack) {
        if(itemStack.getItemMeta() == null){
            return super.getItem(itemStack);
        }
        return itemStack.getItemMeta().getLocalizedName();
    }

    @Override
    public @NotNull String getItem(@NotNull Material material) {
        return super.getItem(material);
    }

    @Override
    public @NotNull String getPotion(@NotNull PotionEffectType potionEffectType) {
        return super.getPotion(potionEffectType);
    }

    @Override
    public @NotNull String getEnchantment(@NotNull Enchantment enchantment) {
        return super.getEnchantment(enchantment);
    }

    @Override
    public @NotNull String getEntity(@NotNull EntityType entityType) {
        return super.getEntity(entityType);
    }
}
