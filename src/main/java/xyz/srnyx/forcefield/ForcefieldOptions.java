package xyz.srnyx.forcefield;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.annoyingapi.file.AnnoyingData;

import java.util.UUID;


public class ForcefieldOptions {
    @NotNull private final AnnoyingData data;
    private boolean enabled;
    private boolean mobs;
    private double radius;
    private double strength;

    /**
     * Creates a new {@link ForcefieldOptions} instance and adds it to {@link ForceField#forcefields}
     *
     * @param   plugin  the plugin instance
     * @param   uuid    the player's UUID
     */
    public ForcefieldOptions(@NotNull ForceField plugin, @NotNull UUID uuid) {
        this.data = new AnnoyingData(plugin, "players/" + uuid + ".yml", false);

        // Get options
        this.enabled = data.get("enabled") != null && data.getBoolean("enabled");
        this.mobs = data.get("mobs") != null ? data.getBoolean("mobs") : plugin.defaultMobs;
        this.radius = data.get("radius") != null ? data.getDouble("radius") : plugin.defaultRadius;
        this.strength = data.get("strength") != null ? data.getDouble("strength") : plugin.defaultStrength;

        // Add to map
        plugin.forcefields.put(uuid, this);
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        data.set("enabled", enabled, true);
    }

    public boolean getMobs() {
        return mobs;
    }

    public void setMobs(boolean mobs) {
        this.mobs = mobs;
        data.set("mobs", mobs, true);
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
        data.set("radius", radius, true);
    }

    public double getStrength() {
        return strength;
    }

    public void setStrength(double strength) {
        this.strength = strength;
        data.set("strength", strength, true);
    }
}
