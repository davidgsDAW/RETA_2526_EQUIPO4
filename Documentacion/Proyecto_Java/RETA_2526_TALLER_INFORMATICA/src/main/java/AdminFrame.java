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
 * Permite gestión completa del inventario: alta, baja, modificación,
 * consulta, gestión de usuarios e informes.
 * Los datos se cargan desde la base de datos MySQL (tabla: material).
 *
 * Estructura esperada:
 *   CREATE TABLE material (
 *       id          INT AUTO_INCREMENT PRIMARY KEY,
 *       nombre      VARCHAR(150) NOT NULL,
 *       categoria   VARCHAR(100),
 *       estado      ENUM('Operativo','Averiado','En reparación','Obsoleto') DEFAULT 'Operativo',
 *       cantidad    INT DEFAULT 0,
 *       armario     VARCHAR(20),
 *       balda       VARCHAR(20),
 *       descripcion TEXT,
 *       observaciones TEXT
 *   );
 *
 * @author IES Miguel Herrero Pereda - DAW 2025/2026
 * @version 2.0
 */
public class AdminFrame extends JFrame {

    // ── Paleta ─────────────────────────────────────────────────────────
    private static final Color COLOR_FONDO       = new Color(15, 23, 42);
    private static final Color COLOR_SIDEBAR     = new Color(23, 33, 52);
    private static final Color COLOR_PANEL       = new Color(30, 41, 59);
    private static final Color COLOR_ACENTO      = new Color(56, 189, 248);
    private static final Color COLOR_ACENTO2     = new Color(99, 102, 241);
    private static final Color COLOR_PELIGRO     = new Color(248, 113, 113);
    private static final Color COLOR_OK          = new Color(74, 222, 128);
    private static final Color COLOR_TEXTO       = new Color(226, 232, 240);
    private static final Color COLOR_SUBTEXTO    = new Color(148, 163, 184);
    private static final Color COLOR_BORDE       = new Color(51, 65, 85);
    private static final Color COLOR_FILA_PAR    = new Color(30, 41, 59);
    private static final Color COLOR_FILA_IMPAR  = new Color(38, 51, 73);
    private static final Color COLOR_SELECCION   = new Color(56, 189, 248, 60);

    private static final String[] COLUMNAS = {
        "ID", "Nombre", "Categoría", "Estado", "Cantidad", "Armario", "Balda"
    };

    // ── Componentes ────────────────────────────────────────────────────
    private JPanel        contenidoCentral;
    private CardLayout    cardLayout;
    private JTable        tablaInventario;
    private DefaultTableModel modeloTabla;
    private JLabel        lblEstado;
    private String        usuarioActual;

    // ──────────────────────────────────────────────────────────────────
    public AdminFrame(String usuario) {
        this.usuarioActual = usuario;
        setTitle("Taller IES MHP · Panel Administrador — " + usuario);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_FONDO);
        setLayout(new BorderLayout(0, 0));

        add(crearSidebar(), BorderLayout.WEST);
        add(crearContenidoCentral(), BorderLayout.CENTER);
        add(crearBarraEstado(), BorderLayout.SOUTH);

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
        sidebar.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel cabSidebar = new JPanel();
        cabSidebar.setOpaque(false);
        cabSidebar.setLayout(new BoxLayout(cabSidebar, BoxLayout.Y_AXIS));
        cabSidebar.setBorder(new EmptyBorder(28, 20, 20, 20));
        cabSidebar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JLabel badgeAdmin = new JLabel("ADMINISTRADOR");
        badgeAdmin.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badgeAdmin.setForeground(COLOR_ACENTO);
        badgeAdmin.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblNombre = new JLabel(usuarioActual);
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblNombre.setForeground(COLOR_TEXTO);
        lblNombre.setAlignmentX(Component.LEFT_ALIGNMENT);

        cabSidebar.add(badgeAdmin);
        cabSidebar.add(Box.createVerticalStrut(4));
        cabSidebar.add(lblNombre);
        sidebar.add(cabSidebar);

        sidebar.add(crearSeparadorSidebar());

