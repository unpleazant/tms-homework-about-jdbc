package com.dbase;

import com.dbase.models.Address;
import com.dbase.models.PhoneNumber;
import com.dbase.models.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static com.dbase.helpers.DbHelper.*;
import static java.sql.ResultSet.*;

public class DbUserStorage {

    public DbUserStorage() {
        openConnection();
    }

    public void save(User user) {
        try {
            setAutoCommit(false);

            PreparedStatement userStatement = connection.prepareStatement("insert into users values (default, ?, ?, ?) returning id");
            userStatement.setString(1, user.getName());
            userStatement.setString(2, user.getUsername());
            userStatement.setString(3, user.getPassword());
            ResultSet userQueryResult = userStatement.executeQuery();
            userQueryResult.next();
            int newUserId = userQueryResult.getInt("id");

            PreparedStatement addressStatement = connection.prepareStatement("insert into user_address values (default, ?, ?)");
            for (Address address : user.getAddress()) {
                addressStatement.setString(1, address.getStreet());
                addressStatement.setInt(2, newUserId);
                addressStatement.execute();
            }

            PreparedStatement phoneNumberStatement = connection.prepareStatement("insert into user_phone_number values (default, ?, ?)");
            for (PhoneNumber phoneNumber : user.getPhoneNumber()) {
                phoneNumberStatement.setString(1, phoneNumber.getNumber());
                phoneNumberStatement.setInt(2, newUserId);
                phoneNumberStatement.execute();
            }

            connection.commit();
        } catch (SQLException throwables) {
            commitRollback();
            throwables.printStackTrace();
        } finally {
            setAutoCommit(true);
        }
    }

    public void updateNameById(int id, String name) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("update users set name = ? where id = ?");
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, id);
            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void updatePasswordById(int id, String password) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("update users set password = ? where id = ?");
            preparedStatement.setString(1, password);
            preparedStatement.setInt(2, id);
            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void deleteById(int id) {
        try {
            setAutoCommit(false);

            PreparedStatement userStatement = connection.prepareStatement("delete from users where id = ?");
            userStatement.setInt(1, id);
            userStatement.execute();

            PreparedStatement addressStatement = connection.prepareStatement("delete from user_address where user_id = ?");
            addressStatement.setInt(1, id);
            addressStatement.execute();

            PreparedStatement phoneNumberStatement = connection.prepareStatement("delete from user_phone_number where user_id = ?");
            phoneNumberStatement.setInt(1, id);
            phoneNumberStatement.execute();

            connection.commit();
        } catch (SQLException throwables) {
            commitRollback();
            throwables.printStackTrace();
        } finally {
            setAutoCommit(true);
        }
    }

    public Optional<User> getByUsername(String searchUsername) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "select u.id, u.name, u.username, u.password " +
                            "from users u " +
                            "where username = ?");

            preparedStatement.setString(1, searchUsername);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int userId = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                return Optional.of(new User(userId, name, username, password, getAddressesByUserID(userId), getPhoneNumbersByUserID(userId)));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<List<User>> getAll() {
        List<User> users = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from users", TYPE_SCROLL_SENSITIVE);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                users.add(new User(id, name, username, password, getAddressesByUserID(id), getPhoneNumbersByUserID(id)));
            }
            if (!users.isEmpty()) return Optional.of(users);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<List<User>> getAllByAddress(String address) {
        List<User> users = new ArrayList<>();
        try {
            PreparedStatement addressStatement = connection.prepareStatement("select * from user_address where address = ?");
            addressStatement.setString(1, address);
            ResultSet addressSet = addressStatement.executeQuery();

            PreparedStatement usersStatement = connection.prepareStatement("select u.id, u.name, u.username, u.password from users u where id = ?");

            while (addressSet.next()) {
                List<Address> addresses = new ArrayList<>();
                int id = addressSet.getInt("id");
                int user_id = addressSet.getInt("user_id");
                addresses.add(new Address(id, address));
                usersStatement.setInt(1, user_id);
                ResultSet userSet = usersStatement.executeQuery();
                if (userSet.next()) {
                    String name = userSet.getString("name");
                    String username = userSet.getString("username");
                    String password = userSet.getString("password");
                    users.add(new User(user_id, name, username, password, getAddressesByUserID(user_id), getPhoneNumbersByUserID(user_id)));
                }
            }
            if (!users.isEmpty()) return Optional.of(users);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<List<User>> getAllByName(String searchName) {
        List<User> users = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "select u.id, u.name, u.username, u.password " +
                        "from users u " +
                        "where name = ?")) {
            preparedStatement.setString(1, searchName);
            ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    String username = resultSet.getString("username");
                    String password = resultSet.getString("password");
                    users.add(new User(id, name, username, password, getAddressesByUserID(id), getPhoneNumbersByUserID(id)));
                }
                if (!users.isEmpty()) return Optional.of(users);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public boolean exist(User user) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from users where id = ?");
            preparedStatement.setInt(1, user.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public boolean existByID(int id) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from users where id = ?");
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }


    private List<Address> getAddressesByUserID(int user_id) {
        List<Address> addressList = new ArrayList<>();
        try (PreparedStatement prepareStatement = connection.prepareStatement("select * from user_address where user_id = ?")) {
            prepareStatement.setInt(1, user_id);
            ResultSet rSet = prepareStatement.executeQuery();
            while (rSet.next()) {
                int id = rSet.getInt("id");
                String address = rSet.getString("address");
                addressList.add(new Address(id, address));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return addressList;
    }

    private List<PhoneNumber> getPhoneNumbersByUserID(int user_id) {
        List<PhoneNumber> phoneNumberList = new ArrayList<>();
        try (PreparedStatement phoneNumberStatement = connection.prepareStatement("select * from user_phone_number where user_id = ?")) {
            phoneNumberStatement.setInt(1, user_id);
            ResultSet rSet = phoneNumberStatement.executeQuery();
            while (rSet.next()) {
                int id = rSet.getInt("id");
                String phoneNumber = rSet.getString("phone_number");
                phoneNumberList.add(new PhoneNumber(id, phoneNumber));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return phoneNumberList;
    }

}