package xyz.srnyx.forcefield.objects;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.annoyingapi.AnnoyingPlugin;
import xyz.srnyx.annoyingapi.file.AnnoyingResource;

import xyz.srnyx.forcefield.ForceField;
import xyz.srnyx.forcefield.SpecialForcefield;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;


/**
 * Represents the plugin's {@code config.yml}
 */
public class ForceFieldConfig {
    @NotNull private final AnnoyingResource config;
    @NotNull public final Default defaults;
    @NotNull public final Mobs mobs;
    @NotNull public final EntityBlacklist entityBlacklist;
    @NotNull public final Blocks blocks;

    public ForceFieldConfig(@NotNull ForceField plugin) {
        config = new AnnoyingResource(plugin, "config.yml");
        defaults = new Default();
        mobs = new Mobs();
        entityBlacklist = new EntityBlacklist();
        blocks = new Blocks();
    }

    public class Default {
        public final boolean inverse = config.getBoolean("defaults.inverse", false);
        public final boolean mobs = config.getBoolean("defaults.mobs", false);
        public final double radius = config.getDouble("defaults.radius", 5);
        public final double strength = config.getDouble("defaults.strength", 0.5);
        @Nullable public final SpecialForcefield special = SpecialForcefield.matchSpecial(config.getString("defaults.special")).orElse(null);
    }

    public class Mobs {
        public final boolean requireEyesight = config.getBoolean("mobs.require-eyesight", false);
    }

    public class EntityBlacklist {
        @NotNull public final Set<EntityType> list = new HashSet<>();
        public final boolean treatAsWhitelist = config.getBoolean("entity-blacklist.treat-as-whitelist", false);

        public EntityBlacklist() {
            for (final String string : config.getStringList("entity-blacklist.list")) try {
                list.add(EntityType.valueOf(string.toUpperCase()));
            } catch (final IllegalArgumentException e) {
                AnnoyingPlugin.log(Level.WARNING, "&eInvalid entity type for entity blacklist: &6" + string);
            }
        }
    }

    public class Blocks {
        public final boolean enabled = config.getBoolean("blocks.enabled", false);
        @NotNull public final Set<String> players = new HashSet<>(config.getStringList("blocks.players"));
        @NotNull public final Blacklist blacklist = new Blacklist();

        public class Blacklist {
            @NotNull public final Set<Material> list = new HashSet<>();
            public final boolean treatAsWhitelist = config.getBoolean("blocks.blacklist.treat-as-whitelist", true);

            public Blacklist() {
                for (final String string : config.getStringList("blocks.blacklist.list")) {
                    final Material material = Material.matchMaterial(string);
                    if (material != null) list.add(material);
                }
            }
        }
    }
}
