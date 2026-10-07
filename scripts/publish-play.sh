#!/usr/bin/env bash
#
# Copied from gym-watch-tracker/scripts/publish-play.sh (package and paths changed); the dated
# incidents quoted in the comments below happened on that app, and apply here the same way.
#
# Publish the signed release AAB to Google Play — no console clicking.
#
# Usage:
#   scripts/publish-play.sh --track internal                    # Testing > Internal testing
#   scripts/publish-play.sh --track beta                        # Testing > Open testing
#   scripts/publish-play.sh --track production                  # Production at 100%
#   scripts/publish-play.sh --track production --rollout 0.2    # staged rollout, 20%
#   scripts/publish-play.sh --track production --defer-review   # upload, but don't auto-submit
#   scripts/publish-play.sh --yes [AAB_PATH]                    # skip confirm / explicit bundle
#   scripts/publish-play.sh --status                            # what each track serves; non-zero if desynced
#
# Every release lands on internal testing AND production. Internal has no review, so it is live
# in minutes and is how you install what you just shipped; production sits in review for days.
# --status enforces that: it exits non-zero whenever internal is behind production.
#
#   scripts/publish-play.sh --track internal --promote-from production
#                                                               # repair: put the shipped build on internal
#   scripts/publish-play.sh --track beta --promote-from production
#                                                               # copy the live build onto Open testing
#   scripts/publish-play.sh --track production --promote-from beta --rollout 0.2
#                                                               # graduate a tested build, 20% rollout
#
# Auth: the developer account's shared Play publisher service account — the same key every
# app on this account releases with (Transit Now V3, Cycle Now, ...). It lives base64-encoded
# in the macOS Keychain under the service name "google-play-apk-release" (stored/rotated by
# the Transit Now release setup; see transit-now-v3-c/fastlane/Fastfile). Nothing to grant
# per-app: the account-level grant already covers this listing.
#
# --defer-review maps to the API's changesNotSentForReview: the upload lands on the track but
# submission stays manual (Play Console shows "changes ready to send for review"). Play REQUIRES
# this while another submission is already in review; it's also the right mode when console-side
# work (Data safety, listing) must land in the same submission. Without the flag, a blocked
# auto-submit fails loudly rather than silently leaving the release unsubmitted.
#
# Release notes: put them in play-assets/release-notes/en-US.txt (plain text, <=500 chars).
# When the file exists it is uploaded as this versionCode's changelog; absent = no notes.
#
# --promote-from moves no bytes: it copies an existing release from that track onto --track by
# version code, leaving the source track untouched (fastlane writes only the destination). Use it
# whenever the build is already on Play, because a versionCode can never be uploaded twice — a
# rebuild of the same version is rejected, so promoting is the ONLY way to add a shipped build to
# another track. --version-code picks the release when the source track holds more than one.
#
# Track names: Play namespaces form-factor tracks as "wear:<track>" — the plain "production"
# track is the PHONE track and rejects a watch-only bundle ("requires android.hardware.type.watch",
# seen live 2026-08-20). This listing is Wear-only, so every track maps to wear:<track> by
# default; PUBLISH_TRACK_NAME overrides the full name if Play's naming ever shifts.

set -euo pipefail

REPO_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
PACKAGE_NAME="com.jsyntax.nowtap"
KEYCHAIN_SERVICE="google-play-apk-release"
DEFAULT_AAB="$REPO_ROOT/app/build/outputs/bundle/release/app-release.aab"
NOTES_FILE="$REPO_ROOT/play-assets/release-notes/en-US.txt"

TRACK=""
ROLLOUT=""
DEFER_REVIEW=0
ASSUME_YES=0
AAB_ARG=""
PROMOTE_FROM=""
VERSION_CODE=""
STATUS=0

usage() { awk 'NR>1 && /^#/ { sub(/^# ?/, ""); print; next } NR>1 { exit }' "$0"; }

