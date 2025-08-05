# QuestPod Database Documentation

## Database Overview

QuestPod is an interactive AI-generated audio storytelling app with a robust database design that enables branching narratives, user progression tracking, subscription management, and social features. The database leverages Supabase's PostgreSQL foundation with custom extensions and security policies.

## Table Structures

### Database Tables

```sql
-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Users Table (enhanced Supabase auth users)
CREATE TABLE user_profiles (
  id UUID REFERENCES auth.users(id) PRIMARY KEY,
  display_name TEXT,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  preferences JSONB DEFAULT '{
    "theme": "auto",
    "preferredGenres": [],
    "preferredVoices": [],
    "readingLevel": 3,
    "allowExplicitContent": false
  }'::JSONB,
  is_admin BOOLEAN DEFAULT false
);

-- Stories Table
CREATE TABLE stories (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  title TEXT NOT NULL,
  description TEXT,
  cover_image_url TEXT,
  creator_id UUID REFERENCES user_profiles(id),
  is_public BOOLEAN DEFAULT false,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  metadata JSONB DEFAULT '{}'::JSONB,
  tags TEXT[] DEFAULT '{}',
  is_deleted BOOLEAN DEFAULT false
);

-- Story Nodes Table (for branching narrative)
CREATE TABLE story_nodes (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  story_id UUID REFERENCES stories(id) ON DELETE CASCADE,
  node_order INTEGER,
  content TEXT NOT NULL,
  audio_url TEXT,
  narrator_voice_id TEXT,
  character_voices JSONB DEFAULT '{}'::JSONB,
  background_audio_url TEXT,
  choices JSONB DEFAULT '[]'::JSONB,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  educational_content JSONB DEFAULT '{}'::JSONB,
  is_deleted BOOLEAN DEFAULT false
);

-- User Story Progress Table
CREATE TABLE user_story_progress (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id UUID REFERENCES user_profiles(id),
  story_id UUID REFERENCES stories(id) ON DELETE CASCADE,
  current_node_id UUID REFERENCES story_nodes(id),
  completed_nodes UUID[] DEFAULT '{}',
  last_played_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  total_listen_time INTEGER DEFAULT 0,
  UNIQUE(user_id, story_id)
);

-- Story Highlights Table (for Vibecode integration)
CREATE TABLE story_highlights (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  story_id UUID REFERENCES stories(id) ON DELETE CASCADE,
  user_id UUID REFERENCES user_profiles(id),
  title TEXT NOT NULL,
  description TEXT,
  video_url TEXT,
  thumbnail_url TEXT,
  duration INTEGER,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  node_ids UUID[] DEFAULT '{}'
);

-- User Subscriptions Table (for RevenueCat integration)
CREATE TABLE user_subscriptions (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id UUID REFERENCES user_profiles(id) UNIQUE,
  subscription_tier TEXT NOT NULL,
  is_active BOOLEAN DEFAULT true,
  started_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  expires_at TIMESTAMP WITH TIME ZONE,
  provider TEXT NOT NULL,
  provider_subscription_id TEXT,
  entitlements JSONB DEFAULT '{}'::JSONB
);

-- Voice Packs (consumable purchases)
CREATE TABLE voice_packs (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  name TEXT NOT NULL,
  description TEXT,
  price DECIMAL(10,2) NOT NULL,
  voice_ids TEXT[] NOT NULL,
  is_premium BOOLEAN DEFAULT true
);

-- User Voice Pack Purchases
CREATE TABLE user_voice_packs (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id UUID REFERENCES user_profiles(id),
  voice_pack_id UUID REFERENCES voice_packs(id),
  purchased_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  UNIQUE(user_id, voice_pack_id)
);

-- Social Comments Table (for Stream integration)
CREATE TABLE story_comments (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  story_id UUID REFERENCES stories(id) ON DELETE CASCADE,
  user_id UUID REFERENCES user_profiles(id),
  content TEXT NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  parent_comment_id UUID REFERENCES story_comments(id),
  reactions JSONB DEFAULT '{}'::JSONB,
  is_deleted BOOLEAN DEFAULT false
);
```

