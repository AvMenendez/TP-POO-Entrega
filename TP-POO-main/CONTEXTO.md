# Sky Defense — Contexto del proyecto

Juego de defensa aérea (TP de POO). El jugador controla un avión que **esquiva**
los misiles que lanzan drones enemigos, avanzando de nivel a medida que sobrevive.
Hecho en **Java + Swing**, con estructura **MVC** (Model / Controlador / Views).

---

## Cómo compilar y ejecutar

Desde la carpeta del proyecto (`TP-POO-main/TP-POO-main`):

```powershell
# Compilar
javac -d bin (Get-ChildItem -Recurse -Filter *.java -Path src | % FullName)

# Ejecutar
java -cp bin Main
```

(O con el botón Run/▶ sobre `src/Main.java` en el IDE.)

---

## Estructura

```
src/
  Main.java                      Arranque (lanza el ControladorJuego en el EDT)
  Model/
    ObjetoVolador.java           Base: posición X (m) y Y = altitud (m)
    Avion.java                   Nave del jugador (energía, posición)
    Dron.java                    Enemigo (movimiento, disparo con cooldown)
    Misil.java                   Cae y detona a una altitud aleatoria
    Explosion.java               Área de daño que persiste unos instantes
    Escuadron.java               Grupo de 10 drones (máx 4 activos)
    Nivel.java                   Número de nivel y multiplicador de dificultad
    Jugador.java                 Nombre, vidas, puntaje, vida extra y daño a la nave
    Juego.java                   Orquesta el modelo (loop, spawn, daño, victoria)
    EstadoJuego.java             enum: JUGANDO / GAME_OVER / VICTORIA
    Leaderboard.java             Top 5 partidas, persistido en leaderboard.csv
    GestorSonido.java            Reproduce efectos y música de fondo; mute música/SFX por separado
    Config.java                  TODAS las constantes ajustables (ver abajo)
  Controlador/
    ControladorJuego.java        Game loop (Timer) y navegación de pantallas
    ControladorTeclado.java      Traduce flechas/WASD en intención de movimiento
    ControladorGameOver.java     Arma la pantalla de Game Over
  Views/
    PanelFondo.java              Base de los menús: fondo general (imagen) + boilerplate común
    Estilo.java                  Paleta y fábricas de UI (botones/títulos): un solo "look"
    PanelJuego.java              Dibuja el juego (convierte metros -> píxeles)
    MenuPrincipal.java           Menú: Jugar / Leaderboard / Opciones / Exit
    PanelNombre.java             Pide el nombre antes de jugar
    PanelLeaderboard.java        Muestra el top 5
    PanelOpciones.java           Silenciar Musica / SFX (toggles ON/OFF)
    PanelGameOver.java           Fin de partida (Reiniciar / Menú / Salir)
    Sprites.java                 Carga las imágenes desde Res/Sprites/
Res/Sprites/                     Imágenes de los objetos y el fondo (PNG)
Res/Sounds/                      Efectos y música de fondo (.wav)
Res/Fuentes/Silkscreen/          Fuente Silkscreen (.ttf) de los botones
```

---

## Cumplimiento de la consigna

| Requisito | Estado | Dónde |
|---|---|---|
| Avión se mueve izq/der y varía altitud **1000–5000 m** | ✅ | `ControladorJuego.moverAvion`, `Config.ALTITUD_*` |
| Misiles caen en línea recta desde el dron | ✅ | `Misil.descender` |
| Detonación automática a altitud aleatoria **1200–4500 m** | ✅ | `Misil` (constructor) |
| Escuadrones de **10**, máx **4** activos a la vez | ✅ | `Juego.TAM_ESCUADRON`, `DRONES_ACTIVOS` |
| Cada dron entra por un **extremo aleatorio**, a tiempos aleatorios y sin superponerse | ✅ | `Juego.controlarSpawnDrones` / `entradaLibre` |
| Sobrevivir → avanza de nivel | ✅ | `Juego.verificarCondicionVictoria` |
| Por nivel aumentan vel. drones, vel. caída misiles y frecuencia **+15%** | ✅ | `Nivel.INCREMENTO_POR_NIVEL` |
| Daño por distancia explosión↔avión (40/20/10/0 pts; 0/-20%/-40%; <20 vida) | ✅ | `Juego.aplicarDanioPorDistancia`, `Jugador.recibirDanio` |
| **+300** puntos por superar nivel | ✅ | `Juego.PUNTOS_NIVEL` |
| **Vida extra cada 1000** puntos | ✅ | `Jugador.sumarPuntos` |

