# Changelog

## Controllers management, atlas loading
**Sep 2, 2026**
- Consolidated all assets in assets folder root (there are not going to be as many enough assets to separate them into subfolders anyway)
- Removed test PFX from repo
- Set min/max size limits for Desktop window
- Editor settings are functional now and are properly saved in Preferences
- Effects can now be saved to a PFX file
- Controllers can be cloned, removed, added and made invisible now
- Fixed the issue Flame had about losing information on .atlas file used in the particle system upon loading and saving it
- Atlas files can now be loaded and used by effects
- A default particle effect is now created upon session start or creating a new file
  -Made Settings entries and defaults statically typed and centralized in Settings class
- Updated README with miserable explanation why even bother use Burst instead of Flame

## Camera, PFX loading, Settings
**Aug 30, 2026**
- Made gizmo a bit thinner
- Added Skin Composer assets and styles for PopColorPicker
- Added a controllable orbiting camera
- Particle presets can now be loaded and get rendered, yippee
- Split EditorUI elements into separate classes
- Implemented EditorPanel with editor settings
- Implemented TopBar and ControllersPanel UI elements (WIP)
- Editor now uses Preferences

## Acting busy
**Aug 28, 2026**
- Added SkinComposer project to the repo
- Added gizmo GLB mesh
- Renamed LWJGL3 module to Desktop and removed Core
- Started work on EditorUI
- Added FilePicker util class to pick files via LWJGL NativeFileDialog
- Added WorldGrid
