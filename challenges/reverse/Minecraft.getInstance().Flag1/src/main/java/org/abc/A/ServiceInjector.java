package org.abc.A;

import org.abc.A.E.DatabaseCore;
import org.abc.A.F.EconomyCore;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.abc.A.util.Language.Game.GameLanguage;
import org.abc.A.util.matcher.item.ItemMatcher;

public class ServiceInjector {
    public static EconomyCore getEconomyCore(@NotNull EconomyCore def){
        @Nullable RegisteredServiceProvider<? extends EconomyCore> registeredServiceProvider =
                Bukkit.getServicesManager().getRegistration(EconomyCore.class);
        if(registeredServiceProvider == null){
            return def;
        }else{
            return registeredServiceProvider.getProvider();
        }
    }
    public static ItemMatcher getItemMatcher(@NotNull ItemMatcher def){
        @Nullable RegisteredServiceProvider<? extends ItemMatcher> registeredServiceProvider =
                Bukkit.getServicesManager().getRegistration(ItemMatcher.class);
        if(registeredServiceProvider == null){
            return def;
        }else{
            return registeredServiceProvider.getProvider();
        }
    }
    public static GameLanguage getGameLanguage(@NotNull GameLanguage def){
        @Nullable RegisteredServiceProvider<? extends GameLanguage> registeredServiceProvider =
                Bukkit.getServicesManager().getRegistration(GameLanguage.class);
        if(registeredServiceProvider == null){
            return def;
        }else{
            return registeredServiceProvider.getProvider();
        }
    }
    public static DatabaseCore getDatabaseCore(@NotNull DatabaseCore def){
        @Nullable RegisteredServiceProvider<? extends DatabaseCore> registeredServiceProvider =
                Bukkit.getServicesManager().getRegistration(DatabaseCore.class);
        if(registeredServiceProvider == null){
            return def;
        }else{
            return registeredServiceProvider.getProvider();
        }
    }
}
