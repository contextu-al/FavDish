#!/bin/sh -x

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

GIT_VERSION=$(git log -1 --format="%h")
BUILD_TIME=$(date)

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
  ./gradlew assembleProdDebug
  APK_LOCATION=app/build/outputs/apk/prod/debug/app-prod-debug.apk
fi

# We use lowercase variables as part of the Artifactory BDD path below
LOWERCASE_APP_ENV=$( tr '[A-Z]' '[a-z]' <<< $APP_ENV)
LOWERCASE_SDK_ENV=$( tr '[A-Z]' '[a-z]' <<< $SDK_ENV)

echo "===== Build FavDish .apk for AppCenter ====="
./gradlew build --refresh-dependencies
./gradlew app:dependencies
./gradlew assembleDebug

echo "===== Uploading .apk to AppCenter ====="
appcenter distribute release --app Contextual/FavDish-"$SDK_ENV"SDK-"$APP_ENV"-"$APP_KEY" --file "$APP_KEY" --group "Collaborators"