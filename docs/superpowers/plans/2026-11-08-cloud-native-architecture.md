# Cloud-Native Architecture Optimization Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Remove Dubbo in favor of HTTP+OpenAPI internal APIs, replace `common` with `platform-web`+`contracts`, and consolidate Redis/MinIO while keeping the five-service topology and Spring Cloud Gateway.

**Architecture:** Keep `gateway` / `auth` / `core` / `multimedia` / `has`. Gateway→Auth permission lookups and HAS→Core resource persistence become internal REST under `/api/v1/internal/**`. Shared non-business code lives in `platform/platform-web`; HTTP/MQ schemas live in `contracts/`. Single Redis (key-prefix isolation) and single MinIO (`raw` / `transcoded` buckets).

**Tech Stack:** Java 17, Spring Boot 3.3.13, Spring Cloud Gateway 4.1.5, Spring WebMVC/WebFlux WebClient, MyBatis-Plus 3.5.7, RabbitMQ, MinIO 8.6.0, Sa-Token 1.38.0, JUnit 5, MockWebServer/MockMvc, Maven, Docker Compose.

**Spec:** `docs/superpowers/specs/2026-11-08-cloud-native-architecture-design.md`

## Global Constraints

- Do **not** introduce Nacos, Dubbo, Feign, or gRPC in this plan.
- Do **not** merge the five deployable services or replace Spring Cloud Gateway.
- Do **not** deliver runnable Kubernetes manifests (archive/delete obsolete `k8s/`; Phase 2 only in docs).
- Internal routes must **not** be registered on Gateway public routes.
- MinIO buckets are fixed names: `raw`, `transcoded`.
- Keep MySQL dual schemas (`*_auth` / `*_magic_video`).
- Prefer keeping Java package `cn.xiaolin.utils` inside `platform-web` this round (ADR notes future rename to `cn.xiaolin.platform.web`).
- Prefer TDD for new HTTP boundaries; every task ends with a verifiable check.
- Commit only when the human asks, or at task boundaries if they opted into frequent commits during execution — plan steps still list suggested commit messages.

## File Structure (target)

```
contracts/
  openapi/auth-internal.v1.yaml
  openapi/core-internal.v1.yaml
  asyncapi/transcode.v1.yaml
platform/
  pom.xml                          # packaging pom
  platform-web/                    # ex-common-utils (Result, exceptions, page, Jackson)
auth/
  .../controller/internal/InternalUserController.java
  .../config/MybatisPlusConfig.java  # inlined from common-db
  .../config/MyMetaObjectHandler.java
core/
  .../controller/internal/InternalResourceController.java
  .../service/ResourceAppService.java
  .../domain/dto/ResourceDto.java  # moved from common-api
  .../config/MybatisPlusConfig.java
gateway/
  .../client/AuthPermClient.java
  .../service/StpServiceImpl.java  # uses AuthPermClient
has/
  .../client/CoreResourceClient.java
  .../mq/...                        # consumer stays; shared MQ types local or duplicated thin DTOs
multimedia/
  .../mq/...                        # producer + Rabbit config moved here
```

Deleted after migration: `common/` (entire tree), all `**/dubbo/**`, Dubbo bootstrap keys, second Redis/MinIO compose services, `volume/promtail/`, obsolete `k8s/*.yaml` (or `docs/archive/k8s/`).

---

### Task 1: Introduce `platform-web` and re-point module dependencies

**Files:**
- Create: `platform/pom.xml`
- Create: `platform/platform-web/pom.xml` (copy from `common/common-utils/pom.xml`, change `artifactId` to `platform-web`)
- Move (git mv preferred): all sources/tests under `common/common-utils/` → `platform/platform-web/`
- Modify: root `pom.xml` — replace `<module>common</module>` with `<module>platform</module>`; remove `${dubbo.version}` only after Task 7 (can leave unused until then)
- Modify: `auth/pom.xml`, `core/pom.xml`, `gateway/pom.xml`, `multimedia/pom.xml`, `has/pom.xml` — `common-utils` → `platform-web`
- Modify: `common/pom.xml` — remove `common-utils` module entry (keep api/db/message temporarily)
- Test: `./mvnw -pl platform/platform-web,auth,core,gateway,multimedia,has -am -DskipTests compile`

