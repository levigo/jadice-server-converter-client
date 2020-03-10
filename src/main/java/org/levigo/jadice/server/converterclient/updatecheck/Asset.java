package org.levigo.jadice.server.converterclient.updatecheck;

import java.net.URL;

import com.fasterxml.jackson.annotation.JsonProperty;

class Asset {

  public String id;

  public String name;

  public String url;

  public String label;

  public User uploader;

  public String node_id;

  @JsonProperty("content_type")
  public String contentType;

  public String state;

  public long size;

  @JsonProperty("download_count")
  public int downloadCount;

  @JsonProperty("created_at")
  public String createdAt;

  @JsonProperty("updated_at")
  public String updatedAt;

  @JsonProperty("browser_download_url")
  public URL browserDownloadUrl;
}
