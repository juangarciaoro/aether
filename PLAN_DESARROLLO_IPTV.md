# AETHER — Plan de Desarrollo IPTV Android
### App IPTV Ultra Premium · Phone + TV · APK Universal

---

## 0. DECISIONES DE ALTO NIVEL

### Nombre del proyecto
**AETHER** — suena premium, único, no describe literalmente "IPTV", escala bien como marca personal.
*(Cámbialo en cualquier momento, está centralizado en `strings.xml` y `build.gradle.kts`)*

### Stack tecnológico completo

| Capa | Tecnología | Versión objetivo |
|------|-----------|-----------------|
| Lenguaje | Kotlin | 2.0+ |
| UI Phone | Jetpack Compose | 1.7+ |
| UI TV | Compose for TV | 1.0-alpha (androidx.tv) |
| Reproducción | Media3 / ExoPlayer | 1.4+ |
| Arquitectura | Clean Architecture + MVI | — |
| DI | Hilt | 2.51+ |
| Red | Retrofit + OkHttp | 2.11 / 4.12 |
| Imágenes | Coil 3 | 3.0+ |
| BD Local | Room | 2.6+ |
| Preferencias | DataStore (Proto) | 1.1+ |
| Async | Coroutines + Flow | 1.9+ |
| Build | Gradle KTS + Version Catalog | AGP 8.5+ |
| Serialización | Kotlinx Serialization | 1.7+ |
| Parseo XML | Xml Pull Parser (nativo) | — |

### Estética: "CHROMATIC VOID"

Concepto diferenciador: cada canal/contenido **tiñe su entorno** con su color dominante, extraído dinámicamente del artwork. No es copiar Netflix ni Disney+. Es una app que respira con el contenido.

**Paleta base:**
```
#050508  —  Void         (fondo absoluto)
#0D0D1A  —  Deep Space   (superficies/cards)  
#141428  —  Cosmic       (inputs, drawers)
#7C6CFF  —  Neon Indigo  (acento primario, focus TV)
#FF6B9D  —  Plasma       (acento secundario, live indicator)
#E8E8FF  —  Starlight    (texto primario)
#6060A0  —  Nebula       (texto secundario)
```

**Color dinámico**: `Palette API` extrae el color dominante de cada canal → se usa como `glowColor` en los focus states de TV y como overlay sutil en el backdrop del player. Nada parecido en ninguna app IPTV existente.

**Tipografía:**
- Display / Títulos: **Space Grotesk** (Google Fonts, sci-fi pero orgánico)
- Cuerpo / UI: **Inter** (máxima legibilidad en pantallas oscuras)
- Datos / EPG / Tiempos: **JetBrains Mono** (reloj, duración, IDs)

**Signature element único**: El *Chromatic Glow Focus* en TV. Cuando haces focus en un canal con D-pad, el borde de la tarjeta emite un glow del color exacto extraído del logo del canal. Ninguna app de un billón de dólares lo hace en TV porque es técnicamente complejo. Nosotros sí.

---

## 1. ESTRUCTURA DE MÓDULOS

```
aether/
├── app/                          # Entry point, DI setup, navigation root
│
├── core/
│   ├── core-common/              # Extensions, utils, constants, Device type detection
│   ├── core-ui/                  # Design System: Theme, Typography, Colors, Components
│   ├── core-network/             # Retrofit, OkHttp, API clients base
│   ├── core-database/            # Room: entities, DAOs, migrations
│   ├── core-datastore/           # DataStore Proto: UserPreferences
│   └── core-player/              # Media3 wrapper, session, PiP manager
│
├── data/
│   ├── data-xtream/              # Xtream Codes API: live, VOD, series, EPG, catch-up
│   ├── data-m3u/                 # M3U parser (URL + local file)
│   └── data-epg/                 # XMLTV parser + EPG repository
│
└── feature/
    ├── feature-onboarding/       # Añadir fuentes, setup inicial
    ├── feature-home/             # Pantalla principal, Continuar viendo, Recientes
    ├── feature-live/             # Lista de canales en directo
    ├── feature-epg/              # Guía de programación EPG grid
    ├── feature-vod/              # Películas
    ├── feature-series/           # Series y episodios
    ├── feature-player/           # Player fullscreen: PiP, Multi-screen, Subtítulos
    ├── feature-search/           # Búsqueda global
    └── feature-settings/         # Configuración completa
```

---

## 2. PLAN DE FASES

---

### FASE 0 — Setup del Proyecto
**Duración estimada: 1 día**

**Objetivos:**
- Repo GitHub con README
- Proyecto Android multi-módulo configurado
- Version catalog (`libs.versions.toml`) completo
- Configuración base de Hilt, Kotlin 2.0, Compose

**Archivos clave a crear:**
```
settings.gradle.kts          # Declaración de todos los módulos
libs.versions.toml           # Version catalog centralizado
build-logic/                 # Convention plugins (para no repetir config)
  ├── android-library.gradle.kts
  ├── android-feature.gradle.kts
  └── android-application.gradle.kts
app/build.gradle.kts
app/src/main/AndroidManifest.xml   # Con LEANBACK_LAUNCHER + MAIN launcher
```