**Interfaces:**
- Consumes: existing `cn.xiaolin.utils.*` types unchanged
- Produces: Maven coordinate `cn.xiaolin:platform-web:0.0.1-SNAPSHOT`

- [ ] **Step 1: Create platform parent POM**

```xml
<!-- platform/pom.xml -->
<project>
  <modelVersion>4.0.0</modelVersion>
  <parent>
    <groupId>cn.xiaolin</groupId>
    <artifactId>magic-video-backend</artifactId>
    <version>0.0.1-SNAPSHOT</version>
  </parent>
  <artifactId>platform</artifactId>
  <packaging>pom</packaging>
  <modules>
    <module>platform-web</module>
  </modules>
</project>
```

- [ ] **Step 2: Move `common-utils` → `platform/platform-web` and set artifactId `platform-web`**

Keep package `cn.xiaolin.utils`. Remove any Spring Boot application main class if present (`CommonUtilsApplication`) — libraries must not ship a boot entrypoint.

- [ ] **Step 3: Update all service POMs to depend on `platform-web`; update root + common module lists**

- [ ] **Step 4: Compile**

Run: `./mvnw -pl platform/platform-web,auth,core,gateway,multimedia,has -am -DskipTests compile`  
Expected: BUILD SUCCESS

- [ ] **Step 5: Suggested commit**

```bash
git add platform pom.xml common/pom.xml auth/pom.xml core/pom.xml gateway/pom.xml multimedia/pom.xml has/pom.xml
git commit -m "refactor: extract platform-web from common-utils"
```

---

### Task 2: Add OpenAPI / AsyncAPI contracts

**Files:**
- Create: `contracts/openapi/auth-internal.v1.yaml`
- Create: `contracts/openapi/core-internal.v1.yaml`
- Create: `contracts/asyncapi/transcode.v1.yaml`
- Create: `contracts/README.md` (how services consume these files; no codegen required this round)

**Interfaces:**
- Produces: path contracts that Tasks 3–6 must implement exactly

- [ ] **Step 1: Write auth-internal OpenAPI**

```yaml
openapi: 3.0.3
info:
  title: auth-internal
  version: 1.0.0
paths:
  /api/v1/internal/users/{id}/permissions:
    get:
      parameters:
        - in: path
          name: id
          required: true
          schema: { type: integer, format: int64 }
      responses:
        "200":
          description: OK
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/ResultListString"
  /api/v1/internal/users/{id}/roles:
    get:
      parameters:
        - in: path
          name: id
          required: true
          schema: { type: integer, format: int64 }
      responses:
        "200":
          description: OK
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/ResultListString"
components:
  schemas:
    ResultListString:
      type: object
      required: [code, msg]
      properties:
        code: { type: integer }
        msg: { type: string }
        data:
          type: array
          items: { type: string }
```

- [ ] **Step 2: Write core-internal OpenAPI**

```yaml
openapi: 3.0.3
info:
  title: core-internal
  version: 1.0.0
paths:
  /api/v1/internal/resources:
    post:
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/ResourceDto"
      responses:
        "200":
          description: OK
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/ResultVoid"
  /api/v1/internal/resources/{id}:
    get:
      parameters:
        - in: path
          name: id
          required: true
          schema: { type: integer, format: int64 }
      responses:
        "200":
          description: OK
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/ResultResource"
components:
  schemas:
    ResourceDto:
      type: object
      properties:
        id: { type: integer, format: int64 }
        md5: { type: string }
        mp4: { type: string }
        m3u8: { type: string }
        mpd: { type: string }
    ResultVoid:
      type: object
      properties:
        code: { type: integer }
        msg: { type: string }
        data: { nullable: true }
    ResultResource:
      type: object
      properties:
        code: { type: integer }
        msg: { type: string }
        data: { $ref: "#/components/schemas/ResourceDto" }
```

- [ ] **Step 3: Write AsyncAPI sketch for transcode message**

