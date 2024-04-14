# redis-session-clustering test

## 목표

- **redis를 사용해서 session-clustering을 구축해서 다중서버에서 접속이 되는지를 확인을 하기.**


- **게시판에서 페이징처리를 하는데 있어서 성능을 향상하는 방법으로 no-offset + 커버링 인덱스 사용**
  - **기존의 엔티티에서 인덱스를 적용 후 no-offset + 커버링 인덱스로 속도 측정해보기.**
  - **1)엔티티에만 인덱스를 적용하고 측정**
  - **2)no-offset 만 할 경우**
  - **3)커버링 인덱스를 적용한 경우**
  - **2)+3)으로 1)과 비교하기.**


- **QueryDsl을 사용해서 no-offset 목록 구현하기**.


- **게시글의 다중서버에서의 동시성을 유지하기 위해서 조회수에 redisson을 적용해서 동시성을 제어해보기.**


- 리액트로 화면 퍼블리싱 해보기( 로그인 / 게시글 목록).

## 기술스택

- Java / Springboot / Gradle / Jpa / MariaDB / Redis-Session / Redis / Redisson


- React

## 기술적용

1. redis-Session을 사용해서 session의 용도로 해서 세션 클러스터링을 구축하기.

**session-clustering ?** 

세션 클러스터링은 사용자의 **세션 데이터를 여러 대의 서버에 분산 저장**하는 기술을 말합니다.

이를 통해 세션 데이터의 안정성과 가용성을 향상시키며, 대규모 트래픽에도 효과적으로 대응할 수 있습니다.


1-1. gradle에 라이브러리를 주입을 한다.
````
implementation 'org.springframework.session:spring-session-data-redis'
````

1-2. Redis에 관련된 설정 클래스를 작성한다.

````
@Configuration
@EnableCaching //캐싱 설정
@EnableRedisHttpSession//redis session
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    //redis 설정
    @Bean
    public RedisConnectionFactory redisConnectionFactory(){
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(port);
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }
    .....
}

````
- @EnableRedisHttpSession : 세션을 redis에서 관리를 하기 위해서 사용되는 어노테이션이다. 

1-3. 스프링 시큐리티를 사용하므로 설정 클래스를 및 관련 필터를 작성을 한다.

````

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] PERMIT_URL_ARRAY = {
            "/api/login","/api/signup","/api/create","/api/logout","/api/check-user","/api/list"
    };

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //인증 url
        http.authorizeHttpRequests(authorize->authorize
                .requestMatchers("/api/board/list","/api/board/create","/api/board/{id}").hasAnyRole("USER","ADMIN")
                .requestMatchers(PERMIT_URL_ARRAY).permitAll()
                .anyRequest().authenticated());
        //form disable
        http.formLogin(AbstractHttpConfigurer::disable);
        //cors & csrf disable
        http
            .cors(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .headers(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .rememberMe(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable);
        //세션을 스프링이 아닌 redis에서 관리를 하므로 세션을 끈다.
        http
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .addFilterBefore(new AuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}


@Log4j2
public class AuthenticationFilter extends OncePerRequestFilter {
    private final static String LOGIN_URL = "/api/login";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("authenticationFilter !!!!!!");
        log.info(request.getRequestURI());


        Member user = (Member) request.getSession().getAttribute("member");


        if(request.getRequestURI().equals(LOGIN_URL)&&Objects.isNull(user)){
            log.info("login !!");
            filterChain.doFilter(request, response);
            return;
        }

        if(!Objects.isNull(user)) {
            GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().toString()); // 사용자 권한
            log.info(authority);
            Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, Collections.singleton(authority)); // 현재 사용자의 인증 정보
            log.info(authentication);
            log.info("filter member data:::"+user);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request,response);
    }
}

````

1-4. 로그인과 관련된 컨트롤러 서비스 단을 작성

````
(entity)

@Entity
@Table(name = "member")
@Getter
@ToString
@NoArgsConstructor
public class Member implements Serializable {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private String password;
    private String userName;
    private String userPhone;
    @Enumerated(EnumType.STRING)
    private Role role;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime updatedTime;

