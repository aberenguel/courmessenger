package org.coursera.cybersecurity.courmessenger.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DatabaseController {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseController.class);

    @Autowired
    private DataSource dataSource;

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    private String[] tables = { "user_account", "message" };

    @GetMapping("/dbdump")
    private String dbdump(Model model) {

        boolean isH2 = jdbcUrl.startsWith("jdbc:h2:");

        model.addAttribute("isH2", isH2);

        return "database";
    }

    @GetMapping(value = "/dbdump/json", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    private HashMap<String, List<HashMap<String, Object>>> showDatabase(Model model) throws SQLException {

        HashMap<String, List<HashMap<String, Object>>> tablesResult = new HashMap<>();

        Connection connection = dataSource.getConnection();

        try {

            for (String table : tables) {

                List<HashMap<String, Object>> tableResult = new ArrayList<>();

                PreparedStatement ps = connection.prepareStatement("SELECT * from " + table);
                ResultSet rs = ps.executeQuery();
                ResultSetMetaData md = rs.getMetaData();
                int columns = rs.getMetaData().getColumnCount();

                while (rs.next()) {
                    HashMap<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columns; i++) {
                        row.put(md.getColumnName(i), rs.getObject(i));
                    }
                    tableResult.add(row);
                }
                rs.close();

                tablesResult.put(table, tableResult);
            }

            model.addAttribute("result", tablesResult);

        } finally {
            connection.close();
        }

        return tablesResult;
    }

    @GetMapping("/dbdump/h2")
    private ResponseEntity<InputStreamResource> downloadDatabase(@Value("${app.database.file}") String databaseFileName)
            throws FileNotFoundException {

        File databaseFile = new File(databaseFileName + ".mv.db");
        LOG.info("Sending database file " + databaseFile.getAbsolutePath());

        // send the database file
        FileInputStream fileStream = new FileInputStream(databaseFile);
        InputStreamResource body = new InputStreamResource(fileStream);

        HttpHeaders respHeaders = new HttpHeaders();
        respHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        respHeaders.setContentDispositionFormData("attachment", databaseFile.getName());

        return new ResponseEntity<InputStreamResource>(body, respHeaders, HttpStatus.OK);
    }
}