**AndroidManifest.xml — configuración TV+Phone:**
```xml
<!-- Phone launcher -->
<activity android:name=".MainActivity">
    <intent-filter>
        <action android:name="android.intent.action.MAIN"/>
        <category android:name="android.intent.category.LAUNCHER"/>
    </intent-filter>
</activity>

<!-- TV launcher (misma Activity, detecta en runtime) -->
<intent-filter>
    <action android:name="android.intent.action.MAIN"/>
    <category android:name="android.intent.category.LEANBACK_LAUNCHER"/>
</intent-filter>

<!-- TV banner 320x180dp en res/drawable-xhdpi/ -->
<application android:banner="@drawable/aether_tv_banner">
```

**Device detection utility (core-common):**
```kotlin
enum class DeviceType { PHONE, TV, TABLET }

fun Context.getDeviceType(): DeviceType {
    val uiModeManager = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
    return when {
        uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION -> DeviceType.TV
        resources.configuration.smallestScreenWidthDp >= 600 -> DeviceType.TABLET
        else -> DeviceType.PHONE
    }
}
```

**Prompts Claude Code sugeridos:**
```
"Crea la estructura completa del proyecto Android multi-módulo llamado Aether 
con Gradle KTS y Version Catalog. Incluye todos los módulos del plan."

"Configura los convention plugins en build-logic/ para android-library, 
android-feature y android-application."
```

---

### FASE 1 — Design System (core-ui)
**Duración estimada: 2 días**

**Objetivos:**
- Token system completo (colores, tipografía, espaciado, formas)
- Tema oscuro "Chromatic Void"
- Adaptive theme: Phone vs TV (mismos tokens, tamaños distintos)
- Componentes base reutilizables

**Estructura del Design System:**
```
core-ui/
├── theme/
│   ├── AetherTheme.kt       # MaterialTheme wrapper + TV adaptation
│   ├── Color.kt             # Paleta completa
│   ├── Typography.kt        # Space Grotesk + Inter + JetBrains Mono
│   ├── Shape.kt             # Border radius tokens
│   └── Spacing.kt           # 4dp grid system
│
├── components/
│   ├── ChannelCard.kt       # Card de canal con chromatic glow
│   ├── EpgCell.kt           # Celda de la guía EPG
│   ├── PlayerMinibar.kt     # Mini reproductor flotante
│   ├── FocusableCard.kt     # TV-aware card con glow dinámico
│   ├── AetherButton.kt      # Botón con spring animation
│   ├── LoadingShimmer.kt    # Shimmer skeleton loader
│   ├── GlassCard.kt         # Card glassmorphism (RenderEffect blur)
│   └── LiveBadge.kt         # Badge "EN DIRECTO" pulsante
│
└── adaptive/
    ├── AdaptiveScaffold.kt  # Layout raíz Phone/TV
    └── WindowSizeClass.kt   # Breakpoints personalizados
```

**Chromatic Glow Component (TV signature):**
```kotlin
@Composable
fun ChromaticFocusCard(
    artwork: ImageBitmap?,
    isFocused: Boolean,
    content: @Composable () -> Unit
) {
    val glowColor = remember(artwork) {
        artwork?.let { Palette.from(it.asAndroidBitmap()).generate()
            .getVibrantColor(Color.Transparent.toArgb()) 
            .let { Color(it) }
        } ?: Color.Transparent
    }
    
    val glowAlpha by animateFloatAsState(
        targetValue = if (isFocused) 0.8f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
    )
    
    Box(
        modifier = Modifier
            .drawBehind {
                if (isFocused) {
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                glowColor.copy(alpha = glowAlpha * 0.4f),
                                Color.Transparent
                            ),
                            radius = size.maxDimension * 0.8f
                        )
                    )
                }
            }
    ) { content() }
}
```

**TV Typography Scale** (más grande que phone):
```kotlin
val TvTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = SpaceGrotesk,
        fontSize = 72.sp,      // vs 57.sp en phone
        fontWeight = FontWeight.Bold
    ),
    // etc.
)
```

**Prompts Claude Code:**
```
"Implementa el Design System completo en core-ui siguiendo la paleta 
Chromatic Void del plan. Incluye Space Grotesk vía downloadable fonts."

"Crea el componente ChromaticFocusCard con extracción de color de Palette API 
y spring animations para TV."
```

---

### FASE 2 — Capa de Red y Parsers
**Duración estimada: 3 días**

**Objetivos:**
- Cliente Xtream Codes API completo
- Parser M3U robusto (URL + archivo local, grupos, artwork, catchup)
- Parser XMLTV/EPG (streaming, eficiente en memoria)
- Repositorios con caché offline (Room)

