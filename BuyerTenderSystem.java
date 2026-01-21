import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.text.SimpleDateFormat;

// Product Model - Represents a seller's offer
class Product {
    private String seller, name, category, portion, complexity, delivery;
    private int price, deliveryTime, sweetness, matchScore;
    private double rating;

    public Product(String seller, String name, int price, double rating, int deliveryTime,
                   String delivery, String category, int sweetness, String portion, String complexity) {
        this.seller = seller;
        this.name = name;
        this.price = price;
        this.rating = rating;
        this.deliveryTime = deliveryTime;
        this.delivery = delivery;
        this.category = category;
        this.sweetness = sweetness;
        this.portion = portion;
        this.complexity = complexity;
        this.matchScore = 0;
    }

    public int getTotalPrice() {
        if (delivery.equalsIgnoreCase("Free")) return price;
        int fee = Integer.parseInt(delivery.replaceAll("[^0-9]", ""));
        return price + fee;
    }

    public int calculateMatchScore(Map<String, Boolean> criteria) {
        int score = 0;
        if (criteria.getOrDefault("cheapest", false) && price <= 15000) score += 3;
        if (criteria.getOrDefault("sweet", false) && sweetness >= 3) score += 2;
        if (criteria.getOrDefault("large", false) && "large".equals(portion)) score += 2;
        if (criteria.getOrDefault("simple", false) && "simple".equals(complexity)) score += 2;
        if (criteria.getOrDefault("fastest", false) && deliveryTime <= 15) score += 3;
        return score;
    }

    public String getSeller() { return seller; }
    public String getName() { return name; }
    public int getPrice() { return price; }
    public double getRating() { return rating; }
    public int getDeliveryTime() { return deliveryTime; }
    public String getDelivery() { return delivery; }
    public String getCategory() { return category; }
    public int getMatchScore() { return matchScore; }
    public void setMatchScore(int score) { this.matchScore = score; }
}

// Search Request - Models customer tender request
class SearchRequest {
    private long id;
    private String query;
    private String timestamp;
    private List<Product> results;
    private Map<String, Boolean> criteria;
    private String category;

    public SearchRequest(String query) {
        this.id = System.currentTimeMillis();
        this.query = query;
        this.timestamp = new SimpleDateFormat("HH:mm").format(new Date());
        this.results = new ArrayList<>();
        this.criteria = parseKeywords(query.toLowerCase());
        this.category = detectCategory(query.toLowerCase());
    }

    private Map<String, Boolean> parseKeywords(String text) {
        Map<String, Boolean> map = new HashMap<>();
        map.put("cheapest", text.matches(".*(cheap|budget|affordable).*"));
        map.put("sweet", text.contains("sweet"));
        map.put("large", text.matches(".*(large|big|jumbo|lot).*"));
        map.put("simple", text.matches(".*(simple|easy|basic).*"));
        map.put("fastest", text.matches(".*(fast|quick|rapid).*"));
        return map;
    }

    private String detectCategory(String text) {
        if (text.matches(".*(padang|rendang).*")) return "padang";
        if (text.matches(".*(drink|beverage|coffee|tea|juice).*")) return "beverage";
        if (text.matches(".*(charger|electronic|gadget).*")) return "electronics";
        return "food";
    }

    public void addProducts(List<Product> products) {
        for (Product p : products) {
            p.setMatchScore(p.calculateMatchScore(criteria));
        }
        results.addAll(products);
        results.sort((a, b) -> Integer.compare(b.getMatchScore(), a.getMatchScore()) != 0 ?
            Integer.compare(b.getMatchScore(), a.getMatchScore()) :
            Integer.compare(a.getPrice(), b.getPrice()));
    }

    public List<Product> getTopResults(int limit) {
        return results.subList(0, Math.min(limit, results.size()));
    }

    public long getId() { return id; }
    public String getQuery() { return query; }
    public String getCategory() { return category; }
    public List<Product> getResults() { return results; }
}

// Data Access Object - Manages product database
class ProductDAO {
    private List<Product> products;

    public ProductDAO() {
        this.products = initializeProducts();
    }

