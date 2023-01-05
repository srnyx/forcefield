package xyz.srnyx.forcefield.objects;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import xyz.srnyx.forcefield.managers.ForcefieldManager;


/**
 * An object containing information when an {@link Entity} is pushed
 */
public class EntityPush {
    @NotNull public final ForcefieldManager manager;
    @NotNull public final Entity entity;
    @NotNull public final Location location;
    public final boolean inverse;

    /**
     * Constructor for {@link EntityPush}
     *
     * @param   manager     the {@link ForcefieldManager} instance
     * @param   entity      the {@link Entity} being pushed
     * @param   location    the {@link Location} of the center of the forcefield
     * @param   inverse     whether the forcefield is inverted
     */
    @Contract(pure = true)
    public EntityPush(@NotNull ForcefieldManager manager, @NotNull Entity entity, @NotNull Location location, boolean inverse) {
        this.manager = manager;
        this.entity = entity;
        this.location = location;
        this.inverse = inverse;
    }
}
