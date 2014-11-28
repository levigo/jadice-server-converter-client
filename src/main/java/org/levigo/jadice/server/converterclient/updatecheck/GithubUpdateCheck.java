package org.levigo.jadice.server.converterclient.updatecheck;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.activemq.util.ByteArrayOutputStream;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.levigo.jadice.server.util.Util;

class GithubUpdateCheck implements UpdateCheckResult {

  private static final Logger LOGGER = Logger.getLogger(GithubUpdateCheck.class);

  private static final URL JSON_URL;

  private static final Pattern VERSION_NUMBER_PATTER = Pattern.compile("(\\d+).(\\d+).(\\d+)");

  private List<Release> releases;

  static {
    try {
      JSON_URL = new URL("https://api.github.com/repos/levigo/jadice-server-converter-client/releases");
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
    ;
  }

  public void checkForUpdates() throws UpdateCheckException {
    if (releases != null) {
      return;
    }
    try {
      LOGGER.info("Retrieving latest release information from github");
      URLConnection conn = JSON_URL.openConnection();
      conn.setRequestProperty("Accept", "application/vnd.github.v3+json");
      conn.connect();

      try (final InputStream is = conn.getInputStream()) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Util.copyAndClose(is, baos);
        LOGGER.info("Retrieved JSON from github");
        LOGGER.debug(new String(baos.toByteArray(), "UTF-8"));
        ObjectMapper mapper = new ObjectMapper();
        releases = mapper.readValue(baos.toByteArray(), //
            new TypeReference<List<Release>>() {
            });

        // Only consider relevant releases + order by release date
        releases = releases.stream() //
        .filter(r -> !r.isDraft) //
        .sorted((a, b) -> a.published_at.compareTo(b.published_at)) //
        .collect(Collectors.toList());

        LOGGER.info(String.format("Found information about %d release(s)", releases.size()));
      }
    } catch (Exception e) {
      LOGGER.error("Could not perform github update check", e);
      throw new UpdateCheckException(e);
    }
  }

  public String getLatestVersionNumber() {
    return extractVersionNumber(getLatestRelease().name);
  }


  public String getLatestReleaseNotes() {
    return getLatestRelease().body;
  }

  public URL getLatestDownloadURL() {
    return getLatestRelease().assets.get(0).browserDownloadUrl;
  }

  @Override
  public URL getLatestReleaseURL() {
    return getLatestRelease().htmlUrl;
  }
  
  private Release getLatestRelease() {
    if (releases == null) {
      throw new IllegalStateException("checkForUpdates() must be called first");
    }
    return releases.get(releases.size() - 1);
  }

  public String getCurrentVersionNumber() {
    final Properties p = new Properties();
    try {
      final InputStream source = getClass().getResourceAsStream(
          "/META-INF/maven/org.levigo.jadice.server/jadice-server-converter-client/pom.properties");
      if (source != null) {
        p.load(source);
      }
    } catch (IOException e) {
      LOGGER.error("Could not determine current version", e);
    }
    return extractVersionNumber(p.getProperty("version", "0.0.0"));
  }

  private String extractVersionNumber(String s) {
    if (s == null) {
      return null;
    }
    final Matcher matcher = VERSION_NUMBER_PATTER.matcher(s);
    if (!matcher.find()) {
      return null;
    }
    return matcher.group();
  }

  private int[] splitVersionNumber(String s) {
    int[] result = new int[3];
    final Matcher matcher = VERSION_NUMBER_PATTER.matcher(s);
    if (!matcher.find()) {
      return null;
    }
    for (int i = 0; i < result.length; i++) {
      result[i] = Integer.parseInt(matcher.group(i + 1));
    }
    return result;
  }

  public boolean isNewerVersionAvailable() {
    final String currentVersion = getCurrentVersionNumber();
    final String latestVersion = getLatestVersionNumber();
    if (currentVersion == null) {
      throw new IllegalArgumentException("Could not determine current version");
    } else if (latestVersion == null) {
      throw new IllegalArgumentException("Could not determine latest version");
    }
    LOGGER.info(String.format("Comparing version current version %s vs. remote version %s", currentVersion, latestVersion));
    final int[] currentSplitted = splitVersionNumber(currentVersion);
    final int[] latestSplitted = splitVersionNumber(latestVersion);
    
    for (int i = 0; i < Math.min(currentSplitted.length, latestSplitted.length); i++) {
      if (currentSplitted[i] < latestSplitted[i]) {
        // current version is older
        return true;
      } else if (currentSplitted[i] > latestSplitted[i]) {
        // current version is newer (how that?)
        return false;
      } else {
        continue;
      }
    }
    // All digits are equal -> no newer version available
    return false;
  };
}
