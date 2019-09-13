# Author

Troy Cardozo

# Version

0.1

# Ore regen docs:

# Entities

# Description:

Entities are a set of different config files that run under the same plugin, having this feature means you can have multiple cave
systems with different ores, set durations etc.

# Commands:

# /ore || /ore help

Shows the commands this plugin can execute along with what it does.

# /ore entity create <name>

Create an entity that will create it’s own config file

# /ore entity set <name>

For OP use only Change to a different entity so you can set or configure it.

# /ore entity list

This will list all the entities that were created and highlight the selected one.

# /ore entity clearall

Removes all spawned blocks associated to the Selected entity

# /ore entity delete <name>

Remove an entity from the config.. info: if you want this completely gone along with the blocks etc., make sure you use select the entity you’re about to delete. Warning: when this command runs the first entity will be selected.

# Range

# Description:

When an entity is created you need to set a range, when you enter this range you will essentially run the config file entitled to it. Warning: Do not overlap ranges.

# Commands:

# /ore entity coords set <value>

The number set will determine how many blocks from each side the range will be, so if you add 50, it will add 50 blocks on all sides in range from where you’re standing. Also makes an outline for 5 seconds (depends where you are you can see it). You can change this anytime.

# /ore entity coords tp

Teleports you to the middle of the range, and creates a outline for 5 seconds

# /ore entity coords show

Shows outline of the range

# /ore entity coords hide

Hides outline of range

# Config variables

# Description:

Change the config for selected entity, eg: ore regen duration, default block type etc.

# Commands

# /ore config duration <time>

Changes the duration of how long it takes for to regenerate. Warning ⚠ set time in nice string format for example /ore config duration 1m 10s

# /ore config defaultblock <block>

Changes the default block to what you desire, you can utilize this when making special caves like nether cave

# /ore config percent <value>

Sets percent to block in your hand only if its in the list for respawn. Warning make sure the list values add up to 100% if not the ore picker will not work properly

# /ore config types

Gives list of all the ores in the list and the percentages assigned to them.

# /ore config placedblocks

Tells the amount of blocks assigned to the entity. Also toggle the command to highlight all the placed blocks.

# Tools

# Description:

You get 2 special tools provided by the plugin, Ore placer & Ore Customizer the ore placer is used as a way to place the special
blocks and the ore customizer is used to toggle the block you want to add/remove from the list. Warning ⚠ you need to set a dedicated
range for the entity before starting to place blocks.

Once you add a block to the list you will noticed its percentage is set to 0. You can change this by holding the block you just added
then set the percentage like /ore config percent 10
Warning ⚠ make sure all the blocks in the list adds up to 100

# Commands

# /ore tools

Gives an Enchanted bedrock and enchanted stick. Warning make sure you have empty inventory slots.
