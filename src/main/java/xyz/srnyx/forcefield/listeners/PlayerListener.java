package xyz.srnyx.forcefield.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import xyz.srnyx.annoyingapi.AnnoyingListener;

import xyz.srnyx.forcefield.ForceField;
import xyz.srnyx.forcefield.ForcefieldOptions;

import java.util.List;


public class PlayerListener implements AnnoyingListener {
    @NotNull private final ForceField plugin;

    @Contract(pure = true)
    public PlayerListener(@NotNull ForceField plugin) {
        this.plugin = plugin;
    }

    @Override @NotNull
    public ForceField getPlugin() {
        return plugin;
    }

    /**
     * Called when a player joins a server
     */
    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        new ForcefieldOptions(plugin, event.getPlayer().getUniqueId());
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
        if (options == null || !options.getEnabled()) return;

        // Get nearby entities
        final double radius = options.getRadius();
        final List<Entity> entities = player.getNearbyEntities(radius, radius, radius);

        // If mobs are disabled, remove non-players
        if (!options.getMobs()) entities.removeIf(entity -> !(entity instanceof Player));

        // Push entities away
        final Location playerLocation = player.getLocation();
        final double strength = options.getStrength();
        entities.forEach(nearby -> {
            final Vector vector = nearby.getLocation().subtract(playerLocation).toVector();
            if (vector.getX() != 0 || vector.getY() != 0 || vector.getZ() != 0) nearby.setVelocity(vector.normalize().multiply(strength));
        });
    }
}
