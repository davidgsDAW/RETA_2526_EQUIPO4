import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;

/**
 * Ventana principal del perfil Profesor.
 * Permite consulta del inventario, búsqueda y filtrado por nombre,
 * categoría, estado o código, localización visual del material
 * (abre el sitio web) y generación de listados.
 *
 * @author IES Miguel Herrero Pereda - DAW 2025/2026
 * @version 1.0
 */
public class ProfesorFrame extends JFrame {

    // ── Paleta (idéntica a AdminFrame) ─────────────────────────────────
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

    // URL del sitio web de localización (MV2)
    private static final String URL_WEB_TALLER   = "http://192.168.56.10/taller/";

    // ── Datos de prueba ────────────────────────────────────────────────
    private static final String[] COLUMNAS = {
        "ID", "Nombre", "Categoría", "Estado", "Cantidad", "Armario", "Balda"
    };
    private static final Object[][] DATOS_PRUEBA = {
        {"001", "Portátil Dell Latitude",   "PC Prácticas",          "Operativo",     "12", "A1", "B1"},
        {"002", "Switch HP 24p",            "Equipos de Red",        "Operativo",      "3", "A2", "B2"},
        {"003", "RAM DDR4 8GB",             "Componentes HW",        "Operativo",     "25", "A3", "B1"},
        {"004", "Router Cisco 2900",        "Equipos de Red",        "Averiado",       "1", "A2", "B3"},
        {"005", "Multímetro digital",       "Herramientas",          "Operativo",      "8", "A4", "B2"},
        {"006", "Cable UTP Cat6 (100m)",    "Material Fungible",     "Operativo",      "5", "A5", "B1"},
        {"007", "Placa base ATX",           "Componentes HW",        "En reparación",  "2", "A3", "B3"},
        {"008", "Crimpadoras RJ45",         "Herramientas",          "Operativo",      "6", "A4", "B1"},
        {"009", "Disco SSD 500GB",          "Componentes HW",        "Operativo",     "10", "A3", "B2"},
        {"010", "Patch Panel 24p",          "Cableado Estructurado", "Operativo",      "2", "A2", "B1"},
        {"011", "Procesador Intel i5",      "Componentes HW",        "Operativo",      "7", "A3", "B4"},
        {"012", "Punto de acceso WiFi",     "Equipos de Red",        "Operativo",      "4", "A2", "B4"},
        {"013", "Pasta térmica 100g",       "Material Fungible",     "Operativo",     "15", "A5", "B2"},
        {"014", "Destornilladores set",     "Herramientas",          "Operativo",      "5", "A4", "B3"},
        {"015", "Fibra óptica LC-LC 5m",    "Cableado Estructurado", "Obsoleto",       "3", "A2", "B5"},
    };

    // ── Componentes ────────────────────────────────────────────────────
    private JPanel             contenidoCentral;
    private CardLayout         cardLayout;
    private DefaultTableModel  modeloTabla;
    private JTable             tablaInventario;
    private JTextField         txtBusqueda;
    private JComboBox<String>  cbCategoria;
    private JComboBox<String>  cbEstado;
    private JLabel             lblEstado;
    private String             usuarioActual;
    private JLabel             lblItemSeleccionado;

