package xyz.srnyx.forcefield.objects;

import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.annoyingapi.AnnoyingMessage;
import xyz.srnyx.annoyingapi.command.AnnoyingSender;
import xyz.srnyx.annoyingapi.file.AnnoyingData;

import xyz.srnyx.forcefield.ForceField;
import xyz.srnyx.forcefield.enums.SpecialForcefield;


/**
 * An object containing the options for the {@link #player}'s forcefield
 */
public class ForcefieldOptions {
    @NotNull private final ForceField plugin;
    @NotNull private final AnnoyingData data;
    @NotNull private final Player player;
    public boolean enabled;
    public boolean inverse;
    @Nullable public SpecialForcefield special;
    public boolean mobs;
    public boolean blocks;
    public double radius;
    public double strength;

    /**
     * Creates a new {@link ForcefieldOptions} instance and adds it to {@link ForceField#forcefields}
     *
     * @param   plugin  the plugin instance
     * @param   player  the player
     */
    public ForcefieldOptions(@NotNull ForceField plugin, @NotNull Player player) {
        this.plugin = plugin;
        this.data = new AnnoyingData(plugin, "players/" + player.getUniqueId() + ".yml", false);
        this.player = player;

        // Get options
        this.enabled = data.get("enabled") != null && data.getBoolean("enabled");
        this.inverse = data.get("inverse") != null ? data.getBoolean("inverse") : plugin.config.defaultInverse;
        this.mobs = data.get("mobs") != null ? data.getBoolean("mobs") : plugin.config.defaultMobs;
        this.blocks = data.get("blocks") != null && data.getBoolean("blocks");
        this.special = data.get("special") != null ? SpecialForcefield.getSpecial(data.getString("special")) : plugin.config.defaultSpecial;
        this.radius = data.get("radius") != null ? data.getDouble("radius") : plugin.config.defaultRadius;
        this.strength = data.get("strength") != null ? data.getDouble("strength") : plugin.config.defaultStrength;

        // Add to map
        plugin.forcefields.put(player.getUniqueId(), this);
    }

    /**
     * Sets {@link #enabled}
     *
     * @param   enabled the new value
     * @param   sender  the {@link AnnoyingSender} who's setting the value
     */
    public void setEnabled(boolean enabled, @NotNull AnnoyingSender sender) {
        this.enabled = enabled;
        data.set("enabled", enabled, true);

        // Send message
        if (sender.getCmdSender().equals(player)) {
            new AnnoyingMessage(plugin, "command.toggle.self")
                    .replace("%state%", enabled, AnnoyingMessage.DefaultReplaceType.BOOLEAN)
                    .send(sender);
        } else {
            new AnnoyingMessage(plugin, "command.toggle.other")
                    .replace("%state%", enabled, AnnoyingMessage.DefaultReplaceType.BOOLEAN)
                    .replace("%target%", player.getName())
                    .send(sender);
        }
    }

    /**
     * Sets {@link #inverse}
     *
     * @param   inverse the new value
     * @param   sender  the {@link AnnoyingSender} who's setting the value
     */
    public void setInverse(boolean inverse, @NotNull AnnoyingSender sender) {
        this.inverse = inverse;
        data.set("inverse", inverse, true);

        // Send message
        if (sender.getCmdSender().equals(player)) {
            new AnnoyingMessage(plugin, "command.inverse.self")
                    .replace("%state%", inverse, AnnoyingMessage.DefaultReplaceType.BOOLEAN)
                    .send(sender);
        } else {
            new AnnoyingMessage(plugin, "command.inverse.other")
                    .replace("%state%", inverse, AnnoyingMessage.DefaultReplaceType.BOOLEAN)
                    .replace("%target%", player.getName())
                    .send(sender);
        }
    }

    /**
     * Sets {@link #special}
     *
     * @param   special the new value
     * @param   sender  the {@link AnnoyingSender} who's setting the value
     */
    public void setSpecial(@Nullable String special, @NotNull AnnoyingSender sender) {
        final SpecialForcefield specialEnum = SpecialForcefield.getSpecial(special);
        if (specialEnum != null && !sender.checkPermission("forcefield.special." + specialEnum.name().toLowerCase())) return;

        // Set blocks
        this.special = specialEnum;
        data.set("special", specialEnum != null ? specialEnum.name() : null, true);

        // Send message
        final String name = SpecialForcefield.getName(specialEnum);
        if (sender.getCmdSender().equals(player)) {
            new AnnoyingMessage(plugin, "command.special.self")
                    .replace("%special%", name)
                    .send(sender);
        } else {
            new AnnoyingMessage(plugin, "command.special.other")
                    .replace("%special%", name)
                    .replace("%target%", player.getName())
                    .send(sender);
        }
    }

