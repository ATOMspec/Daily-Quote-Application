import java.util.List;
import java.util.Scanner;

public class DailyQuoteApp {
    private static QuoteManager quoteManager = new QuoteManager();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    showRandomQuote();
                    break;
                case "2":
                    addQuote();
                    break;
                case "3":
                    listAllQuotes();
                    break;
                case "4":
                    listFavorites();
                    break;
                case "5":
                    markFavorite(true);
                    break;
                case "6":
                    markFavorite(false);
                    break;
                case "0":
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n--- Daily Quote CLI ---");
        System.out.println("1. Show random quote");
        System.out.println("2. Add a new quote");
        System.out.println("3. List all quotes");
        System.out.println("4. List favorite quotes");
        System.out.println("5. Mark a quote as favorite");
        System.out.println("6. Unmark a quote as favorite");
        System.out.println("0. Exit");
        System.out.print("Choose an option: ");
    }

    private static void showRandomQuote() {
        Quote q = quoteManager.getRandomQuote();
        if (q == null) {
            System.out.println("No quotes available.");
        } else {
            System.out.println(q);
        }
    }

    private static void addQuote() {
        System.out.print("Enter quote text: ");
        String text = scanner.nextLine();
        System.out.print("Enter author: ");
        String author = scanner.nextLine();
        quoteManager.addQuote(text, author);
        System.out.println("Quote added.");
    }

    private static void listAllQuotes() {
        List<Quote> quotes = quoteManager.getAllQuotes();
        if (quotes.isEmpty()) {
            System.out.println("No quotes available.");
            return;
        }
        for (int i = 0; i < quotes.size(); i++) {
            System.out.println((i + 1) + ". " + quotes.get(i));
        }
    }

    private static void listFavorites() {
        List<Quote> favs = quoteManager.getFavorites();
        if (favs.isEmpty()) {
            System.out.println("No favorite quotes.");
            return;
        }
        for (int i = 0; i < favs.size(); i++) {
            System.out.println((i + 1) + ". " + favs.get(i));
        }
    }

    private static void markFavorite(boolean fav) {
        listAllQuotes();
        System.out.print("Enter quote number to " + (fav ? "mark" : "unmark") + " as favorite: ");
        try {
            int idx = Integer.parseInt(scanner.nextLine()) - 1;
            quoteManager.markFavorite(idx, fav);
            System.out.println("Updated favorite status.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }
}