public class Cavall extends Thread {
    private final String nombre;
    private final int distanciaMeta;
    private int distanciaRecorrida = 0;
    private int velocidad = 50;  // Velocidad inicial de 50 km/h
    private int velocidadAnterior;
    private boolean terminado = false;
    private int frame = 0;  // Control de animación
    private static int contadorLlegada = 0; // Contador de orden de llegada
    private int ordenLlegada = 0; // Almacena el orden de llegada
    private final Carrera carrera;
    private int energia = 100; // Energía inicial
    private boolean comiendo = false; // Estado de si el caballo está comiendo
    private long lastAnimationUpdate = 0; // Control para la animación independiente
    private Thread animacionThread;  // Hilo para las animaciones

    public Cavall(String nombre, int distanciaMeta, Carrera carrera) {
        this.nombre = nombre;
        this.distanciaMeta = distanciaMeta;
        this.carrera = carrera;
        this.velocidadAnterior = velocidad;
    }

    @Override
    public void run() {
        // Iniciar el hilo de animación
        animacionThread = new Thread(() -> {
            while (!terminado) {
                actualizarFrame();  // Actualizar la animación de manera continua
                try {
                    Thread.sleep(200); // Actualización cada 200 ms para la animación fluida
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        animacionThread.start();  // Comienza la animación en un hilo separado

        long lastSpeedUpdate = System.currentTimeMillis();
        while (distanciaRecorrida < distanciaMeta && !terminado) {
            synchronized (carrera) {
                while (carrera.isPausaGlobal()) {
                    try {
                        carrera.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }

            long currentTime = System.currentTimeMillis();
            if (currentTime - lastSpeedUpdate >= 1000) {
                cambiarVelocidad();
                lastSpeedUpdate = currentTime;
            }

            avanzar();

            if (distanciaRecorrida >= distanciaMeta) {
                asignarOrdenLlegada();
            }

            if (energia <= 0 && !comiendo) {
                irAlComedor();
            }

            try {
                Thread.sleep(200); // Para animación fluida
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        terminado = true;
        animacionThread.interrupt();  // Detener el hilo de animación cuando termine la carrera
    }

    private void cambiarVelocidad() {
        if (energia != 0 && velocidad != 0) {
            int cambio = (int) (Math.random() * 11) - 5;
            velocidad = Math.max(15, Math.min(70, velocidad + cambio));
        }
    }

    public void avanzar() {
        if (energia == 0) {
            if (velocidad != 0) {
                velocidadAnterior = velocidad;
            }
            velocidad = 0;  // El caballo no avanza si no tiene energía
        }
        distanciaRecorrida += velocidad * 1000 / 3600 / 5; // km/h a m/s ajustado a 200 ms
        setEnergia(energia -= velocidad / 12); // gasta un poco menos de energia
    }

    private void irAlComedor() {
        // Intentar entrar al comedor
        if (carrera.getComedor().intentarEntrar()) {
            comiendo = true;
            carrera.getComedor().recargarEnergia(this); // Recargar energía
            carrera.getComedor().salirComedor(); // Salir del comedor

            velocidad = velocidadAnterior;  // Restaurar la velocidad cuando la energía se recarga

        }
        comiendo = false; // Termina de comer
    }


    public void actualizarFrame() {
        // Actualizar la animación mientras espera para entrar al comedor o corre
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastAnimationUpdate >= 200) {
            frame++; // Actualizar el frame de la animación
            lastAnimationUpdate = currentTime;
        }
    }

    public String getNombre() {
        return nombre;
    }

    public int getDistanciaRecorrida() {
        return Math.min(distanciaRecorrida, distanciaMeta);
    }

    public int getVelocidad() {
        return velocidad;
    }

    public int getOrdenLlegada() {
        return ordenLlegada;
    }

    public int setOrdenLlegada(int ordenLlegada) {
        this.ordenLlegada = ordenLlegada;
        return ordenLlegada;
    }

    public synchronized void asignarOrdenLlegada() {
        if (ordenLlegada == 0) { // Solo asignar si no se ha asignado aún
            contadorLlegada++;
            this.ordenLlegada = contadorLlegada;
        }
    }


    public void detener() {
        terminado = true;
    }

    public boolean haTerminado() {
        return terminado;
    }

    public int getEnergia() {
        return energia;
    }

    public void setEnergia(int energia) {
        this.energia = Math.max(energia, 0);
    }

    public char getAnimacion() {
        if (comiendo) {
            return CaballoAssets.slimeComiendo[frame % CaballoAssets.slimeComiendo.length];
        } else if (distanciaRecorrida < distanciaMeta && energia > 0) {
            return CaballoAssets.slimeSaltando[frame % CaballoAssets.slimeSaltando.length];
        } else {
            return CaballoAssets.slimeIdle[frame % CaballoAssets.slimeIdle.length];
        }
    }
}