Tabla de daño, medida **al cuerpo del avión** (distancia de la explosión a la
superficie de la nave = distancia al centro − `RADIO_AVION`, porque la aeronave ocupa espacio):
- **> 150 m** → +40 puntos, sin daño (esquive limpio; ver moderación con `RADIO_AMENAZA` abajo)
- **80–150 m** → +20 puntos, −20% energía
- **20–80 m** → +10 puntos, −40% energía  *(extensión: la consigna da 0)*
- **< 20 m** → pierde una vida

Si la nave se queda sin energía por el daño acumulado, pierde una vida y la energía se
repone al máximo (`Jugador.recibirDanio` → `Avion.reponerEnergia`).

---

## Decisiones de diseño (importante para defender el TP)

1. **Modelo en metros, vista en píxeles.** Todo el modelo trabaja en metros (como la
   consigna). La Vista convierte a píxeles con `Config` (misma escala en ambos ejes).
   `posicionX` = horizontal (m); `posicionY` = **altitud** (m, a mayor valor más alto).

2. **Detonación como explosión con área que persiste.** El misil detona a su altitud
   programada y deja una `Explosion` que vive ~0,45 s. El avión recibe el daño de la
   tabla si entra en el radio mientras la explosión está activa; si nunca se acerca,
   queda a salvo y suma puntos. Esto evita que solo cuenten los impactos directos
   instantáneos, **respetando** las distancias de la consigna.

3. **Daño por distancia al cuerpo + choque (extensión).** El avión tiene un radio de
   cuerpo (`RADIO_AVION = 200 m`), porque la aeronave ocupa espacio.
   - **Detonación automática (consigna):** el misil explota a su altitud programada y el
     daño se mide **al cuerpo** del avión (distancia al centro − `RADIO_AVION`), aplicando
     los tramos **20/80/150** literales sobre esa distancia (`Explosion.distanciaAlAvion`).
     Medir al cuerpo (en vez de al centro) da más alcance efectivo sin tocar los números
     de la consigna.
   - **Choque (extensión de jugabilidad, NO está en la consigna):** un misil no se puede
     atravesar. Si su cuerpo (`RADIO_MISIL`) toca el del avión (`RADIO_AVION`), el misil
     **detona en el acto** y el daño sale de la **misma tabla por distancia** (no es un
     golpe fijo de una vida). La colisión la decide el propio misil (`Misil.impactaA`,
     Information Expert).

4. **Intervalo entre misiles (cooldown) que escala con el nivel.** El cooldown base es
   **1 segundo** (`Config.COOLDOWN_DISPARO_TICKS`), pero se **divide por el multiplicador
   del nivel** (mínimo 1 tick) y se le pasa a cada dron al crearlo. Así la *frecuencia de
   disparo* sube de verdad por nivel (la consigna): sin esto, en niveles altos los drones
   cruzan tan rápido que el cooldown fijo les permitía ~1 misil y el juego se hacía fácil.

5. **Sprites desde archivo.** Cada objeto usa su PNG real desde `Res/Sprites/`
   (`Sprites.java`). Si falta un archivo, se usa un **dibujo de respaldo** por código.

6. **Daño exacto vs visual de la explosión.** El daño usa las distancias exactas de la
   consigna (`ESCALA_RADIO_EXPLOSION = 1.0`). La bola de fuego se dibuja más grande
   solo por estética (`ESCALA_VISUAL_EXPLOSION = 2.5`); no afecta el daño.

7. **Proporciones / "zoom".** Para que los objetos no se vean diminutos se acercó la
   cámara (`METROS_POR_PIXEL = 6`) y se agrandó la ventana (1000×780), sin cambiar los
   valores en metros de la consigna. El tamaño de los sprites se fija en píxeles
   (constantes `DRON_PX`/`NAVE_PX`/`MISIL_W`/`MISIL_H` en `PanelJuego`) y es puramente
   visual: no influye en el daño (que depende solo de la distancia de la explosión).

