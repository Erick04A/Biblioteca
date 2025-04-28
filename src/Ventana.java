import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;

public class Ventana extends JFrame {
    private SistemaBiblioteca sistema;
    private JTabbedPane pestanas;
    private JPanel panelBusqueda, panelPrestamos, panelUsuarios, panelOperaciones;
    private JTextField campoBusqueda, campoIdUsuario, campoCodigoLibro;
    private JButton botonBuscar, botonPrestar, botonDevolver, botonRenovar;
    private JTable tablaResultados, tablaPrestamos, tablaUsuarios, tablaOperaciones;
    private JComboBox<String> comboCriterios, comboCategoria;
    private DefaultTableModel modeloResultados, modeloPrestamos, modeloUsuarios, modeloOperaciones;
    private JScrollPane scrollResultados, scrollPrestamos, scrollUsuarios, scrollOperaciones;
    private JLabel etiquetaEstado;

    public Ventana() {
        // Inicializar el sistema
        sistema = new SistemaBiblioteca();

        // Configurar la ventana principal
        setTitle("Sistema de Biblioteca");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Inicializar etiqueta de estado primero
        etiquetaEstado = new JLabel("Sistema iniciado correctamente");
        etiquetaEstado.setBorder(new EmptyBorder(5, 10, 5, 10));

        // Crear pestañas
        pestanas = new JTabbedPane();
        inicializarPanelBusqueda();
        inicializarPanelPrestamos();
        inicializarPanelUsuarios();
        inicializarPanelOperaciones();

        // Agregar pestañas al panel principal
        pestanas.addTab("Buscar Libros", panelBusqueda);
        pestanas.addTab("Préstamos", panelPrestamos);
        pestanas.addTab("Usuarios", panelUsuarios);
        pestanas.addTab("Historial", panelOperaciones);

        // Agregar componentes al JFrame
        add(pestanas, BorderLayout.CENTER);
        add(etiquetaEstado, BorderLayout.SOUTH);
    }

