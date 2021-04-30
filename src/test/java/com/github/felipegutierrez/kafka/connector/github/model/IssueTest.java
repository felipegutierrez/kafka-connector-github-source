package com.github.felipegutierrez.kafka.connector.github.model;

import com.github.felipegutierrez.kafka.connector.github.GitHubSourceTask;
import com.github.felipegutierrez.kafka.connector.github.config.GitHubSchemas;
import org.apache.kafka.connect.data.Struct;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class IssueTest {
    String issueStr = "{\n" +
            "   \"url\":\"https://api.github.com/repos/kubernetes/kubernetes/issues/101603\",\n" +
            "   \"repository_url\":\"https://api.github.com/repos/kubernetes/kubernetes\",\n" +
            "   \"labels_url\":\"https://api.github.com/repos/kubernetes/kubernetes/issues/101603/labels{/name}\",\n" +
            "   \"comments_url\":\"https://api.github.com/repos/kubernetes/kubernetes/issues/101603/comments\",\n" +
            "   \"events_url\":\"https://api.github.com/repos/kubernetes/kubernetes/issues/101603/events\",\n" +
            "   \"html_url\":\"https://github.com/kubernetes/kubernetes/pull/101603\",\n" +
            "   \"id\":870604917,\n" +
            "   \"node_id\":\"MDExOlB1bGxSZXF1ZXN0NjI1Nzc0Njc4\",\n" +
            "   \"number\":101603,\n" +
            "   \"title\":\"Omit redundant nil check on slices\",\n" +
            "   \"user\":{\n" +
            "      \"login\":\"navist2020\",\n" +
            "      \"id\":74897901,\n" +
            "      \"node_id\":\"MDQ6VXNlcjc0ODk3OTAx\",\n" +
            "      \"avatar_url\":\"https://avatars.githubusercontent.com/u/74897901?v=4\",\n" +
            "      \"gravatar_id\":\"\",\n" +
            "      \"url\":\"https://api.github.com/users/navist2020\",\n" +
            "      \"html_url\":\"https://github.com/navist2020\",\n" +
            "      \"followers_url\":\"https://api.github.com/users/navist2020/followers\",\n" +
            "      \"following_url\":\"https://api.github.com/users/navist2020/following{/other_user}\",\n" +
            "      \"gists_url\":\"https://api.github.com/users/navist2020/gists{/gist_id}\",\n" +
            "      \"starred_url\":\"https://api.github.com/users/navist2020/starred{/owner}{/repo}\",\n" +
            "      \"subscriptions_url\":\"https://api.github.com/users/navist2020/subscriptions\",\n" +
            "      \"organizations_url\":\"https://api.github.com/users/navist2020/orgs\",\n" +
            "      \"repos_url\":\"https://api.github.com/users/navist2020/repos\",\n" +
            "      \"events_url\":\"https://api.github.com/users/navist2020/events{/privacy}\",\n" +
            "      \"received_events_url\":\"https://api.github.com/users/navist2020/received_events\",\n" +
            "      \"type\":\"User\",\n" +
            "      \"site_admin\":false\n" +
            "   },\n" +
            "   \"labels\":[\n" +
            "      {\n" +
            "         \"id\":154660912,\n" +
            "         \"node_id\":\"MDU6TGFiZWwxNTQ2NjA5MTI=\",\n" +
            "         \"url\":\"https://api.github.com/repos/kubernetes/kubernetes/labels/area/cloudprovider\",\n" +
            "         \"name\":\"area/cloudprovider\",\n" +
            "         \"color\":\"0052cc\",\n" +
            "         \"default\":false,\n" +
            "         \"description\":null\n" +
            "      },\n" +
            "      {\n" +
            "         \"id\":253450895,\n" +
            "         \"node_id\":\"MDU6TGFiZWwyNTM0NTA4OTU=\",\n" +
            "         \"url\":\"https://api.github.com/repos/kubernetes/kubernetes/labels/size/S\",\n" +
            "         \"name\":\"size/S\",\n" +
            "         \"color\":\"77bb00\",\n" +
            "         \"default\":false,\n" +
            "         \"description\":\"Denotes a PR that changes 10-29 lines, ignoring generated files.\"\n" +
            "      }\n" +
            "   ],\n" +
            "   \"state\":\"open\",\n" +
            "   \"locked\":false,\n" +
            "   \"assignee\":null,\n" +
            "   \"assignees\":[\n" +
            "      \n" +
            "   ],\n" +
            "   \"milestone\":null,\n" +
            "   \"comments\":3,\n" +
            "   \"created_at\":\"2021-04-29T04:41:30Z\",\n" +
            "   \"updated_at\":\"2021-04-29T04:42:34Z\",\n" +
            "   \"closed_at\":null,\n" +
            "   \"author_association\":\"CONTRIBUTOR\",\n" +
            "   \"active_lock_reason\":null,\n" +
            "   \"pull_request\":{\n" +
            "      \"url\":\"https://api.github.com/repos/kubernetes/kubernetes/pulls/101603\",\n" +
            "      \"html_url\":\"https://github.com/kubernetes/kubernetes/pull/101603\",\n" +
            "      \"diff_url\":\"https://github.com/kubernetes/kubernetes/pull/101603.diff\",\n" +
            "      \"patch_url\":\"https://github.com/kubernetes/kubernetes/pull/101603.patch\"\n" +
            "   },\n" +
            "   \"body\":\"The len function is defined for all slices, even nil ones, which have a length of zero. It is not necessary to check if a slice is not nil before checking that its length is not zero. #### Which issue(s) this PR fixes: <!-- *Automatically closes linked issue when PR is merged\",\n" +
            "   \"closed_by\":null,\n" +
            "   \"performed_via_github_app\":null\n" +
            "}";

    private final JSONObject issueJson = new JSONObject(issueStr);

    @Test
    public void canParseJson() {
        // issue
        Issue issue = Issue.fromJson(issueJson);
        assertEquals(issue.getUrl(), "https://api.github.com/repos/kubernetes/kubernetes/issues/101603");
        assertEquals(issue.getTitle(), "Omit redundant nil check on slices");
        assertEquals(issue.getCreatedAt().toString(), "2021-04-29T04:41:30Z");
        assertEquals(issue.getUpdatedAt().toString(), "2021-04-29T04:42:34Z");
        assertEquals(issue.getNumber(), (Integer) 101603);
        assertEquals(issue.getState(), "open");

        // user
        User user = issue.getUser();
        assertEquals(user.getId(), (Integer) 74897901);
        assertEquals(user.getUrl(), "https://api.github.com/users/navist2020");
        assertEquals(user.getHtmlUrl(), "https://github.com/navist2020");
        assertEquals(user.getLogin(), "navist2020");

        // pr
        PullRequest pullRequest = issue.getPullRequest();
        assertNotNull(pullRequest);
        assertEquals(pullRequest.getUrl(), "https://api.github.com/repos/kubernetes/kubernetes/pulls/101603");
        assertEquals(pullRequest.getHtmlUrl(), "https://github.com/kubernetes/kubernetes/pull/101603");

    }

    @Test
    public void convertsToStruct() {
        // issue
        Issue issue = Issue.fromJson(issueJson);
        Struct struct = new GitHubSourceTask().buildRecordValue(issue);
        assertEquals(struct.get(GitHubSchemas.CREATED_AT_FIELD).getClass(), Date.class);
    }

}
