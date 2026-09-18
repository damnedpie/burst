# Burst Particle Editor
[![Static Badge](https://img.shields.io/badge/libGDX-1.14.2-black?style=for-the-badge&labelColor=%23ff0000)](https://libgdx.com/)
[![GitHub License](https://img.shields.io/github/license/damnedpie/burst?style=for-the-badge)](https://github.com/damnedpie/burst/blob/main/LICENSE)
[![GitHub Repo stars](https://img.shields.io/github/stars/damnedpie/burst?style=for-the-badge&logo=github&logoSize=auto&color=%23FFD700)](https://github.com/damnedpie/burst/stargazers)
![Alt text](desktop/src/main/resources/logo512.png?raw=true "the logo")

## What is this?

Burst is an alternative to Flame, the 3D particle VFX editor for libGDX. My goal with Burst is to provide better UX and bump productivity when it comes to 3D particle VFX editing.

If you end up using Burst, please consider starring the repo - I am just curious how many people out there also struggled with Flame hard enough to bother looking for an alternative (and ended up reading this, lol).

## Why make this?

When I was making my first libGDX-powered 3D game, Cosmic Towers, I relied heavily on Flame editor. I needed about 50 different particle VFX presets for different towers, enemies, heroes and projectiles so my experience with Flame was quite extensive.

There are some issues, however, that made the work almost unbearable:

- Flame often crashed when using hotkeys like Ctrl+C / V / X / Z etc.
- Flame often crashed in color picker context.
- Flame often crashed simply by getting interacted with in any way.
- Flame would store atlas information (a link to .atlas file) only if the atlas was opened in the editor at least once during current session AND a region of the atlas would explicitly be assigned to RegionInfluencer. This means that every time you open a particle VFX project, you need to manually load the atlas and assign a region to the influencer in order to get this data saved next time you save the VFX project. Combined with random crashes, the time loss and frustration is quite noticeable.
- Flame would not remember the editor preferences, such as JSON output style, pretty print and so on.
- Flame can't use GLTF for ModelInstance emitters.
- Flame's UI is arguably not the most productive.

Wanting to address these issues and bring the UI from Swing to Scene2D along the way, I decided to put some time into making Burst as an alternative 3D particle editor.

## What's done?

- Full 2D (Billboards, Point Sprites) particle functionality
- Loading / saving PFX
- Hotkeys (Ctrl+S for saving, Ctrl+O for opening, Ctrl+N for new file)
- Editor settings and persistent preferences
- More comfortable graphs, gradients and color picking

## What's left to do?

- ModelInstance based particles. It's not hard to do if we stick to the old GD3B models but they are not that fun to work with and I want the editor to work with GLB/GLTF. This is tricky due to how libGDX ParticleEffect saving/loading works right now.
- ParticleController based particles
- Undo/redo (maybe?)
