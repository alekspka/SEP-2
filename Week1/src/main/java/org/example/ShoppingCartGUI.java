package org.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class ShoppingCartGUI extends Application {


    private static final String STUDENT_NAME = "Aleksi Kaasalainen";

    private static final class LanguageOption {
        private final String label;
        private final Locale locale;

        private LanguageOption(String label, Locale locale) {
            this.label = label;
            this.locale = locale;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public static class RowData {
        private final String product;
        private final String price;
        private final int quantity;
        private final String subtotal;

        public RowData(String product, String price, int quantity, String subtotal) {
            this.product = product;
            this.price = price;
            this.quantity = quantity;
            this.subtotal = subtotal;
        }

        public String getProduct() { return product; }
        public String getPrice() { return price; }
        public int getQuantity() { return quantity; }
        public String getSubtotal() { return subtotal; }
    }

    private Locale currentLocale = new Locale("en", "US");
    private ResourceBundle rb;
    private Map<String, String> strings = new HashMap<>();
    private NumberFormat currencyFormatter;
    private final ShoppingCart cart = new ShoppingCart();

    private Stage stage;

    private Label lblLanguage;
    private Label lblProduct;
    private Label lblPrice;
    private Label lblQuantity;
    private Button btnAdd;
    private Button btnClear;
    private Button btnSave;
    private Label lblTotalLabel;
    private Label lblTotalValue;

    private ComboBox<LanguageOption> languageCombo;
    private TextField txtProduct;
    private TextField txtPrice;
    private Spinner<Integer> spinQuantity;

    private TableView<RowData> itemTable;
    private TableColumn<RowData, String> colProduct;
    private TableColumn<RowData, String> colPrice;
    private TableColumn<RowData, Integer> colQuantity;
    private TableColumn<RowData, String> colSubtotal;

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

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        loadBundle();
        Scene scene = new Scene(buildRoot(), 760, 560);
        stage.setScene(scene);
        applyLocale();
        stage.show();
    }

    private VBox buildRoot() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        root.getChildren().add(buildTopPanel());
        root.getChildren().add(buildCenterPanel());
        root.getChildren().add(buildBottomPanel());

        return root;
    }

    private HBox buildTopPanel() {
        HBox panel = new HBox(10);
        panel.setPadding(new Insets(5));

        lblLanguage = new Label();
        languageCombo = new ComboBox<>();
        languageCombo.getItems().addAll(
                new LanguageOption("English", new Locale("en", "US")),
                new LanguageOption("Suomi", new Locale("fi", "FI")),
                new LanguageOption("Svenska", new Locale("sv", "SE")),
                new LanguageOption("日本語", new Locale("ja", "JP")),
                new LanguageOption("العربية", new Locale("ar", "SA"))
        );
        languageCombo.getSelectionModel().selectFirst();
        languageCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                currentLocale = newVal.locale;
                loadBundle();
                applyLocale();
            }
        });

        panel.getChildren().addAll(lblLanguage, languageCombo);
        return panel;
    }

    private VBox buildCenterPanel() {
        VBox center = new VBox(10);

        GridPane form = new GridPane();
        form.setHgap(8);
        form.setVgap(8);
        form.setPadding(new Insets(5));

        lblProduct = new Label();
        lblPrice = new Label();
        lblQuantity = new Label();

        txtProduct = new TextField();
        txtPrice = new TextField();
        spinQuantity = new Spinner<>();
        spinQuantity.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 9999, 1));
        spinQuantity.setEditable(true);

        btnAdd = new Button();
        btnClear = new Button();

        form.add(lblProduct, 0, 0);
        form.add(txtProduct, 1, 0);
        form.add(lblPrice, 0, 1);
        form.add(txtPrice, 1, 1);
        form.add(lblQuantity, 0, 2);
        form.add(spinQuantity, 1, 2);

        HBox btnRow = new HBox(8, btnClear, btnAdd);
        form.add(btnRow, 1, 3);

        GridPane.setHgrow(txtProduct, Priority.ALWAYS);
        GridPane.setHgrow(txtPrice, Priority.ALWAYS);

        btnAdd.setOnAction(e -> addItem());
        btnClear.setOnAction(e -> clearForm());
        txtProduct.setOnAction(e -> txtPrice.requestFocus());
        txtPrice.setOnAction(e -> addItem());

        itemTable = new TableView<>();
        colProduct = new TableColumn<>();
        colPrice = new TableColumn<>();
        colQuantity = new TableColumn<>();
        colSubtotal = new TableColumn<>();

        colProduct.setCellValueFactory(new PropertyValueFactory<>("product"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        itemTable.getColumns().addAll(colProduct, colPrice, colQuantity, colSubtotal);
        itemTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        center.getChildren().addAll(form, itemTable);
        VBox.setVgrow(itemTable, Priority.ALWAYS);
        return center;
    }

    private HBox buildBottomPanel() {
        HBox panel = new HBox(10);
        panel.setPadding(new Insets(5));

        lblTotalLabel = new Label();
        lblTotalLabel.setStyle("-fx-font-size: 14px;");
        lblTotalValue = new Label("0.00");
        lblTotalValue.setTextFill(Color.DARKGREEN);
        lblTotalValue.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        btnSave = new Button();
        btnSave.setOnAction(e -> saveCart());

        panel.getChildren().addAll(lblTotalLabel, lblTotalValue, btnSave);
        return panel;
    }

    private void saveCart() {
        if (cart.getItems().isEmpty()) {
            showWarning(msg("errorNoProduct"));
            return;
        }
        String lang = currentLocale.getLanguage() + "_" + currentLocale.getCountry();
        CartService.saveCart(cart.getItems().size(), cart.calculateTotal(), lang, cart.getItems());
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Saved");
        alert.setHeaderText(null);
        alert.setContentText("Cart saved to database!");
        alert.showAndWait();
        cart.clear();
        itemTable.getItems().clear();
        refreshTotal();
    }

    private void loadBundle() {
        String langTag = currentLocale.getLanguage() + "_" + currentLocale.getCountry();
        strings = LocalizationService.loadStrings(langTag);
        try {
            rb = ResourceBundle.getBundle("messages", currentLocale, UTF8_CONTROL);
        } catch (Exception e) {
            rb = ResourceBundle.getBundle("messages", new Locale("en", "US"), UTF8_CONTROL);
        }
        currencyFormatter = NumberFormat.getCurrencyInstance(currentLocale);
    }

<<<<<<< HEAD
    private String msg(String key) {
        if (strings.containsKey(key)) return strings.get(key);
        try { return rb.getString(key); } catch (Exception e) { return key; }
    }
=======
    private void applyLocale() {
        stage.setTitle(rb.getString("appTitle") + " — " + STUDENT_NAME);
        lblLanguage.setText(rb.getString("selectLanguage"));
        lblProduct.setText(rb.getString("product"));
        lblPrice.setText(rb.getString("price"));
        lblQuantity.setText(rb.getString("quantity"));
        btnAdd.setText(rb.getString("addButton"));
        btnClear.setText(rb.getString("clearButton"));
        lblTotalLabel.setText(rb.getString("totalLabel"));
>>>>>>> 109180f96bcdf8e5895f56cb1422cc7919aecf24

    private void applyLocale() {
        stage.setTitle(msg("appTitle"));
        lblLanguage.setText(msg("selectLanguage"));
        lblProduct.setText(msg("product"));
        lblPrice.setText(msg("price"));
        lblQuantity.setText(msg("quantity"));
        btnAdd.setText(msg("addButton"));
        btnClear.setText(msg("clearButton"));
        btnSave.setText(msg("saveButton"));
        lblTotalLabel.setText(msg("totalLabel"));

        colProduct.setText(msg("colProduct"));
        colPrice.setText(msg("colPrice"));
        colQuantity.setText(msg("colQuantity"));
        colSubtotal.setText(msg("colSubtotal"));

        boolean isArabic = "ar".equals(currentLocale.getLanguage());
        NodeOrientation orientation = isArabic
                ? NodeOrientation.RIGHT_TO_LEFT
                : NodeOrientation.LEFT_TO_RIGHT;
        Parent root = (Parent) stage.getScene().getRoot();
        root.setNodeOrientation(orientation);
        root.setStyle(localeAwareFontStyle());

        refreshTotal();
    }

    /**
     * Container images often ship without CJK/Arabic fonts; Docker installs Noto.
     * On Windows/macOS, fallbacks still help when the default UI font lacks glyphs.
     */
    private String localeAwareFontStyle() {
        String lang = currentLocale.getLanguage();
        String families = switch (lang) {
            case "ja" -> "'Noto Sans CJK JP', 'Yu Gothic UI', 'Meiryo', 'MS UI Gothic', sans-serif";
            case "ko" -> "'Noto Sans CJK KR', 'Malgun Gothic', sans-serif";
            case "zh" -> "'Noto Sans CJK SC', 'Noto Sans CJK TC', 'Microsoft YaHei', sans-serif";
            case "ar" -> "'Noto Sans Arabic', 'Noto Naskh Arabic', 'Segoe UI', sans-serif";
            default -> "sans-serif";
        };
        return "-fx-font-family: " + families + ";";
    }

    private void addItem() {
        String product = txtProduct.getText().trim();
        if (product.isEmpty()) {
            showWarning(msg("errorNoProduct"));
            txtProduct.requestFocus();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(txtPrice.getText().trim().replace(',', '.'));
            if (price < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            showWarning(msg("errorInvalidPrice"));
            txtPrice.requestFocus();
            return;
        }

        int quantity = spinQuantity.getValue();
        ShoppingCart.Item item = cart.new Item(product, price, quantity);
        cart.addItem(item);

        itemTable.getItems().add(new RowData(
                product,
                currencyFormatter.format(price),
                quantity,
                currencyFormatter.format(price * quantity)
        ));

        refreshTotal();
        clearForm();
        txtProduct.requestFocus();
    }

    private void showWarning(String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(msg("errorTitle"));
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearForm() {
        txtProduct.clear();
        txtPrice.clear();
        spinQuantity.getValueFactory().setValue(1);
    }

    private void refreshTotal() {
        double total = cart.calculateTotal();
        lblTotalValue.setText(currencyFormatter.format(total));
    }

    public static void main(String[] args) {
        launch(args);
    }
}