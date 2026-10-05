package com.construction.finance;
//developed by Sehansa Pahanmi (IT25103433)

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByProjectId(Long projectId);
    List<Expense> findByCategory(String category);
}
