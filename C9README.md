
# FavDish
Native android sample app developed by the contextual android team, used in testing sdk features,
this is also the primary app used in testing CST conditions via BDD. 

### Build Instructions
- make sure to follow contextual integration steps on our [docs](https://dashboard.pointzi.com/docs/sdks/android/integration/)
- after a successful build upload the app to AppCenter
- verify test app meets [QA testing requirements](https://streethawk.atlassian.net/wiki/spaces/PPD/pages/1900707864/SDK+Testing+Requirements)

To run this application on your device, simply run

```
./gradlew installProdDebug
```

The `app_key` for this app is `Favdish`, it can be found in `app/build.gradle`. You may have to change the `app_key` to your
account's `app_key` for the application to work.


To run this application on your device, simply run

```
./gradlew installProdDebug
```

The `app_key` for this app is `Favdish`, it can be found in `app/build.gradle`. You may have to change the `app_key` to your
account's `app_key` for the application to work.


## Screenshots

The following screenshots shows what our SDK can do using the dashboard and live device side by side

Example of PopupModal
![PopupModal](screenshots/PopupModal.png)


Example of NPS Survey
![NPSSurvey](screenshots/NPSSurvey.png)
