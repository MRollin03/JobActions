# JobActions

JobActions is a Minecraft plugin that allows players to create, list, and fulfill orders for items using an in-game currency (MetaCoins). The plugin provides commands for creating orders, listing orders, receiving ordered items, and managing orders through a graphical user interface (GUI).

## Features

- **Create Orders:** Players can create orders for specific items or items they are holding in their hand.
- **List Orders:** Players can list all orders, filter orders by user or item, and manage orders through a GUI.
- **Receive Orders:** Players can receive their fulfilled orders through a GUI.
- **Permissions:** Fine-grained permissions to control who can create, list, cancel, and receive orders.
- **Configuration:** Customizable configuration options to set order limits, fees, and timeouts.

## Commands

### `/order create <item|hand> <amount> <price>`

Creates an order for a specified item or the item in hand.

- **item:** The item name (e.g., `oak_planks`).
- **hand:** Use `hand` to create an order for the item currently held in hand.
- **amount:** The number of items.
- **price:** The price in MetaCoins.

**Examples:**
- `/order create oak_planks 128 100` - Orders 128 oak planks for 100 MetaCoins.
- `/order create hand 64 1000` - Orders 64 of the item in hand for 1000 MetaCoins.

### `/order list [user|item]`

Lists orders, optionally filtered by user or item. Opens a GUI for managing orders.

- **user:** Filter by username (e.g., `/order list Folas1337`).
- **item:** Filter by item name (e.g., `/order list diamond`).

### `/order received`

Opens a GUI for receiving fulfilled orders.

## Permissions

- `JobActions.create` - Permission to create orders.
- `JobActions.list` - Permission to list orders.
- `JobActions.cancel.self` - Permission to cancel own orders.
- `JobActions.cancel.others` - Permission to cancel others' orders.
- `JobActions.received` - Permission to receive orders.

## Configuration

The `config.yml` file provides several options for customizing the plugin:

```yaml
order-fee: 10 # The fee the person who fulfills the order will be paying. This is mainly to prevent using orders as an infinite supply of items.
order-fee-percentage: true # If this is set to true, the fee is a percentage of the initial price rather than a fixed amount of money.
order-limit: 5 # The maximum amount of orders you can place at once.
order-limit-item-count: 320 # The maximum amount of items you can ask for per order. 320 = 5 stacks.
order-timeout: 10080 # The timeout for an order in minutes. This example means 7 days.

Installation
- 1 Download the latest release of the plugin.
- 2 Place the JobActions.jar file in your server's plugins directory.
- 3 Start your server to generate the default configuration files.
- 4 Customize the config.yml file as needed.
- 5 Restart your server to apply the configuration changes.

###Development
`Prerequisites`
- Maven
- Java Development Kit 21 (JDK)

###Contributing
Contributions are welcome! Please open an issue or submit a pull request.

`License`
This project is licensed under the MIT License.

###Acknowledgements
Special thanks to the Spigot and Bukkit communities for their excellent documentation and support.

###Contact
For any questions or support, please open an issue on the GitHub repository.

Happy crafting!