        sidebar.add(crearSeccion("INVENTARIO"));
        sidebar.add(crearItemMenu("📋", "Ver Inventario",      "inventario"));
        sidebar.add(crearItemMenu("➕", "Alta de Material",    "alta"));
        sidebar.add(crearItemMenu("✏️", "Modificar Material", "modificar"));
        sidebar.add(crearItemMenu("🗑", "Baja de Material",   "baja"));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(crearSeparadorSidebar());
        sidebar.add(crearSeccion("GESTIÓN"));
        sidebar.add(crearItemMenu("👤", "Gestión de Usuarios", "usuarios"));
        sidebar.add(crearItemMenu("📊", "Informes",            "informes"));
        sidebar.add(crearItemMenu("📤", "Importar / Exportar", "importar"));
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
                    @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
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

    // ── Contenido central (CardLayout) ─────────────────────────────────
    private JPanel crearContenidoCentral() {
        cardLayout = new CardLayout();
        contenidoCentral = new JPanel(cardLayout);
        contenidoCentral.setOpaque(false);

        contenidoCentral.add(crearPanelInventario(), "inventario");
        contenidoCentral.add(crearPanelAlta(),       "alta");
        contenidoCentral.add(crearPanelModificar(),  "modificar");
        contenidoCentral.add(crearPanelBaja(),       "baja");
        contenidoCentral.add(crearPanelUsuarios(),   "usuarios");
        contenidoCentral.add(crearPanelInformes(),   "informes");
        contenidoCentral.add(crearPanelImportar(),   "importar");

        return contenidoCentral;
    }

    private void mostrarPanel(String id) {
        cardLayout.show(contenidoCentral, id);
    }

    // ── Carga de datos desde la BD ─────────────────────────────────────