**A) Xtream Codes API (data-xtream):**
```kotlin
interface XtreamApi {
    // Autenticación y datos del servidor
    @GET("{baseUrl}/player_api.php")
    suspend fun authenticate(
        @Path(encoded=true) baseUrl: String,
        @Query("username") user: String,
        @Query("password") pass: String,
        @Query("action") action: String = "get_server_info"
    ): XtreamAuthResponse

    // Canales en directo
    @GET("{baseUrl}/player_api.php")
    suspend fun getLiveStreams(
        @Query("action") action: String = "get_live_streams",
        @Query("category_id") categoryId: String? = null
    ): List<XtreamStream>

    // Categorías de directo
    @GET("{baseUrl}/player_api.php")
    suspend fun getLiveCategories(
        @Query("action") action: String = "get_live_categories"
    ): List<XtreamCategory>

    // EPG de un canal (por stream_id)
    @GET("{baseUrl}/player_api.php")
    suspend fun getEpgForStream(
        @Query("action") action: String = "get_simple_data_table",
        @Query("stream_id") streamId: Int
    ): XtreamEpgResponse

    // EPG completo (todos los canales)
    @GET("{baseUrl}/xmltv.php")
    @Streaming
    suspend fun getXmltvEpg(): ResponseBody

    // VOD
    @GET("{baseUrl}/player_api.php")
    suspend fun getVodStreams(@Query("action") action: String = "get_vod_streams"): List<XtreamVod>
    
    // Info VOD (descripción, reparto, poster)
    @GET("{baseUrl}/player_api.php")  
    suspend fun getVodInfo(
        @Query("action") action: String = "get_vod_info",
        @Query("vod_id") vodId: Int
    ): XtreamVodInfo

    // Series
    @GET("{baseUrl}/player_api.php")
    suspend fun getSeries(@Query("action") action: String = "get_series"): List<XtreamSeries>
    
    @GET("{baseUrl}/player_api.php")
    suspend fun getSeriesInfo(
        @Query("action") action: String = "get_series_info",
        @Query("series_id") seriesId: Int
    ): XtreamSeriesInfo

    // Stream URLs (construir en el repositorio, no llamar a API):
    // Live:    {baseUrl}/live/{user}/{pass}/{stream_id}.{ext}
    // VOD:     {baseUrl}/movie/{user}/{pass}/{vod_id}.{ext}
    // Series:  {baseUrl}/series/{user}/{pass}/{episode_id}.{ext}
    // Catchup: {baseUrl}/timeshift/{user}/{pass}/{duration}/{start}/{stream_id}.ts
}
```

**B) Parser M3U (data-m3u):**
```kotlin
class M3uParser {
    // Soporta:
    // - #EXTINF con tvg-id, tvg-name, tvg-logo, group-title, catchup, catchup-source
    // - #EXTVLCOPT (opciones VLC)
    // - Grupos anidados
    // - Artwork embebido en URLs
    
    suspend fun parseFromUrl(url: String): Flow<M3uChannel>
    suspend fun parseFromFile(uri: Uri): Flow<M3uChannel>
    
    private fun parseExtInf(line: String): M3uExtInf {
        // Regex para extraer todos los atributos tvg-*
        val tvgId = Regex("""tvg-id="([^"]+)"""").find(line)?.groupValues?.get(1)
        val tvgName = Regex("""tvg-name="([^"]+)"""").find(line)?.groupValues?.get(1)
        val tvgLogo = Regex("""tvg-logo="([^"]+)"""").find(line)?.groupValues?.get(1)
        val groupTitle = Regex("""group-title="([^"]+)"""").find(line)?.groupValues?.get(1)
        val catchup = Regex("""catchup="([^"]+)"""").find(line)?.groupValues?.get(1)
        val catchupSource = Regex("""catchup-source="([^"]+)"""").find(line)?.groupValues?.get(1)
        val catchupDays = Regex("""catchup-days="(\d+)"""").find(line)?.groupValues?.get(1)?.toIntOrNull()
        // ...
    }
}
```

**C) Parser XMLTV (data-epg):**
```kotlin
class XmltvParser {
    // Streaming parser — no carga todo en memoria
    // Usa XmlPullParser de Android
    // Maneja ficheros de varios GB sin OOM
    
    suspend fun parse(inputStream: InputStream): Flow<EpgProgram> = flow {
        val parser = Xml.newPullParser()
        parser.setInput(inputStream, "UTF-8")
        
        var eventType = parser.eventType
        var currentProgram: EpgProgramBuilder? = null
        
        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> when (parser.name) {
                    "programme" -> currentProgram = EpgProgramBuilder(
                        channelId = parser.getAttributeValue(null, "channel"),
                        startTime = parser.getAttributeValue(null, "start").parseXmltvDate(),
                        stopTime = parser.getAttributeValue(null, "stop").parseXmltvDate()
                    )
                    "title" -> currentProgram?.title = parser.nextText()
                    "desc" -> currentProgram?.description = parser.nextText()
                    "icon" -> currentProgram?.icon = parser.getAttributeValue(null, "src")
                    "category" -> currentProgram?.categories?.add(parser.nextText())
                }
                XmlPullParser.END_TAG -> if (parser.name == "programme") {
                    currentProgram?.build()?.let { emit(it) }
                    currentProgram = null
                }
            }
            eventType = parser.next()
        }
    }
}
```

