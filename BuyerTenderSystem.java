import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

// ==================== OFFER CLASS ====================
class Offer {
    private String seller;
    private String item;
    private int price;
    private double rating;
    private int time;
    private String delivery;
    private String category;
    private int sweetness;
    private String portion;
    private String complexity;
    private int matchScore;

    public Offer(String seller, String item, int price, double rating, int time, 
                 String delivery, String category, int sweetness, String portion, String complexity) {
        this.seller = seller;
        this.item = item;
        this.price = price;
        this.rating = rating;
        this.time = time;
        this.delivery = delivery;
        this.category = category;
        this.sweetness = sweetness;
        this.portion = portion;
        this.complexity = complexity;
        this.matchScore = 0;
    }

    public int getTotalPrice() {
        int deliveryFee = delivery.equals("Free") ? 0 : Integer.parseInt(delivery.replaceAll("[^0-9]", ""));
        return price + deliveryFee;
    }

    public int matchesCriteria(Map<String, Boolean> criteria) {
        int score = 0;
        if (criteria.get("cheapest") && price <= 15000) score += 3;
        if (criteria.get("sweet") && sweetness >= 3) score += 2;
        if (criteria.get("large") && portion.equals("large")) score += 2;
        if (criteria.get("simple") && complexity.equals("simple")) score += 2;
        if (criteria.get("fastest") && time <= 15) score += 3;
        return score;
    }

    // Getters
    public String getSeller() { return seller; }
    public String getItem() { return item; }
    public int getPrice() { return price; }
    public double getRating() { return rating; }
    public int getTime() { return time; }
    public String getDelivery() { return delivery; }
    public String getCategory() { return category; }
    public int getSweetness() { return sweetness; }
    public String getPortion() { return portion; }
    public String getComplexity() { return complexity; }
    public int getMatchScore() { return matchScore; }
    public void setMatchScore(int score) { this.matchScore = score; }
}

// ==================== TENDER CLASS ====================
class Tender {
    private long id;
    private String request;
    private String timestamp;
    private List<Offer> offers;
    private Map<String, Boolean> criteria;
    private String category;

    public Tender(String request) {
        this.id = System.currentTimeMillis();
        this.request = request;
        this.timestamp = new java.text.SimpleDateFormat("HH:mm").format(new Date());
        this.offers = new ArrayList<>();
        this.criteria = parseCriteria(request);
        this.category = detectCategory(request.toLowerCase());
    }

    private Map<String, Boolean> parseCriteria(String text) {
        String lower = text.toLowerCase();
        Map<String, Boolean> crit = new HashMap<>();
        crit.put("cheapest", lower.contains("cheap") || lower.contains("budget") || lower.contains("affordable"));
        crit.put("sweet", lower.contains("sweet"));
        crit.put("large", lower.contains("large") || lower.contains("big") || lower.contains("jumbo") || lower.contains("lot"));
        crit.put("simple", lower.contains("simple") || lower.contains("easy") || lower.contains("basic"));
        crit.put("fastest", lower.contains("fast") || lower.contains("quick") || lower.contains("rapid"));
        return crit;
    }

    private String detectCategory(String text) {
        if (text.contains("padang") || text.contains("rendang")) return "padang";
        if (text.contains("rice") || text.contains("food") || text.contains("eat") || text.contains("meal")) return "makanan";
        if (text.contains("drink") || text.contains("beverage") || text.contains("coffee") || text.contains("tea") || text.contains("juice")) return "minuman";
        if (text.contains("charger") || text.contains("electronic") || text.contains("gadget")) return "elektronik";
        return "makanan";
    }

    public void addOffers(List<Offer> offersList) {
        for (Offer offer : offersList) {
            int score = offer.matchesCriteria(criteria);
            offer.setMatchScore(score);
        }
        offers.addAll(offersList);
        offers.sort((a, b) -> {
            int scoreCompare = Integer.compare(b.getMatchScore(), a.getMatchScore());
            return scoreCompare != 0 ? scoreCompare : Integer.compare(a.getPrice(), b.getPrice());
        });
    }

    public List<Offer> getTopOffers(int count) {
        return offers.subList(0, Math.min(count, offers.size()));
    }

    // Getters
    public long getId() { return id; }
    public String getRequest() { return request; }
    public String getTimestamp() { return timestamp; }
    public List<Offer> getOffers() { return offers; }
    public Map<String, Boolean> getCriteria() { return criteria; }
    public String getCategory() { return category; }
}