Document payload fields matching current `ResourceMessage` / `TranscodeMessage`: `id`, `md5`, `mp4`, `m3u8`, `mpd`, plus exchange/queue names from `MessageQueueConsts`.

- [ ] **Step 4: Suggested commit**

```bash
git add contracts
git commit -m "docs: add OpenAPI/AsyncAPI contracts for internal APIs"
```

---

### Task 3: Auth internal HTTP API (replace Dubbo provider surface)

**Files:**
- Create: `auth/src/main/java/cn/xiaolin/auth/controller/internal/InternalUserController.java`
- Create: `auth/src/test/java/cn/xiaolin/auth/controller/internal/InternalUserControllerTest.java`
- Modify: keep `DubboUserPermService` temporarily calling same service methods OR extract shared private service — prefer extract:

```java
// auth/.../service/UserPermQueryService.java
public interface UserPermQueryService {
    List<String> listPermissionNames(Long userId);
    List<String> listRoleNames(Long userId);
}
```

- Modify: `DubboUserPermService` to delegate to `UserPermQueryService` until Task 7 deletes it
- Test: MockMvc slice or `@WebMvcTest`

**Interfaces:**
- Consumes: `SysPermissionService.listPermsByUserId`, `SysRoleService.listRolesByUserId`
- Produces:
  - `GET /api/v1/internal/users/{id}/permissions` → `Result<List<String>>`
  - `GET /api/v1/internal/users/{id}/roles` → `Result<List<String>>`

- [ ] **Step 1: Write failing MockMvc test**

```java
@WebMvcTest(InternalUserController.class)
class InternalUserControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean UserPermQueryService userPermQueryService;

    @Test
    void permissions_returnsNames() throws Exception {
        when(userPermQueryService.listPermissionNames(1L))
            .thenReturn(List.of("video:read"));
        mockMvc.perform(get("/api/v1/internal/users/1/permissions"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data[0]").value("video:read"));
    }
}
```

- [ ] **Step 2: Run test — expect FAIL** (controller missing)

Run: `./mvnw -pl auth -Dtest=InternalUserControllerTest test`  
Expected: compilation failure or missing bean/controller

- [ ] **Step 3: Implement `UserPermQueryService` + `InternalUserController`**

```java
@RestController
@RequestMapping("/api/v1/internal/users")
@RequiredArgsConstructor
public class InternalUserController {
    private final UserPermQueryService userPermQueryService;

    @GetMapping("/{id}/permissions")
    public Result<List<String>> permissions(@PathVariable("id") Long id) {
        return Result.ok(userPermQueryService.listPermissionNames(id));
    }

    @GetMapping("/{id}/roles")
    public Result<List<String>> roles(@PathVariable("id") Long id) {
        return Result.ok(userPermQueryService.listRoleNames(id));
    }
}
```

Implementation of query service must mirror current `DubboUserPermService` mapping (`SysPermission::getName`, `SysRole::getName`).

- [ ] **Step 4: Re-run test — expect PASS**

- [ ] **Step 5: Suggested commit**

```bash
git commit -m "feat(auth): add internal HTTP user perm/role API"
```

---

### Task 4: Gateway AuthPerm HTTP client (drop Dubbo consumer)

**Files:**
- Create: `gateway/src/main/java/cn/xiaolin/gateway/client/AuthPermClient.java`
- Create: `gateway/src/main/java/cn/xiaolin/gateway/config/AuthClientProperties.java` (`auth.base-url`)
- Create: `gateway/src/test/java/cn/xiaolin/gateway/client/AuthPermClientTest.java`
- Modify: `gateway/.../service/StpServiceImpl.java` — inject `AuthPermClient` instead of `DubboUserPermClient`
- Modify: `gateway/src/main/resources/bootstrap-*.yaml` — add `auth.base-url` (local: `http://magic-auth:8999`); remove `dubbo.*`
- Modify: `gateway/pom.xml` — remove `common-api`; remove `spring-cloud-starter-loadbalancer`; add nothing Dubbo-related; ensure WebClient available via gateway stack
- Delete (end of task or Task 7): `gateway/.../dubbo/consumer/DubboUserPermClient.java`
- Optional: Caffeine cache wrapper around `AuthPermClient` with TTL ~30s

