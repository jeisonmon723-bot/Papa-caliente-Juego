# Bitácora de prompts --- Equipo EL IMT

## Información general

-   **Equipo:** EL IMT
-   **Proyecto:** Videojuego 2D de ratas en un restaurante, inspirado en
    la temática de «Papa caliente».
-   **Tecnologías indicadas en los prompts:** Java 17, JavaFX y Maven
    Wrapper.
-   **Propósito de esta bitácora:** registrar los prompts utilizados
    durante la Game Jam, sus objetivos, el contexto del proyecto, los
    archivos relevantes, las restricciones y los criterios de
    terminación.
-   **Integrantes registrados:** A00428746, A00434440, A00426475,
    A00429231 y A00425695.

> **Nota:** Esta bitácora organiza la información del documento fuente.
> Los criterios de terminación describen lo solicitado en cada prompt y
> no constituyen, por sí solos, una verificación independiente de que
> cada tarea haya quedado implementada.

## Registro de prompts

### 1. Corrección de compilación y configuración del entorno

-   **ID del equipo/tarea:** `CORRECCION_COMPILACION_01`
-   **Participantes:** A00428746, A00434440, A00426475, A00429231,
    A00425695 OBJETIVO: Actuar como desarrollador experto en JavaFX y
    optimizacion de videojuegos 2D para solucionar los errores de
    compilacion del proyecto y estructurar la configuracion del entorno
    para que todo el equipo pueda ejecutar el juego sin inconvenientes.
    ESTADO_ACTUAL: El proyecto esta construido sobre Java 17 y JavaFX
    utilizando Maven Wrapper (pom.xml, mvnw, mvnw.cmd), pero presenta
    fallas al intentar compilar. ARCHIVOS_RELEVANTES: pom.xml, mvnw,
    mvnw.cmd, codigo dentro de src/. RESTRICCIONES: Java 17, JavaFX, uso
    de Maven Wrapper, arquitectura 2D y cumplimiento de las reglas de
    integridad del evento. CRITERIO_DE_TERMINADO: El proyecto compila y
    se ejecuta correctamente mediante .`\mvnw`{=tex}.cmd javafx:run o
    ./mvnw javafx:run, dejando el entorno listo para el resto de los
    desarrolladores. Ayudame a implementar esto con el menor cambio
    razonable. Explica cualquier supuesto importante y entrega el
    AUDIT_SUMMARY al final. Aclaracion tecnica sobre el entorno: Dado
    que este proyecto esta desarrollado en Java 17 y JavaFX, el
    ecosistema Java no utiliza entornos virtuales .venv, los cuales son
    exclusivos de Python. En Java, la funcionalidad equivalente la
    provee Maven Wrapper (mvnw y mvnw.cmd), el cual se encarga de
    descargar e independizar la version de Maven y las librerias
    necesarias automaticamente para todos los integrantes.

**Objetivo**

Actuar como desarrollador experto en JavaFX y optimizacion de
videojuegos 2D para solucionar los errores de compilacion del proyecto y
estructurar la configuracion del entorno para que todo el equipo pueda
ejecutar el juego sin inconvenientes. ESTADO_ACTUAL: El proyecto esta
construido sobre Java 17 y JavaFX utilizando Maven Wrapper (pom.xml,
mvnw, mvnw.cmd), pero presenta fallas al intentar compilar.
ARCHIVOS_RELEVANTES: pom.xml, mvnw, mvnw.cmd, codigo dentro de src/.
RESTRICCIONES: Java 17, JavaFX, uso de Maven Wrapper, arquitectura 2D y
cumplimiento de las reglas de integridad del evento.
CRITERIO_DE_TERMINADO: El proyecto compila y se ejecuta correctamente
mediante .`\mvnw`{=tex}.cmd javafx:run o ./mvnw javafx:run, dejando el
entorno listo para el resto de los desarrolladores. Ayudame a
implementar esto con el menor cambio razonable. Explica cualquier
supuesto importante y entrega el AUDIT_SUMMARY al final. Aclaracion
tecnica sobre el entorno: Dado que este proyecto esta desarrollado en
Java 17 y JavaFX, el ecosistema Java no utiliza entornos virtuales
.venv, los cuales son exclusivos de Python. En Java, la funcionalidad
equivalente la provee Maven Wrapper (mvnw y mvnw.cmd), el cual se
encarga de descargar e independizar la version de Maven y las librerias
necesarias automaticamente para todos los integrantes.

**Estado inicial**

