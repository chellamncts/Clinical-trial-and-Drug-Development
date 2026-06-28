# CTDS Backend

This Spring Boot project includes a basic authentication flow using simple Spring MVC.

## Implemented Auth Flow

- Home page: `/` or `/home`
- Login page: `/login`
- Login validation: database-backed user lookup + plain password comparison
- Session attributes: `username`, `role`
- Dashboard route: `/dashboard` (role-based view selection)
- Logout: `/logout`
- Default admin preload from `src/main/resources/data.sql`

## Default Admin

Configured in `src/main/resources/application.properties`:

- Username: `admin`
- Password: `admin@123`

## Main Auth Files

- `src/main/java/com/genc/ctds/auth/model/RoleType.java`
- `src/main/java/com/genc/ctds/auth/model/User.java`
- `src/main/java/com/genc/ctds/auth/repository/UserRepository.java`
- `src/main/java/com/genc/ctds/auth/service/AuthService.java`
- `src/main/java/com/genc/ctds/auth/controller/AuthController.java`
- `src/main/java/com/genc/ctds/controller/HomeController.java`
- `src/main/resources/data.sql`

## Run

```powershell
./mvnw.cmd spring-boot:run
```

Open: `http://localhost:8084`

## Login Flow

1. Open `/home`.
2. Click `Login`.
3. Sign in using the seeded admin credentials from `data.sql`.
4. The app stores `username` and `role` in `HttpSession`.
5. `/dashboard` reads the session role and returns the matching dashboard view.

## Test

```powershell
./mvnw.cmd test
```

