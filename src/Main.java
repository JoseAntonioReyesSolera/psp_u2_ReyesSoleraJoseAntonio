import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            int numCaballos = cantCaballos();
            int distanciaMeta = metrosCarrera();

            Carrera carrera = new Carrera(numCaballos, distanciaMeta, CaballoAssets.getNombresDisponibles());
            System.out.print("\033[H\033[2J");
            System.out.flush();
            carrera.iniciar();

            System.out.print("¿Desea realizar una nueva simulación? (s/n): ");
            String respuesta = scanner.next();
            if (!respuesta.equalsIgnoreCase("s")) {
                System.out.println("Programa finalizado.");
                break;
            }
        }
    }

    private static int cantCaballos() {
        int numCaballos = 0;
        while (true) {
            System.out.println("Ingrese el número de caballos (mínimo 10): ");
            try {
                numCaballos = scanner.nextInt();
                if (numCaballos >= 10 && numCaballos <= CaballoAssets.getNombresDisponibles().size()) {
                    break;
                } else {
                    System.out.println("Debe ingresar al menos 10 caballos Y menos que nombres posibles ("+CaballoAssets.getNombresDisponibles().size()+ ")");
                }
            } catch (InputMismatchException e) {
                System.err.println("Error: Debe introducir un número entero válido.");
                scanner.next();  // Limpiar la entrada incorrecta
            }
        }
        return numCaballos;
    }

    private static int metrosCarrera() {
        int distanciaMeta = 0;
        while (true) {
            System.out.println("Ingrese la distancia de la carrera en metros: ");
            try {
                distanciaMeta = scanner.nextInt();
                if (distanciaMeta > 0) {
                    break;
                } else {
                    System.out.println("La distancia debe ser un número positivo.");
                }
            } catch (InputMismatchException e) {
                System.err.println("Error: Debe introducir un número entero válido.");
                scanner.next();  // Limpiar la entrada incorrecta
            }
        }
        return distanciaMeta;
    }
}
