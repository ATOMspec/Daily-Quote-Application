import java.util.*;
import java.io.*;

public class QuoteManager {
    private List<Quote> quotes;
    private final String filePath = "quotes.txt";
    private Random random;

    public QuoteManager() {
        quotes = new ArrayList<>();
        random = new Random();
        loadQuotes();
    }

    private void loadQuotes() {
        File file = new File(filePath);
        if (!file.exists()) {
            // Add a few default quotes if file doesn't exist
            quotes.add(new Quote("The best way to get started is to quit talking and begin doing.", "Walt Disney"));
            quotes.add(new Quote("Don’t let yesterday take up too much of today.", "Will Rogers"));
            quotes.add(new Quote("It’s not whether you get knocked down, it’s whether you get up.", "Vince Lombardi"));
            saveQuotes();
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|", 3);
                if (parts.length >= 2) {
                    Quote q = new Quote(parts[0], parts[1]);
                    if (parts.length == 3 && parts[2].equals("fav")) {
                        q.setFavorite(true);
                    }
                    quotes.add(q);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading quotes: " + e.getMessage());
        }
    }

    public void saveQuotes() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (Quote q : quotes) {
                bw.write(q.getText() + "|" + q.getAuthor() + (q.isFavorite() ? "|fav" : ""));
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving quotes: " + e.getMessage());
        }
    }

    public Quote getRandomQuote() {
        if (quotes.isEmpty()) return null;
        return quotes.get(random.nextInt(quotes.size()));
    }

    public void addQuote(String text, String author) {
        quotes.add(new Quote(text, author));
        saveQuotes();
    }

    public List<Quote> getAllQuotes() {
        return quotes;
    }

    public List<Quote> getFavorites() {
        List<Quote> favs = new ArrayList<>();
        for (Quote q : quotes) {
            if (q.isFavorite()) favs.add(q);
        }
        return favs;
    }

    public void markFavorite(int index, boolean fav) {
        if (index >= 0 && index < quotes.size()) {
            quotes.get(index).setFavorite(fav);
            saveQuotes();
        }
    }
}