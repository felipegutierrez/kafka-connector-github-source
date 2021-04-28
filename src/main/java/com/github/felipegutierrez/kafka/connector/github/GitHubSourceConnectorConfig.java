package com.github.felipegutierrez.kafka.connector.github;

import com.github.felipegutierrez.kafka.connector.github.validator.BatchSizeValidator;
import com.github.felipegutierrez.kafka.connector.github.validator.TimestampValidator;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Type;
import org.apache.kafka.common.config.ConfigDef.Importance;
import com.github.jcustenborder.kafka.connect.utils.config.ConfigKeyBuilder;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Map;

public class GitHubSourceConnectorConfig extends AbstractConfig {

  // public static final String MY_SETTING_CONFIG = "my.setting";
  // private static final String MY_SETTING_DOC = "This is a setting important to my connector.";
  public static final String TOPIC_CONFIG = "topic";
  public static final String GROUP_ID_CONFIG = "group.id";
  // public static final String OFFSET_STORAGE_TOPIC_CONFIG = "offset.storage.topic";
  public static final String OWNER_CONFIG = "github.owner";
  public static final String REPO_CONFIG = "github.repo";
  public static final String SINCE_CONFIG = "since.timestamp";
  public static final String BATCH_SIZE_CONFIG = "batch.size";
  public static final String AUTH_USERNAME_CONFIG = "auth.username";
  public static final String AUTH_PASSWORD_CONFIG = "auth.password";
  private static final String TOPIC_DOC = "Topic to write to";
  private static final String GROUP_ID_DOC = "A unique string that identifies the Connect cluster group this worker belongs to";
  private static final String OFFSET_STORAGE_TOPIC_DOC = "The topic to store offset data for connectors in. \n" +
          "This must be the same for all workers with the same group.id. \n" +
          "To support large Kafka Connect clusters, this topic should have a large number of partitions \n" +
          "(e.g. 25 or 50, just like Kafka’s built-in __consumer_offsets topic) and highly replicated (3x or more). \n" +
          "This topic should be compacted to avoid losing data due to retention policy.";
  private static final String OWNER_DOC = "Owner of the repository you'd like to follow";
  private static final String REPO_DOC = "Repository you'd like to follow";
  private static final String SINCE_DOC =
          "Only issues updated at or after this time are returned.\n"
                  + "This is a timestamp in ISO 8601 format: YYYY-MM-DDTHH:MM:SSZ.\n"
                  + "Defaults to a year from first launch.";
  private static final String BATCH_SIZE_DOC = "Number of data points to retrieve at a time. Defaults to 100 (max value)";
  private static final String AUTH_USERNAME_DOC = "Optional Username to authenticate calls";
  private static final String AUTH_PASSWORD_DOC = "Optional Password to authenticate calls";

  public final String mySetting;

  public GitHubSourceConnectorConfig(Map<?, ?> originals) {
    super(config(), originals);
    this.mySetting = this.getString(TOPIC_CONFIG);
  }

  public static ConfigDef config() {
    return new ConfigDef()
        // .define(ConfigKeyBuilder.of(MY_SETTING_CONFIG, Type.STRING).documentation(MY_SETTING_DOC).importance(Importance.HIGH).build())
            .define(ConfigKeyBuilder.of(TOPIC_CONFIG, Type.STRING).importance(Importance.HIGH).documentation(TOPIC_DOC).build())
            .define(ConfigKeyBuilder.of(GROUP_ID_CONFIG, Type.STRING).importance(Importance.HIGH).documentation(GROUP_ID_DOC).build())
            .define(ConfigKeyBuilder.of(OWNER_CONFIG, Type.STRING).importance(Importance.HIGH).documentation( OWNER_DOC).build())
            .define(ConfigKeyBuilder.of(REPO_CONFIG, Type.STRING).importance(Importance.HIGH).documentation( REPO_DOC).build())
            .define(ConfigKeyBuilder.of(BATCH_SIZE_CONFIG, Type.INT).importance(Importance.HIGH).defaultValue(100).validator(new BatchSizeValidator()).documentation(BATCH_SIZE_DOC).build())
            .define(ConfigKeyBuilder.of(SINCE_CONFIG, Type.STRING).importance(Importance.HIGH).defaultValue(ZonedDateTime.now().minusYears(1).toInstant().toString()).validator(new TimestampValidator()).documentation(SINCE_DOC).build())
            .define(ConfigKeyBuilder.of(AUTH_USERNAME_CONFIG, Type.STRING).importance(Importance.HIGH).defaultValue("").documentation(AUTH_USERNAME_DOC).build())
            .define(ConfigKeyBuilder.of(AUTH_PASSWORD_CONFIG, Type.PASSWORD).importance(Importance.HIGH).defaultValue("").documentation(AUTH_PASSWORD_DOC).build());
  }

  public String getOwnerConfig() {
    return this.getString(OWNER_CONFIG);
  }

  public String getRepoConfig() {
    return this.getString(REPO_CONFIG);
  }

  public Integer getBatchSize() {
    return this.getInt(BATCH_SIZE_CONFIG);
  }

  public Instant getSince() {
    return Instant.parse(this.getString(SINCE_CONFIG));
  }

  public String getTopic() {
    return this.getString(TOPIC_CONFIG);
  }

  public String getAuthUsername() {
    return this.getString(AUTH_USERNAME_CONFIG);
  }

  public String getAuthPassword() {
    return this.getPassword(AUTH_PASSWORD_CONFIG).value();
  }
}