    /**
     * Sets {@link #mobs}
     *
     * @param   mobs    the new value
     * @param   sender  the {@link AnnoyingSender} who's setting the value
     */
    public void setMobs(boolean mobs, @NotNull AnnoyingSender sender) {
        this.mobs = mobs;
        data.set("mobs", mobs, true);

        // Send message
        if (sender.getCmdSender().equals(player)) {
            new AnnoyingMessage(plugin, "command.mobs.self")
                    .replace("%state%", mobs, AnnoyingMessage.DefaultReplaceType.BOOLEAN)
                    .send(sender);
        } else {
            new AnnoyingMessage(plugin, "command.mobs.other")
                    .replace("%state%", mobs, AnnoyingMessage.DefaultReplaceType.BOOLEAN)
                    .replace("%target%", player.getName())
                    .send(sender);
        }
    }

    /**
     * Sets {@link #blocks}
     *
     * @param   blocks  the new value
     * @param   sender  the {@link AnnoyingSender} who's setting the value
     */
    public void setBlocks(boolean blocks, @NotNull AnnoyingSender sender) {
        // Check if player has permission
        if (!sender.checkPermission("forcefield.command.blocks")) return;

        // Check if config allows blocks and list contains player
        if (cantUseBlocks()) {
            new AnnoyingMessage(plugin, "command.blocks.error").send(sender);
            return;
        }

        // Set blocks
        this.blocks = blocks;
        data.set("blocks", blocks, true);

        // Send message
        if (sender.getCmdSender().equals(player)) {
            new AnnoyingMessage(plugin, "command.blocks.self")
                    .replace("%state%", blocks, AnnoyingMessage.DefaultReplaceType.BOOLEAN)
                    .send(sender);
        } else {
            new AnnoyingMessage(plugin, "command.blocks.other")
                    .replace("%state%", blocks, AnnoyingMessage.DefaultReplaceType.BOOLEAN)
                    .replace("%target%", player.getName())
                    .send(sender);
        }
    }

    /**
     * Sets {@link #radius}
     *
     * @param   radius  the new value
     * @param   sender  the {@link AnnoyingSender} who's setting the value
     */
    public void setRadius(@NotNull String radius, @NotNull AnnoyingSender sender) {
        final double radiusDouble;
        try {
            radiusDouble = Double.parseDouble(radius);
        } catch (final NumberFormatException e) {
            new AnnoyingMessage(plugin, "error.invalid-argument")
                    .replace("%argument%", radius)
                    .send(sender);
            return;
        }

        // Set radius
        this.radius = radiusDouble;
        data.set("radius", radiusDouble, true);

        // Send message
        if (sender.getCmdSender().equals(player)) {
            new AnnoyingMessage(plugin, "command.radius.self")
                    .replace("%radius%", radius, AnnoyingMessage.DefaultReplaceType.NUMBER)
                    .send(sender);
        } else {
            new AnnoyingMessage(plugin, "command.radius.other")
                    .replace("%radius%", radius, AnnoyingMessage.DefaultReplaceType.NUMBER)
                    .replace("%target%", player.getName())
                    .send(sender);
        }
    }

    /**
     * Sets {@link #strength}
     *
     * @param   strength    the new value
     * @param   sender      the {@link AnnoyingSender} who's setting the value
     */
    public void setStrength(@NotNull String strength, @NotNull AnnoyingSender sender) {
        final double strengthDouble;
        try {
            strengthDouble = Double.parseDouble(strength);
        } catch (final NumberFormatException e) {
            new AnnoyingMessage(plugin, "error.invalid-argument")
                    .replace("%argument%", strength)
                    .send(sender);
            return;
        }

        // Set radius
        this.strength = strengthDouble;
        data.set("strength", strengthDouble, true);

        // Send message
        if (sender.getCmdSender().equals(player)) {
            new AnnoyingMessage(plugin, "command.strength.self")
                    .replace("%strength%", strength, AnnoyingMessage.DefaultReplaceType.NUMBER)
                    .send(sender);
        } else {
            new AnnoyingMessage(plugin, "command.strength.other")
                    .replace("%strength%", strength, AnnoyingMessage.DefaultReplaceType.NUMBER)
                    .replace("%target%", player.getName())
                    .send(sender);
        }
    }

    /**
     * Whether the player can set/use {@link #blocks}
     *
     * @return  whether the {@link #player} can set/use {@link #blocks}
     */
    public boolean cantUseBlocks() {
        return !plugin.config.blocksEnabled || !plugin.config.blocksPlayers.contains(player.getName()) || !player.hasPermission("forcefield.command.blocks");
    }
}