// ==================== OFFER DATABASE CLASS ====================
class OfferDatabase {
    private List<Offer> allOffers;

    public OfferDatabase() {
        allOffers = initializeOffers();
    }

    private List<Offer> initializeOffers() {
        List<Offer> offers = new ArrayList<>();
        
        // Padang Food
        offers.add(new Offer("Sederhana Restaurant", "Padang Rendang Rice", 22000, 4.9, 20, "5k", "padang", 2, "large", "medium"));
        offers.add(new Offer("Padang Raya Express", "Padang Ayam Pop Rice", 18000, 4.7, 15, "Free", "padang", 1, "normal", "simple"));
        offers.add(new Offer("Budget Padang", "Economy Padang Rice", 15000, 4.5, 12, "Free", "padang", 1, "normal", "simple"));
        offers.add(new Offer("Fast Padang", "Express Padang Rice", 16000, 4.6, 8, "Free", "padang", 1, "normal", "simple"));
        offers.add(new Offer("Royal Padang", "Premium Rendang Set", 30000, 4.9, 25, "10k", "padang", 2, "large", "medium"));
        offers.add(new Offer("Street Padang", "Street Style Padang", 13000, 4.4, 10, "Free", "padang", 1, "normal", "simple"));

        // Other Foods
        offers.add(new Offer("Sari Warung", "Special Fried Rice", 15000, 4.8, 15, "Free", "makanan", 2, "large", "simple"));
        offers.add(new Offer("Mama Kitchen", "Complete Mixed Rice", 12000, 4.5, 25, "Free", "makanan", 1, "normal", "simple"));
        offers.add(new Offer("Geprek Chicken House", "Jumbo Geprek Chicken", 18000, 4.9, 20, "5k", "makanan", 3, "large", "medium"));
        offers.add(new Offer("Burger Station", "Double Beef Burger", 32000, 4.8, 15, "5k", "makanan", 2, "normal", "simple"));
        offers.add(new Offer("Pizza Corner", "Personal Pepperoni Pizza", 35000, 4.6, 25, "10k", "makanan", 2, "normal", "medium"));
        offers.add(new Offer("Sushi Express", "8pcs California Roll", 30000, 4.9, 20, "Free", "makanan", 1, "normal", "medium"));

        // Drinks
        offers.add(new Offer("Juice Corner", "Fresh Fruit Ice", 8000, 4.7, 5, "Free", "minuman", 5, "large", "simple"));
        offers.add(new Offer("Our Coffee", "Palm Sugar Milk Coffee", 12000, 4.9, 8, "2k", "minuman", 4, "normal", "simple"));
        offers.add(new Offer("Sweet Tea Shop", "Jumbo Sweet Iced Tea", 5000, 4.4, 3, "Free", "minuman", 5, "large", "simple"));
        offers.add(new Offer("Boba Time", "Brown Sugar Boba Milk", 20000, 4.8, 12, "5k", "minuman", 5, "large", "medium"));

        return offers;
    }

    public List<Offer> getOffersByCategory(String category) {
        List<Offer> filtered = new ArrayList<>();
        for (Offer offer : allOffers) {
            if (offer.getCategory().equals(category)) {
                filtered.add(offer);
            }
        }
        return filtered;
    }
}

// ==================== CART ITEM CLASS ====================
class CartItem {
    private Offer offer;
    private int quantity;

    public CartItem(Offer offer, int quantity) {
        this.offer = offer;
        this.quantity = quantity;
    }

    public Offer getOffer() { return offer; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getTotalPrice() { return offer.getPrice() * quantity; }
}

// ==================== SHOPPING CART CLASS ====================
class ShoppingCart {
    private List<CartItem> items;

    public ShoppingCart() {
        items = new ArrayList<>();
    }

    public void addItem(Offer offer, int quantity) {
        for (CartItem item : items) {
            if (item.getOffer().getItem().equals(offer.getItem())) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        items.add(new CartItem(offer, quantity));
    }

    public void removeItem(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
        }
    }

    public List<CartItem> getItems() { return items; }
    
    public int getTotalPrice() {
        int total = 0;
        for (CartItem item : items) {
            total += item.getTotalPrice();
        }
        return total;
    }

    public void clear() { items.clear(); }
    public int getItemCount() { return items.size(); }
}

// ==================== MAIN GUI CLASS ====================
public class BuyerTenderSystem extends JFrame {
    private OfferDatabase database;
    private ShoppingCart cart;
    private Tender activeTender;
    private List<Tender> tenderHistory;
    
