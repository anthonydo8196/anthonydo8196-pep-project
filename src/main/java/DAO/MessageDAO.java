package DAO;

import Model.Message;
import Util.ConnectionUtil;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
public class MessageDAO {

    public Message getMessage(int messageId) {
        Connection connection = ConnectionUtil.getConnection();
        try {
            String sql = "SELECT * FROM message WHERE message_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, messageId);
            ResultSet rs = ps.executeQuery();

            // need to have this check so that if there is no result, then this gets skipped so there would be an empty body in the json
            while(rs.next()) {
                Message msg = new Message(rs.getInt("message_id"), rs.getInt("posted_by"), rs.getString("message_text"), rs.getLong("time_posted_epoch"));
                return msg;
            }

        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public List<Message> getAllMessages() {
        Connection connection = ConnectionUtil.getConnection();
        List<Message> msgs = new ArrayList<>();
        try {
            String sql = "SELECT * FROM message";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Message message = new Message(rs.getInt("message_id"), rs.getInt("posted_by"), rs.getString("message_text"), rs.getLong("time_posted_epoch"));
                msgs.add(message);
            }
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        return msgs;
    }

    public Message insertMessage(Message message) {
        // Connection object configured to connect to our specific database
        Connection connection = ConnectionUtil.getConnection();
        try {
            // make sure message_text is not blank, and is not over 255 characters
            if(message.getMessage_text().isEmpty() || message.getMessage_text().length() > 255) {
                return null;
            } 

            // check that posted_by refers to a real, existing user
            String sql = "SELECT * FROM account WHERE account_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, message.getPosted_by());
            ResultSet rs = ps.executeQuery();

            // result set.next returns true if it can move to an available row otherwise it returns false
            // if it's false, then there is no account where there exists an id of posted_by
            if(!rs.next()) {
                return null;
            }
                
            // otherwise continue normally
            // prep sql statement and return any generated keys (account_id in this case)
            sql = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";
            ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // set the values for the placeholders
            ps.setInt(1, message.getPosted_by());
            ps.setString(2, message.getMessage_text());
            ps.setLong(3, message.getTime_posted_epoch());

            // execute the insert statement
            ps.executeUpdate();
            // get the generated keys
            rs = ps.getGeneratedKeys();
            // moves cursor to first row of result set and returns true if there's a row
            if(rs.next()){
                int generated_account_id = rs.getInt(1);
                // return account object with it's proper fields
                return new Message(generated_account_id, message.getPosted_by(), message.getMessage_text(), message.getTime_posted_epoch());
            }
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    } 

    public Message deleteMessage(int messageId) {
        Connection connection = ConnectionUtil.getConnection();
        try { 
            // check for a message that has corresponding message id
            String sql = "SELECT * FROM message WHERE message_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, messageId);
            ResultSet rs = ps.executeQuery();

            // if account doesn't exists then return null
            if(!rs.next()) {
                return null;
            }

            // save the message data before deleting
            Message ret = new Message(messageId, rs.getInt("posted_by"), rs.getString("message_text"), rs.getLong("time_posted_epoch"));

            // delete message
            sql = "DELETE FROM message WHERE message_id = ?";
            // create a new prepared statement object for different SQL query
            ps = connection.prepareStatement(sql);
            ps.setInt(1, messageId);
            ps.executeUpdate();

            // return message data
            return ret;
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public Message patchMessage(int messageId, Message message) {
        Connection connection = ConnectionUtil.getConnection();
            try {
                // check that posted_by refers to an existing message
                String sql = "SELECT * FROM message WHERE message_id = ?";
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setInt(1, messageId);
                ResultSet rs = ps.executeQuery();

                // result set.next returns true if it can move to an available row otherwise it returns false
                if(!rs.next()) {
                    return null;
                }
                
                // make sure message_text is not blank, and is not over 255 characters
                if(message.getMessage_text().isEmpty() || message.getMessage_text().length() > 255) {
                    return null;
                } 

                // otherwise continue normally
                sql = "UPDATE message SET message_text = ? WHERE message_id = ?";
                ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

                // set the values for the placeholders
                ps.setString(1, message.getMessage_text());
                ps.setInt(2, messageId);

                // execute the update statement
                ps.executeUpdate();

                // there is no result set when executing an update so return message immediately
                return new Message(messageId, rs.getInt("posted_by"), message.getMessage_text(), rs.getLong("time_posted_epoch"));

        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public List<Message> getAllMessagesUser(int accountId) {
        Connection connection = ConnectionUtil.getConnection();
        List<Message> msgs = new ArrayList<>();
        try {
            String sql = "SELECT * FROM message WHERE posted_by = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, accountId);

            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                Message message = new Message(rs.getInt("message_id"), rs.getInt("posted_by"), rs.getString("message_text"), rs.getLong("time_posted_epoch"));
                msgs.add(message);
            }
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        return msgs;
    }
}