**Room Database (core-database):**
```kotlin
@Database(
    entities = [
        ChannelEntity::class,
        EpgProgramEntity::class,
        WatchHistoryEntity::class,
        FavoriteEntity::class,
        ProviderEntity::class,
        CategoryEntity::class,
        VodEntity::class,
        SeriesEntity::class,
    ],
    version = 1,
    exportSchema = true
)
abstract class AetherDatabase : RoomDatabase() {
    abstract fun channelDao(): ChannelDao
    abstract fun epgDao(): EpgDao
    abstract fun watchHistoryDao(): WatchHistoryDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun providerDao(): ProviderDao
    abstract fun vodDao(): VodDao
    abstract fun seriesDao(): SeriesDao
}
```

**Prompts Claude Code:**
```
"Implementa el cliente Xtream Codes completo en data-xtream con Retrofit. 
Incluye todos los endpoints del plan y los data classes de respuesta."

"Implementa el M3uParser en data-m3u. Debe soportar parsing por URL 
(con OkHttp streaming) y por Uri de archivo local. Retorna Flow<M3uChannel>."

"Implementa el XmltvParser usando XmlPullParser. Debe ser streaming, 
manejar ficheros de varios GB sin OOM, y retornar Flow<EpgProgram>."
```

---

### FASE 3 — Feature Onboarding
**Duración estimada: 2 días**

**Objetivos:**
- Pantalla de bienvenida impactante (logo AETHER animado)
- Añadir proveedor Xtream Codes (URL, usuario, contraseña)
- Añadir proveedor M3U (URL o selector de archivo)
- Test de conexión con feedback visual
- Sincronización inicial con barra de progreso detallada

**Pantallas:**
```
OnboardingWelcome   → Logo animado + tagline + CTA "Comenzar"
ProviderTypeSelect  → "Xtream Codes" o "Lista M3U"
XtreamSetup         → Campos URL/user/pass + Test conexión
M3uSetup            → URL o "Seleccionar archivo" + preview
SyncProgress        → "Descargando canales… Procesando EPG… Listo"
```

**Animación logo AETHER:**
```kotlin
// Partículas que convergen hacia el logo, luego aparece el nombre
// Usando Canvas API + Compose animations
@Composable
fun AetherLogoAnimation(onComplete: () -> Unit) {
    // 1. Fade in del icono (estrella/orbe)
    // 2. Glow que se expande
    // 3. Texto "AETHER" aparece letra a letra con spring
    // 4. Tagline fade in: "Tu televisión. Sin límites."
}
```

**Prompts Claude Code:**
```
"Implementa el flujo de Onboarding completo. La pantalla de bienvenida 
debe tener el logo AETHER animado con partículas en Canvas. 
Después, formulario de Xtream/M3U con validación y test de conexión real."
```

---

### FASE 4 — Feature Home
**Duración estimada: 2 días**

**Objetivos:**
- Layout adaptativo: Phone (LazyColumn de secciones) / TV (Hero + listas horizontales)
- Sección "Continuar viendo" con tiempo restante
- Sección "Recientes" 
- Sección "Mis favoritos"
- Sección "Canales populares" (por tiempo de visionado)

**Layout TV (estilo Apple TV+):**
```
┌─────────────────────────────────────────────────────────┐
│  [AETHER]                    🔍  ⚙️                      │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  ┌──────────────────────────────────┐  ┌─────────────┐ │
│  │                                  │  │  Canal 2    │ │
│  │     CANAL PRINCIPAL (HERO)       │  │  Canal 3    │ │
│  │     Arte de fondo con glow       │  │  Canal 4    │ │
│  │     ───────────────────          │  │  Canal 5    │ │
│  │     Título del programa          │  │             │ │
│  │     20:00 - 21:30  |  EN DIRECTO │  └─────────────┘ │
│  └──────────────────────────────────┘                   │
│                                                         │
│  Continuar viendo ──────────────────────────────────►   │
│  ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐         │
│  │  📺  │ │  📺  │ │  📺  │ │  📺  │ │  📺  │         │
│  └──────┘ └──────┘ └──────┘ └──────┘ └──────┘         │
│                                                         │
│  Mis Favoritos ─────────────────────────────────────►   │
│  ┌──────┐ ┌──────┐ ┌──────┐                            │
└─────────────────────────────────────────────────────────┘
```

**Prompts Claude Code:**
```
"Implementa la pantalla Home con layout adaptativo TV/Phone. 
TV: hero card con canal destacado y lista lateral. Phone: LazyColumn con secciones.
Secciones: Continuar viendo (con progreso), Favoritos, Recientes."
```

---

### FASE 5 — Feature Live TV (Lista de Canales)
**Duración estimada: 2 días**

