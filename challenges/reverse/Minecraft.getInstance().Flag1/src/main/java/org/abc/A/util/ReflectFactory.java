/*
 * This file is a part of project QuickShop, the name is ReflectFactory.java
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

package org.abc.A.util;

import org.bukkit.Bukkit;

import java.lang.reflect.Field;

public class ReflectFactory {
    private static String cachedVersion = null;
    public static String getServerVersion() {
        if(cachedVersion != null){
            return cachedVersion;
        }
        try {
            Field consoleField = Bukkit.getServer().getClass().getDeclaredField("console");
            consoleField.setAccessible(true); // protected
            Object console = consoleField.get(Bukkit.getServer()); // dedicated server
            cachedVersion = String.valueOf(
                console.getClass().getSuperclass().getMethod("getVersion").invoke(console));
            return cachedVersion;
        } catch (Exception e) {
            e.printStackTrace();
            cachedVersion = "Unknown";
            return cachedVersion;
        }
    }

}
