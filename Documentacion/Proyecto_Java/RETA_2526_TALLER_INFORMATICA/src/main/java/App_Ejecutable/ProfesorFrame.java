package App_Ejecutable;

import Conexion_Base_Datos.ConexionBD;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.sql.*;

/**
 * Ventana principal para los usuarios con perfil de Profesor.
 * Tiene permisos de solo lectura sobre el inventario, puede buscar y filtrar,
 * Los datos se cargan desde la base de datos MySQL.
 * @author David Gómez
 * @version 2.0
 */
public class ProfesorFrame extends JFrame {

    // Paleta de colores para el tema oscuro (estilo moderno/Slate)
    private static final Color COLOR_FONDO       = new Color(15, 23, 42);
    private static final Color COLOR_SIDEBAR     = new Color(23, 33, 52);
    private static final Color COLOR_PANEL       = new Color(30, 41, 59);
    private static final Color COLOR_ACENTO      = new Color(56, 189, 248);
    private static final Color COLOR_ACENTO2     = new Color(99, 102, 241);
    private static final Color COLOR_PELIGRO     = new Color(248, 113, 113);
    private static final Color COLOR_OK          = new Color(74, 222, 128);
    private static final Color COLOR_AMARILLO    = new Color(251, 191, 36);
    private static final Color COLOR_TEXTO       = new Color(226, 232, 240);
    private static final Color COLOR_SUBTEXTO    = new Color(148, 163, 184);
    private static final Color COLOR_BORDE       = new Color(51, 65, 85);
    private static final Color COLOR_FILA_PAR    = new Color(30, 41, 59);
    private static final Color COLOR_FILA_IMPAR  = new Color(38, 51, 73);
    private static final Color COLOR_SELECCION   = new Color(56, 189, 248, 60);

    /** URL del servidor local que gestiona el mapa interactivo del taller */
    private static final String URL_WEB_TALLER = "http://10.0.10.100";

    /** Cabeceras para las tablas de datos del inventario */
    private static final String[] COLUMNAS = {
        "ID", "Nombre", "Categoría", "Estado", "Cantidad", "Armario", "Balda"
    };

    // Componentes de la arquitectura de la interfaz
    private JPanel            contenidoCentral;
    private CardLayout        cardLayout;
    private DefaultTableModel modeloTabla;
    private DefaultTableModel modeloResultados;
    private JTable            tablaInventario;
    private JTextField        txtBusqueda;
    private JComboBox<String> cbCategoria;
    private JComboBox<String> cbEstado;
    private JLabel            lblEstado;
    private JLabel            lblItemSeleccionado;
    private String            usuarioActual;