**Objetivos:**
- Lista de canales con grupos/categorías
- Filtros: Todos / Favoritos / Grupo
- Búsqueda incremental en la lista
- Info EPG en tiempo real junto a cada canal (Ahora/Siguiente)
- Selector de calidad de stream (si el proveedor lo ofrece)

**Layout Phone:**
```
┌────────────────────────┐
│ 🔍 Buscar canal...      │
├────────────────────────┤
│ [Todos] [Deporte] [+]  │  ← Chips de categorías
├────────────────────────┤
│ 📺 La 1     19:00-20:00│  ← Logo + nombre + EPG ahora
│             Telediario  │
├────────────────────────┤
│ 📺 La 2     ...        │
│ ▓▓▓▓▓░░░░  60%         │  ← Barra de progreso EPG
└────────────────────────┘
```

**Prompts Claude Code:**
```
"Implementa la pantalla de canales en directo. Lista con LazyColumn, 
chips de categoría, barra de búsqueda, y dato EPG 'ahora/siguiente' 
obtenido del repositorio de EPG en tiempo real con Flow."
```

---

### FASE 6 — Core Player (El módulo más complejo)
**Duración estimada: 4 días**

**Objetivos:**
- Media3 ExoPlayer con configuración optimizada para IPTV
- Custom Player UI en Compose (overlay de controles)
- **PiP** (Picture-in-Picture) completo
- **Multi-screen**: 2 o 4 streams simultáneos en split view
- **Subtítulos avanzados**: estilo, tamaño, color, posición, idioma
- **Catch-up TV**: reproducción desde URL timeshift
- Controles TV con D-pad (play/pause, avance, retroceso, siguiente canal)
- Media Session para notificación de reproducción y control externo

**A) ExoPlayer Setup IPTV optimizado:**
```kotlin
fun buildExoPlayer(context: Context): ExoPlayer {
    val trackSelector = DefaultTrackSelector(context).apply {
        setParameters(
            buildUponParameters()
                .setPreferredAudioLanguage("spa")    // Español por defecto
                .setPreferredTextLanguage("spa")
                .setMaxVideoSizeSd()                 // Seguridad: empezar en SD
        )
    }
    
    val loadControl = DefaultLoadControl.Builder()
        .setBufferDurationsMs(
            30_000,    // min buffer: 30s (IPTV necesita más que VOD)
            120_000,   // max buffer: 2min
            2_500,     // min buffer to start playback
            5_000      // min buffer to resume after rebuffer
        )
        .build()
    
    return ExoPlayer.Builder(context)
        .setTrackSelector(trackSelector)
        .setLoadControl(loadControl)
        .setMediaSourceFactory(
            DefaultMediaSourceFactory(context)
                .setDataSourceFactory(
                    DefaultHttpDataSource.Factory()
                        .setConnectTimeoutMs(10_000)
                        .setReadTimeoutMs(10_000)
                        .setAllowCrossProtocolRedirects(true)
                        .setUserAgent("Aether IPTV/1.0")
                )
        )
        .build()
}
```

**B) Picture-in-Picture:**
```kotlin
class PipManager(private val activity: Activity) {
    fun enterPip(aspectRatio: Rational = Rational(16, 9)) {
        val params = PictureInPictureParams.Builder()
            .setAspectRatio(aspectRatio)
            .setSeamlessResizeEnabled(true)
            .build()
        activity.enterPictureInPictureMode(params)
    }
    
    // En onUserLeaveHint() → activar PiP automático al salir
}
```

**C) Multi-screen (4 streams simultáneos):**
```kotlin
// Layout en cuadrícula de 2x2 con ExoPlayer independiente en cada celda
// El stream activo (con foco) se amplía ligeramente y tiene controles visibles
// Los otros 3 están en "silencio visual" (sin controles, audio silenciado)

@Composable
fun MultiScreenLayout(
    streams: List<StreamConfig>,  // 2 o 4 streams
    activeIndex: Int,
    onStreamSelect: (Int) -> Unit
) {
    val columns = if (streams.size <= 2) 2 else 2
    LazyVerticalGrid(columns = GridCells.Fixed(columns)) {
        itemsIndexed(streams) { index, stream ->
            MultiScreenCell(
                stream = stream,
                isActive = index == activeIndex,
                modifier = Modifier
                    .aspectRatio(16f / 9f)
                    .clickable { onStreamSelect(index) }
            )
        }
    }
}
```

**D) Subtítulos avanzados:**
```kotlin
data class SubtitleStyle(
    val fontSize: TextUnit = 18.sp,
    val fontWeight: FontWeight = FontWeight.Bold,
    val color: Color = Color.White,
    val backgroundColor: Color = Color.Black.copy(alpha = 0.6f),
    val outlineColor: Color = Color.Black,
    val outlineWidth: Dp = 2.dp,
    val position: SubtitlePosition = SubtitlePosition.BOTTOM_CENTER,
    val verticalOffset: Float = 0.1f   // 10% desde el borde
)

// Aplicado a CaptionStyleCompat de ExoPlayer
// + custom SubtitleView que soporta posicionamiento libre
```

