package com.study.etc;

import com.study.etc.dept.DeptMapper;
import com.study.etc.dept.DeptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringBootTest
class DeptTest {
    private static final Logger log = LoggerFactory.getLogger(DeptTest.class);

    @Autowired
    private DeptService deptService;

    @Test
    void testFindDnameByDeptNo() {

        List<CompletableFuture<String>> futures = IntStream.range(1, 5)
                .mapToObj(idx -> CompletableFuture.supplyAsync(() -> deptService.findDnameByDeptNo(10)))
                .collect(Collectors.toList());

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        CompletableFuture<List<String>> allResults = allFutures.thenApply(future -> futures.stream().map(CompletableFuture::join).collect(Collectors.toList()));

        allResults.join().forEach(result -> log.info("Result :: {}", result));
    }
}

