package xyz.srnyx.forcefield;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.annoyingapi.AnnoyingPlugin;
import xyz.srnyx.annoyingapi.file.AnnoyingResource;

import xyz.srnyx.forcefield.commands.ForcefieldCommand;
import xyz.srnyx.forcefield.listeners.PlayerListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class ForceField extends AnnoyingPlugin {
    public boolean defaultMobs;
    public double defaultRadius;
    public double defaultStrength;
    @NotNull public final Map<UUID, ForcefieldOptions> forcefields = new HashMap<>();

    public ForceField() {
        super();

        // Options
        options.colorLight = ChatColor.GREEN;
        options.colorDark = ChatColor.DARK_GREEN;
        options.commands.add(new ForcefieldCommand(this));
        options.listeners.add(new PlayerListener(this));
    }

    @Override
    public void enable() {
        // Load config
        final AnnoyingResource config = new AnnoyingResource(this, "config.yml");
        defaultMobs = config.getBoolean("default.mobs");
        defaultRadius = config.getDouble("default.radius");
        defaultStrength = config.getDouble("default.strength");
        // Load data
        Bukkit.getOnlinePlayers().forEach(player -> new ForcefieldOptions(this, player.getUniqueId()));
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
        ForcefieldOptions options = forcefields.get(player.getUniqueId());
        if (options == null) options = new ForcefieldOptions(this, player.getUniqueId());
        return options;
    }
}
