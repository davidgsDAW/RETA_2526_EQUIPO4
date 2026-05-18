import Conexion_Base_Datos.ConexionBD;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

/**
 * Ventana principal del perfil Administrador.
 * @author David Gómez
 * @version 3.0
 */
public class AdminFrame extends JFrame {

    // ── Paleta ─────────────────────────────────────────────────────────
    private static final Color COLOR_FONDO      = new Color(15, 23, 42);
    private static final Color COLOR_SIDEBAR    = new Color(23, 33, 52);
    private static final Color COLOR_PANEL      = new Color(30, 41, 59);
    private static final Color COLOR_ACENTO     = new Color(56, 189, 248);
    private static final Color COLOR_ACENTO2    = new Color(99, 102, 241);
    private static final Color COLOR_PELIGRO    = new Color(248, 113, 113);
    private static final Color COLOR_OK         = new Color(74, 222, 128);
    private static final Color COLOR_TEXTO      = new Color(226, 232, 240);
    private static final Color COLOR_SUBTEXTO   = new Color(148, 163, 184);
    private static final Color COLOR_BORDE      = new Color(51, 65, 85);
    private static final Color COLOR_FILA_PAR   = new Color(30, 41, 59);
    private static final Color COLOR_FILA_IMPAR = new Color(38, 51, 73);
    private static final Color COLOR_SELECCION  = new Color(56, 189, 248, 60);

    // Columnas de la tabla de inventario
    private static final String[] COLUMNAS = {
        "ID", "Nombre", "Categoría", "Estado", "Cantidad", "Armario", "Balda"
    };

    // ── Componentes ────────────────────────────────────────────────────
    private JPanel           contenidoCentral;
    private CardLayout       cardLayout;
    private JTable           tablaInventario;
    private DefaultTableModel modeloTabla;
    private JLabel           lblEstado;
    private final String     usuarioActual;
    private final int        idUsuarioActual; // id_usuario en BD

    // ──────────────────────────────────────────────────────────────────
    public AdminFrame(String usuario, int idUsuario) {
        this.usuarioActual   = usuario;
        this.idUsuarioActual = idUsuario;
        setTitle("Taller IES MHP · Panel Administrador — " + usuario);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_FONDO);
        setLayout(new BorderLayout(0, 0));

        add(crearSidebar(),          BorderLayout.WEST);
        add(crearContenidoCentral(), BorderLayout.CENTER);
        add(crearBarraEstado(),      BorderLayout.SOUTH);

