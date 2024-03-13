package pl.zzpj.hellokitty;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Prompt extends AnAction {

    public static final String IMAGE_PROCESSED_EXC = "Image could not be processed!";
    public static final String API_KEY = "live_gcjuuNgfADkl2dG55cA9NMZe3uVxa8R7CA0MT0jSU1CB6HOf0mWjVJUlbYoKg6z5";
    public static final String BASE_URL = "https://api.thecatapi.com/v1/images/search";
    private String catPhotoURL;

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        final ImageType[] type = {ImageType.STATIC};
        String imageURL = this.getCatPhotoURL(type[0]);

        JFrame catPluginWindow = new JFrame("Show Me A Cat!");
        catPluginWindow.setSize(800, 600);

        JPanel mainPanel = new JPanel(new BorderLayout());

        try {
            ImageIcon imageIcon = new ImageIcon(this.getImageFromURL(imageURL));

            JLabel imageLabel = new JLabel(imageIcon);
            JPanel imagePanel = new JPanel(new FlowLayout());
            imagePanel.add(imageLabel, BorderLayout.CENTER);

            JButton closeButton = new JButton("Close");
            closeButton.setPreferredSize(new Dimension(150, 50));
            closeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    catPluginWindow.dispose();
                }
            });

            JButton nextButton = new JButton("Next one");
            nextButton.setPreferredSize(new Dimension(150, 50));
            nextButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        String imageURL = getCatPhotoURL(type[0]);
                        imageIcon.setImage(getImageFromURL(imageURL));
                        mainPanel.revalidate();
                        mainPanel.repaint();
                    } catch (MalformedURLException ex) {
                        throw new RuntimeException(IMAGE_PROCESSED_EXC);
                    }
                }
            });

            JButton gifButton = new JButton("Show me a GIF!");
            gifButton.setPreferredSize(new Dimension(150, 50));
            gifButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        type[0] = ImageType.GIF;
                        String imageURL = getCatPhotoURL(type[0]);
                        imageIcon.setImage(getImageFromURL(imageURL));
                        mainPanel.revalidate();
                        mainPanel.repaint();
                    } catch (MalformedURLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

            JButton staticImageButton = new JButton("Show me a static image!");
            staticImageButton.setPreferredSize(new Dimension(150, 50));
            staticImageButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        type[0] = ImageType.STATIC;
                        String imageURL = getCatPhotoURL(type[0]);
                        imageIcon.setImage(getImageFromURL(imageURL));
                        mainPanel.revalidate();
                        mainPanel.repaint();
                    } catch (MalformedURLException ex) {
                        throw new RuntimeException(IMAGE_PROCESSED_EXC);
                    }
                }
            });

            JPanel buttonPanel = new JPanel(new FlowLayout());
            buttonPanel.add(closeButton);
            buttonPanel.add(nextButton);
            buttonPanel.add(gifButton);
            buttonPanel.add(staticImageButton);

            mainPanel.add(imagePanel, BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            catPluginWindow.add(mainPanel, BorderLayout.CENTER);
            catPluginWindow.setLocationRelativeTo(null);
            catPluginWindow.setVisible(true);
        } catch (IOException e) {
            throw new RuntimeException("Invalid image URL!");
        }
    }

    private Image getImageFromURL(String imageURL) throws MalformedURLException {
        URL url = new URL(imageURL);
        ImageIcon imageIcon = new ImageIcon(url);

        Image image = imageIcon.getImage();
        image.getScaledInstance(800, 600, Image.SCALE_SMOOTH);

        return image;
    }

    private String getCatPhotoURL(ImageType imageType) {
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept("application/json");
        requestSpecification.header("x-api-key", API_KEY);

        Response response;
        if (imageType.equals(ImageType.STATIC)) {
            response = requestSpecification.get(BASE_URL + "?mime_types=jpg,png");
        } else {
            response = requestSpecification.get(BASE_URL + "?mime_types=gif");
        }

        Gson gson = new Gson();
        JsonArray jsonArray = gson.fromJson(response.getBody().asString(), JsonArray.class);
        JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();

        return jsonObject.get("url").getAsString();
    }

    private enum ImageType {
        STATIC,
        GIF
    }
}
