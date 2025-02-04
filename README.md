# Spring 게시판 미니 프로젝트 (백엔드) 🚀

## 프로젝트 개요
Spring Boot와 MyBatis를 활용하여 **게시판**을 구현하며,
**Spring Security**와 **JWT**를 적용하여 인증 및 인가 기능을 추가한 프로젝트
---

## 📂 프로젝트 구조
```
src 
├── main
    │ ├── java/com/bit/boardbackend
    │ │ ├── controller
    │ │ │ ├── BoardController.java
    │ │ │ ├── ReplyController.java
    │ │ │ ├── UserController.java
    │ │ │ 
    │ │ ├── model
    │ │ │ ├── BoardDTO.java
    │ │ │ ├── ReplyDTO.java
    │ │ │ ├── User.java
    │ │ │ ├── UserDTO.java
    │ │ │ 
    │ │ ├── repository
    │ │ │ ├── UserRepository.java 
    │ │ │ 
    │ │ ├── security
    │ │ │ ├── AuthConfig.java        # OAuth2 설정
    │ │ │ ├── CorsConfig.java        # CORS 정책 설정
    │ │ │ ├── JwtAuthFilter.java     # JWT 검증 필터
    │ │ │ ├── SecurityConfig.java    # Security 전체 설정
    │ │ │ 
    │ │ ├── service
    │ │ │ ├── BoardService.java
    │ │ │ ├── ReplyService.java
    │ │ │ ├── UserService.java
    │ │ │ ├── UserDetailServiceImpl.java # 사용자 인증 서비스
    │ │ │ 
    │ │ ├── util
    │ │ │ ├── JwtUtil.java           # JWT 생성/검증 유틸리티
    │ │ │ 
    │ │ ├── resources
    │ │ │ ├── mybatis
    │ │ │ │ ├── mappers
    │ │ │ │ │ ├── BoardMapper.xml
    │ │ │ │ │ ├── ReplyMapper.xml
    │ │ │ │ │ ├── UserMapper.xml
    │ │ │ ├── application.properties
```

## 🔒 Spring Security + JWT 인증 흐름
1. **로그인 요청** → `UserController.login()`
2. **인증 성공 시** → `JwtUtil.generateToken()`으로 JWT 생성
3. **응답 헤더에 JWT 추가** (`Authorization: Bearer <token>`)
4. **클라이언트는 이후 요청마다 JWT 전송**
5. **JwtAuthFilter에서 토큰 검증** → `JwtUtil.validateToken()`
6. **SecurityContext에 인증 정보 저장**

---

## 🛠️ 주요 Security 컴포넌트 설명

### 1. SecurityConfig.java
```java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
                .antMatchers("/api/auth/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/posts/**").permitAll()
                .anyRequest().authenticated()
            .and()
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```

### 2. JwtUtil.java (토큰 관리)
```java
@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secretKey;
    
    // 토큰 생성
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1시간
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            throw new JwtException("유효하지 않은 토큰");
        }
    }
}
```

### 3. JwtAuthFilter.java (요청 필터링)
```java
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String token = resolveToken(request);
        
        if (token != null && jwtUtil.validateToken(token)) {
            String username = jwtUtil.extractUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

---

## 📝 UserController.java (로그인 처리)
```java
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO.LoginRequest dto) {
        User user = userService.authenticateUser(dto.getUsername(), dto.getPassword());
        
        String token = jwtUtil.generateToken(user.getUsername());
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(Map.of("message", "로그인 성공"));
    }
}
```

---

## ⚙️ application.properties 설정
```properties
# JWT 설정
jwt.secret=mySecretKey123!@#  # 실제 운영환경에서는 환경변수 사용 권장
jwt.expiration=3600000        # 1시간(ms)

# DB 설정
spring.datasource.url=jdbc:h2:mem:boarddb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# MyBatis
mybatis.mapper-locations=classpath:mybatis/mappers/*.xml
mybatis.type-aliases-package=com.bit.boardbackend.model
```

---

## 🔄 프론트엔드 연동 가이드
1. **로그인 성공 시 응답 헤더에서 JWT 추출**
```javascript
axios.post('/api/auth/login', {username, password})
  .then(res => {
    const token = res.headers['authorization'].split(' ')[1];
    localStorage.setItem('jwtToken', token);
  })
```

2. **API 요청 시 헤더에 JWT 추가**
```javascript
axios.interceptors.request.use(config => {
  const token = localStorage.getItem('jwtToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
```

3. **토큰 만료 시 자동 로그아웃 처리**
```javascript
axios.interceptors.response.use(
  response => response,
  error => {
    if (error.response.status === 401) {
      localStorage.removeItem('jwtToken');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);
```

## 🌐 RESTful API를 활용한 게시판 기능
RESTful API를 통해 클라이언트와 서버가 효율적으로 통신할 수 있도록 합니다.

### API 설계
| 기능       | HTTP 메서드 | 엔드포인트        | 요청 데이터 | 응답 데이터 |
|------------|------------|-------------------|-------------|-------------|
| 게시글 목록 | GET        | `/posts`          | 없음        | 게시글 목록 |
| 게시글 작성 | POST       | `/posts`          | 게시글 정보 | 생성된 게시글 |
| 게시글 수정 | PUT        | `/posts/{id}`     | 수정할 데이터 | 수정된 게시글 |
| 게시글 삭제 | DELETE     | `/posts/{id}`     | 없음        | 없음       |
| 댓글 추가   | POST       | `/posts/{id}/comments` | 댓글 정보 | 생성된 댓글 |


