# NatjeTube 📺

**Schijt stinkreclames altijd.** Je wil gewoon je video kijken, gap.

NatjeTube is de eigen Android TV-app van **NatjeNN**, gemaakt met Codex en gebaseerd op SmartTube stable 32.68. Eigen branding, dezelfde streamingbasis en nu een vernieuwde interface met **NatjeNightMode**. Een onafhankelijke build, geen officiële YouTube- of SmartTube-release.

![NatjeTube](images/app_banner.png)

## Downloaden en bijwerken

**[Download NatjeTube 2.1.2 voor Android TV](https://github.com/nadaldejonge/NatjeTube/releases/tag/v2.1.2)**

Pak `NatjeTube_2.1.2_universal.apk`, gap. Installeer als gewone update over 2.0.1, 2.1.0 of 2.1.1; dezelfde signingkey, dus je hoeft niets te verwijderen of appgegevens te wissen. Alleen bij de oorspronkelijke 2.0-release met de oudere key moet je eerst opnieuw installeren. Package-ID: `nl.natje.natjetube`; versionCode: 2463.

SHA-256:

```text
2335356eb2b08ea7c90f756da27077b49ac4826d1032931865d2001e49cdf30e
```

## Nieuw in 2.1.2

- Eén aansluitende zijbalk: zoeken, Home, Subscriptions, Library, Explore, Night Mode en Settings, met gelijke rijen en eigen iconen.
- Compactere menubreedte; de hoofdsecties blijven op hun plek bij het selecteren.
- Geen zwevende zoek-/accountknoppen, klok, datum of logo meer over de browsefilters. Accountbeheer staat onder Settings.
- Filters krijgen een eigen horizontale rij met zichtbare selectie en focus.
- Directe pijltjesroutes tussen menu, filters en video's; Night Mode en Settings zijn in beide richtingen bereikbaar.
- Bestaande kleuren, lettertypes en accounts blijven behouden.

## Indeling van 2.1.0

- Compact menu met Zoeken, Home, Abonnementen, Bibliotheek en Ontdekken; Night Mode en Instellingen vast onderaan.
- Home-raster met de aangeleverde YouTube-aanbevelingsvolgorde en onderwerpfilters. De persoonlijke web-feed heeft voorkeur, met de bestaande tv-feed als terugval.
- Aanbevelingen hangen af van je ingelogde account en YouTube; een identieke browservolgorde of vaste eerste plek voor een kanaal is niet gegarandeerd.
- Abonnementen bundelt nieuwe video-inhoud en kanalen; Bibliotheek bundelt persoonlijke onderdelen en Ontdekken de themafeeds.
- Kleuren, lettertypes en opgeslagen kanaal-/afspeellijstpins zijn behouden.

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

**52 automatische tests slagen.** De zijbalktest gebruikt de productie-icoonpresenter en echte schermcontainer op 1080p en 4K, inclusief de uitlijning die Browse bij starten toepast, vaste rijposities en de verbindingen naar de actieknoppen. De APK is daarnaast gestart en met D-pad bediend in een Android TV-emulator. APK-handtekening, zipalign, package-ID en versie zijn gecontroleerd.

De persoonlijke Home-feed is niet in de emulator beoordeeld: die is niet ingelogd. Deze versie is nog niet op de fysieke tv geïnstalleerd.

Warmte is een tintoverlay met een groter bereik, geen gekalibreerde Kelvin- of selectieve videowitpuntcorrectie. De filterfunctie regelt geen TV-backlight of Ambilight. HDR, tunneling en hardwaredecoding worden niet stil uitgeschakeld. Bij pauzeren/PiP is het filter uitgeschakeld; de bestaande PiP-route blijft behouden.

Zie [build- en testrapport](docs/BUILD_REPORT_2.1.2.md), [privacy](PRIVACY.md) en [zelf bouwen](docs/BUILDING.md).

## Bron en licenties

NatjeTube is gebaseerd op [SmartTube van yuliskov](https://github.com/yuliskov/SmartTube). Oorspronkelijke copyrightvermeldingen en [MIT-licentie](LICENSE) zijn behouden, evenals de afzonderlijke dependencylicenties. De offline zonberekening gebruikt AOSP/AndroidX-formules onder Apache 2.0; de attributie en tekst zijn [meegeleverd](smarttubetv/src/main/assets/licenses/NatjeNightMode-Apache-2.0.txt). Oorspronkelijke projectdocumentatie staat [apart](docs/SMARTTUBE_UPSTREAM_README.md).

Private signingkeys, wachtwoorden, accounttokens, SDK’s en lokale backups horen niet in deze repository. De 2.0.1-, 2.1.0- en 2.1.2-APK gebruiken dezelfde eigen thuisbuild-key; die private key wordt niet gedeeld.
