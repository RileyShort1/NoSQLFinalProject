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
    
    // Repository and current user
    private Neo4jUserRepository userRepository;
    private User currentUser;
    private int currentUserId = 1; // Default user ID - should be set from login
    
    // Cache for following list to avoid repeated queries
    private List<User> followingList = new ArrayList<>();
    private User selectedUser = null;
    
    /**
     * Set the repository and current user
     * This should be called after login or when initializing the controller
     */
    public void setRepository(Neo4jUserRepository repository, int userId) {
        this.userRepository = repository;
        this.currentUserId = userId;
        loadCurrentUser();
    }
    
    /**
     * Set the repository and current user with User object
     */
    public void setRepository(Neo4jUserRepository repository, User user) {
        this.userRepository = repository;
        this.currentUser = user;
        this.currentUserId = user.getId();
        
        // Load all data after setting repository and user
        loadProfileData();
        loadFollowingList();
        loadFollowersList();
        loadMutualFriendsList();
        loadPopularUsersList();
        loadSuggestedUsersList();
    }
    
    @FXML
    public void initialize() {
        // Set up custom cell factories for list views to show follow/unfollow buttons
        setupFollowingListView();
        setupFollowersListView();
        setupDiscoverListView();
        setupMutualFriendsListView();
        setupPopularUsersListView();
        setupSuggestedUsersListView();
        
        // Note: Repository should be set before loading data
        // If repository is not set, methods will handle gracefully
        if (userRepository != null) {
            loadProfileData();
            loadFollowingList();
            loadFollowersList();
            loadMutualFriendsList();
            loadPopularUsersList();
            loadSuggestedUsersList();
        }
    }
    
    private void loadCurrentUser() {
        if (userRepository != null) {
            currentUser = userRepository.findUserById(currentUserId);
        }
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
        if (userRepository == null || currentUser == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Repository not initialized. Please login first.");
            alert.showAndWait();
            return;
        }
        
        try {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String gender = genderField.getText();
            String birthday = birthdayField.getText();
            String city = cityField.getText();
            String country = countryField.getText();
            String interests = interestsArea.getText();
            String bio = bioArea.getText();
            
            // Update current user object
            currentUser.setFirstName(firstName);
            currentUser.setLastName(lastName);
            currentUser.setGender(gender);
            currentUser.setDob(birthday);
            currentUser.setCity(city);
            currentUser.setCountry(country);
            currentUser.setInterests(interests);
            currentUser.setBio(bio);
            
            // Save to database
            userRepository.updateUser(currentUser);
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Profile Saved");
            alert.setHeaderText(null);
            alert.setContentText("Your profile has been saved successfully!");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to save profile: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }
    
    @FXML
    private void searchUsers() {
        if (userRepository == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Repository not initialized. Please login first.");
            alert.showAndWait();
            return;
        }
        
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Search Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a search term.");
            alert.showAndWait();
            return;
        }
        
        try {
            List<User> searchResults = userRepository.findUsersByName(searchTerm);
            discoverListView.getItems().clear();
            discoverListView.getItems().addAll(searchResults);
            
            if (searchResults.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("No Results");
                alert.setHeaderText(null);
                alert.setContentText("No users found matching your search.");
                alert.showAndWait();
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to search users: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }
    
    private void followUser(User user) {
        if (userRepository == null || currentUser == null) {
            return;
        }
        
        try {
            // Don't allow following yourself
            if (user.getId() == currentUserId) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Cannot Follow");
                alert.setHeaderText(null);
                alert.setContentText("You cannot follow yourself.");
                alert.showAndWait();
                return;
            }
            
            if (!isFollowing(user)) {
                userRepository.followUser(currentUserId, user.getId());
                
                // Update local cache
                followingList.add(user);
                followingListView.getItems().add(user);
                
                // Refresh all lists to update button states
                refreshFollowersList();
                refreshDiscoverList();
                refreshMutualFriendsList();
                refreshPopularUsersList();
                refreshSuggestedUsersList();
                
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to follow user: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }
    
    private void unfollowUser(User user) {
        if (userRepository == null || currentUser == null) {
            return;
        }
        
        try {
            if (isFollowing(user)) {
                userRepository.unfollowUser(currentUserId, user.getId());
                
                // Update local cache
                followingList.removeIf(u -> u.getId() == user.getId());
                followingListView.getItems().removeIf(u -> u.getId() == user.getId());
                
                // Refresh all lists to update button states
                refreshFollowersList();
                refreshDiscoverList();
                refreshMutualFriendsList();
                refreshPopularUsersList();
                refreshSuggestedUsersList();
                
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to unfollow user: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }
    
    private boolean isFollowing(User user) {
        if (userRepository == null || currentUser == null) {
            return false;
        }
        
        try {
            return userRepository.isFollowing(currentUserId, user.getId());
        } catch (Exception e) {
            // Fallback to local cache if repository call fails
            return followingList.stream().anyMatch(u -> u.getId() == user.getId());
        }
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
        if (userRepository == null || currentUser == null) {
            return;
        }
        
        try {
            // Load current user data into form fields
            firstNameField.setText(currentUser.getFirstName() != null ? currentUser.getFirstName() : "");
            lastNameField.setText(currentUser.getLastName() != null ? currentUser.getLastName() : "");
            genderField.setText(currentUser.getGender() != null ? currentUser.getGender() : "");
            birthdayField.setText(currentUser.getDob() != null ? currentUser.getDob() : "");
            cityField.setText(currentUser.getCity() != null ? currentUser.getCity() : "");
            countryField.setText(currentUser.getCountry() != null ? currentUser.getCountry() : "");
            interestsArea.setText(currentUser.getInterests() != null ? currentUser.getInterests() : "");
            bioArea.setText(currentUser.getBio() != null ? currentUser.getBio() : "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadFollowingList() {
        if (userRepository == null) {
            return;
        }
        
        try {
            List<User> following = userRepository.getFollowing(currentUserId);
            followingList = new ArrayList<>(following);
            followingListView.getItems().clear();
            followingListView.getItems().addAll(following);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadFollowersList() {
        if (userRepository == null) {
            return;
        }
        
        try {
            List<User> followers = userRepository.getFollowers(currentUserId);
            followersListView.getItems().clear();
            followersListView.getItems().addAll(followers);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void showUserProfile(User user) {
    // Set the currently selected user (used for mutual friends, follow button, etc.)
    selectedUser = user;

    // Hide the "no selection" text and show the profile pane
    noSelectionText.setVisible(false);
    userProfilePane.setVisible(true);

    // Display user profile data from User object
    profileNameText.setText(user.toString());
    profileGenderText.setText(
            user.getGender() != null && !user.getGender().isEmpty()
                    ? user.getGender()
                    : "Not specified"
    );
    profileBirthdayText.setText(
            user.getDob() != null && !user.getDob().isEmpty()
                    ? user.getDob()
                    : "Not specified"
    );
    profileCityText.setText(
            user.getCity() != null && !user.getCity().isEmpty()
                    ? user.getCity()
                    : "Not specified"
    );
    profileCountryText.setText(
            user.getCountry() != null && !user.getCountry().isEmpty()
                    ? user.getCountry()
                    : "Not specified"
    );
    profileInterestsText.setText(
            user.getInterests() != null && !user.getInterests().isEmpty()
                    ? user.getInterests()
                    : "Not specified"
    );
    profileBioText.setText(
            user.getBio() != null && !user.getBio().isEmpty()
                    ? user.getBio()
                    : "Not specified"
    );

    // Update follow button based on current following status
    if (isFollowing(user)) {
        profileFollowButton.setText("Unfollow");
    } else {
        profileFollowButton.setText("Follow");
    }

    // Refresh mutual friends between the logged-in user and this selected user
    loadMutualFriendsList();
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
    // Need repo, logged-in user, and some other selected user
    if (userRepository == null || currentUser == null || selectedUser == null) {
        mutualFriendsListView.getItems().clear();
        return;
    }

    try {
        // Mutual friends = users followed by both currentUser and selectedUser
        List<User> mutuals = userRepository.getMutualFriends(currentUserId, selectedUser.getId());

        mutualFriendsListView.getItems().clear();
        mutualFriendsListView.getItems().addAll(mutuals);
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    
    private void loadPopularUsersList() {
        if (userRepository == null) {
            return;
        }
        
        try {
            List<User> popularUsers = userRepository.getPopularUsers(10);
            popularUsersListView.getItems().clear();
            popularUsersListView.getItems().addAll(popularUsers);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadSuggestedUsersList() {
        if (userRepository == null || currentUser == null) {
            return;
        }
        
        try {
            List<User> suggestedUsers = userRepository.getSuggestedUsers(currentUserId, 10);
            suggestedUsersListView.getItems().clear();
            suggestedUsersListView.getItems().addAll(suggestedUsers);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
