package com.bookstore;

import com.bookstore.service.BookstoreService;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import static com.vladmihalcea.sql.SQLStatementCountValidator.assertDeleteCount;
import static com.vladmihalcea.sql.SQLStatementCountValidator.assertInsertCount;
import static com.vladmihalcea.sql.SQLStatementCountValidator.assertSelectCount;
import static com.vladmihalcea.sql.SQLStatementCountValidator.assertUpdateCount;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MainApplication {

    private final BookstoreService bookstoreService;

    public MainApplication(BookstoreService bookstoreService) {
        this.bookstoreService = bookstoreService;
    }

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

    @Bean
    public ApplicationRunner init() {
        return args -> {

            SQLStatementCountValidator.reset();
            bookstoreService.authorOperationsWithoutTransactional();
            // at this point there is no transaction running
            // a total of 5 statements, not very good
            assertInsertCount(1);
            assertUpdateCount(1);
            assertDeleteCount(1);
            assertSelectCount(2);

            SQLStatementCountValidator.reset();
            bookstoreService.authorOperationsWithTransactional();
            // allow the transaction to commit
            // a total of 2 statements instead of 5 as in the case of no explicit transaction
            assertInsertCount(1);
            assertUpdateCount(0);
            assertDeleteCount(1);
            assertSelectCount(0);
        };
    }
}

/*
 * Count and Assert SQL Statements

Description: This application is a sample of counting and asserting SQL statements triggered "behind the scene". Is very useful to count the SQL statements in order to ensure that your code is not generating more SQL statements that you may think (e.g., N+1 can be easily detected by asserting the number of expected statements).

Key points:

for Maven, in pom.xml, add dependencies for DataSource-Proxy library and Vlad Mihalcea's db-util library
create the ProxyDataSourceBuilder with countQuery()
reset the counter via SQLStatementCountValidator.reset()
assert INSERT, UPDATE, DELETE and SELECT via assertInsert/Update/Delete/Select/Count(long expectedNumberOfSql)
Output example (when the number of expected SQLs is not equal with the reality an exception is thrown):



 * 
 */