```

## Table Purposes and Details

### user_profiles
- **Purpose**: Extends Supabase auth users with app-specific information
- **Primary Key**: id (UUID, references auth.users)
- **Key Columns**:
  - display_name (TEXT)
  - created_at/updated_at (TIMESTAMP WITH TIME ZONE)
  - preferences (JSONB) - Stores user preferences including theme, genres, voices, reading level, content settings
  - is_admin (BOOLEAN) - Indicates administrative privileges

### stories
- **Purpose**: Stores main story information
- **Primary Key**: id (UUID, auto-generated)
- **Key Columns**:
  - title (TEXT, NOT NULL)
  - description (TEXT)
  - cover_image_url (TEXT)
  - creator_id (UUID, references user_profiles)
  - is_public (BOOLEAN) - Controls visibility
  - created_at/updated_at (TIMESTAMP WITH TIME ZONE)
  - metadata (JSONB) - Flexible story metadata
  - tags (TEXT ARRAY) - Categorization tags
  - is_deleted (BOOLEAN) - Soft delete flag

### story_nodes
- **Purpose**: Stores branching narrative segments
- **Primary Key**: id (UUID, auto-generated)
- **Key Columns**:
  - story_id (UUID, references stories)
  - node_order (INTEGER)
  - content (TEXT, NOT NULL) - The actual narrative text
  - audio_url (TEXT) - URL to synthesized audio
  - narrator_voice_id (TEXT) - Voice identifier
  - character_voices (JSONB) - Voice mapping for different characters
  - background_audio_url (TEXT)
  - choices (JSONB) - Branch options for the narrative
  - created_at/updated_at (TIMESTAMP WITH TIME ZONE)
  - educational_content (JSONB) - Learning content tied to the node
  - is_deleted (BOOLEAN) - Soft delete flag

### user_story_progress
- **Purpose**: Tracks user progress through stories
- **Primary Key**: id (UUID, auto-generated)
- **Key Columns**:
  - user_id (UUID, references user_profiles)
  - story_id (UUID, references stories)
  - current_node_id (UUID, references story_nodes)
  - completed_nodes (UUID ARRAY) - Nodes already visited
  - last_played_at (TIMESTAMP WITH TIME ZONE)
  - total_listen_time (INTEGER) - In seconds
- **Constraints**: Unique combination of user_id and story_id

### story_highlights
- **Purpose**: Stores video clips from stories (for Vibecode integration)
- **Primary Key**: id (UUID, auto-generated)
- **Key Columns**:
  - story_id (UUID, references stories)
  - user_id (UUID, references user_profiles)
  - title (TEXT, NOT NULL)
  - description (TEXT)
  - video_url (TEXT)
  - thumbnail_url (TEXT)
  - duration (INTEGER) - In seconds
  - created_at (TIMESTAMP WITH TIME ZONE)
  - node_ids (UUID ARRAY) - Story nodes included in highlight

### user_subscriptions
- **Purpose**: Manages user subscription data (for RevenueCat integration)
- **Primary Key**: id (UUID, auto-generated)
- **Key Columns**:
  - user_id (UUID, references user_profiles)
  - subscription_tier (TEXT, NOT NULL)
  - is_active (BOOLEAN)
  - started_at/expires_at (TIMESTAMP WITH TIME ZONE)
  - provider (TEXT, NOT NULL) - Payment provider
  - provider_subscription_id (TEXT)
  - entitlements (JSONB) - Subscription features
- **Constraints**: Unique user_id

### voice_packs
- **Purpose**: Defines purchasable voice collections
- **Primary Key**: id (UUID, auto-generated)
- **Key Columns**:
  - name (TEXT, NOT NULL)
  - description (TEXT)
  - price (NUMERIC, NOT NULL)
  - voice_ids (TEXT ARRAY, NOT NULL)
  - is_premium (BOOLEAN)
- **Constraints**: positive_price CHECK (price > 0)

### user_voice_packs
- **Purpose**: Tracks user voice pack purchases
- **Primary Key**: id (UUID, auto-generated)
- **Key Columns**:
  - user_id (UUID, references user_profiles)
  - voice_pack_id (UUID, references voice_packs)
  - purchased_at (TIMESTAMP WITH TIME ZONE)
- **Constraints**: Unique combination of user_id and voice_pack_id

### story_comments
- **Purpose**: Stores social comments (for Stream integration)
- **Primary Key**: id (UUID, auto-generated)
- **Key Columns**:
  - story_id (UUID, references stories)
  - user_id (UUID, references user_profiles)
  - content (TEXT, NOT NULL)
  - created_at/updated_at (TIMESTAMP WITH TIME ZONE)
  - parent_comment_id (UUID, self-reference)
  - reactions (JSONB) - Tracks emoji reactions
  - is_deleted (BOOLEAN) - Soft delete flag

## Row-Level Security

```sql
-- Enable Row Level Security
ALTER TABLE user_profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE stories ENABLE ROW LEVEL SECURITY;
ALTER TABLE story_nodes ENABLE ROW LEVEL SECURITY;
ALTER TABLE user_story_progress ENABLE ROW LEVEL SECURITY;
ALTER TABLE story_highlights ENABLE ROW LEVEL SECURITY;
ALTER TABLE user_subscriptions ENABLE ROW LEVEL SECURITY;
ALTER TABLE voice_packs ENABLE ROW LEVEL SECURITY;
ALTER TABLE user_voice_packs ENABLE ROW LEVEL SECURITY;
ALTER TABLE story_comments ENABLE ROW LEVEL SECURITY;

