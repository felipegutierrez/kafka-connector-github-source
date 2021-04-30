package com.github.felipegutierrez.kafka.connector.github.config;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Timestamp;

/**
 * Look at: https://developer.github.com/v3/issues/#list-issues-for-a-repository
 */
public class GitHubSchemas {

    public static final String NEXT_PAGE_FIELD = "next_page";

    // Issue fields
    public static final String OWNER_FIELD = "owner"; // maps on GithubSourceTask
    public static final String REPOSITORY_FIELD = "repository"; // maps on GithubSourceTask
    public static final String CREATED_AT_FIELD = "created_at";
    public static final String UPDATED_AT_FIELD = "updated_at";
    public static final String NUMBER_FIELD = "number";
    public static final String URL_FIELD = "url";
    public static final String HTML_URL_FIELD = "html_url";
    public static final String TITLE_FIELD = "title";
    public static final String STATE_FIELD = "state";

    // User fields
    public static final String USER_FIELD = "user";
    public static final String USER_URL_FIELD = "url";
    public static final String USER_HTML_URL_FIELD = "html_url";
    public static final String USER_ID_FIELD = "id";
    public static final String USER_LOGIN_FIELD = "login";

    // PR fields
    public static final String PR_FIELD = "pull_request";
    public static final String PR_URL_FIELD = "url";
    public static final String PR_HTML_URL_FIELD = "html_url";

    // Schema names
    public static final String STATUS_SCHEMA_KEY_NAME = "com.github.felipegutierrez.kafka.connector.github.StatusKey";
    public static final String SCHEMA_PAYLOAD_STRUCT_NAME = "com.github.felipegutierrez.kafka.connector.github.Status";
    public static final String SCHEMA_VALUE_USER = "UserValue";
    public static final String SCHEMA_VALUE_PR = "PrValue";

    // Value Schema
    public static final Schema USER_SCHEMA = SchemaBuilder.struct().name(SCHEMA_VALUE_USER)
            .version(1)
            .field(USER_URL_FIELD, Schema.STRING_SCHEMA)
            .field(USER_ID_FIELD, Schema.INT32_SCHEMA)
            .field(USER_LOGIN_FIELD, Schema.STRING_SCHEMA)
            .field(USER_HTML_URL_FIELD, Schema.STRING_SCHEMA)
            .build();

    // optional schema
    public static final Schema PR_SCHEMA = SchemaBuilder.struct().name(SCHEMA_VALUE_PR)
            .version(1)
            .field(PR_URL_FIELD, Schema.STRING_SCHEMA)
            .field(PR_HTML_URL_FIELD, Schema.STRING_SCHEMA)
            .optional()
            .build();

    // Key Schema
    public static final Schema STATUS_SCHEMA_KEY = SchemaBuilder.struct().name(STATUS_SCHEMA_KEY_NAME)
            .doc("key for github status")
            .field(OWNER_FIELD, Schema.OPTIONAL_STRING_SCHEMA)
            .field(REPOSITORY_FIELD, Schema.OPTIONAL_STRING_SCHEMA)
            .field(NUMBER_FIELD, Schema.OPTIONAL_INT32_SCHEMA)
            .build();

    // Payload Schema
    public static final Schema SCHEMA_PAYLOAD_STRUCT = SchemaBuilder.struct().name(SCHEMA_PAYLOAD_STRUCT_NAME)
            .doc("GitHub status message.")
            .field(URL_FIELD, Schema.STRING_SCHEMA)
            .field(TITLE_FIELD, Schema.STRING_SCHEMA)
            .field(CREATED_AT_FIELD, Timestamp.SCHEMA)
            .field(UPDATED_AT_FIELD, Timestamp.SCHEMA)
            .field(NUMBER_FIELD, Schema.INT32_SCHEMA)
            .field(STATE_FIELD, Schema.STRING_SCHEMA)
            .field(USER_FIELD, USER_SCHEMA) // mandatory
            .field(PR_FIELD, PR_SCHEMA)     // optional
            .build();
}

