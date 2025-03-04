package com.library.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.library.model.Book;
import com.library.model.User;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DataService {
    private static final String DATA_DIR = "data";
    private static final String BOOKS_FILE = DATA_DIR + "/books.json";
    private static final String USERS_FILE = DATA_DIR + "/users.json";
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Book.class, new com.google.gson.JsonDeserializer<Book>() {
                @Override
                public Book deserialize(com.google.gson.JsonElement json, Type type, 
                        com.google.gson.JsonDeserializationContext context) throws com.google.gson.JsonParseException {
                    com.google.gson.JsonObject jsonObject = json.getAsJsonObject();
                    
                    String id = jsonObject.get("id").getAsString();
                    String title = jsonObject.get("title").getAsString();
                    String author = jsonObject.get("author").getAsString();
                    String isbn = jsonObject.get("isbn").getAsString();
                    
                    Book book = new Book(id, title, author, isbn);
                    
                    if (jsonObject.has("totalCopies")) {
                        book.setTotalCopies(jsonObject.get("totalCopies").getAsInt());
                    }
                    
                    if (jsonObject.has("borrowRecords")) {
                        Type recordListType = new TypeToken<List<Book.BorrowRecord>>(){}.getType();
                        List<Book.BorrowRecord> records = context.deserialize(jsonObject.get("borrowRecords"), recordListType);
                        book.getBorrowRecords().addAll(records);
                    }
                    
                    return book;
                }
            })
            .create();
    
    private List<Book> books;
    private List<User> users;
    
    public DataService() {
        // Create data directory if it doesn't exist
        new File(DATA_DIR).mkdirs();
        loadData();
    }
    
    public void loadData() {
        books = loadBooks();
        users = loadUsers();
        
        if (books == null) books = new ArrayList<>();
        if (users == null) users = new ArrayList<>();
    }
    
    private List<Book> loadBooks() {
        return loadFromFile(BOOKS_FILE, new TypeToken<List<Book>>(){}.getType());
    }
    
    private List<User> loadUsers() {
        return loadFromFile(USERS_FILE, new TypeToken<List<User>>(){}.getType());
    }
    
    private <T> List<T> loadFromFile(String filename, Type type) {
        try (Reader reader = new FileReader(filename)) {
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
    
    public void saveData() {
        saveToFile(BOOKS_FILE, books);
        saveToFile(USERS_FILE, users);
    }
    
    private void saveToFile(String filename, Object data) {
        try (Writer writer = new FileWriter(filename)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Book operations
    public void addBook(Book book) {
        books.add(book);
        saveData();
    }
    
    public void updateBook(Book book) {
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getId().equals(book.getId())) {
                books.set(i, book);
                break;
            }
        }
        saveData();
    }
    
    public void deleteBook(String bookId) {
        books.removeIf(book -> book.getId().equals(bookId));
        saveData();
    }
    
    public List<Book> getAllBooks() {
        return new ArrayList<>(books);
    }
    
    // User operations
    public void addUser(User user) {
        users.add(user);
        saveData();
    }
    
    public void updateUser(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(user.getId())) {
                users.set(i, user);
                break;
            }
        }
        saveData();
    }
    
    public void deleteUser(String userId) {
        users.removeIf(user -> user.getId().equals(userId));
        saveData();
    }
    
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }
    
    public User getUserByEmail(String email) {
        return users.stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }
    
    public User getUserById(String id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
} 