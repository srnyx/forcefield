package xyz.srnyx.forcefield.objects;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.annoyingapi.file.AnnoyingResource;

import xyz.srnyx.forcefield.ForceField;
import xyz.srnyx.forcefield.enums.SpecialForcefield;

import java.util.HashSet;
import java.util.Set;
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
        final AnnoyingResource configFile = new AnnoyingResource(plugin, "config.yml");

        defaultInverse = configFile.getBoolean("default.inverse");
        defaultSpecial = SpecialForcefield.getSpecial(configFile.getString("default.tornado"));
        defaultMobs = configFile.getBoolean("default.mobs");
        defaultRadius = configFile.getDouble("default.radius");
        defaultStrength = configFile.getDouble("default.strength");

        // Entities
        entityBlacklist = configFile.getStringList("entity-blacklist.list").stream()
                .map(EntityType::valueOf)
                .collect(Collectors.toSet());
        entityBlacklistTreatAsWhitelist = configFile.getBoolean("entity-blacklist.treat-as-whitelist");

        // Blocks
        blocksEnabled = configFile.getBoolean("blocks.enabled");
        blocksPlayers = new HashSet<>(configFile.getStringList("blocks.players"));
        blocksBlacklist = configFile.getStringList("blocks.blacklist.list").stream()
                .map(Material::valueOf)
                .collect(Collectors.toSet());
        blocksBlacklistTreatAsWhitelist = configFile.getBoolean("blocks.blacklist.treat-as-whitelist");
    }
}
