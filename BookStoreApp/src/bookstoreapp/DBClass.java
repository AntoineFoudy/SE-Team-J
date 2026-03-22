/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bookstoreapp;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 *
 * @author afoud
 */
public class DBClass {
    
    private Connection con;
    private boolean connected;
    
    private String url = "jdbc:mysql://localhost:3306/TeamJBookStore";
    private String user = "root";
    private String password = "password";
    
    
    
    public DBClass() {
        try{
            con = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to DB");
        }
        catch(SQLException ex) {
            System.out.println("SQLException: " + ex);            
        }
    }
    
    // Retive the user by email and password and return the PK of userID to give the system the ability to change all the required
    // function the user requires
    public int loginUser(String email, String password) {
        int userID = 0;
        
        String findUserByEmailAndPassword = """
                                          SELECT userID FROM users
                                          WHERE email = ?
                                          AND password = ?
                                          """;
        
        try(PreparedStatement pstatement = con.prepareStatement(findUserByEmailAndPassword)) {
           pstatement.setString(1, email);
           pstatement.setString(2, password);
           
           try(ResultSet rs = pstatement.executeQuery()) {
               if (rs.next()) {
                   userID = rs.getInt("userID");
               }
           }
           
        }
        catch(SQLException ex) {
            System.out.println("SQLException: " + ex);            
        }
        return userID;        
    }
    
    // Create new User
    public int createUser(String name, String email, String password) {
        String emailExist = "This Email already in use";
        String accountCreated = "Accound has been created";
        
        String checkUserEmail = """
                                SELECT userID FROM users
                                WHERE email = ?
                                """;
        
        String createUserByEmailAndPassword = """
                                              INSERT INTO users (name, email, password)
                                              VALUES (
                                              ?,
                                              ?,
                                              ?
                                              )
                                              """;
        
        // Check if Email already Exists in the system
        try(PreparedStatement pstatement = con.prepareStatement(checkUserEmail)) {
            pstatement.setString(1, email);
            
            try(ResultSet rs = pstatement.executeQuery()) {
               if(rs.next()) {
                    return 0;
               }
            }
            
            // Create new user
            try(PreparedStatement pstatementCreate = con.prepareStatement(createUserByEmailAndPassword, Statement.RETURN_GENERATED_KEYS)) {
                pstatementCreate.setString(1, name);
                pstatementCreate.setString(2, email);
                pstatementCreate.setString(3, password);
                
                // Return the new userID
                int affectedRows = pstatementCreate.executeUpdate();
                
                if(affectedRows > 0) {
                    try(ResultSet keys = pstatementCreate.getGeneratedKeys()) {
                        if(keys.next()) {
                            return keys.getInt(1);
                        }
                    }
                }
            }           
        }
        catch(SQLException ex) {
            System.out.println("SQLException: " + ex);            
        }
        // Error
        return -1;
    }
    
    // Add to basket
    public boolean addToBasket(int userId, int bookId) {
        boolean success = false;
        
        String insertBook = """
                            INSERT INTO basket (userID, bookID)
                            VALUES (
                            ?,
                            ?
                            )
                            """;
        try(PreparedStatement pstatementCreate = con.prepareStatement(insertBook)){
            pstatementCreate.setInt(1, userId);
            pstatementCreate.setInt(2, bookId);
            
            pstatementCreate.executeUpdate();
            
            success = true;
        }
        catch(SQLException ex) {
            System.out.println("SQLException: " + ex);            
        }
        return success;
    }
    
    // Empty every book in the Users basket
    public boolean emptyBasketForUser(int userId) {
        boolean delete = false;
        
        String deleteUserBasket = """
                                  DELETE FROM basket 
                                  WHERE userID = ?
                                  """;
        
        try(PreparedStatement pstatementDelete = con.prepareStatement(deleteUserBasket)) {
            pstatementDelete.setInt(1, userId);
            
            pstatementDelete.executeUpdate();
            
            delete = true;
        }
        catch(SQLException ex) {
            System.out.println("SQLException:" + ex);
        }
        
        return delete;
    }
    
    public ArrayList<ArrayList<String>> getUserBasket(int userId) {
        ArrayList<ArrayList<String>> bookData = new ArrayList<>();
        
        String getUserBasket = """
                               SELECT bookID FROM basket
                               WHERE userID = ?
                               """;
        
        try(PreparedStatement pstatement = con.prepareStatement(getUserBasket)) {
            pstatement.setInt(1, userId);
            
            try(ResultSet rs = pstatement.executeQuery()) {
                while(rs.next()) {
                    bookData.add(getBookData(rs.getInt("bookID")));
                }
            }
            
        }
        catch(SQLException ex) {
            System.out.println("SQLException" + ex);
        }
        
        return bookData;
    }
    