    // ──────────────────────────────────────────────────────────────────
    /**
     * Constructor del frame del Profesor.
     *
     * @param usuario nombre de usuario autenticado
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

        add(crearSidebar(), BorderLayout.WEST);
        add(crearContenidoCentral(), BorderLayout.CENTER);
        add(crearBarraEstado(), BorderLayout.SOUTH);

        mostrarPanel("consulta");
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

        // Cabecera
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
        sidebar.add(crearItemMenu("🔍", "Buscar / Filtrar",    "buscar"));
        sidebar.add(crearItemMenu("📍", "Localizar Material",  "localizar"));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(crearSeparadorSidebar());
        sidebar.add(crearSeccion("INFORMES"));
        sidebar.add(crearItemMenu("📄", "Generar Listados",    "informes"));
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(crearSeparadorSidebar());

        // Cerrar sesión
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

    // ── Área central con CardLayout ─────────────────────────────────────
    private JPanel crearContenidoCentral() {
        cardLayout = new CardLayout();
        contenidoCentral = new JPanel(cardLayout);
        contenidoCentral.setOpaque(false);
        contenidoCentral.setBackground(COLOR_FONDO);

        contenidoCentral.add(crearPanelConsulta(),   "consulta");
        contenidoCentral.add(crearPanelBuscar(),     "buscar");
        contenidoCentral.add(crearPanelLocalizar(),  "localizar");
        contenidoCentral.add(crearPanelInformes(),   "informes");

        return contenidoCentral;
    }

    private void mostrarPanel(String id) {
        cardLayout.show(contenidoCentral, id);
    }

    // ── Panel: Consultar Inventario ─────────────────────────────────────
    /**
     * Muestra la tabla completa del inventario (solo lectura).
     *
     * @return panel con la tabla del inventario
     */
    private JPanel crearPanelConsulta() {
        JPanel panel = crearPanelBase("📋  Inventario del Taller");

        // Barra superior info
        JPanel barraInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barraInfo.setOpaque(false);
        JLabel lblInfo = new JLabel("Vista de solo lectura · " + DATOS_PRUEBA.length + " elementos");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblInfo.setForeground(COLOR_SUBTEXTO);
        barraInfo.add(lblInfo);
        panel.add(barraInfo, BorderLayout.NORTH);

        // Tabla
        modeloTabla = new DefaultTableModel(DATOS_PRUEBA, COLUMNAS) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaInventario = crearTabla(modeloTabla);

        // Color de estado en columna 3
        tablaInventario.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                String estado = val != null ? val.toString() : "";
                switch (estado) {
                    case "Operativo"    -> setForeground(COLOR_OK);
                    case "Averiado"     -> setForeground(COLOR_PELIGRO);
                    case "En reparación"-> setForeground(COLOR_AMARILLO);
                    case "Obsoleto"     -> setForeground(COLOR_SUBTEXTO);
                    default             -> setForeground(COLOR_TEXTO);
                }
                setBackground(sel ? COLOR_SELECCION : (row % 2 == 0 ? COLOR_FILA_PAR : COLOR_FILA_IMPAR));
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(tablaInventario);
        scroll.setOpaque(false);
        scroll.getViewport().setBackground(COLOR_FONDO);
        scroll.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));

        panel.add(scroll, BorderLayout.CENTER);

        // Botón localizar seleccionado (acceso rápido)
        JPanel barraInf = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        barraInf.setOpaque(false);
        JButton btnLoc = crearBoton("📍  Localizar seleccionado", COLOR_ACENTO2);
        btnLoc.addActionListener(e -> localizarSeleccionado());
        barraInf.add(btnLoc);
        panel.add(barraInf, BorderLayout.SOUTH);

        return panel;
    }

    // ── Panel: Buscar / Filtrar ─────────────────────────────────────────
    /**
     * Permite filtrar el inventario por nombre, categoría y/o estado.
     *
     * @return panel de búsqueda y filtrado
     */
    private JPanel crearPanelBuscar() {
        JPanel panel = crearPanelBase("🔍  Buscar y Filtrar Material");

        // Card con los controles de filtro
        JPanel card = crearCard("Criterios de búsqueda");

        JPanel filtros = new JPanel(new GridBagLayout());
        filtros.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 12, 12);

        // Fila 0 — Nombre
        gbc.gridy = 0; gbc.gridx = 0; gbc.weightx = 0;
        filtros.add(etiqueta("Nombre / descripción"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.gridwidth = 3;
        txtBusqueda = campoTexto();
        filtros.add(txtBusqueda, gbc);

        // Fila 1 — Categoría + Estado
        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0; gbc.weightx = 0;
        filtros.add(etiqueta("Categoría"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        cbCategoria = new JComboBox<>(new String[]{
            "Todas", "PC Prácticas", "Componentes HW", "Equipos de Red",
            "Cableado Estructurado", "Herramientas", "Material Fungible"
        });
        estilizarCombo(cbCategoria);
        filtros.add(cbCategoria, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        filtros.add(etiqueta("Estado"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        cbEstado = new JComboBox<>(new String[]{
            "Todos", "Operativo", "Averiado", "En reparación", "Obsoleto"
        });
        estilizarCombo(cbEstado);
        filtros.add(cbEstado, gbc);

        // Fila 2 — Armario
        gbc.gridy = 2; gbc.gridx = 0; gbc.weightx = 0;
        filtros.add(etiqueta("Código armario"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JTextField txtArmario = campoTexto();
        filtros.add(txtArmario, gbc);

        // Botones
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 4;
        gbc.insets = new Insets(8, 0, 0, 0);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);
        JButton btnLimpiar = crearBoton("Limpiar", COLOR_PANEL);
        JButton btnBuscar  = crearBoton("🔍  Buscar", COLOR_ACENTO);
        btnLimpiar.addActionListener(e -> {
            txtBusqueda.setText("");
            txtArmario.setText("");
            cbCategoria.setSelectedIndex(0);
            cbEstado.setSelectedIndex(0);
            actualizarTablaResultados(modeloResultados, DATOS_PRUEBA);
        });
        btnBuscar.addActionListener(e -> {
            String nombre    = txtBusqueda.getText().toLowerCase();
            String categoria = cbCategoria.getSelectedItem().toString();
            String estado    = cbEstado.getSelectedItem().toString();
            String armario   = txtArmario.getText().toLowerCase();
            filtrarDatos(modeloResultados, nombre, categoria, estado, armario);
        });
        btnPanel.add(btnLimpiar);
        btnPanel.add(btnBuscar);
        filtros.add(btnPanel, gbc);

        card.add(filtros, BorderLayout.CENTER);
        panel.add(card, BorderLayout.NORTH);

        // Tabla de resultados
        DefaultTableModel modeloResultados = new DefaultTableModel(DATOS_PRUEBA, COLUMNAS) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        // Guardamos referencia para los listeners de arriba
        this.modeloResultados = modeloResultados;

        JTable tablaRes = crearTabla(modeloResultados);
        tablaRes.getColumnModel().getColumn(3).setCellRenderer(estadoRenderer());

        JScrollPane scroll = new JScrollPane(tablaRes);
        scroll.setOpaque(false);
        scroll.getViewport().setBackground(COLOR_FONDO);
        scroll.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));

        // Botón localizar desde resultados
        JPanel barraInf = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        barraInf.setOpaque(false);
        JButton btnLoc = crearBoton("📍  Localizar seleccionado", COLOR_ACENTO2);
        btnLoc.addActionListener(e -> {
            int fila = tablaRes.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(this,
                    "Selecciona un elemento de la tabla primero.",
                    "Sin selección", JOptionPane.INFORMATION_MESSAGE);
            } else {
                String nombre   = modeloResultados.getValueAt(fila, 1).toString();
                String armario  = modeloResultados.getValueAt(fila, 5).toString();
                String balda    = modeloResultados.getValueAt(fila, 6).toString();
                abrirWebLocalizar(nombre, armario, balda);
            }
        });
        barraInf.add(btnLoc);

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(barraInf, BorderLayout.SOUTH);

        return panel;
    }

    // Referencia al modelo de resultados de búsqueda (acceso desde lambdas)
    private DefaultTableModel modeloResultados;

    // ── Panel: Localizar Material ───────────────────────────────────────
    /**
     * Muestra el panel de localización: selección rápida de un elemento
     * y acceso directo al sitio web de visualización del taller.
     *
     * @return panel de localización
     */
    private JPanel crearPanelLocalizar() {
        JPanel panel = crearPanelBase("📍  Localizar Material en el Taller");

        // Card superior: selección
        JPanel cardSel = crearCard("Seleccionar elemento a localizar");

        JPanel fila = new JPanel(new GridBagLayout());
        fila.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 12);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        fila.add(etiqueta("Elemento"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        String[] nombres = new String[DATOS_PRUEBA.length + 1];
        nombres[0] = "— Selecciona un elemento —";
        for (int i = 0; i < DATOS_PRUEBA.length; i++) {
            nombres[i + 1] = DATOS_PRUEBA[i][0] + " · " + DATOS_PRUEBA[i][1];
        }
        JComboBox<String> cbElemento = new JComboBox<>(nombres);
        estilizarCombo(cbElemento);
        fila.add(cbElemento, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        JButton btnLocalizar = crearBoton("📍  Ver localización", COLOR_ACENTO);
        fila.add(btnLocalizar, gbc);

        cardSel.add(fila, BorderLayout.CENTER);
        panel.add(cardSel, BorderLayout.NORTH);

        // Card central: info del elemento + acceso web
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

        cardInfo.add(infoGrid,           BorderLayout.NORTH);
        cardInfo.add(lblItemSeleccionado, BorderLayout.CENTER);
        cardInfo.add(btnWeb,             BorderLayout.SOUTH);

        // Listener del botón localizar
        btnLocalizar.addActionListener(e -> {
            int idx = cbElemento.getSelectedIndex();
            if (idx == 0) {
                JOptionPane.showMessageDialog(this,
                    "Selecciona un elemento de la lista.",
                    "Sin selección", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            Object[] dato = DATOS_PRUEBA[idx - 1];
            lblsVal[0].setText(dato[1].toString());
            lblsVal[1].setText(dato[2].toString());
            lblsVal[2].setText(dato[5].toString());
            lblsVal[3].setText(dato[6].toString());
            lblItemSeleccionado.setText(
                "Elemento localizado: " + dato[1] + " → Armario " + dato[5] + ", Balda " + dato[6]);
            lblItemSeleccionado.setForeground(COLOR_OK);
            btnAbrirWeb.setEnabled(true);
            btnAbrirWeb.addActionListener(ev ->
                abrirWebLocalizar(dato[1].toString(), dato[5].toString(), dato[6].toString()));
        });

        panel.add(cardInfo, BorderLayout.CENTER);
        return panel;
    }

    // ── Panel: Informes ─────────────────────────────────────────────────
    /**
     * Permite al profesor generar listados del inventario.
     * Listado completo, por categoría/estado y por localización.
     *
     * @return panel de generación de informes
     */
    private JPanel crearPanelInformes() {
        JPanel panel = crearPanelBase("📄  Generar Listados");

        JPanel contenido = new JPanel(new GridLayout(1, 3, 16, 0));
        contenido.setOpaque(false);

        // ── Tarjeta 1: Listado completo
        JPanel c1 = crearCard("📋  Listado completo");
        JLabel d1 = new JLabel("<html><body style='width:150px;color:#94A3B8'>Exporta el inventario<br>completo del taller.</body></html>");
        d1.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JButton b1 = crearBoton("Generar listado", COLOR_ACENTO);
        b1.addActionListener(e -> generarListado("completo"));
        JPanel p1 = new JPanel(new BorderLayout(0, 12));
        p1.setOpaque(false);
        p1.add(d1, BorderLayout.CENTER);
        p1.add(b1, BorderLayout.SOUTH);
        c1.add(p1, BorderLayout.CENTER);

        // ── Tarjeta 2: Por categoría / estado
        JPanel c2 = crearCard("🗂  Por categoría / estado");
        JPanel sel2 = new JPanel(new GridLayout(4, 1, 0, 8));
        sel2.setOpaque(false);
        JComboBox<String> cbCat2 = new JComboBox<>(new String[]{
            "Todas", "PC Prácticas", "Componentes HW", "Equipos de Red",
            "Cableado Estructurado", "Herramientas", "Material Fungible"
        });
        JComboBox<String> cbEst2 = new JComboBox<>(new String[]{
            "Todos", "Operativo", "Averiado", "En reparación", "Obsoleto"
        });
        estilizarCombo(cbCat2);
        estilizarCombo(cbEst2);
        JButton b2 = crearBoton("Generar listado", COLOR_ACENTO);
        b2.addActionListener(e -> generarListado("categoria:" + cbCat2.getSelectedItem()
                + "|estado:" + cbEst2.getSelectedItem()));
        sel2.add(etiqueta("Categoría")); sel2.add(cbCat2);
        sel2.add(etiqueta("Estado"));    sel2.add(cbEst2);
        JPanel p2 = new JPanel(new BorderLayout(0, 12));
        p2.setOpaque(false);
        p2.add(sel2, BorderLayout.CENTER);
        p2.add(b2,   BorderLayout.SOUTH);
        c2.add(p2, BorderLayout.CENTER);

        // ── Tarjeta 3: Por localización
        JPanel c3 = crearCard("📍  Por armario / balda");
        JPanel sel3 = new JPanel(new GridLayout(4, 1, 0, 8));
        sel3.setOpaque(false);
        JComboBox<String> cbArm = new JComboBox<>(new String[]{"Todos", "A1", "A2", "A3", "A4", "A5"});
        JComboBox<String> cbBal = new JComboBox<>(new String[]{"Todas", "B1", "B2", "B3", "B4", "B5"});
        estilizarCombo(cbArm);
        estilizarCombo(cbBal);
        JButton b3 = crearBoton("Generar listado", COLOR_ACENTO);
        b3.addActionListener(e -> generarListado("armario:" + cbArm.getSelectedItem()
                + "|balda:" + cbBal.getSelectedItem()));
        sel3.add(etiqueta("Armario")); sel3.add(cbArm);
        sel3.add(etiqueta("Balda"));   sel3.add(cbBal);
        JPanel p3 = new JPanel(new BorderLayout(0, 12));
        p3.setOpaque(false);
        p3.add(sel3, BorderLayout.CENTER);
        p3.add(b3,   BorderLayout.SOUTH);
        c3.add(p3, BorderLayout.CENTER);

        contenido.add(c1);
        contenido.add(c2);
        contenido.add(c3);

        panel.add(contenido, BorderLayout.CENTER);

        JLabel nota = new JLabel("Los informes se exportarán en PDF o Excel según la configuración del sistema.");
        nota.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        nota.setForeground(COLOR_SUBTEXTO);
        nota.setHorizontalAlignment(SwingConstants.CENTER);
        nota.setBorder(new EmptyBorder(12, 0, 0, 0));
        panel.add(nota, BorderLayout.SOUTH);

        return panel;
    }

    // ── Barra de estado ─────────────────────────────────────────────────
    private JPanel crearBarraEstado() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(new Color(10, 16, 30));
        barra.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, COLOR_BORDE),
                new EmptyBorder(6, 16, 6, 16)));

        lblEstado = new JLabel("✓  Conectado a MySQL · " + DATOS_PRUEBA.length
                + " elementos en inventario · BD: taller_mhp  ·  Perfil: Profesor (solo lectura)");
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEstado.setForeground(COLOR_SUBTEXTO);

        JLabel lblFecha = new JLabel("14/05/2026");
        lblFecha.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFecha.setForeground(COLOR_SUBTEXTO);

        barra.add(lblEstado, BorderLayout.WEST);
        barra.add(lblFecha,  BorderLayout.EAST);
        return barra;
    }

    // ── Lógica ──────────────────────────────────────────────────────────

    /**
     * Filtra los datos del inventario según los criterios indicados
     * y actualiza el modelo de tabla de resultados.
     *
     * @param modelo    modelo de la tabla a actualizar
     * @param nombre    texto a buscar en el nombre (vacío = todos)
     * @param categoria categoría seleccionada ("Todas" = sin filtro)
     * @param estado    estado seleccionado ("Todos" = sin filtro)
     * @param armario   código de armario (vacío = todos)
     */
    private void filtrarDatos(DefaultTableModel modelo,
                              String nombre, String categoria,
                              String estado, String armario) {
        modelo.setRowCount(0);
        int encontrados = 0;
        for (Object[] fila : DATOS_PRUEBA) {
            boolean okNombre    = nombre.isEmpty()    || fila[1].toString().toLowerCase().contains(nombre);
            boolean okCategoria = categoria.equals("Todas") || fila[2].toString().equals(categoria);
            boolean okEstado    = estado.equals("Todos")    || fila[3].toString().equals(estado);
            boolean okArmario   = armario.isEmpty()   || fila[5].toString().toLowerCase().contains(armario);
            if (okNombre && okCategoria && okEstado && okArmario) {
                modelo.addRow(fila);
                encontrados++;
            }
        }
        lblEstado.setText("Búsqueda completada: " + encontrados + " resultado(s) encontrado(s).");
    }

    private void actualizarTablaResultados(DefaultTableModel modelo, Object[][] datos) {
        modelo.setRowCount(0);
        for (Object[] fila : datos) modelo.addRow(fila);
        lblEstado.setText("✓  Conectado a MySQL · " + DATOS_PRUEBA.length + " elementos.");
    }

    /**
     * Abre el navegador con el sitio web de localización del taller,
     * pasando armario y balda como parámetros en la URL.
     *
     * @param nombre  nombre del elemento a localizar
     * @param armario código del armario
     * @param balda   código de la balda
     */
    private void abrirWebLocalizar(String nombre, String armario, String balda) {
        String url = URL_WEB_TALLER + "?armario=" + armario + "&balda=" + balda
                + "&item=" + nombre.replace(" ", "%20");
        try {
            Desktop.getDesktop().browse(new URI(url));
            lblEstado.setText("✓  Abriendo web: " + url);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "No se pudo abrir el navegador.\nURL: " + url
                + "\n\nComprueba que MV2 está activa y accesible.",
                "Error al abrir el sitio web", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Intenta localizar el elemento seleccionado en la tabla de inventario
     * principal y abre el sitio web correspondiente.
     */
    private void localizarSeleccionado() {
        int fila = tablaInventario.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this,
                "Selecciona un elemento de la tabla primero.",
                "Sin selección", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String nombre  = modeloTabla.getValueAt(fila, 1).toString();
        String armario = modeloTabla.getValueAt(fila, 5).toString();
        String balda   = modeloTabla.getValueAt(fila, 6).toString();
        abrirWebLocalizar(nombre, armario, balda);
    }

    /**
     * Simula la generación de un informe según el criterio indicado.
     * En producción lanzaría la exportación a PDF/Excel.
     *
     * @param criterio descripción del tipo de informe a generar
     */
    private void generarListado(String criterio) {
        JOptionPane.showMessageDialog(this,
            "Informe generado correctamente.\n\nCriterio: " + criterio
            + "\n\n(En producción se exportaría a PDF / Excel según la configuración.)",
            "Informe generado", JOptionPane.INFORMATION_MESSAGE);
        lblEstado.setText("✓  Informe generado: " + criterio);
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

    // ── Helpers ──────────────────────────────────────────────────────────
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

    private JPanel crearCard(String titulo) {
        JPanel card = new JPanel(new BorderLayout(0, 12)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
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

    private TableCellRenderer estadoRenderer() {
        return new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                String s = val != null ? val.toString() : "";
                switch (s) {
                    case "Operativo"     -> setForeground(COLOR_OK);
                    case "Averiado"      -> setForeground(COLOR_PELIGRO);
                    case "En reparación" -> setForeground(COLOR_AMARILLO);
                    case "Obsoleto"      -> setForeground(COLOR_SUBTEXTO);
                    default              -> setForeground(COLOR_TEXTO);
                }
                setBackground(sel ? COLOR_SELECCION : (row % 2 == 0 ? COLOR_FILA_PAR : COLOR_FILA_IMPAR));
                return this;
            }
        };
    }

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

    private JLabel etiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(COLOR_SUBTEXTO);
        return lbl;
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
}
