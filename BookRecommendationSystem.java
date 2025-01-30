import java.sql.*;
import java.util.Scanner;

public class BookRecommendationSystem {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/library_system";
    private static final String DB_USER = "root"; // MySQL username
    private static final String DB_PASSWORD = "Arsh7040"; // MySQL password

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Load MySQL JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found. Please add the JDBC library to your project.");
            e.printStackTrace();
            return;
        }

        while (true) {
            System.out.println("\n1. Register User");
            System.out.println("2. Borrow Book");
            System.out.println("3. Return Book");
            System.out.println("4. View Recommendations");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1 -> registerUser(scanner);
                case 2 -> borrowBook(scanner);
                case 3 -> returnBook(scanner);
                case 4 -> viewRecommendations(scanner);
                case 5 -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    // Register a new user
    private static void registerUser(Scanner scanner) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            scanner.nextLine(); // Clear the buffer
            System.out.print("Enter your name: ");
            String name = scanner.nextLine();
            System.out.print("Enter your email: ");
            String email = scanner.nextLine();
            System.out.print("Enter your interests (comma-separated genres): ");
            String interests = scanner.nextLine();

            String query = "INSERT INTO users (name, email, interests) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, name);
                stmt.setString(2, email);
                stmt.setString(3, interests);
                stmt.executeUpdate();
                System.out.println("User registered successfully!");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Borrow a book
    private static void borrowBook(Scanner scanner) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            System.out.print("Enter your email: ");
            String email = scanner.next();

            // Get user details
            String userQuery = "SELECT id FROM users WHERE email = ?";
            try (PreparedStatement userStmt = conn.prepareStatement(userQuery)) {
                userStmt.setString(1, email);
                ResultSet userRs = userStmt.executeQuery();
                if (!userRs.next()) {
                    System.out.println("User not found! Please register first.");
                    return;
                }
                int userId = userRs.getInt("id");

                // Display available books
                String bookQuery = "SELECT id, title, author FROM books WHERE availability = true";
                try (Statement bookStmt = conn.createStatement();
                        ResultSet bookRs = bookStmt.executeQuery(bookQuery)) {
                    System.out.println("Available books:");
                    while (bookRs.next()) {
                        System.out.printf("%d. %s by %s\n", bookRs.getInt("id"), bookRs.getString("title"),
                                bookRs.getString("author"));
                    }
                }

                System.out.print("Enter book ID to borrow: ");
                int bookId = scanner.nextInt();

                conn.setAutoCommit(false);
                String updateBookQuery = "UPDATE books SET availability = false WHERE id = ?";
                String borrowQuery = "INSERT INTO borrow_history (user_id, book_id, borrow_date) VALUES (?, ?, CURRENT_DATE)";
                try (PreparedStatement updateBookStmt = conn.prepareStatement(updateBookQuery);
                        PreparedStatement borrowStmt = conn.prepareStatement(borrowQuery)) {
                    updateBookStmt.setInt(1, bookId);
                    updateBookStmt.executeUpdate();

                    borrowStmt.setInt(1, userId);
                    borrowStmt.setInt(2, bookId);
                    borrowStmt.executeUpdate();

                    conn.commit();
                    System.out.println("Book borrowed successfully!");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void returnBook(Scanner scanner) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            System.out.print("Enter your email: ");
            String email = scanner.next();

            String userQuery = "SELECT id FROM users WHERE email = ?";
            try (PreparedStatement userStmt = conn.prepareStatement(userQuery)) {
                userStmt.setString(1, email);
                ResultSet userRs = userStmt.executeQuery();
                if (!userRs.next()) {
                    System.out.println("User not found! Please register first.");
                    return;
                }
                int userId = userRs.getInt("id");

                String borrowQuery = "SELECT bh.id, b.title, b.author, bh.borrow_date FROM borrow_history bh " +
                        "JOIN books b ON bh.book_id = b.id WHERE bh.user_id = ? AND bh.return_date IS NULL";
                try (PreparedStatement borrowStmt = conn.prepareStatement(borrowQuery)) {
                    borrowStmt.setInt(1, userId);
                    ResultSet borrowRs = borrowStmt.executeQuery();

                    System.out.println("Your borrowed books:");
                    while (borrowRs.next()) {
                        System.out.printf("%d. %s by %s (Borrowed on: %s)\n", borrowRs.getInt("id"),
                                borrowRs.getString("title"),
                                borrowRs.getString("author"), borrowRs.getDate("borrow_date"));
                    }

                    System.out.print("Enter the ID of the book you want to return: ");
                    int borrowHistoryId = scanner.nextInt();

                    // Update return_date for the book
                    String returnQuery = "UPDATE borrow_history SET return_date = CURRENT_DATE WHERE id = ?";
                    try (PreparedStatement returnStmt = conn.prepareStatement(returnQuery)) {
                        returnStmt.setInt(1, borrowHistoryId);
                        returnStmt.executeUpdate();
                        System.out.println("Book returned successfully!");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewRecommendations(Scanner scanner) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            System.out.print("Enter your email: ");
            String email = scanner.next();

            String userQuery = "SELECT id, interests FROM users WHERE email = ?";
            try (PreparedStatement userStmt = conn.prepareStatement(userQuery)) {
                userStmt.setString(1, email);
                ResultSet userRs = userStmt.executeQuery();
                if (!userRs.next()) {
                    System.out.println("User not found! Please register first.");
                    return;
                }
                int userId = userRs.getInt("id");
                String userInterests = userRs.getString("interests");

                String recommendQuery = """
                            SELECT DISTINCT b.id, b.title, b.author, b.genre
                            FROM books b
                            JOIN borrow_history bh ON LOWER(b.genre) IN (
                                SELECT LOWER(genre) FROM books WHERE id IN (
                                    SELECT book_id FROM borrow_history WHERE user_id = ?
                                )
                            )
                            WHERE b.availability = true AND b.id NOT IN (
                                SELECT book_id FROM borrow_history WHERE user_id = ?
                            ) AND LOWER(b.genre) IN (?)
                        """;
                try (PreparedStatement recommendStmt = conn.prepareStatement(recommendQuery)) {
                    recommendStmt.setInt(1, userId);
                    recommendStmt.setInt(2, userId);
                    recommendStmt.setString(3, userInterests);
                    ResultSet recRs = recommendStmt.executeQuery();

                    System.out.println("Recommended books for you:");
                    while (recRs.next()) {
                        System.out.printf("%s by %s (Genre: %s)\n", recRs.getString("title"), recRs.getString("author"),
                                recRs.getString("genre"));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
