package xyz.srnyx.forcefield;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.annoyingapi.AnnoyingPAPIExpansion;

import xyz.srnyx.forcefield.objects.ForcefieldOptions;


public class ForcefieldPlaceholders extends AnnoyingPAPIExpansion {
    @NotNull private final ForceField plugin;

    public ForcefieldPlaceholders(@NotNull ForceField plugin) {
        this.plugin = plugin;
    }

    @Override @NotNull
    public ForceField getAnnoyingPlugin() {
        return plugin;
    }

    @Override @NotNull
    public String getIdentifier() {
        return "forcefield";
    }

    @Override @Nullable
    public String onPlaceholderRequest(@Nullable Player player, @NotNull String params) {
        // Get player & parameter
        String parameter = params;
        if (params.contains("_")) {
            final String[] split = params.split("_", 2);
            parameter = split[0];
            player = Bukkit.getPlayerExact(split[1]);
        }
        if (player == null) return null;

        // Return value
        final ForcefieldOptions options = plugin.getOptions(player);
        switch (parameter) {
            case "enabled":
                return String.valueOf(options.enabled);
            case "inverse":
                return String.valueOf(options.inverse);
            case "special":
                return options.getSpecialName();
            case "mobs":
                return String.valueOf(options.mobs);
            case "blocks":
                return String.valueOf(options.blocks);
            case "radius":
                return String.valueOf(options.radius);
            case "strength":
                return String.valueOf(options.strength);
            default:
                return null;
        }
    }
}
