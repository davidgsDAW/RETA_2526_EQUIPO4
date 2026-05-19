package App_Ejecutable;

import App_Ejecutable.AdminFrame;
import Conexion_Base_Datos.ConexionBD;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.*;

/**
 * Ventana de inicio de sesión de la aplicación de gestión del taller.
 *
 * Tabla utilizada:
 *   usuario (id_usuario, nombre, contrasena, id_rol)
 *   rol_usuario (id, nombre)  →  'ADMINISTRADOR' | 'PROFESOR'
 *
 * @author IES Miguel Herrero Pereda - DAW 2025/2026
 * @version 3.0  (adaptado a Taller_Informatica)
 */
public class LoginFrame extends JFrame {

    private static final Color COLOR_FONDO       = new Color(15, 23, 42);
    private static final Color COLOR_PANEL       = new Color(30, 41, 59);
    private static final Color COLOR_ACENTO      = new Color(56, 189, 248);
    private static final Color COLOR_ACENTO2     = new Color(99, 102, 241);
    private static final Color COLOR_TEXTO       = new Color(226, 232, 240);
    private static final Color COLOR_SUBTEXTO    = new Color(148, 163, 184);
    private static final Color COLOR_CAMPO       = new Color(51, 65, 85);
    private static final Color COLOR_CAMPO_BORDE = new Color(71, 85, 105);
    private static final Color COLOR_BTN         = new Color(56, 189, 248);
    private static final Color COLOR_BTN_HOVER   = new Color(14, 165, 233);
    private static final Color COLOR_ERROR       = new Color(248, 113, 113);

    private JTextField     txtUsuario;
    private JPasswordField txtContrasena;
    private JLabel         lblMensaje;
    private JButton        btnLogin;
    private JCheckBox      chkMostrar;

    public LoginFrame() {
        setTitle("Taller IES Miguel Herrero · Acceso al sistema");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(460, 560);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_FONDO);
        setLayout(new BorderLayout());