    @Builder
    public Member(Long id,String userId,String password,String userName,String userPhone,
                  Role role,
                  LocalDateTime createdTime,LocalDateTime updatedTime){
        this.id = id;
        this.userId = userId;
        this.password = password;
        this.userName = userName;
        this.userPhone = userPhone;
        this.role = role;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
    }
    
}

-----------------------------------------------------------------------
(service)

    @Transactional
    public String login(HttpSession session, LoginDto loginDto){
        Member member = memberRepository.findByUserId(loginDto.getUserId());
        log.info(member);
        log.info(bCryptPasswordEncoder.matches(loginDto.getPassword(),member.getPassword()));

        //회원객체가 없거나 비밀번호가 일치하지 않는 경우
        if(member==null || !bCryptPasswordEncoder.matches(loginDto.getPassword(),member.getPassword())){
            throw new RuntimeException("비밀이 맞지 않습니다.");
        }
        //아닌 경우에는 세션에 회원객체를 저장한다.
        session.setAttribute("member",member);
        return session.getId();
    }

    public void logout(HttpSession httpSession){
       httpSession.removeAttribute("member");
    }
    
     @PostMapping("/login")
    public ResponseEntity<?>login(@RequestBody LoginDto loginDto, HttpSession session){
        String loginResult = memberService.login(session,loginDto);
        return new ResponseEntity<>(loginResult, HttpStatus.OK);
    }

--------------------------------------------------
(controller)

    @PostMapping("/logout")
    public ResponseEntity<?>logout(HttpSession session){
        memberService.logout(session);
        session.invalidate();
        return new ResponseEntity<>("logout",HttpStatus.OK);
    }

    @GetMapping("/check-user")
    public ResponseEntity<?>memberCheck(HttpSession httpSession){
        String result;
        Member member;
        Object currentUser = httpSession.getAttribute("member");
        log.info(currentUser);
        if(isNull(currentUser)){
            result = "로그인이 되어 있지 않음";
        }else{
            result = ((Member)currentUser).getUserId();
            member = ((Member)currentUser);
            log.info(member);
        }
        return new ResponseEntity<>(result,HttpStatus.OK);
    }
    
    //세션 공유용 테스트 컨트롤러
    @GetMapping("/test")
    public ResponseEntity<String>sessionTest(HttpSession session){
        session.setAttribute("member","sharing??");
        String sessionId = session.getId();
        return new ResponseEntity<>(sessionId,HttpStatus.OK);
    }
    
````

1-5. 세션이 다른 포트에서도 공유가 되는지를 확인을 한다.

결과를 보면 다른 서버에서도 세션아이디를 보면 같다는 것을 알 수 있다.