El proyecto esta construido sobre Java 17 y JavaFX utilizando Maven
Wrapper (pom.xml, mvnw, mvnw.cmd), pero presenta fallas al intentar
compilar. ARCHIVOS_RELEVANTES: pom.xml, mvnw, mvnw.cmd, codigo dentro de
src/. RESTRICCIONES: Java 17, JavaFX, uso de Maven Wrapper, arquitectura
2D y cumplimiento de las reglas de integridad del evento.
CRITERIO_DE_TERMINADO: El proyecto compila y se ejecuta correctamente
mediante .`\mvnw`{=tex}.cmd javafx:run o ./mvnw javafx:run, dejando el
entorno listo para el resto de los desarrolladores. Ayudame a
implementar esto con el menor cambio razonable. Explica cualquier
supuesto importante y entrega el AUDIT_SUMMARY al final. Aclaracion
tecnica sobre el entorno: Dado que este proyecto esta desarrollado en
Java 17 y JavaFX, el ecosistema Java no utiliza entornos virtuales
.venv, los cuales son exclusivos de Python. En Java, la funcionalidad
equivalente la provee Maven Wrapper (mvnw y mvnw.cmd), el cual se
encarga de descargar e independizar la version de Maven y las librerias
necesarias automaticamente para todos los integrantes.

**Archivos relevantes**

pom.xml, mvnw, mvnw.cmd, codigo dentro de src/. RESTRICCIONES: Java 17,
JavaFX, uso de Maven Wrapper, arquitectura 2D y cumplimiento de las
reglas de integridad del evento. CRITERIO_DE_TERMINADO: El proyecto
compila y se ejecuta correctamente mediante .`\mvnw`{=tex}.cmd
javafx:run o ./mvnw javafx:run, dejando el entorno listo para el resto
de los desarrolladores. Ayudame a implementar esto con el menor cambio
razonable. Explica cualquier supuesto importante y entrega el
AUDIT_SUMMARY al final. Aclaracion tecnica sobre el entorno: Dado que
este proyecto esta desarrollado en Java 17 y JavaFX, el ecosistema Java
no utiliza entornos virtuales .venv, los cuales son exclusivos de
Python. En Java, la funcionalidad equivalente la provee Maven Wrapper
(mvnw y mvnw.cmd), el cual se encarga de descargar e independizar la
version de Maven y las librerias necesarias automaticamente para todos
los integrantes.

**Restricciones**

Java 17, JavaFX, uso de Maven Wrapper, arquitectura 2D y cumplimiento de
las reglas de integridad del evento.

**Criterio de terminación**

El proyecto compila y se ejecuta correctamente mediante
.`\mvnw`{=tex}.cmd javafx:run o ./mvnw javafx:run, dejando el entorno
listo para el resto de los desarrolladores. Ayudame a implementar esto
con el menor cambio razonable. Explica cualquier supuesto importante y
entrega el AUDIT_SUMMARY al final. Aclaracion tecnica sobre el entorno:
Dado que este proyecto esta desarrollado en Java 17 y JavaFX, el
ecosistema Java no utiliza entornos virtuales .venv, los cuales son
exclusivos de Python. En Java, la funcionalidad equivalente la provee
Maven Wrapper (mvnw y mvnw.cmd), el cual se encarga de descargar e
independizar la version de Maven y las librerias necesarias
automaticamente para todos los integrantes.

**Instrucción general del prompt**

> Ayudame a implementar esto con el menor cambio razonable. Explica
> cualquier supuesto importante y entrega el AUDIT_SUMMARY al final.

### 2. Rediseño de iluminación e interfaz

-   **ID del equipo/tarea:** `REDISENO_ILUMINACION_UI_02`
-   **Participantes:** A00428746, A00434440, A00426475, A00429231,
    A00425695 OBJETIVO: Redisenar la ambientacion visual del videojuego
    para lograr maxima claridad, iluminacion alta y gran detalle en un
    restaurante tradicional, aplicando mejoras en iluminacion, mecanicas
    de juego, fisicas y diseno de interfaz (UI). ESTADO_ACTUAL: El
    prototipo 2D top-down opera sobre Java 17 y JavaFX en un mapa de
    restaurante de 1920x1080 con renderizado SVG, HUD basico, fisicas de
    colision y mecanicas activas de busqueda, trampas y recoleccion de
    comida. ARCHIVOS_RELEVANTES:
    src/main/resources/com/tuempresa/ratonesgame/assets/gameplay/,
    src/main/resources/com/tuempresa/ratonesgame/sprites/,
    GameEngine.java, RestaurantMap.java, MainApp.java. RESTRICCIONES:
    Java 17, JavaFX, uso de Maven Wrapper, arquitectura 2D y
    mantenimiento del rendimiento general del motor.
    CRITERIO_DE_TERMINADO: La interfaz (UI) luce completamente
    redisenada e integrada, el escenario de restaurante tradicional
    presenta una iluminacion alta con maxima claridad visual, y las
    fisicas junto a las mecanicas funcionan correctamente en la partida.
    Ayudame a implementar esto con el menor cambio razonable. Explica
    cualquier supuesto importante y entrega el AUDIT_SUMMARY al final.

