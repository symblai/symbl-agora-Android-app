


# Symbl's APIs empower developers to enable:

Real-time analysis of free-flowing discussions to automatically surface highly relevant summary discussion topics, contextual insights, suggestive action items, follow-ups, decisions, and questions.\
Voice APIs that makes it easy to add AI-powered conversational intelligence to either telephony or WebSocket interfaces.
Conversation APIs that provide a REST interface for managing and processing your conversation data.
Summary UI with a fully customizable and editable reference experience that indexes a searchable transcript and shows generated actionable insights, topics, timecodes, and speaker information.

# Symbl Agora Android App
This tutorial enables you to quickly get started with adding Symbl.ai to an Android app to enable real-time video calls, voice calls, or interactive broadcasting using Agora with a WebSocket.

With your sample app you can:

* Start and end audio/visual communication between two users.
* Join a communication channel.
* Mute and unmute audio.
* Enable and disable video.
* Switch between the front and rear cameras.
* Receive and show live transcriptions and insights from Symbl
## Prerequisites
* Java version 8
* Physical Android device 
* Android Studio 3.3 or above
* Android device (e.g. Nexus 5X). A real device is recommended because some simulators have missing functionality or lack the performance necessary to run the sample.
* Agora Account
* Symbl Account
## Quick Start
This section shows you how to prepare, build, and run the sample application.

### Obtain an App Id
To build and run the sample application, get an App Id:

Create a developer account at agora.io. Once you finish the signup process, you will be redirected to the Dashboard.

Navigate in the Dashboard tree on the left to Projects > Project List.

Save the App Id from the Dashboard for later use.

Generate a temp Access Token (valid for 24 hours) from dashboard page with given channel name, save for later use.

Open src/main/res/values and edit the strings.xml file. In the agoraKit declaration, update <#Your App Id#> with your App Id, and assign the token variable with the temp Access Token generated from dashboard.
```
<string name="agora_app_id">Agora AppId</string>
<string name="agora_channel">Agora Channel Name</string>
<string name="agora_access_token">Agora accessToken</string>

```
### Obtain Symbl App ID and App Secret
Create an account in the Symbl Console if you don't have one already.
After you login, you will find your appId and appSecret on the home page.
Store your appId and appSecret in the .env file in the root level of the application (example below).
Open src/main/res/values and edit the strings.xml file. In there, update symblAppId and symblAppSecret that you see on Symbl console.
```
<string name="symbl_app_id">Symbl AppId</string>
<string name="symbl_app_secret">Symbl AppSecret</string>
```
### Run the application
Open project with Android Studio, connect your Android device, build and run.

Or use ```Gradle``` to build and run

### Community
If you have any questions, feel free to reach out to us at devrelations@symbl.ai or thorugh our Community Slack or our developer community

This guide is actively developed, and we love to hear from you! Please feel free to create an issue or open a pull request with your questions, comments, suggestions and feedback. If you liked our integration guide, please star our repo!

This library is released under the MIT License
