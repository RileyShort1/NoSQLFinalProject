https://www.kaggle.com/datasets/arindamsahoo/social-media-users?resource=download

I found this public dataset on Kaggle. It contains social media users with the attributes UserID,Name,Gender,DOB,Interests,City,Country. 

The original dataset is the file "SocialMediaUsersDataset.csv".
I generated two additional user data files "SocialMediaUsersDataset_US.csv" and "SocialMediaUsersDataset_US_with_bio.csv".
These files contain only the US users, this reduces the dataset to about 12k nodes (users).

The "SocialMediaUsersDataset_US_with_bio.csv" is a modified version of the dataset that adds a 'bio' column for the sake of the assignment spec. 

The "follows.csv" file is a generated list of follows edge data. It was generated with the python script "generate_follows.py". 