**Objetivo**

Redisenar la ambientacion visual del videojuego para lograr maxima
claridad, iluminacion alta y gran detalle en un restaurante tradicional,
aplicando mejoras en iluminacion, mecanicas de juego, fisicas y diseno
de interfaz (UI).

**Estado inicial**

El prototipo 2D top-down opera sobre Java 17 y JavaFX en un mapa de
restaurante de 1920x1080 con renderizado SVG, HUD basico, fisicas de
colision y mecanicas activas de busqueda, trampas y recoleccion de
comida.

**Archivos relevantes**

src/main/resources/com/tuempresa/ratonesgame/assets/gameplay/,
src/main/resources/com/tuempresa/ratonesgame/sprites/, GameEngine.java,
RestaurantMap.java, MainApp.java. RESTRICCIONES: Java 17, JavaFX, uso de
Maven Wrapper, arquitectura 2D y mantenimiento del rendimiento general
del motor. CRITERIO_DE_TERMINADO: La interfaz (UI) luce completamente
redisenada e integrada, el escenario de restaurante tradicional presenta
una iluminacion alta con maxima claridad visual, y las fisicas junto a
las mecanicas funcionan correctamente en la partida. Ayudame a
implementar esto con el menor cambio razonable. Explica cualquier
supuesto importante y entrega el AUDIT_SUMMARY al final.

**Restricciones**

Java 17, JavaFX, uso de Maven Wrapper, arquitectura 2D y mantenimiento
del rendimiento general del motor. CRITERIO_DE_TERMINADO: La interfaz
(UI) luce completamente redisenada e integrada, el escenario de
restaurante tradicional presenta una iluminacion alta con maxima
claridad visual, y las fisicas junto a las mecanicas funcionan
correctamente en la partida. Ayudame a implementar esto con el menor
cambio razonable. Explica cualquier supuesto importante y entrega el
AUDIT_SUMMARY al final.

**Criterio de terminación**

La interfaz (UI) luce completamente redisenada e integrada, el escenario
de restaurante tradicional presenta una iluminacion alta con maxima
claridad visual, y las fisicas junto a las mecanicas funcionan
correctamente en la partida. Ayudame a implementar esto con el menor
cambio razonable. Explica cualquier supuesto importante y entrega el
AUDIT_SUMMARY al final.

**Instrucción general del prompt**

> Ayudame a implementar esto con el menor cambio razonable. Explica
> cualquier supuesto importante y entrega el AUDIT_SUMMARY al final.

### 3. Desarrollo integral del juego inspirado en Papa Caliente

-   **ID del equipo/tarea:** `DESARROLLO_JUEGO_PAPA_CALIENTE_03`
-   **Participantes:** A00428746, A00434440, A00426475, A00429231,
    A00425695

**Objetivo**

Desarrollar integralmente el videojuego basado en la tematica de papa
caliente, implementando dos ratones jugables que roban alimentos en un
restaurante para llenar sus madrigueras, mientras un chef los caza
constantemente, pudiendo eliminarlos o vaciar la comida recolectada,
abarcando sprites, mecanicas, ambientacion y todas las fases del juego.

**Estado inicial**

El proyecto cuenta con un prototipo 2D top-down en Java 17 y JavaFX que
incluye renderizado SVG, mapa de restaurante de 1920x1080, IA del chef
con estados de patrulla y persecucion, sistema de vision, logica de
madrigueras, recoleccion de comida y manejo de vidas.

**Archivos relevantes**

src/main/resources/com/tuempresa/ratonesgame/assets/gameplay/,
src/main/resources/com/tuempresa/ratonesgame/sprites/, GameEngine.java,
ChefAI.java, VisionSystem.java, RestaurantMap.java, GameManager.java,
MainApp.java.

**Restricciones**

Java 17, JavaFX, uso de Maven Wrapper, arquitectura 2D top-down, limite
de tiempo de 24 horas y cumplimiento de las reglas de integridad del
evento.

**Criterio de terminación**

El videojuego se encuentra funcional en todas sus fases, con sprites y
animaciones integradas, mecanicas de sigilo y recoleccion de comida, IA
del chef con deteccion y vaciado de madrigueras, ambientacion completa
del restaurante y flujo continuo de juego con condiciones de victoria y
derrota. Ayudame a implementar esto con el menor cambio razonable.
Explica cualquier supuesto importante y entrega el AUDIT_SUMMARY al
final.

**Instrucción general del prompt**

> Ayudame a implementar esto con el menor cambio razonable. Explica
> cualquier supuesto importante y entrega el AUDIT_SUMMARY al final.

### 4. Organización del equipo y distribución de roles

