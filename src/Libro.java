import java.util.Objects;

public class Libro {
    private String codigo;
    private String titulo;
    private String autor;
    private String categoria;
    private int ejemplaresDisponibles;
    private int ejemplaresTotales;

    public Libro(String codigo, String titulo, String autor, String categoria, int ejemplaresTotales) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.autor = autor;
        this.categoria = categoria;
        this.ejemplaresTotales = ejemplaresTotales;
        this.ejemplaresDisponibles = ejemplaresTotales;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public String getCategoria() {
        return categoria;
    }

    public int getEjemplaresDisponibles() {
        return ejemplaresDisponibles;
    }

    public int getEjemplaresTotales() {
        return ejemplaresTotales;
    }

    public void setEjemplaresDisponibles(int ejemplaresDisponibles) {
        this.ejemplaresDisponibles = ejemplaresDisponibles;
    }

    public boolean esReferencia() {
        return "Referencia".equals(categoria);
    }

    public boolean hayDisponibles() {
        return ejemplaresDisponibles > 0;
    }

    public boolean prestar() {
        if (esReferencia() || !hayDisponibles()) {
            return false;
        }
        ejemplaresDisponibles--;
        return true;
    }

    public void devolver() {
        if (ejemplaresDisponibles < ejemplaresTotales) {
            ejemplaresDisponibles++;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Libro libro = (Libro) o;
        return Objects.equals(codigo, libro.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return "Código: " + codigo +
                " | Título: " + titulo +
                " | Autor: " + autor +
                " | Categoría: " + categoria +
                " | Disponibles: " + ejemplaresDisponibles +
                "/" + ejemplaresTotales;
    }
}
