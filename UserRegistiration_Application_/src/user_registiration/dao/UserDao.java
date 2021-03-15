package user_registiration.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import user_regisitration.bean.User;

public class UserDao {
	private String jdbcURL = "jdbc:mysql://localhost:3306/userdb?useSSL=false";
	private String jdbcUsername = "root";
	private String jdbcPassword = "admin";
	private String jdbcDriver = "com.mysql.jdbc.Driver";
	
	private static final String INSERT_USERS_SQL = "INSERT INTO users" + " (name, email, country) VALUES" + " (?, ?, ?);";
	
	private static final String SELECT_USER_BY_ID = "select id,name,email,country from users where id =?";
	private static final String SELECT_ALL_USERS = "select * from users";
	private static final String DELETE_USERS_SQL = "delete from users where id = ?;";
	private static final String UPDATE_USERS_SQL = "update users set name = ?, email= ?, country =? where id = ?;";
	
	public UserDao() {

	}
	
	protected Connection getConnection() {
		Connection connection = null;
		try {
			Class.forName(jdbcDriver);
			connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return connection;
	}
	
	//CRUD Operations
	//Insert User
	public void insertUser(User user) throws SQLException{
		System.out.println(INSERT_USERS_SQL);
		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USERS_SQL)){
			preparedStatement.setString(1, user.getName());
			preparedStatement.setString(2, user.getEmail());
			preparedStatement.setString(3, user.getCountry());
			System.out.println(preparedStatement);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			printSQLException(e);
		}
	}
	
	//Select user by id
	public User selectUser(int id) {
		User user = null;
		// Establishing the connection
		try (Connection connection = getConnection();
				//Creating a statement
				PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_ID);){
			preparedStatement.setInt(1, id);
			System.out.println(preparedStatement);
			//Runing the query
			ResultSet as = preparedStatement.executeQuery();
			
			while(as.next()) {
				String name = as.getString("name");
				String email = as.getString("email");
				String country = as.getString("country");
				user = new User(id, name, email, country);
			}
		} catch(SQLException e) {
			printSQLException(e);
		}
		return user;
	}
	
	//Select all users
	public List<User> selectAllUsers(){
		List<User> users = new ArrayList<>();
		try (Connection connection = getConnection();
				
			PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_USERS);){
			System.out.println(preparedStatement);
			ResultSet as = preparedStatement.executeQuery();
			
			while(as.next()) {
				int id = as.getInt("id");
				String name = as.getString("name");
				String email = as.getString("email");
				String country = as.getString("country");
				users.add(new User(id, name, email, country));
			}
		} catch (SQLException e) {
			printSQLException(e);
		}
		return users;
	}
	//update user
	public boolean updateUser(User user) throws SQLException{
		boolean rwUpdate;
		try (Connection connection = getConnection();
				PreparedStatement statement = connection.prepareStatement(UPDATE_USERS_SQL);){
			System.out.println("Updated User: " + statement);
			statement.setString(1, user.getName());
			statement.setString(2, user.getEmail());
			statement.setString(3, user.getCountry());
			statement.setInt(4, user.getId());
			
			rwUpdate = statement.executeUpdate() > 0;
		}
		return rwUpdate;
	}
	
	//delete user
	public boolean deleteUser(int id) throws SQLException{
		boolean rwDeleted;
		try (Connection connection = getConnection();
				PreparedStatement statement = connection.prepareStatement(DELETE_USERS_SQL);){
			statement.setInt(1, id);
			rwDeleted = statement.executeUpdate() > 0;
		}
		return rwDeleted;
	}
	
	//printSQLException method
	private void printSQLException(SQLException ex) {
		for(Throwable e : ex) {
			if(e instanceof SQLException) {
				e.printStackTrace(System.err);
				System.err.println("SQLState: "+ ((SQLException) e).getSQLState());
				System.err.println("Error code: "+ ((SQLException) e).getErrorCode());
				System.err.println("Message: "+ e.getMessage());
				Throwable t = ex.getCause();
				while(t != null) {
					System.out.println("Cause: "+ t);
					t = t.getCause();
				}
			}
		}
	}
}