while [[ $# -gt 0 ]]; do
  case "$1" in
    -h|--help) usage; exit 0 ;;
    --track) [[ $# -ge 2 ]] || { echo "--track requires a value" >&2; exit 1; }; TRACK="$2"; shift 2 ;;
    --track=*) TRACK="${1#--track=}"; shift ;;
    --rollout) [[ $# -ge 2 ]] || { echo "--rollout requires a value" >&2; exit 1; }; ROLLOUT="$2"; shift 2 ;;
    --rollout=*) ROLLOUT="${1#--rollout=}"; shift ;;
    --promote-from) [[ $# -ge 2 ]] || { echo "--promote-from requires a value" >&2; exit 1; }; PROMOTE_FROM="$2"; shift 2 ;;
    --promote-from=*) PROMOTE_FROM="${1#--promote-from=}"; shift ;;
    --version-code) [[ $# -ge 2 ]] || { echo "--version-code requires a value" >&2; exit 1; }; VERSION_CODE="$2"; shift 2 ;;
    --version-code=*) VERSION_CODE="${1#--version-code=}"; shift ;;
    --defer-review) DEFER_REVIEW=1; shift ;;
    --status) STATUS=1; shift ;;
    -y|--yes) ASSUME_YES=1; shift ;;
    -*) echo "Unknown flag: $1" >&2; usage >&2; exit 1 ;;
    *) [[ -z "$AAB_ARG" ]] || { echo "Unexpected argument: $1" >&2; exit 1; }; AAB_ARG="$1"; shift ;;
  esac
done

# The key never touches disk: supply reads it from SUPPLY_JSON_KEY_DATA.
load_play_key() {
  local b64
  b64="$(security find-generic-password -w -s "$KEYCHAIN_SERVICE" -a "$USER" 2>/dev/null || true)"
  if [[ -z "$b64" ]]; then
    echo "Play key not found in the macOS Keychain (service '$KEYCHAIN_SERVICE')." >&2
    echo "Store it with: security add-generic-password -a \"\$USER\" -s $KEYCHAIN_SERVICE -U -w \"\$(base64 -i key.json)\"" >&2
    exit 1
  fi
  SUPPLY_JSON_KEY_DATA="$(printf '%s' "$b64" | base64 -d)"
  export SUPPLY_JSON_KEY_DATA
}

# --status: read-only, and the gate on the release invariant --
#
#   EVERY BUILD ON PRODUCTION IS ALSO INSTALLABLE FROM INTERNAL TESTING.
#
# Internal testing has no review: it is live minutes after upload, while a production (and
# especially a Wear OS production) submission sits in review for days. So internal is not a
# staging tier for this app, it is how the developer installs the build they just shipped. A
# release that reached only production means waiting on Google to see your own work.
#
# Seen live 2026-08-20: production and wear:beta both served build 8 (0.2.0) while wear:internal
# was still on build 7 (0.1.3) -- a whole version behind. 0.2.0 had been uploaded straight to
# production and then copied onward to beta, so internal was never in the chain. Nothing errored.
#
# Exits non-zero while internal is behind, so it can gate a script and not just inform a human.
if [[ "$STATUS" -eq 1 ]]; then
  command -v fastlane >/dev/null 2>&1 || { echo "fastlane not installed." >&2; exit 1; }
  load_play_key
  echo "$PACKAGE_NAME — local: versionCode $(sed -n 's/^ *versionCode *\([0-9]*\).*/\1/p' "$REPO_ROOT/app/build.gradle")"
  internal_max=""
  production_max=""
  for t in internal alpha beta production; do
    # `|| true`: a track the API refuses to describe (wear:alpha answered "HALTED release must
    # have fraction" on 2026-09-02) must not abort the status of the tracks that matter.
    codes="$( { fastlane run google_play_track_version_codes package_name:"$PACKAGE_NAME" track:"wear:$t" 2>/dev/null || true; } \
      | sed -n 's/.*Result: \[\([0-9, ]*\)\].*/\1/p' | head -1)"
    printf '  %-16s %s\n' "wear:$t" "${codes:-(empty)}"
    # A track can hold more than one release; what it serves is the highest code.
    max="$(printf '%s' "$codes" | tr ', ' '\n\n' | grep -E '^[0-9]+$' | sort -n | tail -1 || true)"
    case "$t" in
      internal)   internal_max="$max" ;;
      production) production_max="$max" ;;
    esac
  done

  echo
  # Deliberately "internal BEHIND production", not "production's code is missing from internal".
  # Internal ahead of production is the normal mid-release state -- uploaded to internal, not yet
  # promoted -- and is exactly what this invariant wants. Only internal lagging is the disease.
  if [[ -n "$production_max" ]] && { [[ -z "$internal_max" ]] || [[ "$internal_max" -lt "$production_max" ]]; }; then
    echo "DESYNC: production serves $production_max, internal testing only ${internal_max:-nothing}." >&2
    echo "        The shipped build is not installable without waiting on review. Repair with:" >&2
    echo "          scripts/publish-play.sh --track internal --promote-from production --yes" >&2
    exit 1
  fi
  echo "  in sync: internal testing serves ${internal_max:-nothing}, production ${production_max:-nothing}"
  exit 0
