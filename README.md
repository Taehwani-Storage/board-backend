# Spring 학습 가이드 🚀

## 1. Spring 기본 문법 🏗️
Spring을 활용하여 게시판을 구현하는 데 필요한 개념을 학습합니다.

### 1.1. Spring Boot 프로젝트 설정 ⚙️
Spring Boot를 사용하여 빠르게 개발 환경을 구축할 수 있습니다.
```bash
spring init --dependencies=web,data-jpa,h2,lombok spring-board
```

### 1.2. 컨트롤러, 서비스, 리포지토리 패턴 🏛️
Spring에서는 MVC 패턴을 기반으로 애플리케이션을 구성합니다.

#### 1.2.1. 컨트롤러 (Controller)
```java
@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    
    public PostController(PostService postService) {
        this.postService = postService;
    }
    
    @GetMapping
    public List<Post> getPosts() {
        return postService.getAllPosts();
    }
}
```

#### 1.2.2. 서비스 (Service)
```java
@Service
public class PostService {
    private final PostRepository postRepository;
    
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }
    
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }
}
```

#### 1.2.3. 리포지토리 (Repository)
```java
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {}
```

## 2. 게시글 목록 보기 📃
Spring을 이용하여 게시글 목록을 가져오는 API를 구현합니다.
```java
@GetMapping("/posts")
public List<Post> getPosts() {
    return postService.getAllPosts();
}
```

## 3. 게시글 추가, 수정, 삭제, 댓글 기능 ✍️
### 3.1. 게시글 추가
```java
@PostMapping("/posts")
public Post createPost(@RequestBody Post post) {
    return postRepository.save(post);
}
```

### 3.2. 게시글 수정
```java
@PutMapping("/posts/{id}")
public Post updatePost(@PathVariable Long id, @RequestBody Post updatedPost) {
    return postService.updatePost(id, updatedPost);
}
```

### 3.3. 게시글 삭제
```java
@DeleteMapping("/posts/{id}")
public void deletePost(@PathVariable Long id) {
    postService.deletePost(id);
}
```

### 3.4. 댓글 기능
```java
@PostMapping("/posts/{postId}/comments")
public Comment addComment(@PathVariable Long postId, @RequestBody Comment comment) {
    return commentService.addComment(postId, comment);
}
```

## 4. RESTful API를 활용한 게시판 기능 🌐
RESTful API를 통해 클라이언트와 서버가 효율적으로 통신할 수 있도록 합니다.

### 4.1. API 설계 예시
| 기능       | HTTP 메서드 | 엔드포인트        | 요청 데이터 | 응답 데이터 |
|------------|------------|-------------------|-------------|-------------|
| 게시글 목록 | GET        | `/posts`          | 없음        | 게시글 목록 |
| 게시글 작성 | POST       | `/posts`          | 게시글 정보 | 생성된 게시글 |
| 게시글 수정 | PUT        | `/posts/{id}`     | 수정할 데이터 | 수정된 게시글 |
| 게시글 삭제 | DELETE     | `/posts/{id}`     | 없음        | 없음       |
| 댓글 추가   | POST       | `/posts/{id}/comments` | 댓글 정보 | 생성된 댓글 |

## 5. 결론 🎯
이 가이드를 통해 Spring을 활용한 게시판을 구축하는 방법을 익힐 수 있습니다. 데이터베이스 연동, 보안, 성능 최적화 등의 추가 학습을 진행하면 더욱 완성도 높은 애플리케이션을 만들 수 있습니다.