-   **ID del equipo/tarea:** `ORGANIZACION_EQUIPO_ROLES_04`
-   **Participantes:** A00428746, A00434440, A00426475, A00429231,
    A00425695

**Objetivo**

Definir la estructura metodologica paso a paso, la distribucion de roles
entre los cinco integrantes del equipo y las necesidades de coordinacion
para desarrollar el videojuego de manera organizada durante el evento.

**Estado inicial**

El equipo de cinco personas dispone de un prototipo de videojuego 2D
top-down desarrollado en Java 17 y JavaFX en el marco de una Game Jam de
24 horas.

**Archivos relevantes**

README.md, AI_TRACE.md, pom.xml, src/.

**Restricciones**

Tiempo limite de 24 horas, equipo compuesto por cinco participantes, uso
de Java 17 y JavaFX con Maven Wrapper, y cumplimiento obligatorio de las
reglas de trazabilidad del evento.

**Criterio de terminación**

Entregar la ruta de trabajo paso a paso, la asignacion clara de
responsabilidades para los cinco roles y la lista de necesidades
operativas y de integracion del equipo. Ayudame a implementar esto con
el menor cambio razonable. Explica cualquier supuesto importante y
entrega el AUDIT_SUMMARY al final.

**Instrucción general del prompt**

> Ayudame a implementar esto con el menor cambio razonable. Explica
> cualquier supuesto importante y entrega el AUDIT_SUMMARY al final.

### 5. Verificación de compilación y preservación del mapa

-   **ID del equipo/tarea:** `VERIFICACION_COMPILACION_MAPA_05`
-   **Participantes:** A00428746, A00434440, A00426475, A00429231,
    A00425695

**Objetivo**

Confirmar la compilacion exitosa del proyecto en Maven y asegurar la
correcta preservacion e integracion del cambio local realizado en el
mapa.

**Estado inicial**

Se reviso el proyecto Maven, se confirmo que compila correctamente y se
identifico una modificacion local en la estructura del mapa que fue
conservada.

**Archivos relevantes**

pom.xml, mvnw, mvnw.cmd, RestaurantMap.java, src/.

**Restricciones**

Java 17, JavaFX, ejecucion mediante Maven Wrapper y respeto a la
integridad de los cambios locales previos.

**Criterio de terminación**

El proyecto compila sin errores, preserva la modificacion local del mapa
y queda listo para la ejecucion del juego. Ayudame a implementar esto
con el menor cambio razonable. Explica cualquier supuesto importante y
entrega el AUDIT_SUMMARY al final.

**Instrucción general del prompt**

> Ayudame a implementar esto con el menor cambio razonable. Explica
> cualquier supuesto importante y entrega el AUDIT_SUMMARY al final.

### 6. Corrección del comando de ejecución

-   **ID del equipo/tarea:** `CORRECCION_COMANDO_EJECUCION_06`
-   **Participantes:** A00428746, A00434440, A00426475, A00429231,
    A00425695

**Objetivo**

Corregir la sintaxis del comando de ejecucion de Maven para JavaFX de
mvn run:jfx a la sintaxis valida del plugin javafx-maven-plugin (mvn
javafx:run o .`\mvnw`{=tex}.cmd javafx:run).

**Estado inicial**

Se intentaba ejecutar el videojuego utilizando un comando incorrecto
(mvn run:jfx), lo que impide la compilacion y arranque de la aplicacion
JavaFX.

**Archivos relevantes**

pom.xml, mvnw, mvnw.cmd, README.md.

**Restricciones**

Java 17, JavaFX, ejecucion mediante Maven Wrapper y cumplimiento de la
estructura oficial de construccion del proyecto.

**Criterio de terminación**

El videojuego compila e inicia la interfaz grafica correctamente al
ejecutar .`\mvnw`{=tex}.cmd javafx:run en Windows o ./mvnw javafx:run en
macOS/Linux. Ayudame a implementar esto con el menor cambio razonable.
Explica cualquier supuesto importante y entrega el AUDIT_SUMMARY al
final.

**Instrucción general del prompt**

> Ayudame a implementar esto con el menor cambio razonable. Explica
> cualquier supuesto importante y entrega el AUDIT_SUMMARY al final.

### 7. Corrección del lobby y carga de sprites SVG

-   **ID del equipo/tarea:** `CORRECCION_LOBBY_SPRITES_SVG_07`
-   **Participantes:** A00428746, A00434440, A00426475, A00429231,
    A00425695

**Objetivo**

Corregir el fallo por el cual el videojuego se queda bloqueado en la
pantalla del lobby sin pasar a la partida, solucionando los problemas
presentes en la carga y rasterizacion de los sprites SVG.

**Estado inicial**

El juego permanece congelado en el lobby al intentar iniciar la partida
debido a excepciones o fallas durante el procesamiento en memoria de las
imagenes SVG mediante la libreria Batik en el ResourceManager.

