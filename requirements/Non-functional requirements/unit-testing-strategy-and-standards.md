# Unit Testing Strategy and Standards

## Strategy

We use layered automated testing (Test Pyramid): most resources go to fast, isolated unit tests (Mockito, `@WebMvcTest`), and a small part goes to end-to-end tests (`@SpringBootTest`). This gives better resource efficiency.

## Scope

| Layer | Test | Tool |
|-------|------|------|
| Controller | HTTP req/resp, status, validation | `@WebMvcTest` + MockMvc |
| Service | Business logic, edge cases | Mockito |
| Mapper | SQL, CRUD | `@MybatisTest` + H2 |

## Naming

- Class: `{ClassName}Test.java`
- Method: `should_{expected}_when_{condition}`

## Structure (AAA)

```java
@Test
void should_returnPost_when_idExists() {
    // Arrange
    when(mapper.findById(1)).thenReturn(post);
    // Act
    Post result = service.getPost(1);
    // Assert
    assertThat(result).isNotNull();
}
```

## Rules

**Must:**
- One test, one behavior
- Tests are independent (no shared state)
- Mock external dependencies
- Use AssertJ (`assertThat`) for assertions
- Cover positive, negative, and boundary cases

**Must Not:**
- No `Thread.sleep()`
- No catch-exception-to-pass (use `assertThrows`)
- No test logic in production code

## Coverage Target

| Layer | Target |
|-------|--------|
| Service | >= 80% |
| Overall | >= 70% |

## Run

```bash
./mvnw test
./mvnw test -Dtest=MessageServiceTest
```

## Definition of Done

1. All tests pass
2. Positive/negative/boundary covered
3. Naming and structure follow this document