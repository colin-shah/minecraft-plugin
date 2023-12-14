# Auto Tree Chopper Plugin

## Overview

This Minecraft Spigot plugin introduces an Auto Tree Chopper, a mechanized tree-chopping machine with unique behaviors and interactions. The plugin allows players to construct a machine with different materials, each providing varying chopping speeds and bonus features.

## Setup

### Machine Construction

1. **Base:** Place a diamond, emerald, or gold block (“bits”) on the ground.
2. **Top:** Place a chest on top of the bit block.

### Control Mechanism

- If a chest is on top of a bit block, the machine is activated.
- The machine deactivates when the chest is full.

## Features

### Chopping Speed Variation

- **Diamond Block:** Fastest chopping speed.
- **Emerald Block:** Moderate speed with a unique bonus feature (particle effects).
- **Gold Block:** Slower speed with higher yield or special effect.

### Special Blocks Interaction

- **Ferns/Mushrooms/Flowers:** The machine breaks apart upon contact.
- **Water:** Floats and continues operating.
- **Sponge/Ice:** Triggers specific behaviors or effects when encountered.

## Usage

1. Build the machine following the setup instructions.
   - Here we have use command /createcommand (which you can find in plugin.yml)
2. The machine will chop the whole tree
3. A notification will be sent when the chest is full, and the machine will deactivate.

### Challenges or Issues
1. Differentiate the chopping speed by material