8. **Drones repartidos a lo ancho (entran de a uno).** No se crean todos en el mismo
   instante (si no, los del mismo extremo quedaban superpuestos y parecían 2). Entra un
   dron y el siguiente por ese extremo recién aparece cuando el anterior ya avanzó
   `SEPARACION_SPAWN` metros (= `ANCHO_M / DRONES_ACTIVOS` = 1500 m), de modo que los 4
   drones quedan **repartidos a lo ancho** de la pantalla y no amontonados en "dúos"
   junto a los bordes (lo que dejaba grandes zonas sin drones ni misiles y hacía el
   juego demasiado fácil). Se le suma una pequeña espera al azar (`ESPERA_SPAWN_MIN`–
   `MAX` ticks) para que el instante exacto no sea perfectamente regular. Si el extremo
   elegido está ocupado se prueba el otro, y si ambos lo están se reintenta el próximo
   tick (`Juego.controlarSpawnDrones` + `entradaLibre`).

9. **Tope de niveles = 15 (extensión).** La consigna no fija un final: sobrevivir avanza
   de nivel indefinidamente. Se agregó un tope `NIVEL_MAX = 15`: al superar el nivel 15
   el juego termina con **victoria** (`EstadoJuego.VICTORIA` → pantalla "¡GANASTE!"). El
   HUD muestra el progreso como `Nivel: n/15`.

10. **Moderación del puntaje (`RADIO_AMENAZA`).** El +40 por "esquive limpio" se otorga
    solo si el misil realmente fue una amenaza: detonó a ≤ `RADIO_AMENAZA` (600 m) del
    cuerpo del avión. Como los misiles caen rectos y la mayoría explota lejos, sin este
    tope cada misil sumaba +40 y el puntaje (y las vidas extra) se disparaba. Más allá de
    esa banda no suma puntos (`Juego.actualizarExplosiones`).

11. **Daño resuelto en el modelo (Information Expert).** El que tiene las vidas y la nave
    es quien decide qué pasa con el daño: `Jugador.recibirDanio` daña la nave y, si se
    queda sin energía, descuenta una vida y la repone (`Avion.reponerEnergia`). El
    orquestador (`Juego`) ya no manipula la energía de la nave.

12. **Teclado separado del orquestador (SRP).** `ControladorTeclado` traduce flechas/WASD
    en intención de movimiento; `ControladorJuego` solo consulta su estado y se ocupa del
    loop y la navegación. Antes el controlador hacía las dos cosas (era `KeyAdapter`).

13. **UI compartida sin repetición (DRY/SRP/OCP).** `PanelFondo` es la clase base de los
    menús: concentra el armado común (tamaño/layout/margen) y dibuja el fondo con velo
    oscuro. Acepta una imagen por pantalla (constructor con `BufferedImage`): el menú usa
    `Sprites.FONDO_MENU` y el resto el `Sprites.FONDO` general, con cascada de respaldo.
    `Estilo` centraliza la paleta y las fábricas de botones/títulos (Pure Fabrication), así
    el "look" se define en un solo lugar. Los botones son transparentes (solo texto blanco)
    con fuente **Silkscreen**; el título del menú usa Silkscreen y combina colores por
    palabra vía HTML (`Estilo.tituloSilkscreen` + `Estilo.coloreado`).

14. **Sonido centralizado con mute por canal (`GestorSonido`).** Una sola clase (Pure
    Fabrication, estática) carga y reproduce los `.wav` desde `Res/Sounds/` (misma estrategia
    que `Sprites`: disco con respaldo al classpath). Distingue dos canales: **efectos**
    puntuales (`reproducir`, p. ej. despegar/perder vida/subir de nivel) y **música de fondo**
    en bucle (`reproducirMusicaEnLoop`). Cada canal se silencia por separado desde `PanelOpciones`
    (`setMusicaSilenciada` / `setEfectosSilenciados`); el estado es global y persiste entre
    pantallas. La música recuerda la "pista deseada" (`pistaActual`): al mutear frena la línea
    y al desmutear la retoma desde el principio. Los efectos usan un `LineListener` que cierra
    el clip al terminar (evita que el GC corte el sonido). La música del menú no se reinicia al
    volver a él si ya está sonando (guard por pista + `isRunning`).

