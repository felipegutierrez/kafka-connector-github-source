package com.github.felipegutierrez.kafka.connector.github.model;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTest {

    String userStr = "{\n" +
            "   \"login\":\"felipegutierrez\",\n" +
            "   \"id\":5030696,\n" +
            "   \"node_id\":\"MDQ6VXNlcjUwMzA2OTY=\",\n" +
            "   \"avatar_url\":\"https://avatars.githubusercontent.com/u/5030696?v=4\",\n" +
            "   \"gravatar_id\":\"\",\n" +
            "   \"url\":\"https://api.github.com/users/felipegutierrez\",\n" +
            "   \"html_url\":\"https://github.com/felipegutierrez\",\n" +
            "   \"followers_url\":\"https://api.github.com/users/felipegutierrez/followers\",\n" +
            "   \"following_url\":\"https://api.github.com/users/felipegutierrez/following{/other_user}\",\n" +
            "   \"gists_url\":\"https://api.github.com/users/felipegutierrez/gists{/gist_id}\",\n" +
            "   \"starred_url\":\"https://api.github.com/users/felipegutierrez/starred{/owner}{/repo}\",\n" +
            "   \"subscriptions_url\":\"https://api.github.com/users/felipegutierrez/subscriptions\",\n" +
            "   \"organizations_url\":\"https://api.github.com/users/felipegutierrez/orgs\",\n" +
            "   \"repos_url\":\"https://api.github.com/users/felipegutierrez/repos\",\n" +
            "   \"events_url\":\"https://api.github.com/users/felipegutierrez/events{/privacy}\",\n" +
            "   \"received_events_url\":\"https://api.github.com/users/felipegutierrez/received_events\",\n" +
            "   \"type\":\"User\",\n" +
            "   \"site_admin\":false,\n" +
            "   \"name\":\"Felipe Oliveira Gutierrez\",\n" +
            "   \"company\":null,\n" +
            "   \"blog\":\"https://stackoverflow.com/users/2096986/felipe\",\n" +
            "   \"location\":\"Berlin\",\n" +
            "   \"email\":null,\n" +
            "   \"hireable\":null,\n" +
            "   \"bio\":null,\n" +
            "   \"twitter_username\":null,\n" +
            "   \"public_repos\":21,\n" +
            "   \"public_gists\":1,\n" +
            "   \"followers\":14,\n" +
            "   \"following\":25,\n" +
            "   \"created_at\":\"2013-07-17T12:00:26Z\",\n" +
            "   \"updated_at\":\"2021-04-28T14:37:41Z\"\n" +
            "}";

    private final JSONObject userJson = new JSONObject(userStr);

    @Test
    public void canParseJson() {
        User user = User.fromJson(userJson);
        assertEquals(user.getId(), (Integer) 5030696);
        assertEquals(user.getUrl(), "https://api.github.com/users/felipegutierrez");
        assertEquals(user.getLogin(), "felipegutierrez");
    }
}
