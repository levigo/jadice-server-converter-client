package org.levigo.jadice.server.converterclient.util;

import java.io.File;
import java.util.regex.Matcher;

import org.levigo.jadice.server.converterclient.Preferences;

import com.levigo.jadice.server.Job;

public class FilenameGenerator {

  public final static String DEFAULT_PATTERN = PatternKeys.ORIGINAL_FILENAME + "-" + PatternKeys.NUMMER + "."
      + PatternKeys.EXTENSION;

  public static interface PatternKeys {
    final String JOB_ID = "%j";
    final String ORIGINAL_FILENAME = "%f";
    final String EXTENSION = "%e";
    final String NUMMER = "%n";
    final String TIMESTAMP = "%t";
    final String PERCENT_SIGN = "%%";
  }

  public static String generateFilename(Job job, File originalFile, int nmbr, String extension) {
    final String pttrn = Preferences.resultFilenamePatternProperty().getValue();
    // Matcher.quoteReplacement(...) in order to escape $ and \ signs
    return pttrn.replaceAll(PatternKeys.JOB_ID, Matcher.quoteReplacement(job.getUUID()))//
    .replaceAll(PatternKeys.ORIGINAL_FILENAME, Matcher.quoteReplacement(originalFile.getName()))//
    .replaceAll(PatternKeys.EXTENSION, Matcher.quoteReplacement(extension))//
    .replaceAll(PatternKeys.NUMMER, Integer.toString(nmbr))//
    .replaceAll(PatternKeys.TIMESTAMP, Long.toString(System.currentTimeMillis()))//
    .replaceAll(PatternKeys.PERCENT_SIGN, "%")
    // Paranoia checks to prevent from storing in other folders:
    .replaceAll("/", "")//
    .replace("\\", "");
  }

  /**
   * Explains the Filename pattern.
   * 
   * @param sep Separator between items
   * @return explanation
   * @see PatternKeys
   */
  public static String buildExplanationText(String sep) {
    String s = "";
    s += FilenameGenerator.PatternKeys.ORIGINAL_FILENAME + ": original filename";
    s += sep;
    s += FilenameGenerator.PatternKeys.JOB_ID + ": job ID";
    s += sep;
    s += FilenameGenerator.PatternKeys.NUMMER + ": sequential nmbr";
    s += sep;
    s += FilenameGenerator.PatternKeys.EXTENSION + ": result extension";
    s += sep;
    s += FilenameGenerator.PatternKeys.TIMESTAMP + ": timestamp";
    s += sep;
    s += FilenameGenerator.PatternKeys.PERCENT_SIGN + ": %";
    return s;
  }
}
