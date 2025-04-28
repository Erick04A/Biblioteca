import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class SistemaBiblioteca {
    private Map<String, Libro> libros;
    private Map<String, Usuario> usuarios;
    private List<Prestamo> prestamosActivos;
    private Queue<Reserva> reservasPendientes;
    private Stack<Operacion> historialOperaciones;

    public SistemaBiblioteca() {
        libros = new HashMap<>();
        usuarios = new HashMap<>();
        prestamosActivos = new ArrayList<>();
        reservasPendientes = new LinkedList<>();
        historialOperaciones = new Stack<>();

        inicializarDatosEjemplo();
    }

    private void inicializarDatosEjemplo() {
        // Agregar algunos libros de ejemplo
        agregarLibro("C001", "Física Cuántica", "Richard Feynman", "Ciencia", 3);
        agregarLibro("C002", "Cosmos", "Carl Sagan", "Ciencia", 2);
        agregarLibro("L001", "Cien años de soledad", "Gabriel García Márquez", "Literatura", 4);
        agregarLibro("L002", "1984", "George Orwell", "Literatura", 3);
        agregarLibro("H001", "Historia de Roma", "Mary Beard", "Historia", 2);
        agregarLibro("R001", "Enciclopedia Británica", "Varios", "Referencia", 1);

        // Agregar algunos usuarios de ejemplo
        agregarUsuario("U001", "Carlos Pérez");
        agregarUsuario("U002", "Ana Rodríguez");
        agregarUsuario("U003", "Luis Martínez");
    }

    public void agregarLibro(String codigo, String titulo, String autor, String categoria, int ejemplares) {
        Libro libro = new Libro(codigo, titulo, autor, categoria, ejemplares);
        libros.put(codigo, libro);
    }

    public void agregarUsuario(String id, String nombre) {
        Usuario usuario = new Usuario(id, nombre);
        usuarios.put(id, usuario);
    }

    public boolean realizarPrestamo(String idUsuario, String codigoLibro) {
        Usuario usuario = usuarios.get(idUsuario);
        Libro libro = libros.get(codigoLibro);

        if (usuario == null || libro == null) {
            return false;
        }

        if (libro.esReferencia()) {
            registrarOperacion(TipoOperacion.INTENTO_FALLIDO, usuario, libro, "Libro de referencia, solo consulta en sala");
            return false;
        }

        if (!usuario.puedePrestarMas()) {
            registrarOperacion(TipoOperacion.INTENTO_FALLIDO, usuario, libro, "Usuario alcanzó límite de 5 préstamos");
            return false;
        }

        if (!libro.hayDisponibles()) {
            // Crear reserva
            Reserva reserva = new Reserva(libro, usuario, LocalDateTime.now());
            reservasPendientes.add(reserva);
            registrarOperacion(TipoOperacion.RESERVA, usuario, libro, "Sin ejemplares disponibles, pasa a lista de espera");
            return false;
        }

        // Realizar el préstamo
        libro.prestar();
        LocalDate fechaPrestamo = LocalDate.now();
        LocalDate fechaDevolucion = fechaPrestamo.plusDays(7);
        Prestamo prestamo = new Prestamo(libro, usuario, fechaPrestamo, fechaDevolucion, false);

        usuario.agregarPrestamo(prestamo);
        prestamosActivos.add(prestamo);

        registrarOperacion(TipoOperacion.PRESTAMO, usuario, libro,
                "Préstamo hasta " + fechaDevolucion.toString());

        return true;
    }

    public boolean devolverLibro(String idUsuario, String codigoLibro) {
        Usuario usuario = usuarios.get(idUsuario);
        Libro libro = libros.get(codigoLibro);

        if (usuario == null || libro == null) {
            return false;
        }

        Prestamo prestamo = usuario.buscarPrestamoPorCodigo(codigoLibro);
        if (prestamo == null) {
            return false;
        }

        libro.devolver();
        usuario.eliminarPrestamo(prestamo);
        prestamosActivos.remove(prestamo);

        registrarOperacion(TipoOperacion.DEVOLUCION, usuario, libro,
                "Devolución realizada en " + LocalDate.now().toString());

        // Procesar reserva si hay alguna pendiente para este libro
        procesarReservasPendientes(libro);

        return true;
    }

    public boolean renovarPrestamo(String idUsuario, String codigoLibro) {
        Usuario usuario = usuarios.get(idUsuario);
        Libro libro = libros.get(codigoLibro);

        if (usuario == null || libro == null) {
            return false;
        }

        Prestamo prestamo = usuario.buscarPrestamoPorCodigo(codigoLibro);
        if (prestamo == null) {
            return false;
        }

        // Verificar si ya fue renovado
        if (prestamo.isRenovado()) {
            registrarOperacion(TipoOperacion.INTENTO_FALLIDO, usuario, libro,
                    "El préstamo ya fue renovado previamente");
            return false;
        }

        // Verificar si está vencido
        if (prestamo.getFechaDevolucion().isBefore(LocalDate.now())) {
            registrarOperacion(TipoOperacion.INTENTO_FALLIDO, usuario, libro,
                    "No se puede renovar un préstamo vencido");
            return false;
        }

        // Extender la fecha de devolución por 7 días más
        LocalDate nuevaFecha = prestamo.getFechaDevolucion().plusDays(7);
        prestamo.setFechaDevolucion(nuevaFecha);
        prestamo.setRenovado(true);

        registrarOperacion(TipoOperacion.RENOVACION, usuario, libro,
                "Renovación hasta " + nuevaFecha.toString());

        return true;
    }

    private void procesarReservasPendientes(Libro libro) {
        if (!libro.hayDisponibles() || reservasPendientes.isEmpty()) {
            return;
        }

        // Buscar la primera reserva para este libro
        Reserva reservaAProceser = null;
        for (Reserva reserva : reservasPendientes) {
            if (reserva.getLibro().equals(libro)) {
                reservaAProceser = reserva;
                break;
            }
        }

        if (reservaAProceser != null) {
            reservasPendientes.remove(reservaAProceser);

            // Notificar al usuario (en un sistema real esto enviaría un correo/notificación)
            registrarOperacion(TipoOperacion.NOTIFICACION, reservaAProceser.getUsuario(), libro,
                    "Libro disponible para retirar");
        }
    }

    private void registrarOperacion(TipoOperacion tipo, Usuario usuario, Libro libro, String detalles) {
        Operacion operacion = new Operacion(tipo, usuario, libro, LocalDateTime.now(), detalles);
        historialOperaciones.push(operacion); // LIFO
    }

    public Libro buscarLibroPorCodigo(String codigo) {
        return libros.get(codigo);
    }

    public List<Libro> buscarLibrosPorTitulo(String titulo) {
        List<Libro> resultado = new ArrayList<>();
        for (Libro libro : libros.values()) {
            if (libro.getTitulo().toLowerCase().contains(titulo.toLowerCase())) {
                resultado.add(libro);
            }
        }
        return resultado;
    }

    public List<Libro> buscarLibrosPorAutor(String autor) {
        List<Libro> resultado = new ArrayList<>();
        for (Libro libro : libros.values()) {
            if (libro.getAutor().toLowerCase().contains(autor.toLowerCase())) {
                resultado.add(libro);
            }
        }
        return resultado;
    }

    public List<Libro> obtenerLibrosPorCategoria(String categoria) {
        List<Libro> resultado = new ArrayList<>();
        for (Libro libro : libros.values()) {
            if (libro.getCategoria().equals(categoria)) {
                resultado.add(libro);
            }
        }
        return resultado;
    }

    public Usuario buscarUsuarioPorId(String id) {
        return usuarios.get(id);
    }

    public List<Prestamo> obtenerPrestamosVencidos() {
        List<Prestamo> vencidos = new ArrayList<>();
        LocalDate hoy = LocalDate.now();

        for (Prestamo prestamo : prestamosActivos) {
            if (prestamo.getFechaDevolucion().isBefore(hoy)) {
                vencidos.add(prestamo);
            }
        }

        return vencidos;
    }

    public List<Prestamo> obtenerPrestamosActivos() {
        return new ArrayList<>(prestamosActivos);
    }

    public List<Operacion> obtenerHistorialOperaciones() {
        // Convertir Stack a List manteniendo el orden LIFO
        List<Operacion> historial = new ArrayList<>();
        Stack<Operacion> tempStack = new Stack<>();
        tempStack.addAll(historialOperaciones);

        while (!tempStack.isEmpty()) {
            historial.add(tempStack.pop());
        }

        return historial;
    }

    public List<Reserva> obtenerReservasPendientes() {
        return new ArrayList<>(reservasPendientes);
    }

    public Map<String, Libro> getLibros() {
        return libros;
    }

    public Map<String, Usuario> getUsuarios() {
        return usuarios;
    }
}

