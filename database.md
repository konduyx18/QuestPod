# QuestPod Database Schema

## Database Tables

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
  }'::JSONB
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
  tags TEXT[] DEFAULT '{}'
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
  educational_content JSONB DEFAULT '{}'::JSONB
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
  parent_comment_id UUID REFERENCES story_comments(id),
  reactions JSONB DEFAULT '{}'::JSONB
);
```

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
```