**Interfaces:**
- Consumes: auth internal endpoints from Task 3
- Produces:

```java
public interface AuthPermClient {
    List<String> getPermissions(Long userId);
    List<String> getRoles(Long userId);
}
```

On auth 5xx/timeout: throw a dedicated exception that Gateway maps to HTTP 503 (do **not** return empty permission list).

- [ ] **Step 1: Failing unit test with MockWebServer**

```java
@Test
void getRoles_parsesResultData() throws Exception {
    mockWebServer.enqueue(new MockResponse()
        .setBody("{\"code\":200,\"msg\":\"success\",\"data\":[\"common\"]}")
        .addHeader("Content-Type", "application/json"));
    AuthPermClient client = new AuthPermClient(WebClient.builder(),
        mockWebServer.url("/").url().toString().replaceAll("/$", ""));
    assertEquals(List.of("common"), client.getRoles(9L));
}
```

Use the project's actual success `code` from `ResponseCode.SUCCESS` (verify in `platform-web` enums before asserting).

- [ ] **Step 2: Implement WebClient client + wire `StpServiceImpl`**

```java
public List<String> getRoles(Long userId) {
    Result<List<String>> result = webClient.get()
        .uri("/api/v1/internal/users/{id}/roles", userId)
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<Result<List<String>>>() {})
        .block(Duration.ofSeconds(3));
    if (result == null || result.getData() == null) {
        throw new IllegalStateException("auth roles response empty");
    }
    return result.getData();
}
```

- [ ] **Step 3: Run `AuthPermClientTest` — PASS; compile gateway module**

- [ ] **Step 4: Suggested commit**

```bash
git commit -m "feat(gateway): fetch roles/permissions over HTTP instead of Dubbo"
```

---

### Task 5: Core internal resource HTTP API

**Files:**
- Create: `core/src/main/java/cn/xiaolin/core/domain/dto/ResourceDto.java` (copy fields from `common-api` DTO)
- Create: `core/src/main/java/cn/xiaolin/core/service/ResourceAppService.java` (+ impl moving logic from `DubboResourceService`)
- Create: `core/src/main/java/cn/xiaolin/core/controller/internal/InternalResourceController.java`
- Create: `core/src/test/java/cn/xiaolin/core/service/ResourceAppServiceTest.java` (Mockito unit test — no SpringBootTest/MySQL required)
- Modify: `DubboResourceService` temporarily delegates to `ResourceAppService` until deletion
- Delete or gut: `core/.../controller/ResourceController.java` NotImplemented endpoints if they duplicate internal API (prefer delete empty stubs)

**Interfaces:**
- Produces:
  - `POST /api/v1/internal/resources` body `ResourceDto` → `Result.ok()`
  - `GET /api/v1/internal/resources/{id}` → `Result<ResourceDto>`
- Preserve semantics from current `DubboResourceService`: `id` = `video_id`; write up to three `video_source` rows (`mp4`/`hls`/`dash`); idempotent on `(videoId,type,src)`

- [ ] **Step 1: Write failing unit test for idempotent save**

```java
@Test
void saveResource_writesThreeTypes_idempotent() {
    when(videoSourceService.exists(any())).thenReturn(false);
    service.saveResource(ResourceDto.builder().id(1L).mp4("a").m3u8("b").mpd("c").build());
    verify(videoSourceService, times(3)).save(any());
    when(videoSourceService.exists(any())).thenReturn(true);
    service.saveResource(ResourceDto.builder().id(1L).mp4("a").m3u8("b").mpd("c").build());
    verify(videoSourceService, times(3)).save(any()); // still 3 total
}
```

- [ ] **Step 2: Implement `ResourceAppService` by moving code from `DubboResourceService`**

- [ ] **Step 3: Add `InternalResourceController`**

```java
@RestController
@RequestMapping("/api/v1/internal/resources")
@RequiredArgsConstructor
public class InternalResourceController {
    private final ResourceAppService resourceAppService;

    @PostMapping
    public Result<Void> save(@RequestBody ResourceDto body) {
        resourceAppService.saveResource(body);
        return Result.ok();
    }

    @GetMapping("/{id}")
    public Result<ResourceDto> get(@PathVariable Long id) {
        return Result.ok(resourceAppService.getResourceById(id));
    }
}
```

