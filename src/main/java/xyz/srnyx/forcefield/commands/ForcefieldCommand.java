package xyz.srnyx.forcefield.commands;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.annoyingapi.message.AnnoyingMessage;
import xyz.srnyx.annoyingapi.command.AnnoyingCommand;
import xyz.srnyx.annoyingapi.command.AnnoyingSender;
import xyz.srnyx.annoyingapi.message.DefaultReplaceType;
import xyz.srnyx.annoyingapi.utility.BukkitUtility;

import xyz.srnyx.forcefield.ForceField;
import xyz.srnyx.forcefield.SpecialForcefield;
import xyz.srnyx.forcefield.objects.ForcefieldOptions;

import java.util.*;


public class ForcefieldCommand extends AnnoyingCommand {
    @NotNull private final ForceField plugin;

    public ForcefieldCommand(@NotNull ForceField plugin) {
        this.plugin = plugin;
    }

    @Override @NotNull
    public ForceField getAnnoyingPlugin() {
        return plugin;
    }

    @Override @NotNull
    public String getPermission() {
        return "forcefield.command";
    }

    @Override
    public void onCommand(@NotNull AnnoyingSender sender) {
        final String[] args = sender.args;

        // No arguments (toggle)
        if (args.length == 0) {
            if (!sender.checkPlayer() || !sender.checkPermission("forcefield.command.toggle")) return;
            final ForcefieldOptions options = plugin.getOptions(sender.getPlayer());
            options.enabled(!options.enabled, sender);
            return;
        }

        // reload
        if (sender.argEquals(0, "reload")) {
            if (!sender.checkPermission("forcefield.command.reload")) return;
            plugin.reloadPlugin();
            new AnnoyingMessage(plugin, "command.reload").send(sender);
            return;
        }

        // get
        if (sender.argEquals(0, "get")) {
            // No option specified (toggle)
            if (args.length == 1) {
                if (!sender.checkPlayer() || !sender.checkPermission("forcefield.command.toggle")) return;
                final Player player = sender.getPlayer();
                new AnnoyingMessage(plugin, "command.toggle.get")
                        .replace("%target%", player.getName())
                        .replace("%state%", plugin.getOptions(player).enabled, DefaultReplaceType.BOOLEAN)
                        .send(sender);
                return;
            }

            // Check permission
            final String option = sender.getArgument(1, String::toLowerCase);
            if (option == null || !sender.checkPermission("forcefield.command." + option)) return;

            // Get target options
            final ForcefieldOptions options = getTargetOptions(sender, 2);
            if (options == null) return;
            final String name = options.player.getName();

            switch (option) {
                // toggle
                case "toggle":
                    new AnnoyingMessage(plugin, "command.toggle.get")
                            .replace("%target%", name)
                            .replace("%state%", options.enabled, DefaultReplaceType.BOOLEAN)
                            .send(sender);
                    return;
                // inverse
                case "inverse":
                    new AnnoyingMessage(plugin, "command.inverse.get")
                            .replace("%target%", name)
                            .replace("%state%", options.inverse, DefaultReplaceType.BOOLEAN)
                            .send(sender);
                    return;
                // mobs
                case "mobs":
                    new AnnoyingMessage(plugin, "command.mobs.get")
                            .replace("%target%", name)
                            .replace("%state%", options.mobs, DefaultReplaceType.BOOLEAN)
                            .send(sender);
                    return;
                // blocks
                case "blocks":
                    new AnnoyingMessage(plugin, "command.blocks.get")
                            .replace("%target%", name)
                            .replace("%state%", options.blocks, DefaultReplaceType.BOOLEAN)
                            .send(sender);
                    return;
                // radius
                case "radius":
                    new AnnoyingMessage(plugin, "command.radius.get")
                            .replace("%target%", name)
                            .replace("%radius%", options.radius, DefaultReplaceType.NUMBER)
                            .send(sender);
                    return;
                // strength
                case "strength":
                    new AnnoyingMessage(plugin, "command.strength.get")
                            .replace("%target%", name)
                            .replace("%strength%", options.strength, DefaultReplaceType.NUMBER)
                            .send(sender);
                    return;
                // special
                case "special":
                    new AnnoyingMessage(plugin, "command.special.get")
                            .replace("%target%", name)
                            .replace("%special%", options.getSpecialName())
                            .send(sender);
                    return;
            }

            sender.invalidArguments();
            return;
        }

        // set
        if (sender.argEquals(0, "set")) {
            // Check permission
            final String option = sender.getArgument(1, String::toLowerCase);
            if (option == null || !sender.checkPermission("forcefield.command." + option)) return;

            // Get target options
            final ForcefieldOptions options = getTargetOptions(sender, 3);
            if (options == null) return;

            switch (option) {
                // toggle
                case "toggle":
                    options.enabled(sender.argEquals(2, "on"), sender);
                    return;
                // inverse
                case "inverse":
                    options.inverse(sender.argEquals(2, "on"), sender);
                    return;
                // mobs
                case "mobs":
                    options.mobs(sender.argEquals(2, "on"), sender);
                    return;
                // blocks
                case "blocks":
                    options.blocks(sender.argEquals(2, "on"), sender);
                    return;
                // radius
                case "radius":
                    options.radius(args[2], sender);
                    return;
                // strength
                case "strength":
                    options.strength(args[2], sender);
                    return;
                // special
                case "special":
                    options.special(args[2], sender);
                    return;
            }
        }

        sender.invalidArguments();
    }

