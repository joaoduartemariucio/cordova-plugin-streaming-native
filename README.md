# Credits

Stuart McCamley & Nicholas Hutchind 

## Description

This plugin allows you to stream audio and video in a fullscreen, native player on iOS and Android.

* 1.0.0 Works with Cordova 3.x
* 1.0.1+ Works with Cordova >= 4.0

## Installation

```
cordova plugin add https://github.com/joaoduartemariucio/cordova-plugin-streaming-native.git
```

### iOS specifics
* Uses the Swift MediaPlayer.
* Tested on iOS 10.x
* Works on iOS 12

### Android specifics
* Uses VideoView and MediaPlayer.
* Creates two activities in your AndroidManifest.xml file.
* Tested on Android 4.0+

## Play streaming
```javascript
if(window && window.plugins && window.plugins.streamingMedia) {
  let StreamingMedia = window.plugins.streamingMedia
  StreamingMedia.playiOS('url')
}
```
## Play file local
```javascript
if(window && window.plugins && window.plugins.streamingMedia) {
  let StreamingMedia = window.plugins.streamingMedia
  StreamingMedia.playLocaliOS("local file path")
}
```
## PlayList of urls
```javascript
if(window && window.plugins && window.plugins.streamingMedia) {
  let StreamingMedia = window.plugins.streamingMedia
  StreamingMedia.playPlaylist("array to urls")
}
```

## Usage Andoird

```javascript
  var videoUrl = STREAMING_VIDEO_URL;

  // Just play a video
  window.plugins.streamingMedia.playVideo(videoUrl);

  // Play a video with callbacks
  var options = {
    successCallback: function() {
      console.log("Video was closed without error.");
    },
    errorCallback: function(errMsg) {
      console.log("Error! " + errMsg);
    },
    orientation: 'landscape',
    shouldAutoClose: true,  // true(default)/false
    controls: true // true(default)/false. Used to hide controls on fullscreen
  };
  window.plugins.streamingMedia.playVideo(videoUrl, options);


  var audioUrl = STREAMING_AUDIO_URL;

  // Play an audio file (not recommended, since the screen will be plain black)
  window.plugins.streamingMedia.playAudio(audioUrl);

  // Play an audio file with options (all options optional)
  var options = {
    bgColor: "#FFFFFF",
    bgImage: "<SWEET_BACKGROUND_IMAGE>",
    bgImageScale: "fit", // other valid values: "stretch"
    initFullscreen: false, // true(default)/false iOS only
    successCallback: function() {
      console.log("Player closed without error.");
    },
    errorCallback: function(errMsg) {
      console.log("Error! " + errMsg);
    }
  };
  window.plugins.streamingMedia.playAudio(audioUrl, options);

  // Stop current audio
  window.plugins.streamingMedia.stopAudio();

  // Pause current audio (iOS only)
  window.plugins.streamingMedia.pauseAudio();

  // Resume current audio (iOS only)
  window.plugins.streamingMedia.resumeAudio();  

```
