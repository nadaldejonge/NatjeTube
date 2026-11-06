# NatjeTube 2.0.1 — build- en testrapport

7 november 2026 (Europe/Amsterdam). Correctierelease na de fysieke gebruikerstest van 2.0.

## Wijzigingen

Het Night Mode-actiefragment levert nu Leanbacks verplichte MainFragmentAdapter. Voorheen gooide BrowseSupportFragment een IllegalArgumentException zodra de menuregel werd geselecteerd. Het paneel meldt ook wanneer de inhoud gereed is. Alleen selecteren schakelt Night Mode niet in.

De maximale warme tint gaat van 6% naar 60% ambermenging. De schuifregelaar blijft 0–100. De afzonderlijke dimregeling en instellingenopslag zijn niet gewijzigd.

## Validatie

- [GitHub Actions-build](https://github.com/nadaldejonge/NatjeTube/actions/runs/42313373916) en release-lint geslaagd. Gebouwde broncommit: `1f5d19487`; latere documentatiecommits veranderen de appcode niet.
- 37 tests geslaagd, nul fouten: 27 common-tests en 10 TV-UI-tests.
- Nieuwe regressies: Leanback-selectie Night → instellingen → Night zonder crash of inschakelen; neutrale/halve/maximale warmte; afzonderlijke dimming.
- De oude Robolectric-versie rasteriseert geen bitmapkleuren. De kleurtests controleren daarom de echte onDraw-kleurcommando's met standaard alpha-compositie op wit.
- Alle 33 brandingbronnen ongewijzigd. Alle 688 verpakte PNG-afbeeldingen en alle acht native libraries zijn byte-identiek aan de gepubliceerde 2.0-APK.
- Package, versie, SDK's, ARM32/ARM64, TV-launcher en appnaam gecontroleerd. Geen private signingbestanden of google-services.json in de APK.
- Eigen APK-handtekening v1/v2 en zipalign geverifieerd.

## Bestand

| Eigenschap | Waarde |
|---|---|
| APK | NatjeTube_2.0.1_universal.apk |
| Versie / versionCode | 2.0.1 / 2460 |
| Package | nl.natje.natjetube |
| minSdk / targetSdk | 17 / 34 |
| Grootte | 34.964.348 bytes |
| SHA-256 | 0cf1e3a5df75bbe89e9e475089a7300e1c1a4136e54e576264b6fd6bc35a8aa7 |
| Certificaat SHA-256 | f590ea01b366e4c2a4560514bba579bcc4cb1efd69077e43f663bbc9ba41834d |

## Schone installatie

Op expliciet verzoek is een nieuwe eigen RSA-4096-key voor deze thuisbuild aangemaakt. De oorspronkelijke key is behouden. Private keys en versleutelde wachtwoorden staan alleen lokaal, buiten Git.

**Verwijder eerst NatjeTube 2.0 en installeer daarna 2.0.1.** De oude instellingen en accountkoppelingen worden bij verwijderen gewist. Updates op 2.0.1 moeten dezelfde nieuwe key gebruiken. Deze APK kan niet over de 2.0-release met het oorspronkelijke certificaat worden geïnstalleerd.

De gebruiker heeft 2.0 op de TV getest en de twee problemen gemeld. Versie 2.0.1 is nog niet fysiek op die TV getest. Warmte blijft een tintoverlay; 60% kan zwart optillen. Dit verandert geen gekalibreerde kleurtemperatuur en bestuurt geen Ambilight- of TV-backlightinstellingen.