**Archivos relevantes**

src/main/resources/com/tuempresa/ratonesgame/sprites/,
src/main/resources/com/tuempresa/ratonesgame/assets/gameplay/,
ResourceManager.java, MainApp.java.

**Restricciones**

Java 17, JavaFX, uso de Batik para rasterizacion de SVG y mantenimiento
del flujo correcto de navegacion entre pantallas.

**Criterio de terminación**

La transicion desde el lobby hacia la partida se realiza de manera
correcta, cargando todos los sprites SVG rasterizados sin errores y
dejando la pantalla de juego completamente operativa. Ayudame a
implementar esto con el menor cambio razonable. Explica cualquier
supuesto importante y entrega el AUDIT_SUMMARY al final.

**Instrucción general del prompt**

> Ayudame a implementar esto con el menor cambio razonable. Explica
> cualquier supuesto importante y entrega el AUDIT_SUMMARY al final.

### 8. Controles y modos de juego

-   **ID del equipo/tarea:** `CONTROLES_MODOS_JUEGO_08`
-   **Participantes:** A00428746, A00434440, A00426475, A00429231,
    A00425695

**Objetivo**

Configurar los modos de juego e implementar la logica de control de
personajes, permitiendo que el primer jugador controle al raton mediante
teclas WASD en ambos modos, mientras que el cocinero sea controlado
automaticamente por IA en modo un jugador o por el segundo jugador
mediante las flechas de direccion en modo versus.

**Estado inicial**

El prototipo en Java 17 y JavaFX permite la seleccion de modo (1P /
Versus 2P) en la pantalla de inicio, gestionando el movimiento del raton
con WASD y alternando entre la rutina de ChefAI para el modo solitario y
la lectura de flechas de direccion para el control manual del cocinero
en el modo versus.

**Archivos relevantes**

GameEngine.java, ChefAI.java, MainApp.java, ChefConfig.java,
src/main/resources/com/tuempresa/ratonesgame/assets/gameplay/.

**Restricciones**

Java 17, JavaFX, ejecucion mediante Maven Wrapper y mantenimiento del
procesamiento simultaneo de eventos de teclado.

**Criterio de terminación**

En modo 1P el raton responde a WASD y la IA maneja al cocinero; en modo
2P el raton responde a WASD y el cocinero a las flechas de direccion,
permitiendo partidas versus fluidas. Ayudame a implementar esto con el
menor cambio razonable. Explica cualquier supuesto importante y entrega
el AUDIT_SUMMARY al final.

**Instrucción general del prompt**

> Ayudame a implementar esto con el menor cambio razonable. Explica
> cualquier supuesto importante y entrega el AUDIT_SUMMARY al final.

### 9. Integración de sprites e interfaz

-   **ID del equipo/tarea:** `INTEGRACION_SPRITES_INTERFAZ_09`
-   **Participantes:** A00428746, A00434440, A00426475, A00429231,
    A00425695

**Objetivo**

Incorporar e integrar todos los nuevos sprites en la interfaz del
videojuego, abarcando personajes, comida, utensilios, muebles y
elementos de la cocina, asegurando que todos los recursos graficos
queden correctamente empaquetados e incluidos durante la compilacion.

**Estado inicial**

Se cuenta con las hojas de sprites y recursos visuales ubicados en el
directorio de recursos, requeridos para reemplazar o complementar los
elementos de la interfaz y del escenario en JavaFX.

**Archivos relevantes**

src/main/resources/com/tuempresa/ratonesgame/sprites/,
src/main/resources/com/tuempresa/ratonesgame/assets/gameplay/,
ResourceManager.java, pom.xml, MainApp.java.

**Restricciones**

Java 17, JavaFX, rasterizacion de SVG mediante Batik, uso de Maven
Wrapper y empaquetado correcto de recursos en el classpath.

**Criterio de terminación**

Todos los sprites de personajes, comida, utensilios, muebles y cocina se
renderizan adecuadamente en la interfaz del juego y el proyecto compila
sin errores de carga de recursos. Ayudame a implementar esto con el
menor cambio razonable. Explica cualquier supuesto importante y entrega
el AUDIT_SUMMARY al final.

**Instrucción general del prompt**

> Ayudame a implementar esto con el menor cambio razonable. Explica
> cualquier supuesto importante y entrega el AUDIT_SUMMARY al final.

### 10. Creación de la rama game-master

-   **ID del equipo/tarea:** `CREACION_RAMA_GAME_MASTER_10`
-   **Participantes:** A00428746, A00434440, A00426475, A00429231,
    A00425695

**Objetivo**

Crear la nueva rama game-master partiendo del estado actual de la rama
develop, asegurando la conservacion de los cambios locales pendientes y
manteniendo la correcta trazabilidad del repositorio.

