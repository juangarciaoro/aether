# AETHER — IPTV App

## Stack
- Kotlin 2.0, Jetpack Compose 1.7, Compose for TV 1.0-alpha
- Media3/ExoPlayer 1.4, Hilt 2.52, Retrofit 2.11, Room 2.6, DataStore 1.1
- Arquitectura: Clean Architecture + MVI (StateFlow, SharedFlow)
- Build: Gradle KTS + Version Catalog (libs.versions.toml) + Convention Plugins

## Módulos
- `core/core-common` — DeviceType enum, AetherResult, Dispatchers DI
- `core/core-ui` — Design System Chromatic Void: Color, Typography, Theme, Components
- `core/core-network` — OkHttp + Retrofit base, NetworkModule Hilt
- `core/core-database` — Room: todas las entidades y DAOs
- `core/core-datastore` — DataStore Preferences: UserPreferencesRepository
- `core/core-player` — ExoPlayer builder, PipManager, SubtitleStyle
- `data/data-xtream` — Xtream Codes API client + XtreamRepository
- `data/data-m3u` — M3uParser (URL + InputStream → Flow<M3uChannel>)
- `data/data-epg` — XmltvParser (streaming), EpgRepository, EpgSyncWorker
- `feature/feature-onboarding` — Welcome, ProviderSetup screens
- `feature/feature-home` — HomeScreen Phone+TV, HomeViewModel
- `feature/feature-live` — Lista canales con EPG en tiempo real, LiveViewModel
- `feature/feature-epg` — EpgGrid con scroll bidireccional, EpgViewModel
- `feature/feature-vod` — Grid de películas, VodViewModel
- `feature/feature-series` — Grid de series, SeriesViewModel
- `feature/feature-player` — PlayerScreen + PlayerControls + PlayerViewModel
- `feature/feature-search` — Búsqueda global con debounce, SearchViewModel
- `feature/feature-settings` — SettingsScreen + SettingsViewModel

## Convenciones
- Código y nombres en inglés; strings de UI en español (strings.xml)
- StateFlow para UI state, SharedFlow para one-time events
- Repositorios inyectados por interfaz para testabilidad
- DeviceType detection: `Context.getDeviceType()` en core-common
- Paleta: Void(#050508), DeepSpace(#0D0D1A), NeonIndigo(#7C6CFF), Plasma(#FF6B9D)

## TV
- Cada pantalla: FocusRequester en primer elemento
- ChromaticFocusCard para todos los items focusables en TV
- Mínimo 48dp touch targets, mínimo 18sp texto en TV (TvTypography escala automática)
- D-pad: up/down → cambiar canal, left/right → seek 10s en VOD/catchup
- Banner TV: res/drawable/aether_tv_banner.xml (reemplazar con PNG real 320x180)

## Fuentes (pendiente de añadir)
Los .ttf deben ir en `core/core-ui/src/main/res/font/`:
- space_grotesk_light.ttf, space_grotesk_regular.ttf, space_grotesk_medium.ttf, space_grotesk_semibold.ttf, space_grotesk_bold.ttf
- inter_regular.ttf, inter_medium.ttf, inter_semibold.ttf, inter_bold.ttf
- jetbrains_mono_regular.ttf, jetbrains_mono_medium.ttf
Descargar de Google Fonts antes de compilar.

## Próximos pasos (por orden)
1. Añadir fuentes TTF a core-ui/res/font/
2. Crear launcher icon en mipmap/ (adaptative icon)
3. Completar navegación en AetherNavHost
4. Implementar OnboardingViewModel con test de conexión real
5. Implementar ChromaticFocusCard en ChannelCard y FocusableCard TV
6. Implementar MultiScreenLayout en feature-player
7. Implementar EpgSyncWorker en WorkManager al iniciar la app
8. Añadir Shared Element Transitions entre lista y detalle
