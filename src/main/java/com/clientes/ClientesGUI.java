package com.clientes;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/**
 * Interfaz gráfica Swing para la gestión CRUD de Clientes.
 * Diseño moderno con tema oscuro, glassmorphism y animaciones sutiles.
 */
public class ClientesGUI extends JFrame {

    // ─── Paleta de colores ───────────────────────────────────────────────────
    private static final Color BG_DARK        = new Color(13, 17, 23);
    private static final Color BG_CARD        = new Color(22, 27, 34);
    private static final Color BG_INPUT       = new Color(30, 37, 46);
    private static final Color ACCENT         = new Color(88, 166, 255);
    private static final Color ACCENT_HOVER   = new Color(121, 192, 255);
    private static final Color DANGER         = new Color(248, 81, 73);
    private static final Color DANGER_HOVER   = new Color(255, 120, 110);
    private static final Color SUCCESS        = new Color(63, 185, 80);
    private static final Color WARNING        = new Color(210, 153, 34);
    private static final Color TEXT_PRIMARY   = new Color(230, 237, 243);
    private static final Color TEXT_SECONDARY = new Color(125, 139, 152);
    private static final Color BORDER_COLOR   = new Color(48, 54, 61);
    private static final Color ROW_EVEN       = new Color(22, 27, 34);
    private static final Color ROW_ODD        = new Color(27, 33, 40);
    private static final Color ROW_SELECT     = new Color(31, 55, 80);