**E) Controles TV con D-pad:**
```kotlin
// Mapeo de teclas del control remoto
// KEYCODE_DPAD_UP/DOWN → cambiar canal
// KEYCODE_DPAD_LEFT/RIGHT → retroceder/avanzar 10s (en catch-up)
// KEYCODE_MEDIA_PLAY_PAUSE → play/pause
// KEYCODE_BACK → miniplayer
// KEYCODE_MENU → mostrar info EPG
// KEYCODE_MEDIA_NEXT/PREVIOUS → siguiente/anterior canal
```

**Prompts Claude Code:**
```
"Implementa core-player con ExoPlayer Media3 configurado para IPTV. 
Incluye: PlayerViewModel con StateFlow, PlayerScreen en Compose con 
controles overlay que se ocultan tras 3s de inactividad."

"Implementa PiP completo con PipManager. Al pulsar Home en Android 8+ 
debe entrar en PiP automáticamente si hay reproducción activa."

"Implementa MultiScreenLayout para 2 y 4 streams simultáneos. 
ExoPlayer independiente por celda, el activo con audio, el resto silenciados."

"Implementa el sistema de subtítulos avanzado con SubtitleStyleEditor: 
selector de fuente, tamaño, color, fondo, posición. Persistir en DataStore."
```

---

### FASE 7 — Feature EPG (Guía de Programación)
**Duración estimada: 3 días**

**Objetivos:**
- Grid EPG horizontal (eje X = tiempo, eje Y = canales)
- Navegación con scroll libre en ambos ejes
- Indicador "AHORA" en tiempo real
- Tap en programa → detalle + botón "Ver ahora" / "Catch-up"
- Sincronización EPG en background (WorkManager)
- Vista compacta "Ahora y Después" en lista de canales

**Layout EPG Grid:**
```
         [◄ Ayer]   LUN 16 ENE      [Hoy ►]
         
         10:00   11:00   12:00   13:00   14:00
         ┌───┐ ▼ AHORA
La 1  ─► │ El Hormiguero ────│ Telediario │ Tarde ─ │
La 2  ─► │ Saber y Ganar ─── │     ───    │ Aquí la Tierra │
La 3  ─► │      Doc.         │  Concierto  ───────────────  │
TVE   ─► │ ...               │                              │
```

**Tecnología del EPG Grid:**
```kotlin
// No usar LazyGrid estándar — no soporta scroll bidireccional libre
// Implementación custom con:
// - Canvas para las celdas del grid (mejor performance)
// - LazyColumn para el eje vertical (canales)
// - Estado de scroll horizontal compartido entre filas (ScrollState sync)
// - StickyHeader para los logos de canales (columna izquierda fija)
// - StickyHeader para la barra de tiempo (fila superior fija)

@Composable
fun EpgGrid(
    channels: List<EpgChannel>,
    programs: Map<String, List<EpgProgram>>,
    currentTime: Long,
    onProgramClick: (EpgProgram) -> Unit
) {
    // Scroll horizontal sincronizado entre todas las filas
    val horizontalScrollState = rememberScrollState()
    // Scroll vertical para los canales
    val lazyListState = rememberLazyListState()
    // ...
}
```

**WorkManager para sync EPG:**
```kotlin
@HiltWorker
class EpgSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val epgRepository: EpgRepository
) : CoroutineWorker(context, workerParams) {
    
    override suspend fun doWork(): Result {
        return try {
            epgRepository.syncAll()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
    
    companion object {
        fun schedule(workManager: WorkManager) {
            val request = PeriodicWorkRequestBuilder<EpgSyncWorker>(6, TimeUnit.HOURS)
                .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
                .build()
            workManager.enqueueUniquePeriodicWork(
                "epg_sync",
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}
```

**Prompts Claude Code:**
```
"Implementa el EpgGrid con scroll bidireccional libre. 
Columna izquierda (logos) fija, fila superior (tiempo) fija. 
Celda actual resaltada. Indicador de 'AHORA' como línea vertical animada."

"Implementa EpgSyncWorker con WorkManager para sincronización 
periódica del EPG cada 6 horas. Con notificación de progreso."
```

---

### FASE 8 — Feature VOD y Series
**Duración estimada: 2 días**

**Objetivos:**
- Grid de películas con artwork tipo poster
- Pantalla de detalle: sinopsis, reparto, año, género, TMDB rating
- Grid de series, selección de temporada/episodio
- Reproducción con historial de progreso

**Pantalla detalle VOD (estilo Apple TV+):**
```
┌─────────────────────────────────────────────────────────┐
│  [BACKDROP del poster con blur + oscurecimiento]        │
│                                                         │
│    ┌──────┐   TÍTULO DE LA PELÍCULA                     │
│    │POSTER│   2024 · 2h 18min · Acción, Thriller        │
│    │      │   ⭐ 8.4  IMDb                               │
│    └──────┘                                             │
│              ► Reproducir                               │
│              ↻ Continuar (1h 24min restantes)           │
│                                                         │
│  Sinopsis                                               │
│  Una descripción de la película...                      │
│                                                         │
│  Reparto  ──────────────────────────────────────────►   │
│  [Foto] [Foto] [Foto]                                   │
└─────────────────────────────────────────────────────────┘
```