**Estado inicial**

El proyecto se encuentra en la rama develop con modificaciones locales
pendientes que fueron conservadas al momento de generar la rama
game-master.

**Archivos relevantes**

.git/, README.md, AI_TRACE.md, estructura general del proyecto.

**Restricciones**

Preservar la integridad del historial Git, no alterar commits o
timestamps anteriores, mantener los cambios locales intactos y cumplir
con las reglas del evento.

**Criterio de terminación**

La rama game-master queda creada y posicionada correctamente desde
develop, conservando los cambios locales pendientes sin perdida de
informacion. Ayudame a implementar esto con el menor cambio razonable.
Explica cualquier supuesto importante y entrega el AUDIT_SUMMARY al
final.

**Instrucción general del prompt**

> Ayudame a implementar esto con el menor cambio razonable. Explica
> cualquier supuesto importante y entrega el AUDIT_SUMMARY al final.

### 11. Opciones de menú y comida quemada

-   **ID del equipo/tarea:** `OPCIONES_MENU_COMIDA_QUEMADA_11`
-   **Participantes:** A00428746, A00434440, A00426475, A00429231,
    A00425695

**Objetivo**

Agregar las opciones de navegacion para volver a jugar (reiniciar en el
mismo modo) y volver al menu principal, e implementar la mecanica donde
la comida preparada causa daño al jugador tras sostenerla por mas de
cinco segundos.

**Estado inicial**

El videojuego en Java 17 y JavaFX cuenta con la logica de gameplay y
platos de comida, pero requiere las pantallas de reinicio y retorno al
lobby, asi como la activacion del temporizador de 5 segundos de gracia
antes del daño por calor al llevar comida caliente.

**Archivos relevantes**

GameEngine.java, GameManager.java, MainApp.java, HUD.java,
src/main/resources/com/tuempresa/ratonesgame/assets/gameplay/.

**Restricciones**

Java 17, JavaFX, ejecucion mediante Maven Wrapper, mantenimiento del
flujo de navegacion entre menus y sincroniacion de temporizadores.

**Criterio de terminación**

El jugador puede seleccionar repetir la partida o regresar al menu
principal desde las pantallas de pausa o fin de juego, y sostener comida
preparada por mas de 5 segundos inflige daño continuo al personaje.
Ayudame a implementar esto con el menor cambio razonable. Explica
cualquier supuesto importante y entrega el AUDIT_SUMMARY al final.

**Instrucción general del prompt**

> Ayudame a implementar esto con el menor cambio razonable. Explica
> cualquier supuesto importante y entrega el AUDIT_SUMMARY al final.

### 12. Fondo visual de la cocina

-   **ID del equipo/tarea:** `FONDO_VISUAL_COCINA_12`
-   **Participantes:** A00428746, A00434440, A00426475, A00429231,
    A00425695

**Objetivo**

Integrar una nueva imagen de fondo visual para la zona de la cocina
dentro del mapa del restaurante, asegurando su correcta ubicacion y
renderizado en la interfaz del videojuego.

**Estado inicial**

El prototipo 2D en Java 17 y JavaFX dibuja la estructura del restaurante
pero requiere actualizar el fondo de la zona de cocina con el nuevo
recurso grafico de imagen.

**Archivos relevantes**

src/main/resources/com/tuempresa/ratonesgame/assets/gameplay/,
RestaurantMap.java, MainApp.java.

**Restricciones**

Java 17, JavaFX, uso de Maven Wrapper, alineacion con la resolucion del
mapa y mantenimiento del rendimiento de renderizado.

**Criterio de terminación**

La nueva imagen se visualiza correctamente como fondo de la zona de la
cocina en la pantalla de juego sin distorsionar los demas elementos
visuales. Ayudame a implementar esto con el menor cambio razonable.
Explica cualquier supuesto importante y entrega el AUDIT_SUMMARY al
final.

**Instrucción general del prompt**

> Ayudame a implementar esto con el menor cambio razonable. Explica
> cualquier supuesto importante y entrega el AUDIT_SUMMARY al final.

### 13. Colisiones de muebles y cocina

-   **ID del equipo/tarea:** `COLISIONES_MUEBLES_COCINA_13`
-   **Participantes:** A00428746, A00434440, A00426475, A00429231,
    A00425695

**Objetivo**

Configurar las colisiones en los muebles y encimeras de la cocina
basandose en la nueva imagen del escenario, delimitando las zonas no
transitables para el raton pero manteniendo libre el paso por el
pasillo.

**Estado inicial**

La cocina cuenta con un fondo visual pero carece de la definicion exacta
de limites y colisiones para que los personajes respeten los obstaculos
fisicos de las encimeras y muebles.

**Archivos relevantes**

RestaurantMap.java, GameEngine.java, ChefAI.java.

