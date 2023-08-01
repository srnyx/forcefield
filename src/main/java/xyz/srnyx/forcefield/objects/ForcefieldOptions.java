package xyz.srnyx.forcefield.objects;

import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.annoyingapi.command.AnnoyingSender;
import xyz.srnyx.annoyingapi.data.EntityData;
import xyz.srnyx.annoyingapi.file.AnnoyingData;
import xyz.srnyx.annoyingapi.file.AnnoyingFile;
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
    @NotNull private final Player player;
    private boolean enabled;
    private boolean inverse;
    @Nullable private SpecialForcefield special;
    private boolean mobs;
    private boolean blocks;
    private double radius;
    private double strength;

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
        convertOldData();

        // Get options
        enabled = data.has("ff_enabled");
        inverse = Boolean.parseBoolean(data.get("ff_inverse", String.valueOf(plugin.config.defaultInverse)));
        mobs = Boolean.parseBoolean(data.get("ff_mobs", String.valueOf(plugin.config.defaultMobs)));
        blocks = Boolean.parseBoolean(data.get("ff_blocks"));
        final SpecialForcefield specialMatch = SpecialForcefield.matchSpecial(data.get("ff_special"));
        special = specialMatch != null ? specialMatch : plugin.config.defaultSpecial;
        try {
            radius = Double.parseDouble(data.get("ff_radius", String.valueOf(plugin.config.defaultRadius)));
        } catch (NumberFormatException e) {
            radius = plugin.config.defaultRadius;
        }
        try {
            strength = Double.parseDouble(data.get("ff_strength", String.valueOf(plugin.config.defaultStrength)));
        } catch (NumberFormatException e) {
            strength = plugin.config.defaultStrength;
        }
    }

    /**
     * @deprecated  Used to convert old data
     */
    private void convertOldData() {
        final AnnoyingData oldData = new AnnoyingData(plugin, "players/" + player.getUniqueId() + ".yml", new AnnoyingFile.Options<>().canBeEmpty(false));
        if (!oldData.file.exists()) return;

        // enabled
        if (oldData.contains("enabled")) {
            if (oldData.getBoolean("enabled")) data.set("ff_enabled", true);
            oldData.set("enabled", null);
        }
        // inverse
        if (oldData.contains("inverse")) {
            if (oldData.getBoolean("inverse")) data.set("ff_inverse", true);
            oldData.set("inverse", null);
        }
        // mobs
        if (oldData.contains("mobs")) {
            if (oldData.getBoolean("mobs")) data.set("ff_mobs", true);
            oldData.set("mobs", null);
        }
        // blocks
        if (oldData.contains("blocks")) {
            if (oldData.getBoolean("blocks")) data.set("ff_blocks", true);
            oldData.set("blocks", null);
        }
        // special
        if (oldData.contains("special")) {
            final String specialString = oldData.getString("special");
            if (specialString != null) data.set("ff_special", specialString);
            oldData.set("special", null);

        }
        // radius
        if (oldData.contains("radius")) {
            final String radiusString = oldData.getString("radius");
            if (radiusString != null) data.set("ff_radius", radiusString);
            oldData.set("radius", null);
        }
        // strength
        if (oldData.contains("strength")) {
            final String strengthString = oldData.getString("strength");
            if (strengthString != null) data.set("ff_strength", strengthString);
            oldData.set("strength", null);
        }

        // Delete old data
        oldData.delete();
    }

    public boolean enabled() {
        return enabled;
    }

    /**
     * Sets {@link #enabled}
     *
     * @param   enabled the new value
     * @param   sender  the {@link AnnoyingSender} who's setting the value
     */
    public void enabled(boolean enabled, @NotNull AnnoyingSender sender) {
        this.enabled = enabled;
        data.set("ff_enabled", enabled);

        // Send message
        if (sender.cmdSender.equals(player)) {
            new AnnoyingMessage(plugin, "command.toggle.self")
                    .replace("%state%", enabled, DefaultReplaceType.BOOLEAN)
                    .send(sender);
            return;
        }
        new AnnoyingMessage(plugin, "command.toggle.other")
                .replace("%state%", enabled, DefaultReplaceType.BOOLEAN)
                .replace("%target%", player.getName())
                .send(sender);
    }

    public boolean inverse() {
        return inverse;
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

        // Send message
        if (sender.cmdSender.equals(player)) {
            new AnnoyingMessage(plugin, "command.inverse.self")
                    .replace("%state%", inverse, DefaultReplaceType.BOOLEAN)
                    .send(sender);
            return;
        }
        new AnnoyingMessage(plugin, "command.inverse.other")
                .replace("%state%", inverse, DefaultReplaceType.BOOLEAN)
                .replace("%target%", player.getName())
                .send(sender);
    }

    @Nullable
    public SpecialForcefield special() {
        return special;
    }

    /**
     * Sets {@link #special}
     *
     * @param   special the new value
     * @param   sender  the {@link AnnoyingSender} who's setting the value
     */
    public void special(@Nullable String special, @NotNull AnnoyingSender sender) {
        final SpecialForcefield specialEnum = SpecialForcefield.matchSpecial(special);
        if (specialEnum != null && !sender.checkPermission("forcefield.special." + specialEnum.name().toLowerCase())) return;

        // Set blocks
        this.special = specialEnum;
        data.set("ff_special", specialEnum != null ? specialEnum.name() : null);

        // Send message
        final String name = SpecialForcefield.getName(specialEnum);
        if (sender.cmdSender.equals(player)) {
            new AnnoyingMessage(plugin, "command.special.self")
                    .replace("%special%", name)
                    .send(sender);
            return;
        }
        new AnnoyingMessage(plugin, "command.special.other")
                .replace("%special%", name)
                .replace("%target%", player.getName())
                .send(sender);
    }

    public boolean mobs() {
        return mobs;
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

        // Send message
        if (sender.cmdSender.equals(player)) {
            new AnnoyingMessage(plugin, "command.mobs.self")
                    .replace("%state%", mobs, DefaultReplaceType.BOOLEAN)
                    .send(sender);
            return;
        }
        new AnnoyingMessage(plugin, "command.mobs.other")
                .replace("%state%", mobs, DefaultReplaceType.BOOLEAN)
                .replace("%target%", player.getName())
                .send(sender);
    }

    public boolean blocks() {
        return blocks;
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
        data.set("ff_blocks", blocks);

        // Send message
        if (sender.cmdSender.equals(player)) {
            new AnnoyingMessage(plugin, "command.blocks.self")
                    .replace("%state%", blocks, DefaultReplaceType.BOOLEAN)
                    .send(sender);
            return;
        }
        new AnnoyingMessage(plugin, "command.blocks.other")
                .replace("%state%", blocks, DefaultReplaceType.BOOLEAN)
                .replace("%target%", player.getName())
                .send(sender);
    }

    public double radius() {
        return radius;
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
        if (sender.cmdSender.equals(player)) {
            new AnnoyingMessage(plugin, "command.radius.self")
                    .replace("%radius%", radius, DefaultReplaceType.NUMBER)
                    .send(sender);
            return;
        }
        new AnnoyingMessage(plugin, "command.radius.other")
                .replace("%radius%", radius, DefaultReplaceType.NUMBER)
                .replace("%target%", player.getName())
                .send(sender);
    }

    public double strength() {
        return strength;
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
        if (sender.cmdSender.equals(player)) {
            new AnnoyingMessage(plugin, "command.strength.self")
                    .replace("%strength%", strength, DefaultReplaceType.NUMBER)
                    .send(sender);
            return;
        }
        new AnnoyingMessage(plugin, "command.strength.other")
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
        return !plugin.config.blocksEnabled || !plugin.config.blocksPlayers.contains(player.getName()) || !player.hasPermission("forcefield.command.blocks");
    }
}