**Prompts Claude Code:**
```
"Implementa la pantalla de VOD con grid de posters y pantalla de detalle. 
El backdrop debe ser el poster con blur gaussiano + gradiente oscuro. 
Extraer color dominante para el tema de la pantalla."

"Implementa la navegación por series: pantalla principal → 
selección de temporada (tabs) → episodios (lista) → reproducción."
```

---

### FASE 9 — Feature Search
**Duración estimada: 1 día**

**Objetivos:**
- Búsqueda global: canales + VOD + series
- Resultados en tiempo real (Flow con debounce)
- Historial de búsquedas recientes
- TV: teclado virtual con D-pad friendly

```kotlin
// SearchViewModel
val searchResults = searchQuery
    .debounce(300)
    .filter { it.length >= 2 }
    .flatMapLatest { query ->
        combine(
            channelRepository.search(query),
            vodRepository.search(query),
            seriesRepository.search(query)
        ) { channels, vods, series ->
            SearchResults(channels, vods, series)
        }
    }
    .stateIn(viewModelScope, SharingStarted.Lazily, SearchResults.Empty)
```

---

### FASE 10 — Feature Settings
**Duración estimada: 1 día**

**Configuración completa:**
```
⚙️ Configuración
│
├── 📡 Fuentes IPTV
│   ├── Lista de proveedores (Xtream / M3U)
│   ├── Añadir proveedor
│   ├── Editar / Eliminar
│   └── Sincronizar ahora
│
├── 🎬 Reproductor
│   ├── Decodificador (Auto / Software / Hardware)
│   ├── Buffer de reproducción (15s / 30s / 60s)
│   ├── Conexión máxima multi-screen (2 / 4)
│   ├── PiP automático al salir
│   └── Continuar desde donde lo dejé
│
├── 💬 Subtítulos
│   ├── Idioma preferido
│   ├── Tamaño de fuente
│   ├── Color del texto
│   ├── Fondo
│   ├── Posición
│   └── Preview en tiempo real
│
├── 📺 Guía de Programación (EPG)
│   ├── Fuente EPG personalizada (URL XMLTV)
│   ├── Días de EPG a guardar (1 / 3 / 7)
│   └── Última sincronización: hace X horas
│
└── 🎨 Apariencia
    ├── (futuro) Tema de acento
    └── Escala de UI (Normal / Grande — para TV lejano)
```

---

### FASE 11 — Optimización TV
**Duración estimada: 2 días**

**Objetivos:**
- Focus management completo en TODAS las pantallas con D-pad
- `FocusRequester` en los primeros elementos de cada pantalla
- `onPreviewKeyEvent` para capturar teclas del mando en el player
- TV banner `320x180dp` con el logo AETHER
- Prueba en emulador Android TV API 34
- Verificar `LEANBACK_LAUNCHER` intent filter

**Checklist TV:**
```
□ Cada pantalla tiene un elemento con focus inicial automático
□ Tab order lógico en todos los formularios
□ Ningún elemento con focus queda fuera de pantalla
□ Focus visible en TODO (glow cromático)
□ Back button cierra capas correctamente (no sale de la app)
□ Player: D-pad izq/der cambia canal, arriba/abajo volumen
□ EPG: D-pad navega por el grid correctamente
□ Botones de texto son suficientemente grandes (mínimo 48dp touch target)
□ No hay texto < 18sp en modo TV
□ Banner declarado en AndroidManifest
□ Probado con emulador: Android TV API 34
```

---

### FASE 12 — Polish, Testing y Performance
**Duración estimada: 2 días**

**Performance:**
```kotlin
// Coil 3: configuración optimizada
val imageLoader = ImageLoader.Builder(context)
    .memoryCache {
        MemoryCache.Builder()
            .maxSizePercent(context, 0.20)  // 20% de RAM
            .build()
    }
    .diskCache {
        DiskCache.Builder()
            .directory(context.cacheDir.resolve("image_cache"))
            .maxSizeBytes(500 * 1024 * 1024)  // 500MB para logos
            .build()
    }
    .crossfade(true)
    .build()
```

**Loading states — Shimmer skeleton:**
```kotlin
// Todas las listas tienen shimmer mientras cargan
// No hay pantallas vacías sin feedback visual
@Composable
fun ShimmerChannelCard() {
    Box(
        modifier = Modifier
            .shimmerEffect()  // Extension con InfiniteTransition
            .size(160.dp, 90.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF1A1A2E))
    )
}
```

**Error states:**
```kotlin
// Pantalla de error elegante con:
// - Icono temático (no genérico)
// - Mensaje claro en español
// - Botón de reintento
// - Código de error técnico colapsable (para debug)
```

**Animaciones finales:**
- Shared Element Transitions entre lista y detalle (Compose 1.7+)
- Spring animations en todos los botones (pequeño rebote al pulsar)
- Parallax en el hero de Home al hacer scroll
- Crossfade en todas las imágenes

