package org.example.dao.postgres;

import org.example.dao.DAO;
import org.example.dao.DAOUtil;
import org.example.exceptions.DAOException;
import org.example.models.User;
import org.example.models.taxienum.UserRole;
import org.example.security.PasswordAuthentication;
import org.example.util.LocalDateConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.example.exceptions.DAOException.*;

public class UserDAO implements DAO<User> {

    private static final Logger log = LoggerFactory.getLogger(UserDAO.class);
    private static final String CREATE = "INSERT INTO users(first_name,last_name,phone,user_role,email,birthday,password) VALUES(?, ?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE users SET (first_name=?,last_name=?,phone=?,email=?,birthday=?) WHERE user_id=?";
    private static final String UPDATE_PASSWORD = "UPDATE users SET password=? WHERE phone=? AND email=?";
    private static final String DELETE = "DELETE FROM users WHERE id=?";
    private static final String SELECT_ALL = "SELECT * FROM users";
    private static final String SELECT = SELECT_ALL + " WHERE user_id=?";
    private static final String SELECT_BY_PHONEMAIL = "SELECT * FROM users WHERE (phone=? OR email=?)";


    @Override
    public User create(User model, Connection con) throws SQLException {
        con.setAutoCommit(false);
        try (PreparedStatement statement = con.prepareStatement(CREATE)) {
            statement.setString(1, model.getFirstName());
            statement.setString(2, model.getSecondName());
            statement.setString(3, model.getPhone());
            statement.setString(4, String.valueOf(model.getRole()));
            statement.setString(5, model.getEmail());
            statement.setDate(6, Date.valueOf(model.getBirthDate()));
            boolean execute = statement.execute();
            con.commit();
            if (execute) {
                return model;
            }
        } catch (SQLException e) {
            DAOUtil.rollbackCommit(con, log);
            log.error(USER_NOT_CREATE, e);
            throw new DAOException(USER_NOT_CREATE);
        } finally {
            DAOUtil.connectionClose(con, log);
        }
        return User.builder().build();
    }

    public User create(User model, String password, Connection con) throws DAOException {
        try {
            con.commit();
            try (PreparedStatement statement = con.prepareStatement(CREATE)) {
                statement.setString(1, model.getFirstName());
                statement.setString(2, model.getSecondName());
                statement.setString(3, model.getPhone());
                statement.setString(4, String.valueOf(model.getRole()));
                statement.setString(5, model.getEmail());
                statement.setDate(6, Date.valueOf(model.getBirthDate()));
                PasswordAuthentication auth = new PasswordAuthentication(13);
                statement.setString(7, auth.hash(password.toCharArray()));
                ResultSet result = statement.executeQuery();
                if (result.next()) {
                    return buildUser(result);
                }
            }
        } catch (SQLException e) {
            DAOUtil.rollbackCommit(con, log);
            log.error(USER_NOT_CREATE, e);
            throw new DAOException(USER_NOT_CREATE);
        } finally {
            DAOUtil.connectionClose(con, log);
        }
        return null;
    }

    @Override
    public void update(User model, Connection con) {
        try {
            con.setAutoCommit(false);
            con.commit();
            try (PreparedStatement statement = con.prepareStatement(UPDATE)) {
                statement.setString(1, model.getFirstName());
                statement.setString(2, model.getSecondName());
                statement.setString(3, model.getPhone());
                statement.setString(4, String.valueOf(model.getRole()));
                statement.setString(5, model.getEmail());
                statement.setDate(6, Date.valueOf(model.getBirthDate()));
                if (!statement.execute()) {
                    log.error(USER_NOT_UPDATE);
                    throw new DAOException(USER_NOT_UPDATE);
                }
            }
        } catch (SQLException e) {
            DAOUtil.rollbackCommit(con, log);
            log.error(USER_NOT_FOUND, e);
            throw new DAOException(USER_NOT_FOUND);
        } finally {
            DAOUtil.connectionClose(con, log);
        }
    }

    @Override
    public User get(int id, Connection con) {
        try (PreparedStatement statement = con.prepareStatement(SELECT)) {
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return buildUser(result);
            }
        } catch (SQLException e) {
            log.error(USER_NOT_FOUND, e);
            throw new DAOException(USER_NOT_FOUND);
        } finally {
            DAOUtil.connectionClose(con, log);
        }
        return User.builder().build();
    }

    @Override
    public List<User> getAll(Connection con) {
        List<User> users = new ArrayList<>();
        try (PreparedStatement statement = con.prepareStatement(SELECT_ALL)) {
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                users.add(buildUser(result));
            }
        } catch (SQLException e) {
            log.error(USER_NOT_FOUND, e);
            throw new DAOException(USER_NOT_FOUND);
        } finally {
            DAOUtil.connectionClose(con, log);
        }
        return users;
    }

    @Override
    public void delete(int id, Connection con) {
        OrderDAO.callQuery(id, con, DELETE, log);
    }

    public User findUserPhoneMailAndPassword(String login, String password, Connection con)
            throws SQLException {
        try (PreparedStatement statement = con.prepareStatement(SELECT_BY_PHONEMAIL)) {
            PasswordAuthentication auth = new PasswordAuthentication(13);
            statement.setString(1, login);
            statement.setString(2, login);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                if (auth.authenticate(password.toCharArray(), resultSet.getString("password"))) {
                    return buildUser(resultSet);
                }
            }
            return null;
        } finally {
            DAOUtil.connectionClose(con, log);
        }
    }

    public void updatePassword(User model, String newPassword, Connection con) {
        try {
            con.commit();
            try (PreparedStatement statement = con.prepareStatement(UPDATE_PASSWORD)) {
                statement.setString(1, newPassword);
                statement.setString(2, model.getEmail());
                statement.setString(3, model.getPhone());
                if (!statement.execute()) {
                    String message = USER_NOT_UPDATE + "Password update failed.";
                    log.error(message);
                    throw new DAOException(message);
                }
            }
        } catch (SQLException e) {
            DAOUtil.rollbackCommit(con, log);
            log.error(USER_NOT_FOUND, e);
            throw new DAOException(USER_NOT_FOUND);
        } finally {
            DAOUtil.connectionClose(con, log);
        }
    }

    private User buildUser(ResultSet resultSet) throws SQLException {
        User user = User.builder().firstName(resultSet.getString("first_name"))
                .secondName(resultSet.getString("last_name"))
                .email(resultSet.getString("email"))
                .phone(resultSet.getString("phone"))
                .role(UserRole.getRole(resultSet.getString("user_role")))
                .birthDate(LocalDateConverter.convertToEntityAttribute(resultSet.getDate("birthday")))
                .build();
        return user;
    }

}
