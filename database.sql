CREATE DATABASE library_system;

USE library_system;

-- Users Table
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    interests VARCHAR(255)
);

-- Books Table
CREATE TABLE books (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    genre VARCHAR(100) NOT NULL,
    availability BOOLEAN DEFAULT TRUE
);

-- Borrow History Table
CREATE TABLE borrow_history (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    book_id INT NOT NULL,
    borrow_date DATE NOT NULL,  -- Store the borrow date as DATE
    return_date DATE,  -- Optional: Store return date
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (book_id) REFERENCES books(id)
);


-- Adding more books to the `books` table
INSERT INTO books (title, author, genre, availability) VALUES 
('The Great Gatsby', 'F. Scott Fitzgerald', 'Classic', true),
('To Kill a Mockingbird', 'Harper Lee', 'Classic', true),
('1984', 'George Orwell', 'Dystopian', true),
('Pride and Prejudice', 'Jane Austen', 'Romance', true),
('The Catcher in the Rye', 'J.D. Salinger', 'Classic', true),
('The Hobbit', 'J.R.R. Tolkien', 'Fantasy', true),
('Harry Potter and the Philosopher\'s Stone', 'J.K. Rowling', 'Fantasy', true),
('The Lord of the Rings', 'J.R.R. Tolkien', 'Fantasy', true),
('The Da Vinci Code', 'Dan Brown', 'Thriller', true),
('Angels and Demons', 'Dan Brown', 'Thriller', true),
('The Alchemist', 'Paulo Coelho', 'Philosophy', true),
('Sapiens: A Brief History of Humankind', 'Yuval Noah Harari', 'Non-Fiction', true),
('Thinking, Fast and Slow', 'Daniel Kahneman', 'Non-Fiction', true),
('The Silent Patient', 'Alex Michaelides', 'Mystery', true),
('Gone Girl', 'Gillian Flynn', 'Mystery', true),
('The Girl on the Train', 'Paula Hawkins', 'Mystery', true),
('A Song of Ice and Fire: A Game of Thrones', 'George R.R. Martin', 'Fantasy', true),
('Atomic Habits', 'James Clear', 'Self-Help', true),
('The Subtle Art of Not Giving a F*ck', 'Mark Manson', 'Self-Help', true),
('Dune', 'Frank Herbert', 'Science Fiction', true),
('Brave New World', 'Aldous Huxley', 'Dystopian', true),
('The Road', 'Cormac McCarthy', 'Dystopian', true),
('The Fault in Our Stars', 'John Green', 'Romance', true),
('Me Before You', 'Jojo Moyes', 'Romance', true),
('The Art of War', 'Sun Tzu', 'Philosophy', true),
('The Shining', 'Stephen King', 'Horror', true),
('Dracula', 'Bram Stoker', 'Horror', true),
('Frankenstein', 'Mary Shelley', 'Horror', true),
('Sherlock Holmes: The Complete Novels and Stories', 'Arthur Conan Doyle', 'Mystery', true),
('Meditations', 'Marcus Aurelius', 'Philosophy', true);


