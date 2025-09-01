-- Flyway migration V2: Insert sample data
-- This script inserts sample books, users, and related data

-- Insert sample books
INSERT INTO books (title, author, description, cover_image_url, published_year, average_rating, total_reviews, created_at, updated_at) VALUES 
('To Kill a Mockingbird', 'Harper Lee', 'A gripping, heart-wrenching, and wholly remarkable tale of coming-of-age in a South poisoned by virulent prejudice.', 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=400', 1960, 4.3, 150, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('The Great Gatsby', 'F. Scott Fitzgerald', 'A classic American novel set in the summer of 1922 in the fictional town of West Egg on Long Island.', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400', 1925, 4.1, 200, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Educated', 'Tara Westover', 'A memoir about a woman who grew up in a survivalist family and eventually earned a PhD from Cambridge.', 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=400', 2018, 4.5, 300, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('The Handmaid''s Tale', 'Margaret Atwood', 'A dystopian novel set in a totalitarian society where women are subjugated.', 'https://images.unsplash.com/photo-1512820790803-83ca734da794?w=400', 1985, 4.2, 180, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Sapiens', 'Yuval Noah Harari', 'A brief history of humankind from the Stone Age to the modern age.', 'https://images.unsplash.com/photo-1589998059171-988d887df646?w=400', 2014, 4.4, 400, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('1984', 'George Orwell', 'A dystopian social science fiction novel about totalitarian control and surveillance.', 'https://images.unsplash.com/photo-1495640388908-05fa85288e61?w=400', 1949, 4.6, 500, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Pride and Prejudice', 'Jane Austen', 'A romantic novel of manners written by Jane Austen in 1813.', 'https://images.unsplash.com/photo-1518373714866-3f1478910cc0?w=400', 1813, 4.4, 350, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('The Catcher in the Rye', 'J.D. Salinger', 'A novel about teenage rebellion and angst in 1950s America.', 'https://images.unsplash.com/photo-1543002588-bfa74002ed7e?w=400', 1951, 3.8, 280, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Dune', 'Frank Herbert', 'A science fiction novel set in the distant future amidst a feudal interstellar society.', 'https://images.unsplash.com/photo-1446776653964-20c1d3a81b06?w=400', 1965, 4.5, 420, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('The Lord of the Rings', 'J.R.R. Tolkien', 'An epic high-fantasy novel about the quest to destroy the One Ring.', 'https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=400', 1954, 4.7, 600, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Harry Potter and the Philosopher''s Stone', 'J.K. Rowling', 'A young wizard discovers his magical heritage on his 11th birthday.', 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=400', 1997, 4.6, 800, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('The Hobbit', 'J.R.R. Tolkien', 'A reluctant hobbit, Bilbo Baggins, sets out to the Lonely Mountain with a spirited group of dwarves.', 'https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=400', 1937, 4.5, 650, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Brave New World', 'Aldous Huxley', 'A dystopian novel about a technologically advanced future society.', 'https://images.unsplash.com/photo-1495640388908-05fa85288e61?w=400', 1932, 4.0, 320, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('The Chronicles of Narnia', 'C.S. Lewis', 'A series of fantasy novels set in the magical land of Narnia.', 'https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=400', 1950, 4.3, 450, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Jane Eyre', 'Charlotte Brontë', 'The story of an orphaned girl who becomes a governess and falls in love with her employer.', 'https://images.unsplash.com/photo-1518373714866-3f1478910cc0?w=400', 1847, 4.2, 380, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Wuthering Heights', 'Emily Brontë', 'A tale of passion and revenge set on the Yorkshire moors.', 'https://images.unsplash.com/photo-1518373714866-3f1478910cc0?w=400', 1847, 3.9, 290, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('The Alchemist', 'Paulo Coelho', 'A philosophical book about a young Andalusian shepherd on his journey to Egypt.', 'https://images.unsplash.com/photo-1589998059171-988d887df646?w=400', 1988, 4.1, 520, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('One Hundred Years of Solitude', 'Gabriel García Márquez', 'A multi-generational story of the Buendía family in the fictional town of Macondo.', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400', 1967, 4.4, 410, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('The Kite Runner', 'Khaled Hosseini', 'A story of friendship, guilt, and redemption set against the backdrop of Afghanistan.', 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=400', 2003, 4.3, 480, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Life of Pi', 'Yann Martel', 'The story of a young man who survives a shipwreck and is hurtled into an epic journey of adventure and discovery.', 'https://images.unsplash.com/photo-1589998059171-988d887df646?w=400', 2001, 4.0, 360, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('The Book Thief', 'Markus Zusak', 'A story about a young girl living with her adoptive German family during the Nazi era.', 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=400', 2005, 4.5, 540, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Gone Girl', 'Gillian Flynn', 'A psychological thriller about a marriage gone terribly wrong.', 'https://images.unsplash.com/photo-1512820790803-83ca734da794?w=400', 2012, 4.0, 620, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('The Girl with the Dragon Tattoo', 'Stieg Larsson', 'A mystery thriller involving a journalist and a computer hacker.', 'https://images.unsplash.com/photo-1512820790803-83ca734da794?w=400', 2005, 4.2, 580, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('The Da Vinci Code', 'Dan Brown', 'A mystery thriller that follows symbologist Robert Langdon.', 'https://images.unsplash.com/photo-1512820790803-83ca734da794?w=400', 2003, 3.8, 750, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('The Hunger Games', 'Suzanne Collins', 'A dystopian novel about a televised fight to the death.', 'https://images.unsplash.com/photo-1495640388908-05fa85288e61?w=400', 2008, 4.3, 690, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Fahrenheit 451', 'Ray Bradbury', 'A dystopian novel about a future society where books are banned.', 'https://images.unsplash.com/photo-1495640388908-05fa85288e61?w=400', 1953, 4.1, 420, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('The Martian', 'Andy Weir', 'A science fiction novel about an astronaut stranded on Mars.', 'https://images.unsplash.com/photo-1446776653964-20c1d3a81b06?w=400', 2011, 4.4, 510, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Ready Player One', 'Ernest Cline', 'A science fiction novel set in a virtual reality world.', 'https://images.unsplash.com/photo-1446776653964-20c1d3a81b06?w=400', 2011, 4.2, 470, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('The Fault in Our Stars', 'John Green', 'A young adult novel about two teenagers who meet in a cancer support group.', 'https://images.unsplash.com/photo-1518373714866-3f1478910cc0?w=400', 2012, 4.3, 640, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Where the Crawdads Sing', 'Delia Owens', 'A mystery novel about a young woman who raised herself in the marshes of North Carolina.', 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=400', 2018, 4.4, 580, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert book genres (separate table for many-to-many relationship)
INSERT INTO book_genres (book_id, genre) VALUES 
(1, 'FICTION'),
(2, 'FICTION'),
(3, 'NON_FICTION'),
(3, 'BIOGRAPHY'),
(4, 'SCIENCE_FICTION'),
(5, 'NON_FICTION'),
(5, 'HISTORY'),
(6, 'SCIENCE_FICTION'),
(7, 'FICTION'),
(7, 'ROMANCE'),
(8, 'FICTION'),
(9, 'SCIENCE_FICTION'),
(9, 'FANTASY'),
(10, 'FANTASY'),
(11, 'FANTASY'),
(12, 'FANTASY'),
(13, 'SCIENCE_FICTION'),
(14, 'FANTASY'),
(15, 'FICTION'),
(15, 'ROMANCE'),
(16, 'FICTION'),
(16, 'ROMANCE'),
(17, 'FICTION'),
(18, 'FICTION'),
(19, 'FICTION'),
(20, 'FICTION'),
(21, 'FICTION'),
(22, 'MYSTERY'),
(23, 'MYSTERY'),
(24, 'MYSTERY'),
(25, 'SCIENCE_FICTION'),
(26, 'SCIENCE_FICTION'),
(27, 'SCIENCE_FICTION'),
(28, 'SCIENCE_FICTION'),
(29, 'FICTION'),
(30, 'FICTION');

-- Insert sample users (let database auto-generate IDs)
INSERT INTO users (email, password, first_name, last_name, bio, role, created_at, updated_at) VALUES 
('admin@bookreview.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFtuZYDxidxogsKbV7vE8/.', 'Admin', 'User', 'Platform administrator', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('john.doe@email.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFtuZYDxidxogsKbV7vE8/.', 'John', 'Doe', 'Avid reader and book enthusiast', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('jane.smith@email.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFtuZYDxidxogsKbV7vE8/.', 'Jane', 'Smith', 'Love fiction and mystery novels', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample user preferred genres (use auto-generated user IDs)
INSERT INTO user_preferred_genres (user_id, preferred_genres) VALUES 
(1, 'FICTION'),
(1, 'HISTORY'),
(2, 'FICTION'),
(2, 'MYSTERY'),
(2, 'SCIENCE_FICTION');

-- Insert sample reviews (use auto-generated user IDs)
INSERT INTO reviews (user_id, book_id, rating, review_text, created_at, updated_at) VALUES 
(1, 1, 5, 'This book is absolutely phenomenal. Harper Lee''s storytelling is masterful and the themes are as relevant today as they were when it was written.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 1, 4, 'Really enjoyed this book. The character development is excellent and the story is compelling.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 2, 4, 'Fitzgerald''s writing is beautiful, though the story can be a bit slow at times.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 3, 5, 'Tara Westover''s story is both heartbreaking and inspiring. Couldn''t put it down.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 5, 4, 'Harari presents complex topics in an accessible way. Very thought-provoking.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample user favorite books (use auto-generated user IDs)
INSERT INTO user_favorite_books (user_id, book_id) VALUES 
(1, 1),
(1, 5),
(2, 1),
(2, 3),
(2, 9);
