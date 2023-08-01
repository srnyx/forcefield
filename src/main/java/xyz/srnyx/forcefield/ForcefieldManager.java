package xyz.srnyx.forcefield;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.forcefield.objects.EntityPush;
import xyz.srnyx.forcefield.objects.ForcefieldOptions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;


public class ForcefieldManager {
    @NotNull private final ForceField plugin;
    @NotNull private final Player player;
    @NotNull public final ForcefieldOptions options;
    public final double inwardMultiple;

    /**
     * Creates a new {@link ForcefieldManager} instance
     *
     * @param   plugin  the {@link ForceField} instance
     * @param   player  the {@link Player} instance
     */
    @Contract(pure = true)
    public ForcefieldManager(@NotNull ForceField plugin, @NotNull Player player) {
        this.plugin = plugin;
        this.player = player;
        this.options = plugin.forcefields.get(player.getUniqueId());
        final double radius = options.radius() == 0 ? 1 : options.radius() - 1;
        this.inwardMultiple = -0.4 / radius;
    }

    /**
     * Pushes nearby entities
     */
    public void pushEntities() {
        // Get nearby entities
        final Set<Entity> entities = getNearbyEntities();
        if (entities == null || entities.isEmpty()) return;

        // Push entities away
        entities.forEach(entity -> pushEntity(entity, player.getLocation(), options.special(), options.inverse()));
    }

    /**
     * Push nearby blocks
     */
    public void pushBlocks() {
        if (options.cantUseBlocks()) return;

        // Get nearby blocks
        final Set<Block> blocks = getNearbyBlocks();
        if (blocks == null || blocks.isEmpty()) return;

        // Push blocks away
        final Predicate<Location> predicate = getPredicate();
        final World world = player.getWorld();
        blocks.forEach(block -> {
            // Get blockLocation
            final Location blockLocation = block.getLocation();
            blockLocation.setX(blockLocation.getX() + 0.5);
            blockLocation.setY(blockLocation.getY() + 0.5);
            blockLocation.setZ(blockLocation.getZ() + 0.5);
            if (predicate.test(blockLocation)) return;

            // Spawn falling block
            final FallingBlock fallingBlock = world.spawnFallingBlock(blockLocation, block.getType(), (byte) 0);

            // Set properties
            fallingBlock.setDropItem(false);
            fallingBlock.setHurtEntities(false);

            // Set velocity
            pushEntity(fallingBlock, player.getLocation(), options.special(), options.inverse());

            // Remove source block
            block.setType(Material.AIR);
        });
    }

    /**
     * Pushes an entity
     *
     * @param   entity      the {@link Entity} instance
     * @param   location    the center {@link Location} of the forcefield
     * @param   special     the {@link SpecialForcefield} instance
     * @param   inverse     whether the forcefield is inverse
     */
    public void pushEntity(@NotNull Entity entity, @NotNull Location location, @Nullable SpecialForcefield special, boolean inverse) {
        // Normal forcefield
        if (special == null) {
            final Vector vector = entity.getLocation().subtract(location).toVector();
            if (inverse) vector.multiply(-1);
            entity.setVelocity(vector.normalize().multiply(options.strength()));
            return;
        }

        // Special forcefield
        special.getConsumer().accept(new EntityPush(this, entity, location, inverse));
    }

    /**
     * Gets nearby entities of the {@link #player}
     *
     * @return  the {@link Set} of nearby {@link Entity}s
     */
    @Nullable
    public Set<Entity> getNearbyEntities() {
        // Variables
        final boolean mobs = options.mobs();
        final Set<EntityType> blacklist = plugin.config.entityBlacklist;
        final boolean treatAsWhiteList = plugin.config.entityBlacklistTreatAsWhitelist;
        if (mobs && treatAsWhiteList && blacklist.isEmpty()) return null;

        // Get nearby entities
        final double radius = options.radius();
        final List<Entity> entities = player.getNearbyEntities(radius, radius, radius);
        if (entities.isEmpty() || (treatAsWhiteList && blacklist.isEmpty())) return null;

        // Mobs option and blacklist/whitelist
        final Predicate<Location> predicate = getPredicate();
        entities.removeIf(entity -> {
            final EntityType type = entity.getType();
            if (type.equals(EntityType.PLAYER)) {
                if (entity.hasPermission("forcefield.bypass")) return true; // remove players with bypass permission
            } else if (!mobs) return true; // remove non-players (mobs) if mobs option is false
            if (predicate.test(entity.getLocation()) || entity.hasMetadata("NPC")) return true; // prevent "x not finite" error and remove NPCs
            if (treatAsWhiteList) return !blacklist.contains(type); // remove entities not in whitelist
            return blacklist.contains(type); // remove entities in blacklist
        });

        // Return entities
        return new HashSet<>(entities);
    }

    /**
     * Gets nearby blocks of the {@link #player}
     *
     * @return  the {@link Set} of nearby {@link Block}s
     */
    @Nullable
    public Set<Block> getNearbyBlocks() {
        // Get predicate
        final Set<Material> blacklist = plugin.config.blocksBlacklist;
        final Predicate<Material> predicate;
        if (plugin.config.blocksBlacklistTreatAsWhitelist) {
            // Whitelist
            if (blacklist.isEmpty()) return null; // remove all blocks
            predicate = blacklist::contains; // remove blocks not in whitelist
        } else {
            // Blacklist
            if (blacklist.isEmpty()) {
                predicate = material -> true; // remove no blocks
            } else {
                predicate = material -> !blacklist.contains(material); // remove blocks in blacklist
            }
        }

        // Get nearby blocks
        final Set<Block> blocks = new HashSet<>();
        final Location location = player.getLocation();
        final int radius = (int) options.radius();
        for (int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
            for (int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
                for (int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                    final Block block = location.getWorld().getBlockAt(x, y, z);
                    final Material material = block.getType();
                    if (material.isSolid() && predicate.test(material)) blocks.add(block);
                }
            }
        }
        return blocks;
    }

    /**
     * Gets the {@link Predicate} for the {@link #player}
     *
     * @return  the {@link Predicate} for the {@link #player}
     */
    @NotNull
    public Predicate<Location> getPredicate() {
        final Location playerLocation = player.getLocation();
        return location -> playerLocation.getX() == location.getX() && playerLocation.getY() == location.getY() && playerLocation.getZ() == location.getZ();
    }
}
