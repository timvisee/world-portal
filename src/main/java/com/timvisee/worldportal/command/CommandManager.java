package com.timvisee.worldportal.command;

import com.timvisee.worldportal.WorldPortal;
import com.timvisee.worldportal.command.executable.*;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnusedDeclaration")
public class CommandManager {

    /**
     * The list of commandDescriptions.
     */
    private List<CommandDescription> commandDescriptions = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param registerCommands True to register the commands, false otherwise.
     */
    public CommandManager(boolean registerCommands) {
        // Register the commands
        if(registerCommands)
            registerCommands();
    }

    /**
     * Register all commands.
     */
    @SuppressWarnings("SpellCheckingInspection")
    public void registerCommands() {
        // Register the base World Portal command
        CommandDescription worldPortalCommand = new CommandDescription(
                new WorldPortalCommand(),
                new ArrayList<String>() {{
                    add("worldportal");
                    add("wp");
                }},
                "Main command",
                "The main WorldPortal command. The root for all the other commands.", null);

        // Register the help command
        CommandDescription helpCommand = new CommandDescription(
                new HelpCommand(),
                new ArrayList<String>() {{
                    add("help");
                    add("hlp");
                    add("h");
                    add("sos");
                    add("?");
                }},
                "View help",
                "View detailed help pages about WorldPortal commands.",
                worldPortalCommand);
        helpCommand.addArgument(new CommandArgumentDescription("query", "The command or query to view help for.", true));
        helpCommand.setMaximumArguments(false);

        // Register the create command
        CommandDescription createCommand = new CommandDescription(
                new CreateCommand(),
                new ArrayList<String>() {{
                    add("create");
                    add("createstop");
                    add("createportal");
                    add("c");
                    add("cp");
                }},
                "Toggle portal creation",
                "Toggle the creation mode for a new portal.",
                worldPortalCommand);
        createCommand.addArgument(new CommandArgumentDescription("enable", "True to enable, false to disable", true));
        createCommand.setCommandPermissions("worldportal.command.create", CommandPermissions.DefaultPermission.OP_ONLY);

        // Register the remove command
        CommandDescription removeCommand = new CommandDescription(
                new RemoveCommand(),
                new ArrayList<String>() {{
                    add("remove");
                    add("removestop");
                    add("removeportal");
                    add("destroy");
                    add("destroyportal");
                    add("r");
                    add("rp");
                }},
                "Toggle portal removal",
                "Toggle the removal mode for portals.",
                worldPortalCommand);
        removeCommand.addArgument(new CommandArgumentDescription("enable", "True to enable, false to disable", true));
        removeCommand.setCommandPermissions("worldportal.command.remove", CommandPermissions.DefaultPermission.OP_ONLY);

        // Register the world create command
        CommandDescription createWorldCommand = new CommandDescription(
                new CreateWorldCommand(),
                new ArrayList<String>() {{
                    add("createworld");
                    add("cw");
                }},
                "Create world",
                "Create a new world, the name of the world must be unique.",
                worldPortalCommand);
        createWorldCommand.addArgument(new CommandArgumentDescription("world", "The name of the world to create.", false));
        createWorldCommand.addArgument(new CommandArgumentDescription("environment", "Normal, Nether or End", true));
        createWorldCommand.setCommandPermissions("worldportal.command.createworld", CommandPermissions.DefaultPermission.OP_ONLY);

        // Register the info command
        CommandDescription infoCommand = new CommandDescription(
                new InfoCommand(),
                new ArrayList<String>() {{
                    add("info");
                    add("i");
                }},
                "Info about modes",
                "Info about portal building modes that are currently enabled.",
                worldPortalCommand);
        infoCommand.setCommandPermissions("worldportal.command.info", CommandPermissions.DefaultPermission.ALLOWED);

        // Register the teleport command
        CommandDescription teleportCommand = new CommandDescription(
                new TeleportCommand(),
                new ArrayList<String>() {{
                    add("teleport");
                    add("tp");
                    add("t");
                    add("warp");
                    add("goto");
                    add("move");
                }},
                "Teleport to world",
                "Teleports to any another world." ,
                worldPortalCommand);
        teleportCommand.addArgument(new CommandArgumentDescription("world", "The name of the world to teleport to.", false));
        teleportCommand.addArgument(new CommandArgumentDescription("x", "X coordinate to teleport to or 'spawn'", true));
        teleportCommand.addArgument(new CommandArgumentDescription("y", "Y coordinate to teleport to", true));
        teleportCommand.addArgument(new CommandArgumentDescription("z", "Z coordinate to teleport to", true));
        teleportCommand.setCommandPermissions("worldportal.command.teleport", CommandPermissions.DefaultPermission.OP_ONLY);

        // Register the load world command
        CommandDescription loadWorldCommand = new CommandDescription(
                new LoadWorldCommand(),
                new ArrayList<String>() {{
                    add("loadworld");
                    add("load");
                }},
                "Load a world",
                "Load a world if it isn't loaded." ,
                worldPortalCommand);
        loadWorldCommand.addArgument(new CommandArgumentDescription("world", "The name of the world to load.", false));
        loadWorldCommand.setCommandPermissions("worldportal.command.loadworld", CommandPermissions.DefaultPermission.OP_ONLY);

        // Register the unload world command
        CommandDescription unloadWorldCommand = new CommandDescription(
                new UnloadWorldCommand(),
                new ArrayList<String>() {{
                    add("unloadworld");
                    add("unload");
                }},
                "Unload a world",
                "Unload a loaded world." ,
                worldPortalCommand);
        unloadWorldCommand.addArgument(new CommandArgumentDescription("world", "The name of the world to unload.", false));
        unloadWorldCommand.setCommandPermissions("worldportal.command.unloadworld", CommandPermissions.DefaultPermission.OP_ONLY);

        // Register the list world command
        CommandDescription listWorldCommand = new CommandDescription(
                new ListWorldCommand(),
                new ArrayList<String>() {{
                    add("listworlds");
                    add("listworld");
                    add("list");
                    add("worlds");
                    add("world");
                    add("lw");
                }},
                "List worlds",
                "Lists the available worlds and shows some additional information.",
                worldPortalCommand);
        listWorldCommand.setCommandPermissions("worldportal.command.listworlds", CommandPermissions.DefaultPermission.OP_ONLY);

        // Register the save command
        CommandDescription saveCommand = new CommandDescription(
                new SaveCommand(),
                new ArrayList<String>() {{
                    add("save");
                    add("s");
                }},
                "Save all portals",
                "Save all " + WorldPortal.getPluginName() + " portals.",
                worldPortalCommand);
        saveCommand.setCommandPermissions("worldportal.command.save", CommandPermissions.DefaultPermission.OP_ONLY);

        // Register the reload command
        CommandDescription reloadCommand = new CommandDescription(
                new ReloadCommand(),
                new ArrayList<String>() {{
                    add("reload");
                    add("rel");
                    add("refresh");
                }},
                "Reload " + WorldPortal.getPluginName(),
                "Reload the " + WorldPortal.getPluginName() + " plugin.",
                worldPortalCommand);
        reloadCommand.addArgument(new CommandArgumentDescription("force", "Force reload", true));
        reloadCommand.setCommandPermissions("worldportal.command.reload", CommandPermissions.DefaultPermission.OP_ONLY);

        // Register the reload permissions command
        CommandDescription reloadPermissionsCommand = new CommandDescription(
                new ReloadPermissionsCommand(),
                new ArrayList<String>() {{
                    add("reloadpermissions");
                    add("reloadpermission");
                    add("reloadperms");
                    add("rp");
                }},
                "Reload permissions",
                "Reload the permissions system and rehook the installed permissions system.",
                worldPortalCommand);
        reloadPermissionsCommand.setCommandPermissions("worldportal.command.reloadpermissions", CommandPermissions.DefaultPermission.OP_ONLY);

        // Register the status command
        CommandDescription statusCommand = new CommandDescription(
                new StatusCommand(),
                new ArrayList<String>() {{
                    add("status");
                    add("stats");
                    add("s");
                }},
                "Status info",
                "Show detailed plugin status.",
                worldPortalCommand);
        statusCommand.setMaximumArguments(false);
        statusCommand.setCommandPermissions("worldportal.command.status", CommandPermissions.DefaultPermission.OP_ONLY);

        // Register the version command
        CommandDescription versionCommand = new CommandDescription(
                new VersionCommand(),
                new ArrayList<String>() {{
                    add("version");
                    add("ver");
                    add("v");
                    add("about");
                    add("info");
                }},
                "Version info",
                "Show detailed information about the installed " + WorldPortal.getPluginName() + " version, and shows the developers, contributors, license and other information.",
                worldPortalCommand);
        versionCommand.setMaximumArguments(false);

        // Add the base command to the commands array
        this.commandDescriptions.add(worldPortalCommand);
    }

    /**
     * Get the list of command descriptions
     *
     * @return List of command descriptions.
     */
    public List<CommandDescription> getCommandDescriptions() {
        return this.commandDescriptions;
    }

    /**
     * Get the number of command description count.
     *
     * @return Command description count.
     */
    public int getCommandDescriptionCount() {
        return this.getCommandDescriptions().size();
    }

    /**
     * Find the best suitable command for the specified reference.
     *
     * @param queryReference The query reference to find a command for.
     *
     * @return The command found, or null.
     */
    public FoundCommandResult findCommand(CommandParts queryReference) {
        // Make sure the command reference is valid
        if(queryReference.getCount() <= 0)
            return null;

        // Get the base command description
        for(CommandDescription commandDescription : this.commandDescriptions) {
            // Check whether there's a command description available for the current command
            if(!commandDescription.isSuitableLabel(queryReference))
                continue;

            // Find the command reference, return the result
            return commandDescription.findCommand(queryReference);
        }

        // No applicable command description found, return false
        return null;
    }
}
