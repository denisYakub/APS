package GUI;

import lombok.Getter;
import lombok.Setter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import program.Controller;
import program.Order;
import program.events.Event;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import static program.events.Type.*;

@Getter
@Setter
public class Waveform extends JPanel implements ActionListener {
  ArrayList<Order> bufferList;
  private Controller controller;
  private JPanel chartPanel;
  private int generator_low;
  private int canceled_low;
  private HashMap<Integer, Integer> devices_low;
  private HashMap<Integer, Integer> buffer_low;
  private HashMap<Integer, Double> currentVal;
  private HashMap<Integer, XYSeries> series;
  private int canceledOrderCount = 0;
  private boolean ifFirstIteration = true;

  public Waveform(final Controller controller) {
    this.controller = controller;
    bufferList = new ArrayList<>(controller.getBuffer().getSize());
    for (int i = 0; i < controller.getBuffer().getCapacity(); i++) {
      bufferList.add(null);
    }
    series = new HashMap<>(1048576);
    devices_low = new HashMap<>(30);
    buffer_low = new HashMap<>(30);
    currentVal = new HashMap<>(30);
    canceled_low = 1;
    currentVal.put(1, (double) canceled_low);
    series.put(1, new XYSeries("О"));
    for (int i = 0; i < controller.getStatistics().getDevicesCount(); i++) {
      devices_low.put(i, i + 2);
      series.put(i + 2, new XYSeries("П-" + i));
      currentVal.put(i + 2, i + 2.0);
    }

    for (int i = 0; i < controller.getStatistics().getBufferSize(); i++) {
      buffer_low.put(i, devices_low.size() + 2 + i);
      series.put(devices_low.size() + 2 + i, new XYSeries("Б-" + i));
      currentVal.put(devices_low.size() + 2 + i, (double) devices_low.size() + 2 + i);
    }

    generator_low = devices_low.size() + buffer_low.size() + 2;
    series.put(series.size() + 1, new XYSeries("И"));
    currentVal.put(series.size(), (double) series.size());

    XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
    for (int i = 0; i < series.size(); i++) {
      xySeriesCollection.addSeries(series.get(i + 1));
    }

    StringBuilder yAvisLabel = new StringBuilder();
    for (int i = 1; i < series.size(); i++){
      yAvisLabel.append(series.get(i).getKey()).append("     ");
    }
    yAvisLabel.append(series.get(series.size()).getKey());
    /*JFreeChart chart = ChartFactory.createXYLineChart(
      "", "Время", "О     Р-0     Р-1     Р-2     Б-0     Б-1     Б-2     Б-3     Г",
            xySeriesCollection, PlotOrientation.VERTICAL,
            false, true, false);*/
    JFreeChart chart = ChartFactory.createXYLineChart(
      "", "Время", yAvisLabel.toString(),
            xySeriesCollection, PlotOrientation.VERTICAL,
            false, true, false);

    chart.setBackgroundPaint(Color.WHITE);

    final XYPlot plot = chart.getXYPlot();
    plot.setBackgroundPaint(Color.WHITE);

    XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
    for (int i = 0; i < series.size(); i++) {
      renderer.setSeriesShapesVisible(i, false);
      renderer.setSeriesStroke(i, new BasicStroke(1.5f));
    }
    renderer.setSeriesPaint(0, Color.RED);

    for (int i = 1; i < devices_low.size() + 1; i++) {
      renderer.setSeriesPaint(i, Color.blue);
    }
    for (int i = devices_low.size() + 1; i < series.size() - 1; i++) {
      renderer.setSeriesPaint(i, Color.black);
    }
    renderer.setSeriesPaint(series.size() - 1, Color.magenta);

    plot.setRenderer(renderer);
    for (int i = 1; i < series.size() + 1; i++){
      XYTextAnnotation textAnnotation = new
              XYTextAnnotation((String) series.get(i).getKey(), 0.06, (i - 0.13));
      plot.addAnnotation(textAnnotation);
    }

    final ChartPanel chartPanel = new ChartPanel(chart);
    JPanel content = new JPanel(new BorderLayout());
    content.add(chartPanel);
    chartPanel.setPreferredSize(new Dimension(2000, 400));
    this.chartPanel = content;
  }

  public JPanel getJPanel() {
    chartPanel.setPreferredSize(new Dimension(800, 400));
    return chartPanel;
  }

