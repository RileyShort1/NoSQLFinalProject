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
    private ListView<String> followingListView;
    
    // Followers Tab
    @FXML
    private ListView<String> followersListView;
    
    // Search Tab
    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton;
    @FXML
    private ListView<String> discoverListView;
    
    // Discover Tab
    @FXML
    private ListView<String> mutualFriendsListView;
    @FXML
    private ListView<String> popularUsersListView;
    @FXML
    private ListView<String> suggestedUsersListView;
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
    private List<String> followingList = new ArrayList<>();
    private List<String> followersList = new ArrayList<>();
    private String selectedUser = null;
    
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
        followingListView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                        } else {
                            HBox hbox = new HBox(10);
                            Text nameText = new Text(item);
                            nameText.setStyle("-fx-font-size: 14px;");
                            Button unfollowButton = new Button("Unfollow");
                            unfollowButton.setOnAction(e -> unfollowUser(item));
                            hbox.getChildren().addAll(nameText, unfollowButton);
                            setGraphic(hbox);
                        }
                    }
                };
            }
        });
    }
    
    private void setupFollowersListView() {
        followersListView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                        } else {
                            HBox hbox = new HBox(10);
                            Text nameText = new Text(item);
                            nameText.setStyle("-fx-font-size: 14px;");
                            
                            // Check if we're already following this user
                            Button actionButton;
                            if (followingList.contains(item)) {
                                actionButton = new Button("Unfollow");
                                actionButton.setOnAction(e -> unfollowUser(item));
                            } else {
                                actionButton = new Button("Follow");
                                actionButton.setOnAction(e -> followUser(item));
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
        discoverListView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                        } else {
                            HBox hbox = new HBox(10);
                            Text nameText = new Text(item);
                            nameText.setStyle("-fx-font-size: 14px; -fx-fill: #0066cc;");
                            
                            // Check if we're already following this user
                            Button actionButton;
                            if (followingList.contains(item)) {
                                actionButton = new Button("Unfollow");
                                actionButton.setOnAction(e -> {
                                    e.consume(); // Prevent cell selection
                                    unfollowUser(item);
                                });
                            } else {
                                actionButton = new Button("Follow");
                                actionButton.setOnAction(e -> {
                                    e.consume(); // Prevent cell selection
                                    followUser(item);
                                });
                            }
                            
                            hbox.getChildren().addAll(nameText, actionButton);
                            setGraphic(hbox);
                            
                            // Make the entire cell clickable to show profile
                            setOnMouseClicked(e -> {
                                if (e.getClickCount() == 1 && item != null) {
                                    showUserProfile(item);
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
        List<String> searchResults = new ArrayList<>();
        // This would be replaced with actual database query
        searchResults.add("Sample User 1");
        searchResults.add("Sample User 2");
        searchResults.add("Sample User 3");
        
        discoverListView.getItems().clear();
        discoverListView.getItems().addAll(searchResults);
    }
    
    private void followUser(String username) {
        if (!followingList.contains(username)) {
            followingList.add(username);
            followingListView.getItems().add(username);
            
            // Update the button in all lists if present
            refreshFollowersList();
            refreshDiscoverList();
            refreshMutualFriendsList();
            refreshPopularUsersList();
            refreshSuggestedUsersList();
            
            System.out.println("Now following: " + username);
        }
    }
    
    private void unfollowUser(String username) {
        if (followingList.contains(username)) {
            followingList.remove(username);
            followingListView.getItems().remove(username);
            
            // Update the button in all lists if present
            refreshFollowersList();
            refreshDiscoverList();
            refreshMutualFriendsList();
            refreshPopularUsersList();
            refreshSuggestedUsersList();
            
            System.out.println("Unfollowed: " + username);
        }
    }
    
    private void refreshFollowersList() {
        // Refresh the followers list view to update button states
        List<String> currentFollowers = new ArrayList<>(followersListView.getItems());
        followersListView.getItems().clear();
        followersListView.getItems().addAll(currentFollowers);
    }
    
    private void refreshDiscoverList() {
        // Refresh the discover list view to update button states
        List<String> currentDiscover = new ArrayList<>(discoverListView.getItems());
        discoverListView.getItems().clear();
        discoverListView.getItems().addAll(currentDiscover);
    }
    
    private void refreshMutualFriendsList() {
        // Refresh the mutual friends list view to update button states
        List<String> currentMutual = new ArrayList<>(mutualFriendsListView.getItems());
        mutualFriendsListView.getItems().clear();
        mutualFriendsListView.getItems().addAll(currentMutual);
    }
    
    private void refreshPopularUsersList() {
        // Refresh the popular users list view to update button states
        List<String> currentPopular = new ArrayList<>(popularUsersListView.getItems());
        popularUsersListView.getItems().clear();
        popularUsersListView.getItems().addAll(currentPopular);
    }
    
    private void refreshSuggestedUsersList() {
        // Refresh the suggested users list view to update button states
        List<String> currentSuggested = new ArrayList<>(suggestedUsersListView.getItems());
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
        followingList.add("John Doe");
        followingList.add("Jane Smith");
        followingListView.getItems().addAll(followingList);
    }
    
    private void loadFollowersList() {
        // TODO: Load actual followers list from database
        // For now, use sample data
        followersList.add("Alice Johnson");
        followersList.add("Bob Williams");
        followersList.add("Charlie Brown");
        followersListView.getItems().addAll(followersList);
    }
    
    private void showUserProfile(String username) {
        selectedUser = username;
        
        // Hide the "no selection" text and show the profile pane
        noSelectionText.setVisible(false);
        userProfilePane.setVisible(true);
        
        // TODO: Load actual user profile data from database
        // For now, use sample data based on username
        profileNameText.setText(username);
        
        // Sample profile data - replace with actual database query
        profileGenderText.setText(getSampleProfileData(username, "gender"));
        profileBirthdayText.setText(getSampleProfileData(username, "birthday"));
        profileCityText.setText(getSampleProfileData(username, "city"));
        profileCountryText.setText(getSampleProfileData(username, "country"));
        profileInterestsText.setText(getSampleProfileData(username, "interests"));
        profileBioText.setText(getSampleProfileData(username, "bio"));
        
        // Update follow button based on current following status
        if (followingList.contains(username)) {
            profileFollowButton.setText("Unfollow");
        } else {
            profileFollowButton.setText("Follow");
        }
    }
    
    private String getSampleProfileData(String username, String field) {
        // TODO: Replace with actual database query
        // This is just sample data for demonstration
        switch (field) {
            case "gender":
                return "Not specified";
            case "birthday":
                return "Not specified";
            case "city":
                return "Sample City";
            case "country":
                return "USA";
            case "interests":
                return "Technology, Music, Travel";
            case "bio":
                return "This is a sample bio for " + username + ". Replace this with actual database data.";
            default:
                return "-";
        }
    }
    
    @FXML
    private void handleProfileFollowAction() {
        if (selectedUser != null) {
            if (followingList.contains(selectedUser)) {
                unfollowUser(selectedUser);
            } else {
                followUser(selectedUser);
            }
            // Update the button text
            if (followingList.contains(selectedUser)) {
                profileFollowButton.setText("Unfollow");
            } else {
                profileFollowButton.setText("Follow");
            }
        }
    }
    
    private void setupMutualFriendsListView() {
        mutualFriendsListView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                        } else {
                            HBox hbox = new HBox(10);
                            Text nameText = new Text(item);
                            nameText.setStyle("-fx-font-size: 14px;");
                            
                            Button actionButton;
                            if (followingList.contains(item)) {
                                actionButton = new Button("Unfollow");
                                actionButton.setOnAction(e -> {
                                    e.consume();
                                    unfollowUser(item);
                                });
                            } else {
                                actionButton = new Button("Follow");
                                actionButton.setOnAction(e -> {
                                    e.consume();
                                    followUser(item);
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
        popularUsersListView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                        } else {
                            HBox hbox = new HBox(10);
                            Text nameText = new Text(item);
                            nameText.setStyle("-fx-font-size: 14px;");
                            
                            Button actionButton;
                            if (followingList.contains(item)) {
                                actionButton = new Button("Unfollow");
                                actionButton.setOnAction(e -> {
                                    e.consume();
                                    unfollowUser(item);
                                });
                            } else {
                                actionButton = new Button("Follow");
                                actionButton.setOnAction(e -> {
                                    e.consume();
                                    followUser(item);
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
        suggestedUsersListView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                        } else {
                            HBox hbox = new HBox(10);
                            Text nameText = new Text(item);
                            nameText.setStyle("-fx-font-size: 14px;");
                            
                            Button actionButton;
                            if (followingList.contains(item)) {
                                actionButton = new Button("Unfollow");
                                actionButton.setOnAction(e -> {
                                    e.consume();
                                    unfollowUser(item);
                                });
                            } else {
                                actionButton = new Button("Follow");
                                actionButton.setOnAction(e -> {
                                    e.consume();
                                    followUser(item);
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
        List<String> popularUsers = new ArrayList<>();
        popularUsers.add("Celebrity User 1");
        popularUsers.add("Celebrity User 2");
        popularUsers.add("Influencer User");
        popularUsers.add("Popular User 4");
        popularUsers.add("Trending User 5");
        popularUsersListView.getItems().addAll(popularUsers);
    }
    
    private void loadSuggestedUsersList() {
        // TODO: Load actual suggested users from database based on mutual connections, interests, etc.
        // For now, use sample data
        List<String> suggestedUsers = new ArrayList<>();
        suggestedUsers.add("Friend of Friend 1");
        suggestedUsers.add("Similar Interest User");
        suggestedUsers.add("Suggested User 3");
        suggestedUsers.add("Recommended User 4");
        suggestedUsersListView.getItems().addAll(suggestedUsers);
    }
}
