# Zelf NatjeTube bouwen

```sh
git clone --recurse-submodules https://github.com/nadaldejonge/NatjeTube.git
cd NatjeTube
```

Gebruik JDK17, Android SDK34, build-tools30.0.3 en NDK21.0.6113669. Configureer `ANDROID_HOME` of een genegeerde `local.properties`. De Gradle7.5-wrapper is meegeleverd. Er is geen Firebase-configuratie nodig.

```sh
./gradlew :smarttubetv:assembleStstableRelease --no-daemon
```

Zonder eigen signingconfig levert dit een **unsigned** APK op. Voor handmatige builds worden credentials uitsluitend via `NATJETUBE_KEYSTORE`, `NATJETUBE_STORE_PASSWORD`, `NATJETUBE_KEY_ALIAS`, `NATJETUBE_KEY_PASSWORD` of een genegeerde lokale `keystore.properties` gelezen. `NATJETUBE_REQUIRE_SIGNING=true` verplicht signing. Deel geen private key of wachtwoorden. Met een andere key kun je de officiële eigen NatjeTube-installatie niet bijwerken; gebruik daarvoor de gepubliceerde APK.

De brandingguard controleert alle33 goedgekeurde bestanden tegen `branding/installed-assets.json` en `branding-inventory.json`. Er wordt geen beeldmerk gegenereerd. De guard ondersteunt ook de bestaande lokale structuur met broncode in een submap.

De handmatige GitHub Actions-workflow bouwt uitsluitend een unsigned testartefact. De eigen releasekey staat niet in deze repository of workflow.

## Tests

De bestaande Robolectric4.6.1 heeft voor zijn testproces Java11 nodig. Houd Java17 voor Gradle/Javac en zet `NATJETUBE_TEST_JAVA_HOME` op een geïnstalleerde JRE/JDK11:

```sh
./gradlew :common:testStstableReleaseUnitTest :smarttubetv:testStstableReleaseUnitTest -I tools/test-java11.gradle --no-daemon
```

Dit wijzigt de release-JVM of appdependencies niet. Simulaties zijn geen bewijs van HDR-/4K-/TV-stickcompatibiliteit; zie het buildrapport.