- [ ] **Step 4: Run tests — PASS**

- [ ] **Step 5: Suggested commit**

```bash
git commit -m "feat(core): expose internal HTTP resource save/get API"
```

---

### Task 6: HAS CoreResource HTTP client (drop Dubbo consumer)

**Files:**
- Create: `has/src/main/java/cn/xiaolin/has/client/CoreResourceClient.java`
- Create: `has/src/main/java/cn/xiaolin/has/config/CoreClientProperties.java` (`core.base-url`)
- Create: `has/src/test/java/cn/xiaolin/has/client/CoreResourceClientTest.java`
- Modify: `has/.../mq/consumer/MessageConsumerImpl.java` — call `CoreResourceClient.saveResource`
- Modify: `has/src/main/resources/bootstrap-*.yaml` — `core.base-url: http://magic-core:9001`; remove dubbo keys
- Modify: `has/pom.xml` — remove `common-api`; add `spring-boot-starter-webflux` **or** `RestClient` via `spring-boot-starter-web` already present — prefer `RestClient` if servlet stack exists, else WebClient
- Delete: `has/.../dubbo/comsumer/DubboResourceClient.java`

**Interfaces:**
- Consumes: core internal POST/GET from Task 5
- Produces: `void saveResource(ResourceDto dto)` throwing on non-2xx / non-success `code`

On failure: let exception propagate so existing MQ ack/DLQ behavior does **not** silently ack success (align with ADR-0007 / current consumer).

- [ ] **Step 1: Failing MockWebServer test for POST**

- [ ] **Step 2: Implement client; switch `MessageConsumerImpl`**

```java
ResourceDto resourceDto = ResourceDto.builder()
    .id(...)
    .md5(...)
    .mp4(...)
    .m3u8(...)
    .mpd(...)
    .build();
coreResourceClient.saveResource(resourceDto);
```

Use `cn.xiaolin.core.domain.dto.ResourceDto` **only if** has depends on core module — **forbidden**. Instead place a thin DTO in `has/.../dto/ResourceDto.java` **or** under `platform-web` only if it stays transport-agnostic. Spec choice: **duplicate thin DTO in has** mirroring OpenAPI (avoid has→core Maven dependency).

- [ ] **Step 3: Tests PASS; `./mvnw -pl has -am -DskipTests compile`**

- [ ] **Step 4: Suggested commit**

```bash
git commit -m "feat(has): persist transcode results via HTTP to core"
```

---

### Task 7: Purge Dubbo and `common-api`

**Files:**
- Delete: all files under `**/dubbo/**`
- Delete: `common/common-api/` module
- Delete: `core/src/test/java/cn/xiaolin/core/dubbo/**`
- Modify: root `pom.xml` — remove `dubbo.version` and `dubbo-spring-boot-starter` dependencyManagement
- Modify: `auth/pom.xml`, `core/pom.xml`, `gateway/pom.xml`, `has/pom.xml` — remove Dubbo starters and `common-api`
- Modify: every `bootstrap-*.yaml` / `application*.yaml` — remove `dubbo:` blocks and ports 20880–20883 docs references in README later
- Modify: `docs/02-architecture.md` call matrix (can wait for Task 12 if preferred, but code must compile now)

**Interfaces:**
- Produces: zero Dubbo usage

- [ ] **Step 1: Remove code + dependencies**

- [ ] **Step 2: Verify**

Run: `./mvnw -DskipTests compile`  
Run: `rg -i 'dubbo|common-api' --glob '!docs/**' --glob '!.git/**'`  
Expected: no matches in `pom.xml` / `*.java` / `*.yaml` (docs may still mention until Task 12)

- [ ] **Step 3: Suggested commit**

```bash
git commit -m "chore: remove Dubbo and common-api"
```

---

### Task 8: Dissolve `common-message` and `common-db`; delete `common/`

