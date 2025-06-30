# WhatsApp 2: Brújula y Altímeto por GPS

![Imagen WhatsApp 2](https://static.wikia.nocookie.net/shrek-corporation/images/5/5f/WhatsApp2.png/revision/latest?cb=20210926022313&path-prefix=es)

Estoy cansado de salir a la montaña y estar preguntándome todo el rato la altitud a la que estoy respecto al nivel del mar.
A pesar de que un teléfono cualquiera posee el hardware suficiente para saber la altitud (GPS o Barómetro), servicios como Google Maps o las aplicaciones más básicas preinstaladas en los terminales por algún motivo no son muy amables y no muestran la elevación con respecto al nivel del mar.
Si tú también estás cansado de aplicaciones basura que no se pueden desinstalar y que ni siquiera informan de las cosas más básicas he desarrollado esta aplicación ligera y sencilla que puede servir para orientarte en cualquier lugar y obtener las coordenadas geográficas de manera sencilla. Además el tema de la aplicación es WhatsApp 2 por ningún motivo más que el meme y hacerla más graciosa.

## Obtención del ángulo de rumbo

El terminal realiza una fusión de los distintos sensores inerciales integrados, como el **magnetómetro**, el **giróscopo** y el **acelerómetro** para obtener el ángulo de rumbo con respecto al norte magnético.
Es necesario realizar operaciones trigonométricas complejas para compensar la inclinación del dispotivo y compensar de esa manera el vector de campo magnético. Por suerte no tenemos que ocuparnos de todo esto ya que Android posee un fantástico gestor de estos sensores y funciones básicas como `SensorManager.getRotationMatrix()` y `SensorManager.getOrientation()` para obtener la orientación en unas pocas líneas.
Podemos consultar el procedimiento en la [documentación de Android Studio](https://developer.android.com/develop/sensors-and-location/sensors/sensors_position?hl=es-419#sensors-pos-orient)

## Obtención de las coordenadas y la altitud

Para obtener las coordenadas geográficas y la altitud sobre el nivel del mar utilizando el GPS, es necesario crear un gestor de la ubicación que se encargue de realizar peticiones en este caso cada 500ms. Además es necesario gestionar los permisos de ubicación de cara al usuario.
A continuación se implementa la interfaz **LocationListener** con el método `onLocationChanged()`, que actualiza las coordenadas en los respectivos TextView cada vez que el usuario cambia su posición.
