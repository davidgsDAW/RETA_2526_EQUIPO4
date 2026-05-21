package App_Ejecutable;

import Conexion_Base_Datos.ConexionBD;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

/*
 * Ventana principal del perfil Administrador.
 *
 * Esta clase representa el panel de control completo para el rol de administrador
 * dentro de la aplicación de gestión del taller informatico
 * y organiza toda la interfaz.
 *
 * Todas las operaciones de lectura y escritura se realizan contra la base de
 * datos MySQL.
 * @author David Gómez
 * @version 3.0
 */
public class AdminFrame extends JFrame {


    // Definimos todos los colores aquí arriba para cambiarlos fácilmente
    // sin tener que buscarlos por todo el código
    private static final Color COLOR_FONDO      = new Color(15, 23, 42);
    private static final Color COLOR_SIDEBAR    = new Color(23, 33, 52);
    private static final Color COLOR_PANEL      = new Color(30, 41, 59);
    private static final Color COLOR_ACENTO     = new Color(56, 189, 248);   // azul claro
    private static final Color COLOR_ACENTO2    = new Color(99, 102, 241);   // violeta
    private static final Color COLOR_PELIGRO    = new Color(248, 113, 113);  // rojo — para borrar/baja
    private static final Color COLOR_OK         = new Color(74, 222, 128);   // verde — para guardar/alta
    private static final Color COLOR_TEXTO      = new Color(226, 232, 240);
    private static final Color COLOR_SUBTEXTO   = new Color(108, 113, 122);  // gris para etiquetas secundarias
    private static final Color COLOR_BORDE      = new Color(51, 65, 85);
    private static final Color COLOR_FILA_PAR   = new Color(30, 41, 59);
    private static final Color COLOR_FILA_IMPAR = new Color(38, 51, 73);
    private static final Color COLOR_SELECCION  = new Color(56, 189, 248, 60); // semitransparente

    // Columnas de la tabla de inventario — el orden aquí debe coincidir
    // con el orden en que la consulta SQL devuelve los campos
    private static final String[] COLUMNAS = {
        "ID", "Nombre", "Categoría", "Estado", "Cantidad", "Armario", "Balda"
    };


    /*Contenedor central que alterna */
    private JPanel           contenidoCentral;
    /* Gestor de vistas del panel central. */
    private CardLayout       cardLayout;
    /* Tabla principal del inventario. */
    private JTable           tablaInventario;
    /* Modelo de datos */
    private DefaultTableModel modeloTabla;
    /* Etiqueta de la barra inferior que muestra el estado de la conexión. */
    private JLabel           lblEstado;
    /* Nombre de usuario con el que se inició sesión. */
    private final String     usuarioActual;
    /*ID de base de datos del usuario actual ,se guarda por si hace falta en consultas futuras. */
    private final int        idUsuarioActual; 


    /*
     * Construye el frame del administrador y lo deja listo para mostrarse.
     *
     * Configura el tamaño, posición y estructura general de la ventana
     *
     * @param usuario   nombre visible del administrador que inició sesión
     * @param idUsuario identificador numérico del usuario en la tabla {@code usuario} de la BD
     */
    public AdminFrame(String usuario, int idUsuario) {
        this.usuarioActual   = usuario;
        this.idUsuarioActual = idUsuario;
        setTitle("Taller IES MHP · Panel Administrador — " + usuario);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null); // centrar en pantalla
        getContentPane().setBackground(COLOR_FONDO);
        setLayout(new BorderLayout(0, 0));

        // Montamos la estructura principal: sidebar a la izquierda,
        // contenido al centro y barra de estado abajo
        add(crearSidebar(),          BorderLayout.WEST);
        add(crearContenidoCentral(), BorderLayout.CENTER);
        add(crearBarraEstado(),      BorderLayout.SOUTH);

