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
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.IsoFields;
import java.util.Locale;
import java.util.Set;

public class PAPIDate extends PlaceholderExpansion {
    private static final long TICKS_PER_DAY = 24000L;
    private static final long TICKS_AT_MIDNIGHT = 18000L;
    private static final LocalDate EPOCH = LocalDate.of(0, 1, 1);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/u");
    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ofPattern("uuuu-MM-dd");
    private static final DateTimeFormatter ISO_DATE_BASIC = DateTimeFormatter.ofPattern("uuuuMMdd");
    private static final DateTimeFormatter ISO_DATETIME = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss");
    private static final DateTimeFormatter ISO_DATETIME_BASIC = DateTimeFormatter.ofPattern("uuuuMMdd'T'HHmmss");
    private static final DateTimeFormatter ISO_WEEK = new DateTimeFormatterBuilder()
            .appendValue(IsoFields.WEEK_BASED_YEAR, 4)
            .appendLiteral("-W")
            .appendValue(IsoFields.WEEK_OF_WEEK_BASED_YEAR, 2)
            .toFormatter();
    private static final DateTimeFormatter ISO_WEEK_DAY = new DateTimeFormatterBuilder()
            .appendValue(IsoFields.WEEK_BASED_YEAR, 4)
            .appendLiteral("-W")
            .appendValue(IsoFields.WEEK_OF_WEEK_BASED_YEAR, 2)
            .appendLiteral("-")
            .appendValue(ChronoField.DAY_OF_WEEK, 1)
            .toFormatter();

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter TIME12_FORMAT = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);

    private static final Set<String> DATE_FORMATS = Set.of("numeric", "iso", "isobasic", "week", "weekday");

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
        return "0.3.3";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier) {

        final int separator = identifier.indexOf('_');
        if (separator < 0) {
            return null;
        }

        final String type = identifier.substring(0, separator);
        final String rest = identifier.substring(separator + 1);
        if (rest.isEmpty()) {
            return null;
        }

        final boolean supportsFormat = type.equals("date") || type.equals("world")
                || type.equals("datetime") || type.equals("datetime12");

        String format = "numeric";
        String worldName = rest;
        if (supportsFormat) {
            final int fmtSep = rest.indexOf('_');
            if (fmtSep >= 0) {
                final String maybeFormat = rest.substring(0, fmtSep);
                if (DATE_FORMATS.contains(maybeFormat)) {
                    format = maybeFormat;
                    worldName = rest.substring(fmtSep + 1);
                }
            }
        }
        if (worldName.isEmpty()) {
            return null;
        }

        final World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return "Invalid world";
        }

        switch (type) {
            case "date":
            case "world":
                return formatDate(dateOf(world), format);
            case "datetime":
                return formatDateTime(dateOf(world), clockOf(world), format);
            case "datetime12":
                return formatDate(dateOf(world), format) + " " + formatTime12(world);
            case "time":
                return formatTime24(world);
            case "time12":
                return formatTime12(world);
            case "ticks":
                return Long.toString(world.getFullTime());
            case "timetick":
                return Long.toString(world.getTime() % TICKS_PER_DAY);
            default:
                return null;
        }
    }

    private LocalDate dateOf(final World world) {
        final long adjustedTicks = world.getFullTime() - TICKS_AT_MIDNIGHT + TICKS_PER_DAY;
        final long days = adjustedTicks / TICKS_PER_DAY;
        return EPOCH.plusDays(days);
    }

    private String formatDate(final LocalDate date, final String format) {
        switch (format) {
            case "iso":
                return date.format(ISO_DATE);
            case "isobasic":
                return date.format(ISO_DATE_BASIC);
            case "week":
                return date.format(ISO_WEEK);
            case "weekday":
                return date.format(ISO_WEEK_DAY);
            case "numeric":
            default:
                return date.format(DATE_FORMAT);
        }
    }

    private String formatDateTime(final LocalDate date, final LocalTime time, final String format) {
        switch (format) {
            case "iso":
                return LocalDateTime.of(date, time).format(ISO_DATETIME);
            case "isobasic":
                return LocalDateTime.of(date, time).format(ISO_DATETIME_BASIC);
            default:

                return formatDate(date, format) + " " + time.format(TIME_FORMAT);
        }
    }

    private String formatTime24(final World world) {
        return clockOf(world).format(TIME_FORMAT);
    }
    private String formatTime12(final World world) {
        return clockOf(world).format(TIME12_FORMAT);
    }

    private LocalTime clockOf(final World world) {
        final long ticksFromMidnight =
                (world.getTime() - TICKS_AT_MIDNIGHT + TICKS_PER_DAY) % TICKS_PER_DAY;
        final long secondsOfDay = ticksFromMidnight * 18L / 5L;
        return LocalTime.ofSecondOfDay(secondsOfDay);
    }
}