    private void inicializarPanelBusqueda() {
        panelBusqueda = new JPanel(new BorderLayout());
        panelBusqueda.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel superior para búsqueda
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));

        campoBusqueda = new JTextField(20);
        botonBuscar = new JButton("Buscar");

        // Opciones de búsqueda
        String[] criterios = {"Título", "Autor", "Código", "Categoría"};
        comboCriterios = new JComboBox<>(criterios);

        // Categorías
        String[] categorias = {"Todas", "Ciencia", "Literatura", "Historia", "Referencia"};
        comboCategoria = new JComboBox<>(categorias);
        comboCategoria.setVisible(false);

        comboCriterios.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (comboCriterios.getSelectedItem().equals("Categoría")) {
                    campoBusqueda.setVisible(false);
                    comboCategoria.setVisible(true);
                } else {
                    campoBusqueda.setVisible(true);
                    comboCategoria.setVisible(false);
                }
            }
        });

        // Añadir componentes al panel superior
        panelSuperior.add(new JLabel("Buscar por: "));
        panelSuperior.add(comboCriterios);
        panelSuperior.add(campoBusqueda);
        panelSuperior.add(comboCategoria);
        panelSuperior.add(botonBuscar);

        // Tabla de resultados
        String[] columnas = {"Código", "Título", "Autor", "Categoría", "Disponibles", "Total"};
        modeloResultados = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaResultados = new JTable(modeloResultados);
        scrollResultados = new JScrollPane(tablaResultados);

        // Action listeners
        botonBuscar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realizarBusqueda();
            }
        });

        // Añadir componentes al panel principal
        panelBusqueda.add(panelSuperior, BorderLayout.NORTH);
        panelBusqueda.add(scrollResultados, BorderLayout.CENTER);
    }

    private void inicializarPanelPrestamos() {
        panelPrestamos = new JPanel(new BorderLayout());
        panelPrestamos.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel superior para gestión de préstamos
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));

        campoIdUsuario = new JTextField(10);
        campoCodigoLibro = new JTextField(10);
        botonPrestar = new JButton("Prestar");
        botonDevolver = new JButton("Devolver");
        botonRenovar = new JButton("Renovar");

        panelSuperior.add(new JLabel("ID Usuario: "));
        panelSuperior.add(campoIdUsuario);
        panelSuperior.add(new JLabel("Código Libro: "));
        panelSuperior.add(campoCodigoLibro);
        panelSuperior.add(botonPrestar);
        panelSuperior.add(botonDevolver);
        panelSuperior.add(botonRenovar);

        // Tabla de préstamos activos
        String[] columnas = {"Libro", "Usuario", "Fecha Préstamo", "Fecha Devolución", "Estado"};
        modeloPrestamos = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaPrestamos = new JTable(modeloPrestamos);
        scrollPrestamos = new JScrollPane(tablaPrestamos);

        // Action listeners
        botonPrestar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                prestarLibro();
            }
        });

        botonDevolver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                devolverLibro();
            }
        });

        botonRenovar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                renovarPrestamo();
            }
        });

        // Añadir componentes al panel principal
        panelPrestamos.add(panelSuperior, BorderLayout.NORTH);
        panelPrestamos.add(scrollPrestamos, BorderLayout.CENTER);

        // Botón para cargar préstamos
        JButton botonCargarPrestamos = new JButton("Cargar Préstamos Actuales");
        botonCargarPrestamos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarPrestamosActivos();
            }
        });

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelInferior.add(botonCargarPrestamos);
        panelPrestamos.add(panelInferior, BorderLayout.SOUTH);
    }

    private void inicializarPanelUsuarios() {
        panelUsuarios = new JPanel(new BorderLayout());
        panelUsuarios.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Tabla de usuarios
        String[] columnas = {"ID", "Nombre", "Préstamos Activos"};
        modeloUsuarios = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaUsuarios = new JTable(modeloUsuarios);
        scrollUsuarios = new JScrollPane(tablaUsuarios);

        // Panel de búsqueda de usuarios
        JPanel panelBusquedaUsuarios = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField campoIdBusqueda = new JTextField(10);
        JButton botonBuscarUsuario = new JButton("Buscar Usuario");
        JButton botonMostrarTodos = new JButton("Mostrar Todos");

        panelBusquedaUsuarios.add(new JLabel("ID Usuario: "));
        panelBusquedaUsuarios.add(campoIdBusqueda);
        panelBusquedaUsuarios.add(botonBuscarUsuario);
        panelBusquedaUsuarios.add(botonMostrarTodos);

        // Action listeners
        botonBuscarUsuario.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String idUsuario = campoIdBusqueda.getText().trim();
                if (!idUsuario.isEmpty()) {
                    buscarUsuario(idUsuario);
                } else {
                    JOptionPane.showMessageDialog(Ventana.this, "Ingrese un ID de usuario válido");
                }
            }
        });

        botonMostrarTodos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarTodosLosUsuarios();
            }
        });

        // Añadir componentes al panel principal
        panelUsuarios.add(panelBusquedaUsuarios, BorderLayout.NORTH);
        panelUsuarios.add(scrollUsuarios, BorderLayout.CENTER);

        // Cargar usuarios inicialmente
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                cargarTodosLosUsuarios();
            }
        });
    }

    private void inicializarPanelOperaciones() {
        panelOperaciones = new JPanel(new BorderLayout());
        panelOperaciones.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Tabla de operaciones
        String[] columnas = {"Fecha", "Tipo", "Usuario", "Libro", "Detalles"};
        modeloOperaciones = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaOperaciones = new JTable(modeloOperaciones);
        scrollOperaciones = new JScrollPane(tablaOperaciones);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton botonCargarHistorial = new JButton("Cargar Historial");

        panelBotones.add(botonCargarHistorial);

        // Action listener
        botonCargarHistorial.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarHistorialOperaciones();
            }
        });

        // Añadir componentes al panel principal
        panelOperaciones.add(panelBotones, BorderLayout.NORTH);
        panelOperaciones.add(scrollOperaciones, BorderLayout.CENTER);
    }

    private void realizarBusqueda() {
        // Limpiar tabla de resultados
        modeloResultados.setRowCount(0);

        String criterio = (String) comboCriterios.getSelectedItem();
        List<Libro> resultados = null;

        switch (criterio) {
            case "Título":
                resultados = sistema.buscarLibrosPorTitulo(campoBusqueda.getText().trim());
                break;
            case "Autor":
                resultados = sistema.buscarLibrosPorAutor(campoBusqueda.getText().trim());
                break;
            case "Código":
                Libro libro = sistema.buscarLibroPorCodigo(campoBusqueda.getText().trim());
                if (libro != null) {
                    resultados = List.of(libro);
                }
                break;
            case "Categoría":
                String categoria = (String) comboCategoria.getSelectedItem();
                if (!"Todas".equals(categoria)) {
                    resultados = sistema.obtenerLibrosPorCategoria(categoria);
                } else {
                    resultados = sistema.getLibros().values().stream().toList();
                }
                break;
        }

        if (resultados != null && !resultados.isEmpty()) {
            for (Libro libro : resultados) {
                modeloResultados.addRow(new Object[]{
                        libro.getCodigo(),
                        libro.getTitulo(),
                        libro.getAutor(),
                        libro.getCategoria(),
                        libro.getEjemplaresDisponibles(),
                        libro.getEjemplaresTotales()
                });
            }
            etiquetaEstado.setText("Se encontraron " + resultados.size() + " resultados");
        } else {
            etiquetaEstado.setText("No se encontraron libros con ese criterio");
        }
    }

    private void cargarPrestamosActivos() {
        // Limpiar tabla de préstamos
        modeloPrestamos.setRowCount(0);

        List<Prestamo> prestamos = sistema.obtenerPrestamosActivos();
        for (Prestamo prestamo : prestamos) {
            LocalDate fechaDevolucion = prestamo.getFechaDevolucion();
            LocalDate hoy = LocalDate.now();
            String estado = fechaDevolucion.isBefore(hoy) ? "VENCIDO" : (prestamo.isRenovado() ? "RENOVADO" : "VIGENTE");

            modeloPrestamos.addRow(new Object[]{
                    prestamo.getLibro().getTitulo(),
                    prestamo.getUsuario().getNombre(),
                    prestamo.getFechaPrestamo(),
                    prestamo.getFechaDevolucion(),
                    estado
            });
        }

        etiquetaEstado.setText("Préstamos cargados: " + prestamos.size());
    }

    private void prestarLibro() {
        String idUsuario = campoIdUsuario.getText().trim();
        String codigoLibro = campoCodigoLibro.getText().trim();

        if (idUsuario.isEmpty() || codigoLibro.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el ID de usuario y código del libro");
            return;
        }

        boolean exito = sistema.realizarPrestamo(idUsuario, codigoLibro);

        if (exito) {
            etiquetaEstado.setText("Préstamo realizado correctamente");
            cargarPrestamosActivos();
        } else {
            etiquetaEstado.setText("No se pudo realizar el préstamo");
            JOptionPane.showMessageDialog(this, "No se pudo realizar el préstamo. Verifique los datos o que el libro esté disponible.");
        }
    }

    private void devolverLibro() {
        String idUsuario = campoIdUsuario.getText().trim();
        String codigoLibro = campoCodigoLibro.getText().trim();

        if (idUsuario.isEmpty() || codigoLibro.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el ID de usuario y código del libro");
            return;
        }

        boolean exito = sistema.devolverLibro(idUsuario, codigoLibro);

        if (exito) {
            etiquetaEstado.setText("Libro devuelto correctamente");
            cargarPrestamosActivos();
        } else {
            etiquetaEstado.setText("No se pudo devolver el libro");
            JOptionPane.showMessageDialog(this, "No se pudo devolver el libro. Verifique los datos.");
        }
    }

    private void renovarPrestamo() {
        String idUsuario = campoIdUsuario.getText().trim();
        String codigoLibro = campoCodigoLibro.getText().trim();

        if (idUsuario.isEmpty() || codigoLibro.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el ID de usuario y código del libro");
            return;
        }

        boolean exito = sistema.renovarPrestamo(idUsuario, codigoLibro);

        if (exito) {
            etiquetaEstado.setText("Préstamo renovado correctamente");
            cargarPrestamosActivos();
        } else {
            etiquetaEstado.setText("No se pudo renovar el préstamo");
            JOptionPane.showMessageDialog(this, "No se pudo renovar el préstamo. Verifique los datos o que el préstamo no esté ya renovado o vencido.");
        }
    }

    private void cargarTodosLosUsuarios() {
        // Limpiar tabla de usuarios
        modeloUsuarios.setRowCount(0);

        for (Usuario usuario : sistema.getUsuarios().values()) {
            modeloUsuarios.addRow(new Object[]{
                    usuario.getIdUniversitario(),
                    usuario.getNombre(),
                    usuario.getCantidadLibrosPrestados()
            });
        }

        etiquetaEstado.setText("Usuarios cargados: " + sistema.getUsuarios().size());
    }

    private void buscarUsuario(String idUsuario) {
        // Limpiar tabla de usuarios
        modeloUsuarios.setRowCount(0);

        Usuario usuario = sistema.buscarUsuarioPorId(idUsuario);
        if (usuario != null) {
            modeloUsuarios.addRow(new Object[]{
                    usuario.getIdUniversitario(),
                    usuario.getNombre(),
                    usuario.getCantidadLibrosPrestados()
            });
            etiquetaEstado.setText("Usuario encontrado");
        } else {
            etiquetaEstado.setText("Usuario no encontrado");
        }
    }

    private void cargarHistorialOperaciones() {
        // Limpiar tabla de operaciones
        modeloOperaciones.setRowCount(0);

        List<Operacion> operaciones = sistema.obtenerHistorialOperaciones();
        for (Operacion op : operaciones) {
            modeloOperaciones.addRow(new Object[]{
                    op.getFecha(),
                    op.getTipo(),
                    op.getUsuario().getNombre(),
                    op.getLibro().getTitulo(),
                    op.getDetalles()
            });
        }

        etiquetaEstado.setText("Operaciones cargadas: " + operaciones.size());
    }

    public static void main(String[] args) {
        // Configurar el look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Iniciar la aplicación
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Ventana ventana = new Ventana();
                ventana.setVisible(true);
            }
        });
    }
}