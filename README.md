# Kindred — getting an APK onto your phone

This is a complete Android project. The app itself lives in `app/src/main/assets/` and runs inside a WebView, so the whole thing is self-contained — once it's built, it needs no server and no hosting.

Pick whichever route suits you. The first needs nothing installed on your computer.

---

## Route 1 — build it in the cloud (free, ~5 minutes)

GitHub will compile the APK for you. You never install Android Studio.

1. Make a free account at **github.com** if you don't have one
2. Click **New repository**, name it `kindred`, keep it **Private**, create it
3. On the empty repo page click **uploading an existing file**
4. Drag in *everything* from this folder — keep the folder structure, so `app/`, `.github/`, `build.gradle`, `settings.gradle`, `gradle.properties` all land at the top level
5. Click **Commit changes**
6. Go to the **Actions** tab. A run called *Build APK* starts on its own — give it 3–5 minutes for the green tick
7. Click the finished run, scroll to **Artifacts**, download **kindred-apk**
8. Unzip it, move `kindred.apk` to your phone, tap it

Android will ask permission to install from your file manager or browser — allow it, then install. The moon icon appears in your app drawer.

If the run fails, open it and read the red step; the error is usually a file that didn't upload. The `.github/workflows/build-apk.yml` path in particular has to be exact.

## Route 2 — Android Studio on your computer

1. Install **Android Studio** (free, developer.android.com/studio)
2. **Open** → pick this folder → let it sync (first sync downloads dependencies, a few minutes)
3. **Build → Build Bundle(s) / APK(s) → Build APK(s)**
4. Click **locate** in the notification — the file is `app/build/outputs/apk/debug/app-debug.apk`
5. Plug your phone in with USB debugging on and hit ▶ to install directly, or copy the APK across

## Route 3 — no build at all

PWABuilder turns a hosted web app into a signed APK without any of this. Host the PWA folder (Netlify Drop is the quickest), paste the URL into **pwabuilder.com**, then Package for stores → Android. Good if the routes above give you trouble.

---

## Installing an APK on Android

Sideloaded apps need one permission the first time:

- Open the APK from Files or Chrome downloads
- Android says the app can't be installed from this source → **Settings**
- Turn on **Allow from this source** → back → **Install**

You may see "unsafe app blocked" from Play Protect. That warning appears for anything not distributed through the Play Store; **More details → Install anyway**. It's your own build.

## Making the replies work

The app talks to Claude straight from your phone, so it needs your own key:

1. Get one at **console.anthropic.com** → API keys
2. In Kindred: **You** tab → **API key** → paste → Save

It's stored on the device and only ever sent to Anthropic. Costs run around a tenth of a cent per message; switch the model field to `claude-haiku-4-5-20251001` for something cheaper and faster.

---

## What's in here

| Path | What it is |
|---|---|
| `app/src/main/assets/` | The app — HTML, CSS, JS, characters. Edit here to change anything. |
| `app/src/main/java/app/kindred/MainActivity.java` | The WebView shell. Serves assets over a real https origin so the API accepts requests, and wires the hardware back button to close chats. |
| `app/src/main/res/mipmap-*` | Launcher icons, including adaptive foreground/background for Android 8+ |
| `app/src/main/res/values/themes.xml` | Status and navigation bar colours |
| `.github/workflows/build-apk.yml` | The cloud build |
| `app/build.gradle` | App id, version, SDK levels |

## Changing things

- **App name on the home screen:** `app/src/main/res/values/strings.xml`
- **Characters:** the `SEED` array near the top of `app/src/main/assets/index.html`
- **Colours:** the `:root` block in that same file, and `res/values/colors.xml` for the system bars
- **Version:** `versionCode` and `versionName` in `app/build.gradle` — bump `versionCode` for each new build or Android refuses to install over the old one

Rebuild after any change: push to GitHub again, or Build → Build APK.

## If you want it on the Play Store

The debug APK is signed with a throwaway key, which is fine for your own phone but not for publishing. You'd need to generate a release keystore, sign an `assembleRelease` build, and pay Google's one-time $25 developer fee. Worth noting that stores review AI chat apps closely — you'd need a privacy policy and an accurate content rating.