        mostrarPanel("inventario");
    }

    // ── Sidebar ────────────────────────────────────────────────────────
    private JPanel crearSidebar() {
        JPanel sidebar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(COLOR_SIDEBAR);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(COLOR_BORDE);
                g2.fillRect(getWidth() - 1, 0, 1, getHeight());
                g2.dispose();
            }
        };
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

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

        sidebar.add(crearSeccion("INVENTARIO"));
        sidebar.add(crearItemMenu("📋", "Ver Inventario",      "inventario"));
        sidebar.add(crearItemMenu("➕", "Alta de Material",    "alta"));
        sidebar.add(crearItemMenu("✏️", "Modificar Material", "modificar"));
        sidebar.add(crearItemMenu("🗑", "Baja de Material",   "baja"));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(crearSeparadorSidebar());

        sidebar.add(crearSeccion("GESTIÓN"));
        sidebar.add(crearItemMenu("📦", "Préstamos",          "prestamos"));
        sidebar.add(crearItemMenu("👤", "Usuarios",           "usuarios"));
        sidebar.add(crearItemMenu("📊", "Historial",          "historial"));
        sidebar.add(crearItemMenu("📤", "Importar / Exportar","importar"));
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(crearSeparadorSidebar());

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

    private JSeparator crearSeparadorSidebar() {
        JSeparator sep = new JSeparator();
        sep.setForeground(COLOR_BORDE);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    private JLabel crearSeccion(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(COLOR_SUBTEXTO);
        lbl.setBorder(new EmptyBorder(12, 20, 4, 20));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JPanel crearItemMenu(String icono, String texto, String panelId) {
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

    // ── Contenido central ──────────────────────────────────────────────
    private JPanel crearContenidoCentral() {
        cardLayout      = new CardLayout();
        contenidoCentral = new JPanel(cardLayout);
        contenidoCentral.setOpaque(false);

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

    private void mostrarPanel(String id) { cardLayout.show(contenidoCentral, id); }
    
    
    private void cargarInventario(String nombre, String categoria, String estado) {
        modeloTabla.setRowCount(0);

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

    // ── Panel: Inventario ──────────────────────────────────────────────
    private JPanel crearPanelInventario() {
        JPanel panel = crearPanelBase("📋  Inventario del Taller");

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setOpaque(false);

        JTextField busqueda = new JTextField(20);
        estilizarCampo(busqueda);

        // Categorías desde la BD (cargadas dinámicamente)
        JComboBox<String> comboCat   = new JComboBox<>();
        JComboBox<String> comboEst   = new JComboBox<>();
        estilizarCombo(comboCat);
        estilizarCombo(comboEst);
        cargarComboCategoriasConTodos(comboCat);
        cargarComboEstadosConTodos(comboEst);

        JButton btnBuscar  = crearBoton("Buscar",       COLOR_ACENTO2);
        JButton btnRefresh = crearBoton("↻ Actualizar", COLOR_PANEL);

        btnBuscar.addActionListener(e ->
            cargarInventario(busqueda.getText().trim(),
                             comboCat.getSelectedItem().toString(),
                             comboEst.getSelectedItem().toString()));
        btnRefresh.addActionListener(e -> {
            busqueda.setText("");
            comboCat.setSelectedIndex(0);
            comboEst.setSelectedIndex(0);
            cargarInventario(null, null, null);
        });

        toolbar.add(new JLabel("🔍") {{ setForeground(COLOR_SUBTEXTO); }});
        toolbar.add(busqueda);
        toolbar.add(new JLabel("Cat:") {{ setForeground(COLOR_SUBTEXTO); setFont(new Font("Segoe UI", Font.PLAIN, 12)); }});
        toolbar.add(comboCat);
        toolbar.add(new JLabel("Estado:") {{ setForeground(COLOR_SUBTEXTO); setFont(new Font("Segoe UI", Font.PLAIN, 12)); }});
        toolbar.add(comboEst);
        toolbar.add(btnBuscar);
        toolbar.add(btnRefresh);

        modeloTabla     = new DefaultTableModel(COLUMNAS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaInventario = crearTabla(modeloTabla);

        JScrollPane scroll = new JScrollPane(tablaInventario);
        scroll.setOpaque(false);
        scroll.getViewport().setBackground(COLOR_FONDO);
        scroll.setBorder(new LineBorder(COLOR_BORDE, 1, true));

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acciones.setOpaque(false);
        JButton btnAnadir   = crearBoton("➕ Añadir",   COLOR_OK);
        JButton btnEditar   = crearBoton("✏️ Editar",  COLOR_ACENTO);
        JButton btnEliminar = crearBoton("🗑 Eliminar", COLOR_PELIGRO);

        btnAnadir.addActionListener(e -> mostrarPanel("alta"));
        btnEditar.addActionListener(e -> mostrarPanel("modificar"));
        btnEliminar.addActionListener(e -> {
            int fila = tablaInventario.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(this, "Selecciona un elemento primero.",
                    "Sin selección", JOptionPane.INFORMATION_MESSAGE); return;
            }
            String id = modeloTabla.getValueAt(fila, 0).toString();
            darDeBajaMaterial(Integer.parseInt(id));
        });

        acciones.add(btnAnadir);
        acciones.add(btnEditar);
        acciones.add(btnEliminar);

        panel.add(toolbar,  BorderLayout.NORTH);
        panel.add(scroll,   BorderLayout.CENTER);
        panel.add(acciones, BorderLayout.SOUTH);

        SwingUtilities.invokeLater(() -> cargarInventario(null, null, null));
        return panel;
    }

    // ── Panel: Alta ────────────────────────────────────────────────────
    private JPanel crearPanelAlta() {
        JPanel panel = crearPanelBase("➕  Alta de Material");

        JTextField tfNombre   = campoTexto();
        JTextField tfDesc     = campoTexto();
        JTextField tfCantidad = campoTexto();
        JTextField tfObs      = campoTexto();

        // Combos con datos reales de la BD
        JComboBox<String> cbCat  = new JComboBox<>();
        JComboBox<String> cbEst  = new JComboBox<>();
        JComboBox<String> cbArm  = new JComboBox<>(); // ubicaciones disponibles
        estilizarCombo(cbCat); estilizarCombo(cbEst); estilizarCombo(cbArm);
        cargarComboCategorias(cbCat);
        cargarComboEstados(cbEst);
        cargarComboUbicaciones(cbArm);

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
        JButton btnCancelar  = crearBoton("Cancelar",              COLOR_PANEL);
        JButton btnRegistrar = crearBoton("✓ Registrar elemento",  COLOR_OK);

        btnCancelar.addActionListener(e -> mostrarPanel("inventario"));
        btnRegistrar.addActionListener(e -> {
            String nombre   = tfNombre.getText().trim();
            String cantidad = tfCantidad.getText().trim();
            if (nombre.isEmpty() || cantidad.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Nombre y cantidad son obligatorios.", "Campos requeridos",
                    JOptionPane.WARNING_MESSAGE); return;
            }
            try {
                int cant = Integer.parseInt(cantidad);
                int idCat = obtenerIdPorNombre("categoria",      "id_categoria", cbCat.getSelectedItem().toString());
                int idEst = obtenerIdPorNombre("estado_elemento","id",           cbEst.getSelectedItem().toString());
                int idUbic= obtenerIdUbicacion(cbArm.getSelectedItem().toString());
                insertarMaterial(nombre, tfDesc.getText(), cant, idCat, idEst, idUbic, tfObs.getText());
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
            cargarInventario(null, null, null);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error al insertar en la BD:\n" + ex.getMessage(),
                "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Panel: Modificar ───────────────────────────────────────────────
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

        final int[] idReal = {-1};

        JButton btnBuscar = crearBoton("Buscar", COLOR_ACENTO2);
        btnBuscar.addActionListener(e -> {
            String idStr = tfId.getText().trim();
            if (idStr.isEmpty()) return;
            try {
                int id = Integer.parseInt(idStr);
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

        JPanel busq = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        busq.setOpaque(false);
        busq.add(new JLabel("Buscar por ID:") {{ setForeground(COLOR_SUBTEXTO); setFont(new Font("Segoe UI", Font.PLAIN, 13)); }});
        busq.add(tfId);
        busq.add(btnBuscar);

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
            cargarInventario(null, null, null);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error al actualizar en la BD:\n" + ex.getMessage(),
                "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }


    private JPanel crearPanelBaja() {
        JPanel panel = crearPanelBase("🗑  Baja de Material");

        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(new EmptyBorder(20, 0, 0, 0));

        JTextField tfId = new JTextField(12);
        estilizarCampo(tfId);

        JLabel[] lblsVal = new JLabel[5];
        String[] etiq    = {"Nombre:", "Categoría:", "Estado:", "Cantidad:", "Ubicación:"};
        JPanel infoGrid  = new JPanel(new GridLayout(5, 2, 8, 8));
        infoGrid.setOpaque(false);
        for (int i = 0; i < 5; i++) {
            JLabel e = new JLabel(etiq[i]); e.setForeground(COLOR_SUBTEXTO); e.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lblsVal[i] = new JLabel("—"); lblsVal[i].setForeground(COLOR_TEXTO); lblsVal[i].setFont(new Font("Segoe UI", Font.BOLD, 13));
            infoGrid.add(e); infoGrid.add(lblsVal[i]);
        }

        final int[] idReal = {-1};

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
                        for (JLabel l : lblsVal) l.setText("—");
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
            int confirm = JOptionPane.showConfirmDialog(this,
                "¿Confirmas la baja de: " + lblsVal[0].getText() + "?\nEl estado cambiará a BAJA.",
                "Confirmar baja", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                darDeBajaMaterial(idReal[0]);
                idReal[0] = -1;
                for (JLabel l : lblsVal) l.setText("—");
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
     * Cambia el estado del material a BAJA (id=4 según los INSERTs del script).
     * No elimina físicamente el registro, respetando la integridad referencial
     * (préstamos e historial).
     */
    private void darDeBajaMaterial(int id) {
        String sql = "UPDATE material SET id_estado = " +
                     "(SELECT id FROM estado_elemento WHERE nombre='BAJA') " +
                     "WHERE id_material = ?";
        try (Connection con = ConexionBD.getInstance().getConn();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Elemento dado de baja correctamente.",
                "Baja exitosa", JOptionPane.INFORMATION_MESSAGE);
            cargarInventario(null, null, null);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error al dar de baja:\n" + ex.getMessage(),
                "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Panel: Préstamos ───────────────────────────────────────────────
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

    private void cargarPrestamos(DefaultTableModel modelo) {
        modelo.setRowCount(0);
        String sql =
            "SELECT p.id_prestamo, m.nombre AS material, u.nombre AS usuario, " +
            "       p.fecha_prestamo, p.fecha_devolucion, " +
            "       IF(p.fecha_devolucion IS NULL, 'ACTIVO','DEVUELTO') AS estado_pres " +
            "FROM prestamo p " +
            "JOIN material m ON m.id_material = p.id_material " +
            "JOIN usuario  u ON u.id_usuario  = p.id_usuario " +
            "ORDER BY p.id_prestamo DESC";
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
                cargarInventario(null, null, null);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Los IDs deben ser números enteros.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al registrar préstamo:\n" + ex.getMessage(),
                "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrarDevolucion(int idPrestamo, DefaultTableModel modeloP) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Registrar la devolución del préstamo #" + idPrestamo + "?",
            "Confirmar devolución", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

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

    // ── Panel: Usuarios ────────────────────────────────────────────────
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
        JButton btnEditar  = crearBoton("✏️ Editar",       COLOR_ACENTO);
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
        botones.add(btnEditar);
        botones.add(btnElim);
        botones.add(btnRefresh);

        panel.add(scroll,  BorderLayout.CENTER);
        panel.add(botones, BorderLayout.SOUTH);

        SwingUtilities.invokeLater(() -> cargarUsuarios(modeloU));
        return panel;
    }

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

    // ── Panel: Historial ───────────────────────────────────────────────
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

    // ── Panel: Importar/Exportar ───────────────────────────────────────
    private JPanel crearPanelImportar() {
        JPanel panel = crearPanelBase("📤  Importar / Exportar Datos");

        JPanel contenido = new JPanel(new GridLayout(1, 2, 16, 0));
        contenido.setOpaque(false);
        contenido.setBorder(new EmptyBorder(16, 0, 0, 0));

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

    // ── Barra de estado ────────────────────────────────────────────────
    private JPanel crearBarraEstado() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(new Color(10, 16, 30));
        barra.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, COLOR_BORDE),
                new EmptyBorder(6, 16, 6, 16)));

        lblEstado = new JLabel("Conectando a MySQL...");
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEstado.setForeground(COLOR_SUBTEXTO);

        JLabel lblFecha = new JLabel(new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date()));
        lblFecha.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFecha.setForeground(COLOR_SUBTEXTO);

        barra.add(lblEstado, BorderLayout.WEST);
        barra.add(lblFecha,  BorderLayout.EAST);
        return barra;
    }

    // ── Helpers UI ─────────────────────────────────────────────────────
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

    private JTable crearTabla(DefaultTableModel modelo) {
        JTable tabla = new JTable(modelo) {
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
        tabla.setShowVerticalLines(false);
        tabla.setSelectionBackground(COLOR_SELECCION);
        tabla.setSelectionForeground(COLOR_TEXTO);
        tabla.setFillsViewportHeight(true);
        tabla.getTableHeader().setBackground(new Color(23, 33, 52));
        tabla.getTableHeader().setForeground(COLOR_SUBTEXTO);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabla.getTableHeader().setBorder(new MatteBorder(0, 0, 1, 0, COLOR_BORDE));
        return tabla;
    }

    private JTextField campoTexto() {
        JTextField tf = new JTextField();
        estilizarCampo(tf);
        return tf;
    }

    private void estilizarCampo(JTextField tf) {
        tf.setBackground(new Color(51, 65, 85));
        tf.setForeground(COLOR_TEXTO);
        tf.setCaretColor(COLOR_ACENTO);
        tf.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDE, 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    }

    private void estilizarCombo(JComboBox<?> cb) {
        cb.setBackground(COLOR_PANEL);
        cb.setForeground(COLOR_TEXTO);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    }

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
        btn.setForeground(fondo.equals(COLOR_PANEL) ? COLOR_TEXTO : COLOR_FONDO);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        return btn;
    }

    private void cerrarSesion() {
        int resp = JOptionPane.showConfirmDialog(this,
                "¿Cerrar sesión y volver al login?", "Cerrar sesión",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (resp == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }

    // ── Helpers BD: cargar combos ──────────────────────────────────────
    private void cargarComboCategorias(JComboBox<String> cb) {
        cb.removeAllItems();
        try (Connection con = ConexionBD.getInstance().getConn();
             PreparedStatement ps = con.prepareStatement("SELECT nombre FROM categoria ORDER BY nombre");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) cb.addItem(rs.getString("nombre"));
        } catch (SQLException ex) { /* combo queda vacío */ }
    }

    private void cargarComboCategoriasConTodos(JComboBox<String> cb) {
        cb.removeAllItems();
        cb.addItem("Todos");
        try (Connection con = ConexionBD.getInstance().getConn();
             PreparedStatement ps = con.prepareStatement("SELECT nombre FROM categoria ORDER BY nombre");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) cb.addItem(rs.getString("nombre"));
        } catch (SQLException ex) { /* ignora */ }
    }

    private void cargarComboEstados(JComboBox<String> cb) {
        cb.removeAllItems();
        try (Connection con = ConexionBD.getInstance().getConn();
             PreparedStatement ps = con.prepareStatement("SELECT nombre FROM estado_elemento ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) cb.addItem(rs.getString("nombre"));
        } catch (SQLException ex) { /* combo queda vacío */ }
    }

    private void cargarComboEstadosConTodos(JComboBox<String> cb) {
        cb.removeAllItems();
        cb.addItem("Todos");
        try (Connection con = ConexionBD.getInstance().getConn();
             PreparedStatement ps = con.prepareStatement("SELECT nombre FROM estado_elemento ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) cb.addItem(rs.getString("nombre"));
        } catch (SQLException ex) { /* ignora */ }
    }

    /** Carga las ubicaciones en el combo con etiqueta "armario - balda". */
    private void cargarComboUbicaciones(JComboBox<String> cb) {
        cb.removeAllItems();
        String sql = "SELECT CONCAT(IFNULL(codigo_armario,''), ' - ', IFNULL(codigo_balda,'')) AS label " +
                     "FROM ubicacion ORDER BY id_ubicacion";
        try (Connection con = ConexionBD.getInstance().getConn();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) cb.addItem(rs.getString("label"));
        } catch (SQLException ex) { /* ignora */ }
    }

    /** Devuelve el id numérico de una tabla de catálogo buscando por nombre. */
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

    /** Devuelve id_ubicacion a partir de la etiqueta "armario - balda". */
    private int obtenerIdUbicacion(String label) throws SQLException {
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

    private void seleccionarEnCombo(JComboBox<String> cb, String valor) {
        if (valor == null) return;
        for (int i = 0; i < cb.getItemCount(); i++)
            if (cb.getItemAt(i).equals(valor)) { cb.setSelectedIndex(i); return; }
    }
}