    // Get all the data for a spesific book, returned in a ArrayList
    private ArrayList<String> getBookData(int bookId) {
        ArrayList<String> getBookData = new ArrayList<>();
        
        String getBookDataByID = """
                                 SELECT * FROM book
                                 WHERE bookID = ?
                                 """;
        
        try(PreparedStatement pstatement = con.prepareStatement(getBookDataByID)) {
            pstatement.setInt(1, bookId);
            
            // Adds all the data
            try(ResultSet book = pstatement.executeQuery()) {
                if(book.next()) {
                    getBookData.add(book.getString("bookID"));
                    getBookData.add(book.getString("name"));
                    getBookData.add(book.getString("isbn"));
                    getBookData.add(book.getString("author"));
                    getBookData.add(book.getString("category"));
                    getBookData.add(book.getString("description"));
                    getBookData.add(book.getString("type"));
                    getBookData.add(book.getString("price"));
                    getBookData.add(book.getString("stock"));
                }
            }
            
        }
        catch(SQLException ex) {
            System.out.println("SQLException" + ex);
        }
        
        return getBookData;
    }
    
    // Records the Transactions, this spesific method is only used when buying books
    public boolean recordTransaction(int userId, ArrayList<Integer> bookIds, double price, String type) {
        boolean success = false;
        
        String recordTransaction = """
                                   INSERT INTO transactions (userID, bookIDs, price, type)
                                   VALUES (
                                   ?,
                                   ?,
                                   ?,
                                   ?
                                   )
                                   """;
        
        try(PreparedStatement pstatment = con.prepareStatement(recordTransaction)) {
            
            String bookIdsToString = bookIds.stream().map(String::valueOf).collect(Collectors.joining(","));
            
            pstatment.setInt(1, userId);
            pstatment.setString(2, bookIdsToString);
            pstatment.setDouble(3, price);
            pstatment.setString(4, type);
            
            pstatment.executeUpdate();
            
            success = true;
        }
        catch(SQLException ex) {
            System.out.println("SQLException" + ex);
        }
        
        return success;
    }
    
    // Records the Transactions, this spesific overloaded method is only used when renting books
    public boolean recordTransaction(int userId, ArrayList<Integer> bookIds, String type, LocalDate dueDate) {
        boolean success = false;
        Date dueDateSQL = Date.valueOf(dueDate);
        
        String recordTransaction = """
                                   INSERT INTO transactions (userID, bookIDs, type, dueDate)
                                   VALUES (
                                   ?,
                                   ?,
                                   ?,
                                   ?
                                   )
                                   """;
        
        try(PreparedStatement pstatment = con.prepareStatement(recordTransaction)) {
            
            String bookIdsToString = bookIds.stream().map(String::valueOf).collect(Collectors.joining(","));
            
            pstatment.setInt(1, userId);
            pstatment.setString(2, bookIdsToString);
            pstatment.setString(3, type);
            pstatment.setDate(4, dueDateSQL);
            
            pstatment.executeUpdate();
            
            success = true;
        }
        catch(SQLException ex) {
            System.out.println("SQLException" + ex);
        }
        
        return success;
    }
    
    // Check if the stock of a spesific book is enough for the users wants
    public boolean checkStock(int bookId, int amountOfBookNeeded) {
        boolean enoughStock = false;
        
        String checkStock = """
                            SELECT stock FROM book
                            WHERE bookID = ?
                            """;
        
        try(PreparedStatement pstatment = con.prepareStatement(checkStock)) {
            
            pstatment.setInt(1, bookId);
            
 
            try(ResultSet rs = pstatment.executeQuery()) {
               if (rs.next()) {
                   if(rs.getInt("stock") >= amountOfBookNeeded) {
                       enoughStock = true;
                   }
               }
            }
        }
        catch(SQLException ex) {
            System.out.println("SQLException" + ex);
        }
        
        return enoughStock;
    }
    
    // Update the stock of a specific book against the amount that was bought/rented
    public boolean updateStock(int bookId, int usedStock) {
        boolean successful = false;
        
        String updateStock = """
                             UPDATE book
                             SET stock = stock - ?
                             WHERE bookID = ?
                             """;
        
        try(PreparedStatement pstatment = con.prepareStatement(updateStock)) {
            pstatment.setInt(1, usedStock);
            pstatment.setInt(2, bookId);
            
            pstatment.execute();
            
            successful = true;
        }
        catch(SQLException ex) {
            System.out.println("SQLException" + ex);
        }
        
        return successful;
    }
        

}

