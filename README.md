# 🚫 AliienItemBlacklist

A lightweight, efficient, and incredibly easy-to-use Minecraft PaperMC plugin designed to completely stop players from interacting with illegal or blacklisted items on your server. Fully updated with **Paper 26.1 support**!

This plugin was initially thought to be used for items you shouldn't be able to obtain while playing Minecraft in a legit way (like bedrock and barrier blocks, for example), but you are free to list any item you want in the blacklist section of the config file!

## ✨ Features
* **In-Game Editor GUI:** Configure everything within the plugin (except messages) seamlessly in-game via a sleek menu. Say goodbye to typing out tedious add/remove commands!
* **Item Blacklist:** You can list any items you want to completely blacklist from the server, disallowing players from having them in any way (whether it is from picking them up from the floor, grabbing from a container, etc.).
* **Smart Anti-Crafting:** Actively prevents players from crafting blacklisted items in the crafting grid, rather than awkwardly deleting the item after the fact.
* **Disabled Worlds:** Want a free-for-all dimension? You can now specify worlds where the blacklist restrictions will be completely ignored.
* **Staff Alerts:** Broadcasts a fully customizable message alert to online staff when a player attempts to use an illegal item.
* **Rich Customization:** Includes optional message prefixes, toggleable GUI/alert sounds, and full support for legacy color codes (e.g., `&f`) and hex codes (e.g., `&#ffffff`) across all messages.
* **bStats Integration:** Lightweight metrics tracking to help improve the plugin over time.

## 🔐 Permissions
* `aliien.itemblacklist.bypass`
  * **Description:** Allows the player to bypass all blacklist restrictions.

* `aliien.itemblacklist.bypass.<item name>`
  * **Description:** Allows the player to bypass blacklist restrictions for a specific item only.

* `aliien.itemblacklist.alert`
  * **Description:** Players with this permission will receive the alert message when someone else interacts with a blacklisted item.

* `aliien.itemblacklist.reload`
  * **Description:** Allows the player to reload the plugin's configuration in-game.

* `aliien.itemblacklist.version-notify`
  * **Description:** Allows the player to get in-game notification when there are new plugin updates.
  
## 📦 Installation
1. Download the latest `AliienItemBlacklist.jar` from the Releases page.
2. Drop the `.jar` file into your server's `plugins/` folder.
3. Restart your server.
4. Use the new **In-Game Editor Menu** to quickly set up your blacklist, or manually configure the newly organized files (`config.yml`, `settings.yml`, and `messages.yml`). Use the reload command to apply manual file changes instantly!

## 🛠️ Support

If you encounter any issues while using this or any of my plugins, or have any suggestions to make the plugin even better, feel free to join my discord server! I am always very active in there, so support should be quick unless I happen to be busy.

https://discord.gg/K7RKrWBaV7

---
**Thank you for using my plugin!**