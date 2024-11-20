public class Comedor {
    private int caballosComiendo = 0; // Contador de caballos comiendo simultáneamente
    private final int capacidadMaxima = 2; // Máximo de caballos permitidos

    public synchronized boolean intentarEntrar() {
        if (caballosComiendo < capacidadMaxima) {
            caballosComiendo++; // Incrementar el número de caballos comiendo
            return true; // Permitir la entrada
        }
        return false; // No hay espacio
    }

    public synchronized void salirComedor() {
        caballosComiendo--; // Decrementar el número de caballos comiendo
        notifyAll(); // Notificar que hay espacio en el comedor
    }

    public void recargarEnergia(Cavall caballo) {
        try {
            Thread.sleep(5000); // Simular el tiempo de recarga
            caballo.setEnergia(100); // Recargar la energía al máximo
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
