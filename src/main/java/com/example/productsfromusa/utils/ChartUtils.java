package com.example.productsfromusa.utils;

import com.example.productsfromusa.models.Statistic;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class ChartUtils {
    public File generateChart(List<Statistic> statistics) throws IOException {
        try {
            CategoryDataset dataset = createDataset(statistics);

            JFreeChart chart = createChart(dataset);

            BufferedImage chartImage = chart.createBufferedImage(800, 600);

            File tempFile = File.createTempFile("statistics_chart", ".png");
            ImageIO.write(chartImage, "png", tempFile);

            return tempFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private JFreeChart createChart(CategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createLineChart(
                "",  // заголовок графика
                "Даты",                                     // метка категории (ось X)
                "Кол-во посещений",                               // метка значения (ось Y)
                dataset,                                    // данные для графика
                PlotOrientation.VERTICAL,
                true,                                       // включить легенду
                true,
                false                                       // отключить всплывающие подсказки
        );

        chart.setBackgroundPaint(Color.BLACK);
        chart.getTitle().setPaint(Color.WHITE);
        chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 18));

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.BLACK);
        plot.setDomainGridlinePaint(Color.GRAY);
        plot.setRangeGridlinePaint(Color.GRAY);
        plot.setOutlineVisible(false); // Убираем рамку

        plot.getDomainAxis().setLabelPaint(Color.WHITE);
        plot.getDomainAxis().setTickLabelPaint(Color.WHITE);
        plot.getRangeAxis().setLabelPaint(Color.WHITE);
        plot.getRangeAxis().setTickLabelPaint(Color.WHITE);

        plot.getDomainAxis().setLowerMargin(0);

        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setShapesVisible(true);
        renderer.setShapesFilled(true);

        Stroke[] strokes = {
                new BasicStroke(4.0f),
                new BasicStroke(4.0f),
                new BasicStroke(4.0f),
                new BasicStroke(4.0f),
                new BasicStroke(4.0f),
                new BasicStroke(4.0f),
                new BasicStroke(4.0f),
                new BasicStroke(4.0f),
                new BasicStroke(4.0f),
                new BasicStroke(4.0f),
                new BasicStroke(4.0f),
                new BasicStroke(4.0f),
                new BasicStroke(4.0f),
                new BasicStroke(4.0f),
                new BasicStroke(4.0f),
                new BasicStroke(4.0f),
                new BasicStroke(4.0f),
                new BasicStroke(4.0f),
        };
        for (int i = 0; i < strokes.length; i++) {
            renderer.setSeriesStroke(i, strokes[i]);
        }

        // Настройка цветов линий
        Color[] lineColors = {
                new Color(255, 0, 0),        // Красный
                new Color(0, 255, 0),        // Зеленый
                new Color(63, 63, 255),        // Синий
                new Color(255, 255, 0),      // Желтый
                new Color(255, 0, 255),      // Пурпурный
                new Color(0, 255, 255),      // Бирюзовый
                new Color(255, 128, 0),      // Оранжевый
                new Color(128, 0, 255),      // Фиолетовый
                new Color(255, 0, 128),      // Розовый
                new Color(0, 255, 128),      // Лайм
                new Color(128, 255, 0),      // Ярко-зеленый
                new Color(0, 128, 255),      // Ярко-синий
                new Color(255, 128, 128),    // Светло-красный
                new Color(128, 255, 255),    // Светло-бирюзовый
                new Color(255, 128, 255),    // Светло-пурпурный
                new Color(128, 255, 128),    // Светло-зеленый
                new Color(255, 255, 128),    // Ярко-желтый
                new Color(128, 128, 255)     // Светло-синий
        };

        for (int i = 0; i < lineColors.length; i++) {
            renderer.setSeriesPaint(i, lineColors[i]);
        }

        // Настройка легенды
        LegendTitle legend = chart.getLegend();
        legend.setBackgroundPaint(Color.BLACK);
        legend.setItemPaint(Color.WHITE);
        legend.setFrame(new org.jfree.chart.block.BlockBorder(Color.BLACK));

        return chart;
    }

    private DefaultCategoryDataset createDataset(List<Statistic> list) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM");
            for (Statistic statistic : list) {
                String category = statistic.getCategory().getName();
                String date = statistic.getCreationDate().format(formatter);

                // Проверяем, существует ли уже запись с данной категорией и датой
                Number existingValue = null;
                if (dataset.getRowKeys().contains(category) && dataset.getColumnKeys().contains(date)) {
                    existingValue = dataset.getValue(category, date);
                }

                if (existingValue != null) {
                    // Увеличиваем существующее значение на 1
                    dataset.addValue(existingValue.intValue() + 1, category, date);
                } else {
                    // Добавляем новую запись со значением 1
                    dataset.addValue(1, category, date);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataset;
    }
}
