package xyz.srnyx.forcefield;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.forcefield.objects.EntityPush;

import java.util.function.Consumer;


public enum SpecialForcefield {
    /**
     * Entities will be spun around the player (revolve around the player). If {@code inverse} is {@code true}, entities will be spun to the left.
     */
    TORNADO(push -> {
        final Entity entity = push.entity;
        final boolean inverse = push.inverse;

        Vector vector = entity.getLocation().subtract(push.location).toVector();
        final double y = vector.getY();
        if (inverse) vector.multiply(-1);

        // Tornado spin effect
        final Vector perpendicular = new Vector(-vector.getZ(), 0, vector.getX());
        final Vector inwardForce = vector.multiply(push.manager.inwardMultiple);
        if (inverse) inwardForce.multiply(-1);
        vector = perpendicular.add(inwardForce).setY(-y);

        entity.setVelocity(vector.normalize().multiply(push.manager.options.strength()));
    }),
    /**
     * Entities will be "held" in front of the player, {@code radius} blocks away. If {@code inverse} is {@code true}, entities will be "held" behind the player.
     */
    PICKUP(push -> {
        final Location location = push.location;
        location.add(0, 1.5, 0);
        final Vector vector = location.getDirection().multiply(push.manager.options.radius() - 1);
        final Location newLocation = push.inverse ? location.subtract(vector) : location.add(vector);
        push.manager.pushEntity(push.entity, newLocation, null, true);
    });

    /**
     * The {@link Consumer} that will be called when the {@link SpecialForcefield} is activated
     */
    @NotNull private final Consumer<EntityPush> consumer;

    /**
     * Creates a new {@link SpecialForcefield}
     *
     * @param   consumer    the {@link Consumer} that will be called when the {@link SpecialForcefield} is activated
     */
    SpecialForcefield(@NotNull Consumer<EntityPush> consumer) {
        this.consumer = consumer;
    }

    /**
     * Gets the {@link #consumer}
     *
     * @return  the {@link #consumer}
     */
    @NotNull
    public Consumer<EntityPush> getConsumer() {
        return consumer;
    }

    /**
     * Gets the {@link SpecialForcefield} from the given {@code name}. If the {@code name} is invalid, {@code null} will be returned.
     *
     * @param   name    the name of the {@link SpecialForcefield}
     *
     * @return          the {@link SpecialForcefield} with the given {@code name}, or {@code null} if the {@code name} is invalid
     */
    @Nullable
    public static SpecialForcefield matchSpecial(@Nullable String name) {
        if (name == null) return null;
        try {
            return SpecialForcefield.valueOf(name.toUpperCase());
        } catch (final IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Gets the name of the {@link SpecialForcefield}
     *
     * @param   special the {@link SpecialForcefield}
     *
     * @return          the name of the {@link SpecialForcefield}
     */
    @NotNull
    public static String getName(@Nullable SpecialForcefield special) {
        return special == null ? "NONE" : special.name();
    }
}
