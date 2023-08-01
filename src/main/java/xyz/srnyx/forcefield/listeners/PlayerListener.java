package xyz.srnyx.forcefield.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.annoyingapi.AnnoyingListener;

import xyz.srnyx.forcefield.ForceField;
import xyz.srnyx.forcefield.ForcefieldManager;
import xyz.srnyx.forcefield.objects.ForcefieldOptions;


public class PlayerListener implements AnnoyingListener {
    @NotNull private final ForceField plugin;

    public PlayerListener(@NotNull ForceField plugin) {
        this.plugin = plugin;
    }

    @Override @NotNull
    public ForceField getAnnoyingPlugin() {
        return plugin;
    }

    /**
     * Called when a player joins a server
     */
    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        plugin.forcefields.put(player.getUniqueId(), new ForcefieldOptions(plugin, player));
    }

    /**
     * Called when a player leaves a server
     */
    @EventHandler
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        plugin.forcefields.remove(event.getPlayer().getUniqueId());
    }

    /**
     * Holds information for player movement events
     */
    @EventHandler
    public void onPlayerMove(@NotNull PlayerMoveEvent event) {
        final Player player = event.getPlayer();

        // Get options
        final ForcefieldOptions options = plugin.forcefields.get(player.getUniqueId());
        if (options == null || !options.enabled()) return;
        final ForcefieldManager manager = new ForcefieldManager(plugin, player);

        // Push entities
        manager.pushEntities();
        // Push blocks
        if (options.blocks()) manager.pushBlocks();
    }
}
