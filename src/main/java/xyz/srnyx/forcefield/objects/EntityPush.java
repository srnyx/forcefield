package xyz.srnyx.forcefield.objects;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.forcefield.ForcefieldManager;


/**
 * An object containing information when an {@link Entity} is pushed
 */
public class EntityPush {
    /**
     * The {@link ForcefieldManager} instance
     */
    @NotNull public final ForcefieldManager manager;
    /**
     * The {@link Entity} being pushed
     */
    @NotNull public final Entity entity;
    /**
     * The {@link Location} of the center of the forcefield
     */
    @NotNull public final Location location;
    /**
     * Whether the forcefield is inverted
     */
    public final boolean inverse;

    /**
     * Constructor for {@link EntityPush}
     *
     * @param   manager     {@link #manager}
     * @param   entity      {@link #entity}
     * @param   location    {@link #location}
     * @param   inverse     {@link #inverse}
     */
    public EntityPush(@NotNull ForcefieldManager manager, @NotNull Entity entity, @NotNull Location location, boolean inverse) {
        this.manager = manager;
        this.entity = entity;
        this.location = location;
        this.inverse = inverse;
    }
}
