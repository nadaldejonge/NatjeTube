# NatjeTube 📺

**Schijt stinkreclames altijd.** Je wil gewoon je video kijken, gap.

NatjeTube is de eigen Android TV-app van **NatjeNN**, gemaakt met Codex en gebaseerd op SmartTube stable 32.68. Eigen branding, dezelfde streamingbasis en nu een vernieuwde interface met **NatjeNightMode**. Een onafhankelijke build, geen officiële YouTube- of SmartTube-release.

![NatjeTube](images/app_banner.png)

## Downloaden en bijwerken

**[Download NatjeTube 2.0.1 voor Android TV](https://github.com/nadaldejonge/NatjeTube/releases/tag/v2.0.1)**

Pak `NatjeTube_2.0.1_universal.apk` bij de release, gap. Deze ondersteunt ARM32 en ARM64. **Verwijder eerst de oude NatjeTube-app en installeer daarna 2.0.1.** Deze thuisbuild gebruikt op verzoek een nieuwe signingkey. Bij verwijderen verdwijnen je oude instellingen en accountkoppelingen; die stel je na installatie opnieuw in. Volgende updates op deze versie gebruiken dezelfde nieuwe key. Package-ID: `nl.natje.natjetube`; versionCode: 2460.

SHA-256:

```text
0cf1e3a5df75bbe89e9e475089a7300e1c1a4136e54e576264b6fd6bc35a8aa7
```

## Nieuw in 2.0.1

- Night Mode in het menu selecteren sluit de app niet meer af.
- Veel meer warmte: de maximale ambermenging gaat van 6% naar 60%. Zelfde schuifregelaar, veel groter bereik, gap.
- De dimregeling blijft zoals in 2.0.

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

**37 automatische tests slagen.** Handtekening, zipalign, package-ID, versie en TV-launcher zijn gecontroleerd. Alle acht native libraries en alle 688 verpakte PNG-afbeeldingen zijn byte-identiek aan de werkende 2.0-release.

Versie 2.0 is door NatjeNN op de TV getest. De gemelde menucrash en beperkte warmte zijn aangepakt; de nieuwe 2.0.1-APK moet nog op die TV worden getest. De automatische tests vervangen die controle niet.

Warmte is een tintoverlay met een groter bereik, geen gekalibreerde Kelvin- of selectieve videowitpuntcorrectie. De filterfunctie regelt geen TV-backlight of Ambilight. HDR, tunneling en hardwaredecoding worden niet stil uitgeschakeld. Bij pauzeren/PiP is het filter uitgeschakeld; de bestaande PiP-route blijft behouden.

Zie [build- en testrapport](docs/BUILD_REPORT_2.0.1.md), [privacy](PRIVACY.md) en [zelf bouwen](docs/BUILDING.md).

## Bron en licenties

NatjeTube is gebaseerd op [SmartTube van yuliskov](https://github.com/yuliskov/SmartTube). Oorspronkelijke copyrightvermeldingen en [MIT-licentie](LICENSE) zijn behouden, evenals de afzonderlijke dependencylicenties. De offline zonberekening gebruikt AOSP/AndroidX-formules onder Apache 2.0; de attributie en tekst zijn [meegeleverd](smarttubetv/src/main/assets/licenses/NatjeNightMode-Apache-2.0.txt). Oorspronkelijke projectdocumentatie staat [apart](docs/SMARTTUBE_UPSTREAM_README.md).

Private signingkeys, wachtwoorden, accounttokens, SDK’s en lokale backups horen niet in deze repository. De 2.0.1-APK gebruikt de nieuwe eigen thuisbuild-key; die private key wordt niet gedeeld.
