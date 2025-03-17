package ru.bgpu.annotationlk;

import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppConfigWorker {

    private static Logger logger = Logger.getLogger(AppConfigWorker.class.getName());

    public static void configProcessing(String prefix, String filePropName) {

        Reflections reflections = new Reflections(prefix, org.reflections.scanners.Scanners.FieldsAnnotated);

        File prop = new File(filePropName);
        if (prop.isFile()) {
            try {
                Properties properties = new Properties();
                properties.load(new FileInputStream(prop));

                reflections.getFieldsAnnotatedWith(AppConfig.class).forEach(
                        field -> {
                            String value = properties.getProperty(
                                    field.getName(),
                                    field.getAnnotation(AppConfig.class).defValue()
                            );
                            Object targetValue = null;

                            // Обработка различных типов
                            if (field.getType().equals(String.class)) {
                                targetValue = value;
                            } else if (field.getType().equals(Integer.class) || field.getType().equals(int.class)) {
                                targetValue = Integer.valueOf(value);
                            } else if (field.getType().equals(Float.class) || field.getType().equals(float.class)) {
                                targetValue = Float.valueOf(value);
                            } else if (field.getType().equals(Double.class) || field.getType().equals(double.class)) {
                                targetValue = Double.valueOf(value);
                            } else if (field.getType().equals(Integer[].class)) {
                                targetValue = Arrays.stream(value.split(","))
                                        .map(Integer::valueOf)
                                        .toArray(Integer[]::new);
                            } else if (field.getType().equals(Float[].class)) {
                                targetValue = Arrays.stream(value.split(","))
                                        .map(Float::valueOf)
                                        .toArray(Float[]::new);
                            } else if (field.getType().equals(Double[].class)) {
                                targetValue = Arrays.stream(value.split(","))
                                        .map(Double::valueOf)
                                        .toArray(Double[]::new);
                            } else if (field.getType().equals(int[].class)) {
                                targetValue = Arrays.stream(value.split(","))
                                        .mapToInt(Integer::parseInt)
                                        .toArray();
                            } else if (field.getType().equals(float[].class)) {
                                targetValue = Arrays.stream(value.split(","))
                                        .map(Float::parseFloat)
                                        .mapToDouble(Float::doubleValue) // Преобразование в DoubleStream
                                        .map(d -> (float) d) // Преобразование обратно в float
                                        .toArray(); // Преобразование в массив float
                            } else if (field.getType().equals(double[].class)) {
                                targetValue = Arrays.stream(value.split(","))
                                        .map(Double::parseDouble)
                                        .mapToDouble(d -> d) // Преобразование в DoubleStream
                                        .toArray(); // Преобразование в массив double
                            }

                            try {
                                field.setAccessible(true);
                                field.set(field.getDeclaringClass(), targetValue);
                                field.setAccessible(false);
                            } catch (IllegalAccessException e) {
                                logger.log(
                                        Level.WARNING,
                                        "error set " + field.getDeclaringClass().getName()
                                                + "." + field.getName() + " " + value
                                );
                            }
                        }
                );
            } catch (IOException e) {
                logger.log(Level.WARNING, "error load properties", e);
            }
        } else {
            logger.log(Level.WARNING, "config file not found");
        }
    }
}