# 📚 Book Review Platform

A full-stack web application for book reviews, ratings, and personalized recommendations built with Spring Boot and React.

## 🚀 Features

### 📖 Core Functionality
- **User Authentication**: JWT-based secure login/registration
- **Book Management**: Browse, search, and discover books
- **Review System**: Create, read, update, delete reviews with 1-5 star ratings
- **Favorites**: Add/remove books to personal favorites collection
- **User Profiles**: Manage profile information and view reading statistics

### 🤖 Smart Recommendations
- **Top-Rated Books**: Discover highest-rated books across all genres
- **Genre-Based**: Personalized recommendations based on favorite book genres
- **AI-Powered**: OpenAI integration ready for intelligent recommendations
- **Multiple Strategies**: Combines various recommendation algorithms

### 🎨 Modern UI/UX
- **Responsive Design**: Works seamlessly on desktop and mobile
- **Real-time Updates**: No caching, always fresh data
- **Interactive Components**: Modern UI with shadcn/ui components
- **Pagination**: Efficient browsing of large book collections

## 🛠️ Tech Stack

### Backend
- **Framework**: Spring Boot 3.x
- **Database**: H2 (development), PostgreSQL ready
- **Security**: Spring Security with JWT
- **Build Tool**: Gradle
- **API Documentation**: Swagger/OpenAPI

### Frontend
- **Framework**: React 18 with TypeScript
- **Build Tool**: Vite
- **Styling**: TailwindCSS + shadcn/ui
- **State Management**: React Query (TanStack Query)
- **Routing**: React Router v6
- **HTTP Client**: Axios

### Infrastructure
- **Deployment**: AWS ECS Fargate (Terraform scripts included)
- **Containerization**: Docker ready
- **CI/CD**: GitHub Actions ready

## 🏗️ Project Structure

```
cursor_demo/
├── backend/                 # Spring Boot API
│   ├── src/main/java/
│   │   └── com/bookreview/
│   │       ├── auth/        # Authentication & JWT
│   │       ├── book/        # Book management
│   │       ├── review/      # Review system
│   │       ├── user/        # User management
│   │       ├── recommendation/ # AI recommendations
│   │       └── config/      # Configuration
│   └── build.gradle
├── frontend/                # React application
│   ├── src/
│   │   ├── api/            # API clients
│   │   ├── components/     # Reusable components
│   │   ├── pages/          # Route components
│   │   ├── hooks/          # Custom React hooks
│   │   └── types/          # TypeScript definitions
│   └── package.json
├── infra/                  # Terraform infrastructure
└── docs/                   # Documentation
```

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Node.js 18+
- npm or yarn

### Backend Setup

1. **Navigate to backend directory**:
   ```bash
   cd backend
   ```

2. **Run the application**:
   ```bash
   ./gradlew bootRun
   ```

3. **Access Swagger UI**:
   - Open http://localhost:8080/swagger-ui.html

### Frontend Setup

1. **Navigate to frontend directory**:
   ```bash
   cd frontend
   ```

2. **Install dependencies**:
   ```bash
   npm install
   ```

3. **Start development server**:
   ```bash
   npm run dev
   ```

4. **Access the application**:
   - Open http://localhost:3000

## 📱 Application Flow

### Public Pages
- **Home**: Landing page with featured books
- **Books**: Browse and search all books
- **Book Details**: View book information and reviews
- **Login/Register**: User authentication

### Protected Pages (Requires Login)
- **Dashboard**: Personalized user dashboard
- **Profile**: User profile and statistics
- **Favorites**: Personal favorite books collection
- **Recommendations**: AI-powered book suggestions
- **Reviews**: Manage personal reviews

## 🔐 Authentication

The application uses JWT (JSON Web Tokens) for secure authentication:

1. **Register**: Create new account with email/password
2. **Login**: Authenticate and receive JWT token
3. **Protected Routes**: Token required for user-specific features
4. **Auto-logout**: Token expiration handling

## 📊 API Endpoints

### Authentication
- `POST /api/v1/auth/register` - User registration
- `POST /api/v1/auth/login` - User login
- `GET /api/v1/auth/verify` - Token verification

### Books
- `GET /api/v1/books` - Get paginated books
- `GET /api/v1/books/{id}` - Get book details
- `GET /api/v1/books/search` - Search books

### Reviews
- `GET /api/v1/reviews/book/{bookId}` - Get book reviews
- `POST /api/v1/reviews` - Create review
- `PUT /api/v1/reviews/{id}` - Update review
- `DELETE /api/v1/reviews/{id}` - Delete review

### Users & Favorites
- `GET /api/v1/users/profile` - Get user profile
- `GET /api/v1/users/my-favorites` - Get user favorites
- `POST /api/v1/users/favorites/books/{id}` - Add to favorites
- `DELETE /api/v1/users/favorites/books/{id}` - Remove from favorites

### Recommendations
- `GET /api/v1/recommendations/for-me` - Personalized recommendations
- `GET /api/v1/recommendations/top-rated` - Top-rated books

## 🤖 AI Integration

The platform includes OpenAI integration for intelligent recommendations:

1. **Configuration**: Set `application.openai.enabled=true`
2. **API Key**: Configure `application.openai.api-key`
3. **Smart Recommendations**: AI analyzes user preferences and reading history

## 🚀 Deployment

### AWS ECS Fargate (Recommended)

Terraform scripts are included for AWS deployment:

```bash
cd infra
terraform init
terraform plan
terraform apply
```

### Docker

Both frontend and backend are containerization-ready with Dockerfile configurations.

## 🧪 Testing

### Backend Tests
```bash
cd backend
./gradlew test
```

### Frontend Tests
```bash
cd frontend
npm test
```

## 📈 Features Roadmap

- [ ] **Social Features**: Follow users, share reviews
- [ ] **Advanced Search**: Filters, sorting, faceted search
- [ ] **Reading Lists**: Custom book collections
- [ ] **Book Clubs**: Group discussions and reading challenges
- [ ] **Mobile App**: React Native implementation
- [ ] **Analytics**: Reading statistics and insights

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Sushil Pawar**
- GitHub: [@sushilpawar1989](https://github.com/sushilpawar1989)
- Email: sushil.pawar@example.com

## 🙏 Acknowledgments

- Spring Boot community for excellent documentation
- React and TypeScript teams for amazing developer experience
- shadcn/ui for beautiful component library
- OpenAI for AI integration capabilities

---

⭐ **Star this repository if you found it helpful!**