**Files:**
- Move Rabbit producer + `RabbitMQConfig` + constants into `multimedia/src/main/java/cn/xiaolin/multimedia/mq/`
- Move consumer interfaces already in has; ensure message entity classes exist in both sides as needed (prefer single copy in multimedia + has each holding the JSON-compatible DTO, schema documented in `contracts/asyncapi/transcode.v1.yaml`)
- Move existing tests `common-message/src/test/...` → `multimedia` or `has` as appropriate (`DeadLetterRoutingTest`, `TranscodeMessageContractTest`)
- Copy `MybatisPlusConfig` + `MyMetaObjectHandler` into `auth` and `core` config packages; update `@MapperScan` / component scan if needed
- Delete: `common/` directory; remove module from root POM
- Modify: `core/pom.xml` — remove `common-message` dependency (core should not need Rabbit after cleanup)
- Modify: core bootstrap — remove unused `spring.rabbitmq` if present

**Interfaces:**
- Produces: multimedia publishes; has consumes; same JSON field names as today

- [ ] **Step 1: Move MQ code; fix imports; run message contract tests**

Run: `./mvnw -pl multimedia,has -am test -Dtest=TranscodeMessageContractTest,DeadLetterRoutingTest`  
Expected: PASS (adjust test names if moved)

- [ ] **Step 2: Inline DB config into auth/core; delete `common/`**

- [ ] **Step 3: Full compile**

Run: `./mvnw -DskipTests compile`  
Expected: BUILD SUCCESS

- [ ] **Step 4: Suggested commit**

```bash
git commit -m "refactor: dissolve common-message/common-db; remove common module"
```

---

### Task 9: Consolidate Redis to one instance

**Files:**
- Modify: `docker-compose-local.yaml`, `docker-compose-dev.yaml`, `mini-local.yaml` — remove `magic-auth-redis` **or** remove `magic-redis` and keep one hostname `magic-redis`
- Modify: gateway + auth bootstrap — point Sa-Token Redis to `magic-redis:6379`
- Modify: core bootstrap — same host, use key prefix `core:` for cache (Spring Redis `spring.data.redis` + existing cache config)
- Modify: `.env.example`, `.local.env`, `.dev.env` if they reference two Redis hosts
- Update service `depends_on` accordingly

**Decision locked:** keep container name/hostname `magic-redis` on port 6379; delete `magic-auth-redis`.

- [ ] **Step 1: Edit compose files + bootstraps**

- [ ] **Step 2: Config grep**

Run: `rg 'magic-auth-redis|6389' docker-compose*.yaml mini-local.yaml **/bootstrap-*.yaml`  
Expected: no matches (except changelog/docs if any)

- [ ] **Step 3: Suggested commit**

```bash
git commit -m "infra: use a single Redis for Sa-Token and core cache"
```

---

### Task 10: Consolidate MinIO to one instance (`raw` / `transcoded`)

**Files:**
- Modify: compose files — single `magic-minio` service; publish one API port (e.g. host `9010:9000`) and console if needed
- Modify: `multimedia` MinIO config — endpoint `http://magic-minio:9000`; video/image buckets → use `raw` (and image subprefix or same bucket with key prefixes per ADR-0005)
- Modify: `has` MinIO config — same endpoint; read from `raw`, write HLS/DASH to `transcoded`
- Modify: `has/.../config/AppConfig.java` bucket init — ensure both buckets exist + policies
- Modify: any hardcoded `magic-multimedia-minio` / `magic-has-minio` hostnames
- Update README port table

**Bucket names (fixed):** `raw`, `transcoded`.

- [ ] **Step 1: Compose + application config updates**

- [ ] **Step 2: Grep old hostnames**

Run: `rg 'magic-multimedia-minio|magic-has-minio|magic-hls|9020|9021' --glob '!docs/archive/**'`  
Expected: cleaned in runtime configs (docs history OK)

- [ ] **Step 3: Suggested commit**

```bash
git commit -m "infra: consolidate MinIO to one instance with raw/transcoded buckets"
```

---

### Task 11: Align `mini-local`, CI, and delete dead weight

