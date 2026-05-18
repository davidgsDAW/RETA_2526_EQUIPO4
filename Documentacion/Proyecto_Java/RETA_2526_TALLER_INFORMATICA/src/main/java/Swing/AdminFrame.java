import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

/**
 * Ventana principal del perfil Administrador.
 * Permite gestión completa del inventario: alta, baja, modificación,
 * consulta, gestión de usuarios e informes.
 *
 * @author IES Miguel Herrero Pereda - DAW 2025/2026
 * @version 1.0
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

    // ── Datos de prueba ────────────────────────────────────────────────
    private static final String[] COLUMNAS = {
        "ID", "Nombre", "Categoría", "Estado", "Cantidad", "Armario", "Balda"
    };
    private static final Object[][] DATOS_PRUEBA = {
        {"001", "Portátil Dell Latitude", "PC Prácticas",          "Operativo",    "12", "A1", "B1"},
        {"002", "Switch HP 24p",          "Equipos de Red",        "Operativo",     "3", "A2", "B2"},
        {"003", "RAM DDR4 8GB",           "Componentes HW",        "Operativo",    "25", "A3", "B1"},
        {"004", "Router Cisco 2900",      "Equipos de Red",        "Averiado",      "1", "A2", "B3"},
        {"005", "Multímetro digital",     "Herramientas",          "Operativo",     "8", "A4", "B2"},
        {"006", "Cable UTP Cat6 (100m)",  "Material Fungible",     "Operativo",     "5", "A5", "B1"},
        {"007", "Placa base ATX",         "Componentes HW",        "En reparación", "2", "A3", "B3"},
        {"008", "Crimpadoras RJ45",       "Herramientas",          "Operativo",     "6", "A4", "B1"},
        {"009", "Disco SSD 500GB",        "Componentes HW",        "Operativo",    "10", "A3", "B2"},
        {"010", "Patch Panel 24p",        "Cableado Estructurado", "Operativo",     "2", "A2", "B1"},
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
                // Borde derecho sutil
                g2.setColor(COLOR_BORDE);
                g2.fillRect(getWidth() - 1, 0, 1, getHeight());
                g2.dispose();
            }
        };
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(0, 0, 0, 0));

        // Cabecera del sidebar
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

        // Separador
        sidebar.add(crearSeparadorSidebar());

        // Menú
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

        // Botón cerrar sesión
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

        JButton btnBuscar = crearBoton("Buscar", COLOR_ACENTO2);
        JButton btnRefresh = crearBoton("↻ Actualizar", COLOR_PANEL);

        toolbar.add(new JLabel("🔍") {{ setForeground(COLOR_SUBTEXTO); }});
        toolbar.add(busqueda);
        toolbar.add(combo);
        toolbar.add(btnBuscar);
        toolbar.add(btnRefresh);

        // Tabla
        modeloTabla = new DefaultTableModel(DATOS_PRUEBA, COLUMNAS) {
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
        acciones.add(crearBoton("➕ Añadir", COLOR_OK));
        acciones.add(crearBoton("✏️ Editar", COLOR_ACENTO));
        acciones.add(crearBoton("🗑 Eliminar", COLOR_PELIGRO));

        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(acciones, BorderLayout.SOUTH);

        return panel;
    }

    // ── Panel: Alta ────────────────────────────────────────────────────
    private JPanel crearPanelAlta() {
        JPanel panel = crearPanelBase("➕  Alta de Material");

        JPanel form = crearFormulario(new String[][]{
            {"Nombre del elemento",   "text"},
            {"Descripción",           "text"},
            {"Categoría",             "combo:PC Prácticas,Componentes HW,Equipos de Red,Herramientas,Material Fungible,Cableado Estructurado"},
            {"Estado",                "combo:Operativo,Averiado,En reparación,Obsoleto"},
            {"Cantidad",              "text"},
            {"Código de Armario",     "text"},
            {"Balda",                 "text"},
            {"Observaciones",         "text"},
        });

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        botones.add(crearBoton("Cancelar", COLOR_PANEL));
        botones.add(crearBoton("✓ Registrar elemento", COLOR_OK));

        panel.add(form, BorderLayout.CENTER);
        panel.add(botones, BorderLayout.SOUTH);
        return panel;
    }

    // ── Panel: Modificar ───────────────────────────────────────────────
    private JPanel crearPanelModificar() {
        JPanel panel = crearPanelBase("✏️  Modificar Material");

        JPanel busq = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        busq.setOpaque(false);
        JTextField tfId = new JTextField(10);
        tfId.setBackground(COLOR_PANEL); tfId.setForeground(COLOR_TEXTO);
        tfId.setBorder(new CompoundBorder(new LineBorder(COLOR_BORDE, 1, true), new EmptyBorder(6, 10, 6, 10)));
        busq.add(new JLabel("Buscar por ID:") {{ setForeground(COLOR_SUBTEXTO); setFont(new Font("Segoe UI", Font.PLAIN, 13)); }});
        busq.add(tfId);
        busq.add(crearBoton("Buscar", COLOR_ACENTO2));

        JPanel form = crearFormulario(new String[][]{
            {"Nombre del elemento",   "text"},
            {"Descripción",           "text"},
            {"Categoría",             "combo:PC Prácticas,Componentes HW,Equipos de Red,Herramientas,Material Fungible,Cableado Estructurado"},
            {"Estado",                "combo:Operativo,Averiado,En reparación,Obsoleto"},
            {"Cantidad",              "text"},
            {"Código de Armario",     "text"},
            {"Balda",                 "text"},
            {"Observaciones",         "text"},
        });

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        botones.add(crearBoton("Cancelar", COLOR_PANEL));
        botones.add(crearBoton("✓ Guardar cambios", COLOR_ACENTO));

        JPanel centro = new JPanel(new BorderLayout(0, 16));
        centro.setOpaque(false);
        centro.add(busq, BorderLayout.NORTH);
        centro.add(form, BorderLayout.CENTER);

        panel.add(centro, BorderLayout.CENTER);
        panel.add(botones, BorderLayout.SOUTH);
        return panel;
    }

    // ── Panel: Baja ────────────────────────────────────────────────────
    private JPanel crearPanelBaja() {
        JPanel panel = crearPanelBase("🗑  Baja de Material");

        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(new EmptyBorder(20, 0, 0, 0));

        JPanel busq = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        busq.setOpaque(false);
        JTextField tfId = new JTextField(12);
        tfId.setBackground(COLOR_PANEL); tfId.setForeground(COLOR_TEXTO);
        tfId.setBorder(new CompoundBorder(new LineBorder(COLOR_BORDE, 1, true), new EmptyBorder(6, 10, 6, 10)));
        busq.add(new JLabel("ID del elemento:") {{ setForeground(COLOR_SUBTEXTO); setFont(new Font("Segoe UI", Font.PLAIN, 13)); }});
        busq.add(tfId);
        busq.add(crearBoton("Buscar", COLOR_ACENTO2));
        busq.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Tarjeta de vista previa
        JPanel preview = crearCard("Vista previa del elemento a eliminar");
        JLabel[] campos = new JLabel[5];
        String[] etiq = {"Nombre:", "Categoría:", "Estado:", "Cantidad:", "Ubicación:"};
        String[] vals  = {"Portátil Dell Latitude", "PC Prácticas", "Operativo", "12", "Armario A1 · Balda B1"};
        JPanel infoGrid = new JPanel(new GridLayout(5, 2, 8, 8));
        infoGrid.setOpaque(false);
        for (int i = 0; i < 5; i++) {
            JLabel e = new JLabel(etiq[i]); e.setForeground(COLOR_SUBTEXTO); e.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            JLabel v = new JLabel(vals[i]);  v.setForeground(COLOR_TEXTO);    v.setFont(new Font("Segoe UI", Font.BOLD, 13));
            infoGrid.add(e); infoGrid.add(v);
        }
        preview.add(infoGrid, BorderLayout.CENTER);

        JPanel aviso = new JPanel(new FlowLayout(FlowLayout.LEFT));
        aviso.setOpaque(false);
        JLabel warn = new JLabel("⚠  Esta acción no se puede deshacer. El elemento quedará registrado en el histórico.");
        warn.setForeground(new Color(251, 191, 36));
        warn.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        aviso.add(warn);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        botones.add(crearBoton("Cancelar", COLOR_PANEL));
        botones.add(crearBoton("🗑 Confirmar baja", COLOR_PELIGRO));

        contenido.add(busq);
        contenido.add(Box.createVerticalStrut(16));
        contenido.add(preview);
        contenido.add(Box.createVerticalStrut(12));
        contenido.add(aviso);

        panel.add(contenido, BorderLayout.CENTER);
        panel.add(botones, BorderLayout.SOUTH);
        return panel;
    }

    // ── Panel: Usuarios ────────────────────────────────────────────────
    private JPanel crearPanelUsuarios() {
        JPanel panel = crearPanelBase("👤  Gestión de Usuarios");

        String[] cols = {"ID", "Usuario", "Nombre completo", "Rol", "Estado", "Último acceso"};
        Object[][] datos = {
            {"U01", "admin",     "Admin Sistema",       "Administrador", "Activo",   "14/05/2026 09:12"},
            {"U02", "profesor",  "Ana García López",    "Profesor",      "Activo",   "13/05/2026 16:45"},
            {"U03", "jmartinez", "Juan Martínez Ruiz",  "Profesor",      "Activo",   "12/05/2026 11:30"},
            {"U04", "lperez",    "Laura Pérez Sánchez", "Profesor",      "Inactivo", "01/04/2026 08:00"},
        };
        DefaultTableModel modelo = new DefaultTableModel(datos, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = crearTabla(modelo);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setOpaque(false);
        scroll.getViewport().setBackground(COLOR_FONDO);
        scroll.setBorder(new LineBorder(COLOR_BORDE, 1, true));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        botones.add(crearBoton("➕ Nuevo usuario", COLOR_OK));
        botones.add(crearBoton("✏️ Editar", COLOR_ACENTO));
        botones.add(crearBoton("🔒 Desactivar", COLOR_PELIGRO));

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(botones, BorderLayout.SOUTH);
        return panel;
    }

    // ── Panel: Informes ────────────────────────────────────────────────
    private JPanel crearPanelInformes() {
        JPanel panel = crearPanelBase("📊  Informes");

        JPanel grid = new JPanel(new GridLayout(2, 2, 16, 16));
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(16, 0, 0, 0));

        grid.add(crearTarjetaInforme("📋 Listado completo",
                "Inventario completo de todos los elementos del taller.",
                COLOR_ACENTO));
        grid.add(crearTarjetaInforme("🏷 Por categoría / estado",
                "Filtra el inventario por tipo de material o estado actual.",
                COLOR_ACENTO2));
        grid.add(crearTarjetaInforme("📍 Por armario / balda",
                "Localización de todos los elementos por ubicación física.",
                new Color(34, 197, 94)));
        grid.add(crearTarjetaInforme("📤 Exportar PDF / Excel",
                "Genera un archivo exportable con los informes seleccionados.",
                new Color(251, 146, 60)));

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
                // Banda de color superior
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

        // Importar
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

        // Exportar
        JPanel pExport = crearCard("📤  Exportar datos");
        JPanel expContent = new JPanel();
        expContent.setOpaque(false);
        expContent.setLayout(new BoxLayout(expContent, BoxLayout.Y_AXIS));
        JLabel lExpDesc = new JLabel("<html>Exporta el inventario completo o filtrado<br>al formato elegido.</html>");
        lExpDesc.setForeground(COLOR_SUBTEXTO); lExpDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JLabel lExpFormatos = new JLabel("Formatos: CSV, XLSX, PDF");
        lExpFormatos.setForeground(COLOR_ACENTO); lExpFormatos.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lExpFormatos.setBorder(new EmptyBorder(12, 0, 12, 0));
        JButton btnExpCSV   = crearBoton("Exportar CSV",  COLOR_ACENTO2);
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

        lblEstado = new JLabel("✓  Conectado a MySQL · 10 elementos en inventario · BD: taller_mhp");
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEstado.setForeground(COLOR_SUBTEXTO);

        JLabel lblFecha = new JLabel("14/05/2026");
        lblFecha.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFecha.setForeground(COLOR_SUBTEXTO);

        barra.add(lblEstado, BorderLayout.WEST);
        barra.add(lblFecha, BorderLayout.EAST);
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

    private JPanel crearFormulario(String[][] campos) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 12, 12);

        int fila = 0, col = 0;
        for (String[] campo : campos) {
            String nombre = campo[0];
            String tipo   = campo[1];

            gbc.gridx = col * 2;
            gbc.gridy = fila;
            gbc.weightx = 0;
            JLabel lbl = new JLabel(nombre);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lbl.setForeground(COLOR_SUBTEXTO);
            panel.add(lbl, gbc);

            gbc.gridx = col * 2 + 1;
            gbc.weightx = 1;
            if (tipo.startsWith("combo:")) {
                String[] opciones = tipo.substring(6).split(",");
                JComboBox<String> cb = new JComboBox<>(opciones);
                cb.setBackground(COLOR_PANEL);
                cb.setForeground(COLOR_TEXTO);
                cb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                panel.add(cb, gbc);
            } else {
                JTextField tf = new JTextField();
                tf.setBackground(COLOR_PANEL);
                tf.setForeground(COLOR_TEXTO);
                tf.setCaretColor(COLOR_ACENTO);
                tf.setBorder(new CompoundBorder(new LineBorder(COLOR_BORDE, 1, true), new EmptyBorder(6, 10, 6, 10)));
                tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                panel.add(tf, gbc);
            }

            col++;
            if (col >= 2) { col = 0; fila++; }
        }
        return panel;
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
