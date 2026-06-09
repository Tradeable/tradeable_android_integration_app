#!/bin/bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
WRAPPER_DIR="${TRADEABLE_WRAPPER_DIR:-$SCRIPT_DIR/../tradeable_android_wrapper}"
WRAPPER_GIT_URL="${TRADEABLE_WRAPPER_GIT_URL:-https://github.com/deepakgrandhi/tradeable_android_wrapper.git}"

echo "Using wrapper repo: $WRAPPER_GIT_URL"
echo "Using wrapper dir: $WRAPPER_DIR"

if [ ! -d "$WRAPPER_DIR/.git" ]; then
  echo "Cloning wrapper..."
  git clone "$WRAPPER_GIT_URL" "$WRAPPER_DIR"
else
  echo "Wrapper already exists. Pulling latest..."
  git -C "$WRAPPER_DIR" pull --ff-only
fi

echo "Building wrapper AAR and Flutter artifacts..."
(
  cd "$WRAPPER_DIR"
  ./build.sh
)

echo "Copying wrapper AAR to integration app..."
mkdir -p "$SCRIPT_DIR/app/libs"
cp "$WRAPPER_DIR/output/tradeable-android-wrapper.aar" "$SCRIPT_DIR/app/libs/tradeable-android-wrapper.aar"

echo "Done. You can now run: ./gradlew :app:assembleDebug"
