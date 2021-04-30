package com.github.felipegutierrez.kafka.connector.github;

import com.github.felipegutierrez.kafka.connector.github.client.GitHubAPIHttpClient;
import com.github.felipegutierrez.kafka.connector.github.model.Issue;
import com.github.felipegutierrez.kafka.connector.github.model.PullRequest;
import com.github.felipegutierrez.kafka.connector.github.model.User;
import com.github.felipegutierrez.kafka.connector.github.util.DateUtils;
import com.github.felipegutierrez.kafka.connector.github.util.VersionUtil;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.*;

import static com.github.felipegutierrez.kafka.connector.github.config.GitHubSchemas.*;

public class GitHubSourceTask extends SourceTask {
    /*
      Your connector should never use System.out for logging. All of your classes should use slf4j
      for logging
   */
    static final Logger log = LoggerFactory.getLogger(GitHubSourceTask.class);

    public GitHubSourceConnectorConfig config;

    // This allows to query github using a Http Rest API
    public GitHubAPIHttpClient gitHubHttpAPIClient;

    protected Instant nextQuerySince;
    protected Integer lastIssueNumber;
    protected Integer nextPageToVisit = 1;
    protected Instant lastUpdatedAt;

    @Override
    public String version() {
        return VersionUtil.getVersion();
    }

    @Override
    public void start(Map<String, String> map) {
        // DO: Do things here that are required to start your task. This could be open a connection to a database, etc.
        config = new GitHubSourceConnectorConfig(map);
        initializeLastVariables();
        gitHubHttpAPIClient = new GitHubAPIHttpClient(config);
    }

    /**
     * This method finds until up to which point the source connector must resume messages
     */
    private void initializeLastVariables() {
        Map<String, Object> lastSourceOffset = null;
        lastSourceOffset = context.offsetStorageReader().offset(sourcePartition());
        if (lastSourceOffset == null) {
            // we haven't fetched anything yet, so we initialize to 7 days ago
            nextQuerySince = config.getSince();
            lastIssueNumber = -1;
        } else {
            Object updatedAt = lastSourceOffset.get(UPDATED_AT_FIELD);
            Object issueNumber = lastSourceOffset.get(NUMBER_FIELD);
            Object nextPage = lastSourceOffset.get(NEXT_PAGE_FIELD);
            if (updatedAt != null && (updatedAt instanceof String)) {
                nextQuerySince = Instant.parse((String) updatedAt);
            }
            if (issueNumber != null && (issueNumber instanceof String)) {
                lastIssueNumber = Integer.valueOf((String) issueNumber);
            }
            if (nextPage != null && (nextPage instanceof String)) {
                nextPageToVisit = Integer.valueOf((String) nextPage);
            }
        }
    }

    private Map<String, String> sourcePartition() {
        Map<String, String> map = new HashMap<>();
        map.put(OWNER_FIELD, config.getOwnerConfig());
        map.put(REPOSITORY_FIELD, config.getRepoConfig());
        return map;
    }

    private Map<String, String> sourceOffset(Instant updatedAt) {
        Map<String, String> map = new HashMap<>();
        map.put(UPDATED_AT_FIELD, DateUtils.MaxInstant(updatedAt, nextQuerySince).toString());
        map.put(NEXT_PAGE_FIELD, nextPageToVisit.toString());
        return map;
    }

    public Struct buildRecordKey(Issue issue) {
        // Key Schema
        return new Struct(STATUS_SCHEMA_KEY)
                .put(OWNER_FIELD, config.getOwnerConfig())
                .put(REPOSITORY_FIELD, config.getRepoConfig())
                .put(NUMBER_FIELD, issue.getNumber());
    }

