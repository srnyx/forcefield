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
    public final boolean defaultInverse;
    @Nullable public final SpecialForcefield defaultSpecial;
    public final boolean defaultMobs;
    public final double defaultRadius;
    public final double defaultStrength;
    @NotNull public final Set<EntityType> entityBlacklist;
    public final boolean entityBlacklistTreatAsWhitelist;
    public final boolean blocksEnabled;
    @NotNull public final Set<String> blocksPlayers;
    @NotNull public final Set<Material> blocksBlacklist;
    public final boolean blocksBlacklistTreatAsWhitelist;

    /**
     * Constructor for {@link ForceFieldConfig}
     *
     * @param   plugin  the {@link ForceField} instance
     */
    public ForceFieldConfig(@NotNull ForceField plugin) {
        final AnnoyingResource config = new AnnoyingResource(plugin, "config.yml");

        final ConfigurationSection defaultSection = config.getConfigurationSection("default");
        final boolean hasDefault = defaultSection != null;
        defaultInverse = hasDefault && defaultSection.getBoolean("inverse", false);
        defaultSpecial = hasDefault ? SpecialForcefield.matchSpecial(defaultSection.getString("tornado")) : null;
        defaultMobs = hasDefault && defaultSection.getBoolean("mobs", false);
        defaultRadius = hasDefault ? defaultSection.getDouble("radius", 5) : 5;
        defaultStrength = hasDefault ? defaultSection.getDouble("strength", 0.5) : 0.5;

        // Entities
        final ConfigurationSection entityBlacklistSection = config.getConfigurationSection("entity-blacklist");
        final boolean hasEntityBlacklist = entityBlacklistSection != null;
        entityBlacklist = hasEntityBlacklist ? entityBlacklistSection.getStringList("list").stream()
                .map(string -> {
                    try {
                        return EntityType.valueOf(string.toUpperCase());
                    } catch (final IllegalArgumentException e) {
                        AnnoyingPlugin.log(Level.WARNING, "&eInvalid entity type for entity blacklist: &6" + string);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()) : new HashSet<>();
        entityBlacklistTreatAsWhitelist = hasEntityBlacklist && entityBlacklistSection.getBoolean("treat-as-whitelist", false);

        // Blocks
        final ConfigurationSection blocksSection = config.getConfigurationSection("blocks");
        final boolean hasBlocks = blocksSection != null;
        blocksEnabled = hasBlocks && blocksSection.getBoolean("enabled");
        blocksPlayers = hasBlocks ? new HashSet<>(blocksSection.getStringList("players")) : new HashSet<>();
        blocksBlacklist = hasBlocks ? blocksSection.getStringList("blacklist.list").stream()
                .map(Material::matchMaterial)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()) : new HashSet<>();
        blocksBlacklistTreatAsWhitelist = !hasBlocks || blocksSection.getBoolean("blacklist.treat-as-whitelist", true);
    }
}
