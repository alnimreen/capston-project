package com.example.collabcode.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.nio.ByteBuffer;
import java.util.*;

@Document(collection ="Rooms")
public class Room {

    @Id
    private String id;
    private String name;
    private String uuid;
    private List<String> fileIds=new ArrayList<>(); // List of file IDs linked to the room
    private List<String> participants =new ArrayList<>();; // Users who are part of the room
    private Map<String, String> userRoles;  // Mapping of user to their role in the room
    private String owner; // Owner's userId for distinction
    // Override equals
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Room room = (Room) obj;
        return id.equals(room.id); // Compare by ID
    }
    @Override
    public int hashCode() {
        return Objects.hash(id); // Use ID for hashcode
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    // Constructor
    public Room(String id,String uuid) {
        this.id=id;
        this.uuid = uuid;
        this.userRoles = new HashMap<>();
        this.userRoles.put(uuid, "Admin"); // Assigning the creator as Admin
    }
    public Room() {
        this.userRoles = new HashMap<>();  // Initialize in the constructor
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<String> getFileIds() {
        return fileIds;
    }

    public void setFileIds(List<String> fileIds) {
        this.fileIds = fileIds;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public Map<String, String> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Map<String, String> userRoles) {
        this.userRoles = userRoles;
    }
    @Override
    public String toString() {
        return "Room{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", uuid='" + uuid + '\'' +
                ", userRoles=" + userRoles +
                ", fileIds=" + fileIds +
                '}';
    }

}