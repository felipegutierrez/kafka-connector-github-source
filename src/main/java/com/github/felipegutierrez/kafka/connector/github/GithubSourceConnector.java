package com.github.felipegutierrez.kafka.connector.github;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.felipegutierrez.kafka.connector.github.util.VersionUtil;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jcustenborder.kafka.connect.utils.config.Description;
import com.github.jcustenborder.kafka.connect.utils.config.DocumentationImportant;
import com.github.jcustenborder.kafka.connect.utils.config.DocumentationNote;
import com.github.jcustenborder.kafka.connect.utils.config.DocumentationTip;
import com.github.jcustenborder.kafka.connect.utils.config.Title;

@Description("This is a description of this connector and will show up in the documentation")
@DocumentationImportant("This is a important information that will show up in the documentation.")
@DocumentationTip("This is a tip that will show up in the documentation.")
@Title("Github Source Connector") //This is the display name that will show up in the documentation.
@DocumentationNote("This is a note that will show up in the documentation")
public class GithubSourceConnector extends SourceConnector {
  /*
    Your connector should never use System.out for logging. All of your classes should use slf4j
    for logging
 */
  private static Logger log = LoggerFactory.getLogger(GithubSourceConnector.class);
  private GitHubSourceConnectorConfig config;

  @Override
  public String version() {
    return VersionUtil.getVersion();
  }

  @Override
  public void start(Map<String, String> map) {
    // DO: Add things you need to do to setup your connector.
    config = new GitHubSourceConnectorConfig(map);
  }

  @Override
  public Class<? extends Task> taskClass() {
    // DO: Return your task implementation.
    return GithubSourceTask.class;
  }

  @Override
  public List<Map<String, String>> taskConfigs(int i) {
    // DO: Define the individual task configurations that will be executed.
    // throw new UnsupportedOperationException("This has not been implemented.");
    ArrayList<Map<String, String>> configs = new ArrayList<>(1);
    configs.add(config.originalsStrings());
    return configs;
  }

  @Override
  public void stop() {
    //TODO: Do things that are necessary to stop your connector.
  }

  @Override
  public ConfigDef config() {
    return GitHubSourceConnectorConfig.config();
  }
}
