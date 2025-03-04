package com.library.ui;

import com.library.model.Book;
import com.library.model.User;
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

        borrowButton.addActionListener(e -> {
            int selectedRow = booksTable.getSelectedRow();
            if (selectedRow != -1) {
                String bookId = (String) tableModel.getValueAt(selectedRow, 0);
                Book book = getBookById(bookId);
                if (book != null) {
                    if (book.isAvailable()) {
                        showBorrowDialog(book);
                    } else {
                        handleReturn(book);
                    }
                    refreshTable();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a book");
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

    private void showBorrowDialog(Book book) {
        List<User> users = dataService.getAllUsers();
        if (users.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No users registered in the system",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        JComboBox<User> userComboBox = new JComboBox<>(users.toArray(new User[0]));
        Object[] message = {
            "Select user:", userComboBox
        };

        int option = JOptionPane.showConfirmDialog(this,
            message,
            "Borrow Book",
            JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            User selectedUser = (User) userComboBox.getSelectedItem();
            book.setAvailable(false);
            book.setBorrowerId(selectedUser.getId());
            selectedUser.borrowBook(book.getId());
            dataService.updateBook(book);
            dataService.updateUser(selectedUser);
            refreshTable();
            // Notify UsersPanel to refresh
            firePropertyChange("REFRESH_USERS", null, null);
            JOptionPane.showMessageDialog(this,
                "Book borrowed successfully by " + selectedUser.getName());
        }
    }

    private void handleReturn(Book book) {
        User user = null;
        for (User u : dataService.getAllUsers()) {
            if (u.getId().equals(book.getBorrowerId())) {
                user = u;
                break;
            }
        }

        if (user != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Return book borrowed by " + user.getName() + "?",
                "Return Book",
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                book.setAvailable(true);
                book.setBorrowerId(null);
                user.returnBook(book.getId());
                dataService.updateBook(book);
                dataService.updateUser(user);
                refreshTable();
                // Notify UsersPanel to refresh
                firePropertyChange("REFRESH_USERS", null, null);
                JOptionPane.showMessageDialog(this, "Book returned successfully");
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Error: Borrower information not found",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
} 