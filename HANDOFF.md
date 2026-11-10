# Staturefy — handoff

Estado verificado el 10 de noviembre de 2026. Este documento describe el árbol fuente local y los artefactos mencionados en la conversación; no presupone que el JAR enviado coincida con el código fuente.

## 1. Qué es Staturefy

Staturefy es un mod visual solo de cliente para cambiar los perfiles corporales de los avatares de jugadores en Minecraft Java.
Conserva el renderer vanilla y sus dimensiones de gameplay; no cambia hitboxes, movimiento, datos del servidor ni protocolo.
El proyecto apunta solo a Minecraft Java **26.3**, **Fabric Loader 0.19.5+**, **Fabric API** y **Java 25**; Mod Menu es opcional. No hay target para Forge ni NeoForge.

## 2. Estructura y archivos clave

- `build.gradle`, `gradle.properties`, `settings.gradle`: Fabric Loom, dependencias y versión. El árbol fuente actual declara `mod_version=1.0.0`, `minecraft_version=26.3`, `archives_base_name=staturefy`.
- `src/main/resources/fabric.mod.json`: metadata actual del source tree: ID `staturefy`, icono `assets/staturefy/icon.png`, client entrypoints y referencia al Mixin config.
- `src/main/resources/staturefy.client.mixins.json`: mezcla de renderizado del jugador, capa de modelo, partes del modelo, armadura de Smooth Player Animations y PatPat. `defaultRequire` actualmente es `1`.
- `src/main/resources/assets/staturefy/icon.png` y `src/main/resources/assets/staturefy/lang/`: logo estático y traducciones `en_us`, `es_ar`, `es_es`, `es_mx`, `fr_fr`, `id_id`, `ja_jp`, `ko_kr`, `ru_ru`, `zh_cn`.
- `src/client/java/dev/chibi/player/BodyProfile.java` y `BodyProfiles.java`: perfiles y escalas. Valores actuales: Vanilla `1/1/1`, Teen `0.90/0.84/0.90` y cabeza `1.30`, Chibi `0.78/0.68/0.78` y cabeza `1.55`, Tall `1/1.06/1` y cabeza `1.0`.
- `StaturefyConfig.java`: persistencia automática en `config/staturefy.json`, perfil local/global y overrides por UUID.
- `StaturefyConfigScreen.java`, `ProfileCardButton.java`, `PlayerProfilesScreen.java`: menú principal con tarjetas de perfiles, ajustes globales/locales y lista buscable/paginada de jugadores.
- `StaturefyClient.java`, `StaturefyModMenu.java`: comando `/staturefy` (y `/staturefy config`) e integración opcional con Mod Menu.
- `StaturefyRenderContext.java`: contexto de perfil por hilo y `VanillaRigTree`, usado también por la ruta especial de casco CEM.
- Mixin principales en `src/client/java/dev/chibi/player/mixin/`:
  - `LivingEntityRendererMixin.java`: escala del cuerpo/cabeza, ajuste al sentarse con Chibi y Foot IK de Touch'n Motion sobre vallas para Chibi/Teen.
  - `ModelFeatureRendererMixin.java`, `ModelMixin.java`, `ModelPartRenderScopeMixin.java`: propagan el perfil a las partes vanilla y restauran/limpian estado temporal.
  - `SmoothPlayerAnimationsArmorModelMixin.java` y `SmoothPlayerAnimationsArmorBendMixin.java`: ruta especial de armadura/casco cuando Smooth Player Animations y CEM están habilitados.
  - `PatPatRendererMixin.java`: integración opcional para subir la animación de caricia con Chibi/Teen.
- `docs/ARCHITECTURE_ANALYSIS.md`: comparación de enfoques; recomienda preservar el renderer y las partes vanilla. `docs/REQUIREMENTS.md`, `docs/ROADMAP.md`, `docs/PROJECT_RULES.md`, `docs/RELEASE_CHECKLIST.md` y `docs/PUBLISHING_COPY.md` contienen requisitos/proceso, aunque varios necesitan sincronizarse con el estado actual.

## 3. Trabajo realizado y commits

### Funciones presentes en el source tree actual

