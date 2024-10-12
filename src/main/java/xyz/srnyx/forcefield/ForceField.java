package xyz.srnyx.forcefield;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import org.jetbrains.annotations.Nullable;
import xyz.srnyx.annoyingapi.AnnoyingPlugin;
import xyz.srnyx.annoyingapi.PluginPlatform;

import xyz.srnyx.forcefield.objects.ForceFieldConfig;
import xyz.srnyx.forcefield.objects.ForcefieldOptions;

import java.util.*;


public class ForceField extends AnnoyingPlugin {
    @NotNull public static final Set<String> COLUMNS = new HashSet<>(Arrays.asList("ff_enabled", "ff_inverse", "ff_mobs", "ff_blocks", "ff_special", "ff_radius", "ff_strength"));

    public ForceFieldConfig config;
    @NotNull public final Map<UUID, ForcefieldOptions> forcefields = new HashMap<>();
    @Nullable public BukkitTask task;

    public ForceField() {
        options
                .pluginOptions(pluginOptions -> pluginOptions.updatePlatforms(
                        PluginPlatform.modrinth("forcefield"),
                        PluginPlatform.hangar(this, "srnyx"),
                        PluginPlatform.spigot("107048")))
                .bStatsOptions(bStatsOptions -> bStatsOptions.id(18869))
                .dataOptions(dataOptions -> dataOptions
                        .enabled(true)
                        .entityDataColumns(COLUMNS))
                .registrationOptions
                .automaticRegistration(automaticRegistration -> automaticRegistration.packages(
                        "xyz.srnyx.forcefield.commands",
                        "xyz.srnyx.forcefield.listeners"))
                .papiExpansionToRegister(() -> new ForcefieldPlaceholders(this));
    }

    @Override
    public void enable() {
        reload();
    }

    @Override
    public void reload() {
        config = new ForceFieldConfig(this);
        forcefields.clear();
        Bukkit.getOnlinePlayers().forEach(player -> forcefields.put(player.getUniqueId(), new ForcefieldOptions(this, player)));

        // Start runnable
        if (task != null) task.cancel();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                for (final ForcefieldOptions options : forcefields.values()) if (options.enabled) {
                    final ForcefieldManager manager = new ForcefieldManager(ForceField.this, options.player);
                    // Push entities
                    manager.pushEntities();
                    // Push blocks
                    if (options.blocks) manager.pushBlocks();
                }
            }
        }.runTaskTimer(this, 0, 1);
    }

    /**
     * Gets the {@link ForcefieldOptions} instance for the specified player. If they don't have one, a new one will be created
     *
     * @param   player  the {@link Player}
     *
     * @return          the {@link ForcefieldOptions} instance of the specified {@link Player}
     */
    @NotNull
    public ForcefieldOptions getOptions(@NotNull Player player) {
        final ForcefieldOptions options = forcefields.get(player.getUniqueId());
        if (options != null) return options;
        final ForcefieldOptions newOptions = new ForcefieldOptions(this, player);
        forcefields.put(player.getUniqueId(), newOptions);
        return newOptions;
    }
}
