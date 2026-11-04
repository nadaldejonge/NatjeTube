# NatjeTube 📺

**Schijt stinkreclames altijd.** Je wil gewoon je video kijken, gap.

NatjeTube is de eigen Android TV-app van **NatjeNN**, gemaakt met Codex en gebaseerd op SmartTube stable 32.68. Eigen branding, dezelfde streamingbasis en nu een vernieuwde interface met **NatjeNightMode**. Een onafhankelijke build, geen officiële YouTube- of SmartTube-release.

![NatjeTube](images/app_banner.png)

## Downloaden en bijwerken

**[Download NatjeTube 2.0 voor Android TV](https://github.com/nadaldejonge/NatjeTube/releases/tag/v2.0)**

Pak `NatjeTube_2.0_universal.apk` bij de release. Deze ondersteunt ARM32 en ARM64. Installeer hem als update over de bestaande NatjeTube-app; verwijder de app niet en wis geen appgegevens. Package-ID en signingcertificaat zijn behouden; versionCode 2459 is hoger dan 2458.

SHA-256:

```text
c86f94f19b1df1651ec5154612b64d6e4a4c569dba01bb86ad085f1ab9d2db0c
```

## Nieuw in 2.0

- Donkere, rustigere TV-interface met consistente typografie, afgeronde videokaarten en duidelijke focus.
- Vernieuwde zijbalk, zoekveld, instellingenstijl en playercontrols.
- **NatjeNightMode:** warmte 0–100%, beeld dimmen 0–80%, tekstverzachting, overgangen, presets en eigen profielen.
- Automatische tijden, ook over middernacht; optioneel offline zonsondergang/zonsopkomst met handmatige coördinaten.
- Night-instellingen gelden apparaatbreed; bestaande accounts, menupins en volgorde blijven behouden.

**Bediening:** focus op NatjeNightMode in het linkermenu en druk OK om te schakelen. Ingedrukt OK of Menu opent de instellingen. Je vindt ze ook bij **Instellingen → NatjeNightMode**. Alleen focus of scrollen schakelt nooit. Handmatig schakelen tijdens een schema geldt tot de volgende geplande grens.

## Bestaande functies

Video’s zonder de gebruikelijke videoreclames, SponsorBlock, zoeken, accountkoppeling, abonnementen, geschiedenis, playlists, ondertitels, videokwaliteit, snelheid en de bestaande afstandsbediening blijven gebaseerd op de bestaande architectuur. 4K/HDR en frame rates zijn afhankelijk van video en apparaat. De player-engine is niet vervangen door Media3 en de UI gebruikt de bestaande Leanback-stack.

## Wat is gecontroleerd?

**34 lokale tests en 89 APK-/resourcecontroles slagen.** Eigen signing, package-ID, launcherlabels, branding en native libraries zijn gecontroleerd. Alle acht verpakte native libraries zijn byte-identiek aan de eigen 32.68-release.

Er was geen TV-stick aangesloten voor deze release. Fysieke installatie, echte remote-longpress, live account-/playbackregressies, HDR-/tunnelingcompositie, PiP en prestaties op de Philips-TV zijn nog niet gevalideerd. Gesimuleerde 1080p-/4K-layouttests bewijzen geen native 4K-interface op een echt apparaat.

Warmte is een lichte tintoverlay, geen gekalibreerde Kelvin- of selectieve videowitpuntcorrectie. De filterfunctie regelt geen TV-backlight of Ambilight. HDR, tunneling en hardwaredecoding worden niet stil uitgeschakeld. Bij pauzeren/PiP is het filter uitgeschakeld; de bestaande PiP-route blijft behouden.

Zie [build- en testrapport](docs/BUILD_REPORT_2.0.md), [privacy](PRIVACY.md) en [zelf bouwen](docs/BUILDING.md).

## Bron en licenties

NatjeTube is gebaseerd op [SmartTube van yuliskov](https://github.com/yuliskov/SmartTube). Oorspronkelijke copyrightvermeldingen en [MIT-licentie](LICENSE) zijn behouden, evenals de afzonderlijke dependencylicenties. De offline zonberekening gebruikt AOSP/AndroidX-formules onder Apache 2.0; de attributie en tekst zijn [meegeleverd](smarttubetv/src/main/assets/licenses/NatjeNightMode-Apache-2.0.txt). Oorspronkelijke projectdocumentatie staat [apart](docs/SMARTTUBE_UPSTREAM_README.md).

Private signingkeys, wachtwoorden, accounttokens, SDK’s en lokale backups horen niet in deze repository. De downloadbare APK is met de bestaande eigen key ondertekend; die private key wordt niet gedeeld.