        mostrarPanel("inventario"); // panel de inicio al arrancar
    }


    /**
     * Crea y devuelve el panel lateral de navegación 
     *
     * El sidebar contiene la cabecera con el nombre y rol del usuario,
     * los ítems de menú agrupados por secciones  y
     * el botón de cierre de sesión en la parte inferior.
     *
     * El fondo se pinta manualmente sobreescribiendo 
     * para poder añadir la línea separadora derecha con un píxel de grosor.
     */
    private JPanel crearSidebar() {
        // Sobreescribimos paintComponent para pintar el fondo oscuro del sidebar
        // y el borde derecho de 1px que lo separa del contenido central
        JPanel sidebar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(COLOR_SIDEBAR);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(COLOR_BORDE);
                g2.fillRect(getWidth() - 1, 0, 1, getHeight()); // línea de 1px a la derecha
                g2.dispose();
            }
        };
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        //cabecera
        JPanel cab = new JPanel();
        cab.setOpaque(false);
        cab.setLayout(new BoxLayout(cab, BoxLayout.Y_AXIS));
        cab.setBorder(new EmptyBorder(28, 20, 20, 20));
        cab.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JLabel badge = new JLabel("ADMINISTRADOR");
        badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badge.setForeground(COLOR_ACENTO);
        badge.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblNombre = new JLabel(usuarioActual);
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblNombre.setForeground(COLOR_TEXTO);
        lblNombre.setAlignmentX(Component.LEFT_ALIGNMENT);

        cab.add(badge);
        cab.add(Box.createVerticalStrut(4));
        cab.add(lblNombre);
        sidebar.add(cab);
        sidebar.add(crearSeparadorSidebar());

        // Sección INVENTARIO
        sidebar.add(crearSeccion("INVENTARIO"));
        sidebar.add(crearItemMenu("📋", "Ver Inventario",      "inventario"));
        sidebar.add(crearItemMenu("➕", "Alta de Material",    "alta"));
        sidebar.add(crearItemMenu("✏️", "Modificar Material", "modificar"));
        sidebar.add(crearItemMenu("🗑", "Baja de Material",   "baja"));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(crearSeparadorSidebar());

        // Sección GESTIÓN
        sidebar.add(crearSeccion("GESTIÓN"));
        sidebar.add(crearItemMenu("📦", "Préstamos",          "prestamos"));
        sidebar.add(crearItemMenu("👤", "Usuarios",           "usuarios"));
        sidebar.add(crearItemMenu("📊", "Historial",          "historial"));
        sidebar.add(crearItemMenu("📤", "Importar / Exportar","importar"));
        sidebar.add(Box.createVerticalGlue()); // empuja el botón de logout hacia abajo
        sidebar.add(crearSeparadorSidebar());

        // Botón de cierre de sesión , lo he puesto abajo en la izquierda para que sea visible y a la vez no moleste
        JPanel panelLogout = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        panelLogout.setOpaque(false);
        panelLogout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        JButton btnLogout = new JButton("⬅  Cerrar sesión");
        btnLogout.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnLogout.setForeground(COLOR_PELIGRO);
        btnLogout.setContentAreaFilled(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> cerrarSesion());
        panelLogout.add(btnLogout);
        sidebar.add(panelLogout);

        return sidebar;
    }

    /**
     * Crea una línea separadora horizontal para usar entre secciones
     *
     * @return el color de borde de la paleta
     */
    private JSeparator crearSeparadorSidebar() {
        JSeparator sep = new JSeparator();
        sep.setForeground(COLOR_BORDE);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    /**
     * Crea una etiqueta de sección
     *
     * Se usa para encabezar grupos de ítems de menú, por ejemplo
     * "INVENTARIO" o "GESTIÓN". El texto se muestra en mayúsculas,
     * tamaño pequeño y con el color de subtexto para no competir
     * visualmente con los ítems.
     *
     * @param texto texto a mostrar como título de sección
     */
    private JLabel crearSeccion(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(COLOR_SUBTEXTO);
        lbl.setBorder(new EmptyBorder(12, 20, 4, 20));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    /**
     * Crea un ítem de menú interactivo para el sidebar.
     *
     * Cada ítem es un jpanel con efecto hover personalizado-> esto es que al pasar el raton por encima canbia su visualizacion 
     *
     * @param icono   emoji o carácter unicode que actúa como icono
     * @param texto   texto descriptivo de la opción
     * @param panelId identificador de la vista que se mostrará al pulsar
     * @return el panel que representa el ítem de menú
     */
    private JPanel crearItemMenu(String icono, String texto, String panelId) {
        // Panel con hover personalizado; usamos una clase anónima-> esto es pq es mas facil que crear componentes 
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 10)) {
            private boolean hover = false;
            {
                setOpaque(false);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hover = true;  repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
                    @Override public void mouseClicked(MouseEvent e) { mostrarPanel(panelId); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                if (hover) {
                    // Fondo semitransparente + barra azul 
                    g.setColor(new Color(56, 189, 248, 20));
                    g.fillRect(0, 0, getWidth(), getHeight());
                    g.setColor(COLOR_ACENTO);
                    g.fillRect(0, 0, 3, getHeight());
                }
                super.paintComponent(g);
            }
        };
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        JLabel icoLbl = new JLabel(icono);
        icoLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        JLabel txtLbl = new JLabel(texto);
        txtLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtLbl.setForeground(COLOR_TEXTO);
        item.add(icoLbl);
        item.add(txtLbl);
        return item;
    }



    /**
     * Crea el panel central que alberga todas las vistas de la aplicacion.
     *
     * Usa un  CardLayout para poder cambiar de vista de forma
     * instantánea sin recargar nada. Cada vista se identifica con una
     * clave de texto
     *
     * @return el jpanelcon el CardLayout ya configurado
     */
    private JPanel crearContenidoCentral() {
        cardLayout      = new CardLayout();
        contenidoCentral = new JPanel(cardLayout);
        contenidoCentral.setOpaque(false);

        // Registramos cada vista con su clave — el orden no importa
        contenidoCentral.add(crearPanelInventario(), "inventario");
        contenidoCentral.add(crearPanelAlta(),       "alta");
        contenidoCentral.add(crearPanelModificar(),  "modificar");
        contenidoCentral.add(crearPanelBaja(),       "baja");
        contenidoCentral.add(crearPanelPrestamos(),  "prestamos");
        contenidoCentral.add(crearPanelUsuarios(),   "usuarios");
        contenidoCentral.add(crearPanelHistorial(),  "historial");
        contenidoCentral.add(crearPanelImportar(),   "importar");

        return contenidoCentral;
    }

    /**
     * Muestra la vista indicada en el área central.
     *
     * @param id clave de la vista a mostrar 
     */
    private void mostrarPanel(String id) { cardLayout.show(contenidoCentral, id); }

    /**
     * Consulta el inventario en la BD 
     *
     * La consulta se construye dinámicamente: solo se añaden cláusulas
     * para los filtros que no sean nulos ni vacíos. Si todos
     * son nulos, se carga el inventario completo.
     * 
     * @param nombre    texto a buscar en el nombre del material 
     * @param categoria nombre exacto de la categoría
     * @param estado    nombre exacto del estado
     */
    private void cargarInventario(String nombre, String categoria, String estado) {
        modeloTabla.setRowCount(0); // limpiamos la tabla antes de rellenarla

        // Construimos la query base con los JOINs necesarios para mostrar los nombres en lugar de los IDs foráneos
        
        StringBuilder sql = new StringBuilder(
            "SELECT m.id_material, m.nombre, c.nombre AS categoria,c.descripcion, " +
            "       e.nombre AS estado, m.cantidad, " +
            "       u.codigo_armario, u.codigo_balda " +
            "FROM material m " +
            "JOIN categoria       c ON c.id_categoria = m.id_categoria " +
            "JOIN estado_elemento e ON e.id            = m.id_estado " +
            "JOIN ubicacion       u ON u.id_ubicacion  = m.id_ubicacion " +
            "WHERE 1=1");
        
        
        if (nombre    != null && !nombre.isEmpty())
            sql.append(" AND LOWER(m.nombre) LIKE ?");
        if (categoria != null && !categoria.equals("Todos"))
            sql.append(" AND c.nombre = ?");
        if (estado    != null && !estado.equals("Todos"))
            sql.append(" AND e.nombre = ?");
        sql.append(" ORDER BY m.id_material");

        try (Connection con = ConexionBD.getInstance().getConn();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            // Asignamos los parámetros en el mismo orden en que se añadieron
            int idx = 1;
            if (nombre    != null && !nombre.isEmpty())
                ps.setString(idx++, "%" + nombre.toLowerCase() + "%");
            if (categoria != null && !categoria.equals("Todos"))
                ps.setString(idx++, categoria);
            if (estado    != null && !estado.equals("Todos"))
                ps.setString(idx, estado);

            ResultSet rs = ps.executeQuery();
            int total = 0;
            while (rs.next()) {
                // Formateamos el ID con ceros a la izquierda para que ordene bien visualmente
                modeloTabla.addRow(new Object[]{
                    String.format("%03d", rs.getInt("id_material")),
                    rs.getString("nombre"),
                    rs.getString("categoria"),
                    rs.getString("estado"),
                    rs.getInt("cantidad"),
                    rs.getString("codigo_armario") != null ? rs.getString("codigo_armario") : "—",
                    rs.getString("codigo_balda")   != null ? rs.getString("codigo_balda")   : "—"
                });
                total++;
            }
            lblEstado.setText("✓  Conectado · " + total + " elementos · BD: Taller_Informatica");

        } catch (SQLException ex) {
            lblEstado.setText("✗  Error BD: " + ex.getMessage());
            JOptionPane.showMessageDialog(this,
                "Error al cargar el inventario:\n" + ex.getMessage(),
                "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * Crea el panel de consulta del inventario con barra de búsqueda/filtros y
     * los botones de acción rápida
     *
     * La tabla se carga automáticamente al construir el panel 
     * 
     * @return el panel "inventario" listo
     */
    private JPanel crearPanelInventario() {
        JPanel panel = crearPanelBase("📋  Inventario del Taller");

        // Barra superior de busqueda y filtros
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setOpaque(false);

        JTextField busqueda = new JTextField(20);
        estilizarCampo(busqueda);

        // Categorias desde la BD
        JComboBox<String> comboCat   = new JComboBox<>();
        JComboBox<String> comboEst   = new JComboBox<>();
        estilizarCombo(comboCat);
        estilizarCombo(comboEst);
        cargarComboCategoriasConTodos(comboCat);
        cargarComboEstadosConTodos(comboEst);

        JButton btnBuscar  = crearBoton("Buscar",       COLOR_ACENTO2);
        JButton btnRefresh = crearBoton("↻ Actualizar", COLOR_PANEL);

        // Al buscar aplicamos los tres filtros, al actualizar limpiamos todo
        btnBuscar.addActionListener(e ->
            cargarInventario(busqueda.getText().trim(),
                             comboCat.getSelectedItem().toString(),
                             comboEst.getSelectedItem().toString()));
        btnRefresh.addActionListener(e -> {
            busqueda.setText("");
            comboCat.setSelectedIndex(0);
            comboEst.setSelectedIndex(0);
            cargarInventario(null, null, null); // recarga sin filtros
        });

        toolbar.add(new JLabel("🔍") {{ setForeground(COLOR_SUBTEXTO); }});
        toolbar.add(busqueda);
        toolbar.add(new JLabel("Cat:") {{ setForeground(COLOR_SUBTEXTO); setFont(new Font("Segoe UI", Font.PLAIN, 12)); }});
        toolbar.add(comboCat);
        toolbar.add(new JLabel("Estado:") {{ setForeground(COLOR_SUBTEXTO); setFont(new Font("Segoe UI", Font.PLAIN, 12)); }});
        toolbar.add(comboEst);
        toolbar.add(btnBuscar);
        toolbar.add(btnRefresh);

        // Modelo con celdas no editables — el usuario solo puede consultar
        modeloTabla     = new DefaultTableModel(COLUMNAS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaInventario = crearTabla(modeloTabla);

        JScrollPane scroll = new JScrollPane(tablaInventario);
        scroll.setOpaque(false);
        scroll.getViewport().setBackground(COLOR_FONDO);
        scroll.setBorder(new LineBorder(COLOR_BORDE, 1, true));

        // Botones de accion rápida en la parte inferior del panel
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acciones.setOpaque(false);
        JButton btnAnadir   = crearBoton("➕ Añadir",   COLOR_OK);
        JButton btnEditar   = crearBoton("✏️ Editar",  COLOR_ACENTO);
        JButton btnEliminar = crearBoton("🗑 Eliminar", COLOR_PELIGRO);

        // Añadir y editar
        btnAnadir.addActionListener(e -> mostrarPanel("alta"));
        btnEditar.addActionListener(e -> mostrarPanel("modificar"));
        btnEliminar.addActionListener(e -> {
            int fila = tablaInventario.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(this, "Selecciona un elemento primero.",
                    "Sin selección", JOptionPane.INFORMATION_MESSAGE); return;
            }
            // Obtenemos el ID de la primera columna para pasarlo a la baja
            String id = modeloTabla.getValueAt(fila, 0).toString();
            darDeBajaMaterial(Integer.parseInt(id));
        });

        acciones.add(btnAnadir);
        acciones.add(btnEditar);
        acciones.add(btnEliminar);

        panel.add(toolbar,  BorderLayout.NORTH);
        panel.add(scroll,   BorderLayout.CENTER);
        panel.add(acciones, BorderLayout.SOUTH);

        // Cargamos los datos al mostrar la ventana
        SwingUtilities.invokeLater(() -> cargarInventario(null, null, null));
        return panel;
    }


    /**
     * Crea el formulario de alta de nuevo material.
     *
     * El formulario incluye campos de texto para nombre, descripción,
     * cantidad y observaciones, y desplegables para categoría, estado y
     * ubicación (todos cargados desde la BD en tiempo real). Al confirmar,
     * se validan los campos obligatorios .
     *
     * @return el panel "alta" listo
     */
    private JPanel crearPanelAlta() {
        JPanel panel = crearPanelBase("➕  Alta de Material");

        JTextField tfNombre   = campoTexto();
        JTextField tfDesc     = campoTexto();
        JTextField tfCantidad = campoTexto();
        JTextField tfObs      = campoTexto();

        // Combos con datos reales de la BD
        JComboBox<String> cbCat  = new JComboBox<>();//categorias
        JComboBox<String> cbEst  = new JComboBox<>();//estados
        JComboBox<String> cbArm  = new JComboBox<>(); // ubicaciones disponibles
        estilizarCombo(cbCat); estilizarCombo(cbEst); estilizarCombo(cbArm);
        cargarComboCategorias(cbCat);
        cargarComboEstados(cbEst);
        cargarComboUbicaciones(cbArm);

        // Formulario en dos columnas 
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 12, 12);

        // Array de pares facilita añadir o reordenar campos
        Object[][] filas = {
            {"Nombre del elemento", tfNombre},  {"Descripción",   tfDesc},
            {"Categoría",           cbCat},      {"Estado",        cbEst},
            {"Cantidad",            tfCantidad}, {"Ubicación",     cbArm},
            {"Observaciones",       tfObs},      {"",              new JLabel()},
        };
        int col = 0, fila = 0;
        for (Object[] f : filas) {
            gbc.gridx = col * 2; gbc.gridy = fila; gbc.weightx = 0;
            JLabel lbl = new JLabel(f[0].toString());
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lbl.setForeground(COLOR_SUBTEXTO);
            form.add(lbl, gbc);
            gbc.gridx = col * 2 + 1; gbc.weightx = 1;
            form.add((Component) f[1], gbc);
            col++; if (col >= 2) { col = 0; fila++; }
        }

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        JButton btnCancelar  = crearBoton("Cancelar",              COLOR_PANEL);
        JButton btnRegistrar = crearBoton("✓ Registrar elemento",  COLOR_OK);

        btnCancelar.addActionListener(e -> mostrarPanel("inventario"));
        btnRegistrar.addActionListener(e -> {
            String nombre   = tfNombre.getText().trim();
            String cantidad = tfCantidad.getText().trim();
            // Validación básica: nombre y cantidad son los únicos campos obligatorios
            if (nombre.isEmpty() || cantidad.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Nombre y cantidad son obligatorios.", "Campos requeridos",
                    JOptionPane.WARNING_MESSAGE); return;
            }
            try {
                int cant = Integer.parseInt(cantidad);
                // Resolvemos los IDs foráneos a partir de los nombres seleccionados en los combos
                int idCat = obtenerIdPorNombre("categoria",      "id_categoria", cbCat.getSelectedItem().toString());
                int idEst = obtenerIdPorNombre("estado_elemento","id",           cbEst.getSelectedItem().toString());
                int idUbic= obtenerIdUbicacion(cbArm.getSelectedItem().toString());
                insertarMaterial(nombre, tfDesc.getText(), cant, idCat, idEst, idUbic, tfObs.getText());
                // Limpiamos el formulario para que quede listo para otro alta
                tfNombre.setText(""); tfDesc.setText(""); tfCantidad.setText(""); tfObs.setText("");
                cbCat.setSelectedIndex(0); cbEst.setSelectedIndex(0); cbArm.setSelectedIndex(0);
                mostrarPanel("inventario");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                    "La cantidad debe ser un número entero.", "Dato inválido",
                    JOptionPane.WARNING_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                    "Error al obtener datos de la BD:\n" + ex.getMessage(),
                    "Error de BD", JOptionPane.ERROR_MESSAGE);
            }
        });

        botones.add(btnCancelar);
        botones.add(btnRegistrar);
        panel.add(form,    BorderLayout.CENTER);
        panel.add(botones, BorderLayout.SOUTH);
        return panel;
    }


    /**
     * Inserta un nuevo material en la tabla de la BD.
     *
     * La fecha de alta se establece automáticamente 
     * en la propia consulta SQL para evitar discrepancias de zona horaria
     * entre el cliente y el servidor.
     *
     * @param nombre   nombre del material
     * @param desc     descripción del material (puede estar vacía)
     * @param cantidad cantidad inicial en almacén
     * @param idCat    FK a la tabla  categoria
     * @param idEst    FK a la tabla estado_elemento
     * @param idUbic   FK a la tabla ubicacion
     * @param obs      observaciones adicionales (puede estar vacío)
     */
    private void insertarMaterial(String nombre, String desc, int cantidad,
                                  int idCat, int idEst, int idUbic, String obs) {
        String sql =
            "INSERT INTO material (nombre, descripcion, cantidad, fecha_alta, " +
            "                      observaciones, id_categoria, id_estado, id_ubicacion) " +
            "VALUES (?, ?, ?, CURDATE(), ?, ?, ?, ?)";
        try (Connection con = ConexionBD.getInstance().getConn();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, desc);
            ps.setInt(3, cantidad);
            ps.setString(4, obs);
            ps.setInt(5, idCat);
            ps.setInt(6, idEst);
            ps.setInt(7, idUbic);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Elemento registrado correctamente.",
                "Alta exitosa", JOptionPane.INFORMATION_MESSAGE);
            cargarInventario(null, null, null); // refrescamos la tabla
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error al insertar en la BD:\n" + ex.getMessage(),
                "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * Crea el panel de modificación de material existente.
     *usuario escribe el ID del elemento, pulsa "Buscar"
     * y los campos se rellenan con los datos actuales. Entonces puede editar
     * y guardar con "Guardar cambios".
     *
     * El ID real del elemento se guarda en un array de un elemento
     * para que la lambda del boton Guardar pueda
     * acceder a el sin necesidad de declararlo 
     *
     * @return el panel modificar listo
     */
    private JPanel crearPanelModificar() {
        JPanel panel = crearPanelBase("✏️  Modificar Material");

        JTextField tfId = new JTextField(10);
        estilizarCampo(tfId);

        JTextField tfNombre   = campoTexto();
        JTextField tfDesc     = campoTexto();
        JTextField tfCantidad = campoTexto();
        JTextField tfObs      = campoTexto();

        JComboBox<String> cbCat  = new JComboBox<>();
        JComboBox<String> cbEst  = new JComboBox<>();
        JComboBox<String> cbArm  = new JComboBox<>();
        estilizarCombo(cbCat); estilizarCombo(cbEst); estilizarCombo(cbArm);
        cargarComboCategorias(cbCat);
        cargarComboEstados(cbEst);
        cargarComboUbicaciones(cbArm);

        //array de 1 elemento para que la lambda pueda modificarlo
        final int[] idReal = {-1};

        JButton btnBuscar = crearBoton("Buscar", COLOR_ACENTO2);
        btnBuscar.addActionListener(e -> {
            String idStr = tfId.getText().trim();
            if (idStr.isEmpty()) return;
            try {
                int id = Integer.parseInt(idStr);
                // Traemos todos los campos del material más los nombres de las FK
                String sql =
                    "SELECT m.*, c.nombre AS cat_nombre, e.nombre AS est_nombre, " +
                    "       CONCAT(IFNULL(u.codigo_armario,''), ' - ', IFNULL(u.codigo_balda,'')) AS ubic_label " +
                    "FROM material m " +
                    "JOIN categoria       c ON c.id_categoria = m.id_categoria " +
                    "JOIN estado_elemento e ON e.id            = m.id_estado " +
                    "JOIN ubicacion       u ON u.id_ubicacion  = m.id_ubicacion " +
                    "WHERE m.id_material = ?";
                try (Connection con = ConexionBD.getInstance().getConn();
                     PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, id);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        idReal[0] = rs.getInt("id_material");
                        tfNombre.setText(rs.getString("nombre"));
                        tfDesc.setText(rs.getString("descripcion")    != null ? rs.getString("descripcion")    : "");
                        tfCantidad.setText(String.valueOf(rs.getInt("cantidad")));
                        tfObs.setText(rs.getString("observaciones")   != null ? rs.getString("observaciones")  : "");
                        // Seleccionamos el ítem correcto en cada combo
                        seleccionarEnCombo(cbCat, rs.getString("cat_nombre"));
                        seleccionarEnCombo(cbEst, rs.getString("est_nombre"));
                        seleccionarEnCombo(cbArm, rs.getString("ubic_label"));
                    } else {
                        JOptionPane.showMessageDialog(this, "No se encontró el elemento con ID " + id,
                            "No encontrado", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            } catch (NumberFormatException ex2) {
                JOptionPane.showMessageDialog(this, "ID debe ser un número.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
            } catch (SQLException ex2) {
                JOptionPane.showMessageDialog(this, "Error al buscar:\n" + ex2.getMessage(),
                    "Error de BD", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Panel de búsqueda por ID
        JPanel busq = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        busq.setOpaque(false);
        busq.add(new JLabel("Buscar por ID:") {{ setForeground(COLOR_SUBTEXTO); setFont(new Font("Segoe UI", Font.PLAIN, 13)); }});
        busq.add(tfId);
        busq.add(btnBuscar);

        // Formulario de edición (misma estructura que el de alta)
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 12, 12);

        Object[][] filas = {
            {"Nombre del elemento", tfNombre},  {"Descripción",   tfDesc},
            {"Categoría",           cbCat},      {"Estado",        cbEst},
            {"Cantidad",            tfCantidad}, {"Ubicación",     cbArm},
            {"Observaciones",       tfObs},      {"",              new JLabel()},
        };
        int col = 0, fila = 0;
        for (Object[] f : filas) {
            gbc.gridx = col * 2; gbc.gridy = fila; gbc.weightx = 0;
            JLabel lbl = new JLabel(f[0].toString());
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lbl.setForeground(COLOR_SUBTEXTO);
            form.add(lbl, gbc);
            gbc.gridx = col * 2 + 1; gbc.weightx = 1;
            form.add((Component) f[1], gbc);
            col++; if (col >= 2) { col = 0; fila++; }
        }

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        JButton btnCancelar = crearBoton("Cancelar",         COLOR_PANEL);
        JButton btnGuardar  = crearBoton("✓ Guardar cambios",COLOR_ACENTO);

        btnCancelar.addActionListener(e -> mostrarPanel("inventario"));
        btnGuardar.addActionListener(e -> {
            // No dejamos guardar si no se ha buscado primero un elemento
            if (idReal[0] < 0) {
                JOptionPane.showMessageDialog(this, "Busca un elemento primero.", "Sin selección", JOptionPane.WARNING_MESSAGE); return;
            }
            try {
                int cant  = Integer.parseInt(tfCantidad.getText().trim());
                int idCat = obtenerIdPorNombre("categoria",       "id_categoria", cbCat.getSelectedItem().toString());
                int idEst = obtenerIdPorNombre("estado_elemento", "id",           cbEst.getSelectedItem().toString());
                int idUbic= obtenerIdUbicacion(cbArm.getSelectedItem().toString());
                actualizarMaterial(idReal[0], tfNombre.getText(), tfDesc.getText(),
                                   cant, idCat, idEst, idUbic, tfObs.getText());
                mostrarPanel("inventario");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "La cantidad debe ser un número entero.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al obtener IDs:\n" + ex.getMessage(), "Error de BD", JOptionPane.ERROR_MESSAGE);
            }
        });

        botones.add(btnCancelar);
        botones.add(btnGuardar);

        JPanel centro = new JPanel(new BorderLayout(0, 16));
        centro.setOpaque(false);
        centro.add(busq, BorderLayout.NORTH);
        centro.add(form, BorderLayout.CENTER);

        panel.add(centro,  BorderLayout.CENTER);
        panel.add(botones, BorderLayout.SOUTH);
        return panel;
    }


    /**
     * Actualiza en la BD los datos de un material ya existente.
     *
     * @param id      identificador del material a actualizar
     * @param nombre  nuevo nombre
     * @param desc    nueva descripción
     * @param cantidad nueva cantidad
     * @param idCat   nueva FK de categoría
     * @param idEst   nueva FK de estado
     * @param idUbic  nueva FK de ubicación
     * @param obs     nuevas observaciones
     */
    private void actualizarMaterial(int id, String nombre, String desc, int cantidad,
                                    int idCat, int idEst, int idUbic, String obs) {
        String sql =
            "UPDATE material SET nombre=?, descripcion=?, cantidad=?, " +
            "id_categoria=?, id_estado=?, id_ubicacion=?, observaciones=? " +
            "WHERE id_material=?";
        try (Connection con = ConexionBD.getInstance().getConn();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, desc);
            ps.setInt(3, cantidad);
            ps.setInt(4, idCat);
            ps.setInt(5, idEst);
            ps.setInt(6, idUbic);
            ps.setString(7, obs);
            ps.setInt(8, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Elemento actualizado correctamente.",
                "Modificación exitosa", JOptionPane.INFORMATION_MESSAGE);
            cargarInventario(null, null, null); // refrescamos la tabla principal
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error al actualizar en la BD:\n" + ex.getMessage(),
                "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * Crea el panel de baja de material.
     *
     * El administrador introduce el ID, visualiza los datos del elemento
     * para confirmar que es el correcto y a continuación pulsa "Confirmar baja".
     * El aviso amarillo recuerda que el estado cambia a BAJA pero el registro no se elimina de la base de datos
     * (LO HE HECHO ASI POR QUE ME PARECE LA MANERA MAS COHERENTE Y QUE MAS ME GUSTABA)
     *
     * @return el panel baja listo 
     */
    private JPanel crearPanelBaja() {
        JPanel panel = crearPanelBase("🗑  Baja de Material");

        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(new EmptyBorder(20, 0, 0, 0));

        JTextField tfId = new JTextField(12);
        estilizarCampo(tfId);

        //mostrar la info del elemento encontrado
        JLabel[] lblsVal = new JLabel[5];
        String[] etiq    = {"Nombre:", "Categoría:", "Estado:", "Cantidad:", "Ubicación:"};
        JPanel infoGrid  = new JPanel(new GridLayout(5, 2, 8, 8));
        infoGrid.setOpaque(false);
        for (int i = 0; i < 5; i++) {
            JLabel e = new JLabel(etiq[i]); e.setForeground(COLOR_SUBTEXTO); e.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lblsVal[i] = new JLabel("—"); lblsVal[i].setForeground(COLOR_TEXTO); lblsVal[i].setFont(new Font("Segoe UI", Font.BOLD, 13));
            infoGrid.add(e); infoGrid.add(lblsVal[i]);
        }

        final int[] idReal = {-1}; // guarda el ID mientras el usuario decide si confirmar

        JButton btnBuscarBaja = crearBoton("Buscar", COLOR_ACENTO2);
        btnBuscarBaja.addActionListener(e -> {
            String idStr = tfId.getText().trim();
            if (idStr.isEmpty()) return;
            try {
                int id = Integer.parseInt(idStr);
                String sql =
                    "SELECT m.nombre, c.nombre AS categoria, e.nombre AS estado, " +
                    "       m.cantidad, u.codigo_armario, u.codigo_balda " +
                    "FROM material m " +
                    "JOIN categoria       c ON c.id_categoria = m.id_categoria " +
                    "JOIN estado_elemento e ON e.id            = m.id_estado " +
                    "JOIN ubicacion       u ON u.id_ubicacion  = m.id_ubicacion " +
                    "WHERE m.id_material = ?";
                try (Connection con = ConexionBD.getInstance().getConn();
                     PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, id);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        idReal[0] = id;
                        lblsVal[0].setText(rs.getString("nombre"));
                        lblsVal[1].setText(rs.getString("categoria"));
                        lblsVal[2].setText(rs.getString("estado"));
                        lblsVal[3].setText(String.valueOf(rs.getInt("cantidad")));
                        lblsVal[4].setText("Armario " + rs.getString("codigo_armario")
                                         + " · Balda " + rs.getString("codigo_balda"));
                    } else {
                        idReal[0] = -1;
                        for (JLabel l : lblsVal) l.setText("—"); // reseteamos la vista previa
                        JOptionPane.showMessageDialog(this, "No se encontró el elemento con ID " + id,
                            "No encontrado", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            } catch (NumberFormatException ex2) {
                JOptionPane.showMessageDialog(this, "ID debe ser un número.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
            } catch (SQLException ex2) {
                JOptionPane.showMessageDialog(this, "Error al buscar:\n" + ex2.getMessage(),
                    "Error de BD", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel busq = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        busq.setOpaque(false);
        busq.add(new JLabel("ID del elemento:") {{ setForeground(COLOR_SUBTEXTO); setFont(new Font("Segoe UI", Font.PLAIN, 13)); }});
        busq.add(tfId);
        busq.add(btnBuscarBaja);
        busq.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel preview = crearCard("Vista previa del elemento a dar de baja");
        preview.add(infoGrid, BorderLayout.CENTER);

        // Aviso destacado en amarillo para que no pase desapercibido
        JPanel aviso = new JPanel(new FlowLayout(FlowLayout.LEFT));
        aviso.setOpaque(false);
        JLabel warn = new JLabel("⚠  El estado cambiará a BAJA y quedará registrado en el historial.");
        warn.setForeground(new Color(251, 191, 36));
        warn.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        aviso.add(warn);

        contenido.add(busq);
        contenido.add(Box.createVerticalStrut(16));
        contenido.add(preview);
        contenido.add(Box.createVerticalStrut(12));
        contenido.add(aviso);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        JButton btnCancelar  = crearBoton("Cancelar",           COLOR_PANEL);
        JButton btnConfirmar = crearBoton("🗑 Confirmar baja",  COLOR_PELIGRO);

        btnCancelar.addActionListener(e -> mostrarPanel("inventario"));
        btnConfirmar.addActionListener(e -> {
            if (idReal[0] < 0) {
                JOptionPane.showMessageDialog(this, "Busca un elemento primero.", "Sin selección", JOptionPane.WARNING_MESSAGE); return;
            }
            // Segunda confirmación antes de realizar la baja
            int confirm = JOptionPane.showConfirmDialog(this,
                "¿Confirmas la baja de: " + lblsVal[0].getText() + "?\nEl estado cambiará a BAJA.",
                "Confirmar baja", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                darDeBajaMaterial(idReal[0]);
                idReal[0] = -1;
                for (JLabel l : lblsVal) l.setText("—"); // limpiamos la vista previa
                tfId.setText("");
                mostrarPanel("inventario");
            }
        });
        botones.add(btnCancelar);
        botones.add(btnConfirmar);

        panel.add(contenido, BorderLayout.CENTER);
        panel.add(botones,   BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Cambia el estado del material a BAJA en la BD.
     * No elimina físicamente el registro, solo actualiza el campo de estado y lo pone en BAJA.
     * @param id identificador del material a dar de baja
     */
    private void darDeBajaMaterial(int id) {
        // Usamos una subconsulta para no depender de un ID concreto del estado "BAJA"
        String sql = "UPDATE material SET id_estado = " +
                     "(SELECT id FROM estado_elemento WHERE nombre='BAJA') " +
                     "WHERE id_material = ?";
        try (Connection con = ConexionBD.getInstance().getConn();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Elemento dado de baja correctamente.",
                "Baja exitosa", JOptionPane.INFORMATION_MESSAGE);
            cargarInventario(null, null, null); // refrescamos para que desaparezca o cambie de estado
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error al dar de baja:\n" + ex.getMessage(),
                "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * Crea el panel de gestión de préstamos.
     *
     * Muestra una tabla con todos los préstamos (activos y devueltos)
     * y ofrece botones para crear nuevos préstamos y registrar devoluciones.
     * Al registrar una devolución se actualiza la fecha en la BD y el material
     * vuelve a estar disponible.
     *
     * @return el panel prestamos 
     */
    private JPanel crearPanelPrestamos() {
        JPanel panel = crearPanelBase("📦  Gestión de Préstamos");

        String[] cols = {"ID Préstamo", "Material", "Usuario", "Fecha préstamo", "Fecha devolución", "Estado"};
        DefaultTableModel modeloP = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = crearTabla(modeloP);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setOpaque(false);
        scroll.getViewport().setBackground(COLOR_FONDO);
        scroll.setBorder(new LineBorder(COLOR_BORDE, 1, true));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);

        JButton btnNuevo    = crearBoton("➕ Nuevo préstamo",    COLOR_OK);
        JButton btnDevolver = crearBoton("↩ Registrar devolución", COLOR_ACENTO);
        JButton btnRefresh  = crearBoton("↻ Actualizar",          COLOR_PANEL);

        btnRefresh.addActionListener(e -> cargarPrestamos(modeloP));
        btnNuevo.addActionListener(e -> dialogoNuevoPrestamo(modeloP));
        btnDevolver.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(this, "Selecciona un préstamo activo.", "Sin selección", JOptionPane.INFORMATION_MESSAGE); return;
            }
            int idPres = Integer.parseInt(modeloP.getValueAt(fila, 0).toString());
            registrarDevolucion(idPres, modeloP);
        });

        botones.add(btnNuevo);
        botones.add(btnDevolver);
        botones.add(btnRefresh);

        panel.add(scroll,  BorderLayout.CENTER);
        panel.add(botones, BorderLayout.SOUTH);

        SwingUtilities.invokeLater(() -> cargarPrestamos(modeloP));
        return panel;
    }

    /**
     * Carga todos los préstamos desde la BD en el modelo indicado.
     *
     * El estado del préstamo se calcula dinámicamente
     *
     * @param modelo al que se añaden las filas
     */
    private void cargarPrestamos(DefaultTableModel modelo) {
        modelo.setRowCount(0);
        String sql =
            "SELECT p.id_prestamo, m.nombre AS material, u.nombre AS usuario, " +
            "       p.fecha_prestamo, p.fecha_devolucion, " +
            "       IF(p.fecha_devolucion IS NULL, 'ACTIVO','DEVUELTO') AS estado_pres " +
            "FROM prestamo p " +
            "JOIN material m ON m.id_material = p.id_material " +
            "JOIN usuario  u ON u.id_usuario  = p.id_usuario " +
            "ORDER BY p.id_prestamo DESC"; // los más recientes primero
        try (Connection con = ConexionBD.getInstance().getConn();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("id_prestamo"),
                    rs.getString("material"),
                    rs.getString("usuario"),
                    rs.getDate("fecha_prestamo"),
                    rs.getDate("fecha_devolucion") != null ? rs.getDate("fecha_devolucion") : "—",
                    rs.getString("estado_pres")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar préstamos:\n" + ex.getMessage(),
                "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Abre un diálogo para crear un nuevo préstamo.
     *
     * Solicita el ID del material y el ID del usuario. La fecha de
     * préstamo se asigna automáticamente a la fecha actual del servidor
     *
     * @param modeloP modelo de la tabla de préstamos para refrescarlo tras la inserción
     */
    private void dialogoNuevoPrestamo(DefaultTableModel modeloP) {
        JTextField tfIdMat  = new JTextField(8);
        JTextField tfIdUsr  = new JTextField(8);
        estilizarCampo(tfIdMat); estilizarCampo(tfIdUsr);

        JPanel dlg = new JPanel(new GridLayout(4, 2, 8, 8));
        dlg.setBackground(COLOR_PANEL);
        dlg.add(new JLabel("ID Material:") {{ setForeground(COLOR_TEXTO); }});    dlg.add(tfIdMat);
        dlg.add(new JLabel("ID Usuario:")  {{ setForeground(COLOR_TEXTO); }});    dlg.add(tfIdUsr);
        dlg.add(new JLabel("Fecha:") {{ setForeground(COLOR_TEXTO); }});
        JLabel hoy = new JLabel("Hoy (" + java.time.LocalDate.now() + ")");
        hoy.setForeground(COLOR_SUBTEXTO);
        dlg.add(hoy);

        int opt = JOptionPane.showConfirmDialog(this, dlg, "Nuevo préstamo",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opt != JOptionPane.OK_OPTION) return;

        try {
            int idMat = Integer.parseInt(tfIdMat.getText().trim());
            int idUsr = Integer.parseInt(tfIdUsr.getText().trim());
            String sql = "INSERT INTO prestamo (id_material, id_usuario, fecha_prestamo) VALUES (?,?,CURDATE())";
            try (Connection con = ConexionBD.getInstance().getConn();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, idMat); ps.setInt(2, idUsr);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Préstamo registrado. El estado del material cambiará a PRESTADO.",
                    "Préstamo creado", JOptionPane.INFORMATION_MESSAGE);
                cargarPrestamos(modeloP);
                cargarInventario(null, null, null); // el estado del material habrá cambiado
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Los IDs deben ser números enteros.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al registrar préstamo:\n" + ex.getMessage(),
                "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Registra la devolución de un préstamo activo estableciendo la fecha actual.
     *
     * Solo actualiza filas donde la fecha sea nula
     *
     * @param idPrestamo identificador del préstamo a marcar como devuelto
     * @param modeloP    modelo de la tabla para refrescarlo tras el cambio
     */
    private void registrarDevolucion(int idPrestamo, DefaultTableModel modeloP) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Registrar la devolución del préstamo #" + idPrestamo + "?",
            "Confirmar devolución", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        // La condición "AND fecha_devolucion IS NULL" protege de dobles devoluciones
        String sql = "UPDATE prestamo SET fecha_devolucion = CURDATE() WHERE id_prestamo = ? AND fecha_devolucion IS NULL";
        try (Connection con = ConexionBD.getInstance().getConn();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPrestamo);
            int filas = ps.executeUpdate();
            if (filas > 0) {
                JOptionPane.showMessageDialog(this, "Devolución registrada. El material vuelve a DISPONIBLE.",
                    "Devolución exitosa", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Ese préstamo ya estaba devuelto o no existe.",
                    "Sin cambios", JOptionPane.WARNING_MESSAGE);
            }
            cargarPrestamos(modeloP);
            cargarInventario(null, null, null);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al registrar devolución:\n" + ex.getMessage(),
                "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * Crea el panel de gestión de usuarios.
     *
     * Lista todos los usuarios del sistema 
     * Permite crear nuevos usuarios, eliminarlos y refrescar la tabla.
     *(pendiente de la implementación del boton "editar")
     * @return el panel usuario
     */
    private JPanel crearPanelUsuarios() {
        JPanel panel = crearPanelBase("👤  Gestión de Usuarios");

        String[] cols = {"ID", "Nombre", "Rol"};
        DefaultTableModel modeloU = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable tabla = crearTabla(modeloU);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setOpaque(false);
        scroll.getViewport().setBackground(COLOR_FONDO);
        scroll.setBorder(new LineBorder(COLOR_BORDE, 1, true));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        JButton btnNuevo   = crearBoton("➕ Nuevo usuario", COLOR_OK);
//        JButton btnEditar  = crearBoton("✏️ Editar",       COLOR_ACENTO);
        JButton btnElim    = crearBoton("🗑 Eliminar",      COLOR_PELIGRO);
        JButton btnRefresh = crearBoton("↻ Actualizar",    COLOR_PANEL);

        btnRefresh.addActionListener(e -> cargarUsuarios(modeloU));
        btnNuevo.addActionListener(e -> dialogoNuevoUsuario(modeloU));
        btnElim.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) { JOptionPane.showMessageDialog(this, "Selecciona un usuario.", "Sin selección", JOptionPane.INFORMATION_MESSAGE); return; }
            int id = Integer.parseInt(modeloU.getValueAt(fila, 0).toString());
            eliminarUsuario(id, modeloU);
        });

        botones.add(btnNuevo);
//        botones.add(btnEditar);
        botones.add(btnElim);
        botones.add(btnRefresh);

        panel.add(scroll,  BorderLayout.CENTER);
        panel.add(botones, BorderLayout.SOUTH);

        SwingUtilities.invokeLater(() -> cargarUsuarios(modeloU));
        return panel;
    }

    /**
     * Carga todos los usuarios de la BD
     *
     * @param modelo al que se añaden las filas
     */
    private void cargarUsuarios(DefaultTableModel modelo) {
        modelo.setRowCount(0);
        String sql =
            "SELECT u.id_usuario, u.nombre, r.nombre AS rol " +
            "FROM usuario u " +
            "JOIN rol_usuario r ON r.id = u.id_rol " +
            "ORDER BY u.id_usuario";
        try (Connection con = ConexionBD.getInstance().getConn();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("id_usuario"),
                    rs.getString("nombre"),
                    rs.getString("rol")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar usuarios:\n" + ex.getMessage(),
                "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Abre un diálogo modal para dar de alta a un nuevo usuario.
     *
     * Solicita nombre, contraseña y rol. La contraseña se almacena
     * en la BD con bcript.
     * @param modeloU modelo de usuarios para refrescarlo tras la inserción
     */
    private void dialogoNuevoUsuario(DefaultTableModel modeloU) {
        JTextField tfNombre = new JTextField(15);
        JTextField tfPass   = new JPasswordField(15);
        estilizarCampo(tfNombre); estilizarCampo(tfPass);
        JComboBox<String> cbRol = new JComboBox<>(new String[]{"ADMINISTRADOR","PROFESOR"});
        estilizarCombo(cbRol);

        JPanel dlg = new JPanel(new GridLayout(3, 2, 8, 8));
        dlg.setBackground(COLOR_PANEL);
        dlg.add(new JLabel("Nombre:")     {{ setForeground(COLOR_TEXTO); }}); dlg.add(tfNombre);
        dlg.add(new JLabel("Contraseña:") {{ setForeground(COLOR_TEXTO); }}); dlg.add(tfPass);
        dlg.add(new JLabel("Rol:")        {{ setForeground(COLOR_TEXTO); }}); dlg.add(cbRol);

        int opt = JOptionPane.showConfirmDialog(this, dlg, "Nuevo usuario",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opt != JOptionPane.OK_OPTION) return;

        try {
            int idRol = obtenerIdPorNombre("rol_usuario", "id", cbRol.getSelectedItem().toString());
            String sql = "INSERT INTO usuario (nombre, contrasena, id_rol) VALUES (?,?,?)";
            try (Connection con = ConexionBD.getInstance().getConn();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, tfNombre.getText().trim());
                ps.setString(2, tfPass.getText().trim());
                ps.setInt(3, idRol);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Usuario creado correctamente.",
                    "Usuario creado", JOptionPane.INFORMATION_MESSAGE);
                cargarUsuarios(modeloU);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al crear usuario:\n" + ex.getMessage(),
                "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Elimina físicamente un usuario de la BD previa confirmación.
     *
     * A diferencia de la baja de material, aquí sí se elimina el registro.
     *
     * @param id      identificador del usuario a eliminar
     * @param modeloU modelo de usuarios para refrescarlo tras el borrado
     */
    private void eliminarUsuario(int id, DefaultTableModel modeloU) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Eliminar el usuario con ID " + id + "?", "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        String sql = "DELETE FROM usuario WHERE id_usuario = ?";
        try (Connection con = ConexionBD.getInstance().getConn();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            cargarUsuarios(modeloU);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al eliminar usuario:\n" + ex.getMessage(),
                "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Crea el panel del historial de movimientos.
     *
     * Muestra los últimos 200 registros de la tabla ordenados por fecha descendente , el limite de 200 registros ayuda a talleres con mucha actividad. 
     *
     * @return el panel historial
     */
    private JPanel crearPanelHistorial() {
        JPanel panel = crearPanelBase("📊  Historial de Movimientos");

        String[] cols = {"ID", "Material", "Usuario", "Tipo movimiento", "Fecha", "Observaciones"};
        DefaultTableModel modeloH = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable tabla = crearTabla(modeloH);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setOpaque(false);
        scroll.getViewport().setBackground(COLOR_FONDO);
        scroll.setBorder(new LineBorder(COLOR_BORDE, 1, true));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        JButton btnRefresh = crearBoton("↻ Actualizar", COLOR_PANEL);
        btnRefresh.addActionListener(e -> cargarHistorial(modeloH));
        botones.add(btnRefresh);

        panel.add(scroll,  BorderLayout.CENTER);
        panel.add(botones, BorderLayout.SOUTH);

        SwingUtilities.invokeLater(() -> cargarHistorial(modeloH));
        return panel;
    }

    /**
     * Carga los movimientos del historial desde la BD en el modelo indicado.
     *
     * Se limita a los 200 registros más recientes para mantener el rendimiento.
     *
     * @param modelo el {@link DefaultTableModel} al que se añaden las filas
     */
    private void cargarHistorial(DefaultTableModel modelo) {
        modelo.setRowCount(0);
        String sql =
            "SELECT h.id_movimiento, m.nombre AS material, u.nombre AS usuario, " +
            "       h.tipo_movimiento, h.fecha, h.observaciones " +
            "FROM historial_movimiento h " +
            "JOIN material m ON m.id_material = h.id_material " +
            "JOIN usuario  u ON u.id_usuario  = h.id_usuario " +
            "ORDER BY h.fecha DESC LIMIT 200";
        try (Connection con = ConexionBD.getInstance().getConn();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("id_movimiento"),
                    rs.getString("material"),
                    rs.getString("usuario"),
                    rs.getString("tipo_movimiento"),
                    rs.getTimestamp("fecha"),
                    rs.getString("observaciones") != null ? rs.getString("observaciones") : "—"
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar historial:\n" + ex.getMessage(),
                "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Crea el panel de importación y exportación de datos.
     *
     * Divide el espacio en dos tarjetas: una para importar (CSV/XLSX)
     * y otra para exportar (CSV, Excel, PDF). En esta versión los botones
     * están creados pero la lógica de importación/exportación real aún
     * no está implementada lo dejamos como mejora para proximas sesiones.
     *
     * @return el pane importar
     */
    private JPanel crearPanelImportar() {
        JPanel panel = crearPanelBase("📤  Importar / Exportar Datos");

        JPanel contenido = new JPanel(new GridLayout(1, 2, 16, 0));
        contenido.setOpaque(false);
        contenido.setBorder(new EmptyBorder(16, 0, 0, 0));

        // Tarjeta de importación
        JPanel pImport = crearCard("📥  Importar datos");
        JPanel impContent = new JPanel();
        impContent.setOpaque(false);
        impContent.setLayout(new BoxLayout(impContent, BoxLayout.Y_AXIS));
        JLabel lImpDesc = new JLabel("<html>Importa elementos desde un archivo CSV o Excel.<br>El archivo debe seguir la plantilla proporcionada.</html>");
        lImpDesc.setForeground(COLOR_SUBTEXTO); lImpDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JLabel lImpFmt = new JLabel("Formatos: CSV, XLSX, XLS");
        lImpFmt.setForeground(COLOR_ACENTO); lImpFmt.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lImpFmt.setBorder(new EmptyBorder(12, 0, 12, 0));
        JButton btnSelFile = crearBoton("📁  Seleccionar archivo", COLOR_ACENTO2);
        btnSelFile.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton btnImport = crearBoton("✓ Importar", COLOR_OK);
        btnImport.setAlignmentX(Component.LEFT_ALIGNMENT);
        impContent.add(lImpDesc); impContent.add(lImpFmt);
        impContent.add(btnSelFile); impContent.add(Box.createVerticalStrut(8)); impContent.add(btnImport);
        pImport.add(impContent, BorderLayout.CENTER);

        // Tarjeta de exportación
        JPanel pExport = crearCard("📤  Exportar datos");
        JPanel expContent = new JPanel();
        expContent.setOpaque(false);
        expContent.setLayout(new BoxLayout(expContent, BoxLayout.Y_AXIS));
        JLabel lExpDesc = new JLabel("<html>Exporta el inventario completo o filtrado<br>al formato elegido.</html>");
        lExpDesc.setForeground(COLOR_SUBTEXTO); lExpDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JLabel lExpFmt = new JLabel("Formatos: CSV, XLSX, PDF");
        lExpFmt.setForeground(COLOR_ACENTO); lExpFmt.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lExpFmt.setBorder(new EmptyBorder(12, 0, 12, 0));
        JButton btnExpCSV   = crearBoton("Exportar CSV",   COLOR_ACENTO2);
        JButton btnExpExcel = crearBoton("Exportar Excel", new Color(34, 197, 94));
        JButton btnExpPDF   = crearBoton("Exportar PDF",   new Color(251, 146, 60));
        btnExpCSV.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnExpExcel.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnExpPDF.setAlignmentX(Component.LEFT_ALIGNMENT);
        expContent.add(lExpDesc); expContent.add(lExpFmt);
        expContent.add(btnExpCSV); expContent.add(Box.createVerticalStrut(6));
        expContent.add(btnExpExcel); expContent.add(Box.createVerticalStrut(6));
        expContent.add(btnExpPDF);
        pExport.add(expContent, BorderLayout.CENTER);

        contenido.add(pImport);
        contenido.add(pExport);
        panel.add(contenido, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Crea la barra de estado inferior de la ventana.
     *
     * A la izquierda muestra el estado de la conexión a la BD
     * y a la derecha la fecha actual formateada.
     *
     * @return el panel de la barra de estado
     */
    private JPanel crearBarraEstado() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(new Color(10, 16, 30));
        barra.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, COLOR_BORDE),
                new EmptyBorder(6, 16, 6, 16)));

        lblEstado = new JLabel("Conectando a MySQL...");
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEstado.setForeground(COLOR_SUBTEXTO);

        // Mostramos la fecha del sistema en el lado derecho
        JLabel lblFecha = new JLabel(new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date()));
        lblFecha.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFecha.setForeground(COLOR_SUBTEXTO);

        barra.add(lblEstado, BorderLayout.WEST);
        barra.add(lblFecha,  BorderLayout.EAST);
        return barra;
    }

    /**
     * Crea un panel base con título grande para cualquier vista del área central.
     *
     * Todos los paneles de contenido usan este método como punto de partida
     * para garantizar un aspecto uniforme: fondo transparente, márgenes
     * consistentes y título en la parte superior.
     *
     * @param titulo texto que aparece como encabezado de la vista
     * @return el panel base listo para añadirle el contenido específico
     */
    private JPanel crearPanelBase(String titulo) {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(28, 28, 20, 28));
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(COLOR_TEXTO);
        lblTitulo.setBorder(new EmptyBorder(0, 0, 8, 0));
        panel.add(lblTitulo, BorderLayout.NORTH);
        return panel;
    }

    /**
     * Crea una "tarjeta" con fondo redondeado y borde sutil.
     *
     * Se usa para agrupar visualmente secciones dentro de un panel,
     * El fondo y el borde se pintan manualmente para conseguir esquinas
     * redondeadas sin depender de componentes externos..
     *
     * @param titulo texto que aparece como título de la tarjeta
     * @return el panel con aspecto de tarjeta
     */
    private JPanel crearCard(String titulo) {
        JPanel card = new JPanel(new BorderLayout(0, 12)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_PANEL);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel t = new JLabel(titulo);
        t.setFont(new Font("Segoe UI", Font.BOLD, 15));
        t.setForeground(COLOR_TEXTO);
        card.add(t, BorderLayout.NORTH);
        return card;
    }

    /**
     * Crea y configura una tabla con el estilo visual de la aplicación.
     *
     * Aplica colores alternados de fila elimina las líneas
     * verticales, configura el header con fuente pequeña en mayúsculas y
     * deshabilita el renderizado por defecto para tomar poder controlar todo el tema visual. 
     *
     * @param modelo el modelo por defecto que usará la tabla
     * @return la tabla estilizada
     */
    private JTable crearTabla(DefaultTableModel modelo) {
        JTable tabla = new JTable(modelo) {
            // Sobreescribimos prepareRenderer para aplicar colores de fila y selección
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                c.setBackground(isRowSelected(row) ? COLOR_SELECCION
                               : (row % 2 == 0 ? COLOR_FILA_PAR : COLOR_FILA_IMPAR));
                c.setForeground(COLOR_TEXTO);
                return c;
            }
        };
        tabla.setBackground(COLOR_FONDO);
        tabla.setForeground(COLOR_TEXTO);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setRowHeight(34);
        tabla.setGridColor(COLOR_BORDE);
        tabla.setShowHorizontalLines(true);
        tabla.setShowVerticalLines(false); // solo horizontales para un look más limpio
        tabla.setSelectionBackground(COLOR_SELECCION);
        tabla.setSelectionForeground(COLOR_TEXTO);
        tabla.setFillsViewportHeight(true);
        tabla.getTableHeader().setBackground(new Color(23, 33, 52));
        tabla.getTableHeader().setForeground(COLOR_SUBTEXTO);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabla.getTableHeader().setBorder(new MatteBorder(0, 0, 1, 0, COLOR_BORDE));
        return tabla;
    }

    /**
     * Crea un texto con el estilo visual estándar de la aplicación.
     *
     * @return el campo de texto estilizado
     */
    private JTextField campoTexto() {
        JTextField tf = new JTextField();
        estilizarCampo(tf);
        return tf;
    }

    /**
     * Aplica el estilo visual estándar a un texto existente.
     *
     * Se extrae como método separado para poder reutilizarlo 
     *
     * @param tf el campo de texto al que se aplica el estilo
     */
    private void estilizarCampo(JTextField tf) {
        tf.setBackground(new Color(51, 65, 85));
        tf.setForeground(COLOR_TEXTO);
        tf.setCaretColor(COLOR_ACENTO); // cursor en azul para que se vea bien
        tf.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDE, 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    }

    /**
     * Aplica el estilo visual estándar a un combo box .
     *
     * @param cb el combo box al que se aplica el estilo
     */
    private void estilizarCombo(JComboBox<?> cb) {
        cb.setBackground(COLOR_PANEL);
        cb.setForeground(COLOR_TEXTO);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    }

    /**
     * Crea un botón con fondo de color personalizado y efecto hover.
     *
     * El fondo se pinta manualmente con esquinas redondeadas.
     * Al pasar el ratón por encima, el color cambia a uno mas clarito.
     * @param texto texto que aparece en el botón
     * @param fondo color de fondo del botón
     * @return el boton estilizado
     */
    private JButton crearBoton(String texto, Color fondo) {
        JButton btn = new JButton(texto) {
            private boolean hover = false;
            { addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true;  repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hover ? fondo.brighter() : fondo);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        // Si el fondo es el color de panel , el texto va en claro; si no, en oscuro
        btn.setForeground(fondo.equals(COLOR_PANEL) ? COLOR_TEXTO : COLOR_FONDO);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        return btn;
    }

    /**
     * Solicita confirmación y vuelve a la pantalla de login cerrando esta ventana para optimizar los recursos loq ue hace que nuestra aplicacion sea mas ligera.
     */
    private void cerrarSesion() {
        int resp = JOptionPane.showConfirmDialog(this,
                "¿Cerrar sesión y volver al login?", "Cerrar sesión",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (resp == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }

    // metodos que cargan los combobox

    /**
     * Rellena un combo con todas las categorías de la tabla categorias
     * @param cb el combo a rellenar
     */
    private void cargarComboCategorias(JComboBox<String> cb) {
        cb.removeAllItems();
        try (Connection con = ConexionBD.getInstance().getConn();
             PreparedStatement ps = con.prepareStatement("SELECT nombre FROM categoria ORDER BY nombre");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) cb.addItem(rs.getString("nombre"));
        } catch (SQLException ex) { }
    }

    /**
     * Rellena un combo con todas las categorías más la opción
     *
     * @param cb el combo a rellenar
     */
    private void cargarComboCategoriasConTodos(JComboBox<String> cb) {
        cb.removeAllItems();
        cb.addItem("Todos"); // opción para no filtrar por categoría
        try (Connection con = ConexionBD.getInstance().getConn();
             PreparedStatement ps = con.prepareStatement("SELECT nombre FROM categoria ORDER BY nombre");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) cb.addItem(rs.getString("nombre"));
        } catch (SQLException ex) { }
    }

    /**
     * Rellena un combo con todos los estados disponibles en la tabla elementos_estados 
     *
     * @param cb el combo a rellenar
     */
    private void cargarComboEstados(JComboBox<String> cb) {
        cb.removeAllItems();
        try (Connection con = ConexionBD.getInstance().getConn();
             PreparedStatement ps = con.prepareStatement("SELECT nombre FROM estado_elemento ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) cb.addItem(rs.getString("nombre"));
        } catch (SQLException ex) {  }
    }

    /**
     * Rellena un combo con todos los estados
     *
     * @param cb el combo a rellenar
     */
    private void cargarComboEstadosConTodos(JComboBox<String> cb) {
        cb.removeAllItems();
        cb.addItem("Todos"); // opción para no filtrar por estado
        try (Connection con = ConexionBD.getInstance().getConn();
             PreparedStatement ps = con.prepareStatement("SELECT nombre FROM estado_elemento ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) cb.addItem(rs.getString("nombre"));
        } catch (SQLException ex) { }
    }


    /**
     * Rellena un combo con todas las ubicaciones de la tabla ubicacion
     * mostradas con el formato "armario - balda".
     *
     * @param cb el combo a rellenar
     */
    private void cargarComboUbicaciones(JComboBox<String> cb) {
        cb.removeAllItems();
        // CONCAT para mostrar "A1 - B2" en lugar de solo el ID
        String sql = "SELECT CONCAT(IFNULL(codigo_armario,''), ' - ', IFNULL(codigo_balda,'')) AS label " +
                     "FROM ubicacion ORDER BY id_ubicacion";
        try (Connection con = ConexionBD.getInstance().getConn();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) cb.addItem(rs.getString("label"));
        } catch (SQLException ex) { }
    }

    /**
     * Busca el ID numérico de un registro a partir de su nombre en una tabla genérica.
     *
     * Se usa para resolver los IDs de categoría, estado...
     *
     * @param tabla   nombre de la tabla donde buscar
     * @param campoId nombre de la columna que contiene el ID
     * @param nombre  valor del campo nombre a buscar
     * @return el ID encontrado
     * @throws SQLException si no se encuentra el nombre en la tabla o hay un error de BD
     */
    private int obtenerIdPorNombre(String tabla, String campoId, String nombre) throws SQLException {
        String sql = "SELECT " + campoId + " FROM " + tabla + " WHERE nombre = ?";
        try (Connection con = ConexionBD.getInstance().getConn();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        throw new SQLException("No se encontró '" + nombre + "' en " + tabla);
    }


    /**
     * Resuelve el id de la tabla ubicacion a partir del label "armario - balda"
     * que muestra el combo de ubicaciones.
     *
     * Separa el string por " - " para obtener los dos códigos y luego
     * busca la fila correspondiente en la tabla ubicacion
     *
     * @param label texto con formato "codigo_armario - codigo_balda"
     * @return el id de la ubicacion correspondiente
     * @throws SQLException si no se encuentra la ubicación o hay un error de BD
     */
    private int obtenerIdUbicacion(String label) throws SQLException {
        // Separamos el label "A1 - B2" en sus dos partes
        String[] partes = label.split(" - ", 2);
        String arm  = partes.length > 0 ? partes[0].trim() : "";
        String balda = partes.length > 1 ? partes[1].trim() : "";
        String sql =
            "SELECT id_ubicacion FROM ubicacion " +
            "WHERE IFNULL(codigo_armario,'') = ? AND IFNULL(codigo_balda,'') = ? LIMIT 1";
        try (Connection con = ConexionBD.getInstance().getConn();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, arm); ps.setString(2, balda);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        throw new SQLException("No se encontró la ubicación: " + label);
    }

    /**
     * Selecciona en un combo el ítem que coincide exactamente con el valor dado.
     *
     * Se usa al cargar los datos de un elemento en el formulario
     *
     * @param cb    el combo en el que buscar
     * @param valor el texto del ítem a seleccionar; no hace nada si es nulo
     */
    private void seleccionarEnCombo(JComboBox<String> cb, String valor) {
        if (valor == null) return;
        for (int i = 0; i < cb.getItemCount(); i++)
            if (cb.getItemAt(i).equals(valor)) { cb.setSelectedIndex(i); return; }
    }
}
