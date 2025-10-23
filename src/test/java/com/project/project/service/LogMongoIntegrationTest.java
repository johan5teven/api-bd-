package com.project.project.service;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.project.project.model.LogEventoMongo;
import com.project.project.repository.LogMongoRepository;

@SpringBootTest
public class LogMongoIntegrationTest {

    @Autowired
    private com.project.project.service.LogMongoService logService;

    @Autowired
    private LogMongoRepository logMongoRepository;

    @Test
    public void whenSaveLog_thenRepositoryContainsIt() {
        long before = logMongoRepository.count();

    logService.registrarEvento("test_table", "create", "descripcion test");

        long after = logMongoRepository.count();
        assertThat(after).isGreaterThanOrEqualTo(before + 1);

    // Optionally fetch last inserted and assert fields
    java.util.List<LogEventoMongo> all = logMongoRepository.findAll();
    LogEventoMongo last = all.isEmpty() ? null : all.get(all.size() - 1);
    assertThat(last).isNotNull();
    LogEventoMongo nonNullLast = last;
    assertThat(nonNullLast.getTabla()).isEqualTo("test_table");
    }
}
