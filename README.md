# Burst Particle Editor

![Alt text](desktop/src/main/resources/logo512.png?raw=true "the logo")

## What is this?

Burst is an alternative to Flame, the 3D particle VFX editor for libGDX. My goal with Burst is to provide better UX and bump productivity when it comes to 3D particle VFX editing.

If you end up using Burst, please consider starring the repo - I am just curious how many people out there who struggled with Flame hard enough to bother looking for an alternative (and resulting reading this, lol).

## Why make this?

When I was making my first libGDX-powered 3D game, Cosmic Towers, I relied heavily on Flame editor. I needed about 50 different particle VFX presets for different towers, enemies, heroes and projectiles so my experience with Flame was quite extensive.

There are some issues, however, that made the work almost unbearable:

- Flame often crashed when using hotkeys like Ctrl+C / V / X / Z etc.
- Flame often crashed in color picker context.
- Flame would store atlas information (a link to .atlas file) only if the atlas was opened in the editor at least once during current session AND a region of the atlas would explicitly be assigned to RegionInfluencer. This means that every time you open a particle VFX project, you need to manually load the atlas and assign a region to the influencer in order to get this data saved next time you save the VFX project. Combined with random crashes, the time loss and frustration is quite noticeable.
- Flame would not remember my editor preferences, such as JSON output style, pretty print and so on.
- Flame's UI is arguably not the most productive.

Wanting to address these issues and bring the UI from Swing to Scene2D along the way, I decided to put some time into making Burst as an alternative 3D particle editor.
