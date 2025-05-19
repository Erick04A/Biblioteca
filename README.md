import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.JOptionPane; // Para los mensajes

public class SistemaGestionPersonajes {

    private ArrayList<Personaje> personajes = new ArrayList<>();

    public SistemaGestionPersonajes() {
        // Inicializar con los datos de ejemplo
        personajes.add(new Personaje(5, "Link", "Arquero", 300, "Épico"));
        personajes.add(new Personaje(15, "Kratos", "Guerrero", 500, "Legendario"));
        personajes.add(new Personaje(30, "Aloy", "Arquero", 400, "Épico"));
        personajes.add(new Personaje(50, "Geralt", "Mago", 600, "Raro"));
    }

    public ArrayList<Personaje> getPersonajes() {
        return personajes;
    }

    public boolean agregarPersonaje(int id, String nombre, String clase, int fuerza, String rareza) {
        // Validaciones
        if (id % 5 != 0) {
            JOptionPane.showMessageDialog(null, "El ID debe ser múltiplo de 5.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (fuerza % 100 != 0) {
            JOptionPane.showMessageDialog(null, "La Fuerza debe ser múltiplo de 100.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (buscarPersonajePorId(id) != null) {
            JOptionPane.showMessageDialog(null, "El ID ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        personajes.add(new Personaje(id, nombre, clase, fuerza, rareza));
        return true;
    }

    public Personaje buscarPersonajePorId(int id) {
        for (Personaje personaje : personajes) {
            if (personaje.getId() == id) {
                return personaje;
            }
        }
        return null;
    }

    public ArrayList<Personaje> buscarPersonajesPorNombre(String nombre) {
        ArrayList<Personaje> resultados = new ArrayList<>();
        for (Personaje personaje : personajes) {
            if (personaje.getNombre().equalsIgnoreCase(nombre)) {
                resultados.add(personaje);
            }
        }
        return resultados;
    }

    public void ordenarPorFuerzaDescendente() {
        Collections.sort(personajes, new Comparator<Personaje>() {
            @Override
            public int compare(Personaje p1, Personaje p2) {
                return p2.getFuerza() - p1.getFuerza(); // Orden descendente
            }
        });
    }
}
