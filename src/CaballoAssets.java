import java.util.Arrays;
import java.util.List;

public class CaballoAssets {
    static char[] slimeSaltando = new char[] {'o', '^', 'º', 'V', 'v', '_'};
    static char[] slimeComiendo = new char[] {'o', 'O', '0', 'o', '~', 'o'};
    static char[] slimeIdle = new char[] {'o', 'O', 'o', 'O', 'o', 'O'};

    static List<String> nombresDisponibles = Arrays.asList(
            "Relámpago", "Tormenta", "Furia", "Viento", "Sombra", "Destello", "Trueno",
            "Centella", "Rayo", "Amanecer", "Diamante", "Corcel", "Espíritu", "Pegaso",
            "Galán", "Estrella", "Valiente", "Sultán", "Mustang", "Veloz",
            "Cometa", "Torbellino", "Relámpago Azul", "Luz", "Niebla", "Eclipse", "Fénix"
    );


    public static List<String> getNombresDisponibles() {
        return nombresDisponibles;
    }
}
