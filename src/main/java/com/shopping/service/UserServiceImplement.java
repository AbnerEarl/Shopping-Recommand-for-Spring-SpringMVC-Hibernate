package com.shopping.service;

import com.shopping.dao.UserDao;
import com.shopping.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by 14437 on 2017/3/1.
 */
@Service
public class UserServiceImplement implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public User getUser(int id) {
        return userDao.getUser(id);
    }

    @Override
    public User getUser(String nameOrEmail) {
        return userDao.getUser(nameOrEmail);
    }

    @Override
    public void addUser(User user) {
        userDao.addUser(user);
    }

    @Override
    public boolean deleteUser(int id) {
        return userDao.deleteUser(id);
    }

    @Override
    public boolean updateUser(User user) {
        return userDao.updateUser(user);
    }

    @Override
    public List<User> getAllUser() {
        return userDao.getAllUser();
    }
}
