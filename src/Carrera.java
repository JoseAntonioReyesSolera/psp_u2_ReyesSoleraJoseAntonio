import java.util.*;

public class Carrera {
    private final List<Cavall> caballos;
    private final int distanciaMeta;
    private boolean carreraTerminada = false;
    private volatile boolean pausaGlobal = false;
    private Comedor comedor;

    public Carrera(int numCaballos, int distanciaMeta, List<String> nombresDisponibles) {
        this.distanciaMeta = distanciaMeta;
        this.comedor = new Comedor();
        caballos = new ArrayList<>();

        Collections.shuffle(nombresDisponibles);
        for (int i = 0; i < numCaballos; i++)
            caballos.add(new Cavall(nombresDisponibles.get(i) + " (" + (i + 1) + ")", distanciaMeta, this));
    }

    public synchronized boolean isPausaGlobal() {
        return pausaGlobal;
    }

    public synchronized void setPausaGlobal(boolean pausa) {
        pausaGlobal = pausa;
        if (!pausa) {
            notifyAll(); // Reanuda todos los hilos de caballos
        }
    }

    public Comedor getComedor() {
        return comedor;
    }

    public void iniciar() {
        caballos.forEach(Thread::start);
        controlarCarrera();
    }

    private void controlarCarrera() {
        Scanner scanner = new Scanner(System.in);
        boolean respuestaMarcada = false;
        boolean mostrarCarrera = true; // Control para mostrar o no la carrera

        while (!carreraTerminada) {
            int caballosTerminados = (int) caballos.stream().filter(Cavall::haTerminado).count();

            // Mostrar la carrera solo si la variable mostrarCarrera es true
            if (mostrarCarrera) {
                mostrarEstadoCarrera();
            }

            if (caballosTerminados >= 3 && !respuestaMarcada) {
                // Pausar la carrera para que el usuario decida si continuar
                setPausaGlobal(true);
                System.out.println("\nTres caballos han cruzado la meta. ¿Desea continuar la carrera? (s/n)");
                String respuesta = scanner.nextLine().trim().toLowerCase();

                if (respuesta.equals("s")) {
                    // Continuar la carrera sin preguntar más
                    respuestaMarcada = true;
                    setPausaGlobal(false);  // Reanudar carrera
                } else {
                    respuestaMarcada = true;
                    detenerCaballosRestantes();
                }
            }

            // Cuando todos los caballos han terminado, se acaba la carrera
            if (caballosTerminados == caballos.size()) {
                carreraTerminada = true;
                mostrarClasificacion();
            }

            try {
                Thread.sleep(200); // Control de actualización de pantalla
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void detenerCaballosRestantes() {
        List<Cavall> caballosRestantes = caballos.stream()
                .filter(c -> !c.haTerminado()) // Filtrar caballos que no han llegado a la meta
                .sorted(Comparator.comparingInt(Cavall::getDistanciaRecorrida).reversed()) // Ordenar por distancia
                .toList();

        synchronized (Cavall.class) {
            for (Cavall c : caballosRestantes) {
                c.asignarOrdenLlegada();
                c.detener();
            }
        }
    }

    private void mostrarEstadoCarrera() {
        System.out.print("\033[H"); // Cursor al inicio de la consola
        caballos.forEach(c -> {
            int distanciaVisual = (c.getDistanciaRecorrida() * 30) / distanciaMeta;
            char animacion = c.getAnimacion();
            String pista = " ".repeat(distanciaVisual) + animacion + " ".repeat(30 - distanciaVisual);
            System.out.printf("%-20s [%s] %5d m %5d km/h %5d energia\n",
                    c.getNombre(), pista, c.getDistanciaRecorrida(), c.getVelocidad(), c.getEnergia());
        });

    }

    private void mostrarClasificacion() {
        System.out.println("\nClasificación final:");
        System.out.printf("%-20s %-15s\n", "Caballo", "Distancia");
        caballos.stream()
                .sorted(Comparator.comparingInt(Cavall::getOrdenLlegada))
                .forEach(c -> System.out.printf("%-20s %-15d\n",
                        c.getNombre(), c.getDistanciaRecorrida()));
    }
}
