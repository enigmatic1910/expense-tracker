package com.project.expensetracker.service.ai.parseTask;

import com.project.expensetracker.entity.AiParsingTask;
import com.project.expensetracker.enums.Status;
import com.project.expensetracker.repo.AiParsingRepo;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.spi.Limit;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParseTaskServiceImpl implements ParseTaskService {

    private final AiParsingRepo aiParsingRepo;

    @Override
    public AiParsingTask save(AiParsingTask parsingTask) {

        return aiParsingRepo.save(parsingTask);
    }

    @Override
    public List<AiParsingTask> getPendingTasks(Status status) {
        final var limit = new Limit();
        limit.setMaxRows(13);
        return aiParsingRepo.findAllByStatusOrderByCreatedAtAsc(status, limit);
    }
}
