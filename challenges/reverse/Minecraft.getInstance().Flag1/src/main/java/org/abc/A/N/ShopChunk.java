/*
 * This file is a part of project QuickShop, the name is ShopChunk.java
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

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.World;

@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class ShopChunk {
    private String world;

    private int x;

    private int z;

    public boolean isSame(World world, int x, int z) {
        return isSame(world.getName(), x, z);
    }

    public boolean isSame(String world, int x, int z) {
        return this.x == x && this.z == z && this.world.equals(world);
    }

}
