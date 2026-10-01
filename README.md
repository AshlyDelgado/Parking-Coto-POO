# Parking Coto

Sistema de gestión de un parqueo privado con cobro por hora, hecho en Java con una interfaz gráfica en JavaFX. Proyecto 2 del curso EIF400 · Paradigmas de Programación. El diseño, el diagrama UML y las pruebas se documentan en el informe.

## Requisitos

- **JDK 8 de Oracle** (ya trae JavaFX) **o JDK 17 o superior**. Probado con 8 y 21: el código se compila para Java 8, así que corre en ambos.
- **Maven 3.8 o superior** (probado con 3.8.7). NetBeans ya lo trae integrado, no hace falta instalarlo aparte; si `mvn` no está en el `PATH`, se usa desde NetBeans con los botones *Run*, *Test* y *Clean and Build*.
- Opcional: **NetBeans** para ejecutar con un clic, y **Scene Builder** para editar las pantallas (archivos `.fxml`).

## Cómo ejecutar

Todos los comandos se escriben dentro de la carpeta `parking-coto`.

### 1. La aplicación con interfaz gráfica

Con NetBeans: abrir la carpeta `parking-coto` como proyecto y usar *Run Project* (F6). Funciona con el JDK 8 y con el 17 o superior.

Desde la terminal con JDK 17 o superior (descarga JavaFX la primera vez, necesita internet):

```bash
mvn javafx:run
```

Desde la terminal con JDK 8 de Oracle, que ya trae JavaFX:

```bash
mvn compile
java -cp target/classes cr.ac.una.parking.coto.ParkingCoto
```

### 2. Los casos de prueba en consola (no necesita JavaFX)

```bash
mvn compile
java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -cp target/classes cr.ac.una.parking.coto.ParkingCoto --console
```

Las dos opciones `-D` hacen que `₡` y las tildes se vean bien: `file.encoding` es la que funciona con JDK 8 y `stdout.encoding` la que funciona con JDK 19 o superior. Si la terminal de Windows aun así muestra símbolos raros, puede ayudar ejecutar antes `chcp 65001`. Esto no afecta a la interfaz gráfica ni a las pruebas.

Con `--markdown` en lugar de `--console` imprime los casos como tabla Markdown.

### 3. Las pruebas automáticas (JUnit)

```bash
mvn test
```

Con NetBeans: clic derecho sobre el proyecto y *Test*. Resultado esperado: 62 pruebas, 0 fallos.

## Cómo usar la interfaz en una demostración

El menú lateral tiene un paso por pantalla:

1. **Vehículos** → *Cargar datos de ejemplo* registra 7 espacios y 7 vehículos.
2. **Registrar ingreso** → elige el vehículo y escribe la fecha y hora de entrada. Se asigna automáticamente un espacio compatible. Marcando *Elegir el espacio manualmente* se pueden probar los rechazos por espacio ocupado, fuera de servicio o incompatible.
3. **Registrar salida** → los atajos (`+1 min`, `+60 min`, `+61 min`, `+2 h`, `+11 h`, `+25 h`) ponen la hora de salida a ese tiempo después de la entrada, así se muestran todas las permanencias sin esperar. Muestra horas cobradas y monto.
4. **Registrar pago** → cobra el ticket cerrado. Un ticket que sigue activo se rechaza.
5. **Tickets** y **Consultas y reportes** → tickets activos e historial, vehículos dentro, espacios disponibles, pagos, ocupación por tipo e ingresos totales.
6. **Casos de prueba** → *Ejecutar casos de prueba* corre los 15 casos obligatorios del enunciado y 4 adicionales, y compara lo esperado con lo obtenido.

Cada operación muestra un aviso verde si funcionó, o uno rojo con el nombre de la excepción de negocio si una regla la rechazó. El panel *Actividad reciente* del Dashboard guarda la secuencia.

Además de estos casos se pueden hacer pruebas manuales adicionales en la interfaz; las pruebas automáticas (sección 3) ya cubren los casos de negocio.