**Restricciones**

Java 17, JavaFX, mantenimiento de la malla de colision 2D y
compatibilidad con el sistema de navegacion BFS de la IA del chef.

**Criterio de terminación**

El raton y el chef no pueden atravesar las encimeras ni muebles de la
cocina, mientras que el pasillo permanece completamente libre y
transitable. Ayudame a implementar esto con el menor cambio razonable.
Explica cualquier supuesto importante y entrega el AUDIT_SUMMARY al
final.

**Instrucción general del prompt**

> Ayudame a implementar esto con el menor cambio razonable. Explica
> cualquier supuesto importante y entrega el AUDIT_SUMMARY al final.

### 14. Sprites de personajes alrededor de las mesas

-   **ID del equipo/tarea:** `SPRITES_PERSONAJES_MESAS_14`
-   **Participantes:** A00428746, A00434440, A00426475, A00429231,
    A00425695

**Objetivo**

Incorporar los sprites de los clientes o personajes sentados alrededor
de las mesas del restaurante como objetos decorativos y obstaculos en el
mapa.

**Estado inicial**

Las mesas del restaurante se encuentran vacias sin elementos visuales
adicionales ni obstaculos secundarios que representen a los comensales.

**Archivos relevantes**

src/main/resources/com/tuempresa/ratonesgame/sprites/,
RestaurantMap.java, ResourceManager.java.

**Restricciones**

Java 17, JavaFX, empaquetado de recursos en el classpath y optimizacion
del renderizado en pantalla.

**Criterio de terminación**

Los personajes se muestran posicionados alrededor de las mesas como
sprites en la interfaz y actuan como obstaculos del escenario. Ayudame a
implementar esto con el menor cambio razonable. Explica cualquier
supuesto importante y entrega el AUDIT_SUMMARY al final.

**Instrucción general del prompt**

> Ayudame a implementar esto con el menor cambio razonable. Explica
> cualquier supuesto importante y entrega el AUDIT_SUMMARY al final.

### 15. Actualización de sprites y colisiones de mesas

-   **ID del equipo/tarea:** `SPRITE_Y_COLISION_MESAS_15`
-   **Participantes:** A00428746, A00434440, A00426475, A00429231,
    A00425695

**Objetivo**

Reemplazar los sprites de las mesas del restaurante por una nueva imagen
y aplicar cajas de colision para que ni la rata ni el chef puedan
atravesarlas, obligandolos a rodearlas.

**Estado inicial**

Las mesas anteriores requieren ser actualizadas graficamente y sus cajas
de colision deben ajustarse a las nuevas dimensiones para bloquear el
paso de los personajes.

**Archivos relevantes**

src/main/resources/com/tuempresa/ratonesgame/sprites/,
RestaurantMap.java, GameEngine.java.

**Restricciones**

Java 17, JavaFX, ejecucion mediante Maven Wrapper y preservacion de la
dinamica de persecucion y navegacion en la sala.

**Criterio de terminación**

El nuevo sprite de las mesas se renderiza en la sala y tanto el raton
como el chef colisionan con ellas sin poder cruzarlas por encima,
teniendo que rodearlas. Ayudame a implementar esto con el menor cambio
razonable. Explica cualquier supuesto importante y entrega el
AUDIT_SUMMARY al final.

**Instrucción general del prompt**

> Ayudame a implementar esto con el menor cambio razonable. Explica
> cualquier supuesto importante y entrega el AUDIT_SUMMARY al final.

### 16. Mecánica para soltar comida y restaurar velocidad

-   **ID del equipo/tarea:** `MECANICA_SOLTAR_Y_VELOCIDAD_16`
-   **Participantes:** A00428746, A00434440, A00426475, A00429231,
    A00425695

**Objetivo**

Unificar la tecla de interaccion para recoger y soltar comida en el
raton utilizando la misma tecla (E), permitiendo entregar alimento al
estar cerca de la madriguera y restableciendo la velocidad normal de
desplazamiento del personaje inmediatamente al soltar o entregar el
objeto.

**Estado inicial**

El raton puede recoger comida con la tecla E aplicando una penalizacion
de velocidad por peso, pero se requiere ajustar la logica para soltar o
entregar con esa misma tecla y asegurar la restauracion automatica de la
velocidad base.

**Archivos relevantes**

GameEngine.java, GameManager.java, HUD.java, Burrow.java.

**Restricciones**

Java 17, JavaFX, manejo fluido de eventos de teclado y sincronizacion de
estadisticas del jugador en tiempo real.

**Criterio de terminación**

Presionar la tecla E permite recoger y soltar comida; al soltar el
objeto o entregarlo en la madriguera el raton recupera al instante su
velocidad de movimiento normal. Ayudame a implementar esto con el menor
cambio razonable. Explica cualquier supuesto importante y entrega el
AUDIT_SUMMARY al final.