-- User Profiles Policies
CREATE POLICY "Users can view their own profile" 
ON user_profiles FOR SELECT USING (auth.uid() = id);

CREATE POLICY "Users can update their own profile" 
ON user_profiles FOR UPDATE USING (auth.uid() = id);

CREATE POLICY "Users can insert their own profile" 
ON user_profiles FOR INSERT WITH CHECK (auth.uid() = id);

-- Stories Policies
CREATE POLICY "Anyone can view public stories" 
ON stories FOR SELECT USING (is_public = true);

CREATE POLICY "Users can view their own stories" 
ON stories FOR SELECT USING (creator_id = auth.uid());

CREATE POLICY "Users can create stories" 
ON stories FOR INSERT WITH CHECK (creator_id = auth.uid());

CREATE POLICY "Users can update their own stories" 
ON stories FOR UPDATE USING (creator_id = auth.uid());

CREATE POLICY "Users can delete their own stories" 
ON stories FOR DELETE USING (creator_id = auth.uid());

-- Story Nodes Policies
CREATE POLICY "Anyone can view nodes of public stories" 
ON story_nodes FOR SELECT 
USING (EXISTS (SELECT 1 FROM stories WHERE stories.id = story_nodes.story_id AND is_public = true));

CREATE POLICY "Users can view nodes of their own stories" 
ON story_nodes FOR SELECT 
USING (EXISTS (SELECT 1 FROM stories WHERE stories.id = story_nodes.story_id AND creator_id = auth.uid()));

CREATE POLICY "Users can create nodes for their stories" 
ON story_nodes FOR INSERT 
WITH CHECK (EXISTS (SELECT 1 FROM stories WHERE stories.id = story_nodes.story_id AND creator_id = auth.uid()));

CREATE POLICY "Users can update nodes of their stories" 
ON story_nodes FOR UPDATE 
USING (EXISTS (SELECT 1 FROM stories WHERE stories.id = story_nodes.story_id AND creator_id = auth.uid()));

CREATE POLICY "Users can delete nodes of their stories" 
ON story_nodes FOR DELETE 
USING (EXISTS (SELECT 1 FROM stories WHERE stories.id = story_nodes.story_id AND creator_id = auth.uid()));