    public Struct buildRecordValue(Issue issue) {
        // Issue top level fields
        Struct valueStruct = new Struct(SCHEMA_PAYLOAD_STRUCT)
                .put(URL_FIELD, issue.getUrl())
                .put(TITLE_FIELD, issue.getTitle())
                .put(CREATED_AT_FIELD, Date.from(issue.getCreatedAt()))
                .put(UPDATED_AT_FIELD, Date.from(issue.getUpdatedAt()))
                .put(NUMBER_FIELD, issue.getNumber())
                .put(STATE_FIELD, issue.getState());

        // User is mandatory
        User user = issue.getUser();
        Struct userStruct = new Struct(USER_SCHEMA)
                .put(USER_URL_FIELD, user.getUrl())
                .put(USER_ID_FIELD, user.getId())
                .put(USER_LOGIN_FIELD, user.getLogin())
                .put(USER_HTML_URL_FIELD, user.getHtmlUrl());
        valueStruct.put(USER_FIELD, userStruct);

        // Pull request is optional
        PullRequest pullRequest = issue.getPullRequest();
        if (pullRequest != null) {
            Struct prStruct = new Struct(PR_SCHEMA)
                    .put(PR_URL_FIELD, pullRequest.getUrl())
                    .put(PR_HTML_URL_FIELD, pullRequest.getHtmlUrl());
            valueStruct.put(PR_FIELD, prStruct);
        }
        return valueStruct;
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        // DO: Create SourceRecord objects that will be sent the kafka cluster.
        // throw new UnsupportedOperationException("This has not been implemented.");
        gitHubHttpAPIClient.sleepIfNeed();

        // fetch data
        final ArrayList<SourceRecord> records = new ArrayList<>();
        JSONArray issues = gitHubHttpAPIClient.getNextIssues(nextPageToVisit, nextQuerySince);
        // we'll count how many results we get with i
        int i = 0;
        for (Object obj : issues) {
            Issue issue = Issue.fromJson((JSONObject) obj);
            log.info("..................................................................");
            log.info("..................................................................");
            log.info("..................................................................");
            log.info("POLLING RECORD ISSUE: " + issue);
            SourceRecord sourceRecord = generateSourceRecord(issue);
            log.info("..................................................................");
            log.info("..................................................................");
            log.info("..................................................................");
            log.info("GENERATED SOURCE RECORD: " + sourceRecord);
            records.add(sourceRecord);
            i += 1;
            lastUpdatedAt = issue.getUpdatedAt();
        }
        if (i > 0) log.info(String.format("Fetched %s record(s)", i));

        // this is the logic for the pagination to retrieve always 100 issues and present the next page if it exists.
        if (i == 100) {
            // we have reached a full batch, we need to get the next one
            nextPageToVisit += 1;
        } else {
            nextQuerySince = lastUpdatedAt.plusSeconds(1);
            nextPageToVisit = 1;
            gitHubHttpAPIClient.sleep();
        }
        return records;
    }

    /**
     * In order to generate a record to Kafka Connect we need a source partition and a source record.
     * The source partition and the source offsets are meant to track the Kafka Connect source.
     * <p>
     * The source partition allows Kafka Connect to know which source we have been reading.
     * The source offsets allow Kafka Connect to track until when we have been reading for the source partition that we choose.
     * They are NOT the partition and offsets for Kafka. This is a Kafka CONNECT source feature!
     * <p>
     * They basic allow where to resume from if we stop the Kafka Connect source. They are necessary to know how to
     * build the next dynamic url using the RestAPI.
     *
     * @param issue
     * @return
     * @see GitHubAPIHttpClient
     */
    private SourceRecord generateSourceRecord(Issue issue) {
        // SourceRecord record = new SourceRecord(
        // sourcePartition,
        // sourceOffset,
        // this.config.topic,
        // StatusConverter.STATUS_SCHEMA_KEY,
        // keyStruct,
        // StatusConverter.STATUS_SCHEMA,
        // valueStruct);
        return new SourceRecord(
                sourcePartition(),
                sourceOffset(issue.getUpdatedAt()),
                config.getTopic(),
                null, // partition will be inferred by the framework
                STATUS_SCHEMA_KEY,
                buildRecordKey(issue),
                SCHEMA_PAYLOAD_STRUCT,
                buildRecordValue(issue),
                issue.getUpdatedAt().toEpochMilli());
    }

    @Override
    public void stop() {
        //TODO: Do whatever is required to stop your task.
    }
}