---

## Constantes ajustables (`Model/Config.java`)

| Constante | Valor | Para qué |
|---|---|---|
| `ANCHO_PX` / `ALTO_PX` | 1000 / 780 | Tamaño de ventana |
| `METROS_POR_PIXEL` | 6.0 | "Zoom" (menos = objetos más grandes) |
| `MS_POR_TICK` | 30 | Duración del tick (~33 fps) |
| `COOLDOWN_DISPARO_TICKS` | 33 (=1 s) | Cooldown base entre misiles (nivel 1); se divide por el multiplicador del nivel |
| `ALTITUD_MIN/MAX` | 1000 / 5000 | Rango de altitud del avión (consigna) |
| `DETONACION_MIN/MAX` | 1200 / 4500 | Altitud de detonación (consigna) |
| `VEL_HORIZONTAL / VEL_ALTITUD` | 56 / 56 | Velocidad de la nave del jugador (m por tick) |
| `ALTITUD_INICIAL` | 1500 | Altitud de la nave al empezar la partida |
| `VIDAS_INICIALES` | 3 | Vidas con las que arranca el jugador |
| `RADIO_DANIO / _ALTO / _CRITICO` | 150 / 80 / 20 | Distancias de daño (consigna) |
| `RADIO_AMENAZA` | 600 | Banda exterior: el +40 por esquive se da solo si el misil detonó a ≤ 600 m |
| `ESCALA_RADIO_EXPLOSION` | 1.0 | 1.0 = consigna exacta |
| `RADIO_AVION` | 200 | Radio del cuerpo de la nave: se resta para medir el daño al cuerpo y define el choque |
| `RADIO_MISIL` | 40 | Radio del cuerpo del misil para el choque con la nave |
| `ESCALA_VISUAL_EXPLOSION` | 2.5 | Tamaño visual de la explosión (no daña) |

Tamaños de **dibujo** de los objetos (solo visual, no afectan el daño) en
`Views/PanelJuego.java`: `DRON_PX=96`, `NAVE_PX=96`, `MISIL_W=36`, `MISIL_H=66`.

Otros (en `Juego.java`): `TAM_ESCUADRON=10`, `NIVEL_MAX=15` (tope de niveles; al
superar el nivel 15 se gana el juego → `EstadoJuego.VICTORIA`), `DRONES_ACTIVOS=4`,
`SEPARACION_SPAWN=ANCHO_M/DRONES_ACTIVOS` (=1500 m; reparte los drones a lo ancho
y evita los "dúos"), `ESPERA_SPAWN_MIN=10`/`ESPERA_SPAWN_MAX=40` (jitter aleatorio
en ticks entre un dron y el siguiente), `DRON_VEL_BASE=22`, `MISIL_VEL_BASE=32`,
`FREC_DISPARO_BASE=0.04` (velocidades/frecuencia base del nivel 1; valores propios,
escalan +15% por nivel), `PUNTOS_NIVEL=300`, `PUNTOS_LEJOS/MEDIA/CERCA=40/20/10`.
Dificultad por nivel en `Nivel.INCREMENTO_POR_NIVEL=0.15`. Las vidas y velocidades
iniciales de la nave ahora viven en `Config` (`VIDAS_INICIALES`, `VEL_HORIZONTAL`,
`VEL_ALTITUD`, `ALTITUD_INICIAL`). El escuadrón se crea con `new Escuadron(TAM_ESCUADRON)`.

---

## Imágenes (`Res/Sprites/`)

Nombres esperados (PNG con **fondo transparente**): `dron.png`, `misil.png`,
`explosion.png`, `nave.png`. Detalle en `Res/Sprites/LEEME.txt`.

- `dron.png`, `explosion.png`, `nave.png` → provistos por el usuario, **cargan OK**.
- `misil.png` → **recortado de `dron.png`** (el mismo misil que cuelga del dron:
  cabezal rojo + cuerpo verde, punta abajo), así coincide con el del dron. El original
  quedó respaldado en `misil_backup.png`.