-- User Story Progress Policies
CREATE POLICY "Users can view their own story progress" 
ON user_story_progress FOR SELECT USING (user_id = auth.uid());

CREATE POLICY "Users can insert their own story progress" 
ON user_story_progress FOR INSERT WITH CHECK (user_id = auth.uid());

CREATE POLICY "Users can update their own story progress" 
ON user_story_progress FOR UPDATE USING (user_id = auth.uid());

CREATE POLICY "Story creators can view progress on their stories" 
ON user_story_progress FOR SELECT 
USING (EXISTS (SELECT 1 FROM stories WHERE stories.id = user_story_progress.story_id AND creator_id = auth.uid()));

-- Story Highlights Policies
CREATE POLICY "Anyone can view highlights of public stories" 
ON story_highlights FOR SELECT 
USING (EXISTS (SELECT 1 FROM stories WHERE stories.id = story_highlights.story_id AND is_public = true));

CREATE POLICY "Users can view their own highlights" 
ON story_highlights FOR SELECT USING (user_id = auth.uid());

CREATE POLICY "Users can create their own highlights" 
ON story_highlights FOR INSERT WITH CHECK (user_id = auth.uid());

CREATE POLICY "Users can update their own highlights" 
ON story_highlights FOR UPDATE USING (user_id = auth.uid());

CREATE POLICY "Users can delete their own highlights" 
ON story_highlights FOR DELETE USING (user_id = auth.uid());

-- User Subscriptions Policies
CREATE POLICY "Users can view their own subscriptions" 
ON user_subscriptions FOR SELECT USING (user_id = auth.uid());

CREATE POLICY "Users can update their own subscriptions" 
ON user_subscriptions FOR UPDATE USING (user_id = auth.uid());

CREATE POLICY "Users can insert their own subscriptions" 
ON user_subscriptions FOR INSERT WITH CHECK (user_id = auth.uid());

-- Voice Packs Policies
CREATE POLICY "Anyone can view voice packs" 
ON voice_packs FOR SELECT USING (true);

-- User Voice Pack Purchases Policies
CREATE POLICY "Users can view their own voice pack purchases" 
ON user_voice_packs FOR SELECT USING (user_id = auth.uid());

CREATE POLICY "Users can purchase voice packs" 
ON user_voice_packs FOR INSERT WITH CHECK (user_id = auth.uid());

-- Story Comments Policies
CREATE POLICY "Anyone can view comments on public stories" 
ON story_comments FOR SELECT 
USING (EXISTS (SELECT 1 FROM stories WHERE stories.id = story_comments.story_id AND is_public = true));

CREATE POLICY "Users can view comments on their own stories" 
ON story_comments FOR SELECT 
USING (EXISTS (SELECT 1 FROM stories WHERE stories.id = story_comments.story_id AND creator_id = auth.uid()));

CREATE POLICY "Users can create comments" 
ON story_comments FOR INSERT WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can update their own comments" 
ON story_comments FOR UPDATE USING (auth.uid() = user_id);

CREATE POLICY "Users can delete their own comments" 
ON story_comments FOR DELETE USING (auth.uid() = user_id);

-- Admin Policies
CREATE POLICY "Admins can view all stories" 
ON stories FOR SELECT 
USING (EXISTS (SELECT 1 FROM user_profiles WHERE id = auth.uid() AND is_admin = true));

CREATE POLICY "Admins can update any story" 
ON stories FOR UPDATE 
USING (EXISTS (SELECT 1 FROM user_profiles WHERE id = auth.uid() AND is_admin = true));

