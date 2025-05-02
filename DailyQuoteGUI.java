import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DailyQuoteGUI extends JFrame {
    private QuoteManager quoteManager;
    private JLabel quoteLabel;
    private JLabel authorLabel;
    private JButton randomButton;
    private JButton addButton;
    private JButton favButton;
    private JButton showFavButton;
    private JTextField quoteField;
    private JTextField authorField;
    private JList<String> quoteList;
    private DefaultListModel<String> listModel;
    private boolean showingFavorites = false;
    private List<Quote> currentQuotes;
    private int selectedIndex = -1;

    public DailyQuoteGUI() {
        quoteManager = new QuoteManager();
        setTitle("Daily Quote App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Attractive gradient background panel
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(255, 183, 197);
                Color color2 = new Color(144, 224, 239);
                GradientPaint gp = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                // Add some circles for pattern
                g2d.setColor(new Color(255, 255, 255, 60));
                for (int i = 0; i < 8; i++) {
                    int size = 60 + (i * 10);
                    g2d.fillOval((int)(Math.random()*getWidth()), (int)(Math.random()*getHeight()), size, size);
                }
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        // Top panel for quote display
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setOpaque(false);
        quoteLabel = new JLabel("Welcome to Daily Quote!", SwingConstants.CENTER);
        quoteLabel.setFont(new Font("Serif", Font.BOLD, 26));
        quoteLabel.setForeground(new Color(54, 54, 54));
        authorLabel = new JLabel("", SwingConstants.CENTER);
        authorLabel.setFont(new Font("SansSerif", Font.ITALIC, 18));
        authorLabel.setForeground(new Color(80, 80, 80));
        topPanel.add(quoteLabel);
        topPanel.add(authorLabel);
        backgroundPanel.add(topPanel, BorderLayout.NORTH);

        // Center panel for quote list
        listModel = new DefaultListModel<>();
        quoteList = new JList<>(listModel);
        quoteList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        quoteList.setFont(new Font("SansSerif", Font.PLAIN, 16));
        quoteList.setBackground(new Color(255, 255, 255, 180));
        quoteList.setSelectionBackground(new Color(255, 223, 186));
        JScrollPane scrollPane = new JScrollPane(quoteList);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        backgroundPanel.add(scrollPane, BorderLayout.CENTER);
        updateQuoteList(false);

        // Bottom panel for controls
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        randomButton = new JButton("Show Random Quote");
        addButton = new JButton("Add Quote");
        favButton = new JButton("Mark/Unmark Favorite");
        showFavButton = new JButton("Show Favorites");
        JButton exitButton = new JButton("Exit");
        styleButton(randomButton, new Color(255, 140, 105));
        styleButton(addButton, new Color(144, 224, 239));
        styleButton(favButton, new Color(255, 183, 197));
        styleButton(showFavButton, new Color(255, 223, 186));
        styleButton(exitButton, new Color(200, 200, 200));
        bottomPanel.add(randomButton);
        bottomPanel.add(addButton);
        bottomPanel.add(favButton);
        bottomPanel.add(showFavButton);
        bottomPanel.add(exitButton);
        backgroundPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Add quote input dialog
        addButton.addActionListener(_ -> showAddDialog());

        // Show random quote
        randomButton.addActionListener(_ -> showRandomQuote());

        // Mark/unmark favorite
        favButton.addActionListener(_ -> toggleFavorite());

        // Show favorites or all
        showFavButton.addActionListener(_ -> {
            showingFavorites = !showingFavorites;
            updateQuoteList(showingFavorites);
            showFavButton.setText(showingFavorites ? "Show All" : "Show Favorites");
        });

        // Exit button
        exitButton.addActionListener(_ -> System.exit(0));

        // List selection
        quoteList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectedIndex = quoteList.getSelectedIndex();
                displaySelectedQuote();
            }
        });

        // Initial display
        if (currentQuotes != null && !currentQuotes.isEmpty()) {
            quoteList.setSelectedIndex(0);
            displaySelectedQuote();
        }
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(6, 18, 6, 18),
            new RoundedBorder(18, color.darker())
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.brighter());
                button.setForeground(new Color(30,30,30));
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(6, 18, 6, 18),
                    new RoundedBorder(22, color)
                ));
                button.repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
                button.setForeground(Color.BLACK);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(6, 18, 6, 18),
                    new RoundedBorder(18, color.darker())
                ));
                button.repaint();
            }
        });
        button.setFocusPainted(false);
        button.setFocusable(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setMargin(new Insets(8, 18, 8, 18));
        button.setBorderPainted(false);
        button.setOpaque(false);
    }

    private void showAddDialog() {
        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Quote:"));
        quoteField = new JTextField();
        panel.add(quoteField);
        panel.add(new JLabel("Author:"));
        authorField = new JTextField();
        panel.add(authorField);
        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Quote", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String text = quoteField.getText().trim();
            String author = authorField.getText().trim();
            if (!text.isEmpty() && !author.isEmpty()) {
                quoteManager.addQuote(text, author);
                updateQuoteList(showingFavorites);
            } else {
                JOptionPane.showMessageDialog(this, "Both fields are required.");
            }
        }
    }

    private void showRandomQuote() {
        Quote q = quoteManager.getRandomQuote();
        if (q == null) {
            quoteLabel.setText("No quotes available.");
            authorLabel.setText("");
        } else {
            quoteLabel.setText('"' + q.getText() + '"');
            authorLabel.setText("- " + q.getAuthor() + (q.isFavorite() ? " (Favorite)" : ""));
        }
    }

    private void updateQuoteList(boolean favorites) {
        listModel.clear();
        currentQuotes = favorites ? quoteManager.getFavorites() : quoteManager.getAllQuotes();
        for (Quote q : currentQuotes) {
            listModel.addElement("\"" + q.getText() + "\" - \"" + q.getAuthor() + "\"" + (q.isFavorite() ? " (Favorite)" : ""));
        }
        if (!currentQuotes.isEmpty()) {
            quoteList.setSelectedIndex(0);
        } else {
            quoteLabel.setText("No quotes available.");
            authorLabel.setText("");
        }
    }

    private void displaySelectedQuote() {
        if (selectedIndex >= 0 && selectedIndex < currentQuotes.size()) {
            Quote q = currentQuotes.get(selectedIndex);
            quoteLabel.setText('"' + q.getText() + '"');
            authorLabel.setText("- " + q.getAuthor() + (q.isFavorite() ? " (Favorite)" : ""));
        }
    }

    private void toggleFavorite() {
        if (selectedIndex >= 0 && selectedIndex < currentQuotes.size()) {
            Quote q = currentQuotes.get(selectedIndex);
            int idx = quoteManager.getAllQuotes().indexOf(q);
            if (idx != -1) {
                quoteManager.markFavorite(idx, !q.isFavorite());
                updateQuoteList(showingFavorites);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DailyQuoteGUI().setVisible(true);
        });
    }
}


// Add this class at the end of the file
class RoundedBorder extends javax.swing.border.AbstractBorder {
    private int radius;
    private Color borderColor;
    public RoundedBorder(int radius, Color borderColor) {
        this.radius = radius;
        this.borderColor = borderColor;
    }
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x+1, y+1, width-3, height-3, radius, radius);
        g2.dispose();
    }
    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(this.radius+2, this.radius+2, this.radius+2, this.radius+2);
    }
    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = insets.right = insets.top = insets.bottom = this.radius+2;
        return insets;
    }
}