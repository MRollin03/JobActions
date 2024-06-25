# JobActions

JobActions is a Minecraft plugin that enables players to create, list, and fulfill orders for items using an in-game currency called MetaCoins. The plugin provides commands for creating and managing orders through a graphical user interface (GUI).

## Features

- **Create Orders:** Players can create orders for specific items or for items they are currently holding in their hand.
- **List Orders:** Players can list all orders, filter orders by user or item, and manage orders through a GUI.
- **Receive Orders:** Players can receive their fulfilled orders through a GUI.
- **Permissions:** Fine-grained permissions control who can create, list, cancel, and receive orders.
- **Configuration:** Customizable options to set order limits, fees, and timeouts.

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
- `JobActions.listJobActions.listJobActions.list` - Permission to list orders.
- `JobActions.cancel.self` - Permission to cancel own orders.
- `JobActions.cancel.others` - Permission to cancel others' orders.
- `JobActions.received` - Permission to receive orders.

## Configuration

The `config.yml` file provides several options for customizing the plugin:

```yaml
order-fee: 10 # The fee the person who fulfills the order will pay to prevent using orders as an infinite supply of items.
order-fee-percentage: true # If true, the fee is a percentage of the initial price rather than a fixed amount of money.
order-limit: 5 # The maximum amount of orders that can be placed at once.
order-limit-item-count: 320 # The maximum amount of items per order. 320 = 5 stacks.
order-timeout: 10080 # The timeout for an order in minutes (e.g., 7 days).

Installation
- Download the latest release of the plugin.