    private List<Product> initializeProducts() {
        List<Product> list = new ArrayList<>();
        list.add(new Product("Chef's Kitchen", "Padang Rice with Rendang", 22000, 4.9, 20, "5k", "padang", 2, "large", "medium"));
        list.add(new Product("Express Padang", "Ayam Pop Padang Rice", 18000, 4.7, 15, "Free", "padang", 1, "normal", "simple"));
        list.add(new Product("Budget Meals", "Economy Padang Rice", 15000, 4.5, 12, "Free", "padang", 1, "normal", "simple"));
        list.add(new Product("Fast Kitchen", "Express Padang Rice", 16000, 4.6, 8, "Free", "padang", 1, "normal", "simple"));
        list.add(new Product("Warung Sari", "Special Fried Rice", 15000, 4.8, 15, "Free", "food", 2, "large", "simple"));
        list.add(new Product("Home Cook", "Complete Mixed Rice", 12000, 4.5, 25, "Free", "food", 1, "normal", "simple"));
        list.add(new Product("Fried Chicken", "Jumbo Geprek Chicken", 18000, 4.9, 20, "5k", "food", 3, "large", "medium"));
        list.add(new Product("Burger Stop", "Double Beef Burger", 32000, 4.8, 15, "5k", "food", 2, "normal", "simple"));
        list.add(new Product("Pizza House", "Personal Pizza", 35000, 4.6, 25, "10k", "food", 2, "normal", "medium"));
        list.add(new Product("Fresh Juice", "Fresh Fruit Ice", 8000, 4.7, 5, "Free", "beverage", 5, "large", "simple"));
        list.add(new Product("Coffee Shop", "Palm Sugar Coffee", 12000, 4.9, 8, "2k", "beverage", 4, "normal", "simple"));
        list.add(new Product("Tea House", "Jumbo Sweet Iced Tea", 5000, 4.4, 3, "Free", "beverage", 5, "large", "simple"));
        list.add(new Product("Boba Store", "Brown Sugar Boba Milk", 20000, 4.8, 12, "5k", "beverage", 5, "large", "medium"));
        return list;
    }

    public List<Product> getByCategory(String category) {
        List<Product> filtered = new ArrayList<>();
        for (Product p : products) {
            if (p.getCategory().equals(category)) filtered.add(p);
        }
        return filtered;
    }
}

// Cart Item - Represents single cart entry
class CartEntry {
    private Product product;
    private int quantity;

    public CartEntry(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int qty) { this.quantity = qty; }
    public int getTotal() { return product.getPrice() * quantity; }
}

// Shopping Cart - Manages cart operations
class Cart {
    private List<CartEntry> items;

    public Cart() { this.items = new ArrayList<>(); }

    public void add(Product product, int qty) {
        for (CartEntry item : items) {
            if (item.getProduct().getName().equals(product.getName())) {
                item.setQuantity(item.getQuantity() + qty);
                return;
            }
        }
        items.add(new CartEntry(product, qty));
    }

    public void remove(int index) {
        if (index >= 0 && index < items.size()) items.remove(index);
    }

    public List<CartEntry> getItems() { return items; }
    public int getTotal() {
        return items.stream().mapToInt(CartEntry::getTotal).sum();
    }

    public void clear() { items.clear(); }
    public int count() { return items.size(); }
}

// Main GUI - Product Tender System
public class BuyerTenderSystem extends JFrame {
    private ProductDAO db;
    private Cart cart;
    private SearchRequest currentSearch;
    private JTextArea searchInput;
    private JPanel productsDisplay;
    private JLabel cartLabel;

    public BuyerTenderSystem() {
        db = new ProductDAO();
        cart = new Cart();
        initializeFrame();
    }

