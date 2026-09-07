# Changelog

## Region Influencer done
**Sep 7, 2026**
- Now Burst uses an atlas for empty projects by default
- Controller editing menu gets closed upon loading a project or creating a new one
- Added an abstraction layer for all influencer displays that handles common UI settings, header and folding/unfolding
- Fixed a bug in EditableGraph when points deletion was not synced with Controller
- Implemented RegionInfluencerDisplay supporting all 3 types of RegionInfluencer (Single, Random, Animated)
- Implemented ListPicker UI component for picking multiple items from a list and arranging their order
- ControllerPanel now adequately reacts to controller deletion and renaming
- Removed source textures from SkinComposer project directory

## EditableGraph, DisplayComponents
**Sep 5, 2026**
- Added 2 more fonts for UI
- Added ControllerPanel class which is the right-side panel for editing a single ParticleController
- Outlined classes for different types of Influencers
- Added DisplayComponent class which manages UI and manipulation of Influencer or Emitter components
- Added EditableGraph UI class to edit scale/timeline values of different influencer components
- Implemented RegularEmitterDisplay for displaying and editing RegularEmitter properties
- Renamed InputChecks -> UserInput and added shortcuts there for TextField filters and input confirmation listener template
- ControllersPanel renamed into ProjectPanel to avoid confusion
- Forcing Adoptium Temurin Java 17 for Gradle

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