- `fondo.png` → **fondo general** de las pantallas (los menús lo dibujan vía
  `PanelFondo` con un velo oscuro; el juego, vía `PanelJuego`). Si falta, queda el
  color de respaldo `Estilo.FONDO`.
- `fondo_menu.png` → **fondo propio del menú principal** (`Sprites.FONDO_MENU`). Cada
  pantalla puede pasarle a `PanelFondo` su imagen; si la específica no está, cae al
  fondo general y, si tampoco, al color de respaldo.

---

## Sonido (`Res/Sounds/`)

Archivos `.wav` (los carga `GestorSonido` desde disco; si falta uno, se avisa por `System.err`
y el juego sigue sin sonido):

- **Efectos** (canal SFX): `iniciarJuego.wav` (al despegar), `perderVida.wav`, `subirDeNivel.wav`.
- **Música** (canal Música, en bucle): `Entry_Screen_Rimworld_OST.wav` (menú) y
  `Enemy_Approaching.wav` (durante la partida).

Ambos canales se silencian por separado desde **Opciones** (`Musica: ON/OFF`, `SFX: ON/OFF`).
Nota: los `.wav` de música pesan bastante (40 / 10 MB) y se abren en el hilo de UI, así que la
primera carga puede tener un pequeño tirón (mejora futura: cargar en un hilo aparte).

---

## Fuente (`Res/Fuentes/Silkscreen/`)

Los botones usan la fuente **Silkscreen** (`slkscr.ttf` regular / `slkscrb.ttf` bold).
**Debe estar instalada en el sistema** (clic derecho en el `.ttf` → *Instalar*), porque el
código la pide por nombre (`new Font("Silkscreen", ...)` en `Estilo`). Si no está instalada,
los botones salen igual (transparentes, texto blanco) pero con la fuente por defecto.

---

## Pendientes / ideas

- [x] **Sonido**: efectos + música de fondo (`GestorSonido`).
- [x] Pantalla de **Opciones**: silenciar Musica / SFX por separado.
- [ ] Más opciones: volumen (slider), dificultad, borrar leaderboard.
- [ ] Cargar la música pesada en un hilo aparte (evitar el tirón al abrir menú/partida).
- [ ] Posible feedback de daño (flash de pantalla, texto flotante "+40 / −20%").

---

## Notas de POO / arquitectura

Se mantuvo la estructura MVC. Mejoras aplicadas respecto a la versión original:
- Lógica de drones movida al `Escuadron` (Information Expert).
- Detonación y choque decididos por el propio `Misil` (`haDetonado` / `impactaA`, Information Expert).
- Daño resuelto en el modelo: `Jugador.recibirDanio` coordina energía ↔ vidas y la `Avion`
  es dueña de su energía (`reponerEnergia`); el orquestador ya no la manipula (Information Expert).
- Encapsulamiento: se quitaron setters/getters muertos que permitían romper invariantes
  (p. ej. `setPuntaje`), y las listas expuestas (`getDrones`, `getMisilesActivos`,
  `getExplosionesActivas`) se devuelven en **solo lectura**.
- UI sin repetición (DRY/SRP/OCP): `PanelFondo` (base de menús con el fondo común) y `Estilo`
  (paleta + fábricas de botones/títulos en un solo lugar, una *Pure Fabrication*).
- Input separado: `ControladorTeclado` maneja el teclado; `ControladorJuego` solo orquesta (SRP).
- Sonido aislado en `GestorSonido` (Pure Fabrication): efectos y música con mute por canal; el
  resto del código solo pide "reproducí esto", sin conocer `javax.sound`.
- Constantes de jugabilidad nombradas y centralizadas en `Config`.
- La Vista no tiene reglas; el Modelo no hace I/O de presentación.
- Cada función tiene un comentario breve que explica qué hace.

Decisión consciente: `ObjetoVolador` es una base que comparte **solo el estado de posición**
(no define un `mover()` polimórfico). Es a propósito: los objetos se mueven de forma
heterogénea (la nave por input externo con parámetros; la explosión no se mueve), así que
forzar un contrato común obligaría a métodos vacíos o artificiales y empeoraría el diseño.
