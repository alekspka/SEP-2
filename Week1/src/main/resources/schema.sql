CREATE DATABASE IF NOT EXISTS shopping_cart_localization
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE shopping_cart_localization;

CREATE TABLE IF NOT EXISTS cart_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    total_items INT NOT NULL,
    total_cost DOUBLE NOT NULL,
    language VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cart_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cart_record_id INT,
    item_number INT NOT NULL,
    price DOUBLE NOT NULL,
    quantity INT NOT NULL,
    subtotal DOUBLE NOT NULL,
    FOREIGN KEY (cart_record_id) REFERENCES cart_records(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS localization_strings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    `key` VARCHAR(100) NOT NULL,
    value VARCHAR(255) NOT NULL,
    language VARCHAR(10) NOT NULL
);

INSERT INTO localization_strings (`key`, value, language) VALUES
-- English
('appTitle',          'Shopping Cart',                               'en_US'),
('selectLanguage',    'Language:',                                   'en_US'),
('product',           'Product name:',                               'en_US'),
('price',             'Price:',                                      'en_US'),
('quantity',          'Quantity:',                                   'en_US'),
('addButton',         'Add Item',                                    'en_US'),
('clearButton',       'Clear',                                       'en_US'),
('saveButton',        'Save Cart',                                   'en_US'),
('totalLabel',        'Total:',                                      'en_US'),
('colProduct',        'Product',                                     'en_US'),
('colPrice',          'Unit Price',                                  'en_US'),
('colQuantity',       'Qty',                                         'en_US'),
('colSubtotal',       'Subtotal',                                    'en_US'),
('errorTitle',        'Input Error',                                 'en_US'),
('errorNoProduct',    'Please enter a product name.',                'en_US'),
('errorInvalidPrice', 'Please enter a valid price (e.g. 1.99).',    'en_US'),
-- Finnish
('appTitle',          'Ostoskori',                                   'fi_FI'),
('selectLanguage',    'Kieli:',                                      'fi_FI'),
('product',           'Tuotteen nimi:',                              'fi_FI'),
('price',             'Hinta:',                                      'fi_FI'),
('quantity',          'Määrä:',                                      'fi_FI'),
('addButton',         'Lisää tuote',                                 'fi_FI'),
('clearButton',       'Tyhjennä',                                    'fi_FI'),
('saveButton',        'Tallenna ostoskori',                          'fi_FI'),
('totalLabel',        'Yhteensä:',                                   'fi_FI'),
('colProduct',        'Tuote',                                       'fi_FI'),
('colPrice',          'Yksikköhinta',                                'fi_FI'),
('colQuantity',       'Määrä',                                       'fi_FI'),
('colSubtotal',       'Yhteishinta',                                 'fi_FI'),
('errorTitle',        'Syötevirhe',                                  'fi_FI'),
('errorNoProduct',    'Anna tuotteen nimi.',                         'fi_FI'),
('errorInvalidPrice', 'Anna kelvollinen hinta (esim. 1.99).',        'fi_FI'),
-- Swedish
('appTitle',          'Varukorg',                                    'sv_SE'),
('selectLanguage',    'Språk:',                                      'sv_SE'),
('product',           'Produktnamn:',                                'sv_SE'),
('price',             'Pris:',                                       'sv_SE'),
('quantity',          'Antal:',                                      'sv_SE'),
('addButton',         'Lägg till vara',                              'sv_SE'),
('clearButton',       'Rensa',                                       'sv_SE'),
('saveButton',        'Spara kundvagn',                              'sv_SE'),
('totalLabel',        'Totalt:',                                     'sv_SE'),
('colProduct',        'Produkt',                                     'sv_SE'),
('colPrice',          'Enhetspris',                                  'sv_SE'),
('colQuantity',       'Antal',                                       'sv_SE'),
('colSubtotal',       'Delsumma',                                    'sv_SE'),
('errorTitle',        'Inmatningsfel',                               'sv_SE'),
('errorNoProduct',    'Ange ett produktnamn.',                       'sv_SE'),
('errorInvalidPrice', 'Ange ett giltigt pris (t.ex. 1,99).',        'sv_SE'),
-- Japanese
('appTitle',          'ショッピングカート',                            'ja_JP'),
('selectLanguage',    '言語：',                                       'ja_JP'),
('product',           '商品名：',                                     'ja_JP'),
('price',             '価格：',                                       'ja_JP'),
('quantity',          '数量：',                                       'ja_JP'),
('addButton',         '商品を追加',                                   'ja_JP'),
('clearButton',       'クリア',                                       'ja_JP'),
('saveButton',        'カートを保存',                                  'ja_JP'),
('totalLabel',        '合計：',                                       'ja_JP'),
('colProduct',        '商品',                                        'ja_JP'),
('colPrice',          '単価',                                        'ja_JP'),
('colQuantity',       '数量',                                        'ja_JP'),
('colSubtotal',       '小計',                                        'ja_JP'),
('errorTitle',        '入力エラー',                                   'ja_JP'),
('errorNoProduct',    '商品名を入力してください。',                      'ja_JP'),
('errorInvalidPrice', '有効な価格を入力してください（例：1.99）。',        'ja_JP'),
-- Arabic
('appTitle',          'سلة التسوق',                                  'ar_SA'),
('selectLanguage',    'اللغة:',                                      'ar_SA'),
('product',           'المنتج:',                                     'ar_SA'),
('price',             'السعر:',                                      'ar_SA'),
('quantity',          'الكمية:',                                     'ar_SA'),
('addButton',         'إضافة منتج',                                  'ar_SA'),
('clearButton',       'مسح',                                         'ar_SA'),
('saveButton',        'حفظ السلة',                                   'ar_SA'),
('totalLabel',        'الإجمالي:',                                   'ar_SA'),
('colProduct',        'المنتج',                                      'ar_SA'),
('colPrice',          'سعر الوحدة',                                  'ar_SA'),
('colQuantity',       'الكمية',                                      'ar_SA'),
('colSubtotal',       'الإجمالي الفرعي',                             'ar_SA'),
('errorTitle',        'خطأ في الإدخال',                              'ar_SA'),
('errorNoProduct',    'يرجى إدخال اسم المنتج.',                      'ar_SA'),
('errorInvalidPrice', 'يرجى إدخال سعر صالح (مثال: 1.99).',          'ar_SA');
