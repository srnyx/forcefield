package xyz.srnyx.forcefield.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.annoyingapi.AnnoyingMessage;
import xyz.srnyx.annoyingapi.AnnoyingUtility;
import xyz.srnyx.annoyingapi.command.AnnoyingCommand;
import xyz.srnyx.annoyingapi.command.AnnoyingSender;

import xyz.srnyx.forcefield.ForceField;
import xyz.srnyx.forcefield.enums.SpecialForcefield;
import xyz.srnyx.forcefield.objects.ForcefieldOptions;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class ForcefieldCommand implements AnnoyingCommand {
    @NotNull private final ForceField plugin;

    @Contract(pure = true)
    public ForcefieldCommand(@NotNull ForceField plugin) {
        this.plugin = plugin;
    }

    @Override @NotNull
    public ForceField getPlugin() {
        return plugin;
    }

    @Override @NotNull
    public String getPermission() {
        return "forcefield.command";
    }

    @Override
    public void onCommand(@NotNull AnnoyingSender sender) {
        final String[] args = sender.getArgs();

        // No arguments (toggle)
        if (args.length == 0 && sender.checkPlayer() && sender.checkPermission("forcefield.command.toggle")) {
            final ForcefieldOptions options = plugin.getOptions(sender.getPlayer());
            options.setEnabled(!options.enabled, sender);
            return;
        }

        // <reload|toggle|inverse|mobs|blocks>
        if (args.length == 1) {
            // reload
            if (sender.argEquals(0, "reload") && sender.checkPermission("forcefield.command.reload")) {
                plugin.reloadPlugin();
                new AnnoyingMessage(plugin, "command.reload").send(sender);
                return;
            }

            // <toggle|inverse|mobs|blocks>
            if (sender.checkPlayer()) {
                final Player player = sender.getPlayer();
                final ForcefieldOptions options = plugin.getOptions(player);

                // toggle
                if (sender.argEquals(0, "toggle") && sender.checkPermission("forcefield.command.toggle")) {
                    options.setEnabled(!options.enabled, sender);
                    return;
                }

                // inverse
                if (sender.argEquals(0, "inverse") && sender.checkPermission("forcefield.command.inverse")) {
                    options.setInverse(!options.inverse, sender);
                    return;
                }

                // mobs
                if (sender.argEquals(0, "mobs") && sender.checkPermission("forcefield.command.mobs")) {
                    options.setMobs(!options.mobs, sender);
                    return;
                }

                // blocks
                if (sender.argEquals(0, "blocks")) {
                    options.setBlocks(!options.blocks, sender);
                    return;
                }
            }
        }

        // <toggle|inverse|mobs|blocks> <on|off> [<player>], <radius|strength> [<number>] [<player>], special [<special>] [<player>]
        if (args.length >= 2) {
            final Player player;
            if (args.length == 3 && sender.checkPermission("forcefield.command.other")) {
                player = Bukkit.getPlayer(args[2]);
                if (player == null) {
                    new AnnoyingMessage(plugin, "command.invalid-argument")
                            .replace("%argument%", args[2])
                            .send(sender);
                    return;
                }
            } else if (sender.checkPlayer()) {
                player = sender.getPlayer();
            } else return;
            final ForcefieldOptions options = plugin.getOptions(player);

            // toggle <on|off> [<player>]
            if (sender.argEquals(0, "toggle") && sender.checkPermission("forcefield.command.toggle")) {
                options.setEnabled(sender.argEquals(1, "on"), sender);
                return;
            }

            // inverse <on|off> [<player>]
            if (sender.argEquals(0, "inverse") && sender.checkPermission("forcefield.command.inverse")) {
                options.setInverse(sender.argEquals(1, "on"), sender);
                return;
            }

            // mobs <on|off> [<player>]
            if (sender.argEquals(0, "mobs") && sender.checkPermission("forcefield.command.mobs")) {
                options.setMobs(sender.argEquals(1, "on"), sender);
                return;
            }

            // blocks <on|off> [<player>]
            if (sender.argEquals(0, "blocks")) {
                options.setBlocks(sender.argEquals(1, "on"), sender);
                return;
            }

            // radius [<number>] [<player>]
            if (sender.argEquals(0, "radius") && sender.checkPermission("forcefield.command.radius")) {
                options.setRadius(args[1], sender);
                return;
            }

            // strength [<number>] [<player>]
            if (sender.argEquals(0, "strength") && sender.checkPermission("forcefield.command.strength")) {
                options.setStrength(args[1], sender);
                return;
            }

            // special [<special>] [<player>]
            if (sender.argEquals(0, "special") && sender.checkPermission("forcefield.command.special")) options.setSpecial(args[1], sender);
        }
    }

    @Override @Nullable
    public Collection<String> onTabComplete(@NotNull AnnoyingSender sender) {
        final String[] args = sender.getArgs();

        // <reload|toggle|inverse|special|mobs|blocks|radius|strength>
        if (args.length == 1) return Arrays.asList("reload", "toggle", "inverse", "mobs", "blocks", "radius", "strength", "special");

        // <toggle|inverse|mobs|blocks> <on|off>, <radius|strength> [<number>], special [<special>]
        if (args.length == 2) {
            // <toggle|inverse|mobs|blocks> <on|off>
            if (sender.argEquals(0, "toggle", "inverse", "mobs", "blocks")) return Arrays.asList("on", "off");
            // <radius|strength> [<number>]
            if (sender.argEquals(0, "radius", "strength")) return Collections.singleton("[<number>]");
            // special [<special>]
            if (sender.argEquals(0, "special")) {
                final Set<String> completions = new HashSet<>();
                completions.add("NONE");
                for (final SpecialForcefield special : SpecialForcefield.values()) completions.add(special.name());
                return completions;
            }
        }

        // <toggle|inverse|mobs|blocks> <on|off> [<player>], <radius|strength> [<number>] [<player>], special [<special>] [<player>]
        if (args.length == 3) {
            // toggle <on|off> [<player>]
            if (sender.argEquals(0, "toggle")) {
                // toggle on [<player>]
                if (sender.argEquals(1, "on")) return getPlayerNames(options -> options == null || !options.enabled);
                // toggle off [<player>]
                if (sender.argEquals(1, "off")) return plugin.forcefields.entrySet().stream()
                        .filter(entry -> entry.getValue().enabled)
                        .map(entry -> Bukkit.getPlayer(entry.getKey()))
                        .filter(Objects::nonNull)
                        .map(Player::getName)
                        .collect(Collectors.toSet());
            }

            // inverse <on|off> [<player>]
            if (sender.argEquals(0, "inverse")) {
                // inverse on [<player>]
                if (sender.argEquals(1, "on")) return getPlayerNames(options -> (options != null && !options.inverse) || (options == null && !plugin.config.defaultInverse));
                // inverse off [<player>]
                if (sender.argEquals(1, "off")) return getPlayerNames(options -> (options != null && options.inverse) || (options == null && plugin.config.defaultInverse));
            }

            // mobs <on|off> [<player>]
            if (sender.argEquals(0, "mobs")) {
                // mobs on [<player>]
                if (sender.argEquals(1, "on")) return getPlayerNames(options -> (options != null && !options.mobs) || (options == null && !plugin.config.defaultMobs));
                // mobs off [<player>]
                if (sender.argEquals(1, "off")) return getPlayerNames(options -> (options != null && options.mobs) || (options == null && plugin.config.defaultMobs));
            }

            // blocks <on|off> [<player>]
            if (sender.argEquals(0, "blocks")) {
                // blocks on [<player>]
                if (sender.argEquals(1, "on")) return getPlayerNames(options -> options == null || !options.blocks);
                // blocks off [<player>]
                if (sender.argEquals(1, "off")) return plugin.forcefields.entrySet().stream()
                        .filter(entry -> entry.getValue().blocks)
                        .map(entry -> Bukkit.getPlayer(entry.getKey()))
                        .filter(Objects::nonNull)
                        .map(Player::getName)
                        .collect(Collectors.toSet());
            }

            // <radius|strength> [<number>] [<player>]
            if (sender.argEquals(0, "radius", "strength")) return AnnoyingUtility.getOnlinePlayerNames();

            // special [<special>] [<player>]
            if (sender.argEquals(0, "special")) {
                final SpecialForcefield special = SpecialForcefield.getSpecial(args[1]);
                return getPlayerNames(options -> (options != null && options.special != special) || (options == null && plugin.config.defaultSpecial != special));
            }
        }

        return null;
    }

    @NotNull
    private Set<String> getPlayerNames(@NotNull Predicate<ForcefieldOptions> predicate) {
        return Bukkit.getOnlinePlayers().stream()
                .filter(player -> predicate.test(plugin.forcefields.get(player.getUniqueId())))
                .map(Player::getName)
                .collect(Collectors.toSet());
    }
}
