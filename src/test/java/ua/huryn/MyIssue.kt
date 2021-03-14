package ua.huryn

import com.trivadis.plsql.formatter.settings.ConfiguredTestFormatter
import org.junit.jupiter.api.Test

class MyIssue : ConfiguredTestFormatter() {

    @Test
    fun in_clause() {
        var actual = """
            SELECT n.*
              FROM node n,
                   node_status ns
             WHERE n.id = r.id
               AND n.node_status_id = ns.id
               AND ns.status_group IN (
                      cgsNew,
                      cgsReadyToRun,
                      cgsRunning
                   );
        """.trimIndent()
        formatAndAssert(actual);
    }
    @Test
    fun update() {
        var actual = """
            UPDATE some_table t
               SET t.value = 'value'
             WHERE t.name = 'name';
        """.trimIndent()
        formatAndAssert(actual);
    }

    @Test
    fun update_with_slash() {
        var actual = """
            UPDATE some_table t
               SET t.value = 'value'
             WHERE t.name = 'name'
            / 
        """.trimIndent()
        formatAndAssert(actual);
    }

    @Test
    fun update_inside_block() {
        var actual = """
            DECLARE
             -- comment
             BEGIN
               UPDATE some_table t
                  SET t.value = 'value'
                WHERE t.name = 'name';
               COMMIT;
            END;
            /
        """.trimIndent()
        formatAndAssert(actual);
    }

    @Test
    fun drop_table_with_slash() {
        var actual = """
            DROP TABLE some_table
            /
        """.trimIndent()
        formatAndAssert(actual);
    }

	    @Test
    fun drop_table_with_column() {
        var actual = """
            DROP TABLE some_table;
        """.trimIndent()
        formatAndAssert(actual);
    }
}