- Cuatro perfiles: Vanilla, Teen, Chibi y Tall. Vanilla es el valor inicial tanto para el jugador local como para otros jugadores.
- Perfil local, perfil global para otros y override individual; los ajustes se guardan localmente y el override individual prevalece sobre el global.
- Menú de tarjetas con perfiles y una pantalla de jugadores con búsqueda y paginación.
- Comandos `/staturefy` y `/staturefy config`.
- Ajustes específicos de renderizado para monturas, Foot IK experimental de Touch'n Motion, PatPat y Smooth Player Animations con CEM.
- Localizaciones para diez idiomas/regiones y logo de Mod Menu.
- Mantiene la ruta vanilla de skin/renderizado y el hitbox del juego; no requiere instalación en el servidor.

### Git

- Rama: `main`; remoto `origin`: `https://github.com/SiSoyKuri/StatureFy.git`.
- `HEAD` está en `fc2b445` (`Keep only the approved project logo`), etiquetado `v0.1.0`.
- Commits recientes visibles: `fc2b445` (logo aprobado), `c4e40ab` (logo del proyecto), `2ffa68f` (preparación de Staturefy 0.1.0).
- Hay cambios locales **sin commit** para 1.0.0, cambio del ID/namespace, UI, presets y renderizado. `git status --short` mostraba 16 archivos modificados/eliminados y varios archivos/carpetas sin seguimiento. **No hacer reset, checkout ni limpieza del árbol antes de guardar/revisar esos cambios.**

## 4. Bugs conocidos, riesgos y cuestiones abiertas

1. **Artefacto de 1.0 no coincide con el source tree.** El archivo recibido como `C:\Users\PcVIP\Desktop\Staturefy\mod\staturefy-1.0.0+26.3-fabric.jar` declara en su `fabric.mod.json` `id: chibiplayer`, `version: 0.1.0+26.3`, ruta de icono `assets/chibiplayer/icon.png` y Mixin `chibiplayer.client.mixins.json`. El source tree actual declara `id: staturefy`, versión Gradle `1.0.0+26.3`, assets `assets/staturefy/` y Mixin `staturefy.client.mixins.json`. El JAR sí contiene clases Teen/Tall, `ProfileCardButton`, `PatPatRendererMixin` e idiomas adicionales, pero no se debe asumir que es el build reproducible del source tree. **Recompilar y verificar metadata/rutas antes de subir.**
2. **Mixin targets frágiles:** `staturefy.client.mixins.json` tiene `defaultRequire: 1`; los `@Inject` de `LivingEntityRendererMixin`, `ModelMixin`, `ModelFeatureRendererMixin`, `ModelPartRenderScopeMixin` y los mixins SPA no declaran `require = 0`. Solo los puntos de inyección de PatPat declaran `require = 0`. Esto puede volver un cambio de firma/nombre de Minecraft o un mod opcional un fallo de arranque. La conversación contiene una petición anterior de tolerar inyecciones fallidas, pero el código actual no la refleja globalmente; revisar de forma deliberada antes de modificar, ya que hacerlo opcional también puede ocultar una función desactivada.
3. **Compatibilidad sin matriz reproducible:** la arquitectura conserva el renderer/modelo vanilla, pero no hay pruebas automatizadas de integración en `src/test`. La compatibilidad exacta con cada versión de Punchy, Fresh Animations Player Extension, Just Expressions, cosméticos/capas y proveedores de skin debe tratarse como pendiente de validar por combinación.
4. `README.md` todavía enumera solo Vanilla y Chibi. `docs/REQUIREMENTS.md` conserva restricciones de V1 anteriores a Teen/Tall. `docs/RELEASE_CHECKLIST.md` y `CHANGELOG.md` también deben revisarse contra el artefacto final antes de publicar.
5. El usuario había confirmado previamente que el casco de SPA con CEM y la ruta FA:Player ya funcionaban, y que los ajustes de monturas, vallas/Foot IK y PatPat quedaron bien. No se ha vuelto a probar el source tree actual dentro del modpack después de los cambios locales de 1.0.

### Crash reports / stack traces

- No hay crash report ni log dentro de este repositorio.
- Históricamente se reportó un `InvalidInjectionException` con `renderToBuffer` al probar una versión antigua de Minecraft/Not Enough Animations; el usuario confirmó que una corrección anterior permitió iniciar. El archivo exacto del crash no está en el repo y no se puede reconstruir aquí.
- Como el source actual aún inyecta `Model.renderToBuffer` y mantiene `defaultRequire: 1`, volver a validar ese target para las versiones soportadas antes de ampliar compatibilidad.
- En los últimos mensajes el usuario no reportó un bug visual abierto; lo pendiente inmediato es coherencia del artefacto y validación de release.