**Instrucción general del prompt**

> Ayudame a implementar esto con el menor cambio razonable. Explica
> cualquier supuesto importante y entrega el AUDIT_SUMMARY al final.

### 17. Refactorización a un solo jugador y pantalla Game Over

-   **ID del equipo/tarea:** `REFACTORIZACION_SINGLEPLAYER_GAMEOVER_17`
-   **Participantes:** A00428746, A00434440, A00426475, A00429231,
    A00425695

**Objetivo**

Refactorizar el codigo fuente del videojuego para establecer de forma
exclusiva el modo Single-Player, eliminando por completo la logica,
clases, controles de teclado/mouse y referencias visuales asociadas al
segundo jugador, e implementando la pantalla de Game Over con la opcion
de Jugar de nuevo.

**Estado inicial**

El proyecto en Java 17 y JavaFX dispone de seleccion de modo 1P y Versus
2P en la pantalla inicial, controles de teclado mapeados con flechas
para un segundo jugador y logica de vidas, permitiendo reiniciar pero
requiriendo la eliminacion total del modo multijugador local.

**Archivos relevantes**

GameEngine.java, MainApp.java, ChefAI.java, VisionSystem.java,
GameManager.java, HUD.java.

**Restricciones**

Java 17, JavaFX, ejecucion mediante Maven Wrapper, mantenimiento de la
arquitectura 2D y cumplimiento de las reglas de trazabilidad del evento.

**Criterio de terminación**

El videojuego se ejecuta unicamente en modo Single-Player sin
componentes ni controles para un segundo jugador, y al agotarse las
vidas se muestra la pantalla de Game Over con la opcion funcional de
Jugar de nuevo para reiniciar la partida. Ayudame a implementar esto con
el menor cambio razonable. Explica cualquier supuesto importante y
entrega el AUDIT_SUMMARY al final.

**Instrucción general del prompt**

> Ayudame a implementar esto con el menor cambio razonable. Explica
> cualquier supuesto importante y entrega el AUDIT_SUMMARY al final.

### 18. Generación de sprites de cocina y clientes

-   **ID del equipo/tarea:** `GENERACION_SPRITES_COCINA_CLIENTES_18`
-   **Participantes:** A00428746, A00434440, A00426475, A00429231,
    A00425695

**Objetivo**

Crear y diseñar los recursos gráficos en estilo pixel art de 32x32
píxeles en vista cenital para un espacio de cocina que muestre platos y
comida en preparación sin personal de cocina, e incluir una lista
variada de clientes del restaurante para utilizarlos como sprites dentro
del videojuego.

**Estado inicial**

El prototipo 2D top-down desarrollado en Java 17 y JavaFX utiliza un
sistema de procesamiento de imágenes para cargar e integrar los sprites
en la interfaz.

**Archivos relevantes**

src/main/resources/com/tuempresa/ratonesgame/sprites/,
src/main/resources/com/tuempresa/ratonesgame/assets/gameplay/,
ResourceManager.java.

**Restricciones**

Formato de arte en pixel art de 32x32 píxeles, perspectiva cenital
(top-down), Java 17, JavaFX y ejecucion mediante Maven Wrapper.

**Criterio de terminación**

Los nuevos sprites en 32x32 píxeles de la cocina en preparación y la
variedad de clientes se encuentran agregados e integrados correctamente
en los archivos de recursos y en la interfaz visual del juego. Ayudame a
implementar esto con el menor cambio razonable. Explica cualquier
supuesto importante y entrega el AUDIT_SUMMARY al final.

**Instrucción general del prompt**

> Ayudame a implementar esto con el menor cambio razonable. Explica
> cualquier supuesto importante y entrega el AUDIT_SUMMARY al final.

## Observaciones generales

A lo largo de los registros se identifican varias líneas de trabajo:

1.  **Estabilidad y ejecución:** compilación, configuración de Maven
    Wrapper, corrección del comando de inicio y resolución del bloqueo
    en el lobby.
2.  **Mecánicas de juego:** recolección y entrega de comida, persecución
    del chef, vidas, daño por comida caliente, colisiones y flujo de
    victoria/derrota.
3.  **Interfaz y recursos gráficos:** rediseño de la iluminación,
    integración de sprites SVG, fondo de cocina, mesas y clientes.
4.  **Controles y alcance del juego:** configuración de modos, controles
    y posterior refactorización al modo de un solo jugador.
5.  **Organización y trazabilidad:** asignación de roles, creación de la
    rama `game-master` y preservación de cambios locales.

## Cierre

Este archivo reúne los 18 registros de prompts presentes en el documento
fuente y puede guardarse en la raíz del repositorio como
`BITACORA_PROMPTS.md`.
