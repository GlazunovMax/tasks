package by.htp.library.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import by.htp.library.bean.User;
import by.htp.library.dao.UserDao;
import by.htp.library.dao.exception.DaoException;
import by.htp.library.dbConnection.ConnectionPool;
import by.htp.library.dbConnection.ConnectionPoolException;
import by.htp.library.dbConnection.FactoryConnectionPool;

public class DatabaseUserDao implements UserDao {
	private static final Logger log = Logger.getLogger(DatabaseUserDao.class);

	private static final String SQL_SELECT = "SELECT id, name, surname, login, password, role FROM user WHERE login=? AND password=?";
	private static final String SQL_INSERT = "INSERT INTO user( name, surname, login, password, role) VALUES(?,?,?,?,?)";

	private static final String ID = "id";
	private static final String NAME = "name";
	private static final String SURNAME = "surname";
	private static final String LOGIN = "login";
	private static final String PASSWORD = "password";
	private static final String ROLE = "role";

	@Override
	public User signIn(String login, String password) throws DaoException {

		FactoryConnectionPool factory = FactoryConnectionPool.getInstance();
		ConnectionPool connectionPool = factory.getConnectionPool();
		Connection connection = null;

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		User user = null;

		try {
			user = new User();

			connection = connectionPool.takeConnection();

			preparedStatement = connection.prepareStatement(SQL_SELECT);

			preparedStatement.setString(1, login);
			preparedStatement.setString(2, password);

			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				user.setId(resultSet.getLong(ID));
				user.setName(resultSet.getString(NAME));
				user.setSurname(resultSet.getString(SURNAME));
				user.setLogin(resultSet.getString(LOGIN));
				user.setPassword(resultSet.getString(PASSWORD));
				user.setRole(resultSet.getString(ROLE));

			} else {
				user = null;
			}

		} catch (SQLException e) {
			throw new DaoException("User cannot sign in ", e);
		} catch (ConnectionPoolException e) {
			log.error("Error connecting to the connection pool ", e);
		} finally {
			try {
				if (resultSet != null)
					resultSet.close();
				log.trace("resultSet closed");
			} catch (SQLException e) {
				log.error("Cannot close resultSet ", e);
			}

			try {
				if (preparedStatement != null)
					preparedStatement.close();
				log.trace("preparedStatement closed");
			} catch (SQLException e) {
				log.error("Cannot close preparedStatement ", e);
			}

			try {
				if (connection != null)
					connection.close();
				log.trace("connection closed");
			} catch (SQLException e) {
				log.error("Cannot close connection ", e);
			}

		}

		return user;

	}

	@Override
	public void registration(User user) throws DaoException {
		FactoryConnectionPool factory = FactoryConnectionPool.getInstance();
		ConnectionPool connectionPool = factory.getConnectionPool();
		
		PreparedStatement preparedStatement = null;

		try (Connection connection = connectionPool.takeConnection()) {
			
			preparedStatement = connection.prepareStatement(SQL_INSERT);

			preparedStatement.setString(1, user.getName());
			preparedStatement.setString(2, user.getSurname());
			preparedStatement.setString(3, user.getLogin());
			preparedStatement.setString(4, user.getPassword());
			preparedStatement.setString(5, user.getRole());

			preparedStatement.executeUpdate();

		} catch (SQLException e) {
			throw new DaoException("User cannot registration ", e);
		} catch (ConnectionPoolException e) {
			log.error("Error connecting to the data source ", e);
		} finally {

			try {
				if (preparedStatement != null)
					preparedStatement.close();
				log.trace("preparedStatement closed");
			} catch (SQLException e) {
				log.error("Cannot close preparedStatement ", e);
			}
		}
	}
}