fi

case "$TRACK" in
  internal|alpha|beta|production) ;;
  "") echo "Missing --track (internal|alpha|beta|production)." >&2; exit 1 ;;
  *) echo "Unknown track: $TRACK (expected internal, alpha, beta, or production)" >&2; exit 1 ;;
esac
TRACK_NAME="${PUBLISH_TRACK_NAME:-wear:$TRACK}"

if [[ -n "$ROLLOUT" && "$TRACK" != "production" ]]; then
  echo "--rollout only applies to --track production." >&2
  exit 1
fi

FROM_TRACK_NAME=""
if [[ -n "$PROMOTE_FROM" ]]; then
  case "$PROMOTE_FROM" in
    internal|alpha|beta|production) ;;
    *) echo "Unknown --promote-from track: $PROMOTE_FROM (expected internal, alpha, beta, or production)" >&2; exit 1 ;;
  esac
  [[ "$PROMOTE_FROM" != "$TRACK" ]] || { echo "--promote-from and --track are both '$TRACK'; nothing to promote." >&2; exit 1; }
  [[ -z "$AAB_ARG" ]] || { echo "--promote-from copies a release already on Play; drop the AAB argument." >&2; exit 1; }
  FROM_TRACK_NAME="${PUBLISH_FROM_TRACK_NAME:-wear:$PROMOTE_FROM}"
elif [[ -n "$VERSION_CODE" ]]; then
  echo "--version-code only applies with --promote-from (an upload's version code comes from the AAB)." >&2
  exit 1
fi

RELEASE_STATUS="completed"
if [[ "$TRACK" == "production" ]]; then
  ROLLOUT="${ROLLOUT%\%}"
  ROLLOUT="${ROLLOUT:-1}"
  awk -v v="$ROLLOUT" 'BEGIN { exit !(v > 0 && v <= 1) }' || {
    echo "Invalid --rollout '$ROLLOUT'. Use a fraction in (0, 1], e.g. 0.2 for 20%." >&2
    exit 1
  }
  awk -v v="$ROLLOUT" 'BEGIN { exit !(v < 1) }' && RELEASE_STATUS="inProgress" || true
fi

command -v fastlane >/dev/null 2>&1 || { echo "fastlane not installed." >&2; exit 1; }

