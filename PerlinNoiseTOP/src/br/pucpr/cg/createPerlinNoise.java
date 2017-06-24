package br.pucpr.cg;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Random;
import java.util.Scanner;

public class createPerlinNoise {

    static float perlin[][];

    public static double[] getFraction(double x, double y){
        double fX = x - (int) x, fY = y - (int) y;;

        double[] fractions = {fX,fY};

        return fractions;
    }

    public static double smoothNoise(double x, double y, int width, int height) {


        double[] f = getFraction(x, y);

        int x1 = (int) x + width % width;
        int x2 = (x1 + width - 1) % width;

        int y1 = (int) y + height % height;
        int y2 = (y1 + height - 1) % height;

        double value = 0.0;
        value += f[0] * f[1] * perlin[y1][x1];
        value += (1 - f[0]) * f[1] * perlin[y1][x2];
        value += f[0] * (1 - f[1]) * perlin[y2][x1];
        value += (1 - f[0]) * (1 - f[1]) * perlin[y2][x2];

        return value;
    }

    public static double functionT(double x, double y, double size, int width, int height) {
        double value = 0.0, initialSize = size;

        while (size >= 1) {
            value += smoothNoise(x / size, y / size, width, height) * size;
            size /= 2.0;
        }

        return (128.0 * value / initialSize);
    }

    static void GeneratePerlin(int width, int height) {

        Random generator = new Random();

        perlin = new float[width][height];

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < result.getHeight() - 1; y++) {
            for (int x = 0; x < result.getWidth() - 1; x++) {
                perlin[x][y] = generator.nextFloat();
            }
        }

        for (int y = 0; y < result.getHeight() - 1; y++) {
            for (int x = 0; x < result.getWidth() - 1; x++) {
                int color = (int) functionT(x, y, 32, width, height);
                result.setRGB(x, y, new Color(color , color, color).getRGB());
            }
        }

        try {
            ImageIO.write(result, "png", new File("img/opengl/heights/dwarf.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run () {

        Scanner reader = new Scanner(System.in);
        System.out.println("width: ");
        int width = reader.nextInt();
        System.out.println("height: ");
        int height = reader.nextInt();

        GeneratePerlin(width, height);
    }
}