    /**
     * Constructor principal. Configura el marco general de la ventana e inicializa
     * los paneles de navegación y contenido.
     * @param usuario Nombre de usuario que ha iniciado sesión para personalizar la interfaz.
     */
    public ProfesorFrame(String usuario) {
        this.usuarioActual = usuario;
        setTitle("Taller IES MHP · Panel Profesor — " + usuario);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(860, 580));
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_FONDO);
        setLayout(new BorderLayout(0, 0));

        add(crearSidebar(),           BorderLayout.WEST);
        add(crearContenidoCentral(),  BorderLayout.CENTER);
        add(crearBarraEstado(),       BorderLayout.SOUTH);

        mostrarPanel("consulta");
    }

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

        JLabel badgeProf = new JLabel("PROFESOR");
        badgeProf.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badgeProf.setForeground(COLOR_AMARILLO);
        badgeProf.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblNombre = new JLabel(usuarioActual);
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblNombre.setForeground(COLOR_TEXTO);
        lblNombre.setAlignmentX(Component.LEFT_ALIGNMENT);

        cab.add(badgeProf);
        cab.add(Box.createVerticalStrut(4));
        cab.add(lblNombre);
        sidebar.add(cab);
        sidebar.add(crearSeparadorSidebar());
        
        sidebar.add(crearSeccion("INVENTARIO"));
        sidebar.add(crearItemMenu("📋", "Consultar Inventario", "consulta"));
        sidebar.add(crearItemMenu("🔍", "Buscar / Filtrar",     "buscar"));
        sidebar.add(crearItemMenu("📍", "Localizar Material",   "localizar"));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(crearSeparadorSidebar());
        
        sidebar.add(crearSeccion("INFORMES"));
        sidebar.add(crearItemMenu("📄", "Generar Listados",     "informes"));
        
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
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(new Color(56, 189, 248, 20));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JLabel ico = new JLabel(icono);
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));

        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setForeground(COLOR_TEXTO);

        item.add(ico);
        item.add(lbl);
        return item;
    }

    private JPanel crearContenidoCentral() {
        cardLayout = new CardLayout();
        contenidoCentral = new JPanel(cardLayout);
        contenidoCentral.setOpaque(false);
        contenidoCentral.setBackground(COLOR_FONDO);

        contenidoCentral.add(crearPanelConsulta(),  "consulta");
        contenidoCentral.add(crearPanelBuscar(),    "buscar");
        contenidoCentral.add(crearPanelLocalizar(), "localizar");
        contenidoCentral.add(crearPanelInformes(),  "informes");

        return contenidoCentral;
    }

    private void mostrarPanel(String id) {
        cardLayout.show(contenidoCentral, id);
    }

    /**
     * Método core encargado de realizar las consultas SQL a la base de datos.
     * @param modelo El TableModel de destino que se actualizará con los datos obtenidos.
     * @param nombre Filtro por nombre de material (búsqueda parcial utilizando LIKE).
     * @param categoria Filtro por categoría exacta.
     * @param estado Filtro por estado del equipo.
     * @param armario Filtro por ubicación o código del armario.
     */
    private void cargarInventario(DefaultTableModel modelo,
                                  String nombre, String categoria,
                                  String estado, String armario) {
        modelo.setRowCount(0);

        StringBuilder sql = new StringBuilder(
            "SELECT m.id_material, m.nombre, m.cantidad, " +
            "c.nombre AS nombre_categoria, " +
            "e.nombre AS nombre_estado, " +
            "u.codigo_armario, u.codigo_balda " +
            "FROM material m " +
            "LEFT JOIN categoria c ON m.id_categoria = c.id_categoria " +
            "LEFT JOIN estado_elemento e ON m.id_estado = e.id " +
            "LEFT JOIN ubicacion u ON m.id_ubicacion = u.id_ubicacion " +
            "WHERE 1=1");
        
        if (nombre != null && !nombre.isEmpty())
            sql.append(" AND LOWER(m.nombre) LIKE ?");
        if (categoria != null && !categoria.equals("Todas"))
            sql.append(" AND c.nombre = ?");
        if (estado != null && !estado.equals("Todos"))
            sql.append(" AND e.nombre = ?");
        if (armario != null && !armario.isEmpty() && !armario.equals("Todos"))
            sql.append(" AND u.codigo_armario = ?");

        try (Connection con = ConexionBD.getInstance().getConn();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int idx = 1;
            if (nombre != null && !nombre.isEmpty())
                ps.setString(idx++, "%" + nombre.toLowerCase() + "%");
            if (categoria != null && !categoria.equals("Todas"))
                ps.setString(idx++, categoria);
            if (estado != null && !estado.equals("Todos"))
                ps.setString(idx++, estado);
            if (armario != null && !armario.isEmpty() && !armario.equals("Todos"))
                ps.setString(idx++, armario);

            ResultSet rs = ps.executeQuery();
            int total = 0;
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    String.format("%03d", rs.getInt("id_material")),
                    rs.getString("nombre"),
                    rs.getString("nombre_categoria") != null ? rs.getString("nombre_categoria") : "Sin categoría",
                    rs.getString("nombre_estado") != null ? rs.getString("nombre_estado") : "Sin estado",
                    rs.getInt("cantidad"),
                    rs.getString("codigo_armario") != null ? rs.getString("codigo_armario") : "—",
                    rs.getString("codigo_balda") != null ? rs.getString("codigo_balda") : "—"
                });
                total++;
            }
            actualizarBarraEstado(total);

        } catch (SQLException ex) {
            lblEstado.setText("✗ Error al conectar con la BD: " + ex.getMessage());
            JOptionPane.showMessageDialog(this,
                "Error al cargar el inventario:\n" + ex.getMessage(),
                "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String[] cargarCategorias() {
        java.util.List<String> lista = new java.util.ArrayList<>();
        lista.add("Todas");
        try (Connection con = ConexionBD.getInstance().getConn();
             PreparedStatement ps = con.prepareStatement(
                 "SELECT nombre FROM categoria ORDER BY nombre");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(rs.getString("nombre"));
        } catch (SQLException ignored) {}
        return lista.toArray(new String[0]);
    }

    private String[] cargarEstados() {
        java.util.List<String> lista = new java.util.ArrayList<>();
        lista.add("Todos");
        try (Connection con = ConexionBD.getInstance().getConn();
             PreparedStatement ps = con.prepareStatement(
                 "SELECT nombre FROM estado_elemento ORDER BY nombre");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(rs.getString("nombre"));
        } catch (SQLException ignored) {}
        return lista.toArray(new String[0]);
    }

    private String[] cargarArmarios() {
        java.util.List<String> lista = new java.util.ArrayList<>();
        lista.add("Todos");
        try (Connection con = ConexionBD.getInstance().getConn();
             PreparedStatement ps = con.prepareStatement(
                 "SELECT DISTINCT codigo_armario FROM ubicacion " +
                 "WHERE codigo_armario IS NOT NULL ORDER BY codigo_armario");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(rs.getString("codigo_armario"));
        } catch (SQLException ignored) {}
        return lista.toArray(new String[0]);
    }

    private String[] cargarBaldas() {
        java.util.List<String> lista = new java.util.ArrayList<>();
        lista.add("Todas");
        try (Connection con = ConexionBD.getInstance().getConn();
             PreparedStatement ps = con.prepareStatement(
                 "SELECT DISTINCT codigo_balda FROM ubicacion " +
                 "WHERE codigo_balda IS NOT NULL ORDER BY codigo_balda");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(rs.getString("codigo_balda"));
        } catch (SQLException ignored) {}
        return lista.toArray(new String[0]);
    }

    private void actualizarBarraEstado(int total) {
        lblEstado.setText("✓  Conectado a MySQL · " + total
            + " elemento(s) · BD: Taller_Informatica  ·  Perfil: Profesor (solo lectura)");
    }

    private JPanel crearPanelConsulta() {
        JPanel panel = crearPanelBase("📋  Inventario del Taller");

        JPanel barraInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barraInfo.setOpaque(false);
        JLabel lblInfo = new JLabel("Vista de solo lectura · cargando datos...");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblInfo.setForeground(COLOR_SUBTEXTO);
        barraInfo.add(lblInfo);
        panel.add(barraInfo, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaInventario = crearTabla(modeloTabla);
        tablaInventario.getColumnModel().getColumn(3).setCellRenderer(estadoRenderer());

        JScrollPane scroll = new JScrollPane(tablaInventario);
        scroll.setOpaque(false);
        scroll.getViewport().setBackground(COLOR_FONDO);
        scroll.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        panel.add(scroll, BorderLayout.CENTER);

        JPanel barraInf = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        barraInf.setOpaque(false);

        JButton btnRefresh = crearBoton("↻ Actualizar", COLOR_PANEL);
        JButton btnLoc     = crearBoton("📍  Localizar seleccionado", COLOR_ACENTO2);

        btnRefresh.addActionListener(e -> {
            cargarInventario(modeloTabla, null, null, null, null);
            lblInfo.setText("Vista de solo lectura · " + modeloTabla.getRowCount() + " elementos");
        });
        btnLoc.addActionListener(e -> localizarSeleccionado());

        barraInf.add(btnRefresh);
        barraInf.add(btnLoc);
        panel.add(barraInf, BorderLayout.SOUTH);

        SwingUtilities.invokeLater(() -> {
            cargarInventario(modeloTabla, null, null, null, null);
            lblInfo.setText("Vista de solo lectura · " + modeloTabla.getRowCount() + " elementos");
        });

        return panel;
    }

    private JPanel crearPanelBuscar() {
        JPanel panel = crearPanelBase("🔍  Buscar y Filtrar Material");

        JPanel card = crearCard("Criterios de búsqueda");

        JPanel filtros = new JPanel(new GridBagLayout());
        filtros.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 12, 12);

        gbc.gridy = 0; gbc.gridx = 0; gbc.weightx = 0;
        filtros.add(etiqueta("Nombre / descripción"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.gridwidth = 3;
        txtBusqueda = campoTexto();
        filtros.add(txtBusqueda, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0; gbc.weightx = 0;
        filtros.add(etiqueta("Categoría"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        cbCategoria = new JComboBox<>(cargarCategorias());
        estilizarCombo(cbCategoria);
        filtros.add(cbCategoria, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        filtros.add(etiqueta("Estado"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        cbEstado = new JComboBox<>(cargarEstados());
        estilizarCombo(cbEstado);
        filtros.add(cbEstado, gbc);

        gbc.gridy = 2; gbc.gridx = 0; gbc.weightx = 0;
        filtros.add(etiqueta("Código armario"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JComboBox<String> cbArmario = new JComboBox<>(cargarArmarios());
        estilizarCombo(cbArmario);
        filtros.add(cbArmario, gbc);

        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 4;
        gbc.insets = new Insets(8, 0, 0, 0);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);
        JButton btnLimpiar = crearBoton("Limpiar",    COLOR_PANEL);
        JButton btnBuscar  = crearBoton("🔍  Buscar", COLOR_ACENTO);

        modeloResultados = new DefaultTableModel(COLUMNAS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tablaRes = crearTabla(modeloResultados);
        tablaRes.getColumnModel().getColumn(3).setCellRenderer(estadoRenderer());

        btnLimpiar.addActionListener(e -> {
            txtBusqueda.setText("");
            cbCategoria.setSelectedIndex(0);
            cbEstado.setSelectedIndex(0);
            cbArmario.setSelectedIndex(0);
            cargarInventario(modeloResultados, null, null, null, null);
        });
        
        btnBuscar.addActionListener(e -> {
            String armarioSel = cbArmario.getSelectedIndex() > 0 ? 
                cbArmario.getSelectedItem().toString() : null;
            cargarInventario(
                modeloResultados,
                txtBusqueda.getText().trim(),
                cbCategoria.getSelectedItem().toString(),
                cbEstado.getSelectedItem().toString(),
                armarioSel
            );
        });

        btnPanel.add(btnLimpiar);
        btnPanel.add(btnBuscar);
        filtros.add(btnPanel, gbc);
        card.add(filtros, BorderLayout.CENTER);
        panel.add(card, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(tablaRes);
        scroll.setOpaque(false);
        scroll.getViewport().setBackground(COLOR_FONDO);
        scroll.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));

        JPanel barraInf = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        barraInf.setOpaque(false);
        JButton btnLoc = crearBoton("📍  Localizar seleccionado", COLOR_ACENTO2);
        btnLoc.addActionListener(e -> {
            int fila = tablaRes.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(this,
                    "Selecciona un elemento de la tabla primero.",
                    "Sin selección", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            abrirWebLocalizar(
                modeloResultados.getValueAt(fila, 1).toString(),
                modeloResultados.getValueAt(fila, 5).toString(),
                modeloResultados.getValueAt(fila, 6).toString()
            );
        });
        barraInf.add(btnLoc);

        panel.add(scroll,   BorderLayout.CENTER);
        panel.add(barraInf, BorderLayout.SOUTH);

        SwingUtilities.invokeLater(() ->
            cargarInventario(modeloResultados, null, null, null, null));

        return panel;
    }

    private JPanel crearPanelLocalizar() {
        JPanel panel = crearPanelBase("📍  Localizar Material en el Taller");

        JPanel cardSel = crearCard("Seleccionar elemento a localizar");

        JPanel fila = new JPanel(new GridBagLayout());
        fila.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 12);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        fila.add(etiqueta("Elemento"), gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        JComboBox<String> cbElemento = new JComboBox<>();
        cbElemento.addItem("— Selecciona un elemento —");
        estilizarCombo(cbElemento);
        fila.add(cbElemento, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        JButton btnLocalizar = crearBoton("📍  Ver localización", COLOR_ACENTO);
        fila.add(btnLocalizar, gbc);

        cardSel.add(fila, BorderLayout.CENTER);
        panel.add(cardSel, BorderLayout.NORTH);

        JPanel cardInfo = crearCard("Ubicación del elemento");

        JPanel infoGrid = new JPanel(new GridLayout(2, 4, 16, 12));
        infoGrid.setOpaque(false);

        JLabel[] lblsKey = new JLabel[4];
        JLabel[] lblsVal = new JLabel[4];
        String[] claves = {"Nombre", "Categoría", "Armario", "Balda"};
        for (int i = 0; i < 4; i++) {
            lblsKey[i] = etiqueta(claves[i]);
            lblsVal[i] = new JLabel("—");
            lblsVal[i].setFont(new Font("Segoe UI", Font.BOLD, 15));
            lblsVal[i].setForeground(COLOR_ACENTO);
            infoGrid.add(lblsKey[i]);
        }
        for (JLabel v : lblsVal) infoGrid.add(v);

        JPanel btnWeb = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 16));
        btnWeb.setOpaque(false);
        JButton btnAbrirWeb = crearBoton("🌐  Abrir sitio web de visualización del taller", COLOR_ACENTO2);
        btnAbrirWeb.setPreferredSize(new Dimension(420, 46));
        btnAbrirWeb.setEnabled(false);
        btnWeb.add(btnAbrirWeb);

        lblItemSeleccionado = new JLabel("Selecciona un elemento para ver su ubicación.");
        lblItemSeleccionado.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblItemSeleccionado.setForeground(COLOR_SUBTEXTO);
        lblItemSeleccionado.setHorizontalAlignment(SwingConstants.CENTER);

        cardInfo.add(infoGrid,            BorderLayout.NORTH);
        cardInfo.add(lblItemSeleccionado, BorderLayout.CENTER);
        cardInfo.add(btnWeb,              BorderLayout.SOUTH);

        java.util.List<Object[]> filasBD = new java.util.ArrayList<>();

        SwingUtilities.invokeLater(() -> {
            String sql = "SELECT m.id_material, m.nombre, c.nombre AS nombre_categoria, " +
                         "u.codigo_armario, u.codigo_balda " +
                         "FROM material m " +
                         "LEFT JOIN categoria c ON m.id_categoria = c.id_categoria " +
                         "LEFT JOIN ubicacion u ON m.id_ubicacion = u.id_ubicacion " +
                         "ORDER BY m.nombre";
            try (Connection con = ConexionBD.getInstance().getConn();
                 PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    filasBD.add(new Object[]{
                        rs.getString("id_material"),
                        rs.getString("nombre"),
                        rs.getString("nombre_categoria") != null ? rs.getString("nombre_categoria") : "Sin categoría",
                        rs.getString("codigo_armario") != null ? rs.getString("codigo_armario") : "",
                        rs.getString("codigo_balda") != null ? rs.getString("codigo_balda") : ""
                    });
                    cbElemento.addItem(
                        String.format("%03d", rs.getInt("id_material")) + " · " + rs.getString("nombre"));
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                    "Error al cargar elementos:\n" + ex.getMessage(),
                    "Error de BD", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnLocalizar.addActionListener(e -> {
            int idx = cbElemento.getSelectedIndex();
            if (idx == 0) {
                JOptionPane.showMessageDialog(this,
                    "Selecciona un elemento de la lista.",
                    "Sin selección", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            Object[] dato = filasBD.get(idx - 1);
            lblsVal[0].setText(dato[1].toString());
            lblsVal[1].setText(dato[2].toString());
            lblsVal[2].setText(dato[3] != null && !dato[3].toString().isEmpty() ? dato[3].toString() : "—");
            lblsVal[3].setText(dato[4] != null && !dato[4].toString().isEmpty() ? dato[4].toString() : "—");
            lblItemSeleccionado.setText(
                "Elemento localizado: " + dato[1] + " → Armario " + dato[3] + ", Balda " + dato[4]);
            lblItemSeleccionado.setForeground(COLOR_OK);
            btnAbrirWeb.setEnabled(true);

            for (ActionListener al : btnAbrirWeb.getActionListeners())
                btnAbrirWeb.removeActionListener(al);
            btnAbrirWeb.addActionListener(ev ->
                abrirWebLocalizar(dato[1].toString(),
                                  dato[3] != null ? dato[3].toString() : "",
                                  dato[4] != null ? dato[4].toString() : ""));
        });

        panel.add(cardInfo, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelInformes() {
        JPanel panel = crearPanelBase("📄  Generar Listados");

        JPanel contenido = new JPanel(new GridLayout(1, 3, 16, 0));
        contenido.setOpaque(false);

        // Tarjeta 1: Inventario Completo
        JPanel c1 = crearCard("📋  Listado completo");
        JLabel d1 = new JLabel("<html><body style='width:150px;color:#94A3B8'>Exporta el inventario<br>completo del taller.</body></html>");
        d1.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JButton b1 = crearBoton("Generar listado", COLOR_ACENTO);
        b1.addActionListener(e -> generarListado("completo", null, null));
        JPanel p1 = new JPanel(new BorderLayout(0, 12)); p1.setOpaque(false);
        p1.add(d1, BorderLayout.CENTER); p1.add(b1, BorderLayout.SOUTH);
        c1.add(p1, BorderLayout.CENTER);

        // Tarjeta 2: Filtros por categoría y estado
        JPanel c2 = crearCard("🗂  Por categoría / estado");
        JPanel sel2 = new JPanel(new GridLayout(4, 1, 0, 8)); sel2.setOpaque(false);
        JComboBox<String> cbCat2 = new JComboBox<>(cargarCategorias());
        JComboBox<String> cbEst2 = new JComboBox<>(cargarEstados());
        estilizarCombo(cbCat2); estilizarCombo(cbEst2);
        JButton b2 = crearBoton("Generar listado", COLOR_ACENTO);
        b2.addActionListener(e -> generarListado(
            "categoria", cbCat2.getSelectedItem().toString(),
            cbEst2.getSelectedItem().toString()));
        sel2.add(etiqueta("Categoría")); sel2.add(cbCat2);
        sel2.add(etiqueta("Estado"));   sel2.add(cbEst2);
        JPanel p2 = new JPanel(new BorderLayout(0, 12)); p2.setOpaque(false);
        p2.add(sel2, BorderLayout.CENTER); p2.add(b2, BorderLayout.SOUTH);
        c2.add(p2, BorderLayout.CENTER);

        // Tarjeta 3: Filtros por ubicación
        JPanel c3 = crearCard("📍  Por armario / balda");
        JPanel sel3 = new JPanel(new GridLayout(4, 1, 0, 8)); sel3.setOpaque(false);
        JComboBox<String> cbArm = new JComboBox<>(cargarArmarios());
        JComboBox<String> cbBal = new JComboBox<>(cargarBaldas());
        estilizarCombo(cbArm); estilizarCombo(cbBal);
        JButton b3 = crearBoton("Generar listado", COLOR_ACENTO);
        b3.addActionListener(e -> generarListado(
            "ubicacion", cbArm.getSelectedItem().toString(),
            cbBal.getSelectedItem().toString()));
        sel3.add(etiqueta("Armario")); sel3.add(cbArm);
        sel3.add(etiqueta("Balda"));   sel3.add(cbBal);
        JPanel p3 = new JPanel(new BorderLayout(0, 12)); p3.setOpaque(false);
        p3.add(sel3, BorderLayout.CENTER); p3.add(b3, BorderLayout.SOUTH);
        c3.add(p3, BorderLayout.CENTER);

        contenido.add(c1); contenido.add(c2); contenido.add(c3);
        panel.add(contenido, BorderLayout.CENTER);

        JLabel nota = new JLabel("Los informes se exportarán en PDF o Excel según la configuración del sistema.");
        nota.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        nota.setForeground(COLOR_SUBTEXTO);
        nota.setHorizontalAlignment(SwingConstants.CENTER);
        nota.setBorder(new EmptyBorder(12, 0, 0, 0));
        panel.add(nota, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearBarraEstado() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(new Color(10, 16, 30));
        barra.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, COLOR_BORDE),
                new EmptyBorder(6, 16, 6, 16)));

        lblEstado = new JLabel("Conectando a MySQL...");
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEstado.setForeground(COLOR_SUBTEXTO);

        JLabel lblFecha = new JLabel(
            new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date()));
        lblFecha.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFecha.setForeground(COLOR_SUBTEXTO);

        barra.add(lblEstado, BorderLayout.WEST);
        barra.add(lblFecha,  BorderLayout.EAST);
        return barra;
    }

    private void localizarSeleccionado() {
        int fila = tablaInventario.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this,
                "Selecciona un elemento de la tabla primero.",
                "Sin selección", JOptionPane.INFORMATION_MESSAGE);
        }
    }

       /**
     * Utiliza el API Desktop del sistema operativo para abrir de forma automática
     * el navegador por defecto cargando la URL paramétrica del mapa del taller.
     * @param nombre Nombre del material.
     * @param armario Código identificador del armario.
     * @param balda Número de la balda.
     */
    private void abrirWebLocalizar(String nombre, String armario, String balda) {
        // Codificamos los espacios en blanco a formato URL estándar
        String url = URL_WEB_TALLER + "?armario=" + armario + "&balda=" + balda
                + "&item=" + nombre.replace(" ", "%20");
        try {
            Desktop.getDesktop().browse(new URI(url));
            lblEstado.setText("✓  Abriendo web: " + url);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "No se pudo abrir el navegador.\nURL: " + url,
                "Error al abrir el sitio web", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Simula y procesa la generación de un reporte cuantitativo contra la base de datos,
     * mostrando al usuario un resumen en pantalla mediante un panel informativo.
     * @param tipo Criterio seleccionado ("completo", "categoria" o "ubicacion").
     * @param filtro1 Primer parámetro del filtro (ej: Nombre de Categoría o Código de Armario).
     * @param filtro2 Segundo parámetro del filtro (ej: Estado o Código de Balda).
     */
    private void generarListado(String tipo, String filtro1, String filtro2) {
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(*) AS total FROM material m " +
            "LEFT JOIN categoria c ON m.id_categoria = c.id_categoria " +
            "LEFT JOIN estado_elemento e ON m.id_estado = e.id " +
            "LEFT JOIN ubicacion u ON m.id_ubicacion = u.id_ubicacion " +
            "WHERE 1=1");

        switch (tipo) {
            case "categoria" -> {
                if (filtro1 != null && !filtro1.equals("Todas"))
                    sql.append(" AND c.nombre = '").append(filtro1).append("'");
                if (filtro2 != null && !filtro2.equals("Todos"))
                    sql.append(" AND e.nombre = '").append(filtro2).append("'");
            }
            case "ubicacion" -> {
                if (filtro1 != null && !filtro1.equals("Todos"))
                    sql.append(" AND u.codigo_armario = '").append(filtro1).append("'");
                if (filtro2 != null && !filtro2.equals("Todas"))
                    sql.append(" AND u.codigo_balda = '").append(filtro2).append("'");
            }
        }

        try (Connection con = ConexionBD.getInstance().getConn();
             PreparedStatement ps = con.prepareStatement(sql.toString());
             ResultSet rs = ps.executeQuery()) {

            int total = rs.next() ? rs.getInt("total") : 0;
            String desc = switch (tipo) {
                case "categoria" -> "Categoría: " + (filtro1 != null ? filtro1 : "Todas")
                                  + "\nEstado: " + (filtro2 != null ? filtro2 : "Todos");
                case "ubicacion" -> "Armario: " + (filtro1 != null ? filtro1 : "Todos")
                                  + "\nBalda: " + (filtro2 != null ? filtro2 : "Todas");
                default          -> "Inventario completo";
            };
            JOptionPane.showMessageDialog(this,
                "Informe generado correctamente.\n\n" + desc
                + "\n\nElementos encontrados: " + total,
                "Informe generado", JOptionPane.INFORMATION_MESSAGE);
            lblEstado.setText("✓ Informe generado: " + desc.replace("\n", " · "));

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error al generar el informe:\n" + ex.getMessage(),
                "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Solicita confirmación de salida al usuario. Destruye la ventana de profesor
     * y devuelve el flujo abriendo un nuevo marco de LoginFrame.
     */
    private void cerrarSesion() {
        int resp = JOptionPane.showConfirmDialog(this,
                "¿Cerrar sesión y volver al login?", "Cerrar sesión",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (resp == JOptionPane.YES_OPTION) {
            dispose(); // Cierra y libera los recursos gráficos del marco actual
            new LoginFrame().setVisible(true); // Redirección
        }
    }

    /**
     * Factoría de interfaz para construir el panel contenedor base. Asegura
     * una homogeneidad estética en todas las vistas compartiendo padding y estilos de títulos.
     * * @param titulo Encabezado principal del panel de trabajo.
     * @return Panel pre-formateado.
     */
    private JPanel crearPanelBase(String titulo) {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setOpaque(false);
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(new EmptyBorder(28, 28, 20, 28));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(COLOR_TEXTO);
        lblTitulo.setBorder(new EmptyBorder(0, 0, 8, 0));
        panel.add(lblTitulo, BorderLayout.NORTH);
        return panel;
    }

    /**
     * Componente contenedor con diseño de tarjeta ("Card") que emula interfaces modernas.
     * Implementa bordes y esquinas redondeadas suavizadas mediante técnicas de Antialiasing.
     * * @param titulo El sub-título de la sección o tarjeta.
     * @return El panel estilizado como tarjeta.
     */
    private JPanel crearCard(String titulo) {
        JPanel card = new JPanel(new BorderLayout(0, 12)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                // Activación de filtros de suavizado para evitar dientes de sierra en los bordes curvos
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_PANEL);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
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
     * Inicializa y parametriza las propiedades globales visuales de las tablas de datos,
     * sobreescribiendo el renderizador nativo para alternar el tono de las filas pares e impares (filas cebra).
     * * @param modelo Estructura de datos que alimentará la JTable.
     * @return JTable modificada con diseño Dark UI.
     */
    private JTable crearTabla(DefaultTableModel modelo) {
        JTable tabla = new JTable(modelo) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                // Alternador de color de fondo por fila para mejorar considerablemente la lectura
                if (isRowSelected(row)) {
                    c.setBackground(COLOR_SELECCION);
                } else {
                    c.setBackground(row % 2 == 0 ? COLOR_FILA_PAR : COLOR_FILA_IMPAR);
                }
                c.setForeground(COLOR_TEXTO);
                return c;
            }
        };
        // Parámetros de dimensionamiento y visualización de rejillas
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
        
        // Ajustes y maquetación de la cabecera (Header) de la tabla
        tabla.getTableHeader().setBackground(new Color(23, 33, 52));
        tabla.getTableHeader().setForeground(COLOR_SUBTEXTO);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabla.getTableHeader().setBorder(new MatteBorder(0, 0, 1, 0, COLOR_BORDE));
        return tabla;
    }

    /**
     * Renderizador propio de celdas enfocado a evaluar la columna "Estado".
     * Modifica dinámicamente el color del texto dependiendo del valor de la celda
     * (Operativo -> Verde, Averiado -> Rojo, etc.).
     * * @return Un objeto TableCellRenderer con lógica condicional de color.
     */
    private TableCellRenderer estadoRenderer() {
        return new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                String s = val != null ? val.toString() : "";
                // Mapeo cromático según los estados de inventario establecidos en la BD
                switch (s) {
                    case "DISPONIBLE"      -> setForeground(COLOR_OK);
                    case "PRESTADO"        -> setForeground(COLOR_AMARILLO);
                    case "EN_REPARACION"   -> setForeground(COLOR_PELIGRO);
                    case "BAJA"            -> setForeground(COLOR_SUBTEXTO);
                    default                -> setForeground(COLOR_TEXTO);
                }
                setBackground(sel ? COLOR_SELECCION
                                  : (row % 2 == 0 ? COLOR_FILA_PAR : COLOR_FILA_IMPAR));
                return this;
            }
        };
    }

    /**
     * Crea un botón corporativo estilizado con esquinas redondeadas y
     * comportamiento dinámico integrado para simular transiciones al pasar el cursor.
     * * @param texto Cadena textual interna del botón.
     * @param fondo Color base de fondo del elemento.
     * @return JButton configurado.
     */
    private JButton crearBoton(String texto, Color fondo) {
        JButton btn = new JButton(texto) {
            private boolean hover = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hover = true;  repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Si está en foco del ratón, aclaramos levemente el tono para denotar interactividad
                g2.setColor(hover ? fondo.brighter() : fondo);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        // Ajuste inteligente del color de la fuente para cumplir criterios de accesibilidad/contraste
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
     * Generador rápido de JLabels estandarizadas para textos explicativos de formularios.
     * * @param texto Contenido textual de la etiqueta.
     * @return El objeto JLabel resultante.
     */
    private JLabel etiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(COLOR_SUBTEXTO);
        return lbl;
    }

    /**
     * Inicializa un JTextField con un estilo plano unificado, configurando bordes
     * internos (paddings) para evitar que el texto colisione con el marco físico del control.
     * * @return Componente JTextField personalizado.
     */
    private JTextField campoTexto() {
        JTextField tf = new JTextField();
        tf.setBackground(new Color(51, 65, 85));
        tf.setForeground(COLOR_TEXTO);
        tf.setCaretColor(COLOR_ACENTO); // Color del puntero titilante
        tf.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDE, 1, true),
                new EmptyBorder(6, 10, 6, 10))); // Margen interno de confort
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return tf;
    }

    /**
     * Setea los colores base para uniformar los componentes JComboBox del sistema.
     * * @param cb Referencia del JComboBox a alterar.
     */
    private void estilizarCombo(JComboBox<?> cb) {
        cb.setBackground(COLOR_PANEL);
        cb.setForeground(COLOR_TEXTO);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    }
}