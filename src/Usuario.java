import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Usuario {
    private String idUniversitario;
    private String nombre;
    private List<Prestamo> librosPrestados;

    public Usuario(String idUniversitario, String nombre) {
        this.idUniversitario = idUniversitario;
        this.nombre = nombre;
        this.librosPrestados = new ArrayList<>();
    }

    public String getIdUniversitario() {
        return idUniversitario;
    }

    public String getNombre() {
        return nombre;
    }

    public List<Prestamo> getLibrosPrestados() {
        return new ArrayList<>(librosPrestados);
    }

    public int getCantidadLibrosPrestados() {
        return librosPrestados.size();
    }

    public boolean puedePrestarMas() {
        return librosPrestados.size() < 5;
    }

    public void agregarPrestamo(Prestamo prestamo) {
        librosPrestados.add(prestamo);
    }

    public void eliminarPrestamo(Prestamo prestamo) {
        librosPrestados.remove(prestamo);
    }

    public Prestamo buscarPrestamoPorCodigo(String codigoLibro) {
        for (Prestamo prestamo : librosPrestados) {
            if (prestamo.getLibro().getCodigo().equals(codigoLibro)) {
                return prestamo;
            }
        }
        return null;
    }

    public List<Prestamo> getPrestamosVencidos() {
        List<Prestamo> vencidos = new ArrayList<>();
        LocalDate hoy = LocalDate.now();

        for (Prestamo prestamo : librosPrestados) {
            if (prestamo.getFechaDevolucion().isBefore(hoy)) {
                vencidos.add(prestamo);
            }
        }

        return vencidos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(idUniversitario, usuario.idUniversitario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUniversitario);
    }

    @Override
    public String toString() {
        return "ID: " + idUniversitario + " | Nombre: " + nombre + " | Libros prestados: " + librosPrestados.size();
    }
}