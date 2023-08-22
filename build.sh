#!/bin/sh -x

# If user has set CONTEXTUAL_SDK_VERSION in environment, it will be used.
if [ "$CONTEXTUAL_SDK_VERSION" ]; then
    echo "VERSION_NAME=${CONTEXTUAL_SDK_VERSION}" >> local.properties
    echo "Building ${CONTEXTUAL_SDK_VERSION} of SDK"
fi


# Invoked from upstream SDK pipeline.
if [ "$UPSTREAM_VERSION_NAME" ]; then
   git clone https://gitlab.com/contextual/sdks/android/contextual-sdk-android
   cd contextual-sdk-android
   git checkout $UPSTREAM_VERSION_NAME
   CONTEXTUAL_SDK_TAG=$(git describe --tags --abbrev=0)
   UPSTREAM_VERSION_GIT_HASH=-${UPSTREAM_VERSION_NAME}
   UPSTREAM_VERSION=${CONTEXTUAL_SDK_TAG}${UPSTREAM_VERSION_GIT_HASH}
   cd ..
   echo "VERSION_NAME=${UPSTREAM_VERSION}" >> local.properties
   echo "Building ${UPSTREAM_VERSION} of SDK"
fi

echo "===== Ensuring correct language encoding and paths on build machine ====="
source ~/.profile

echo "===== Cleanup before fresh build ====="
rm -rf .app/build/outputs

echo "===== Determine git branch name ====="
if [ -z "$CI_COMMIT_REF_NAME" ]; then
    GIT_BRANCH=$(git symbolic-ref --short -q HEAD)
else
    GIT_BRANCH=$CI_COMMIT_REF_NAME
fi

echo "===== Setting Default Environment Variables ======"
APP_ENV="Prod"
APP_KEY="FavDish"
SDK_ENV="Dev"
APK_LOCATION=""

if [ "$should_setup_emulator" == "true" ]; then
  # hard coding path is bad
  export JAVA_OPTS='-XX:+IgnoreUnrecognizedVMOptions --add-modules java.xml.bind'
  /Users/buildcontextual/Library/Android/sdk/tools/bin/sdkmanager --install 'system-images;android-TiramisuPrivacySandbox;google_apis_playstore;x86_64'
  /Users/buildcontextual/Library/Android/sdk/tools/bin/sdkmanager --install emulator
  echo "no" | /Users/buildcontextual/Library/Android/sdk/tools/bin/avdmanager --verbose create avd --force --name "contextual_sdk_emulator" --package "system-images;android-TiramisuPrivacySandbox;google_apis_playstore;x86_64"
  /Users/buildcontextual/Library/Android/sdk/tools/bin/emulator -avd contextual_sdk_emulator -noaudio -no-boot-anim -netdelay none -accel on -no-window -no-snapshot -gpu swiftshader_indirect
fi


GIT_VERSION=$(git log -1 --format="%h")
BUILD_TIME=$(date)
#git add app/build.gradle
#git commit -m "Bump FavDish app version"
#git push
if [ ! -f local.properties ]; then
  touch local.properties
fi

# Default is Develop using above environment variables
# Staging
if [ "$GIT_BRANCH" = "staging" ]; then 
  APP_ENV="Staging"
  APP_KEY="FavDish_staging"
  ./gradlew assembleStagingDebug
  APK_LOCATION=app/build/outputs/apk/staging/debug/app-staging-debug.apk
# Production
elif [ "$GIT_BRANCH" = "main" ]; then
  SDK_ENV='Prod'
  ./gradlew assembleProdDebug
  APK_LOCATION=app/build/outputs/apk/prod/debug/app-prod-debug.apk
elif [ "$GIT_BRANCH" = "develop" ]; then
  SDK_ENV='Dev'
  ./gradlew assembleContinuousIntegrationDebug
  APK_LOCATION=app/build/outputs/apk/continuousIntegration/debug/app-continuousIntegration-debug.apk
fi

# We use lowercase variables as part of the Artifactory BDD path below
LOWERCASE_APP_ENV=$( tr '[A-Z]' '[a-z]' <<< $APP_ENV)
LOWERCASE_SDK_ENV=$( tr '[A-Z]' '[a-z]' <<< $SDK_ENV)

echo "===== Uploading .apk to AppCenter ====="
appcenter distribute release --app Contextual/FavDish-"$SDK_ENV"SDK-"$APP_ENV"-"$APP_KEY" --file "$APK_LOCATION" --group "Collaborators"