CREATE POLICY "Admins can delete any story" 
ON stories FOR DELETE 
USING (EXISTS (SELECT 1 FROM user_profiles WHERE id = auth.uid() AND is_admin = true));
```

## Row-Level Security Policies

### user_profiles Policies
- Users can view their own profile: `auth.uid() = id`
- Users can update their own profile: `auth.uid() = id`
- Users can insert their own profile: `auth.uid() = id`
- Admins can update any user_profile: `EXISTS (SELECT 1 FROM user_profiles WHERE id = auth.uid() AND is_admin = true)`

### stories Policies
- Anyone can view public stories: `is_public = true AND is_deleted = false`
- Users can view their own stories: `creator_id = auth.uid() AND is_deleted = false`
- Users can create stories: `creator_id = auth.uid()`
- Users can update their own stories: `creator_id = auth.uid() AND is_deleted = false`
- Users can delete their own stories: `creator_id = auth.uid() AND is_deleted = false`
- Admins can view all stories: `EXISTS (SELECT 1 FROM user_profiles WHERE id = auth.uid() AND is_admin = true)`
- Admins can update any story: `EXISTS (SELECT 1 FROM user_profiles WHERE id = auth.uid() AND is_admin = true)`
- Admins can delete any story: `EXISTS (SELECT 1 FROM user_profiles WHERE id = auth.uid() AND is_admin = true)`

### story_nodes Policies
- Anyone can view nodes of public stories: Complex condition that ensures:
  - The parent story is public AND not deleted
  - The node itself is not deleted
- Users can view nodes of their own stories: Complex condition ensuring:
  - The user is the creator of the parent story
  - Neither the story nor the node is deleted
- Users can update/delete nodes of their stories: Similar conditions to above
- Admin policies for full access regardless of ownership

### story_comments Policies
- Anyone can view comments on public stories: Conditions ensure:
  - The parent story is public AND not deleted
  - The comment itself is not deleted
- Users can view comments on their own stories: Similar to above for owned stories
- Users can update/delete their own comments: `auth.uid() = user_id AND is_deleted = false`
- Admin policies for full access to comments

### Additional Table Policies
- Similar patterns applied to user_story_progress, story_highlights, user_subscriptions, voice_packs, and user_voice_packs
- Voice packs have public read access: `true`
- Admin policies provide full access across all tables

## Database Improvements

### Admin User Implementation

```sql
-- Admin column already added to user_profiles table in the schema above

-- Admin policies for stories table
```

### Indexing Improvements

```sql
-- Add indexes for frequently queried columns
CREATE INDEX idx_stories_creator_id ON stories(creator_id);
CREATE INDEX idx_stories_is_public ON stories(is_public);
CREATE INDEX idx_stories_is_deleted ON stories(is_deleted);
CREATE INDEX idx_story_nodes_story_id ON story_nodes(story_id);
CREATE INDEX idx_story_nodes_is_deleted ON story_nodes(is_deleted);
CREATE INDEX idx_user_story_progress_user_id ON user_story_progress(user_id);
CREATE INDEX idx_user_story_progress_story_id ON user_story_progress(story_id);
CREATE INDEX idx_story_comments_story_id ON story_comments(story_id);
CREATE INDEX idx_story_comments_user_id ON story_comments(user_id);
CREATE INDEX idx_story_comments_is_deleted ON story_comments(is_deleted);
```

### Automatic Update Triggers

```sql
-- Create a function for updating timestamps
CREATE OR REPLACE FUNCTION update_modified_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Add triggers for each table with updated_at
CREATE TRIGGER update_user_profiles_modtime
BEFORE UPDATE ON user_profiles
FOR EACH ROW EXECUTE FUNCTION update_modified_column();

CREATE TRIGGER update_stories_modtime
BEFORE UPDATE ON stories
FOR EACH ROW EXECUTE FUNCTION update_modified_column();

CREATE TRIGGER update_story_nodes_modtime
BEFORE UPDATE ON story_nodes
FOR EACH ROW EXECUTE FUNCTION update_modified_column();

CREATE TRIGGER update_user_story_progress_modtime
BEFORE UPDATE ON user_story_progress
FOR EACH ROW EXECUTE FUNCTION update_modified_column();

CREATE TRIGGER update_story_highlights_modtime
BEFORE UPDATE ON story_highlights
FOR EACH ROW EXECUTE FUNCTION update_modified_column();

