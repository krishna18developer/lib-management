package com.library.ui;

import com.library.model.Book;
import com.library.service.DataService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.UUID;

public class BooksPanel extends JPanel {
    private final DataService dataService;
    private JTable booksTable;
    private DefaultTableModel tableModel;
    
    public BooksPanel(DataService dataService) {
        this.dataService = dataService;
        setupUI();
        refreshTable();
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        
        // Create table
        String[] columns = {"ID", "Title", "Author", "ISBN", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        booksTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(booksTable);
        
        // Create buttons panel
        JPanel buttonsPanel = new JPanel();
        JButton addButton = new JButton("Add Book");
        JButton editButton = new JButton("Edit Book");
        JButton deleteButton = new JButton("Delete Book");
        JButton borrowButton = new JButton("Borrow/Return");
        
        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(borrowButton);
        
        // Add components
        add(scrollPane, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);
        
        // Add button listeners
        addButton.addActionListener(e -> showAddBookDialog());
        editButton.addActionListener(e -> {
            int selectedRow = booksTable.getSelectedRow();
            if (selectedRow != -1) {
                String bookId = (String) tableModel.getValueAt(selectedRow, 0);
                showEditBookDialog(getBookById(bookId));
            } else {
                JOptionPane.showMessageDialog(this, "Please select a book to edit");
            }
        });
        
        deleteButton.addActionListener(e -> {
            int selectedRow = booksTable.getSelectedRow();
            if (selectedRow != -1) {
                String bookId = (String) tableModel.getValueAt(selectedRow, 0);
                if (confirmDelete()) {
                    dataService.deleteBook(bookId);
                    refreshTable();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a book to delete");
            }
        });
    }
    
    private void showAddBookDialog() {
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField isbnField = new JTextField();
        
        Object[] message = {
            "Title:", titleField,
            "Author:", authorField,
            "ISBN:", isbnField
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Add New Book", 
            JOptionPane.OK_CANCEL_OPTION);
            
        if (option == JOptionPane.OK_OPTION) {
            String id = UUID.randomUUID().toString();
            Book book = new Book(id, titleField.getText(), authorField.getText(), isbnField.getText());
            dataService.addBook(book);
            refreshTable();
        }
    }
    
    private void showEditBookDialog(Book book) {
        if (book == null) return;
        
        JTextField titleField = new JTextField(book.getTitle());
        JTextField authorField = new JTextField(book.getAuthor());
        JTextField isbnField = new JTextField(book.getIsbn());
        
        Object[] message = {
            "Title:", titleField,
            "Author:", authorField,
            "ISBN:", isbnField
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Edit Book", 
            JOptionPane.OK_CANCEL_OPTION);
            
        if (option == JOptionPane.OK_OPTION) {
            book.setTitle(titleField.getText());
            book.setAuthor(authorField.getText());
            book.setIsbn(isbnField.getText());
            dataService.updateBook(book);
            refreshTable();
        }
    }
    
    private boolean confirmDelete() {
        return JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this book?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
    
    private Book getBookById(String id) {
        return dataService.getAllBooks().stream()
                .filter(book -> book.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Book> books = dataService.getAllBooks();
        for (Book book : books) {
            Object[] row = {
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.isAvailable() ? "Available" : "Borrowed"
            };
            tableModel.addRow(row);
        }
    }
} 