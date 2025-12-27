package com.mvc_unit.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mvc_unit.entity.User;
import com.mvc_unit.repository.UserRepository;



@Service
public class UserService {
	
	private UserRepository userRepo;

	public UserService(UserRepository userRepo) {
		this.userRepo = userRepo;
	}
	
	public String getUserName(int id) {
        return userRepo.findById(id).map(User::getName).orElse("Unknown User");
    }

    public User createUser(User user) {
        return userRepo.save(user);
    }

    public Optional<User> updateUser(int id, User user) {
        return userRepo.findById(id).map(existing -> userRepo.save(new User(id, user.getName())));
    }

    public boolean deleteUser(int id) {
        if (userRepo.findById(id).isPresent()) {
        	userRepo.deleteById(id);
            return true;
        }
        return false;
    }
    
    public List<User> findAllUsers() {
        return userRepo.findAll();
    }
    
}
