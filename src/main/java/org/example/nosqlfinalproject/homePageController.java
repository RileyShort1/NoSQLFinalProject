package org.example.nosqlfinalproject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class homePageController {
    
    // Profile Tab Fields
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField genderField;
    @FXML
    private TextField birthdayField;
    @FXML
    private TextField cityField;
    @FXML
    private TextField countryField;
    @FXML
    private TextArea interestsArea;
    @FXML
    private TextArea bioArea;
    @FXML
    private Button saveProfileButton;
    
    // Following Tab
    @FXML
    private ListView<User> followingListView;
    
    // Followers Tab
    @FXML
    private ListView<User> followersListView;
    
    // Search Tab
    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton;
    @FXML
    private ListView<User> discoverListView;
    
    // Discover Tab
    @FXML
    private ListView<User> mutualFriendsListView;
    @FXML
    private ListView<User> popularUsersListView;
    @FXML
    private ListView<User> suggestedUsersListView;
    @FXML
    private VBox userProfilePane;
    @FXML
    private Text profileNameText;
    @FXML
    private Text profileGenderText;
    @FXML
    private Text profileBirthdayText;
    @FXML
    private Text profileCityText;
    @FXML
    private Text profileCountryText;
    @FXML
    private Text profileInterestsText;
    @FXML
    private Text profileBioText;
    @FXML
    private Button profileFollowButton;
    @FXML
    private Text noSelectionText;
    
    // TabPane
    @FXML
    private TabPane mainTabPane;
    
    // Sample data - replace with actual database connections
    private List<User> followingList = new ArrayList<>();
    private List<User> followersList = new ArrayList<>();
    private User selectedUser = null;
    
    @FXML
    public void initialize() {
        // Set up custom cell factories for list views to show follow/unfollow buttons
        setupFollowingListView();
        setupFollowersListView();
        setupDiscoverListView();
        setupMutualFriendsListView();
        setupPopularUsersListView();
        setupSuggestedUsersListView();
        
        // Load initial data (replace with actual data loading)
        loadProfileData();
        loadFollowingList();
        loadFollowersList();
        loadMutualFriendsList();
        loadPopularUsersList();
        loadSuggestedUsersList();
    }
    
    private void setupFollowingListView() {
        followingListView.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
            @Override
            public ListCell<User> call(ListView<User> param) {
                return new ListCell<User>() {
                    @Override
                    protected void updateItem(User user, boolean empty) {
                        super.updateItem(user, empty);
                        if (empty || user == null) {
                            setGraphic(null);
                        } else {
                            HBox hbox = new HBox(10);
                            Text nameText = new Text(user.toString());
                            nameText.setStyle("-fx-font-size: 14px;");
                            Button unfollowButton = new Button("Unfollow");
                            unfollowButton.setOnAction(e -> unfollowUser(user));
                            hbox.getChildren().addAll(nameText, unfollowButton);
                            setGraphic(hbox);
                        }
                    }
                };
            }
        });
    }
    
    private void setupFollowersListView() {
        followersListView.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
            @Override
            public ListCell<User> call(ListView<User> param) {
                return new ListCell<User>() {
                    @Override
                    protected void updateItem(User user, boolean empty) {
                        super.updateItem(user, empty);
                        if (empty || user == null) {
                            setGraphic(null);
                        } else {
                            HBox hbox = new HBox(10);
                            Text nameText = new Text(user.toString());
                            nameText.setStyle("-fx-font-size: 14px;");
                            
                            // Check if we're already following this user
                            Button actionButton;
                            if (isFollowing(user)) {
                                actionButton = new Button("Unfollow");
                                actionButton.setOnAction(e -> unfollowUser(user));
                            } else {
                                actionButton = new Button("Follow");
                                actionButton.setOnAction(e -> followUser(user));
                            }
                            
                            hbox.getChildren().addAll(nameText, actionButton);
                            setGraphic(hbox);
                        }
                    }
                };
            }
        });
    }
    
    private void setupDiscoverListView() {
        discoverListView.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
            @Override
            public ListCell<User> call(ListView<User> param) {
                return new ListCell<User>() {
                    @Override
                    protected void updateItem(User user, boolean empty) {
                        super.updateItem(user, empty);
                        if (empty || user == null) {
                            setGraphic(null);
                        } else {
                            HBox hbox = new HBox(10);
                            Text nameText = new Text(user.toString());
                            nameText.setStyle("-fx-font-size: 14px; -fx-fill: #0066cc;");
                            
                            // Check if we're already following this user
                            Button actionButton;
                            if (isFollowing(user)) {
                                actionButton = new Button("Unfollow");
                                actionButton.setOnAction(e -> {
                                    e.consume(); // Prevent cell selection
                                    unfollowUser(user);
                                });
                            } else {
                                actionButton = new Button("Follow");
                                actionButton.setOnAction(e -> {
                                    e.consume(); // Prevent cell selection
                                    followUser(user);
                                });
                            }
                            
                            hbox.getChildren().addAll(nameText, actionButton);
                            setGraphic(hbox);
                            
                            // Make the entire cell clickable to show profile
                            setOnMouseClicked(e -> {
                                if (e.getClickCount() == 1 && user != null) {
                                    showUserProfile(user);
                                }
                            });
                        }
                    }
                };
            }
        });
        
        // Also handle selection change event (when using keyboard navigation)
        discoverListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showUserProfile(newVal);
            }
        });
    }
    
    @FXML
    private void saveProfile() {
        // TODO: Implement actual save to database
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String gender = genderField.getText();
        String birthday = birthdayField.getText();
        String city = cityField.getText();
        String country = countryField.getText();
        String interests = interestsArea.getText();
        String bio = bioArea.getText();
        
        // For now, just show a confirmation
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Profile Saved");
        alert.setHeaderText(null);
        alert.setContentText("Your profile has been saved successfully!");
        alert.showAndWait();
        
        System.out.println("Profile saved:");
        System.out.println("Name: " + firstName + " " + lastName);
        System.out.println("Gender: " + gender);
        System.out.println("Birthday: " + birthday);
        System.out.println("Location: " + city + ", " + country);
        System.out.println("Interests: " + interests);
        System.out.println("Bio: " + bio);
    }
    
    @FXML
    private void searchUsers() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Search Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a search term.");
            alert.showAndWait();
            return;
        }
        
        // TODO: Implement actual search in database
        // For now, show sample results
        List<User> searchResults = new ArrayList<>();
        // This would be replaced with actual database query
        searchResults.add(new User(1, "Sample", "User 1", "Male", "1990-01-01", "Technology", "Sample bio 1", "New York", "USA"));
        searchResults.add(new User(2, "Sample", "User 2", "Female", "1992-05-15", "Music", "Sample bio 2", "Los Angeles", "USA"));
        searchResults.add(new User(3, "Sample", "User 3", "Other", "1988-12-20", "Travel", "Sample bio 3", "Chicago", "USA"));
        
        discoverListView.getItems().clear();
        discoverListView.getItems().addAll(searchResults);
    }
    
    private void followUser(User user) {
        if (!isFollowing(user)) {
            followingList.add(user);
            followingListView.getItems().add(user);
            
            // Update the button in all lists if present
            refreshFollowersList();
            refreshDiscoverList();
            refreshMutualFriendsList();
            refreshPopularUsersList();
            refreshSuggestedUsersList();
            
            System.out.println("Now following: " + user.toString());
        }
    }
    
    private void unfollowUser(User user) {
        if (isFollowing(user)) {
            followingList.removeIf(u -> u.getId() == user.getId());
            followingListView.getItems().removeIf(u -> u.getId() == user.getId());
            
            // Update the button in all lists if present
            refreshFollowersList();
            refreshDiscoverList();
            refreshMutualFriendsList();
            refreshPopularUsersList();
            refreshSuggestedUsersList();
            
            System.out.println("Unfollowed: " + user.toString());
        }
    }
    
    private boolean isFollowing(User user) {
        return followingList.stream().anyMatch(u -> u.getId() == user.getId());
    }
    
    private void refreshFollowersList() {
        // Refresh the followers list view to update button states
        List<User> currentFollowers = new ArrayList<>(followersListView.getItems());
        followersListView.getItems().clear();
        followersListView.getItems().addAll(currentFollowers);
    }
    
    private void refreshDiscoverList() {
        // Refresh the discover list view to update button states
        List<User> currentDiscover = new ArrayList<>(discoverListView.getItems());
        discoverListView.getItems().clear();
        discoverListView.getItems().addAll(currentDiscover);
    }
    
    private void refreshMutualFriendsList() {
        // Refresh the mutual friends list view to update button states
        List<User> currentMutual = new ArrayList<>(mutualFriendsListView.getItems());
        mutualFriendsListView.getItems().clear();
        mutualFriendsListView.getItems().addAll(currentMutual);
    }
    
    private void refreshPopularUsersList() {
        // Refresh the popular users list view to update button states
        List<User> currentPopular = new ArrayList<>(popularUsersListView.getItems());
        popularUsersListView.getItems().clear();
        popularUsersListView.getItems().addAll(currentPopular);
    }
    
    private void refreshSuggestedUsersList() {
        // Refresh the suggested users list view to update button states
        List<User> currentSuggested = new ArrayList<>(suggestedUsersListView.getItems());
        suggestedUsersListView.getItems().clear();
        suggestedUsersListView.getItems().addAll(currentSuggested);
    }
    
    private void loadProfileData() {
        // TODO: Load actual profile data from database
        // For now, leave fields empty or set sample data
    }
    
    private void loadFollowingList() {
        // TODO: Load actual following list from database
        // For now, use sample data
        User user1 = new User(101, "John", "Doe", "Male", "1990-01-15", "Technology, Gaming", "Tech enthusiast", "San Francisco", "USA");
        User user2 = new User(102, "Jane", "Smith", "Female", "1992-05-20", "Music, Art", "Artist and musician", "New York", "USA");
        followingList.add(user1);
        followingList.add(user2);
        followingListView.getItems().addAll(followingList);
    }
    
    private void loadFollowersList() {
        // TODO: Load actual followers list from database
        // For now, use sample data
        User user1 = new User(201, "Alice", "Johnson", "Female", "1988-03-10", "Travel, Photography", "World traveler", "Seattle", "USA");
        User user2 = new User(202, "Bob", "Williams", "Male", "1995-07-25", "Sports, Fitness", "Fitness coach", "Miami", "USA");
        User user3 = new User(203, "Charlie", "Brown", "Male", "1991-11-30", "Food, Cooking", "Chef", "Portland", "USA");
        followersList.add(user1);
        followersList.add(user2);
        followersList.add(user3);
        followersListView.getItems().addAll(followersList);
    }
    
    private void showUserProfile(User user) {
        selectedUser = user;
        
        // Hide the "no selection" text and show the profile pane
        noSelectionText.setVisible(false);
        userProfilePane.setVisible(true);
        
        // Display user profile data from User object
        profileNameText.setText(user.toString());
        profileGenderText.setText(user.getGender() != null ? user.getGender() : "Not specified");
        profileBirthdayText.setText(user.getDob() != null ? user.getDob() : "Not specified");
        profileCityText.setText(user.getCity() != null ? user.getCity() : "Not specified");
        profileCountryText.setText(user.getCountry() != null ? user.getCountry() : "Not specified");
        profileInterestsText.setText(user.getInterests() != null ? user.getInterests() : "Not specified");
        profileBioText.setText(user.getBio() != null ? user.getBio() : "Not specified");
        
        // Update follow button based on current following status
        if (isFollowing(user)) {
            profileFollowButton.setText("Unfollow");
        } else {
            profileFollowButton.setText("Follow");
        }
    }
    
    @FXML
    private void handleProfileFollowAction() {
        if (selectedUser != null) {
            if (isFollowing(selectedUser)) {
                unfollowUser(selectedUser);
            } else {
                followUser(selectedUser);
            }
            // Update the button text
            if (isFollowing(selectedUser)) {
                profileFollowButton.setText("Unfollow");
            } else {
                profileFollowButton.setText("Follow");
            }
        }
    }
    
    private void setupMutualFriendsListView() {
        mutualFriendsListView.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
            @Override
            public ListCell<User> call(ListView<User> param) {
                return new ListCell<User>() {
                    @Override
                    protected void updateItem(User user, boolean empty) {
                        super.updateItem(user, empty);
                        if (empty || user == null) {
                            setGraphic(null);
                        } else {
                            HBox hbox = new HBox(10);
                            Text nameText = new Text(user.toString());
                            nameText.setStyle("-fx-font-size: 14px;");
                            
                            Button actionButton;
                            if (isFollowing(user)) {
                                actionButton = new Button("Unfollow");
                                actionButton.setOnAction(e -> {
                                    e.consume();
                                    unfollowUser(user);
                                });
                            } else {
                                actionButton = new Button("Follow");
                                actionButton.setOnAction(e -> {
                                    e.consume();
                                    followUser(user);
                                });
                            }
                            
                            hbox.getChildren().addAll(nameText, actionButton);
                            setGraphic(hbox);
                        }
                    }
                };
            }
        });
    }
    
    private void setupPopularUsersListView() {
        popularUsersListView.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
            @Override
            public ListCell<User> call(ListView<User> param) {
                return new ListCell<User>() {
                    @Override
                    protected void updateItem(User user, boolean empty) {
                        super.updateItem(user, empty);
                        if (empty || user == null) {
                            setGraphic(null);
                        } else {
                            HBox hbox = new HBox(10);
                            Text nameText = new Text(user.toString());
                            nameText.setStyle("-fx-font-size: 14px;");
                            
                            Button actionButton;
                            if (isFollowing(user)) {
                                actionButton = new Button("Unfollow");
                                actionButton.setOnAction(e -> {
                                    e.consume();
                                    unfollowUser(user);
                                });
                            } else {
                                actionButton = new Button("Follow");
                                actionButton.setOnAction(e -> {
                                    e.consume();
                                    followUser(user);
                                });
                            }
                            
                            hbox.getChildren().addAll(nameText, actionButton);
                            setGraphic(hbox);
                        }
                    }
                };
            }
        });
    }
    
    private void setupSuggestedUsersListView() {
        suggestedUsersListView.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
            @Override
            public ListCell<User> call(ListView<User> param) {
                return new ListCell<User>() {
                    @Override
                    protected void updateItem(User user, boolean empty) {
                        super.updateItem(user, empty);
                        if (empty || user == null) {
                            setGraphic(null);
                        } else {
                            HBox hbox = new HBox(10);
                            Text nameText = new Text(user.toString());
                            nameText.setStyle("-fx-font-size: 14px;");
                            
                            Button actionButton;
                            if (isFollowing(user)) {
                                actionButton = new Button("Unfollow");
                                actionButton.setOnAction(e -> {
                                    e.consume();
                                    unfollowUser(user);
                                });
                            } else {
                                actionButton = new Button("Follow");
                                actionButton.setOnAction(e -> {
                                    e.consume();
                                    followUser(user);
                                });
                            }
                            
                            hbox.getChildren().addAll(nameText, actionButton);
                            setGraphic(hbox);
                        }
                    }
                };
            }
        });
    }
    
    private void loadMutualFriendsList() {
        // TODO: Load actual mutual friends from database
        // For now, leave empty as requested
        mutualFriendsListView.getItems().clear();
    }
    
    private void loadPopularUsersList() {
        // TODO: Load actual popular users from database
        // For now, use sample data
        List<User> popularUsers = new ArrayList<>();
        popularUsers.add(new User(301, "Celebrity", "User 1", "Male", "1985-06-10", "Entertainment, Acting", "Famous actor", "Los Angeles", "USA"));
        popularUsers.add(new User(302, "Celebrity", "User 2", "Female", "1990-08-15", "Music, Singing", "Pop star", "Nashville", "USA"));
        popularUsers.add(new User(303, "Influencer", "User", "Other", "1992-04-22", "Fashion, Lifestyle", "Fashion influencer", "New York", "USA"));
        popularUsers.add(new User(304, "Popular", "User 4", "Male", "1988-12-05", "Sports, Fitness", "Athlete", "Boston", "USA"));
        popularUsers.add(new User(305, "Trending", "User 5", "Female", "1995-02-18", "Technology, Innovation", "Tech entrepreneur", "San Francisco", "USA"));
        popularUsersListView.getItems().addAll(popularUsers);
    }
    
    private void loadSuggestedUsersList() {
        // TODO: Load actual suggested users from database based on mutual connections, interests, etc.
        // For now, use sample data
        List<User> suggestedUsers = new ArrayList<>();
        suggestedUsers.add(new User(401, "Friend of", "Friend 1", "Male", "1991-03-12", "Technology, Gaming", "Tech enthusiast", "Austin", "USA"));
        suggestedUsers.add(new User(402, "Similar Interest", "User", "Female", "1993-07-08", "Music, Art", "Musician", "Portland", "USA"));
        suggestedUsers.add(new User(403, "Suggested", "User 3", "Male", "1989-09-25", "Travel, Photography", "Travel blogger", "Denver", "USA"));
        suggestedUsers.add(new User(404, "Recommended", "User 4", "Female", "1994-11-14", "Food, Cooking", "Food blogger", "Seattle", "USA"));
        suggestedUsersListView.getItems().addAll(suggestedUsers);
    }
}
