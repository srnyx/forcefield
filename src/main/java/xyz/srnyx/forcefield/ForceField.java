package xyz.srnyx.forcefield;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.annoyingapi.AnnoyingPlugin;

import xyz.srnyx.forcefield.commands.ForcefieldCommand;
import xyz.srnyx.forcefield.listeners.PlayerListener;
import xyz.srnyx.forcefield.objects.ForceFieldConfig;
import xyz.srnyx.forcefield.objects.ForcefieldOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class ForceField extends AnnoyingPlugin {
    @NotNull public ForceFieldConfig config = new ForceFieldConfig(this);
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
        reload();
    }

    @Override
    public void reload() {
        // Load config and data
        config = new ForceFieldConfig(this);
        forcefields.clear();
        Bukkit.getOnlinePlayers().forEach(player -> new ForcefieldOptions(this, player));
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
        if (options == null) options = new ForcefieldOptions(this, player);
        return options;
    }
}
