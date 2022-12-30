# ForceField

Very epic, yet simplistic, forcefield plugin! Push players and/or mobs to your heart's content! Support Minecraft 1.11+

- **🐛 Bugs / 💡 Suggestions:** Please [open an issue](https://github.com/srnyx/forcefield/issues/new/choose) to report a bug or suggest an idea
- **🆘 Support:** Please [join the Discord](https://srnyx.xyz/discord) to get support

### Download

- **Stable:** You can download the latest **stable** version at [Modrinth](https://modrinth.com/plugin/forcefield), [Polymart](https://polymart.org/resource/3238), [Spigot](https://spigotmc.org/resources/106637), [Bukkit](https://dev.bukkit.org/projects/forcefieldplugin), or [releases/latest](https://github.com/srnyx/forcefield/releases/latest)
- **Snapshot:** You can download the latest **snapshot** version at [actions/workflows/build.yml](https://github.com/srnyx/forcefield/actions/workflows/build.yml)

## Commands

- `/forcefield [toggle] [on|off] [<player>]` - Toggles forcefield on/off
- `/forcefield mobs [on|off] [<player>]` - Toggles forcefield on/off for mobs
- `/forcefield radius [<double>] [<player>]` - Sets the radius of the forcefield
- `/forcefield strength [<double>] [<player>]` - Sets the strength of the forcefield
- `/forcefield reload` - Reloads the plugin (config and player data)

## Permissions

- `forcefield.command` - Allows access to the base command
- `forcefield.command.toggle` - Allows access to toggle the forcefield
- `forcefield.command.mobs` - Allows access to toggle the forcefield for mobs
- `forcefield.command.radius` - Allows access to change the radius of the forcefield
- `forcefield.command.strength` - Allows access to change the strength of the forcefield
- `forcefield.command.reload` - Allows access to reload the plugin (config and player data)
- `forcefield.others` - Allows using the commands on other players
- `forcefield.bypass` - Allows bypassing other players' forcefields
