package org.example.nosqlfinalproject;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.neo4j.driver.Values;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Neo4jUserRepository {
    private final Driver driver;
    private final String databaseName;

    /**
     * Constructor without database name (uses default database)
     */
    public Neo4jUserRepository(Driver driver) {
        this.driver = driver;
        this.databaseName = null;
    }

    /**
     * Constructor with database name
     * @param driver The Neo4j driver
     * @param databaseName The name of the database to use (null for default database)
     */
    public Neo4jUserRepository(Driver driver, String databaseName) {
        this.driver = driver;
        this.databaseName = databaseName;
    }

    /**
     * Helper method to create a session with the specified database (if any)
     */
    private Session createSession() {
        if (databaseName != null && !databaseName.isEmpty()) {
            return driver.session(SessionConfig.forDatabase(databaseName));
        } else {
            return driver.session();
        }
    }

    /**
     * Create a new user node in Neo4j
     */
    public void createUser(User user) {
        try (Session session = createSession()) {
            // Combine firstName and lastName into name
            String fullName = (user.getFirstName() != null ? user.getFirstName() : "") + 
                             " " + (user.getLastName() != null ? user.getLastName() : "");
            fullName = fullName.trim();
            
            String query = """
                CREATE (u:User {
                    userId: $userId,
                    name: $name,
                    gender: $gender,
                    dob: $dob,
                    interests: $interests,
                    bio: $bio,
                    city: $city,
                    country: $country
                })
                RETURN u
                """;
            
            session.run(query, Values.parameters(
                "userId", user.getId(),
                "name", fullName,
                "gender", user.getGender() != null ? user.getGender() : "",
                "dob", user.getDob() != null ? user.getDob() : "",
                "interests", user.getInterests() != null ? user.getInterests() : "",
                "bio", user.getBio() != null ? user.getBio() : "",
                "city", user.getCity() != null ? user.getCity() : "",
                "country", user.getCountry() != null ? user.getCountry() : ""
            ));
        }
    }

    /**
     * Find a user by ID
     */
    public User findUserById(int id) {
        try (Session session = createSession()) {
            String query = "MATCH (u:User {userId: $userId}) RETURN u";
            Result result = session.run(query, Values.parameters("userId", id));
            
            if (result.hasNext()) {
                Record record = result.next();
                return mapRecordToUser(record);
            }
            return null;
        }
    }

    /**
     * Find users by name (searches the name property which contains full name)
     */
    public List<User> findUsersByName(String searchTerm) {
        try (Session session = createSession()) {
            // Search in the name property (which contains full name like "Randy Moreno")
            String query = """
                MATCH (u:User)
                WHERE u.name IS NOT NULL AND toLower(u.name) CONTAINS toLower($searchTerm)
                RETURN u
                LIMIT 50
                """;
            
            Result result = session.run(query, Values.parameters("searchTerm", searchTerm));
            
            List<User> users = result.stream()
                .map(this::mapRecordToUser)
                .collect(Collectors.toList());
            
            return users;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Find a user by exact first and last name match (case-insensitive)
     * Searches the name property which contains full name
     */
    public User findUserByExactName(String firstName, String lastName) {
        try (Session session = createSession()) {
            // Search in the name property for exact match (case-insensitive)
            String fullName = firstName + " " + lastName;
            String query = """
                MATCH (u:User)
                WHERE u.name IS NOT NULL AND toLower(u.name) = toLower($fullName)
                RETURN u
                LIMIT 1
                """;
            
            Result result = session.run(query, Values.parameters("fullName", fullName));
            
            if (result.hasNext()) {
                Record record = result.next();
                User user = mapRecordToUser(record);
                return user;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Test database connection and return count of users
     */
    public int testConnection() {
        try (Session session = createSession()) {
            String query = "MATCH (u:User) RETURN count(u) as userCount";
            Result result = session.run(query);
            if (result.hasNext()) {
                int count = result.next().get("userCount").asInt();
                return count;
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Update user profile information
     */
    public void updateUser(User user) {
        try (Session session = createSession()) {
            // Combine firstName and lastName into name
            String fullName = (user.getFirstName() != null ? user.getFirstName() : "") + 
                             " " + (user.getLastName() != null ? user.getLastName() : "");
            fullName = fullName.trim();
            
            String query = """
                MATCH (u:User {userId: $userId})
                SET u.name = $name,
                    u.gender = $gender,
                    u.dob = $dob,
                    u.interests = $interests,
                    u.bio = $bio,
                    u.city = $city,
                    u.country = $country
                RETURN u
                """;
            
            session.run(query, Values.parameters(
                "userId", user.getId(),
                "name", fullName,
                "gender", user.getGender() != null ? user.getGender() : "",
                "dob", user.getDob() != null ? user.getDob() : "",
                "interests", user.getInterests() != null ? user.getInterests() : "",
                "bio", user.getBio() != null ? user.getBio() : "",
                "city", user.getCity() != null ? user.getCity() : "",
                "country", user.getCountry() != null ? user.getCountry() : ""
            ));
        }
    }

    /**
     * Create a FOLLOWS relationship from follower to followee
     */
    public void followUser(int followerId, int followeeId) {
        try (Session session = createSession()) {
            String query = """
                MATCH (follower:User {userId: $followerId})
                MATCH (followee:User {userId: $followeeId})
                MERGE (follower)-[r:FOLLOWS]->(followee)
                RETURN r
                """;
            
            session.run(query, Values.parameters(
                "followerId", followerId,
                "followeeId", followeeId
            ));
        }
    }
    
    /**
     * Remove a FOLLOWS relationship
     */
    public void unfollowUser(int followerId, int followeeId) {
        try (Session session = createSession()) {
            String query = """
                MATCH (follower:User {userId: $followerId})-[r:FOLLOWS]->(followee:User {userId: $followeeId})
                DELETE r
                """;
            
            session.run(query, Values.parameters(
                "followerId", followerId,
                "followeeId", followeeId
            ));
        }
    }
    
    /**
     * Check if a user follows another user
     */
    public boolean isFollowing(int followerId, int followeeId) {
        try (Session session = createSession()) {
            String query = """
                MATCH (follower:User {userId: $followerId})-[r:FOLLOWS]->(followee:User {userId: $followeeId})
                RETURN count(r) > 0 as isFollowing
                """;
            
            Result result = session.run(query, Values.parameters(
                "followerId", followerId,
                "followeeId", followeeId
            ));
            
            if (result.hasNext()) {
                return result.next().get("isFollowing").asBoolean();
            }
            return false;
        }
    }
    
    /**
     * Get all users that a given user follows
     */
    public List<User> getFollowing(int userId) {
        try (Session session = createSession()) {
            String query = """
                MATCH (u:User {userId: $userId})-[r:FOLLOWS]->(followed:User)
                RETURN followed
                ORDER BY followed.name
                """;
            
            Result result = session.run(query, Values.parameters("userId", userId));
            return result.stream()
                .map(record -> {
                    var userNode = record.get("followed").asNode();
                    return mapNodeToUser(userNode);
                })
                .collect(Collectors.toList());
        }
    }
    
    /**
     * Get all users that follow a given user
     */
    public List<User> getFollowers(int userId) {
        try (Session session = createSession()) {
            String query = """
                MATCH (follower:User)-[r:FOLLOWS]->(u:User {userId: $userId})
                RETURN follower
                ORDER BY follower.name
                """;
            
            Result result = session.run(query, Values.parameters("userId", userId));
            return result.stream()
                .map(record -> {
                    var userNode = record.get("follower").asNode();
                    return mapNodeToUser(userNode);
                })
                .collect(Collectors.toList());
        }
    }
    
    /**
     * Get mutual friends (users that both user1 and user2 follow)
     */
    public List<User> getMutualFriends(int userId1, int userId2) {
        try (Session session = createSession()) {
            String query = """
                MATCH (u1:User {userId: $userId1})-[r1:FOLLOWS]->(mutual:User)<-[r2:FOLLOWS]-(u2:User {userId: $userId2})
                RETURN mutual
                ORDER BY mutual.name
                """;
            
            Result result = session.run(query, Values.parameters(
                "userId1", userId1,
                "userId2", userId2
            ));
            return result.stream()
                .map(record -> {
                    var userNode = record.get("mutual").asNode();
                    return mapNodeToUser(userNode);
                })
                .collect(Collectors.toList());
        }
    }

    /**
     * Get most popular users (users with the most followers)
     */
    public List<User> getPopularUsers(int limit) {
        try (Session session = createSession()) {
            String query = """
                MATCH (u:User)<-[r:FOLLOWS]-(follower:User)
                WITH u, count(r) as followerCount
                RETURN u
                ORDER BY followerCount DESC
                LIMIT $limit
                """;
            
            Result result = session.run(query, Values.parameters("limit", limit));
            return result.stream()
                .map(record -> {
                    var userNode = record.get("u").asNode();
                    return mapNodeToUser(userNode);
                })
                .collect(Collectors.toList());
        }
    }

    /**
     * Get suggested users for a given user
     * Suggests users based on mutual connections and similar interests
     */
    public List<User> getSuggestedUsers(int userId, int limit) {
        try (Session session = createSession()) {
            String query = """
                MATCH (u:User {userId: $userId})
                // Find users that are followed by people you follow (friends of friends)
                OPTIONAL MATCH (u)-[:FOLLOWS]->(friend:User)-[:FOLLOWS]->(suggested:User)
                WHERE suggested.userId <> $userId 
                  AND NOT (u)-[:FOLLOWS]->(suggested)
                WITH suggested, count(DISTINCT friend) as mutualCount
                ORDER BY mutualCount DESC
                LIMIT $limit
                RETURN suggested
                """;
            
            Result result = session.run(query, Values.parameters(
                "userId", userId,
                "limit", limit
            ));
            return result.stream()
                .map(record -> {
                    var userNode = record.get("suggested").asNode();
                    return mapNodeToUser(userNode);
                })
                .collect(Collectors.toList());
        }
    }

    /**
     * Get all users (for testing/debugging)
     */
    public List<User> getAllUsers() {
        try (Session session = createSession()) {
            String query = "MATCH (u:User) RETURN u LIMIT 1000";
            Result result = session.run(query);
            List<User> users = result.stream()
                .map(this::mapRecordToUser)
                .collect(Collectors.toList());
            return users;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Get sample users with their names (for debugging)
     */
    public void printSampleUsers(int limit) {
        try (Session session = createSession()) {
            // First, let's see what properties exist on User nodes
            String query = "MATCH (u:User) RETURN u LIMIT $limit";
            Result result = session.run(query, Values.parameters("limit", limit));
            // Method kept for potential future use but no output
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete a user and all their relationships
     */
    public void deleteUser(int userId) {
        try (Session session = createSession()) {
            String query = """
                MATCH (u:User {userId: $userId})
                DETACH DELETE u
                """;
            
            session.run(query, Values.parameters("userId", userId));
        }
    }

    /**
     * Helper method to map a Neo4j record to a User object
     */
    private User mapRecordToUser(Record record) {
        var userNode = record.get("u").asNode();
        return mapNodeToUser(userNode);
    }

    /**
     * Helper method to map a Neo4j node to a User object
     * Based on actual database schema: userId, name, gender, dob, interests, bio, city, country
     */
    private User mapNodeToUser(org.neo4j.driver.types.Node userNode) {
        // Handle userId - the actual property name in the database
        int id = 0;
        if (userNode.containsKey("userId") && !userNode.get("userId").isNull()) {
            try {
                id = userNode.get("userId").asInt();
            } catch (Exception e) {
                // userId could not be parsed as int
            }
        } else if (userNode.containsKey("id") && !userNode.get("id").isNull()) {
            try {
                id = userNode.get("id").asInt();
            } catch (Exception e) {
                // id could not be parsed as int
            }
        }
        
        // Handle name - split into firstName and lastName
        String firstName = "";
        String lastName = "";
        
        if (userNode.containsKey("name") && !userNode.get("name").isNull()) {
            String fullName = userNode.get("name").asString().trim();
            String[] nameParts = fullName.split("\\s+", 2);
            if (nameParts.length > 0) {
                firstName = nameParts[0];
            }
            if (nameParts.length > 1) {
                lastName = nameParts[1];
            }
        } else if (userNode.containsKey("firstName") && !userNode.get("firstName").isNull()) {
            firstName = userNode.get("firstName").asString();
            if (userNode.containsKey("lastName") && !userNode.get("lastName").isNull()) {
                lastName = userNode.get("lastName").asString();
            }
        }
        
        // Handle optional fields with null checks (using lowercase property names)
        String gender = (userNode.containsKey("gender") && !userNode.get("gender").isNull()) 
            ? userNode.get("gender").asString() : "";
        String dob = (userNode.containsKey("dob") && !userNode.get("dob").isNull()) 
            ? userNode.get("dob").asString() : "";
        String interests = (userNode.containsKey("interests") && !userNode.get("interests").isNull()) 
            ? userNode.get("interests").asString() : "";
        String bio = (userNode.containsKey("bio") && !userNode.get("bio").isNull()) 
            ? userNode.get("bio").asString() : "";
        String city = (userNode.containsKey("city") && !userNode.get("city").isNull()) 
            ? userNode.get("city").asString() : "";
        String country = (userNode.containsKey("country") && !userNode.get("country").isNull()) 
            ? userNode.get("country").asString() : "";
        
        return new User(id, firstName, lastName, gender, dob, interests, bio, city, country);
    }
}