enum TipoOperacion {
    PRESTAMO,
    DEVOLUCION,
    RENOVACION,
    RESERVA,
    NOTIFICACION,
    INTENTO_FALLIDO
}

class Prestamo {
    private Libro libro;
    private Usuario usuario;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;
    private boolean renovado;

    public Prestamo(Libro libro, Usuario usuario, LocalDate fechaPrestamo, LocalDate fechaDevolucion, boolean renovado) {
        this.libro = libro;
        this.usuario = usuario;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
        this.renovado = renovado;
    }

    public Libro getLibro() {
        return libro;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDate fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public boolean isRenovado() {
        return renovado;
    }

    public void setRenovado(boolean renovado) {
        this.renovado = renovado;
    }

    @Override
    public String toString() {
        return "Libro: " + libro.getTitulo() +
                " | Usuario: " + usuario.getNombre() +
                " | Prestado: " + fechaPrestamo +
                " | Devolución: " + fechaDevolucion +
                (renovado ? " (Renovado)" : "");
    }
}

class Reserva {
    private Libro libro;
    private Usuario usuario;
    private LocalDateTime fechaSolicitud;

    public Reserva(Libro libro, Usuario usuario, LocalDateTime fechaSolicitud) {
        this.libro = libro;
        this.usuario = usuario;
        this.fechaSolicitud = fechaSolicitud;
    }

    public Libro getLibro() {
        return libro;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    @Override
    public String toString() {
        return "Libro: " + libro.getTitulo() +
                " | Usuario: " + usuario.getNombre() +
                " | Solicitado: " + fechaSolicitud;
    }
}

class Operacion {
    private TipoOperacion tipo;
    private Usuario usuario;
    private Libro libro;
    private LocalDateTime fecha;
    private String detalles;

    public Operacion(TipoOperacion tipo, Usuario usuario, Libro libro, LocalDateTime fecha, String detalles) {
        this.tipo = tipo;
        this.usuario = usuario;
        this.libro = libro;
        this.fecha = fecha;
        this.detalles = detalles;
    }

    public TipoOperacion getTipo() {
        return tipo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Libro getLibro() {
        return libro;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getDetalles() {
        return detalles;
    }

    @Override
    public String toString() {
        return "[" + fecha + "] " + tipo +
                " - Usuario: " + usuario.getNombre() +
                " - Libro: " + libro.getTitulo() +
                " - " + detalles;
    }
}
