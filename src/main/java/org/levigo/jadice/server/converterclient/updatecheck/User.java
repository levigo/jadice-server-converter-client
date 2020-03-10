package org.levigo.jadice.server.converterclient.updatecheck;

import com.fasterxml.jackson.annotation.JsonProperty;

class User {
  
  public long id;
  
  public String login;
  
  @JsonProperty("avatar_url")
  public String avatarUrl;
  
  @JsonProperty("gravatar_id")
  public String gravatarId;
  
  public String url;
  
  public String html_url;
  
  public String followers_url;
  
  public String following_url;
  
  public String gists_url;
  
  public String starred_url;
  
  public String subscriptions_url;
  
  public String organizations_url;
  
  public String repos_url;
  
  public String events_url;
  
  public String received_events_url;
  
  public String type;
  
  public String site_admin;
  
  public String node_id;
}