    private void initializeFrame() {
        setTitle("Product Tender System - Buy Smart");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        buildUI();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void buildUI() {
        add(createHeader(), BorderLayout.NORTH);
        add(createLeftPanel(), BorderLayout.WEST);
        JScrollPane center = new JScrollPane(createCenterPanel());
        add(center, BorderLayout.CENTER);
        showEmptyState();
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(37, 99, 235));
        panel.setPreferredSize(new Dimension(0, 80));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("Product Tender System");
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(Color.WHITE);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setBackground(new Color(37, 99, 235));
        
        JButton cartBtn = createButton("🛒 View Cart", new Color(34, 197, 94), e -> showCart());
        cartLabel = new JLabel("(0 items)");
        cartLabel.setFont(new Font("Arial", Font.BOLD, 12));
        cartLabel.setForeground(Color.WHITE);

        right.add(cartBtn);
        right.add(cartLabel);

        panel.add(title, BorderLayout.WEST);
        panel.add(right, BorderLayout.EAST);
        return panel;
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setPreferredSize(new Dimension(300, 0));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("🔍 Search Products");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(new Color(37, 99, 235));

        searchInput = new JTextArea(5, 20);
        searchInput.setLineWrap(true);
        searchInput.setWrapStyleWord(true);
        searchInput.setFont(new Font("Arial", Font.PLAIN, 13));
        searchInput.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(37, 99, 235), 2),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)));

        JButton searchBtn = createButton("🚀 Search", new Color(37, 99, 235), e -> performSearch());
        searchBtn.setPreferredSize(new Dimension(0, 45));

        JPanel center = new JPanel(new BorderLayout(5, 5));
        center.setBackground(new Color(245, 247, 250));
        center.add(new JScrollPane(searchInput), BorderLayout.CENTER);
        center.add(searchBtn, BorderLayout.SOUTH);

        JPanel tips = createTipsPanel();

        panel.add(title, BorderLayout.NORTH);
        panel.add(center, BorderLayout.CENTER);
        panel.add(tips, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createTipsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(191, 219, 254), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JLabel title = new JLabel("💡 Tips");
        title.setFont(new Font("Arial", Font.BOLD, 11));
        title.setForeground(new Color(37, 99, 235));

        String[] tips = {"Use 'cheap' for budget", "Use 'fast' for quick delivery", "Mention category: food, beverage"};
        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        for (String tip : tips) {
            JLabel label = new JLabel("• " + tip);
            label.setFont(new Font("Arial", Font.PLAIN, 10));
            label.setForeground(new Color(71, 85, 105));
            panel.add(label);
        }
        return panel;
    }

    private JPanel createCenterPanel() {
        productsDisplay = new JPanel();
        productsDisplay.setLayout(new GridLayout(0, 2, 15, 15));
        productsDisplay.setBackground(new Color(239, 246, 255));
        productsDisplay.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        return productsDisplay;
    }

    private void performSearch() {
        String query = searchInput.getText().trim();
        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search query!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        currentSearch = new SearchRequest(query);
        List<Product> categoryProducts = db.getByCategory(currentSearch.getCategory());
        currentSearch.addProducts(categoryProducts);
        displayProducts();
        searchInput.setText("");
    }

    private void displayProducts() {
        productsDisplay.removeAll();
        productsDisplay.setLayout(new GridLayout(0, 2, 15, 15));

        if (currentSearch == null || currentSearch.getResults().isEmpty()) {
            showEmptyState();
            return;
        }

        for (Product p : currentSearch.getTopResults(6)) {
            productsDisplay.add(createProductCard(p));
        }

        productsDisplay.revalidate();
        productsDisplay.repaint();
    }

    private JPanel createProductCard(Product product) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(191, 219, 254), 2));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(37, 99, 235));
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel seller = new JLabel(product.getSeller());
        seller.setFont(new Font("Arial", Font.BOLD, 13));
        seller.setForeground(Color.WHITE);

        JLabel rating = new JLabel(String.format("⭐ %.1f", product.getRating()));
        rating.setForeground(Color.WHITE);

        header.add(seller, BorderLayout.WEST);
        header.add(rating, BorderLayout.EAST);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(Color.WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel name = new JLabel(product.getName());
        name.setFont(new Font("Arial", Font.BOLD, 13));

        JLabel price = new JLabel(String.format("💰 IDR %,d", product.getPrice()));
        price.setFont(new Font("Arial", Font.BOLD, 15));
        price.setForeground(new Color(34, 197, 94));

        JLabel info = new JLabel(String.format("⏱️ %d min | 🚚 %s", product.getDeliveryTime(), product.getDelivery()));
        info.setFont(new Font("Arial", Font.PLAIN, 10));

        body.add(name);
        body.add(Box.createRigidArea(new Dimension(0, 8)));
        body.add(price);
        body.add(Box.createRigidArea(new Dimension(0, 5)));
        body.add(info);

        JPanel buttons = new JPanel(new GridLayout(1, 2, 8, 0));
        buttons.setBackground(Color.WHITE);
        buttons.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        buttons.add(createButton("🛒 Add", new Color(234, 179, 8), e -> addToCart(product)));
        buttons.add(createButton("✅ Buy", new Color(34, 197, 94), e -> buyNow(product)));

        body.add(buttons);
        card.add(header, BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JButton createButton(String text, Color bg, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 11));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(action);
        return btn;
    }

    private void addToCart(Product product) {
        cart.add(product, 1);
        updateCartCount();
        JOptionPane.showMessageDialog(this, product.getName() + " added to cart!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void buyNow(Product product) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Buy " + product.getName() + " for IDR " + product.getPrice() + "?",
            "Confirm Purchase", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this,
                "Purchase successful!\nProduct: " + product.getName() +
                "\nVendor: " + product.getSeller() + "\nDelivery: " + product.getDeliveryTime() + " min",
                "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showCart() {
        JDialog dialog = new JDialog(this, "Shopping Cart", true);
        dialog.setSize(500, 400);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel items = new JPanel();
        items.setLayout(new BoxLayout(items, BoxLayout.Y_AXIS));
        items.setBackground(new Color(245, 247, 250));
        items.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        if (cart.getItems().isEmpty()) {
            JLabel empty = new JLabel("Your cart is empty");
            empty.setFont(new Font("Arial", Font.PLAIN, 13));
            items.add(empty);
        } else {
            for (int i = 0; i < cart.getItems().size(); i++) {
                CartEntry entry = cart.getItems().get(i);
                JPanel item = new JPanel(new BorderLayout(10, 0));
                item.setBackground(Color.WHITE);
                item.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(191, 219, 254), 1),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)));
                item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

                JLabel info = new JLabel(String.format("<html><b>%s</b><br>Qty: %d | IDR %,d</html>",
                    entry.getProduct().getName(), entry.getQuantity(), entry.getTotal()));
                info.setFont(new Font("Arial", Font.PLAIN, 11));

                final int idx = i;
                JButton remove = createButton("❌ Remove", new Color(239, 68, 68), e -> {
                    cart.remove(idx);
                    updateCartCount();
                    dialog.dispose();
                    showCart();
                });
                remove.setPreferredSize(new Dimension(80, 30));

                item.add(info, BorderLayout.CENTER);
                item.add(remove, BorderLayout.EAST);
                items.add(item);
                items.add(Box.createRigidArea(new Dimension(0, 8)));
            }
        }

        JScrollPane scroll = new JScrollPane(items);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        JPanel bottom = new JPanel(new BorderLayout(10, 10));
        bottom.setBackground(Color.WHITE);
        bottom.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel total = new JLabel("Total: IDR " + String.format("%,d", cart.getTotal()));
        total.setFont(new Font("Arial", Font.BOLD, 18));
        total.setForeground(new Color(37, 99, 235));

        JPanel buttons = new JPanel(new GridLayout(1, 2, 10, 0));
        buttons.add(createButton("Close", new Color(107, 114, 128), e -> dialog.dispose()));
        buttons.add(createButton("Checkout", new Color(34, 197, 94), e -> {
            JOptionPane.showMessageDialog(dialog, "Checkout successful!");
            cart.clear();
            updateCartCount();
            dialog.dispose();
        }));

        bottom.add(total, BorderLayout.NORTH);
        bottom.add(buttons, BorderLayout.SOUTH);

        dialog.add(scroll, BorderLayout.CENTER);
        dialog.add(bottom, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showEmptyState() {
        productsDisplay.removeAll();
        productsDisplay.setLayout(new GridBagLayout());
        
        JLabel label = new JLabel("<html><center>🛍️ No Products Yet<br><br>Search for products to get started!</center></html>");
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(new Color(147, 197, 253));
        
        productsDisplay.add(label);
        productsDisplay.revalidate();
        productsDisplay.repaint();
    }

    private void updateCartCount() {
        cartLabel.setText("(" + cart.count() + " items)");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BuyerTenderSystem());
    }
}
