/*
 *
 * PAPIDate
 * Copyright (C) 2026 Sebastian Software Studios
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 */

package com.sebastiangrupo.studios.papidate;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class PAPIDate extends PlaceholderExpansion {

    private static final long TICKS_PER_DAY = 24000L;

    @Override
    public @NotNull String getIdentifier() {
        return "papidate";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Sebastian Software Studios";
    }

    @Override
    public @NotNull String getVersion() {
        return "0.3.2";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier) {

        if (identifier.startsWith("world_")) {
            String worldName = identifier.substring("world_".length());
            World world = Bukkit.getWorld(worldName);
            if (world == null) return "Invalid world";
            long days = world.getFullTime() / TICKS_PER_DAY;
            LocalDate localDate = LocalDate.of(0, 1, 1).plusDays(days);
            Date date = Date.from(
                    localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
            );
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String formattedDate = dateFormat.format(date);

            String[] parts = formattedDate.split("/");
            if (parts.length == 3) {
                String year = parts[2].replaceFirst("^0+(?!$)", "");
                return parts[0] + "/" + parts[1] + "/" + year;
            }
            return formattedDate;
        }

        return null;
    }
}

