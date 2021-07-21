package org.levigo.jadice.server.converterclient.util;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;
import org.jadice.filetype.Analyzer;
import org.jadice.filetype.AnalyzerException;
import org.jadice.filetype.database.ExtensionAction;
import org.levigo.jadice.server.converterclient.Preferences;

import com.levigo.jadice.server.Job;
import com.levigo.jadice.server.shared.types.Stream;

public class FilenameGenerator {
  
  private static final Logger LOGGER = Logger.getLogger(FilenameGenerator.class);
  
  private static Analyzer analyzer; 

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

  public static String generateFilename(Job job, Stream stream, File originalFile, int nmbr) {
    String pttrn = Preferences.resultFilenamePatternProperty().getValue();
    // Caveat! Matcher.quoteReplacement(...) in order to escape $ and \ signs
    
    if (mustDetermineExtension()) {
      final String extension = determineExtension(stream);
      pttrn = pttrn.replaceAll(PatternKeys.EXTENSION, Matcher.quoteReplacement(extension));
    }
    return pttrn.replaceAll(PatternKeys.JOB_ID, Matcher.quoteReplacement(job.getUUID()))//
        .replaceAll(PatternKeys.ORIGINAL_FILENAME, Matcher.quoteReplacement(originalFile.getName()))//
        .replaceAll(PatternKeys.NUMMER, Integer.toString(nmbr))//
        .replaceAll(PatternKeys.TIMESTAMP, Long.toString(System.currentTimeMillis()))//
        .replaceAll(PatternKeys.PERCENT_SIGN, "%")
        // Paranoia checks to prevent from storing in other folders:
        .replaceAll("/", "")//
        .replace("\\", "");
  }
  
  protected static boolean mustDetermineExtension() {
    return Preferences.resultFilenamePatternProperty().getValue().contains(PatternKeys.EXTENSION);
  }

  private static String determineExtension(Stream stream) {
    try {
      final UncloseableSeekableInputStreamWrapper usis = new UncloseableSeekableInputStreamWrapper(stream.getInputStream());
      final Map<String, Object> alResults;
      try {
        usis.seek(0);
        usis.lockClose();
        alResults = getAnalyzer().analyze(usis);
      } finally {
        usis.seek(0);
        usis.unlockClose();
      }

      final Object o = alResults.get(ExtensionAction.KEY);
      return (o != null) ? o.toString() : Preferences.defaultExtensionProperty().getValue();
    } catch (IOException | AnalyzerException e) {
      LOGGER.error("Could not determine file extension", e);
      return Preferences.defaultExtensionProperty().getValue();
    }
  }

  private static Analyzer getAnalyzer() throws AnalyzerException {
    // Initiate lazily
    if (analyzer == null) {
      analyzer = Analyzer.getInstance("/magic.xml");
    }
    return analyzer;
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