  public void actionPerformed(final ActionEvent e) {
    if (ifFirstIteration) {
      ifFirstIteration = false;
      series.get(1).add(0, canceled_low);
      series.get(series.size()).add(0, generator_low);
      for (int i = 0; i < devices_low.size(); i++) {
        series.get(i + 2).add(0, devices_low.get(i));
      }
      for (int i = devices_low.size() + 2, y = 0; i < series.size(); i++, y++) {
        series.get(i).add(0, buffer_low.get(y));
      }
    }
    final Event event = controller.getCurrentEvent();
    final double time = controller.getCurrentTime();
    final int generateIndex = series.size();
    final int cancelIndex = 1;
    if (event.getEventType() == GENERATED) {
      //generated clock
      series.get(generateIndex).add(time, currentVal.get(generateIndex));
      upValue(generateIndex);
      series.get(generateIndex).add(time, currentVal.get(generateIndex));
      downValue(generateIndex);
      series.get(generateIndex).add(time, currentVal.get(generateIndex));
      //up buffer or canceled clock
      for (int i = devices_low.size() + 2; i < series.size(); i++) {
        series.get(i).add(time, currentVal.get(i));
      }
      final int bufferIndex = findDifferenceInLists(controller.getBuffer().getOrders());
      if (canceledOrderCount != controller.getStatistics().getCanceledOrdersCount()) {
        findDifferenceInLists(controller.getBuffer().getOrders());
        series.get(cancelIndex).add(time, currentVal.get(cancelIndex));
        upValue(cancelIndex);
        series.get(cancelIndex).add(time, currentVal.get(cancelIndex));
        downValue(cancelIndex);
        series.get(cancelIndex).add(time, currentVal.get(cancelIndex));
        canceledOrderCount = controller.getStatistics().getCanceledOrdersCount();
        System.out.println("Tyt device 1.0");
        //downValue(devices_low.size() + 2 + bufferIndex);
        series.get(devices_low.size() + 2 + bufferIndex)
          .add(time, currentVal.get(devices_low.size() + 2 + bufferIndex));
      }
      if (bufferIndex != -1) {
        System.out.println("Tyt device 2");
        upValue(devices_low.size() + 2 + bufferIndex);
        series.get(devices_low.size() + 2 + bufferIndex)
          .add(time, currentVal.get(devices_low.size() + 2 + bufferIndex));
      }
      //devices
      //System.out.println("Tyt device 3");
      for (int i = 0; i < devices_low.size(); i++) {
        series.get(i + 2).add(time, currentVal.get(i + 2));
      }
    } else if (event.getEventType() == IN_PROGRESS
      && event.id != -1) {
      //down buffer
      for (int i = devices_low.size() + 2; i < series.size(); i++) {
        series.get(i).add(time, currentVal.get(i));
      }
      final int bufferIndex = findDifferenceInLists(controller.getBuffer().getOrders());
      //final int bufferIndex = findNullInLists(controller.getBuffer().getOrders());
      if (bufferIndex != -1) {
        System.out.println("Tyt bufferIndex = " + bufferIndex);
        downValue(devices_low.size() + 2 + bufferIndex);
      }
      series.get(devices_low.size() + 2 + bufferIndex)
        .add(time, currentVal.get(devices_low.size() + 2 + bufferIndex));
      //up device
      for (int i = 0; i < devices_low.size(); i++) {
        series.get(i + 2).add(time, currentVal.get(i + 2));
      }
      upValue(event.getId() + 2);
      series.get(event.getId() + 2).add(time, currentVal.get(event.getId() + 2));
    } else if (event.getEventType() == COMPLETED) {
      //down device
      for (int i = 0; i < devices_low.size(); i++) {
        series.get(i + 2).add(time, currentVal.get(i + 2));
      }
      downValue(event.getId() + 2);
      series.get(event.getId() + 2).add(time, currentVal.get(event.getId() + 2));
      for (int i = devices_low.size() + 2; i < series.size(); i++) {
        series.get(i).add(time, currentVal.get(i));
      }
    }
    series.get(series.size()).add(time, generator_low);
    series.get(1).add(time, canceled_low);
  }

  private void upValue(final int lineNum) {
    currentVal.replace(lineNum, currentVal.get(lineNum) + 0.5);
  }

  private void downValue(final int lineNum) {
    currentVal.replace(lineNum, currentVal.get(lineNum) - 0.5);
  }

  private int findDifferenceInLists(final ArrayList<Order> list) {
    for (int i = 0; i < list.size(); i++) {
      boolean listNew = list.get(i) != null;
      boolean listOld = bufferList.get(i) != null;
      if (listNew != listOld) {
        bufferList.set(i, list.get(i));
        return i;
      }
    }
    return -1;
  }

  private int findNullInLists(final ArrayList<Order> list) {
    for (int i = 0; i < list.size(); i++) {
      if (list.get(i) == null) {
        //bufferList.set(i, list.get(i));
        return i;
      }
    }
    return -1;
  }
}
