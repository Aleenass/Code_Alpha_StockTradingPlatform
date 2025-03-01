import java.io.*;
import java.util.*;

class Stock {
    String symbol;
    String companyName;
    double price;
    int quantity;

    public Stock(String symbol, String companyName, double price, int quantity) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.price = price;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return symbol + " - " + companyName + " | Price: $" + price + " | Quantity: " + quantity;
    }
}

public class StockTrading {
    private static final String FILE_NAME = "portfolio.txt";
    private static HashMap<String, Stock> stockMarket = new HashMap<>();
    private static HashMap<String, String> stockList = new HashMap<>();
    private static List<Stock> portfolio = new ArrayList<>();

    public static void main(String[] args) {
        initializeStockMarket();
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Welcome to the Stock Trading Platform!");

        while (true) {
            System.out.println("\nChoose an option:");
            System.out.println("1. Buy Stocks");
            System.out.println("2. Sell Stocks");
            System.out.println("3. View Portfolio");
            System.out.println("4. Exit");

            int choice = scanner.nextInt();
            scanner.nextLine(); 

            switch (choice) {
                case 1:
                    buyStock(scanner);
                    break;
                case 2:
                    sellStock(scanner);
                    break;
                case 3:
                    viewPortfolio();
                    break;
                case 4:
                    System.out.println("Exiting the trading platform. Goodbye!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice! Please select a valid option.");
            }
        }
    }

    private static void initializeStockMarket() {
        stockMarket.put("AAPL", new Stock("AAPL", "Apple Inc.", 175.30, 0));
        stockMarket.put("TSLA", new Stock("TSLA", "Tesla Inc.", 205.50, 0));
        stockMarket.put("AMZN", new Stock("AMZN", "Amazon.com Inc.", 145.75, 0));
        stockMarket.put("MSFT", new Stock("MSFT", "Microsoft Corporation", 320.90, 0));
        stockMarket.put("GOOGL", new Stock("GOOGL", "Alphabet Inc. (Google)", 132.40, 0));
        stockMarket.put("NFLX", new Stock("NFLX", "Netflix Inc.", 412.15, 0));
        stockMarket.put("META", new Stock("META", "Meta (Facebook)", 278.55, 0));
        stockMarket.put("NVDA", new Stock("NVDA", "NVIDIA Corporation", 460.25, 0));

        for (String key : stockMarket.keySet()) {
            stockList.put(key, stockMarket.get(key).companyName);
        }

        loadPortfolio();
    }

    private static void buyStock(Scanner scanner) {
        while (true) {
            System.out.print("Enter stock symbol to buy (or type 'help' to see common stocks): ");
            String input = scanner.nextLine().toUpperCase();

            if (input.equals("HELP")) {
                System.out.println("\nHere are some common stock symbols:");
                for (String symbol : stockList.keySet()) {
                    System.out.println("- " + symbol + " → " + stockList.get(symbol));
                }
                System.out.println();
            } else if (stockMarket.containsKey(input)) {
                Stock stock = stockMarket.get(input);
                System.out.println("You selected: " + stock.companyName + " (" + input + ") - Price: $" + stock.price);
                System.out.print("Enter quantity to buy: ");
                int quantity = scanner.nextInt();
                scanner.nextLine(); 

                portfolio.add(new Stock(stock.symbol, stock.companyName, stock.price, quantity));
                savePortfolio();
                System.out.println("Successfully bought " + quantity + " shares of " + stock.companyName);
                break;
            } else {
                System.out.println("Invalid stock symbol. Try again or type 'help' for assistance.");
            }
        }
    }

    private static void sellStock(Scanner scanner) {
        if (portfolio.isEmpty()) {
            System.out.println("Your portfolio is empty! Buy stocks first.");
            return;
        }

        System.out.println("\nYour Portfolio:");
        viewPortfolio();

        System.out.print("Enter stock symbol to sell: ");
        String symbol = scanner.nextLine().toUpperCase();

        boolean found = false;
        Iterator<Stock> iterator = portfolio.iterator();
        while (iterator.hasNext()) {
            Stock stock = iterator.next();
            if (stock.symbol.equals(symbol)) {
                found = true;
                System.out.print("Enter quantity to sell: ");
                int quantity = scanner.nextInt();
                scanner.nextLine(); 

                if (quantity >= stock.quantity) {
                    iterator.remove();
                    System.out.println("Sold all shares of " + stock.companyName);
                } else {
                    stock.quantity -= quantity;
                    System.out.println("Sold " + quantity + " shares of " + stock.companyName);
                }
                savePortfolio();
                break;
            }
        }

        if (!found) {
            System.out.println("Stock not found in your portfolio.");
        }
    }

    private static void viewPortfolio() {
        if (portfolio.isEmpty()) {
            System.out.println("Your portfolio is empty.");
            return;
        }
        System.out.println("\nYour Portfolio:");
        for (Stock stock : portfolio) {
            System.out.println(stock);
        }
    }

    private static void savePortfolio() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Stock stock : portfolio) {
                writer.write(stock.symbol + "," + stock.companyName + "," + stock.price + "," + stock.quantity);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving portfolio: " + e.getMessage());
        }
    }

    private static void loadPortfolio() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 4) continue;

                portfolio.add(new Stock(parts[0], parts[1], Double.parseDouble(parts[2]), Integer.parseInt(parts[3])));
            }
        } catch (IOException e) {
            System.out.println("Error loading portfolio: " + e.getMessage());
        }
    }
}