    @Nullable
    private ForcefieldOptions getTargetOptions(@NotNull AnnoyingSender sender, int index) {
        OfflinePlayer player = null;
        if (sender.args.length == index + 1) {
            if (!sender.checkPermission("forcefield.others")) return null;
            player = sender.getArgumentOptionalFlat(index, BukkitUtility::getOfflinePlayer).orElse(null);
        } else if (sender.checkPlayer()) {
            player = sender.getPlayer();
        }
        return player != null ? plugin.getOptions(player) : null;
    }
    
    @NotNull private static final Set<String> SPECIAL = new HashSet<>();
    static {
        SPECIAL.add("NONE");
        for (final SpecialForcefield special : SpecialForcefield.values()) SPECIAL.add(special.name());
    }

    @Override @Nullable
    public Collection<String> onTabComplete(@NotNull AnnoyingSender sender) {
        final CommandSender cmdSender = sender.cmdSender;
        final String[] args = sender.args;

        // <reload|get|set>
        if (args.length == 1) {
            final Set<String> options = new HashSet<>(Arrays.asList("get", "set"));
            if (cmdSender.hasPermission("forcefield.command.reload")) options.add("reload");
            return options;
        }

        // <get|set> <toggle|inverse|mobs|blocks|radius|strength|special>
        if (args.length == 2) {
            final Set<String> options = new HashSet<>(Arrays.asList("toggle", "inverse", "mobs", "blocks", "radius", "strength", "special"));
            options.removeIf(option -> !cmdSender.hasPermission("forcefield.command." + option));
            return options;
        }

        // <get|set> <toggle|inverse|mobs|blocks|radius|strength|special> [...]
        if (args.length == 3) {
            // get
            if (sender.argEquals(0, "get")) {
                if (sender.argEquals(1, "toggle", "inverse", "mobs", "blocks", "radius", "strength", "special") && sender.cmdSender.hasPermission("forcefield.others")) return BukkitUtility.getOnlinePlayerNames();
                return null;
            }

            // set
            if (sender.argEquals(0, "set")) {
                // <toggle|inverse|mobs|blocks> <on|off>
                if (sender.argEquals(1, "toggle", "inverse", "mobs", "blocks")) return Arrays.asList("on", "off");

                // <radius|strength> [<number>]
                if (sender.argEquals(1, "radius", "strength")) return Collections.singleton("[<number>]");

                // special [<special>]
                if (sender.argEquals(1, "special")) return SPECIAL;

                return null;
            }
        }

        // set <toggle|inverse|mobs|blocks|radius|strength|special> [...] [<player>]
        if (args.length == 4 && sender.argEquals(0, "set") && cmdSender.hasPermission("forcefield.others")) return BukkitUtility.getOnlinePlayerNames();

        return null;
    }
}
