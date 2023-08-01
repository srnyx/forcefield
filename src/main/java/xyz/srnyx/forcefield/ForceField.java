package xyz.srnyx.forcefield;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.annoyingapi.AnnoyingPlugin;
import xyz.srnyx.annoyingapi.PluginPlatform;

import xyz.srnyx.forcefield.objects.ForceFieldConfig;
import xyz.srnyx.forcefield.objects.ForcefieldOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class ForceField extends AnnoyingPlugin {
    public ForceFieldConfig config;
    @NotNull public final Map<UUID, ForcefieldOptions> forcefields = new HashMap<>();

    public ForceField() {
        options
                .pluginOptions(pluginOptions -> pluginOptions.updatePlatforms(
                        PluginPlatform.modrinth("forcefield"),
                        PluginPlatform.hangar(this, "srnyx"),
                        PluginPlatform.spigot("107048")))
                .bStatsOptions(bStatsOptions -> bStatsOptions.id(18869))
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
