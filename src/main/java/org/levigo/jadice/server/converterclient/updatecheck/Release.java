package org.levigo.jadice.server.converterclient.updatecheck;

import java.net.URL;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

class Release {
  
  public Long id;

  public String name;

  public String url;

  @JsonProperty("assets_url")
  public String assertsUrl;

  @JsonProperty("upload_url")
  public String uploadUrl;

  @JsonProperty("node_id")
  public String node_id;

  @JsonProperty("html_url")
  public URL htmlUrl;

  @JsonProperty("tag_name")
  public String tagName;

  @JsonProperty("target_commitish")
  public String targetCommitish;

  @JsonProperty("draft")
  public boolean isDraft;

  @JsonProperty("prerelease")
  public boolean isPreleases;

  @JsonProperty("created_at")
  public String created_at;

  @JsonProperty("published_at")
  public String published_at;

  @JsonProperty("assets")
  public List<Asset> assets;

  public User author;

  @JsonProperty("tarball_url")
  public String tarballUrl;

  @JsonProperty("zipball_url")
  public String zipballUrl;

  public String body;

}