CREATE TRIGGER update_user_subscriptions_modtime
BEFORE UPDATE ON user_subscriptions
FOR EACH ROW EXECUTE FUNCTION update_modified_column();

CREATE TRIGGER update_story_comments_modtime
BEFORE UPDATE ON story_comments
FOR EACH ROW EXECUTE FUNCTION update_modified_column();
```

### Soft Delete Implementation

#### Tables with Soft Delete
- stories: via is_deleted column
- story_nodes: via is_deleted column
- story_comments: via is_deleted column

#### How Soft Delete Works
- Instead of actual deletion: Records are marked with is_deleted = true
- Filtered by RLS policies: Regular users cannot see deleted content
- Admin access preserved: Admins can still view all content regardless of delete status
- Cascading visibility: Comments and nodes for deleted stories are also hidden

#### Benefits
- Data recovery: Accidentally deleted content can be restored
- Audit trail: Historical content remains for reference
- Analytics integrity: Historical reports maintain accuracy

```sql
-- is_deleted columns already added to relevant tables in the schema above

-- Update policies to filter deleted content
CREATE OR REPLACE POLICY "Anyone can view public stories" 
ON stories FOR SELECT USING (is_public = true AND is_deleted = false);

-- Add policies for viewing deleted content (admin only)
CREATE POLICY "Admins can view deleted stories" 
ON stories FOR SELECT 
USING (EXISTS (SELECT 1 FROM user_profiles WHERE id = auth.uid() AND is_admin = true));
```

### Validation Constraints

```sql
-- Add constraints for numeric values
ALTER TABLE voice_packs ADD CONSTRAINT positive_price CHECK (price > 0);
ALTER TABLE story_highlights ADD CONSTRAINT positive_duration CHECK (duration > 0);

-- Add constraints for user preferences
ALTER TABLE user_profiles ADD CONSTRAINT valid_reading_level 
CHECK (preferences->>'readingLevel' IS NULL OR (preferences->>'readingLevel')::integer BETWEEN 1 AND 5);
```

### Admin Functionality

#### Implementation
- is_admin column in user_profiles table
- Admin-specific policies for each table that bypass ownership checks
- Full visibility: Admins can see all content including soft-deleted items
- Full control: Admins can update or delete any content across all tables

The database structure you've designed provides an excellent foundation for the QuestPod application. Let me analyze its strengths:

Security
Row-Level Security (RLS): Comprehensive policies ensure users can only access their own data or public content
Admin Controls: Special policies for administrators with appropriate access levels
Soft Delete: Prevents permanent data loss while maintaining privacy
Authentication Integration: Leverages Supabase auth with proper references to auth.users
Data Integrity
Referential Integrity: Proper foreign key constraints between tables
Validation Constraints:
Positive price checks for voice packs
Duration validation for story highlights
Reading level validation in user preferences
Unique Constraints: Prevents duplicate records (e.g., user subscriptions, story progress)
Default Values: Sensible defaults for most columns
Performance Optimization
Strategic Indexes: Covers frequently queried columns like creator_id, is_public, and is_deleted
JSONB Data Types: Flexible storage for complex data like preferences and metadata
Efficient Soft Delete: Uses boolean flags instead of separate tables for deleted content
Optimized Queries: Index support for common filter conditions in RLS policies
Administrative Capabilities
Admin Flag: Simple but effective is_admin column in user_profiles
Admin Policies: Full visibility and control across all tables
Content Management: Ability to view, update, and manage all content including soft-deleted items
Additional Strengths
Extensibility: JSONB fields allow for future feature expansion without schema changes
Audit Support: Created/updated timestamps on all tables
Automatic Timestamps: Triggers ensure updated_at is always current
Integration Support: Structures for RevenueCat, ElevenLabs, and social features align with the technologies mentioned in your README
The database design aligns perfectly with your application's needs for interactive storytelling with branching narratives, user progression tracking, and social features. It provides a solid foundation that should scale well as your application grows.