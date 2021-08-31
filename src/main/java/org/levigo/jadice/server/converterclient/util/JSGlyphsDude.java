package org.levigo.jadice.server.converterclient.util;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.jensd.fx.glyphs.GlyphIcon;
import de.jensd.fx.glyphs.GlyphIcons;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialicons.MaterialIconView;
import de.jensd.fx.glyphs.octicons.OctIconView;
import de.jensd.fx.glyphs.weathericons.WeatherIconView;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Labeled;
import javafx.scene.text.Font;
import javafx.scene.text.Text;


public class JSGlyphsDude {

    static {
        try {
            Font.loadFont(JSGlyphsDude.class.getResource(FontAwesomeIconView.TTF_PATH).openStream(), 10.0);
            Font.loadFont(JSGlyphsDude.class.getResource(WeatherIconView.TTF_PATH).openStream(), 10.0);
            Font.loadFont(JSGlyphsDude.class.getResource(MaterialIconView.TTF_PATH).openStream(), 10.0);
            Font.loadFont(JSGlyphsDude.class.getResource(MaterialIconView.TTF_PATH).openStream(), 10.0);
            Font.loadFont(JSGlyphsDude.class.getResource(OctIconView.TTF_PATH).openStream(), 10.0);
        } catch (IOException ex) {
            Logger.getLogger(MaterialIconView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Text createIcon(GlyphIcons icon) {
        return JSGlyphsDude.createIcon(icon, GlyphIcon.DEFAULT_FONT_SIZE);
    }

    public static Text createIcon(GlyphIcons icon, String iconSize) {
        Text text = new Text(icon.unicode());
        text.getStyleClass().add("glyph-icon");
        text.setStyle(String.format("-fx-font-family: %s; -fx-font-size: %s;", icon.fontFamily(), iconSize));
        return text;
    }

    public static void setIcon(Labeled labeled, GlyphIcons icon, String iconSize, ContentDisplay contentDisplay) {
        if (labeled == null) {
            throw new IllegalArgumentException("The component must not be 'null'!");
        }
        labeled.setGraphic(JSGlyphsDude.createIcon(icon, iconSize));
        labeled.setContentDisplay(contentDisplay);
    }
}
