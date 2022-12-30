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
import xyz.srnyx.forcefield.ForcefieldOptions;

import java.util.*;
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
            options.setEnabled(!options.getEnabled());
            new AnnoyingMessage(plugin, "command.toggle.self")
                    .replace("%state%", options.getEnabled(), AnnoyingMessage.DefaultReplaceType.BOOLEAN)
                    .send(sender);
            return;
        }

        // <toggle|mobs|reload>
        if (args.length == 1 && sender.checkPlayer()) {
            // toggle
            if (sender.argEquals(0, "toggle") && sender.checkPermission("forcefield.command.toggle")) {
                final ForcefieldOptions options = plugin.getOptions(sender.getPlayer());
                options.setEnabled(!options.getEnabled());
                new AnnoyingMessage(plugin, "command.toggle.self")
                        .replace("%state%", options.getEnabled(), AnnoyingMessage.DefaultReplaceType.BOOLEAN)
                        .send(sender);
                return;
            }

            // mobs
            if (sender.argEquals(0, "mobs") && sender.checkPermission("forcefield.command.mobs")) {
                final ForcefieldOptions options = plugin.getOptions(sender.getPlayer());
                options.setMobs(!options.getMobs());
                new AnnoyingMessage(plugin, "command.mobs.self")
                        .replace("%state%", options.getMobs(), AnnoyingMessage.DefaultReplaceType.BOOLEAN)
                        .send(sender);
                return;
            }

            // reload
            if (sender.argEquals(0, "reload") && sender.checkPermission("forcefield.command.reload")) {
                plugin.enable();
                new AnnoyingMessage(plugin, "command.reload").send(sender);
                return;
            }
        }

        // <toggle|mobs> <on|off>, <radius|strength> [<double>]
        if (args.length == 2 && sender.checkPlayer()) {
            // toggle <on|off>
            if (sender.argEquals(0, "toggle") && sender.checkPermission("forcefield.command.toggle")) {
                final ForcefieldOptions options = plugin.getOptions(sender.getPlayer());
                options.setEnabled(sender.argEquals(1, "on"));
                new AnnoyingMessage(plugin, "command.toggle.self")
                        .replace("%state%", options.getEnabled(), AnnoyingMessage.DefaultReplaceType.BOOLEAN)
                        .send(sender);
                return;
            }

            // mobs <on|off>
            if (sender.argEquals(0, "mobs") && sender.checkPermission("forcefield.command.mobs")) {
                final ForcefieldOptions options = plugin.getOptions(sender.getPlayer());
                options.setMobs(sender.argEquals(1, "on"));
                new AnnoyingMessage(plugin, "command.mobs.self")
                        .replace("%state%", options.getMobs(), AnnoyingMessage.DefaultReplaceType.BOOLEAN)
                        .send(sender);
                return;
            }

            // radius [<double>]
            if (sender.argEquals(0, "radius") && sender.checkPermission("forcefield.command.radius")) {
                // Get radius
                final double radius;
                try {
                    radius = Double.parseDouble(args[1]);
                } catch (final NumberFormatException e) {
                    new AnnoyingMessage(plugin, "error.invalid-argument")
                            .replace("%argument%", args[1])
                            .send(sender);
                    return;
                }

                // Set radius
                plugin.getOptions(sender.getPlayer()).setRadius(radius);
                new AnnoyingMessage(plugin, "command.radius.self")
                        .replace("%radius%", args[1], AnnoyingMessage.DefaultReplaceType.NUMBER)
                        .send(sender);
                return;
            }

            // strength [<double>]
            if (sender.argEquals(0, "strength") && sender.checkPermission("forcefield.command.strength")) {
                // Get strength
                final double strength;
                try {
                    strength = Double.parseDouble(args[1]);
                } catch (final NumberFormatException e) {
                    new AnnoyingMessage(plugin, "error.invalid-argument")
                            .replace("%argument%", args[1])
                            .send(sender);
                    return;
                }

                // Set strength
                plugin.getOptions(sender.getPlayer()).setStrength(strength);
                new AnnoyingMessage(plugin, "command.strength.self")
                        .replace("%strength%", args[1], AnnoyingMessage.DefaultReplaceType.NUMBER)
                        .send(sender);
                return;
            }
        }

        // <toggle|mobs> <on|off> [<player>], <radius|strength> [<double>] [<player>]
        if (args.length == 3 && sender.checkPermission("forcefield.others")) {
            // Get target
            final Player target = Bukkit.getPlayer(args[2]);
            if (target == null) {
                new AnnoyingMessage(plugin, "error.invalid-argument")
                        .replace("%argument%", args[2])
                        .send(sender);
                return;
            }

            // toggle <on|off> [<player>]
            if (sender.argEquals(0, "toggle") && sender.checkPermission("forcefield.command.toggle")) {
                final ForcefieldOptions options = plugin.getOptions(target);
                options.setEnabled(sender.argEquals(1, "on"));
                new AnnoyingMessage(plugin, "command.toggle.other")
                        .replace("%state%", options.getEnabled(), AnnoyingMessage.DefaultReplaceType.BOOLEAN)
                        .replace("%target%", target.getName())
                        .send(sender);
                return;
            }

            // mobs <on|off> [<player>]
            if (sender.argEquals(0, "mobs") && sender.checkPermission("forcefield.command.mobs")) {
                final ForcefieldOptions options = plugin.getOptions(target);
                options.setMobs(sender.argEquals(1, "on"));
                new AnnoyingMessage(plugin, "command.mobs.other")
                        .replace("%state%", options.getMobs(), AnnoyingMessage.DefaultReplaceType.BOOLEAN)
                        .replace("%target%", target.getName())
                        .send(sender);
                return;
            }

            // radius [<double>] [<player>]
            if (sender.argEquals(0, "radius") && sender.checkPermission("forcefield.command.radius")) {
                // Get radius
                final double radius;
                try {
                    radius = Double.parseDouble(args[1]);
                } catch (final NumberFormatException e) {
                    new AnnoyingMessage(plugin, "error.invalid-argument")
                            .replace("%argument%", args[1])
                            .send(sender);
                    return;
                }

                // Set radius
                plugin.getOptions(target).setRadius(radius);
                new AnnoyingMessage(plugin, "command.radius.other")
                        .replace("%radius%", args[1], AnnoyingMessage.DefaultReplaceType.NUMBER)
                        .replace("%target%", target.getName())
                        .send(sender);
                return;
            }

            // strength [<double>] [<player>]
            if (sender.argEquals(0, "strength") && sender.checkPermission("forcefield.command.strength")) {
                // Get strength
                final double strength;
                try {
                    strength = Double.parseDouble(args[1]);
                } catch (final NumberFormatException e) {
                    new AnnoyingMessage(plugin, "error.invalid-argument")
                            .replace("%argument%", args[1])
                            .send(sender);
                    return;
                }

                // Set strength
                plugin.getOptions(target).setStrength(strength);
                new AnnoyingMessage(plugin, "command.strength.other")
                        .replace("%strength%", args[1], AnnoyingMessage.DefaultReplaceType.NUMBER)
                        .replace("%target%", target.getName())
                        .send(sender);
            }
        }
    }

    @Override @Nullable
    public Collection<String> onTabComplete(@NotNull AnnoyingSender sender) {
        final String[] args = sender.getArgs();

        // <toggle|mobs|radius|strength|reload>
        if (args.length == 1) return Arrays.asList("toggle", "mobs", "radius", "strength", "reload");

        // <toggle|mobs> <on|off>, <radius|strength> [<double>]
        if (args.length == 2) {
            // <toggle|mobs> <on|off>
            if (sender.argEquals(0, "toggle") || sender.argEquals(0, "mobs")) return Arrays.asList("on", "off");
            // <radius|strength> [<double>]
            if (sender.argEquals(0, "radius") || sender.argEquals(0, "strength")) return Collections.singleton("[<double>]");
        }

        // <toggle|mobs> <on|off> [<player>], <radius|strength> [<double>] [<player>]
        if (args.length == 3) {
            // toggle <on|off> [<player>]
            if (sender.argEquals(0, "toggle")) {
                // toggle on [<player>]
                if (sender.argEquals(1, "on")) return Bukkit.getOnlinePlayers().stream()
                        .filter(player -> {
                            final ForcefieldOptions options = plugin.forcefields.get(player.getUniqueId());
                            return options == null || !options.getEnabled();
                        })
                        .map(Player::getName)
                        .collect(Collectors.toList());

                // toggle off [<player>]
                if (sender.argEquals(1, "off")) return plugin.forcefields.entrySet().stream()
                        .filter(entry -> entry.getValue().getEnabled())
                        .map(entry -> Bukkit.getPlayer(entry.getKey()))
                        .filter(Objects::nonNull)
                        .map(Player::getName)
                        .collect(Collectors.toSet());
            }

            // mobs <on|off> [<player>]
            if (sender.argEquals(0, "mobs")) {
                // mobs on [<player>]
                if (sender.argEquals(1, "on")) return Bukkit.getOnlinePlayers().stream()
                        .filter(player -> {
                            final ForcefieldOptions options = plugin.forcefields.get(player.getUniqueId());
                            return options == null || !options.getMobs();
                        })
                        .map(Player::getName)
                        .collect(Collectors.toList());

                // mobs off [<player>]
                if (sender.argEquals(1, "off")) return plugin.forcefields.entrySet().stream()
                        .filter(entry -> entry.getValue().getMobs())
                        .map(entry -> Bukkit.getPlayer(entry.getKey()))
                        .filter(Objects::nonNull)
                        .map(Player::getName)
                        .collect(Collectors.toSet());
            }

            // <radius|strength> [<double>] [<player>]
            if (sender.argEquals(0, "radius") || sender.argEquals(0, "strength")) return AnnoyingUtility.getOnlinePlayerNames();
        }

        return null;
    }
}
