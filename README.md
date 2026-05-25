
# Clutch by AbyssalMC
This mod shows whether it is possible to block ladder clutch (bladder) and boat clutch. It also contains many quality of life competitive clutching features.
Modrinth download: [Modrinth](https://modrinth.com/mod/clutch) | Youtube: [AbyssalMC](https://youtube.com/@AbyssalMC)

**Clutch possibility indicator and difficulty scale**
- Can be toggled on and off with O and configured in controls)
- The block in which the player is looking at is assumed to be where the player lands, and the clutch possibility is based on this.
- It uses the player's position and speed and calculates whether the clutch is possible or not based on the following colors: 
- Red: impossible to clutch
- Orange: (difficult) bladder peak tier greater than 3 blocks | 2t no stall boat
- Yellow: (medium) bladder peak tier greater than 2.75 blocks | 3t no stall boat
- Lime: (easy) bladder peak tier greater than 2.25 blocks | 3t stall boat
- Green: (very easy) all other possible bladders | 2t stall boat

**Better resets**
- A reset hotkey can be configured in controls
- The reset point can be set using /clutch platform (no arguments) which is stored in persistent data.
- It teleports you back to the starting platform with a good position and angle.
- You can change the offset from the default position by using /clutch platform [int offset].
- Pressing the reset hotkey also works in guis unlike standard auto text hotkeys
- Positioned farther forward for faster resets than /kill
- The pitch by default is set to 77.0 degrees, but this can be changed through /clutch pitch [angle] which is stored locally.
- Automatic movement can also be enabled through /clutch automov enable, which makes the player move backwards and jump when reset.

**Setup generation**
- Setups can be tedious to set up, so the command /clutch setupgen [setupid] allows them to be generated automatically.
- The ids consist of the items and slots of the clutch split by S.
- Enchants can be added by adding E around the enchant
- Examples: /clutch setupgen 13oak_log1 (log ladder), /clutch setupgen 13netherite_hoe1S14enchanted_bookEefficiency5E1 (single enchant)

**Cursor offset**
- There is a way to offset your cursor in GUIs from its original position in the center of the screen (960,540)
- However it is tedious to set up after each attempt, so this mod replicates it exactly to save time
- This can be done with /clutch cursoroffset [cursorX] [cursorY], and can be disabled using /clutch disableoffset

**Recipe book disabling**
- For difficult crafts it can get quite annoying when you accidentally open the recipe book.
- To stop the recipe book from opening, use /clutch recipebook disable
- To completely obscure the button as well, use /clutch recipebook occlude instead.

**Input locators**
- Show where you click in guis using /clutch inputloc.
- 3 different locator styles.
- Can be always shown, only on misses, or disabled.

**GUI input sounds**
- Set a sound to play when you make an input using /clutch inputsounds [sound].
- This sound may be set to either "osu" or "basskick".
  
**Custom GUI time**
- A custom gui time in ticks can be set using /clutch guitime set [time].
- It may be disabled using /clutch guitime default or /clutch guitime set 0.

**Attempt tracker**
- Track the number of attempts a clutch takes using /clutch attempts get.
- Remove a platform's attempts using /clutch attempts remove.
- Clear all persistent attempts using /clutch attempts clear.

**Health, hunger, and saturation modifiers**
- Set the player health using /clutch health [health]
- The same can be done with hunger and saturation using /clutch hunger and /clutch saturation.

**Velocity command**
- Use /clutch velocity <x> <y> <z> to set the player's velocity vector.


![mod icon](https://cdn.modrinth.com/data/cached_images/74b2a2f95183019fcc775191ff24e749ef464790.png)