    /**
     * Carga todos los elementos del inventario desde la BD y rellena el modelo de tabla.
     * Acepta filtros opcionales de nombre, categoría y estado.
     */
    private void cargarInventario(String nombre, String categoria, String estado) {
        modeloTabla.setRowCount(0);

        StringBuilder sql = new StringBuilder(
            "SELECT id, nombre, categoria, estado, cantidad, armario, balda FROM material WHERE 1=1");
        if (nombre != null && !nombre.isEmpty())
            sql.append(" AND LOWER(nombre) LIKE ?");
        if (categoria != null && !categoria.equals("Todos"))
            sql.append(" AND categoria = ?");
        if (estado != null && !estado.equals("Todos"))
            sql.append(" AND estado = ?");
        sql.append(" ORDER BY id");

        try (Connection con = ConexionBD.getInstance().getConn();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int idx = 1;
            if (nombre != null && !nombre.isEmpty())
                ps.setString(idx++, "%" + nombre.toLowerCase() + "%");
            if (categoria != null && !categoria.equals("Todos"))
                ps.setString(idx++, categoria);
            if (estado != null && !estado.equals("Todos"))
                ps.setString(idx, estado);

            ResultSet rs = ps.executeQuery();
            int total = 0;
            while (rs.next()) {
                modeloTabla.addRow(new Object[]{
                    String.format("%03d", rs.getInt("id")),
                    rs.getString("nombre"),
                    rs.getString("categoria"),
                    rs.getString("estado"),
                    rs.getString("cantidad"),
                    rs.getString("armario"),
                    rs.getString("balda")
                });
                total++;
            }
            lblEstado.setText("✓  Conectado a MySQL · " + total + " elementos · BD: taller_mhp");

        } catch (SQLException ex) {
            lblEstado.setText("✗  Error al conectar con la BD: " + ex.getMessage());
            JOptionPane.showMessageDialog(this,
                "Error al cargar el inventario:\n" + ex.getMessage(),
                "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Panel: Inventario ──────────────────────────────────────────────
    private JPanel crearPanelInventario() {
        JPanel panel = crearPanelBase("📋  Inventario del Taller");

        // Barra de herramientas
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setOpaque(false);

        JTextField busqueda = new JTextField(20);
        busqueda.setBackground(COLOR_PANEL);
        busqueda.setForeground(COLOR_TEXTO);
        busqueda.setCaretColor(COLOR_ACENTO);
        busqueda.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDE, 1, true), new EmptyBorder(6, 10, 6, 10)));
        busqueda.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        String[] filtros = {"Todos", "PC Prácticas", "Componentes HW", "Equipos de Red",
                            "Herramientas", "Material Fungible", "Cableado Estructurado"};
        JComboBox<String> combo = new JComboBox<>(filtros);
        combo.setBackground(COLOR_PANEL);
        combo.setForeground(COLOR_TEXTO);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JButton btnBuscar  = crearBoton("Buscar", COLOR_ACENTO2);
        JButton btnRefresh = crearBoton("↻ Actualizar", COLOR_PANEL);

        // Búsqueda filtrada
        btnBuscar.addActionListener(e ->
            cargarInventario(busqueda.getText().trim(),
                             combo.getSelectedItem().toString(), "Todos"));

        // Recargar todo el inventario
        btnRefresh.addActionListener(e -> {
            busqueda.setText("");
            combo.setSelectedIndex(0);
            cargarInventario(null, null, null);
        });

        toolbar.add(new JLabel("🔍") {{ setForeground(COLOR_SUBTEXTO); }});
        toolbar.add(busqueda);
        toolbar.add(combo);
        toolbar.add(btnBuscar);
        toolbar.add(btnRefresh);

        // Tabla
        modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaInventario = crearTabla(modeloTabla);

        JScrollPane scroll = new JScrollPane(tablaInventario);
        scroll.setOpaque(false);
        scroll.getViewport().setBackground(COLOR_FONDO);
        scroll.setBorder(new LineBorder(COLOR_BORDE, 1, true));

        // Botones de acción rápida
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
                    "Sin selección", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String id = modeloTabla.getValueAt(fila, 0).toString();
            eliminarMaterial(Integer.parseInt(id));
        });

        acciones.add(btnAnadir);
        acciones.add(btnEditar);
        acciones.add(btnEliminar);

        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(scroll,  BorderLayout.CENTER);
        panel.add(acciones, BorderLayout.SOUTH);

        // Cargar datos al mostrar el panel
        SwingUtilities.invokeLater(() -> cargarInventario(null, null, null));

        return panel;
    }

    // ── Panel: Alta ────────────────────────────────────────────────────
    private JPanel crearPanelAlta() {
        JPanel panel = crearPanelBase("➕  Alta de Material");

        // Campos del formulario
        JTextField tfNombre  = campoTexto();
        JTextField tfDesc    = campoTexto();
        JTextField tfCantidad = campoTexto();
        JTextField tfArmario = campoTexto();
        JTextField tfBalda   = campoTexto();
        JTextField tfObs     = campoTexto();

        String[] cats = {"PC Prácticas","Componentes HW","Equipos de Red",
                         "Herramientas","Material Fungible","Cableado Estructurado"};
        String[] estados = {"Operativo","Averiado","En reparación","Obsoleto"};
        JComboBox<String> cbCat    = new JComboBox<>(cats);
        JComboBox<String> cbEstado = new JComboBox<>(estados);
        estilizarCombo(cbCat);
        estilizarCombo(cbEstado);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 12, 12);

        Object[][] filas = {
            {"Nombre del elemento", tfNombre},   {"Descripción",       tfDesc},
            {"Categoría",           cbCat},       {"Estado",            cbEstado},
            {"Cantidad",            tfCantidad},  {"Código de Armario", tfArmario},
            {"Balda",               tfBalda},     {"Observaciones",     tfObs},
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

            col++;
            if (col >= 2) { col = 0; fila++; }
        }

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        JButton btnCancelar  = crearBoton("Cancelar", COLOR_PANEL);
        JButton btnRegistrar = crearBoton("✓ Registrar elemento", COLOR_OK);

        btnCancelar.addActionListener(e -> mostrarPanel("inventario"));
        btnRegistrar.addActionListener(e -> {
            String nombre   = tfNombre.getText().trim();
            String cantidad = tfCantidad.getText().trim();
            if (nombre.isEmpty() || cantidad.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Nombre y cantidad son obligatorios.", "Campos requeridos",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                int cant = Integer.parseInt(cantidad);
                insertarMaterial(nombre, tfDesc.getText(), cbCat.getSelectedItem().toString(),
                                 cbEstado.getSelectedItem().toString(), cant,
                                 tfArmario.getText(), tfBalda.getText(), tfObs.getText());
                // Limpiar campos
                tfNombre.setText(""); tfDesc.setText(""); tfCantidad.setText("");
                tfArmario.setText(""); tfBalda.setText(""); tfObs.setText("");
                cbCat.setSelectedIndex(0); cbEstado.setSelectedIndex(0);
                mostrarPanel("inventario");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                    "La cantidad debe ser un número entero.", "Dato inválido",
                    JOptionPane.WARNING_MESSAGE);
            }
        });

        botones.add(btnCancelar);
        botones.add(btnRegistrar);

        panel.add(form,    BorderLayout.CENTER);
        panel.add(botones, BorderLayout.SOUTH);
        return panel;
    }

    /** Inserta un nuevo elemento en la tabla material. */
    private void insertarMaterial(String nombre, String desc, String cat, String estado,
                                  int cantidad, String armario, String balda, String obs) {
        String sql = "INSERT INTO material (nombre, descripcion, categoria, estado, cantidad, armario, balda, observaciones) "
                   + "VALUES (?,?,?,?,?,?,?,?)";
        try (Connection con = ConexionBD.getInstance().getConn();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, desc);
            ps.setString(3, cat);
            ps.setString(4, estado);
            ps.setInt(5, cantidad);
            ps.setString(6, armario);
            ps.setString(7, balda);
            ps.setString(8, obs);
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
        tfId.setBackground(COLOR_PANEL); tfId.setForeground(COLOR_TEXTO);
        tfId.setBorder(new CompoundBorder(new LineBorder(COLOR_BORDE, 1, true), new EmptyBorder(6, 10, 6, 10)));

        // Campos editables
        JTextField tfNombre   = campoTexto();
        JTextField tfDesc     = campoTexto();
        JTextField tfCantidad = campoTexto();
        JTextField tfArmario  = campoTexto();
        JTextField tfBalda    = campoTexto();
        JTextField tfObs      = campoTexto();
        String[] cats    = {"PC Prácticas","Componentes HW","Equipos de Red","Herramientas","Material Fungible","Cableado Estructurado"};
        String[] estados = {"Operativo","Averiado","En reparación","Obsoleto"};
        JComboBox<String> cbCat    = new JComboBox<>(cats);
        JComboBox<String> cbEstado = new JComboBox<>(estados);
        estilizarCombo(cbCat);
        estilizarCombo(cbEstado);

        // Referencia al ID real de BD (no el formateado de pantalla)
        final int[] idReal = {-1};

        JButton btnBuscar = crearBoton("Buscar", COLOR_ACENTO2);
        btnBuscar.addActionListener(e -> {
            String idStr = tfId.getText().trim();
            if (idStr.isEmpty()) return;
            try {
                int id = Integer.parseInt(idStr);
                String sql = "SELECT * FROM material WHERE id = ?";
                try (Connection con = ConexionBD.getInstance().getConn();
                     PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, id);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        idReal[0] = rs.getInt("id");
                        tfNombre.setText(rs.getString("nombre"));
                        tfDesc.setText(rs.getString("descripcion") != null ? rs.getString("descripcion") : "");
                        tfCantidad.setText(String.valueOf(rs.getInt("cantidad")));
                        tfArmario.setText(rs.getString("armario") != null ? rs.getString("armario") : "");
                        tfBalda.setText(rs.getString("balda") != null ? rs.getString("balda") : "");
                        tfObs.setText(rs.getString("observaciones") != null ? rs.getString("observaciones") : "");
                        // Seleccionar categoría y estado en los combos
                        String cat = rs.getString("categoria");
                        for (int i = 0; i < cbCat.getItemCount(); i++)
                            if (cbCat.getItemAt(i).equals(cat)) { cbCat.setSelectedIndex(i); break; }
                        String est = rs.getString("estado");
                        for (int i = 0; i < cbEstado.getItemCount(); i++)
                            if (cbEstado.getItemAt(i).equals(est)) { cbEstado.setSelectedIndex(i); break; }
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
            {"Nombre del elemento", tfNombre},   {"Descripción",       tfDesc},
            {"Categoría",           cbCat},       {"Estado",            cbEstado},
            {"Cantidad",            tfCantidad},  {"Código de Armario", tfArmario},
            {"Balda",               tfBalda},     {"Observaciones",     tfObs},
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
            col++;
            if (col >= 2) { col = 0; fila++; }
        }

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        JButton btnCancelar = crearBoton("Cancelar", COLOR_PANEL);
        JButton btnGuardar  = crearBoton("✓ Guardar cambios", COLOR_ACENTO);

        btnCancelar.addActionListener(e -> mostrarPanel("inventario"));
        btnGuardar.addActionListener(e -> {
            if (idReal[0] < 0) {
                JOptionPane.showMessageDialog(this, "Busca un elemento primero.", "Sin selección", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                int cant = Integer.parseInt(tfCantidad.getText().trim());
                actualizarMaterial(idReal[0], tfNombre.getText(), tfDesc.getText(),
                                   cbCat.getSelectedItem().toString(),
                                   cbEstado.getSelectedItem().toString(),
                                   cant, tfArmario.getText(), tfBalda.getText(), tfObs.getText());
                mostrarPanel("inventario");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "La cantidad debe ser un número entero.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
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

    /** Actualiza un elemento existente en la BD. */
    private void actualizarMaterial(int id, String nombre, String desc, String cat,
                                    String estado, int cantidad, String armario,
                                    String balda, String obs) {
        String sql = "UPDATE material SET nombre=?, descripcion=?, categoria=?, estado=?, "
                   + "cantidad=?, armario=?, balda=?, observaciones=? WHERE id=?";
        try (Connection con = ConexionBD.getInstance().getConn()
                ;
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, desc);
            ps.setString(3, cat);
            ps.setString(4, estado);
            ps.setInt(5, cantidad);
            ps.setString(6, armario);
            ps.setString(7, balda);
            ps.setString(8, obs);
            ps.setInt(9, id);
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

    // ── Panel: Baja ────────────────────────────────────────────────────
    private JPanel crearPanelBaja() {
        JPanel panel = crearPanelBase("🗑  Baja de Material");

        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(new EmptyBorder(20, 0, 0, 0));

        JTextField tfId = new JTextField(12);
        tfId.setBackground(COLOR_PANEL); tfId.setForeground(COLOR_TEXTO);
        tfId.setBorder(new CompoundBorder(new LineBorder(COLOR_BORDE, 1, true), new EmptyBorder(6, 10, 6, 10)));

        // Info del elemento buscado
        JLabel[] lblsVal = new JLabel[5];
        String[] etiq = {"Nombre:", "Categoría:", "Estado:", "Cantidad:", "Ubicación:"};
        JPanel infoGrid = new JPanel(new GridLayout(5, 2, 8, 8));
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
                String sql = "SELECT * FROM material WHERE id = ?";
                try (Connection con = ConexionBD.getInstance().getConn();
                     PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, id);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        idReal[0] = rs.getInt("id");
                        lblsVal[0].setText(rs.getString("nombre"));
                        lblsVal[1].setText(rs.getString("categoria"));
                        lblsVal[2].setText(rs.getString("estado"));
                        lblsVal[3].setText(String.valueOf(rs.getInt("cantidad")));
                        lblsVal[4].setText("Armario " + rs.getString("armario") + " · Balda " + rs.getString("balda"));
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

        JPanel preview = crearCard("Vista previa del elemento a eliminar");
        preview.add(infoGrid, BorderLayout.CENTER);

        JPanel aviso = new JPanel(new FlowLayout(FlowLayout.LEFT));
        aviso.setOpaque(false);
        JLabel warn = new JLabel("⚠  Esta acción no se puede deshacer. El elemento quedará registrado en el histórico.");
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
        JButton btnCancelar = crearBoton("Cancelar", COLOR_PANEL);
        JButton btnConfirmar = crearBoton("🗑 Confirmar baja", COLOR_PELIGRO);
        btnCancelar.addActionListener(e -> mostrarPanel("inventario"));
        btnConfirmar.addActionListener(e -> {
            if (idReal[0] < 0) {
                JOptionPane.showMessageDialog(this, "Busca un elemento primero.", "Sin selección", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this,
                "¿Confirmas la baja de: " + lblsVal[0].getText() + "?\nEsta acción no se puede deshacer.",
                "Confirmar baja", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                eliminarMaterial(idReal[0]);
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

    /** Elimina un elemento del inventario por su ID. */
    private void eliminarMaterial(int id) {
        String sql = "DELETE FROM material WHERE id = ?";
        try (Connection con = ConexionBD.getInstance().getConn();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Elemento eliminado correctamente.",
                "Baja exitosa", JOptionPane.INFORMATION_MESSAGE);
            cargarInventario(null, null, null);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error al eliminar de la BD:\n" + ex.getMessage(),
                "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Panel: Usuarios ────────────────────────────────────────────────
    private JPanel crearPanelUsuarios() {
        JPanel panel = crearPanelBase("👤  Gestión de Usuarios");

        String[] cols = {"ID", "Usuario", "Nombre completo", "Rol", "Estado", "Último acceso"};
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
        botones.add(crearBoton("➕ Nuevo usuario", COLOR_OK));
        botones.add(crearBoton("✏️ Editar",       COLOR_ACENTO));
        botones.add(crearBoton("🔒 Desactivar",   COLOR_PELIGRO));

        panel.add(scroll,   BorderLayout.CENTER);
        panel.add(botones,  BorderLayout.SOUTH);

        // Cargar usuarios desde la BD al mostrar el panel
        SwingUtilities.invokeLater(() -> cargarUsuarios(modeloU));

        return panel;
    }

    /** Carga los usuarios desde la BD. */
    private void cargarUsuarios(DefaultTableModel modelo) {
        modelo.setRowCount(0);
        String sql = "SELECT id, usuario, nombre, rol, IF(activo=1,'Activo','Inactivo') AS estado, ultimo_acceso FROM usuarios ORDER BY id";
        try (Connection con = ConexionBD.getInstance().getConn();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String acceso = rs.getTimestamp("ultimo_acceso") != null
                    ? rs.getTimestamp("ultimo_acceso").toString() : "—";
                modelo.addRow(new Object[]{
                    String.format("U%02d", rs.getInt("id")),
                    rs.getString("usuario"),
                    rs.getString("nombre"),
                    rs.getString("rol"),
                    rs.getString("estado"),
                    acceso
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar usuarios:\n" + ex.getMessage(),
                "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Panel: Informes ────────────────────────────────────────────────
    private JPanel crearPanelInformes() {
        JPanel panel = crearPanelBase("📊  Informes");

        JPanel grid = new JPanel(new GridLayout(2, 2, 16, 16));
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(16, 0, 0, 0));

        grid.add(crearTarjetaInforme("📋 Listado completo",
                "Inventario completo de todos los elementos del taller.", COLOR_ACENTO));
        grid.add(crearTarjetaInforme("🏷 Por categoría / estado",
                "Filtra el inventario por tipo de material o estado actual.", COLOR_ACENTO2));
        grid.add(crearTarjetaInforme("📍 Por armario / balda",
                "Localización de todos los elementos por ubicación física.", new Color(34, 197, 94)));
        grid.add(crearTarjetaInforme("📤 Exportar PDF / Excel",
                "Genera un archivo exportable con los informes seleccionados.", new Color(251, 146, 60)));

        panel.add(grid, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearTarjetaInforme(String titulo, String desc, Color acento) {
        JPanel card = new JPanel() {
            private boolean hover = false;
            {
                setOpaque(false);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
                    @Override public void mouseClicked(MouseEvent e) {
                        JOptionPane.showMessageDialog(AdminFrame.this,
                            "Generando informe: " + titulo.replaceAll("[^a-zA-Z ñáéíóú]", "").trim(),
                            "Informes", JOptionPane.INFORMATION_MESSAGE);
                    }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hover ? new Color(38, 51, 73) : COLOR_PANEL);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(hover ? acento : COLOR_BORDE);
                g2.setStroke(new BasicStroke(hover ? 2f : 1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.setColor(acento);
                g2.fillRoundRect(0, 0, getWidth(), 4, 4, 4);
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel t = new JLabel(titulo);
        t.setFont(new Font("Segoe UI", Font.BOLD, 15));
        t.setForeground(COLOR_TEXTO);
        t.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel d = new JLabel("<html>" + desc + "</html>");
        d.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        d.setForeground(COLOR_SUBTEXTO);
        d.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(t);
        card.add(Box.createVerticalStrut(8));
        card.add(d);
        card.add(Box.createVerticalGlue());

        JLabel lnk = new JLabel("Generar →");
        lnk.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lnk.setForeground(acento);
        lnk.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lnk);

        return card;
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
        JLabel lImpFormatos = new JLabel("Formatos: CSV, XLSX, XLS");
        lImpFormatos.setForeground(COLOR_ACENTO); lImpFormatos.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lImpFormatos.setBorder(new EmptyBorder(12, 0, 12, 0));
        JButton btnSelFile = crearBoton("📁  Seleccionar archivo", COLOR_ACENTO2);
        btnSelFile.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton btnImport = crearBoton("✓ Importar", COLOR_OK);
        btnImport.setAlignmentX(Component.LEFT_ALIGNMENT);
        impContent.add(lImpDesc);
        impContent.add(lImpFormatos);
        impContent.add(btnSelFile);
        impContent.add(Box.createVerticalStrut(8));
        impContent.add(btnImport);
        pImport.add(impContent, BorderLayout.CENTER);

        JPanel pExport = crearCard("📤  Exportar datos");
        JPanel expContent = new JPanel();
        expContent.setOpaque(false);
        expContent.setLayout(new BoxLayout(expContent, BoxLayout.Y_AXIS));
        JLabel lExpDesc = new JLabel("<html>Exporta el inventario completo o filtrado<br>al formato elegido.</html>");
        lExpDesc.setForeground(COLOR_SUBTEXTO); lExpDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JLabel lExpFormatos = new JLabel("Formatos: CSV, XLSX, PDF");
        lExpFormatos.setForeground(COLOR_ACENTO); lExpFormatos.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lExpFormatos.setBorder(new EmptyBorder(12, 0, 12, 0));
        JButton btnExpCSV   = crearBoton("Exportar CSV",   COLOR_ACENTO2);
        JButton btnExpExcel = crearBoton("Exportar Excel", new Color(34, 197, 94));
        JButton btnExpPDF   = crearBoton("Exportar PDF",   new Color(251, 146, 60));
        btnExpCSV.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnExpExcel.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnExpPDF.setAlignmentX(Component.LEFT_ALIGNMENT);
        expContent.add(lExpDesc);
        expContent.add(lExpFormatos);
        expContent.add(btnExpCSV);
        expContent.add(Box.createVerticalStrut(6));
        expContent.add(btnExpExcel);
        expContent.add(Box.createVerticalStrut(6));
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

    // ── Helpers ────────────────────────────────────────────────────────
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
                if (isRowSelected(row)) {
                    c.setBackground(COLOR_SELECCION);
                } else {
                    c.setBackground(row % 2 == 0 ? COLOR_FILA_PAR : COLOR_FILA_IMPAR);
                }
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
        tf.setBackground(new Color(51, 65, 85));
        tf.setForeground(COLOR_TEXTO);
        tf.setCaretColor(COLOR_ACENTO);
        tf.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDE, 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return tf;
    }

    private void estilizarCombo(JComboBox<?> cb) {
        cb.setBackground(COLOR_PANEL);
        cb.setForeground(COLOR_TEXTO);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    }

    private JButton crearBoton(String texto, Color fondo) {
        JButton btn = new JButton(texto) {
            private boolean hover = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = hover ? fondo.brighter() : fondo;
                g2.setColor(c);
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
}