**Files:**
- Modify: `mini-local.yaml` — add `multimedia`, `has`, `magic-minio` with health/`depends_on` consistent with local compose (fix P1-13)
- Modify: `.github/workflows/prod.yml` — build/push images for `gateway`, `auth`, `core`, `multimedia`, `has` (all five)
- Delete or move: `k8s/*.yaml` → `docs/archive/k8s/` with README “obsolete”
- Delete: `volume/promtail/`
- Delete: Gateway Sa-Token rules for `/has/**` if still present without route
- Delete: remaining `NotImplemented` controller methods listed in tech-debt P0-7 that are not being implemented
- Unify image naming to one prefix already used in compose (`xiaolinstar/*` unless README clearly standardizes otherwise — pick compose as source of truth)
- Modify: `static/mysql/{dev,local,prod}/` — align `CREATE TABLE` sets (archive obsolete tables under `static/mysql/archive/` with a short README)

- [ ] **Step 1: Expand CI matrix to five modules**

- [ ] **Step 2: Fix mini-local media path**

- [ ] **Step 3: Align DDL across dev/local/prod (same table set; archive extras)**

- [ ] **Step 4: Remove dead code/files**

- [ ] **Step 5: Suggested commit**

```bash
git commit -m "chore: align CI and mini-local; remove obsolete k8s/promtail/dead APIs"
```

---

### Task 12: Documentation + ADRs + smoke checklist

**Files:**
- Modify: `docs/02-architecture.md` — new call graph (HTTP internal, no Dubbo)
- Modify: `docs/09-tech-debt.md` — close items completed (P1-13, Dubbo-related, dual MinIO/Redis if listed)
- Create: `docs/adr/0009-remove-dubbo-http-openapi.md`
- Create: `docs/adr/0010-platform-and-contracts.md`
- Create: `docs/adr/0011-single-minio-redis.md`
- Modify: root `README.md` — ports, architecture blurb, no Nacos/Dubbo
- Modify: design spec status line to `已批准 / 实现中` or `已完成` when Done
- Create: `docs/superpowers/plans/smoke-checklist-cloud-native.md` with manual steps:

```text
1. docker compose -f docker-compose-local.yaml up -d --build
2. Register + login via gateway :9000
3. Upload video via /multimedia/**
4. Wait for has consumer logs (ffmpeg + HTTP save)
5. Verify video_source rows in MySQL
6. rg -i dubbo (code) → empty
```

- [ ] **Step 1: Write ADRs 0009–0011**

- [ ] **Step 2: Update architecture + README + tech-debt**

- [ ] **Step 3: Run smoke checklist (or document blockers)**

- [ ] **Step 4: Suggested commit**

```bash
git commit -m "docs: record HTTP/OpenAPI migration and infra consolidation ADRs"
```

---

## Self-Review (plan vs spec)

| Spec requirement | Task |
|---|---|
| Remove Dubbo; HTTP+OpenAPI | 2, 3, 4, 5, 6, 7 |
| Keep Gateway + 5 services | Global + Tasks 3–6 |
| `platform-web` + `contracts` | 1, 2, 8 |
| Inline `common-db`; split message | 8 |
| Single MinIO / single Redis | 9, 10 |
| Fix mini-local + CI five images | 11 |
| Delete dead k8s/promtail/NotImplemented | 11 |
| Error handling (no empty perms; HAS failure→MQ) | 4, 6 |
| Phase 2 K8s/Mesh docs only | 12 ADRs + architecture “非目标” |
| DDL unify | **Gap:** add Step under Task 11 or 12 — compare `static/mysql/{dev,local,prod}` and make table sets match |

### Follow-up patch to plan coverage

- [ ] **Task 11 extra step: DDL alignment** — diff `static/mysql/dev/DDL_video.sql` vs local/prod; archive unused tables or add missing ones so all three environments share the same table set (no Flyway yet).

---

## Execution Handoff

Plan saved to `docs/superpowers/plans/2026-11-08-cloud-native-architecture.md`.

**Two execution options:**

1. **Subagent-Driven (recommended)** — fresh subagent per task, review between tasks  
2. **Inline Execution** — execute tasks in this session with checkpoints  

Which approach? (Still no git commit until you ask.)
