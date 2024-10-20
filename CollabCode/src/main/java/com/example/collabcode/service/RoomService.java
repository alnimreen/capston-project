package com.example.collabcode.service;// RoomService.java
import com.example.collabcode.dto.RoleAssignmentRequest;
import com.example.collabcode.model.Room;
import com.example.collabcode.model.User;
import com.example.collabcode.repository.RoomRepository;
import com.example.collabcode.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository; // Assuming you have a UserRepository
    @Autowired
    private UserService userService;

    public List<User> getUsersInRoom(String roomId) {
        // Find the room by ID
        Optional<Room> roomOptional = roomRepository.findById(roomId);

        // Return an empty list if the room is not found
        if (!roomOptional.isPresent()) {
            return new ArrayList<>(); // No room found, return empty list
        }

        Room room = roomOptional.get();
        List<User> users = new ArrayList<>();

        // Fetch users who are participants of the room
        for (String participantId : room.getParticipants()) {
            Optional<User> user = userRepository.findById(participantId);
            user.ifPresent(users::add); // Add user to the list if present
        }

        return users;
    }

    // Method to assign role to a user in a room
    public void assignRole(String roomId, RoleAssignmentRequest request) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // Ensure the user is part of the room
        if (room.getParticipants().contains(request.getUserId())) {
            room.getUserRoles().put(request.getUserId(), request.getRole());
            roomRepository.save(room);  // Save the updated room
        } else {
            throw new RuntimeException("User is not part of the room");
        }
    }
}
