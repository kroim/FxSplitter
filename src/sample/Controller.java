package sample;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

public class Controller {
    public ImageView imageView;
    public ImageView patternView;
    public ComboBox selectPattern;
    public ImageView result1;
    public ImageView result2;

    ImageProcessing imageProcessing = new ImageProcessing();

    @FXML
    public void initialize() {
        selectPattern.getItems().setAll("Pattern 1", "Pattern 2", "Pattern 3", "Pattern 4", "Pattern 5", "Pattern 6", "Pattern 7");
    }

    @FXML
    public void selectImage(){
        System.out.println("select image button");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.addChoosableFileFilter(new ImageFilter());
        fileChooser.setAcceptAllFileFilterUsed(false);
        int option = fileChooser.showOpenDialog(null);
        if (option == JFileChooser.APPROVE_OPTION){
            File file = fileChooser.getSelectedFile();
            String filePath = file.toURI().toString();
            filePath = filePath.replace("file:/", "").replace("%20", " ");
            String inputImagePath = imageProcessing.resize(filePath, "lib/input.png", 320, 320);
            File input_file = new File(inputImagePath);
            Image input_image = new Image(input_file.toURI().toString());
            imageView.setImage(input_image);
        }
    }
    @FXML
    public void selectPattern() {
        String patternPath = "lib/patterns/01.png";
        switch (selectPattern.getValue().toString()) {
            case "Pattern 1":
                patternPath = "lib/patterns/01.png";
                break;
            case "Pattern 2":
                patternPath = "lib/patterns/02.png";
                break;
            case "Pattern 3":
                patternPath = "lib/patterns/03.png";
                break;
            case "Pattern 4":
                patternPath = "lib/patterns/04.png";
                break;
            case "Pattern 5":
                patternPath = "lib/patterns/05.png";
                break;
            case "Pattern 6":
                patternPath = "lib/patterns/06.png";
                break;
            case "Pattern 7":
                patternPath = "lib/patterns/07.png";
                break;
        }
        File file = new File(patternPath);
        Image pattern = new Image(file.toURI().toString());
        patternView.setImage(pattern);
        Boolean bwPF = imageProcessing.convertToBlackWhite(patternPath, "lib/bwPattern.png");
        System.out.println(bwPF);
    }
    @FXML
    public void convertFunc(){
        System.out.println("convert");
        int[][] pixelValues = imageProcessing.getPixelColor();
        boolean convert = imageProcessing.splitImages("lib/input.png");
        if (convert) {
            File file1 = new File("lib/result1.png");
            File file2 = new File("lib/result2.png");
            Image resImg1 = new Image(file1.toURI().toString());
            Image resImg2 = new Image(file2.toURI().toString());
            result1.setImage(resImg1);
            result2.setImage(resImg2);
        }
    }
    @FXML
    public void saveResult1(){
        saveResult("lib/result1.png");
    }
    @FXML
    public void saveResult2(){
        saveResult("lib/result2.png");
    }
    public void saveResult(String savePath) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png"));
        File file = chooser.showSaveDialog(null);
        if (file != null) {
            File origin = new File(savePath);
            try {
                Files.copy(origin.toPath(), file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class ImageFilter extends FileFilter {
    public final static String JPEG = "jpeg";
    public final static String JPG = "jpg";
    public final static String GIF = "gif";
    public final static String TIFF = "tiff";
    public final static String TIF = "tif";
    public final static String PNG = "png";

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = getExtension(f);
        if (extension != null) {
            if (extension.equals(TIFF) ||
                    extension.equals(TIF) ||
                    extension.equals(GIF) ||
                    extension.equals(JPEG) ||
                    extension.equals(JPG) ||
                    extension.equals(PNG)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public String getDescription() {
        return "Image Only";
    }

    String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
}