    private JTextArea requestArea;
    private JPanel offersPanel;
    private JLabel cartCountLabel;

    public BuyerTenderSystem() {
        database = new OfferDatabase();
        cart = new ShoppingCart();
        tenderHistory = new ArrayList<>();
        
        setTitle("BUYERS ARE KINGS - Tender System");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        createGUI();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void createGUI() {
        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(37, 99, 235));
        headerPanel.setPreferredSize(new Dimension(0, 100));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("BUYERS ARE KINGS - Tender System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        
        JButton cartBtn = new JButton("🛒 View Cart");
        cartBtn.setFont(new Font("Arial", Font.BOLD, 14));
        cartBtn.setBackground(new Color(34, 197, 94));
        cartBtn.setForeground(Color.WHITE);
        cartBtn.setFocusPainted(false);
        cartBtn.addActionListener(e -> showCart());
        
        cartCountLabel = new JLabel("(0 items)");
        cartCountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        cartCountLabel.setForeground(Color.WHITE);
        
        JPanel cartPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        cartPanel.setBackground(new Color(37, 99, 235));
        cartPanel.add(cartBtn);
        cartPanel.add(cartCountLabel);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(cartPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Left Panel - Search & Filter
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setPreferredSize(new Dimension(320, 0));
        leftPanel.setBackground(new Color(245, 247, 250));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Title
        JLabel reqLabel = new JLabel("🔍 Cari Penawaran");
        reqLabel.setFont(new Font("Arial", Font.BOLD, 20));
        reqLabel.setForeground(new Color(37, 99, 235));

        // Description
        JLabel descLabel = new JLabel("<html>Jelaskan apa yang Anda cari dengan detail untuk mendapatkan penawaran terbaik!</html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setForeground(new Color(100, 116, 139));

        // Text area
        requestArea = new JTextArea(5, 20);
        requestArea.setLineWrap(true);
        requestArea.setWrapStyleWord(true);
        requestArea.setFont(new Font("Arial", Font.PLAIN, 14));
        requestArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(37, 99, 235), 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        requestArea.setBackground(Color.WHITE);
        JScrollPane reqScroll = new JScrollPane(requestArea);

        // Start Button
        JButton startBtn = new JButton("🚀 Cari Penawaran");
        startBtn.setFont(new Font("Arial", Font.BOLD, 15));
        startBtn.setBackground(new Color(37, 99, 235));
        startBtn.setForeground(Color.WHITE);
        startBtn.setFocusPainted(false);
        startBtn.setPreferredSize(new Dimension(0, 50));
        startBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startBtn.addActionListener(e -> startTender());

        // Tips Panel
        JPanel tipsPanel = new JPanel();
        tipsPanel.setLayout(new BoxLayout(tipsPanel, BoxLayout.Y_AXIS));
        tipsPanel.setBackground(new Color(245, 247, 250));
        tipsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(191, 219, 254), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JLabel tipsTitle = new JLabel("💡 Tips");
        tipsTitle.setFont(new Font("Arial", Font.BOLD, 12));
        tipsTitle.setForeground(new Color(37, 99, 235));

        String[] tips = {
            "✓ Gunakan kata 'murah' untuk harga terjangkau",
            "✓ Gunakan 'cepat' untuk pengiriman kilat",
            "✓ Sebutkan kategori: makanan, minuman, dll"
        };
        
        tipsPanel.add(tipsTitle);
        tipsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        for (String tip : tips) {
            JLabel tipLabel = new JLabel(tip);
            tipLabel.setFont(new Font("Arial", Font.PLAIN, 11));
            tipLabel.setForeground(new Color(71, 85, 105));
            tipsPanel.add(tipLabel);
        }

        JPanel topLeft = new JPanel(new BorderLayout(5, 5));
        topLeft.setBackground(new Color(245, 247, 250));
        topLeft.add(reqLabel, BorderLayout.NORTH);
        topLeft.add(descLabel, BorderLayout.NORTH);
        
        JPanel centerLeft = new JPanel(new BorderLayout(5, 5));
        centerLeft.setBackground(new Color(245, 247, 250));
        centerLeft.add(reqScroll, BorderLayout.CENTER);
        centerLeft.add(startBtn, BorderLayout.SOUTH);
        
        leftPanel.add(reqLabel, BorderLayout.NORTH);
        leftPanel.add(centerLeft, BorderLayout.CENTER);
        leftPanel.add(tipsPanel, BorderLayout.SOUTH);
        add(leftPanel, BorderLayout.WEST);

        // Center Panel - Offers Display
        offersPanel = new JPanel();
        offersPanel.setLayout(new GridLayout(0, 2, 15, 15));
        offersPanel.setBackground(new Color(239, 246, 255));
        offersPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JScrollPane offersScroll = new JScrollPane(offersPanel);
        add(offersScroll, BorderLayout.CENTER);

        showEmptyState();
    }

    private void showEmptyState() {
        offersPanel.removeAll();
        offersPanel.setLayout(new GridBagLayout());
        
        JLabel emptyLabel = new JLabel("<html><center>🛒 Belum Ada Penawaran<br><br>Silakan ketik pencarian Anda di sebelah kiri untuk melihat penawaran terbaik!</center></html>");
        emptyLabel.setFont(new Font("Arial", Font.BOLD, 18));
        emptyLabel.setForeground(new Color(147, 197, 253));
        
        offersPanel.add(emptyLabel);
        offersPanel.revalidate();
        offersPanel.repaint();
    }

    private void startTender() {
        String request = requestArea.getText().trim();
        if (request.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Silakan masukkan pencarian Anda!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Tender tender = new Tender(request);
        List<Offer> categoryOffers = database.getOffersByCategory(tender.getCategory());
        tender.addOffers(categoryOffers);

        activeTender = tender;
        tenderHistory.add(0, tender);
        displayOffers();
        requestArea.setText("");
    }

    private void displayOffers() {
        offersPanel.removeAll();
        offersPanel.setLayout(new GridLayout(0, 2, 15, 15));
        
        for (Offer offer : activeTender.getTopOffers(6)) {
            offersPanel.add(createOfferCard(offer));
        }
        
        offersPanel.revalidate();
        offersPanel.repaint();
    }

    private JPanel createOfferCard(Offer offer) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(191, 219, 254), 2));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(37, 99, 235));
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel sellerLbl = new JLabel(offer.getSeller());
        sellerLbl.setFont(new Font("Arial", Font.BOLD, 14));
        sellerLbl.setForeground(Color.WHITE);
        
        JLabel ratingLbl = new JLabel(String.format("⭐ %.1f", offer.getRating()));
        ratingLbl.setForeground(Color.WHITE);
        
        header.add(sellerLbl, BorderLayout.WEST);
        header.add(ratingLbl, BorderLayout.EAST);

        // Body
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(Color.WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel itemLbl = new JLabel(offer.getItem());
        itemLbl.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel priceLbl = new JLabel(String.format("💰 Rp %,d", offer.getPrice()));
        priceLbl.setFont(new Font("Arial", Font.BOLD, 16));
        priceLbl.setForeground(new Color(34, 197, 94));

        JLabel infoLbl = new JLabel(String.format("⏱️ %d min | 🚚 %s", offer.getTime(), offer.getDelivery()));
        infoLbl.setFont(new Font("Arial", Font.PLAIN, 11));

        body.add(itemLbl);
        body.add(Box.createRigidArea(new Dimension(0, 8)));
        body.add(priceLbl);
        body.add(Box.createRigidArea(new Dimension(0, 5)));
        body.add(infoLbl);

        // Buttons
        JPanel btns = new JPanel(new GridLayout(1, 2, 8, 0));
        btns.setBackground(Color.WHITE);
        btns.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton cartBtn = new JButton("🛒 Cart");
        cartBtn.setBackground(new Color(234, 179, 8));
        cartBtn.setForeground(Color.WHITE);
        cartBtn.setFocusPainted(false);
        cartBtn.addActionListener(e -> addToCart(offer));

        JButton buyBtn = new JButton("✅ Buy");
        buyBtn.setBackground(new Color(34, 197, 94));
        buyBtn.setForeground(Color.WHITE);
        buyBtn.setFocusPainted(false);
        buyBtn.addActionListener(e -> buyNow(offer));

        btns.add(cartBtn);
        btns.add(buyBtn);
        body.add(btns);

        card.add(header, BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private void addToCart(Offer offer) {
        cart.addItem(offer, 1);
        updateCartCount();
        JOptionPane.showMessageDialog(this, 
            offer.getItem() + " berhasil ditambahkan ke keranjang!", 
            "✅ Berhasil", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void buyNow(Offer offer) {
        int confirm = JOptionPane.showConfirmDialog(this,
            String.format("Beli %s seharga Rp %,d?", offer.getItem(), offer.getPrice()),
            "Konfirmasi Pembelian", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this,
                String.format("✅ Pembelian Berhasil!\n\nProduk: %s\nPenjual: %s\nHarga: Rp %,d\nPengiriman: %d menit",
                    offer.getItem(), offer.getSeller(), offer.getPrice(), offer.getTime()),
                "✅ Sukses", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showCart() {
        JDialog cartDialog = new JDialog(this, "🛒 Keranjang Belanja", true);
        cartDialog.setSize(550, 450);
        cartDialog.setLayout(new BorderLayout(10, 10));
        cartDialog.setBackground(new Color(245, 247, 250));

        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBackground(new Color(245, 247, 250));
        itemsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        if (cart.getItems().isEmpty()) {
            JLabel emptyLabel = new JLabel("Keranjang Anda kosong");
            emptyLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            emptyLabel.setForeground(new Color(100, 116, 139));
            itemsPanel.add(emptyLabel);
        } else {
            for (int i = 0; i < cart.getItems().size(); i++) {
                CartItem item = cart.getItems().get(i);
                JPanel itemPanel = new JPanel(new BorderLayout(10, 0));
                itemPanel.setBackground(Color.WHITE);
                itemPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(191, 219, 254), 1),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)));
                itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
                
                JLabel infoLabel = new JLabel(String.format(
                    "<html><b>%s</b><br>Qty: %d | Rp %,d</html>",
                    item.getOffer().getItem(), 
                    item.getQuantity(), 
                    item.getTotalPrice()));
                infoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
                
                final int index = i;
                JButton removeBtn = new JButton("❌ Hapus");
                removeBtn.setPreferredSize(new Dimension(90, 30));
                removeBtn.setBackground(new Color(239, 68, 68));
                removeBtn.setForeground(Color.WHITE);
                removeBtn.setFocusPainted(false);
                removeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                removeBtn.addActionListener(e -> {
                    cart.removeItem(index);
                    updateCartCount();
                    cartDialog.dispose();
                    showCart();
                });
                
                itemPanel.add(infoLabel, BorderLayout.CENTER);
                itemPanel.add(removeBtn, BorderLayout.EAST);
                itemsPanel.add(itemPanel);
                itemsPanel.add(Box.createRigidArea(new Dimension(0, 8)));
            }
        }

        JScrollPane scroll = new JScrollPane(itemsPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(191, 219, 254), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        
        JLabel totalLbl = new JLabel(String.format("Total: Rp %,d", cart.getTotalPrice()));
        totalLbl.setFont(new Font("Arial", Font.BOLD, 20));
        totalLbl.setForeground(new Color(37, 99, 235));
        
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.setBackground(Color.WHITE);
        
        JButton closeBtn = new JButton("❌ Tutup");
        closeBtn.setBackground(new Color(107, 114, 128));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.setPreferredSize(new Dimension(0, 40));
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> cartDialog.dispose());
        
        JButton checkoutBtn = new JButton("✅ Checkout");
        checkoutBtn.setBackground(new Color(34, 197, 94));
        checkoutBtn.setForeground(Color.WHITE);
        checkoutBtn.setFocusPainted(false);
        checkoutBtn.setPreferredSize(new Dimension(0, 40));
        checkoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        checkoutBtn.addActionListener(e -> {
            if (cart.getItems().isEmpty()) {
                JOptionPane.showMessageDialog(cartDialog, "Keranjang Anda kosong!", "⚠️ Peringatan", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(cartDialog,
                    String.format("✅ Checkout Berhasil!\n\nTotal Pembayaran: Rp %,d\nJumlah Item: %d",
                        cart.getTotalPrice(), cart.getItemCount()),
                    "✅ Sukses", JOptionPane.INFORMATION_MESSAGE);
                cart.clear();
                updateCartCount();
                cartDialog.dispose();
            }
        });
        
        btnPanel.add(closeBtn);
        btnPanel.add(checkoutBtn);
        
        bottomPanel.add(totalLbl, BorderLayout.NORTH);
        bottomPanel.add(btnPanel, BorderLayout.SOUTH);

        cartDialog.add(scroll, BorderLayout.CENTER);
        cartDialog.add(bottomPanel, BorderLayout.SOUTH);
        
        cartDialog.setLocationRelativeTo(this);
        cartDialog.setVisible(true);
    }

    private void updateCartCount() {
        cartCountLabel.setText(String.format("(%d items)", cart.getItemCount()));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BuyerTenderSystem());
    }
}