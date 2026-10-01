<h1 align="center" id="title">AI Expense Tracker</h1>

<p align="center"><img src="https://socialify.git.ci/enigmatic1910/expense-tracker/image?language=1&amp;name=1&amp;owner=1&amp;theme=Light" alt="project-image"></p>

<p id="description">A secure AI-powered expense tracking application built with Java Spring Boot and Next.js. The application helps users manage accounts cards transactions spending categories and personalized financial insights.</p>

<h2>🚀 Demo</h2>

[https://expense-tracker-frontend-one-ivory.vercel.app/](https://expense-tracker-frontend-one-ivory.vercel.app/)

  
  
<h2>🧐 Features</h2>

Here're some of the project's best features:

*   User registration login JWT authentication and refresh tokens
*   Manage bank accounts cards categories and payment methods
*   Record income expenses and account transfers
*   View transaction summaries category-wise spending and spending trends
*   Parse natural-language expense descriptions using Google Gemini
*   Generate AI-powered financial insights and actionable recommendations
*   Validation centralized exception handling and ownership checks

<h2>🛠️ Installation Steps:</h2>

<p>1. Clone Repository</p>

```
git clone < repo url >
cd expense-tracker
```

<p>2. Configure PostgresSql</p>

<p>3. Configure Redis</p>

<p>4. Configure Environment Variables</p>

```
DB_URL=your_database_url 
DB_USER=postgres 
DB_PASS=your_database_password 
GEMINI_API_KEY=your_gemini_api_key 
GEMINI_MODEL=gemini-2.5-flash 
SECRET_KEY=your_secure_jwt_secret_key 
CORS_ALLOWED_ORIGIN=your_frontend_url
```

<p>5. Build Project</p>

```
mvnw.cmd clean install
```

<p>6. Run Application</p>

```
mvnw.cmd spring-boot:run
```

  
  
<h2>💻 Built with</h2>

Technologies used in the project:

*   Java 21
*   SpringBoot
*   SpringDataJpa
*   Supabase
*   Redis
*   SpringSecurity
*   JWT
*   Hibernate
*   Mapstruct
*   Lombok
*   SpringAI
*   Spring Scheduling
*   Railway
*   Vercel