        add(crearPanelCabecera(),   BorderLayout.NORTH);
        add(crearPanelFormulario(), BorderLayout.CENTER);
        add(crearPanelPie(),        BorderLayout.SOUTH);
    }

    // ── Cabecera ───────────────────────────────────────────────────────
    private JPanel crearPanelCabecera() {
        JPanel panel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(56, 189, 248, 30),
                        getWidth(), getHeight(), new Color(99, 102, 241, 30));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(40, 40, 20, 40));

        JLabel icono = new JLabel("🖥") {
            @Override public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(56, 189, 248, 40));
                g2.fillOval(0, 0, getWidth(), getHeight());
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        icono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        icono.setAlignmentX(Component.CENTER_ALIGNMENT);
        icono.setPreferredSize(new Dimension(80, 80));

        JLabel titulo = new JLabel("Gestión del Taller");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(COLOR_TEXTO);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel("IES Miguel Herrero Pereda · DAW 2025/2026");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(COLOR_SUBTEXTO);
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSeparator sep = new JSeparator() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        sep.setMaximumSize(new Dimension(300, 2));
        sep.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(icono);
        panel.add(Box.createVerticalStrut(12));
        panel.add(titulo);
        panel.add(Box.createVerticalStrut(6));
        panel.add(subtitulo);
        panel.add(Box.createVerticalStrut(20));
        panel.add(sep);
        return panel;
    }

    // ── Formulario ─────────────────────────────────────────────────────
    private JPanel crearPanelFormulario() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);

        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_PANEL);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(71, 85, 105));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 16, 0);
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1;

        card.add(crearEtiqueta("Usuario"), gbc); gbc.gridy++;
        txtUsuario = crearCampoTexto("Introduce tu nombre de usuario");
        card.add(txtUsuario, gbc);

        gbc.gridy++;
        card.add(crearEtiqueta("Contraseña"), gbc); gbc.gridy++;
        txtContrasena = crearCampoPassword("••••••••");
        card.add(txtContrasena, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 8, 0);
        chkMostrar = new JCheckBox("Mostrar contraseña");
        chkMostrar.setForeground(COLOR_SUBTEXTO);
        chkMostrar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkMostrar.setOpaque(false);
        chkMostrar.setFocusPainted(false);
        chkMostrar.addActionListener(e ->
            txtContrasena.setEchoChar(chkMostrar.isSelected() ? (char) 0 : '•'));
        card.add(chkMostrar, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 12, 0);
        lblMensaje = new JLabel(" ");
        lblMensaje.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblMensaje.setForeground(COLOR_ERROR);
        lblMensaje.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(lblMensaje, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 0, 0);
        btnLogin = crearBotonLogin();
        card.add(btnLogin, gbc);

        outer.add(card, new GridBagConstraints());

        // Enter en password → login; Enter en usuario → foco a password
        txtContrasena.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) autenticar();
            }
        });
        txtUsuario.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) txtContrasena.requestFocus();
            }
        });

        return outer;
    }

    private JPanel crearPanelPie() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, 20, 0));
        JLabel hint = new JLabel("Sistema de gestión del taller · BD: Taller_Informatica");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(new Color(71, 85, 105));
        panel.add(hint);
        return panel;
    }

    // ── Helpers de UI ─────────────────────────────────────────────────
    private JLabel crearEtiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(COLOR_SUBTEXTO);
        return lbl;
    }

    private JTextField crearCampoTexto(String placeholder) {
        JTextField campo = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_CAMPO);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        campo.setOpaque(false);
        campo.setForeground(COLOR_TEXTO);
        campo.setCaretColor(COLOR_ACENTO);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setBorder(new CompoundBorder(
                new LineBorder(COLOR_CAMPO_BORDE, 1, true),
                new EmptyBorder(10, 14, 10, 14)));
        campo.setPreferredSize(new Dimension(0, 44));
        campo.setText(placeholder);
        campo.setForeground(COLOR_SUBTEXTO);
        campo.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (campo.getText().equals(placeholder)) {
                    campo.setText(""); campo.setForeground(COLOR_TEXTO);
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (campo.getText().isEmpty()) {
                    campo.setText(placeholder); campo.setForeground(COLOR_SUBTEXTO);
                }
            }
        });
        return campo;
    }

    private JPasswordField crearCampoPassword(String placeholder) {
        JPasswordField campo = new JPasswordField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_CAMPO);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        campo.setOpaque(false);
        campo.setForeground(COLOR_TEXTO);
        campo.setCaretColor(COLOR_ACENTO);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setEchoChar('•');
        campo.setBorder(new CompoundBorder(
                new LineBorder(COLOR_CAMPO_BORDE, 1, true),
                new EmptyBorder(10, 14, 10, 14)));
        campo.setPreferredSize(new Dimension(0, 44));
        return campo;
    }

    private JButton crearBotonLogin() {
        JButton btn = new JButton("Iniciar sesión") {
            private boolean hover = false;
            { addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hover ? COLOR_BTN_HOVER : COLOR_BTN);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(COLOR_FONDO);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 46));
        btn.addActionListener(e -> autenticar());
        return btn;
    }

    // ── Lógica de autenticación ────────────────────────────────────────
    /**
     * Consulta la tabla 'usuario' junto con 'rol_usuario' para validar
     * las credenciales. El campo de búsqueda es 'nombre' (VARCHAR 100)
     * y se compara con 'contrasena' (VARCHAR 255).
     *
     * Roles posibles: 'ADMINISTRADOR' → AdminFrame
     *                 'PROFESOR'      → ProfesorFrame
     *
     * NOTA: en producción almacena la contraseña con BCrypt y compara
     *       con BCrypt.checkpw(plain, hash).
     */
    private void autenticar() {
        String usuario    = txtUsuario.getText().trim();
        String contrasena = new String(txtContrasena.getPassword());

        lblMensaje.setForeground(COLOR_ERROR);

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            lblMensaje.setText("Por favor, introduce usuario y contraseña.");
            return;
        }

        btnLogin.setEnabled(false);
        lblMensaje.setForeground(COLOR_SUBTEXTO);
        lblMensaje.setText("Verificando credenciales...");

        new Thread(() -> {
            // JOIN con rol_usuario para obtener el nombre del rol
            String sql =
                "SELECT u.id_usuario, u.nombre, r.nombre AS rol " +
                "FROM usuario u " +
                "JOIN rol_usuario r ON r.id = u.id_rol " +
                "WHERE u.nombre = ? AND u.contrasena = ?";

            try (Connection con = ConexionBD.getInstance().getConn();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, usuario);
                ps.setString(2, contrasena); // reemplaza por BCrypt en producción

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    String nombre = rs.getString("nombre");
                    String rol    = rs.getString("rol"); // 'ADMINISTRADOR' o 'PROFESOR'
                    int    idUsr  = rs.getInt("id_usuario");

                    SwingUtilities.invokeLater(() -> {
                        lblMensaje.setForeground(new Color(74, 222, 128));
                        lblMensaje.setText("✓ Acceso correcto — " + rol);
                        Timer t = new Timer(600, e -> {
                            dispose();
                            if ("ADMINISTRADOR".equals(rol)) {
                                new AdminFrame(nombre, idUsr).setVisible(true);
                            } else {
//                                new ProfesorFrame(nombre, idUsr).setVisible(true);
                            }
                        });
                        t.setRepeats(false);
                        t.start();
                    });
                } else {
                    SwingUtilities.invokeLater(() -> {
                        lblMensaje.setForeground(COLOR_ERROR);
                        lblMensaje.setText("✗ Usuario o contraseña incorrectos.");
                        btnLogin.setEnabled(true);
                    });
                }

            } catch (SQLException ex) {
                SwingUtilities.invokeLater(() -> {
                    lblMensaje.setForeground(COLOR_ERROR);
                    lblMensaje.setText("✗ Error de conexión a la base de datos.");
                    btnLogin.setEnabled(true);
                    JOptionPane.showMessageDialog(LoginFrame.this,
                        "No se pudo conectar a la base de datos:\n" + ex.getMessage(),
                        "Error de BD", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