AAB_PATH=""
AAB_PACKAGE=""
AAB_VERSION_CODE=""
if [[ -z "$PROMOTE_FROM" ]]; then
  AAB_PATH="${AAB_ARG:-$DEFAULT_AAB}"
  [[ "$AAB_PATH" == /* ]] || AAB_PATH="$PWD/$AAB_PATH"
  [[ -f "$AAB_PATH" ]] || { echo "AAB not found: $AAB_PATH (bump versionCode in app/build.gradle, then ./gradlew :app:bundleRelease)" >&2; exit 1; }

  # Identity guard: never upload someone else's bundle to this listing. bundletool is the
  # authority; when it's missing we refuse rather than guess.
  command -v bundletool >/dev/null 2>&1 || { echo "bundletool is required to verify the AAB (brew install bundletool)." >&2; exit 1; }
  AAB_PACKAGE="$(bundletool dump manifest --bundle "$AAB_PATH" --xpath "/manifest/@package" 2>/dev/null | tr -d '[:space:]')"
  AAB_VERSION_CODE="$(bundletool dump manifest --bundle "$AAB_PATH" --xpath "/manifest/@android:versionCode" 2>/dev/null | tr -d '[:space:]')"
  if [[ "$AAB_PACKAGE" != "$PACKAGE_NAME" ]]; then
    echo "MISMATCH: this script publishes $PACKAGE_NAME but the AAB is built as '$AAB_PACKAGE'. Refusing." >&2
    exit 1
  fi
fi

load_play_key

# supply wants changelogs as <metadata>/<locale>/changelogs/<versionCode>.txt; build that
# layout in a temp dir from the flat notes file so the repo keeps a single editable file.
METADATA_ARGS=(skip_upload_changelogs:"true")
TMP_META=""
if [[ -f "$NOTES_FILE" && -n "$AAB_VERSION_CODE" ]]; then
  TMP_META="$(mktemp -d)"
  trap 'rm -rf "$TMP_META"' EXIT
  mkdir -p "$TMP_META/en-US/changelogs"
  cp "$NOTES_FILE" "$TMP_META/en-US/changelogs/$AAB_VERSION_CODE.txt"
  METADATA_ARGS=(metadata_path:"$TMP_META")
fi

echo "Publishing to Google Play:"
if [[ -n "$PROMOTE_FROM" ]]; then
  echo "  Mode:         promote (no upload; the bundle is already on Play)"
  echo "  From track:   $FROM_TRACK_NAME (left as-is)"
  echo "  versionCode:  ${VERSION_CODE:-whichever release the source track holds}"
else
  echo "  AAB:          $AAB_PATH"
  echo "  applicationId: $AAB_PACKAGE (verified)"
  echo "  versionCode:  ${AAB_VERSION_CODE:-unknown}"
fi
echo "  Track:        $TRACK_NAME"
[[ "$TRACK" == "production" ]] && echo "  Rollout:      $(awk -v v="$ROLLOUT" 'BEGIN { printf "%g%%", v * 100 }') ($RELEASE_STATUS)"
echo "  Auto-submit:  $([[ "$DEFER_REVIEW" -eq 1 ]] && echo 'no (--defer-review)' || echo 'yes')"
echo "  Release notes: $([[ -n "$TMP_META" ]] && echo "$NOTES_FILE" || echo none)"

if [[ "$ASSUME_YES" -ne 1 ]]; then
  read -r -p "Upload this build? [y/N] " CONFIRM
  case "$CONFIRM" in y|Y|yes|YES) ;; *) echo "Aborted."; exit 0 ;; esac
fi

if [[ -n "$PROMOTE_FROM" ]]; then
  # Promote copies the source release onto the destination track by version code. supply writes
  # only the destination, so the source track keeps the release; the copied release object carries
  # its own notes, hence no changelog upload here.
  #
  # supply's run summary prints "deactivate_on_promote | true", which reads like it will strip the
  # release off the SOURCE track -- alarming when promoting production -> internal to repair a
  # desync, because the source is production. It does not: the option is vestigial for the releases
  # API, and supply's promote_track only ever calls update_track on the destination. Verified live
  # 2026-08-20 -- promoted wear:production -> wear:internal and production still served build 8
  # afterwards. Do not let the summary line talk you out of the repair.
  CMD=(fastlane run upload_to_play_store
    package_name:"$PACKAGE_NAME"
    track:"$FROM_TRACK_NAME"
    track_promote_to:"$TRACK_NAME"
    skip_upload_apk:"true"
    skip_upload_aab:"true"
    skip_upload_metadata:"true"
    skip_upload_images:"true"
    skip_upload_screenshots:"true"
    skip_upload_changelogs:"true")
  [[ -n "$VERSION_CODE" ]] && CMD+=(version_code:"$VERSION_CODE")
else
  CMD=(fastlane run upload_to_play_store
    aab:"$AAB_PATH"
    package_name:"$PACKAGE_NAME"
    track:"$TRACK_NAME"
    release_status:"$RELEASE_STATUS"
    skip_upload_metadata:"true"
    skip_upload_images:"true"
    skip_upload_screenshots:"true"
    "${METADATA_ARGS[@]}")
fi
[[ "$TRACK" == "production" ]] && CMD+=(rollout:"$ROLLOUT")
[[ "$DEFER_REVIEW" -eq 1 ]] && CMD+=(changes_not_sent_for_review:"true")

exec "${CMD[@]}"