    // ─── Fuentes ─────────────────────────────────────────────────────────────
    private static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_LABEL  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_INPUT  = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BTN    = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_TABLE  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 13);

    // ─── Componentes del formulario ───────────────────────────────────────────
    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtCorreo;
    private JTextField txtTelefono;
    private JTextField txtDireccion;
    private JTextField txtPais;
    private JTextField txtBuscar;

    // ─── Tabla ────────────────────────────────────────────────────────────────
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    // ─── Botones de acción ────────────────────────────────────────────────────
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JButton btnBuscar;
    private JButton btnMostrarTodos;

    // ─── DAO ─────────────────────────────────────────────────────────────────
    private final ClienteDAO dao = new ClienteDAO();

    // ─── Barra de estado ─────────────────────────────────────────────────────
    private JLabel lblEstado;

    // ═════════════════════════════════════════════════════════════════════════
    // CONSTRUCTOR
    // ═════════════════════════════════════════════════════════════════════════
    public ClientesGUI() {
        super("Gestión de Clientes — Rosaprima");
        ClienteDAO.init();
        configurarVentana();
        construirUI();
        cargarTodos();
        setVisible(true);
    }

    // ─── Configuración de la ventana ─────────────────────────────────────────
    private void configurarVentana() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setBackground(BG_DARK);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(0, 0));

        // Limpiar recursos JPA al cerrar
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ClienteDAO.close();
            }
        });
    }

    // ═════════════════════════════════════════════════════════════════════════
    // CONSTRUCCIÓN DE UI
    // ═════════════════════════════════════════════════════════════════════════
    private void construirUI() {
        // Header
        add(crearHeader(), BorderLayout.NORTH);

        // Panel central (formulario + tabla)
        JPanel centro = new JPanel(new BorderLayout(15, 0));
        centro.setBackground(BG_DARK);
        centro.setBorder(new EmptyBorder(15, 20, 10, 20));
        centro.add(crearPanelFormulario(), BorderLayout.WEST);
        centro.add(crearPanelTabla(),      BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        // Barra de estado
        add(crearBarraEstado(), BorderLayout.SOUTH);
    }

    // ─── Header ──────────────────────────────────────────────────────────────
    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_CARD);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                new EmptyBorder(14, 24, 14, 24)));

        // Logo + título
        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        izq.setOpaque(false);

        JLabel icono = new JLabel("🌸");
        icono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 1));
        textos.setOpaque(false);

        JLabel titulo = new JLabel("Rosaprima — Gestión de Clientes");
        titulo.setFont(FONT_TITLE);
        titulo.setForeground(TEXT_PRIMARY);

        JLabel subtitulo = new JLabel("Sistema de administración CRUD");
        subtitulo.setFont(FONT_LABEL);
        subtitulo.setForeground(TEXT_SECONDARY);

        textos.add(titulo);
        textos.add(subtitulo);
        izq.add(icono);
        izq.add(textos);
        header.add(izq, BorderLayout.WEST);

        // Badge de estado de conexión
        JLabel badge = crearBadge("● Conectado a PostgreSQL", SUCCESS);
        header.add(badge, BorderLayout.EAST);

        return header;
    }

    // ─── Panel del formulario ────────────────────────────────────────────────
    private JPanel crearPanelFormulario() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(20, 20, 20, 20)));
        card.setPreferredSize(new Dimension(310, 0));

        // Título sección
        JLabel tForm = new JLabel("Datos del Cliente");
        tForm.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tForm.setForeground(TEXT_PRIMARY);
        tForm.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(tForm);
        card.add(Box.createVerticalStrut(4));

        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_COLOR);
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        card.add(sep);
        card.add(Box.createVerticalStrut(16));

        // Campos
        txtId        = crearCampoReadOnly("ID (auto-generado)");
        txtNombre    = crearCampo("Nombre completo *");
        txtCorreo    = crearCampo("Correo electrónico *");
        txtTelefono  = crearCampo("Teléfono");
        txtDireccion = crearCampo("Dirección");
        txtPais      = crearCampo("País");

        agregarCampoForm(card, "ID",        txtId);
        agregarCampoForm(card, "Nombre *",  txtNombre);
        agregarCampoForm(card, "Correo *",  txtCorreo);
        agregarCampoForm(card, "Teléfono",  txtTelefono);
        agregarCampoForm(card, "Dirección", txtDireccion);
        agregarCampoForm(card, "País",      txtPais);

        card.add(Box.createVerticalStrut(20));

        // Botones principales
        btnGuardar    = crearBoton("💾  Guardar",    ACCENT,   ACCENT_HOVER);
        btnActualizar = crearBoton("✏️  Actualizar",  WARNING,  WARNING.brighter());
        btnEliminar   = crearBoton("🗑️  Eliminar",    DANGER,   DANGER_HOVER);
        btnLimpiar    = crearBoton("🔄  Limpiar",     new Color(48, 54, 61), new Color(68, 74, 81));

        btnGuardar.addActionListener(e -> guardar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        // Deshabilitar actualizar/eliminar hasta seleccionar fila
        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);

        for (JButton btn : new JButton[]{btnGuardar, btnActualizar, btnEliminar, btnLimpiar}) {
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            card.add(btn);
            card.add(Box.createVerticalStrut(8));
        }

        card.add(Box.createVerticalGlue());
        return card;
    }

    // ─── Panel de tabla ──────────────────────────────────────────────────────
    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BG_DARK);

        // Barra de búsqueda
        panel.add(crearBarraBusqueda(), BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"ID", "Nombre", "Correo", "Teléfono", "Dirección", "País"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
            @Override
            public Class<?> getColumnClass(int col) {
                return col == 0 ? Long.class : String.class;
            }
        };

        tabla = new JTable(modeloTabla);
        estilizarTabla();

        // Listener de selección
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                cargarFilaEnFormulario(tabla.getSelectedRow());
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBackground(BG_CARD);
        scroll.getViewport().setBackground(BG_CARD);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scroll.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scroll.getHorizontalScrollBar().setUI(new ModernScrollBarUI());

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // ─── Barra de búsqueda ───────────────────────────────────────────────────
    private JPanel crearBarraBusqueda() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        bar.setBackground(BG_DARK);

        txtBuscar = new JTextField(20);
        estilizarInput(txtBuscar);
        txtBuscar.putClientProperty("placeholder", "Buscar por nombre…");

        // Búsqueda al presionar Enter
        txtBuscar.addActionListener(e -> buscar());

        btnBuscar       = crearBotonSmall("🔍 Buscar",      ACCENT,   ACCENT_HOVER);
        btnMostrarTodos = crearBotonSmall("📋 Todos",       new Color(48, 54, 61), new Color(68, 74, 81));

        btnBuscar.addActionListener(e -> buscar());
        btnMostrarTodos.addActionListener(e -> { txtBuscar.setText(""); cargarTodos(); });

        bar.add(txtBuscar);
        bar.add(btnBuscar);
        bar.add(btnMostrarTodos);

        // Contador de registros
        JPanel barDer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        barDer.setBackground(BG_DARK);

        lblEstado = new JLabel("Cargando…");
        lblEstado.setFont(FONT_LABEL);
        lblEstado.setForeground(TEXT_SECONDARY);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_DARK);
        wrapper.add(bar, BorderLayout.WEST);
        wrapper.add(lblEstado, BorderLayout.EAST);
        return wrapper;
    }

    // ─── Barra de estado inferior ─────────────────────────────────────────────
    private JPanel crearBarraEstado() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 6));
        barra.setBackground(BG_CARD);
        barra.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));

        JLabel info = new JLabel("* Campos obligatorios   |   Selecciona una fila para editar o eliminar");
        info.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        info.setForeground(TEXT_SECONDARY);
        barra.add(info);
        return barra;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // OPERACIONES CRUD
    // ═════════════════════════════════════════════════════════════════════════

    private void guardar() {
        if (!validarCamposObligatorios()) return;
        try {
            Clientes c = new Clientes(
                    txtNombre.getText().trim(),
                    txtCorreo.getText().trim(),
                    txtTelefono.getText().trim(),
                    txtDireccion.getText().trim(),
                    txtPais.getText().trim()
            );
            dao.guardar(c);
            mostrarMensaje("✅ Cliente guardado exitosamente.", SUCCESS);
            limpiarFormulario();
            cargarTodos();
        } catch (Exception ex) {
            mostrarError("Error al guardar: " + ex.getMessage());
        }
    }

    private void actualizar() {
        String idTxt = txtId.getText().trim();
        if (idTxt.isEmpty()) { mostrarError("Selecciona un cliente de la tabla primero."); return; }
        if (!validarCamposObligatorios()) return;
        try {
            Clientes c = dao.buscarPorId(Long.parseLong(idTxt));
            if (c == null) { mostrarError("Cliente no encontrado."); return; }
            c.setNombre(txtNombre.getText().trim());
            c.setCorreo(txtCorreo.getText().trim());
            c.setTelefono(txtTelefono.getText().trim());
            c.setDireccion(txtDireccion.getText().trim());
            c.setPais(txtPais.getText().trim());
            dao.actualizar(c);
            mostrarMensaje("✅ Cliente actualizado exitosamente.", WARNING);
            limpiarFormulario();
            cargarTodos();
        } catch (Exception ex) {
            mostrarError("Error al actualizar: " + ex.getMessage());
        }
    }

    private void eliminar() {
        String idTxt = txtId.getText().trim();
        if (idTxt.isEmpty()) { mostrarError("Selecciona un cliente de la tabla primero."); return; }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Estás seguro de eliminar al cliente \"" + txtNombre.getText() + "\"?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            dao.eliminar(Long.parseLong(idTxt));
            mostrarMensaje("🗑️ Cliente eliminado.", DANGER);
            limpiarFormulario();
            cargarTodos();
        } catch (Exception ex) {
            mostrarError("Error al eliminar: " + ex.getMessage());
        }
    }

    private void buscar() {
        String texto = txtBuscar.getText().trim();
        if (texto.isEmpty()) { cargarTodos(); return; }
        try {
            List<Clientes> lista = dao.buscarPorNombre(texto);
            poblarTabla(lista);
            setEstado(lista.size() + " resultado(s) encontrado(s)");
        } catch (Exception ex) {
            mostrarError("Error en búsqueda: " + ex.getMessage());
        }
    }

    private void cargarTodos() {
        try {
            List<Clientes> lista = dao.listarTodos();
            poblarTabla(lista);
            setEstado(lista.size() + " cliente(s) en total");
        } catch (Exception ex) {
            mostrarError("Error al cargar clientes: " + ex.getMessage());
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // HELPERS DE UI
    // ═════════════════════════════════════════════════════════════════════════

    private void poblarTabla(List<Clientes> lista) {
        modeloTabla.setRowCount(0);
        for (Clientes c : lista) {
            modeloTabla.addRow(new Object[]{
                    c.getIdCliente(),
                    c.getNombre(),
                    c.getCorreo(),
                    c.getTelefono(),
                    c.getDireccion(),
                    c.getPais()
            });
        }
    }

    private void cargarFilaEnFormulario(int fila) {
        txtId.setText(String.valueOf(modeloTabla.getValueAt(fila, 0)));
        txtNombre.setText(str(modeloTabla.getValueAt(fila, 1)));
        txtCorreo.setText(str(modeloTabla.getValueAt(fila, 2)));
        txtTelefono.setText(str(modeloTabla.getValueAt(fila, 3)));
        txtDireccion.setText(str(modeloTabla.getValueAt(fila, 4)));
        txtPais.setText(str(modeloTabla.getValueAt(fila, 5)));
        btnActualizar.setEnabled(true);
        btnEliminar.setEnabled(true);
        btnGuardar.setEnabled(false);
    }

    private String str(Object o) { return o == null ? "" : o.toString(); }

    private void limpiarFormulario() {
        txtId.setText("");
        txtNombre.setText("");
        txtCorreo.setText("");
        txtTelefono.setText("");
        txtDireccion.setText("");
        txtPais.setText("");
        tabla.clearSelection();
        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);
        btnGuardar.setEnabled(true);
        txtNombre.requestFocus();
    }

    private boolean validarCamposObligatorios() {
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarError("El campo 'Nombre' es obligatorio.");
            txtNombre.requestFocus();
            return false;
        }
        if (txtCorreo.getText().trim().isEmpty()) {
            mostrarError("El campo 'Correo' es obligatorio.");
            txtCorreo.requestFocus();
            return false;
        }
        return true;
    }

    private void mostrarMensaje(String msg, Color color) {
        lblEstado.setText(msg);
        lblEstado.setForeground(color);
    }

    private void mostrarError(String msg) {
        lblEstado.setText("❌ " + msg);
        lblEstado.setForeground(DANGER);
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void setEstado(String txt) {
        lblEstado.setText(txt);
        lblEstado.setForeground(TEXT_SECONDARY);
    }

    // ─── Estilo de tabla ─────────────────────────────────────────────────────
    private void estilizarTabla() {
        tabla.setBackground(BG_CARD);
        tabla.setForeground(TEXT_PRIMARY);
        tabla.setFont(FONT_TABLE);
        tabla.setRowHeight(36);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setSelectionBackground(ROW_SELECT);
        tabla.setSelectionForeground(TEXT_PRIMARY);
        tabla.setFillsViewportHeight(true);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

        // Header
        JTableHeader header = tabla.getTableHeader();
        header.setBackground(new Color(30, 37, 46));
        header.setForeground(ACCENT);
        header.setFont(FONT_HEADER);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        header.setReorderingAllowed(false);

        // Renderer alternado
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                if (sel) {
                    setBackground(ROW_SELECT);
                } else {
                    setBackground(row % 2 == 0 ? ROW_EVEN : ROW_ODD);
                }
                setForeground(sel ? TEXT_PRIMARY : (col == 0 ? ACCENT : TEXT_PRIMARY));
                return this;
            }
        });

        // Renderer numérico (ID)
        tabla.setDefaultRenderer(Long.class, new DefaultTableCellRenderer() {
            { setHorizontalAlignment(JLabel.CENTER); }
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                setBackground(sel ? ROW_SELECT : (row % 2 == 0 ? ROW_EVEN : ROW_ODD));
                setForeground(ACCENT);
                setFont(FONT_TABLE.deriveFont(Font.BOLD));
                return this;
            }
        });

        // Anchos de columna
        int[] anchos = {60, 180, 200, 100, 200, 120};
        for (int i = 0; i < anchos.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
    }

    // ─── Fábrica de componentes ──────────────────────────────────────────────

    private void agregarCampoForm(JPanel panel, String label, JTextField campo) {
        JLabel lbl = crearEtiqueta(label);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lbl);
        panel.add(Box.createVerticalStrut(4));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        panel.add(campo);
        panel.add(Box.createVerticalStrut(12));
    }

    private JTextField crearCampo(String tooltip) {
        JTextField tf = new JTextField();
        tf.setToolTipText(tooltip);
        estilizarInput(tf);
        return tf;
    }

    private JTextField crearCampoReadOnly(String tooltip) {
        JTextField tf = crearCampo(tooltip);
        tf.setEditable(false);
        tf.setBackground(new Color(22, 27, 34));
        tf.setForeground(TEXT_SECONDARY);
        return tf;
    }

    private void estilizarInput(JTextField tf) {
        tf.setBackground(BG_INPUT);
        tf.setForeground(TEXT_PRIMARY);
        tf.setCaretColor(ACCENT);
        tf.setFont(FONT_INPUT);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(6, 10, 6, 10)));
        tf.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ACCENT, 1),
                        new EmptyBorder(6, 10, 6, 10)));
            }
            @Override public void focusLost(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_COLOR, 1),
                        new EmptyBorder(6, 10, 6, 10)));
            }
        });
    }

    private JButton crearBoton(String texto, Color bg, Color hover) {
        JButton btn = new JButton(texto);
        btn.setFont(FONT_BTN);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(bg);    }
        });
        return btn;
    }

    private JButton crearBotonSmall(String texto, Color bg, Color hover) {
        JButton btn = crearBoton(texto, bg, hover);
        btn.setFont(FONT_LABEL.deriveFont(Font.BOLD));
        return btn;
    }

    private JLabel crearEtiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(TEXT_SECONDARY);
        return lbl;
    }

    private JLabel crearBadge(String texto, Color color) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(color);
        lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 1),
                new EmptyBorder(4, 10, 4, 10)));
        return lbl;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // SCROLL BAR MODERNO
    // ═════════════════════════════════════════════════════════════════════════
    private static class ModernScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            thumbColor = new Color(48, 54, 61);
            trackColor = new Color(22, 27, 34);
        }
        @Override protected JButton createDecreaseButton(int o) { return invisibleBtn(); }
        @Override protected JButton createIncreaseButton(int o) { return invisibleBtn(); }
        private JButton invisibleBtn() {
            JButton btn = new JButton();
            btn.setPreferredSize(new Dimension(0, 0));
            return btn;
        }
        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fill(new RoundRectangle2D.Float(r.x + 2, r.y + 2, r.width - 4, r.height - 4, 6, 6));
            g2.dispose();
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // MAIN
    // ═════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        // Tema de sistema para JOptionPane
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Personalizar JOptionPane
        UIManager.put("OptionPane.background",        BG_CARD);
        UIManager.put("Panel.background",             BG_CARD);
        UIManager.put("OptionPane.messageForeground", TEXT_PRIMARY);
        UIManager.put("Button.background",            BG_INPUT);
        UIManager.put("Button.foreground",            TEXT_PRIMARY);

        SwingUtilities.invokeLater(ClientesGUI::new);
    }
}
