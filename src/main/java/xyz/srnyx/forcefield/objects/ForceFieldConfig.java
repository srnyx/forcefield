package xyz.srnyx.forcefield.objects;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.annoyingapi.AnnoyingPlugin;
import xyz.srnyx.annoyingapi.file.AnnoyingResource;

import xyz.srnyx.forcefield.ForceField;
import xyz.srnyx.forcefield.SpecialForcefield;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;


/**
 * Represents the plugin's {@code config.yml}
 */
public class ForceFieldConfig {
    @NotNull public final Default defaults;
    @NotNull public final Mobs mobs;
    @NotNull public final EntityBlacklist entityBlacklist;
    @NotNull public final Blocks blocks;

    public ForceFieldConfig(@NotNull ForceField plugin) {
        final AnnoyingResource config = new AnnoyingResource(plugin, "config.yml");
        defaults = new Default(config.getConfigurationSection("defaults"));
        mobs = new Mobs(config.getConfigurationSection("mobs"));
        entityBlacklist = new EntityBlacklist(config.getConfigurationSection("entity-blacklist"));
        blocks = new Blocks(config.getConfigurationSection("blocks"));
    }

    public static class Default {
        public final boolean inverse;
        public final boolean mobs;
        public final double radius;
        public final double strength;
        @Nullable public final SpecialForcefield special;

        public Default(@Nullable ConfigurationSection section) {
            if (section == null) {
                inverse = false;
                mobs = false;
                radius = 5;
                strength = 0.5;
                special = null;
                return;
            }
            inverse = section.getBoolean("inverse", false);
            mobs = section.getBoolean("mobs", false);
            radius = section.getDouble("radius", 5);
            strength = section.getDouble("strength", 0.5);
            special = SpecialForcefield.matchSpecial(section.getString("special")).orElse(null);
        }
    }

    public static class Mobs {
        public final boolean requireEyesight;

        public Mobs(@Nullable ConfigurationSection section) {
            if (section == null) {
                requireEyesight = false;
                return;
            }
            requireEyesight = section.getBoolean("require-eyesight", false);
        }
    }

    public static class EntityBlacklist {
        @NotNull public final Set<EntityType> list;
        public final boolean treatAsWhitelist;

        public EntityBlacklist(@Nullable ConfigurationSection section) {
            if (section == null) {
                list = new HashSet<>();
                treatAsWhitelist = false;
                return;
            }
            list = section.getStringList("list").stream()
                    .map(string -> {
                        try {
                            return EntityType.valueOf(string.toUpperCase());
                        } catch (final IllegalArgumentException e) {
                            AnnoyingPlugin.log(Level.WARNING, "&eInvalid entity type for entity blacklist: &6" + string);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            treatAsWhitelist = section.getBoolean("treat-as-whitelist", false);
        }
    }

    public static class Blocks {
        public final boolean enabled;
        @NotNull public final Set<String> players;
        @NotNull public final Blacklist blacklist;

        public Blocks(@Nullable ConfigurationSection section) {
            if (section == null) {
                enabled = false;
                players = new HashSet<>();
                blacklist = new Blacklist(null);
                return;
            }
            enabled = section.getBoolean("enabled");
            players = new HashSet<>(section.getStringList("players"));
            blacklist = new Blacklist(section.getConfigurationSection("blacklist"));
        }

        public static class Blacklist {
            @NotNull public final Set<Material> list = new HashSet<>();
            public final boolean treatAsWhitelist;

            public Blacklist(@Nullable ConfigurationSection section) {
                if (section == null) {
                    treatAsWhitelist = true;
                    return;
                }
                for (final String string : section.getStringList("list")) {
                    final Material material = Material.matchMaterial(string);
                    if (material != null) list.add(material);
                }
                treatAsWhitelist = section.getBoolean("treat-as-whitelist", true);
            }
        }
    }
}
