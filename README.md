## 1) Project Installation Guide
- Extract the PodomoroTimer.7z file
- Open the PodomoroTimer folder and run the PodomoroTimer.exe file
- The App should be launched.

## 2) Changes made to the Project
- MainController user interface has been cleaned up
- Buttons on the MainController are now icons
- Added a mute/unmute functionality as well as a volume slider
- MusicPlayerGUI is fully removed, as the music player is controlled through the MainController
- MusicPlaylistDialogFX also removed, as the background music are now presets found on the settings menu
- The music will play/pause depending on the timer play/pause state(If timer started, music starts, and vice versa)
- Added End State button on main controller to skip the current state(Focus/Rest)
- Added a simple gimmick, which is a simple tracker where whenever focus state ends, a cup of coffee will be added on the bottom, and the size varies depending on the focus time. So the user can check how many session they did from the total of the coffee appeared.

## 3) Additional notes
- The Groovy playlist has a slight delay at the beginning of the track; this delay is part of the audio file itself and not caused by the code
- Because JAVA cannot directly configure with Windows, we've decided that the safer method is to instruct the user to turn on DND from Windows Settings itself
- This App only works for Windows 10/11.

## 4) Disclaimer

This project is intended for **educational purposes only**.  
All content, including code, assets, and references, is used in good faith for learning and demonstration.  
**Commercial use is strictly prohibited.**  
All copyrights and trademarks belong to their respective owners.
