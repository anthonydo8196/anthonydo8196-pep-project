package DAO;

import Model.Account;
import Util.ConnectionUtil;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class AccountDAO {

    public List<Account> getAllAccounts() {
        Connection connection = ConnectionUtil.getConnection();
        List<Account> accs = new ArrayList<>();
        try {
            String sql = "SELECT * FROM account";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Account account = new Account(rs.getInt("account_id"), rs.getString("username"), rs.getString("password"));
                accs.add(account);
            }
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        return accs;
    }

    public Account insertAccount(Account account) {
        // Connection object configured to connect to our specific database
        Connection connection = ConnectionUtil.getConnection();
        try {
            // make sure username isn't blank, password is at least 4 characters long
            if(account.getUsername().isEmpty() || account.getPassword().length() < 4) {
                return null;
            } 

            // check that account with this username doesn't already exist
            String sql = "SELECT * FROM account WHERE username = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, account.getUsername());
            ResultSet rs = ps.executeQuery();

            // result set.next returns true if it can move to an available row otherwise it returns false
            if(rs.next()) {
                return null;
            }
                

            // otherwise continue normally
            // prep sql statement and return any generated keys (account_id in this case)
            sql = "INSERT INTO account (username, password) VALUES (?, ?)" ;
            ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // set the values for the placeholders
            ps.setString(1, account.getUsername());
            ps.setString(2, account.getPassword());

            // execute the insert statement
            ps.executeUpdate();
            // get the generated keys
            rs = ps.getGeneratedKeys();
            // moves cursor to first row of result set and returns true if there's a row
            if(rs.next()){
                int generated_account_id = rs.getInt(1);
                // return account object with it's proper fields
                return new Account(generated_account_id, account.getUsername(), account.getPassword());
            }
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }    

    public Account loginAccount(Account account) {
        // Connection object configured to connect to our specific database
        Connection connection = ConnectionUtil.getConnection();
        try {
            // check that account with this username and password exists as well as retrieving the account_id
            String sql = "SELECT account_id FROM account WHERE username = ? AND password = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, account.getUsername());
            ps.setString(2, account.getPassword());
            ResultSet rs = ps.executeQuery();

            // result set.next returns true if it can move to an available row otherwise it returns false
            // if it is true, then there is an account that matches
            if(rs.next()) {
                // retrieve the account id from the query
                int account_id = rs.getInt(1);
                return new Account(account_id, account.getUsername(), account.getPassword());
            }
            // otherwise there is no account
            return null;
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return null;        
    }
}
