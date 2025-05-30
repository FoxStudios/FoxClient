# FoxClient
A Fabric (and Quilt) Mod that changes the look of the Minecraft UI and adds some features.

## Features
### Ingame HUD:
- FPS
- Server IP
- Coordinates
- TPS
- Biome Display
#### Custom main and pause menu
#### Discord RPC

### Supported Languages: 
- English
- German
- Filipino/Tagalog
- Dutch
- Spanish (España)

## Planned features:
- Friend system
- chat system (maybe)
- togglable modules (e.g: more particle effects, autotext)

## FAQ
### Can I use this on a Vanilla server?
Yeah sure. FoxClient is 100% client side and doesn't require anything to be installed on the server.
### Do settings exist to tweak the HUD?
Yes, you can access them via Right_CTRL or via the main menu
### Can I toggle the HUD?
Sure! Just press F6. (key is changable via the minecraft keybind settings) 

## TODO:
- Make the HUD more customizable
- More stability improvements
- Add more language translations (feel free to add more translations and make a pull request)

### Build Instructions

#### Please note that the latest version may be unstable!

Requirements: 
- Java 17 or higher

```bash
git clone https://github.com/FoxStudios/FoxClient.git
cd FoxClient
git submodule update --init --recursive 
java -jar brachyura-bootstrap-0.jar build
```
Copy the foxclient-x.x.x.jar (name may vary) from build/libs into your minecraft mods folder.

### How to contribute?

[Contribution Instructions](contribute.md)

### Is there gonna be a forge version?
NO [here's why](forge.md)

###### Copyright (C) 2025 FoxStudios