---

### FASE 13 — Build y APK
**Duración estimada: 0.5 días**

**app/build.gradle.kts — configuración de firma y build:**
```kotlin
android {
    namespace = "com.tudominio.aether"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.tudominio.aether"
        minSdk = 21        // Android 5.0 (TV requiere 21+)
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }

    signingConfigs {
        create("release") {
            storeFile = file("../aether.keystore")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = "aether"
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-DEBUG"
            isDebuggable = true
        }
    }
    
    // APK universal (no AAB para sideload)
    bundle {
        // Desactivar si quieres APK puro
    }
}
```

**Comandos finales:**
```bash
# Generar keystore (solo una vez)
keytool -genkey -v -keystore aether.keystore -alias aether \
        -keyalg RSA -keysize 2048 -validity 10000

# Build APK debug (para pruebas)
./gradlew assembleDebug

# Build APK release firmado
KEYSTORE_PASSWORD=xxx KEY_PASSWORD=xxx ./gradlew assembleRelease

# Output en: app/build/outputs/apk/release/app-release.apk
```

**ProGuard rules importantes:**
```proguard
# ExoPlayer / Media3
-keep class androidx.media3.** { *; }
# Retrofit / Kotlinx Serialization
-keepattributes *Annotation*
-keep class kotlinx.serialization.** { *; }
# Coil
-keep class coil.** { *; }
# Room
-keep class * extends androidx.room.RoomDatabase
```

---

## 3. FLUJO DE TRABAJO CON CLAUDE CODE

### Cómo usar Claude Code en cada fase

```bash
# Instalar Claude Code si no está
npm install -g @anthropic-ai/claude-code

# Lanzar en el directorio del proyecto
cd aether/
claude

# Comandos útiles dentro de Claude Code:
/init       # Generar CLAUDE.md con contexto del proyecto
/compact    # Compactar contexto en conversaciones largas
/review     # Revisar cambios antes de aplicar
```

### CLAUDE.md recomendado para el proyecto:
```markdown
# AETHER — IPTV App

## Stack
- Kotlin 2.0, Jetpack Compose, Compose for TV
- Media3/ExoPlayer, Hilt, Retrofit, Room, DataStore
- Arquitectura: Clean Architecture + MVI

## Módulos principales
- core-ui: Design System Chromatic Void
- data-xtream: Xtream Codes API client
- data-m3u: Parser de listas M3U
- data-epg: Parser XMLTV
- feature-player: Media3 + PiP + Multi-screen

## Convenciones
- Nombres en inglés (código), español (strings.xml)
- StateFlow para UI state, SharedFlow para one-time events
- Repositorios inyectados por interfaz (testabilidad)
- Device type detection: DeviceType enum en core-common

## TV
- Cada pantalla tiene FocusRequester en primer elemento
- ChromaticFocusCard para todos los items focusables en TV
- Mínimo 48dp en touch targets, mínimo 18sp en texto TV
```

---

## 4. ESTIMACIÓN TOTAL

| Fase | Módulo/Feature | Días |
|------|---------------|------|
| 0 | Setup + estructura | 1 |
| 1 | Design System | 2 |
| 2 | Red + Parsers | 3 |
| 3 | Onboarding | 2 |
| 4 | Home | 2 |
| 5 | Live TV lista | 2 |
| 6 | Core Player (PiP, Multi, Subs) | 4 |
| 7 | EPG Grid | 3 |
| 8 | VOD + Series | 2 |
| 9 | Search | 1 |
| 10 | Settings | 1 |
| 11 | TV Optimization | 2 |
| 12 | Polish + Performance | 2 |
| 13 | Build + APK | 0.5 |
| **Total** | | **~28 días** |

*(A ritmo de sesiones de Claude Code de 1-2h diarias. A full-time serían ~2 semanas.)*

---

## 5. ORDEN DE IMPLEMENTACIÓN RECOMENDADO CON CLAUDE CODE

Para obtener valor funcional cuanto antes (y poder probar en dispositivo real):

```
Semana 1: Setup → Design System → Parsers → Onboarding
           → Ya puedes cargar tu lista IPTV

Semana 2: Home → Live TV → Player básico (sin PiP ni multi)
           → Ya puedes ver canales en directo

Semana 3: EPG → Catch-up → PiP → Multi-screen
           → Funcionalidades premium

Semana 4: VOD → Series → Search → Settings → TV optimization → APK
           → App completa y compilada
```

---

## 6. FEATURES FUTURAS (POST-1.0)

- **Chromecast/Google Cast**: enviar stream al TV desde el teléfono
- **Favoritos sincronizados** via Firebase (si quieres multi-dispositivo)
- **Grabación local** (requiere permisos de almacenamiento + HLS recorder)
- **Notificaciones de inicio de programa** (EPG + AlarmManager)
- **Widget de Android** con "en directo ahora" en los favoritos
- **Modo coche** (Android Auto — limitado pero posible para audio)
