package com.github.felipegutierrez.kafka.connector.github.client;

import com.github.felipegutierrez.kafka.connector.github.GitHubSourceConnectorConfig;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static com.github.felipegutierrez.kafka.connector.github.GitHubSourceConnectorConfig.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GitHubAPIHttpClientTest {

    private Map<String, String> config;
    private GitHubSourceConnectorConfig gitHubSourceConnectorConfig;
    private GitHubAPIHttpClient gitHubAPIHttpClient;

    @BeforeAll
    public void setUpInitialConfig() {
        config = new HashMap<>();
        config.put(OWNER_CONFIG, "kubernetes");
        config.put(REPO_CONFIG, "kubernetes");
        config.put(SINCE_CONFIG, "2020-04-26T01:23:45Z");
        config.put(BATCH_SIZE_CONFIG, "2");
        config.put(TOPIC_CONFIG, "github-issues");

        gitHubSourceConnectorConfig = new GitHubSourceConnectorConfig(config);
        gitHubAPIHttpClient = new GitHubAPIHttpClient(gitHubSourceConnectorConfig);
    }

    @Test
    void constructUrl() {
        String expectedUrl = "https://api.github.com/repos/kubernetes/kubernetes/issues?page=10&per_page=2&since=2020-04-26T01:23:45Z&state=all&direction=asc&sort=updated";
        Integer page = 10;
        Instant instant = Instant.parse("2020-04-26T01:23:45Z");

        String actualUrl = gitHubAPIHttpClient.constructUrl(page, instant);
        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    void getNextIssuesAPI() throws UnirestException {
        Integer page = 10;
        Instant instant = Instant.parse("2020-04-26T01:23:45Z");
        HttpResponse<JsonNode> jsonNodeHttpResponse = gitHubAPIHttpClient.getNextIssuesAPI(page, instant);
        assertNotNull(jsonNodeHttpResponse);
        assertEquals(200, jsonNodeHttpResponse.getStatus());
        assertNotNull(jsonNodeHttpResponse.getBody());
        assertNotNull(jsonNodeHttpResponse.getHeaders());
    }

    @Test
    void getNextIssues() throws InterruptedException {
        Integer page = 10;
        Instant instant = Instant.parse("2020-04-26T01:23:45Z");
        JSONArray jsonNodeHttpResponse = gitHubAPIHttpClient.getNextIssues(page, instant);
        assertNotNull(jsonNodeHttpResponse);
        assertEquals(2, jsonNodeHttpResponse.length());
        jsonNodeHttpResponse.forEach(System.out::println);
    }
}