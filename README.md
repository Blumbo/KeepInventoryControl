# Keep Inventory Control
Keep Inventory Control is a fabric mod that gives you control over which types of items will be kept and which dropped when a player dies.  
  
Whitelisted items are **kept** upon death if `keepInventory` gamerule is set to `false`.  
Blacklisted items are **dropped** upon death if `keepInventory` gamerule is set to `true`.  
  
Item lists are universal for each player and can only be modified by the server owner / an operator (if in multiplayer).

## Commands
`/keepinvcontrol info` shows general information about the usage of the mod.  
`/keepinvcontrol <whitelist|blacklist> add <item>` to add an item to whitelist or blacklist.  
`/keepinvcontrol <whitelist|blacklist> remove <item>` to remove an item from whitelist or blacklist.  
`/keepinvcontrol <whitelist|blacklist> addxp` to add experience to whitelist or blacklist.  
`/keepinvcontrol <whitelist|blacklist> removexp` to remove experience from whitelist or blacklist.  
`/keepinvcontrol <whitelist|blacklist> list` shows all listed items.  
