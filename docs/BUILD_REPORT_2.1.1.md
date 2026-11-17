# NatjeTube 2.1.1 — zijbalkbreedte

17 november 2026. Gerichte correctie op 2.1.0.

De vervangende LinearLayout rond de Leanback-menulijst had geen eigen layoutparameters. In de wrap_content-headerdock kreeg de root daardoor standaard MATCH_PARENT en konden Night Mode en Instellingen tot over het videoveld uitrekken. De root neemt nu expliciet de bestaande dimensie lb_browse_headers_width over.

De eerdere test dwong de fragmentroot zelf op 360 pixels breed en controleerde alleen de verticale positie. Die test kon deze fout niet vinden. De nieuwe test gebruikt de echte lb_browse_fragment-layout met zijn wrap_content-headerdock op 1920×1080 en 3840×2160. Root en beide footerknoppen moeten exact de zijbalkbreedte meten. In dezelfde test wordt de oude MATCH_PARENT-instelling hersteld: die moet de te brede footer reproduceren. Beide gevallen slagen.

Alle 51 regressietests, compilatie en release-lint geslaagd. [Build](https://github.com/nadaldejonge/NatjeTube/actions/runs/44139415912), appcode e40c2e96b. De nieuwe breedtetest is een Android-layoutsimulatie; deze nieuwe APK is nog niet op de fysieke TV getest.

APK: NatjeTube_2.1.1_universal.apk. Versie/code: 2.1.1 / 2462. Package: nl.natje.natjetube. Dezelfde signer als 2.0.1 en 2.1.0: f590ea01b366e4c2a4560514bba579bcc4cb1efd69077e43f663bbc9ba41834d. v1/v2-handtekening en zipalign gecontroleerd. SHA-256: fcda6aa0b31b17916b90a14c5480cc8dca58741053a01a1094d27ae6fa545eb4.

Buiten de versie en tests is alleen de breedte van de zijbalkcontainer aangepast. Kleuren, lettertypes, Home-feed en overige indeling zijn behouden. Installeer als gewone update over 2.0.1 of 2.1.0; verwijderen of gegevens wissen is niet nodig. De oorspronkelijke 2.0-release gebruikt nog de oudere key en vereist een schone installatie.
