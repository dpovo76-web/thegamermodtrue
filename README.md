# Gamer System (mod para NeoForge)

Mod de **sistema RPG** para Minecraft inspirado en el género "gamer": nivel, EXP, stats, MP, habilidades,
zonas protegidas / de entrenamiento y una mazmorra por pisos que usa **mobs vanilla**.

> Proyecto de aficionado, sin afiliación con ninguna obra. No incluye texturas, textos ni imágenes de terceros:
> todos los assets son originales o placeholders.

- **Minecraft:** 26.2 · **Loader:** NeoForge · **Java:** 25

## Estado actual (v0.1.0)

| Función | Estado |
|---|---|
| Habilidad "The Gamer" al entrar al mundo por primera vez | ✅ |
| Nivel, EXP, puntos de stat y MP (se guardan y se conservan al morir) | ✅ |
| Stats que modifican atributos reales (VIT→vida, STR→daño, DEX→velocidad, LUK→suerte, INT/WIS→MP) | ✅ |
| `/gamer status`, `/gamer add <stat> <n>`, `/gamer floors` | ✅ |
| Funciones de lo vanilla (ver abajo) | ✅ base |
| **Núcleo de Zona**: zona protegida (sin spawn natural de hostiles, regeneración HP/MP) | ✅ |
| Zona de entrenamiento: aparecen enemigos de **un tipo** elegido | ✅ |
| Pisos de mazmorra definidos en config con mobs vanilla | ✅ (datos) |
| Dimensión/estructura de la mazmorra, jefes por piso | ⏳ siguiente fase |
| Ventana de estado gráfica con tecla | ⏳ |
| Habilidades con nivel propio y libros de habilidad | ⏳ |
| Rangos (inmortales, tops mundiales...) y misiones | ⏳ pendiente de la wiki |
| Diálogos de NPC con IA local | ⏳ |

### Qué hace cada cosa vanilla

- **Matar cualquier mob** → EXP según su vida máxima.
- **Picar/romper bloques** → EXP según su dureza.
- **Craftear, fundir, pescar** → EXP.
- **Comer comida vanilla** → restaura MP según su nutrición (+1 EXP).
- **Beber pociones vanilla** → restaura MP.
- **Correr** → EXP ("ejercicio").

### Núcleo de Zona

Craftea/obtén el bloque `gamersystem:zone_core` (pestaña de bloques funcionales).
- **Clic derecho:** alterna *Protegida* ↔ *Entrenamiento*.
- **Shift + clic derecho:** cambia el tipo de enemigo de entrenamiento.
- Los enemigos de entrenamiento dan la mitad de EXP y no sueltan drops.

### Configuración (`config/gamersystem-common.toml`)

`zoneRadius`, `maxTrainingMobs`, `trainingMobs` (lista de IDs) y `dungeonFloors` (`piso=mob1,mob2`).
Admite mobs vanilla y de otros mods.

## Compilar y probar

1. Instala **JDK 25** (por ejemplo Temurin 25).
2. Forma más fácil: abre la carpeta en **IntelliJ IDEA** (importa Gradle solo y crea el wrapper).
   Alternativa por consola: instala Gradle 9.1+ y usa `gradle runClient` / `gradle build`.
3. Cliente de pruebas: `runClient` (tarea de Gradle).
4. Jar final: `build` → `build/libs/`.
5. Revisa en `gradle.properties` la versión exacta de NeoForge (`neo_version`).

### Si falla la compilación

Este proyecto se escribió contra la API de NeoForge 26.x sin poder compilarlo en el momento. Los puntos más
probables a ajustar son: el constructor de `BlockEntityType` (`ModBlockEntities`), la lectura de etiquetas
de entidad (`ZoneTags`), `Level#isClientSide()` y las firmas de `saveAdditional/loadAdditional`.
Abre un *issue* con el error del compilador y se corrige.

## Compilar con GitHub (sin instalar nada en tu PC)

Al subir el proyecto, GitHub Actions hace todo automáticamente:

1. **Compila** el mod en cada `push` (pestaña **Actions**). También puedes lanzarlo a mano con *Run workflow*.
2. Si **sale bien**, descarga el `.jar` desde *Artifacts* → `gamersystem-jar`.
3. Si **falla**, abre la ejecución: arriba aparece un resumen con los errores del compilador
   (y el log completo en el artifact `build-log`). Cópialo y pásalo para corregirlo.
4. Para publicar una versión: `git tag v0.1.0 && git push --tags` crea una *Release* con el `.jar` adjunto.
5. Opcional: *Actions → Generar Gradle wrapper → Run workflow* añade `gradlew` al repositorio.

## Subir a GitHub

```bash
git init
git add .
git commit -m "Gamer System: base del mod (v0.1.0)"
git branch -M main
git remote add origin https://github.com/TU_USUARIO/gamersystem.git
git push -u origin main
```

Antes de publicar: cambia `TuNombre` en `LICENSE` y `gradle.properties`, y ajusta `mod_group_id` si quieres
otro paquete. Mientras no uses nombres ni arte de ninguna obra protegida, el repositorio puede ser público;
si añades contenido basado en una obra concreta, es más prudente mantenerlo **privado**.

## Licencia

Código bajo licencia MIT (ver `LICENSE`).
