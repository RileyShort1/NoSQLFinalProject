package org.example.nosqlfinalproject;

public class User {
    private int id;
    private String firstName;
    private String lastName;
    private String gender;
    private String dob;
    private String interests;
    private String bio;
    private String city;
    private String country;

    public User(int id, String firstName, String lastName, String gender, String dob, String interests, String bio, String city, String country) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.dob = dob;
        this.interests = interests;
        this.bio = bio;
        this.city = city;
        this.country = country;
    }

    // Getters
    public int getId() { 
        return id; 
    }

    public String getFirstName() { 
        return firstName; 
    }

    public String getLastName() { 
        return lastName; 
    }

    public String getGender() { 
        return gender; 
    }

    public String getDob() { 
        return dob; 
    }

    public String getInterests() { 
        return interests; 
    }

    public String getBio() { 
        return bio; 
    }

    public String getCity() { 
        return city; 
    }

    public String getCountry() { 
        return country; 
    }

    // Setters
    public void setId(int id) { 
        this.id = id; 
    }

    public void setFirstName(String firstName) { 
        this.firstName = firstName; 
    }

    public void setLastName(String lastName) { 
        this.lastName = lastName; 
    }

    public void setGender(String gender) { 
        this.gender = gender; 
    }

    public void setDob(String dob) { 
        this.dob = dob; 
    }

    public void setInterests(String interests) { 
        this.interests = interests; 
    }

    public void setBio(String bio) { 
        this.bio = bio; 
    }

    public void setCity(String city) { 
        this.city = city; 
    }

    public void setCountry(String country) { 
        this.country = country; 
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}

