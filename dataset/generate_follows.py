import csv
import random
import math

def generate_follows_relationships(input_file, output_file, edges_per_user=3):
    """
    Generate follows relationships for users with a non-uniform distribution.
    Some users will be more popular (have more followers) than others.
    
    Args:
        input_file: Path to the US users CSV file
        output_file: Path to save the follows relationships CSV
        edges_per_user: Average number of follows per user (default: 3)
    """
    # Read all user IDs from the US users file
    user_ids = []
    with open(input_file, 'r', encoding='utf-8') as f:
        reader = csv.DictReader(f)
        for row in reader:
            user_ids.append(row['UserID'])
    
    num_users = len(user_ids)
    total_edges = num_users * edges_per_user
    
    print(f"Total users: {num_users}")
    print(f"Generating approximately {total_edges} follows relationships...")
    
    # Create popularity weights using power-law distribution
    # This makes some users much more popular than others
    popularity_weights = []
    for i in range(num_users):
        # Power-law: weight = (i+1)^(-alpha) where alpha controls the distribution
        # Lower index = more popular (we'll shuffle later)
        weight = (i + 1) ** (-0.8)  # alpha=0.8 gives a good power-law distribution
        popularity_weights.append(weight)
    
    # Normalize weights to probabilities
    total_weight = sum(popularity_weights)
    popularity_probs = [w / total_weight for w in popularity_weights]
    
    # Shuffle user indices to randomize which users are popular
    user_indices = list(range(num_users))
    random.shuffle(user_indices)
    
    # Create a mapping from shuffled index to actual popularity
    shuffled_probs = [popularity_probs[user_indices[i]] for i in range(num_users)]
    
    # Generate follows relationships
    follows = []
    seen_pairs = set()  # To avoid duplicate follows relationships
    
    for _ in range(total_edges):
        # Select a follower (uniform distribution)
        follower_idx = random.randint(0, num_users - 1)
        follower_id = user_ids[follower_idx]
        
        # Select who they follow (weighted by popularity)
        # Use the popularity distribution to select a user to follow
        followed_idx = random.choices(
            range(num_users),
            weights=shuffled_probs,
            k=1
        )[0]
        followed_id = user_ids[followed_idx]
        
        # Avoid self-follows and duplicate relationships
        if follower_id != followed_id:
            pair = (follower_id, followed_id)
            if pair not in seen_pairs:
                follows.append(pair)
                seen_pairs.add(pair)
    
    # Write to CSV
    with open(output_file, 'w', encoding='utf-8', newline='') as f:
        writer = csv.writer(f)
        writer.writerow(['FollowerID', 'FollowedID'])  # Header
        writer.writerows(follows)
    
    print(f"Generated {len(follows)} unique follows relationships")
    
    # Calculate and display some statistics
    follower_counts = {}
    followed_counts = {}
    
    for follower, followed in follows:
        follower_counts[follower] = follower_counts.get(follower, 0) + 1
        followed_counts[followed] = followed_counts.get(followed, 0) + 1
    
    # Find most popular users (most followed)
    sorted_by_followers = sorted(followed_counts.items(), key=lambda x: x[1], reverse=True)
    
    print(f"\nTop 10 most followed users:")
    for user_id, count in sorted_by_followers[:10]:
        print(f"  User {user_id}: {count} followers")
    
    print(f"\nAverage followers per user: {sum(followed_counts.values()) / num_users:.2f}")
    print(f"Average follows per user: {sum(follower_counts.values()) / num_users:.2f}")
    print(f"Max followers: {max(followed_counts.values()) if followed_counts else 0}")
    print(f"Min followers: {min(followed_counts.values()) if followed_counts else 0}")

if __name__ == "__main__":
    # Set random seed for reproducibility (optional)
    random.seed(42)
    
    input_file = "SocialMediaUsersDataset_US.csv"
    output_file = "follows.csv"
    
    # Generate follows relationships with average of 3 follows per user
    # This creates a realistic social network where some users are more popular
    generate_follows_relationships(input_file, output_file, edges_per_user=3)

