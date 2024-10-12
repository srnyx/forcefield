package xyz.srnyx.forcefield.objects;

import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.annoyingapi.command.AnnoyingSender;
import xyz.srnyx.annoyingapi.data.EntityData;
import xyz.srnyx.annoyingapi.libs.javautilities.manipulation.Mapper;
import xyz.srnyx.annoyingapi.message.AnnoyingMessage;
import xyz.srnyx.annoyingapi.message.DefaultReplaceType;

import xyz.srnyx.forcefield.ForceField;
import xyz.srnyx.forcefield.SpecialForcefield;


/**
 * An object containing the options for the {@link #player}'s forcefield
 */
public class ForcefieldOptions {
    @NotNull private final ForceField plugin;
    @NotNull private final EntityData data;
    @NotNull public final Player player;
    public boolean enabled;
    public boolean inverse;
    public boolean mobs;
    public boolean blocks;
    public double radius;
    public double strength;
    @Nullable public SpecialForcefield special;

    /**
     * Creates a new {@link ForcefieldOptions} instance and adds it to {@link ForceField#forcefields}
     *
     * @param   plugin  the plugin instance
     * @param   player  the player
     */
    public ForcefieldOptions(@NotNull ForceField plugin, @NotNull Player player) {
        this.plugin = plugin;
        data = new EntityData(plugin, player);
        this.player = player;

        // Old data conversion
        data.convertOldData(ForceField.COLUMNS);

        // Get options
        enabled = data.has("ff_enabled");
        inverse = Boolean.parseBoolean(data.get("ff_inverse", String.valueOf(plugin.config.defaults.inverse)));
        mobs = Boolean.parseBoolean(data.get("ff_mobs", String.valueOf(plugin.config.defaults.mobs)));
        blocks = data.has("ff_blocks");
        radius = Mapper.toDouble(data.get("ff_radius")).orElse(plugin.config.defaults.radius);
        strength = Mapper.toDouble(data.get("ff_strength")).orElse(plugin.config.defaults.strength);
        special = SpecialForcefield.matchSpecial(data.get("ff_special")).orElse(plugin.config.defaults.special);
    }

    @NotNull
    public String getSpecialName() {
        return special == null ? "NONE" : special.name();
    }

    /**
     * Sets {@link #enabled}
     *
     * @param   enabled the new value
     * @param   sender  the {@link AnnoyingSender} who's setting the value
     */
    public void enabled(boolean enabled, @NotNull AnnoyingSender sender) {
        this.enabled = enabled;
        data.set("ff_enabled", enabled ? true : null);
        new AnnoyingMessage(plugin, "command.toggle.set")
                .replace("%state%", enabled, DefaultReplaceType.BOOLEAN)
                .replace("%target%", player.getName())
                .send(sender);
    }

    /**
     * Sets {@link #inverse}
     *
     * @param   inverse the new value
     * @param   sender  the {@link AnnoyingSender} who's setting the value
     */
    public void inverse(boolean inverse, @NotNull AnnoyingSender sender) {
        this.inverse = inverse;
        data.set("ff_inverse", inverse);
        new AnnoyingMessage(plugin, "command.inverse.set")
                .replace("%state%", inverse, DefaultReplaceType.BOOLEAN)
                .replace("%target%", player.getName())
                .send(sender);
    }

    /**
     * Sets {@link #special}
     *
     * @param   special the new value
     * @param   sender  the {@link AnnoyingSender} who's setting the value
     */
    public void special(@Nullable String special, @NotNull AnnoyingSender sender) {
        final SpecialForcefield specialEnum = SpecialForcefield.matchSpecial(special).orElse(null);
        if (specialEnum != null && !sender.checkPermission("forcefield.special." + specialEnum.name().toLowerCase())) return;

        // Set special
        this.special = specialEnum;
        final String name = getSpecialName();
        data.set("ff_special", name);

        // Send message
        new AnnoyingMessage(plugin, "command.special.set")
                .replace("%special%", name)
                .replace("%target%", player.getName())
                .send(sender);
    }

    /**
     * Sets {@link #mobs}
     *
     * @param   mobs    the new value
     * @param   sender  the {@link AnnoyingSender} who's setting the value
     */
    public void mobs(boolean mobs, @NotNull AnnoyingSender sender) {
        this.mobs = mobs;
        data.set("ff_mobs", mobs);
        new AnnoyingMessage(plugin, "command.mobs.set")
                .replace("%state%", mobs, DefaultReplaceType.BOOLEAN)
                .replace("%target%", player.getName())
                .send(sender);
    }

    /**
     * Sets {@link #blocks}
     *
     * @param   blocks  the new value
     * @param   sender  the {@link AnnoyingSender} who's setting the value
     */
    public void blocks(boolean blocks, @NotNull AnnoyingSender sender) {
        // Check if player has permission
        if (!sender.checkPermission("forcefield.command.blocks")) return;

        // Check if config allows blocks and list contains player
        if (cantUseBlocks()) {
            new AnnoyingMessage(plugin, "command.blocks.error").send(sender);
            return;
        }

        // Set blocks
        this.blocks = blocks;
        data.set("ff_blocks", blocks ? true : null);

        // Send message
        new AnnoyingMessage(plugin, "command.blocks.set")
                .replace("%state%", blocks, DefaultReplaceType.BOOLEAN)
                .replace("%target%", player.getName())
                .send(sender);
    }

    /**
     * Sets {@link #radius}
     *
     * @param   radius  the new value
     * @param   sender  the {@link AnnoyingSender} who's setting the value
     */
    public void radius(@NotNull String radius, @NotNull AnnoyingSender sender) {
        final double radiusDouble;
        try {
            radiusDouble = Double.parseDouble(radius);
        } catch (final NumberFormatException e) {
            sender.invalidArgument(radius);
            return;
        }

        // Set radius
        this.radius = radiusDouble;
        data.set("ff_radius", radiusDouble);

        // Send message
        new AnnoyingMessage(plugin, "command.radius.set")
                .replace("%radius%", radius, DefaultReplaceType.NUMBER)
                .replace("%target%", player.getName())
                .send(sender);
    }

    /**
     * Sets {@link #strength}
     *
     * @param   strength    the new value
     * @param   sender      the {@link AnnoyingSender} who's setting the value
     */
    public void strength(@NotNull String strength, @NotNull AnnoyingSender sender) {
        final double strengthDouble;
        try {
            strengthDouble = Double.parseDouble(strength);
        } catch (final NumberFormatException e) {
            sender.invalidArgument(strength);
            return;
        }

        // Set radius
        this.strength = strengthDouble;
        data.set("ff_strength", strengthDouble);

        // Send message
        new AnnoyingMessage(plugin, "command.strength.set")
                .replace("%strength%", strength, DefaultReplaceType.NUMBER)
                .replace("%target%", player.getName())
                .send(sender);
    }

    /**
     * Whether the player can set/use {@link #blocks}
     *
     * @return  whether the {@link #player} can set/use {@link #blocks}
     */
    public boolean cantUseBlocks() {
        return !plugin.config.blocks.enabled || !plugin.config.blocks.players.contains(player.getName()) || !player.hasPermission("forcefield.command.blocks");
    }
}
