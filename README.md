<div align="center">

![JobActions Banner](https://github.com/MRollin03/JobActions/blob/master/Git%20banner.png)

# JobActions

**A Minecraft plugin for creating and managing item orders with in-game currency**

[![WIP](https://img.shields.io/badge/status-work%20in%20progress-yellow)](https://github.com/MRollin03/JobActions)
[![Issues](https://img.shields.io/badge/contributions-welcome-brightgreen)](https://github.com/MRollin03/JobActions/issues)

</div>

> [!IMPORTANT]
> This Project is WIP and probably has bugs.
> Further testing needed, gladly open issues

---

##  Description

JobActions is a powerful Minecraft plugin that enables players to create, list, and fulfill orders for items using an in-game currency system. The plugin provides an intuitive chest-based **graphical user interface (GUI)** for seamless order management.

---

##  Dependencies

This plugin requires the following dependencies to function properly:

### Required Plugins

- **[Vault](https://www.spigotmc.org/resources/vault.34315/)** - Economy API wrapper
- **Economy Plugin** (one of the following):
  - [XConomy](https://github.com/YiC200333/XConomy)
  - [iConomy](https://www.spigotmc.org/resources/iconomy-reloaded.11170/)
  - Any Vault-compatible economy plugin
- **Permissions Plugin**:
  - [LuckPerms](https://github.com/LuckPerms/LuckPerms) (recommended)
  - Any Vault-compatible permissions plugin

---

##  Features

- **📝 Create Orders** - Players can create orders for specific items or items they're currently holding
- **❌ Cancel Orders** - Players can cancel their own orders at any time
- **🏪 Order Market** - Interactive chest GUI displaying all open community orders
- **📋 List Orders** - View and manage all orders through an intuitive GUI
- **📦 Receive Orders** - Collect fulfilled orders through a dedicated vault GUI
- **🔐 Permissions System** - Fine-grained control over who can create, list, cancel, and receive orders
- **⚙️ Customizable Configuration** - Set order limits, fees, timeouts, and more

---

## 🎮 Commands

### Create an Order

```
/order create <item|hand> <amount> <price>
```

Creates an order for a specified item or the item currently in your hand.

**Parameters:**
- `item` - The item name (e.g., `oak_planks`)
- `hand` - Use the item currently held in your hand
- `amount` - The quantity of items
- `price` - The price in your server's currency

**Examples:**
```
/order create oak_planks 128 100
/order create hand 64 1000
```

---

### Cancel an Order

```
/order cancel [orderID]
```

Deletes a specific order by ID or opens the cancel GUI.

**Examples:**
```
/order cancel 12345    # Cancel a specific order
/order cancel          # Open the cancel GUI
```

---

### Open Market

```
/market
```

Opens the market GUI where all current orders are displayed. Item stacks are interactive, with order information stored in the item's metadata/lore.

---

### List Orders

```
/order list [user|item]
```

> **Note:** This function is not yet fully implemented.

Lists orders with optional filtering by user or item. Opens a GUI for managing orders.

**Examples:**
```
/order list ArasBuilds    # Filter by username
/order list diamond       # Filter by item name
```

---

### Access Vault

```
/order vault
```

Opens the vault GUI where you can receive your fulfilled orders.

---

## Permissions

| Permission | Description |
|------------|-------------|
| `JobActions.create` | Allows players to create orders |
| `JobActions.list` | Allows players to list orders |
| `JobActions.cancel.self` | Allows players to cancel their own orders |
| `JobActions.cancel.others` | Allows players to cancel other players' orders |
| `JobActions.received` | Allows players to receive fulfilled orders |

---

## Configuration

The `config.yml` file provides extensive customization options:

```yaml
# Debug mode activated?
debug: false

# Order system configuration
Order:
  # Base fee charged when creating a new order (prevents abuse as teleportation)
  order-basefee: 10
  
  # Additional fee as percentage of order price
  order-fee-percentage: 10
  
  # Enable percentage-based fees
  order-fee-percentage-activated: true
  
  # Enable base fee
  order-base-fee-activated: true
  
  # Maximum number of simultaneous orders per player
  order-limit: 5
  
  # Maximum items per order (in stacks: 320 = 5 stacks of 64)
  order-limit-stack-count: 5
  
  # Order expiration time in minutes
  order-timeout: 60
  
  # Interval for checking order expiration (in minutes)
  order-time-interval: 10080
  
  # Enable automatic order expiration
  order-timeout-activated: true
```

---

##  Installation

1. Download the latest release of JobActions
2. Place the `.jar` file in your server's `plugins` folder
3. Ensure all dependencies (Vault, economy plugin, permissions plugin) are installed
4. Start or restart your Spigot/Paper server
5. Configure the plugin by editing `plugins/JobActions/config.yml`
6. Reload the plugin or restart the server to apply changes

---

## 🤝 Contributing

Contributions are welcome! If you encounter bugs or have suggestions:

1. Check existing [issues](https://github.com/MRollin03/JobActions/issues)
2. Open a new issue with detailed information
3. Submit pull requests with improvements

---

## License

This project is licensed under the **MIT** License - see the LICENSE file for details.

---

## 💬 Support

For support, bug reports, or feature requests, please [open an issue](https://github.com/MRollin03/JobActions/issues) on GitHub.

---

<div align="center">

**Made with ❤️ for the Minecraft community**

</div>
