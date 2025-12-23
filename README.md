# 📱 Data Viewer App

A complete Android application for university mini-project demonstrating REST API integration, SQLite persistence, Material Design themes, and comprehensive Android concepts.

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![SQLite](https://img.shields.io/badge/SQLite-003B57?style=for-the-badge&logo=sqlite&logoColor=white)

## ✨ Features

### 🎨 Theme Management
- **Light Theme** - Clean, bright interface
- **Dark Theme** - Easy on the eyes at night
- **Ocean Theme** - Custom blue/teal color scheme
- Themes persist across app restarts

### 🔐 User Authentication
- Login/Welcome screen with input validation
- Session state stored in SharedPreferences
- Auto-login on app restart
- Secure logout with confirmation

### 🌐 API Integration
- Fetches posts from [JSONPlaceholder API](https://jsonplaceholder.typicode.com/posts)
- Uses `HttpURLConnection` for networking
- JSON parsing into model classes
- Graceful error handling

### 💾 Offline Support (SQLite)
- Local database storage for offline access
- Full CRUD operations (Create, Read, Update, Delete)
- Automatic sync when online
- Offline mode indicator

### 📋 Adapters & Lists
- Custom `RecyclerView.Adapter` with ViewHolder pattern
- Material CardView for list items
- Item click and long-click handling
- Pull-to-refresh functionality

### 📱 Menu Types
- **Options Menu** - Theme switching, refresh, logout
- **Context Menu** - Edit, delete, view on web (long-press)
- **Popup Menu** - Quick actions on each item

### 🌍 WebView Integration
- In-app web browser
- JavaScript enabled
- Loading progress indicator
- Back navigation within WebView

### 🔄 Lifecycle Management
- State preservation during rotation
- No unnecessary API re-fetching
- Proper resource cleanup

---

## 📂 Project Structure

```
app/src/main/java/com/example/hifzaa4/
├── activities/
│   ├── LoginActivity.java      # Login screen
│   ├── MainActivity.java       # Posts list
│   ├── DetailActivity.java     # Post details
│   ├── EditPostActivity.java   # Create/edit post
│   └── WebViewActivity.java    # WebView screen
├── adapters/
│   └── PostAdapter.java        # RecyclerView adapter
├── database/
│   ├── DatabaseHelper.java     # SQLite setup
│   └── PostRepository.java     # CRUD operations
├── models/
│   └── Post.java              # Data model
├── network/
│   └── ApiService.java        # API calls
└── utils/
    ├── ThemeManager.java      # Theme handling
    ├── NetworkUtils.java      # Connectivity checks
    └── AppPreferences.java    # SharedPreferences
```

---

## 🗄️ SQLite Database Schema

```sql
CREATE TABLE posts (
    id INTEGER PRIMARY KEY,
    user_id INTEGER NOT NULL,
    title TEXT NOT NULL,
    body TEXT NOT NULL,
    is_favorite INTEGER DEFAULT 0
);
```

---

## 🔄 Navigation Flow

```
┌─────────────┐     ┌─────────────┐     ┌──────────────┐
│   Login     │────▶│    Main     │────▶│    Detail    │
│   Screen    │     │  (Posts)    │     │    Screen    │
└─────────────┘     └─────────────┘     └──────────────┘
                           │                   │
                           ▼                   ▼
                    ┌─────────────┐     ┌──────────────┐
                    │  Add/Edit   │     │   WebView    │
                    │    Post     │     │    Screen    │
                    └─────────────┘     └──────────────┘
```

---

## 🛠️ Technologies Used

| Component | Technology |
|-----------|------------|
| Language | Java |
| Min SDK | API 24 (Android 7.0) |
| Target SDK | API 36 |
| UI | Material Design Components |
| Database | SQLite |
| Networking | HttpURLConnection |
| List Display | RecyclerView |
| API Source | JSONPlaceholder |

---

## 🚀 Setup Instructions

1. **Clone or download** this repository
2. **Open** in Android Studio (Hedgehog or later)
3. **Sync** Gradle files
4. **Run** on emulator or device (API 24+)

---

## 📸 Screenshots

| Login | Posts List | Dark Theme |
|-------|------------|------------|
| Welcome screen with validation | RecyclerView with cards | Material dark mode |

| Detail View | Edit Post | WebView |
|-------------|-----------|---------|
| Full post content | Form with inputs | In-app browser |

---

## 📋 Android Concepts Demonstrated

- [x] Multiple Activities with Intents
- [x] SharedPreferences for persistence
- [x] SQLite database with CRUD
- [x] HttpURLConnection for REST API
- [x] RecyclerView with custom Adapter
- [x] Material Design theming
- [x] Options, Context, and Popup Menus
- [x] WebView with JavaScript
- [x] Activity lifecycle handling
- [x] Configuration change handling
- [x] Input validation
- [x] SwipeRefreshLayout

---

## 👤 Author

**University Mini Project**

---

## 📄 License

This project is for educational purposes.