![스크린샷 2024-04-07 125545](https://github.com/well0924/coffie_placeVol.02/assets/89343159/9411baa3-13f6-4bf2-952b-bc4314c4a450)

![스크린샷 2024-04-07 125608](https://github.com/well0924/coffie_placeVol.02/assets/89343159/95e3638f-0fcc-431d-ba11-963961da35a7)


2. no-offset vs 커버링 인덱스

게시글 테이블에 10만건의 데이터를 기준으로 대용량의 데이터를 페이징을 할 경우 어떤 방식으로 해야 성능을 올릴 수 있는지를
확인해보고자 offset vs no-offset vs 커버링 인덱스로 각각의 성능을 측정하고자 한다.

2-1. offset

offset방식은 흔히 알고 있는 페이징방식이고 쿼리는 다음과 같다.

````
select * from 테이블명 limit 페이지 번호 offset 페이지 사이즈

select * from board limit 10 offset 100000
````
위의 쿼리는 100000개의 row를 읽고 10개만 사용하는 것이다. 10개를 사용하기 위해서 99990개를
버리는 식의 방식은 굉장히 비효율적인 방법이라고 생각을 한다. 그리고 offset의 크기가 커지면 커질수록
속도는 더욱 더 느려진다는 것을 알 수 있다. 

실제로 디비에 쿼리를 날려보면 이러하다. 

![스크린샷 2024-04-07 151535](https://github.com/well0924/rediss-practice/assets/89343159/f0dad848-1eab-40d9-ad55-29b22428d83e)

2-2. no-offset 

no-offset방식은 위의 방식과는 달리 offset을 사용하지 않는 방식이다. 기존의 offset방식과는 다르게 불필요한
데이터를 한번에 가져오지 않아도 되기 때문에 속도가 offset보다 빠를 수 밖에 없다. 

````
select 
  * 
from 
  board b 
where 
  b.board_id < 목록에서의 마지막 번호 
order by 
  board_id desc 
limit 페이지 번호 
````
다만 단점이 있다면 기존의 페이징과는 다르게 이전 페이지 값을 기준으로 하기 때문에 특정 페이지로 이동이 불가능하다.
보통은 목록 더보기 또는 무한스크롤에 자주 사용이 된다. 

![스크린샷 2024-04-07 163900](https://github.com/well0924/coffie_placeVol.02/assets/89343159/cd12f25e-4446-4f0d-a841-ea41081b50fb)

속도를 보면 기존의 offset보다는 속도가 빠르다는것을 알 수가 있다. 

2-3.covering-index

우선 커버링 인덱스는 SELECT / WHERE / GROUP BY / ORDER BY에 사용되는 컬럼이 모두 인덱스일 때 효율적으로 탐색하는 방법을 말한다.


```
select 
  * 
from 
  board b 
join(
  select 
    board_id 
  from 
    board p 
  order by board_id desc 
  limit 10 offset 99993)
on b.board_id = p.board_id;  
```
쿼리는 서브쿼리를 사용해서 board_id를 뽑아낸 뒤 게시번호랑 셀프조인을 하는 방식으로 했다.

![스크린샷 2024-04-07 172854](https://github.com/well0924/rediss-practice/assets/89343159/5e30e503-729e-4e5d-8b53-7858dabcdb4c)

다만 이 방식도 단점은 있다. 바로 인덱스가 많아진다는 점이고 그래서 인덱스가 커진다는 단점이 있고, 데이터양이 많아지고, 페이지 번호가 뒤로 갈수록 
NoOffset에 비해 느려진다.


QueryDsl을 활용해서 무한 스크롤 구현 

```
    @Override
    public List<BoardResponse> boardResponseList(Long size, Long lastBoardId) {
        List<BoardResponse>boardResponseList = jpaQueryFactory
                .select(Projections.constructor(BoardResponse.class,qBoard))
                .from(qBoard)
                .where(ltBoardId(lastBoardId))
                .orderBy(qBoard.id.desc())
                .limit(size)
                .fetch();
        return boardResponseList;
    }
    
    @Transactional
    public List<BoardResponse>nooffSetList(Long size, Long boardId){
        return boardRepository.boardResponseList(size,boardId);
    }
    
    
    @GetMapping("/off-list")
    public ResponseEntity<?>listOffSet(@RequestParam(defaultValue = "5") Long size,@RequestParam(value = "lastId") Long boardId){
        List<BoardResponse>list = boardService.nooffSetList(size,boardId);
        return new ResponseEntity<>(list,HttpStatus.OK);
    }
    
```

3. **Redisson을 활용해서 분산락을 구현**하기.

redis를 활용해서 동시성을 처리를 할 수 있는데 대표적으로는 Lettuce를 사용하는 방법과
redisson을 활용한 방법이 있다. 우선은 이 둘의 차이점을 보도록 하자.

**Lettuce**

lettuce는 공식적으로 분산락 기능을 제공하지 않는다. 따라서 직접 구현해서 사용해야 한다.

Lettuce의 락 획득 방식은 **락을 획득하지 못한 경우 락을 획득**하기 위해 Redis에 계속해서 요청을 보내는 

**스핀락(spin lock)**으로 구성되어 있다. 이 스핀락 방식은 계속해서 요청을 보내는 방식으로 인해 **redis에 부하가 생길 수 있다는 단점**이 있다.

**redisson**

redisson은 락 획득 시 스핀락 방식이 아닌 **pub/sub 방식**을 이용한다.

pub/sub 방식은 락이 해제될때마다 subscribe중인 클라이언트에게 "이제 락 획득을 시도해도 된다."라는 알림을 보내기 때문에, 

클라이언트에서 락 획득을 실패했을 때, redis에 지속적으로 락 획득 요청을 보내는 과정이 사라지고, 이에 따라 부하가 발생하지 않게 된다.

또한 Redisson은 RLock이라는 락을 위한 인터페이스를 제공한다. 이 인터페이스를 이용하여 비교적 손쉽게 락을 사용할 수 있다.

그럼 Redisson을 활용한 동시성 제어에 관련된 코드는 다음과 같다.


Redisson 설정 클래스
````
@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    private static final String REDISSON_HOST_PREFIX = "redis://";

    //redisson 설정
    @Bean
    public RedissonClient redissonClient() {
        RedissonClient redisson = null;
        Config config = new Config();
        config.useSingleServer().setAddress(REDISSON_HOST_PREFIX + host + ":" + port);
        redisson = Redisson.create(config);
        return redisson;
    }
}

````

분산락에 공통적으로 사용될 어노테이션
````
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributeLock {
    //락의 이름
    String key();
    //시간 단위
    TimeUnit timeUnit() default TimeUnit.SECONDS;
    //락을 획득하기 위한 시간
    long waitTime() default 5L;
    //락을 임대하는 시간
    long leaseTime() default 3L;
}
````

어노테이션을 선언한 메소드를 실행을 할때 작동이 되는 aop클래스입니다.

````
@Log4j2
@Aspect
@Component
@RequiredArgsConstructor
public class DistributeLockAop {

    private static final String REDISSON_KEY_PREFIX = "RLOCK_";

    private final RedissonClient redissonClient;

    private final AopForTransaction aopForTransaction;

    @Around("@annotation(com.example.redissessionclusteringindexpractice.config.redis.DistributeLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributeLock distributeLock = method.getAnnotation(DistributeLock.class);

        String key = REDISSON_KEY_PREFIX + CustomSpringELParser
                .getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), distributeLock.key());

        RLock rLock = redissonClient.getLock(key);   

        try {
            boolean available = rLock.tryLock(distributeLock.waitTime(), distributeLock.leaseTime(), distributeLock.timeUnit());
            if (!available) {
                return false;
            }

            log.info("get lock success {}" , key);
            return aopForTransaction.proceed(joinPoint);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            throw new InterruptedException();
        } finally {
            rLock.unlock();
        }
    }
}
````

어노테이션에서 key를 SpringExpression으로 전달하고 이를 파싱하는 util클래스
````
public class CustomSpringELParser {
    public static Object getDynamicValue(String[] parameterNames, Object[] args, String key) {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        return parser.parseExpression(key).getValue(context, Object.class);
    }
}
````

Aop내에서 트랜잭션을 별도로 가져가기 위해 클래스
````
@Component
public class AopForTransaction {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Object proceed(final ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed();
    }
}
````


````
@Component
@RequiredArgsConstructor
public class ReadCountService {

    private final BoardRepository boardRepository;

    @DistributeLock(key = "#lockKey")
    public void readCountUp(String lockKey,Long boardId){
        BoardResponse response = boardRepository.boardDetail(boardId);
        boardRepository.readCountUp(boardId);
    }
}

  @Transactional
    @Cacheable(value = CacheKey.BOARD,key = "#boardId",unless = "#result == null")
    public BoardResponse boardDetail(Long boardId){

        log.info("service!");
        BoardResponse detail = boardRepository.boardDetail(boardId);
        String key = CacheKey.BOARD +":"+ boardId;
        //조회수 증가.
        //readCountService.readCountUp(key,boardId);
        return detail;
    }

````

작성된 코드를 실험하기 위해서 테스트 코드는 다음과 같다.

````
@SpringBootTest
public class RedisTest {

    @Autowired
    private BoardService boardService;

    @Autowired
    private ReadCountService readCountService;

    @Test
    @DisplayName("분산락 테스트")
    public void distributeLockTest()throws Exception{
        //시나리오 :: 게시글 조회시 조회수가 원하는 만큼 증가하는가??

        int numberOfThreads = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        BoardResponse detail = boardService.boardDetail(1L);

        for(int i =0; i< numberOfThreads; i++){
            executorService.submit(()->{
                try{
                    readCountService.readCountUp(CacheKey.BOARD,detail.getId());
                }finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        System.out.println(detail.getReadCount());
    }
}

````

