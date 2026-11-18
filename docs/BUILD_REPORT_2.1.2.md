# NatjeTube 2.1.2 — samenhangende navigatie

18 november 2026.

Aanleiding: foto's van 2.1.1 lieten een te brede menukolom, afwijkende tekstknoppen, overlappende zoek/account/klok/logo-elementen en onlogische D-pad-routes zien.

De zijbalk is 232dp breed met 16dp binnenruimte. Zoeken, Home, Subscriptions, Library, Explore, Night Mode en Settings gebruiken hetzelfde icon_header_item-layout, dezelfde rijhoogte van 56dp en bestaande typografie. De vier hoofdsecties blijven de bestaande Leanback-selectie gebruiken; de drie acties staan er direct tegenaan. Nachtmodus schakelt alleen bij klikken; lang indrukken of Menu opent de nachtinstellingen.

Browse maakt de oude NavigateTitleView niet meer aan. Daardoor bestaan de zwevende zoekknop, accountknop, klok, datum en badge op dit scherm niet meer. Accountbeheer blijft beschikbaar onder Settings. De filters hebben eigen marges boven de videogrid, een horizontale scrollrij en afzonderlijke selectie- en focusmarkering in het bestaande kleurenpalet.

D-pad: zoeken omlaag naar Home; eerste hoofdsectie omhoog naar zoeken; laatste hoofdsectie omlaag naar Night Mode; Night Mode omhoog naar de laatste sectie; Settings omhoog naar Night Mode. Rechts vanuit het menu gaat naar het geselecteerde inhoudsfilter. Links vanaf het eerste filter of de eerste videokolom opent het menu. Omhoog vanaf de eerste videorij gaat naar het geselecteerde filter; omlaag vanaf een filter naar de video's. De bovenste filterrij kan niet meer naar de oude bovenbalk wegspringen.

Validatie: 52 tests, nul fouten, [CI-build 44366512437](https://github.com/nadaldejonge/NatjeTube/actions/runs/44366512437). Appcode ab2bb9dfb. Extra controles simuleren de Browse.onStart-uitlijning en bevestigen dat selecteren van Explore Home niet uit de compacte lijst scrolt. Een 1920×1080 Android TV-emulator bevestigt de echte focusrechthoeken: Home y144–256, Subscriptions y256–368, Library y368–480, Explore y480–592, Night Mode y592–704, Settings y704–816; alle x32–432. Night Mode → Settings → Night Mode → Explore werkte met D-pad. Persoonlijke feed niet beoordeeld (emulator niet ingelogd). Geen installatie op de fysieke tv voor deze versie.

APK: NatjeTube_2.1.2_universal.apk, package nl.natje.natjetube, versie/code 2.1.2 / 2463. v1/v2-handtekening en zipalign gecontroleerd. Dezelfde signer als 2.0.1/2.1.0/2.1.1: f590ea01b366e4c2a4560514bba579bcc4cb1efd69077e43f663bbc9ba41834d. SHA-256: 2335356eb2b08ea7c90f756da27077b49ac4826d1032931865d2001e49cdf30e.
