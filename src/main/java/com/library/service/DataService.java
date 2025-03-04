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
    private static final String BOOKS_FILE = "books.json";
    private static final String USERS_FILE = "users.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    private List<Book> books;
    private List<User> users;
    
    public DataService() {
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
} 