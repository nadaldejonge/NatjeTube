# Privacy in NatjeTube

NatjeTube is een onafhankelijke Android TV-client van NatjeNN. De app gebruikt externe diensten voor video’s, accountkoppeling en optionele extra functies.

Video’s en accountgegevens worden bij YouTube/Google opgevraagd. Bij accountkoppeling worden aanmeldtokens op het apparaat bewaard. Deel deze tokens en appback-ups niet openbaar.

Optionele diensten zoals SponsorBlock, DeArrow en Return YouTube Dislike ontvangen video-ID’s om hun functies te leveren. Voor het gebruik van externe diensten gelden ook hun eigen privacyvoorwaarden.

Crashrapportage via het oorspronkelijke Firebase-project en automatische appupdates zijn uitgeschakeld. Updates installeer je handmatig. Projectlinks verwijzen naar de NatjeTube-repository op GitHub; het openen ervan maakt verbinding met GitHub.

Deze beschrijving gaat over de aangepaste broncode. Er is nog geen volledige controle van het netwerkverkeer op een fysiek TV-apparaat uitgevoerd. Zet geen privégegevens of aanmeldtokens in openbare GitHub-issues.

NatjeNightMode slaat voorkeuren, eigen presetnamen en eventuele handmatig ingevoerde coördinaten lokaal op het apparaat op. De zonberekening werkt offline; de functie vraagt geen locatiepermissie en verstuurt deze gegevens niet naar een extra dienst.
