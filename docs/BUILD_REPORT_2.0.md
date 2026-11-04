# NatjeTube 2.0 — buildrapport

Release van 4 november 2026, gebaseerd op SmartTube 32.68. De bestaande eigen release is lokaal onaangetast gebleven.

| Eigenschap | Waarde |
|---|---|
| APK | `NatjeTube_2.0_universal.apk` |
| Versie / versionCode | 2.0 / 2459 |
| Package-ID | `nl.natje.natjetube` |
| ABI’s | arm64-v8a, armeabi-v7a |
| min/target/compile SDK | 17 / 34 / 34 |
| Grootte | 34.971.073 bytes |
| SHA-256 | `c86f94f19b1df1651ec5154612b64d6e4a4c569dba01bb86ad085f1ab9d2db0c` |
| Certificaat SHA-256 | `0a13b21f612f0bf2d21d4f8db64afd9e463b7260e4001a9a7a141d75d565e0cf` |
| Signing | Bestaande eigen NatjeTube-key; v1 en v2 geverifieerd |

Definitieve `ststableRelease`: **BUILD SUCCESSFUL in 3m 40s**, 786 taken waarvan 17 uitgevoerd en 769 up-to-date. Release-lint inbegrepen. JDK17, Gradle7.5, AGP7.4.2, Kotlin1.8.10, SDK34, NDK21.0.6113669. Geen brede dependency-upgrade, Compose- of Media3-migratie. De bestaande strip-fallback voor libglide-webp.so behield de library.

## Tests

| Suite | Tests | Fouten |
|---|---:|---:|
| NatjeNightScheduleTest | 7 | 0 |
| NatjeNightModeTest | 7 | 0 |
| NatjeSidebarMigrationTest | 2 | 0 |
| NatjeTvUiTest | 7 | 0 |
| ScreensaverManagerTest, bestaand | 10 | 0 |
| SuggestionsControllerTest, bestaand | 1 | 0 |

34 tests: middernacht, lokale DST/tijdzones, solar/poolcondities, locatieconfiguratie, opslag/reset/presets/override, menu-migratie, dim-compositie, observer-lifecycle, D-pad/switchinstellingen, heropenen en gesimuleerde 1080p-/4K-settingsvensters. Robolectric4.6.1 gebruikt een afzonderlijke Java11-test-JVM; release/Javac bleven op Java17.

89 onafhankelijke APK-/resourcecontroles: package/versie/labels, ARM-ABI/ELF, Leanback/touchscreen, launcher/export, eigen cert/v1/v2, zipalign, providers, oude activiteiten, geen nieuwe permissies/secrets/Firebase-config, Night-activity/DEX-klassen, branding en licenties. Alle 33 goedgekeurde brandingoutputs ongewijzigd, negen actieve stable-graphics pixelmatig vergeleken. Beta/fdroid-bronassets gecontroleerd; geen beta/fdroid-APK als getest opgeleverd.

Streaming-/accountmodules, beide SharedModules-bronkopieën en ExoPlayer-broncode zijn inhoudelijk behouden. Voor publicatie worden dezelfde updaterbeveiligingen via de reeds bestaande eigen submodulecommits gebruikt. Alle acht verpakte native libraries zijn byte-identiek aan de werkende eigen 32.68-release. Updates naar de oorspronkelijke upstream-app blijven uitgeschakeld.

## Night Mode

Warmte0–100%, dim0–80%, afzonderlijke lichte tekstverzachting, directe/5/15/30/60s-overgang, handmatige of automatische planning, offline solar met handmatige locatie, standaardpresets en maximaal20 eigen profielen. Versie1-opslag in een eigen apparaatbreed preferencebestand; bestaande account-/playerprefs blijven behouden. Override tot volgende schemagrens; geen permanente service, root- of systeemoverlaypermissie.

De decorlaag houdt videodecoding en renderkeuze intact en blijft onafhankelijk van verborgen controls/nieuwe video’s. Bestaande zwarte dimming en Night-dimming worden tot het maximum gecombineerd; screen-off100% blijft zwart. Warmte is maximaal6% ambermenging, geen gekalibreerde pixeltransformatie. Geen duplicerende videowitpuntslider of onbewezen TextureView-/shaderroute toegevoegd.

## Grenzen

Geen fysieke TV aangesloten. Installatie/update, account/voice/live playback, fysieke remote, HDR/tunneling/secure surface, AFR, ondertitels/SponsorBlock in echte streams, native UI-bufferresolutie en geheugen/energie/fps op de stick zijn niet uitgevoerd. Codebehoud en buildresultaat zijn geen vervanging voor die proeven. Het filter is bij pause/PiP uitgeschakeld; echte TV-helderheid/Ambilight worden niet bestuurd. Zwart kan door tintmenging beperkt worden opgetild. Nieuwe Night-labels zijn Nederlands/Engels.

## Update installeren

Download de APK uit [releasev2.0](https://github.com/nadaldejonge/NatjeTube/releases/tag/v2.0). Package-ID en certificaat komen overeen met de eigen 32.68-release; versionCode is hoger. Normaal bijwerken via Android of `adb install -r NatjeTube_2.0_universal.apk`. **Niet uninstalleren of appdata wissen.** De daadwerkelijke update op een fysiek apparaat is nog niet uitgevoerd.
