package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class ShoppingCartGUI extends JFrame {


    private Locale currentLocale = new Locale("en", "US");
    private ResourceBundle rb;
    private NumberFormat currencyFormatter;
    private final ShoppingCart cart = new ShoppingCart();


    private JLabel lblLanguage;
    private JLabel lblProduct;
    private JLabel lblPrice;
    private JLabel lblQuantity;
    private JButton btnAdd;
    private JButton btnClear;
    private JLabel lblTotalLabel;
    private JLabel lblTotalValue;


    private DefaultTableModel tableModel;
    private JTable itemTable;


    private JTextField txtProduct;
    private JTextField txtPrice;
    private JSpinner spinQuantity;

    private static final ResourceBundle.Control UTF8_CONTROL = new ResourceBundle.Control() {
        @Override
        public ResourceBundle newBundle(String baseName, Locale locale, String format,
                                        ClassLoader loader, boolean reload)
                throws IllegalAccessException, InstantiationException, IOException {
            String bundleName = toBundleName(baseName, locale);
            String resourceName = toResourceName(bundleName, "properties");
            InputStream stream = loader.getResourceAsStream(resourceName);
            if (stream == null) {
                return null;
            }
            try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                return new PropertyResourceBundle(reader);
            }
        }
    };

    public ShoppingCartGUI() {
        loadBundle();
        buildUI();
        applyLocale();
        setVisible(true);
    }


    private void loadBundle() {
        try {
            rb = ResourceBundle.getBundle("messages", currentLocale, UTF8_CONTROL);
        } catch (Exception e) {
            rb = ResourceBundle.getBundle("messages", new Locale("en", "US"), UTF8_CONTROL);
        }
        currencyFormatter = NumberFormat.getCurrencyInstance(currentLocale);
    }

    /** Re-read all labels from the current ResourceBundle. */
    private void applyLocale() {
        setTitle(rb.getString("appTitle"));
        lblLanguage.setText(rb.getString("selectLanguage"));
        lblProduct.setText(rb.getString("product"));
        lblPrice.setText(rb.getString("price"));
        lblQuantity.setText(rb.getString("quantity"));
        btnAdd.setText(rb.getString("addButton"));
        btnClear.setText(rb.getString("clearButton"));
        lblTotalLabel.setText(rb.getString("totalLabel"));

        tableModel.setColumnIdentifiers(new String[]{
                rb.getString("colProduct"),
                rb.getString("colPrice"),
                rb.getString("colQuantity"),
                rb.getString("colSubtotal")
        });

        boolean isArabic = "ar".equals(currentLocale.getLanguage());
        ComponentOrientation orientation = isArabic
                ? ComponentOrientation.RIGHT_TO_LEFT
                : ComponentOrientation.LEFT_TO_RIGHT;
        applyComponentOrientation(orientation);

        refreshTotal();
    }

    private void refreshTotal() {
        double total = cart.calculateTotal();
        lblTotalValue.setText(currencyFormatter.format(total));
    }


    private void buildUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(620, 480));
        setLayout(new BorderLayout(8, 8));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildTopPanel(),    BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);
        add(buildBottomPanel(), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    /** Language selector bar */
    private JPanel buildTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        panel.setBorder(BorderFactory.createTitledBorder(""));

        lblLanguage = new JLabel();
        panel.add(lblLanguage);

        String[] languages = {"English", "Suomi", "日本語","Svenska", "العربية "};
        JComboBox<String> langCombo = new JComboBox<>(languages);
        langCombo.setSelectedIndex(0);
        langCombo.addActionListener(e -> {
            int idx = langCombo.getSelectedIndex();
            switch (idx) {
                case 0 -> currentLocale = new Locale("en", "US");
                case 1 -> currentLocale = new Locale("fi", "FI");
                case 2 -> currentLocale = new Locale("ja", "JP");
                case 3 -> currentLocale = new Locale("sv", "SE");
                case 4 -> currentLocale = new Locale("ar", "SA");

            }
            loadBundle();
            applyLocale();
        });

        panel.add(langCombo);
        return panel;
    }

    /** Item-entry form */
    private JPanel buildCenterPanel() {
        JPanel outer = new JPanel(new BorderLayout(8, 8));

        // --- Form ---
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder(""));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        lblProduct  = new JLabel();
        lblPrice    = new JLabel();
        lblQuantity = new JLabel();

        txtProduct  = new JTextField(18);
        txtPrice    = new JTextField(10);
        spinQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));

        btnAdd   = new JButton();
        btnClear = new JButton();

        // Row 0 – product
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        form.add(lblProduct, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(txtProduct, gbc);

        // Row 1 – price
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        form.add(lblPrice, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(txtPrice, gbc);

        // Row 2 – quantity
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        form.add(lblQuantity, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(spinQuantity, gbc);

        // Row 3 – buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        btnRow.add(btnClear);
        btnRow.add(btnAdd);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        form.add(btnRow, gbc);

        btnAdd.addActionListener(e -> addItem());
        btnClear.addActionListener(e -> clearForm());

        // Allow Enter key in product/price fields to trigger Add
        txtProduct.addActionListener(e -> txtPrice.requestFocus());
        txtPrice.addActionListener(e -> btnAdd.doClick());

        // --- Table ---
        tableModel = new DefaultTableModel(new String[]{"Product","Price","Qty","Subtotal"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        itemTable = new JTable(tableModel);
        itemTable.setFillsViewportHeight(true);
        itemTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(itemTable);
        scrollPane.setPreferredSize(new Dimension(580, 200));

        outer.add(form, BorderLayout.NORTH);
        outer.add(scrollPane, BorderLayout.CENTER);
        return outer;
    }

    /** Total display */
    private JPanel buildBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        panel.setBorder(BorderFactory.createEtchedBorder());

        lblTotalLabel = new JLabel();
        lblTotalLabel.setFont(lblTotalLabel.getFont().deriveFont(Font.BOLD, 14f));
        lblTotalValue = new JLabel("0.00");
        lblTotalValue.setFont(lblTotalValue.getFont().deriveFont(Font.BOLD, 14f));
        lblTotalValue.setForeground(new Color(0, 120, 0));

        panel.add(lblTotalLabel);
        panel.add(lblTotalValue);
        return panel;
    }


    private void addItem() {
        String product = txtProduct.getText().trim();
        if (product.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    rb.getString("errorNoProduct"),
                    rb.getString("errorTitle"),
                    JOptionPane.WARNING_MESSAGE);
            txtProduct.requestFocus();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(txtPrice.getText().trim().replace(',', '.'));
            if (price < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    rb.getString("errorInvalidPrice"),
                    rb.getString("errorTitle"),
                    JOptionPane.WARNING_MESSAGE);
            txtPrice.requestFocus();
            return;
        }

        int quantity = (int) spinQuantity.getValue();

        ShoppingCart.Item item = cart.new Item(product, price, quantity);
        cart.addItem(item);

        tableModel.addRow(new Object[]{
                product,
                currencyFormatter.format(price),
                quantity,
                currencyFormatter.format(price * quantity)
        });

        refreshTotal();
        clearForm();
        txtProduct.requestFocus();
    }

    private void clearForm() {
        txtProduct.setText("");
        txtPrice.setText("");
        spinQuantity.setValue(1);
    }



    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(ShoppingCartGUI::new);
    }
}