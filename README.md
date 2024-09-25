# JobActions 

> [!IMPORTANT]
> This Project is WIP and prbably has a bunch of bugs.
> further testing needed, gladly open issues

## Description
JobActions is a Minecraft plugin that enables players to create, list, and fulfil orders for items using an in-game currency. The plugin provides commands for creating and managing orders through a graphical user interface (GUI).

## Features

- **Create Orders:** Players can create orders for specific items or items they are currently holding.
- **Order Market**:  Chest GUI with available orders.
- 
- **List Orders:** Players can list all orders, filter orders by user or item, and manage orders through a GUI.
- **Receive Orders:** Players can receive their fulfilled orders through a GUI.
- **Permissions:** Fine-grained permissions control who can create, list, cancel, and receive orders.
- **Configuration:** Customizable options to set order limits, fees, and timeouts.

## Commands

### `/order create <item|hand> <amount> <price>`

Creates an order for a specified item or the item in hand.

- **item:** The item name (e.g., `oak_planks`).
- **hand:** Use `hand` to create an order for the item currently held in hand.
- **Amount:** The number of items.
- **Price:** The price in MetaCoins.

**Examples:**
- `/order create oak_planks 128 100` - Orders 128 oak planks for 100 MetaCoins.
- `/order create hand 64 1000` - Orders 64 of the item in hand for 1000 MetaCoins.

### `/market`
Opens market/Chest GUI where all current ordes are listed.
Itemstack are interactable and order-infomation is 
stored in the Metadate/lore of the itemstack itself.


### `/order list [user|item]` 
>[!NOTE]
> Function is not yet implemented

Lists orders, optionally filtered by user or item. Opens a GUI for managing orders.

- **user:** Filter by username (e.g., `/order list ArasBuilds`).
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
order-base fee: 10 # The fee the person creates a new order. This is mainly to prevent using orders as an item teleportation system.
order-fee-percentage: # The extra fee in percentage, added on top of  the base fee
order-fee-percentage-activated: true # If true, the fee is a percentage of the initial price rather than a fixed amount of money.
order-limit: 5 # The maximum number of orders that can be placed simultaneously.
order-limit-item-count: 320 # The maximum amount of items per order. 320 = 5 stacks.
order-timeout: 10080 # The timeout for an order in minutes (e.g., 7 days).

Installation
- Download the latest release of the plugin.