## 5. Estado al pausar y siguiente paso

La tarea conversacional inmediatamente anterior fue preparar un changelog comparando el JAR subido (`staturefy-0.1.0-26.3-fabric.jar`) con el JAR recibido como 1.0.0. Se creó un borrador externo al repo en `C:\Users\PcVIP\Desktop\Staturefy\mod\CHANGELOG_1.0.0.md`; no se copió aún a la raíz del repositorio. La comparación confirmó diferencias de presets, UI, idiomas, icono, PatPat y ajustes de renderizado, además de la discrepancia de metadata descrita arriba.

En el source tree hay cambios locales no confirmados para actualizar el proyecto a `staturefy`/`1.0.0`, mover recursos y mixins al nuevo namespace, terminar el menú/tarjetas y actualizar comportamiento de renderizado. El siguiente paso recomendado es conservar ese diff, compilar desde esta raíz, inspeccionar el `fabric.mod.json` y entradas del JAR resultante, probarlo en el modpack exacto y solo entonces sincronizar changelog/README/checklist y preparar commit/tag.

La sesión anterior registró un `build` exitoso después de cambiar el source tree, pero el JAR 1.0.0 enviado posteriormente no coincide con esos metadatos; repetir el build y la inspección del artefacto antes de considerarlo validado.

## 6. Compilar y probar

Usar JDK 25. Comandos Windows desde la raíz del repo:

```powershell
java -version
.\gradlew.bat clean
.\gradlew.bat build
.\gradlew.bat jar
.\gradlew.bat runClient
.\gradlew.bat test
```

- `runClient` está disponible en las tareas Gradle. Es la forma de iniciar cliente para prueba; luego cargar el modpack objetivo para verificar integraciones.
- `test` existe, pero no hay `src/test` en el árbol actual, por lo que no aporta cobertura de lógica por ahora. No se encontró una tarea `runTest`.
- `build.gradle` define `verifyReleaseVersion`; para validar la sección 1.0.0 del changelog y generar el JAR:

```powershell
.\gradlew.bat jar verifyReleaseVersion -PreleaseModVersion=1.0.0
```

- El JAR esperado desde el source tree actual es `build/libs/staturefy-1.0.0+26.3-fabric.jar`.
- CI (`.github/workflows/build.yml`) ejecuta `./gradlew --no-daemon jar`; el workflow de release además ejecuta `verifyReleaseVersion` para el tag `v<version>`.
- La enumeración de tareas se comprobó el 10-11-2026: aparecen `build`, `jar`, `runClient`, `runServer`, `runClientRenderDoc` y `test`; no aparece `runTest`.

## 7. Decisiones de diseño que hay que preservar

- **No registrar un renderer sustituto ni un segundo modelo de jugador.** La arquitectura elegida mantiene el renderer, skin, estado de renderizado, modelo y capas vanilla para reducir conflictos con animaciones, armadura, capas, cosméticos y expresiones.
- **Solo transformar renderizado del cliente.** No cambiar dimensiones, colisiones, eye height, movimiento, paquetes ni datos del servidor. Otros jugadores no necesitan instalar el mod.
- **Trabajar con perfiles fijos**, no sliders por parte. Un intento de sliders independientes desfasó los objetos de las manos al modificar el torso; el usuario decidió volver a presets para evitar rigs inconsistentes.
- **Preservar la pose y restaurar estado temporal.** `StaturefyRenderContext` propaga el perfil; los cambios temporales a escalas/visibilidad deben limpiarse al final de la ruta de render y no filtrarse entre entidades.
- **Integraciones opcionales aisladas.** SPA/CEM usa `@Pseudo` y lookup reflectivo de configuración; Touch'n Motion usa reflexión; PatPat tiene mixins `@Pseudo` con `require = 0`. Evitar convertir mods opcionales en dependencias duras.
- **No tocar las mezclas de armadura SPA a la ligera.** El fallback de `VanillaRigTree` se añadió para el casco con CEM y el usuario confirmó previamente que resolvió ese caso.
- **Cambios pequeños y verificados.** Las proporciones y offsets se ajustaron mediante pruebas del usuario; no cambiar escalas/offsets sin reproducir el problema en el perfil/modpack correspondiente.

